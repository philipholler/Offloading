package p7gruppe.p7.offloading.performance.mock;

import java.util.Optional;
import java.util.Random;

public class RandomIntervalJobSpawner implements JobSpawner {

    private final int averageJobComputeTime;
    private final int averageJobIntervalMillis;
    private final int maximumJobIntervalDeviationMillis;

    private long nextJobTime;

    private int REQUESTED_WORKERS = 3;

    private Random random;

    public RandomIntervalJobSpawner(int averageJobComputeTime, long randomSeed) {
        this.averageJobComputeTime = averageJobComputeTime;
        averageJobIntervalMillis = averageJobComputeTime;
        maximumJobIntervalDeviationMillis = averageJobIntervalMillis / 2;

        random = new Random(randomSeed);

        long now = System.currentTimeMillis();
        nextJobTime = now + random.nextInt(averageJobIntervalMillis + maximumJobIntervalDeviationMillis);
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
        int deviation = random.nextInt(averageJobComputeTime) - (averageJobComputeTime / 2);
        return new MockJob(averageJobComputeTime + deviation, REQUESTED_WORKERS);
    }
}
