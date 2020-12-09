package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.performance.mock.MockEmployer;
import p7gruppe.p7.offloading.performance.mock.MockUser;
import p7gruppe.p7.offloading.performance.mock.MockWorker;
import p7gruppe.p7.offloading.performance.mock.UserBase;
import p7gruppe.p7.offloading.statistics.DataPoint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatisticsSummary {

    private UserBase userBase;
    private RepositorySupplier repositorySupplier;

    public StatisticsSummary(UserBase userBase, RepositorySupplier repositorySupplier) {
        this.userBase = userBase;
        this.repositorySupplier = repositorySupplier;
    }

    public long getMaximumTimeFromUploadTillProcessedMillis() {
        long maximumProcessingTime = 0L;
        for (MockEmployer employer : userBase.getEmployers()) {
            List<JobStatistic> jobs = employer.getJobsStatistics();
            for (JobStatistic jobStat : jobs) {
                maximumProcessingTime = Math.max(maximumProcessingTime, jobStat.getProcessingTime());
            }
        }
        return maximumProcessingTime;
    }

    public long getAverageJobTimeForFinishedJobsMillis() {
        long totalJobTime = 0L;
        int totalJobs = 0;
        for (MockEmployer employer : userBase.getEmployers()) {
            List<JobStatistic> jobs = employer.getJobsStatistics();
            for (JobStatistic jobStat : jobs) {
                if (jobStat.isJobCompleted()) {
                    totalJobTime += jobStat.getProcessingTime();
                    totalJobs += 1;
                }
            }
        }
        if (totalJobs == 0) return 0;
        return (long) ((double) totalJobTime / (double) totalJobs);
    }

    public int getAmountOfResults() {
        int count = 0;
        for (MockEmployer employer : userBase.getEmployers()) {
            List<JobStatistic> jobs = employer.getJobsStatistics();
            for (JobStatistic jobStat : jobs) {
                if (jobStat.isJobCompleted()) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getAmountOfMaliciousResults() {
        int count = 0;
        for (MockEmployer employer : userBase.getEmployers()) {
            List<JobStatistic> jobs = employer.getJobsStatistics();
            for (JobStatistic jobStat : jobs) {
                if (jobStat.isJobCompleted() && !jobStat.isResultCorrect()) {
                    count++;
                }
            }
        }
        return count;
    }

    private List<JobStatistic> allJobs() {
        List<JobStatistic> jobs = new ArrayList<>();
        for (MockEmployer employer : userBase.getEmployers()) {
            jobs.addAll(employer.getJobsStatistics());
        }
        return jobs;
    }

    private List<JobStatistic> allCompletedJobs() {
        return allJobs().stream().filter(JobStatistic::isJobCompleted).collect(Collectors.toList());
    }

    public List<DataPoint<Double>> confidenceDataPoints() {
        List<DataPoint<Double>> dataPoints = new ArrayList<>();

        for (JobStatistic job : allCompletedJobs()) {
            Optional<JobEntity> jobEntityOptional = repositorySupplier.jobRepository.findById(job.jobID);
            if (!jobEntityOptional.isPresent())
                throw new RuntimeException("Job with id " + job.jobID + " present in statistics not server database");

            dataPoints.add(new DataPoint<Double>(job.getUploadTime(), jobEntityOptional.get().confidenceLevel));
        }

        return dataPoints;
    }

    public double averageConfidence() {
        double total = 0;
        int dataPoints = 0;

        for (JobStatistic job : allCompletedJobs()) {
            Optional<JobEntity> jobEntityOptional = repositorySupplier.jobRepository.findById(job.jobID);
            if (!jobEntityOptional.isPresent())
                throw new RuntimeException("Job with id " + job.jobID + " present in statistics not server database");

            dataPoints++;
            total += jobEntityOptional.get().confidenceLevel;
        }

        return total / (double) dataPoints;
    }


    public List<DataPoint<Integer>> getThroughputOverTime(int timeStepMillis) {
        List<DataPoint<Integer>> throughputOverTime = new ArrayList<>();

        List<JobStatistic> correctFullConfidenceJobs = allCompletedJobs().stream().filter((job) -> {
            Optional<JobEntity> jobEntityOptional = repositorySupplier.jobRepository.findById(job.jobID);
            double confidence = jobEntityOptional.get().confidenceLevel;
            return confidence >= 0.99d && job.isResultCorrect();
        }).sorted(Comparator.comparingLong(JobStatistic::getUploadTime)).collect(Collectors.toList());

        if (correctFullConfidenceJobs.size() <= 10) {
            throw new RuntimeException("Dataset too small to perform throughput analysis");
        }

        long startTime = correctFullConfidenceJobs.get(0).getUploadTime();
        long endTime = correctFullConfidenceJobs.get(correctFullConfidenceJobs.size() - 1).getUploadTime();

        int length = (int) ((endTime - startTime) / timeStepMillis);
        length += ((int) ((endTime - startTime) % timeStepMillis) > 0) ? 1 : 0;
        int[] throughputValues = new int[length];

        for (JobStatistic jobStat : correctFullConfidenceJobs) {
            int index = (int) ((jobStat.getUploadTime() - startTime) / timeStepMillis);
            throughputValues[index] += 1;
        }

        for (int i = 0; i < throughputValues.length; i++) {
            throughputOverTime.add(new DataPoint<Integer>(i * timeStepMillis, throughputValues[i]));
        }

        return throughputOverTime;
    }

    // Throughput is defined as correct 100% confidence results
    public long getTotalThroughput() {
        return allCompletedJobs().stream().filter((job) -> {
            Optional<JobEntity> jobEntityOptional = repositorySupplier.jobRepository.findById(job.jobID);
            double confidence = jobEntityOptional.get().confidenceLevel;
            return confidence >= 0.99d && job.isResultCorrect();
        }).count();
    }

    // Sum activation of all devices for a given user over time
    public List<DataPoint<Long>> getActivationOverTime(String deviceImei) {
        for (MockWorker worker : userBase.getWorkers()) {
            if (worker.deviceId.getImei().equals(deviceImei)) {
                return worker.statistic.getContributionOverTime();
            }
        }
        throw new RuntimeException("No user with name " + deviceImei);
    }
}
