package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.performance.mock.*;
import p7gruppe.p7.offloading.statistics.DataPoint;
import p7gruppe.p7.offloading.statistics.ServerStatistic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatisticsSummary {

    private UserBase userBase;
    private RepositorySupplier repositorySupplier;
    private final long startTimeMillis;

    public StatisticsSummary(UserBase userBase, RepositorySupplier repositorySupplier, long startTimeMillis) {
        this.userBase = userBase;
        this.repositorySupplier = repositorySupplier;
        this.startTimeMillis = startTimeMillis;
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

    public int getAmountOfPostedJobs() {
        return allJobs().size();
    }

    public int getAmountOfCompletedJobs() {
        return allCompletedJobs().size();
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

            dataPoints.add(new DataPoint<Double>((job.getFinishTimeStampMillis() - startTimeMillis) / 1000, jobEntityOptional.get().confidenceLevel));
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

    // Throughput is defined as correct 100% confidence results
    public long getTotalThroughput() {
        return allCompletedJobs().stream().filter((job) -> {
            Optional<JobEntity> jobEntityOptional = repositorySupplier.jobRepository.findById(job.jobID);
            double confidence = jobEntityOptional.get().confidenceLevel;
            return confidence >= 0.99d && job.isResultCorrect();
        }).count();
    }

    // Sum activation of all devices for a given user over time
    public List<DataPoint<Long>> getActivationOverTime(String uuid) {
        for (MockWorker worker : userBase.getWorkers()) {
            if (worker.deviceId.getUuid().equals(uuid)) {
                return worker.statistic.getContributionOverTime()
                        .stream().map((dp) -> new DataPoint<>(dp.timestamp / 1000, dp.value / 1000))
                        .collect(Collectors.toList());
            }
        }
        throw new RuntimeException("No user with name " + uuid);
    }

    public int getWrongAnswersOutOfFirstNJobs(int N){
        List<JobStatistic> jobs = getFirstNJobsFinishedJobs(N);

        int wrongResults = 0;
        for(JobStatistic job : jobs){
            wrongResults = job.isResultCorrect() ? wrongResults : wrongResults + 1;
        }

        return wrongResults;
    }

    public int getCorrectAnswersOutOfFirstNJobs(int N){
        List<JobStatistic> jobs = getFirstNJobsFinishedJobs(N);

        int correctResults = 0;
        for(JobStatistic job : jobs){
            correctResults = job.isResultCorrect() ? correctResults + 1 : correctResults;
        }

        return correctResults;
    }

    private List<JobStatistic> getFirstNJobsFinishedJobs(int N){
        List<JobStatistic> jobs = userBase.getJobStatistics();

        List<JobStatistic> finishedJobs = new ArrayList<>();

        finishedJobs.addAll(jobs.stream().filter((j) -> j.isJobCompleted()).collect(Collectors.toList()));

        return finishedJobs.subList(0, Math.min(finishedJobs.size() - 1, 200));
    }

    public List<DataPoint<Long>> getBankedTimeAndJobTime() {
        List<DataPoint<Long>> dataPoints = new ArrayList<>();

        for (JobStatistic jobStatistic : allCompletedJobs()) {
            long uploadTime = jobStatistic.getUploadTimeMillis();
            long bankedTime = ServerStatistic.getCPUTime(jobStatistic.user.userCredentials.getUsername(), uploadTime);
            long jobTime = jobStatistic.getProcessingTime();
            dataPoints.add(new DataPoint<>(bankedTime / 1000L, jobTime / 1000L));
        }

        return dataPoints;
    }

    private Optional<MockEmployer> getEmployer(MockUser user) {
        for (MockEmployer employer : userBase.getEmployers()) {
            if (employer.mockUser.userCredentials.getUsername().equals(user.userCredentials.getUsername())) {
                return Optional.of(employer);
            }
        }
        return Optional.empty();
    }

    public List<DataPoint<Integer>> getThroughputOverTime(int timeStepMillis) {
        List<DataPoint<Integer>> throughputOverTime = new ArrayList<>();

        List<JobStatistic> correctFullConfidenceJobs = allCompletedJobs().stream().filter((job) -> {
            Optional<JobEntity> jobEntityOptional = repositorySupplier.jobRepository.findById(job.jobID);
            double confidence = jobEntityOptional.get().confidenceLevel;
            return confidence >= 0.99d && job.isResultCorrect();
        }).sorted(Comparator.comparingLong(JobStatistic::getFinishTimeStampMillis)).collect(Collectors.toList());

        if (correctFullConfidenceJobs.size() <= 10) {
            throw new RuntimeException("Dataset too small to perform throughput analysis");
        }

        long startTime = correctFullConfidenceJobs.get(0).getFinishTimeStampMillis();
        long endTime = correctFullConfidenceJobs.get(correctFullConfidenceJobs.size() - 1).getFinishTimeStampMillis();

        int length = (int) ((endTime - startTime) / timeStepMillis);
        length += ((int) ((endTime - startTime) % timeStepMillis) > 0) ? 1 : 0;
        int[] throughputValues = new int[length];

        for (JobStatistic jobStat : correctFullConfidenceJobs) {
            int index = (int) ((jobStat.getFinishTimeStampMillis() - startTime) / timeStepMillis);
            throughputValues[index] += 1;
        }

        for (int i = 0; i < throughputValues.length; i++) {
            throughputOverTime.add(new DataPoint<Integer>(i * timeStepMillis, throughputValues[i]));
        }

        return throughputOverTime;
    }

    public List<DataPoint<Double>> getAverageConfidenceIntervals(int timeStepMillis) {
        List<DataPoint<Double>> throughputOverTime = new ArrayList<>();

        List<JobStatistic> completedJobs = allCompletedJobs();
        completedJobs.sort(Comparator.comparingLong(JobStatistic::getFinishTimeStampMillis));

        if (completedJobs.size() <= 10) {
            throw new RuntimeException("Dataset too small to perform throughput analysis");
        }

        long startTime = completedJobs.get(0).getFinishTimeStampMillis();
        long endTime = completedJobs.get(completedJobs.size() - 1).getFinishTimeStampMillis();

        int length = (int) ((endTime - startTime) / timeStepMillis);
        length += ((int) ((endTime - startTime) % timeStepMillis) > 0) ? 1 : 0;

        Averager[] averagers = new Averager[length];
        for (int i = 0; i < length; i++) averagers[i] = new Averager();

        for (JobStatistic jobStat : completedJobs) {
            int index = (int) ((jobStat.getFinishTimeStampMillis() - startTime) / timeStepMillis);
            JobEntity jobEntity = repositorySupplier.jobRepository.findById(jobStat.jobID).get();
            averagers[index].add(jobEntity.confidenceLevel);
        }

        for (int i = 0; i < averagers.length; i++) {
            throughputOverTime.add(new DataPoint<>(i * timeStepMillis, averagers[i].getAverage()));
        }

        return throughputOverTime;
    }

    public List<DataPoint<Double>> getAverageCorrectnessRatioJobInterval(int jobsPerInterval){
        List<JobStatistic> completedJobs = allCompletedJobs();

        completedJobs.sort(Comparator.comparingLong(JobStatistic::getFinishTimeStampMillis));

        if (completedJobs.size() <= 10) {
            throw new RuntimeException("Dataset too small to perform throughput analysis");
        }

        int intervals = completedJobs.size() / jobsPerInterval;
        intervals += (completedJobs.size() % jobsPerInterval > 0) ? 1 : 0;

        Averager[] averagers = new Averager[intervals];
        for (int i = 0; i < intervals; i++) averagers[i] = new Averager();

        for(int i = 0; i < completedJobs.size(); i++){
            if(completedJobs.get(i).isResultCorrect() && completedJobs.get(i).isJobCompleted()){
                averagers[i / jobsPerInterval].add(1);
            }
            else {
                averagers[i / jobsPerInterval].add(0);
            }
        }

        List<DataPoint<Double>> correctnessRatioOverTime = new ArrayList<>();
        for (int i = 0; i < averagers.length; i++) {
            correctnessRatioOverTime.add(new DataPoint<>(i * jobsPerInterval, averagers[i].getAverage()));
        }

        return correctnessRatioOverTime;
    }

    public List<DataPoint<Double>> getAverageConfidenceJobInterval(int jobsPerInterval) {
        List<JobStatistic> completedJobs = allCompletedJobs();
        completedJobs.sort(Comparator.comparingLong(JobStatistic::getFinishTimeStampMillis));

        if (completedJobs.size() <= 10) {
            throw new RuntimeException("Dataset too small to perform throughput analysis");
        }

        int intervals = completedJobs.size() / jobsPerInterval;
        intervals += (completedJobs.size() % jobsPerInterval > 0) ? 1 : 0;

        Averager[] averagers = new Averager[intervals];
        for (int i = 0; i < intervals; i++) averagers[i] = new Averager();

        int amountOfJobs = completedJobs.size();
        for (int i = 0; i < amountOfJobs; i++) {
            JobEntity jobEntity = repositorySupplier.jobRepository.findById(completedJobs.get(i).jobID).get();
            averagers[i / jobsPerInterval].add(jobEntity.confidenceLevel);
        }

        List<DataPoint<Double>> confidenceOverTime = new ArrayList<>();
        for (int i = 0; i < averagers.length; i++) {
            confidenceOverTime.add(new DataPoint<>(i * jobsPerInterval, averagers[i].getAverage()));
        }

        return confidenceOverTime;
    }

    private class Averager{

        private double total = 0;
        private int count = 0;

        public void add(double value){
            count += 1;
            total += value;
        }

        public double getAverage(){
            if (count == 0) return 0.0;
            return total / (double) count;
        }

    }


    public List<DataPoint<Double>> percentageUncompletedJobsByBankedTime(int bankInterval){
        List<Integer> incompleteJobsByBankedTime = new ArrayList<>();
        List<Integer> totalJobCounts = new ArrayList<>();
        for (JobStatistic jobStatistic : allJobs()) {
            long uploadTime = jobStatistic.getUploadTimeMillis();
            long bankedTime = ServerStatistic.getCPUTime(jobStatistic.user.userCredentials.getUsername(), uploadTime);
            if (bankedTime < 0) continue;

            int index = (int) bankedTime / bankInterval;
            while (index >= incompleteJobsByBankedTime.size()) {
                incompleteJobsByBankedTime.add(0);
                totalJobCounts.add(0);
            }

            totalJobCounts.set(index, totalJobCounts.get(index) + 1);
            if (!jobStatistic.isJobCompleted()) {
                incompleteJobsByBankedTime.set(index, incompleteJobsByBankedTime.get(index) + 1);
            }
        }

        List<DataPoint<Double>> dataPoints = new ArrayList<>();
        for (int i = 0; i < incompleteJobsByBankedTime.size(); i++) {
            double percentageIncomplete;
            if (totalJobCounts.get(i) == 0) percentageIncomplete = 0;
            else percentageIncomplete = ((double) incompleteJobsByBankedTime.get(i)) / (double) totalJobCounts.get(i);
            percentageIncomplete *= 100;
            percentageIncomplete = Math.round(percentageIncomplete);
            dataPoints.add(new DataPoint<>(i * bankInterval, percentageIncomplete));
        }

        return dataPoints;
    }


}
