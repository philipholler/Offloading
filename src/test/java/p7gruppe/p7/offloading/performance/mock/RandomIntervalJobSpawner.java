package p7gruppe.p7.offloading.performance.mock;

import java.util.Optional;
import java.util.Random;

public class RandomIntervalJobSpawner implements JobSpawner {

    private int averageJobComputeTimeMillis;
    private int maximumJobComputeTimeDeviationMillis;

    private int averageJobIntervalMillis;
    private int maximumJobIntervalDeviationMillis;

    private long nextJobTime;

    private int REQUESTED_WORKERS = 3;

    private Random random;

    public RandomIntervalJobSpawner(int averageJobComputeTime, int averageJobIntervalMillis, long randomSeed) {
        this.averageJobComputeTimeMillis = averageJobComputeTime;
        this.averageJobIntervalMillis = averageJobIntervalMillis;
        maximumJobComputeTimeDeviationMillis = averageJobIntervalMillis / 4;
        maximumJobIntervalDeviationMillis = averageJobIntervalMillis / 4;

        random = new Random(randomSeed);

        long now = System.currentTimeMillis();
        nextJobTime = now + random.nextInt(averageJobIntervalMillis + maximumJobIntervalDeviationMillis);
    }

    public void setMaximumComputeTimeDeviationMillis(int maximumJobComputeTimeDeviationMillis) {
        this.maximumJobComputeTimeDeviationMillis = maximumJobComputeTimeDeviationMillis;
    }

    public void setMaximumSpawnIntervalDeviationMillis(int maximumJobIntervalDeviationMillis) {
        this.maximumJobIntervalDeviationMillis = maximumJobIntervalDeviationMillis;
    }

    @Override
    public Optional<MockJob> pollJob() {
        if (System.currentTimeMillis() < nextJobTime)
            return Optional.empty();

        int deviation = random.nextInt(maximumJobIntervalDeviationMillis * 2) - maximumJobIntervalDeviationMillis;
        nextJobTime = System.currentTimeMillis() + averageJobIntervalMillis +  deviation;
        return Optional.of(createNewJob());
    }

    private MockJob createNewJob() {
        int maxDev = maximumJobComputeTimeDeviationMillis;
        int deviation = (maxDev == 0) ? 0 : (random.nextInt(maxDev * 2) - (maxDev / 2));
        return new MockJob(averageJobComputeTimeMillis + deviation, REQUESTED_WORKERS);
    }
}
