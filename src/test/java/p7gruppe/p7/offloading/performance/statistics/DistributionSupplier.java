package p7gruppe.p7.offloading.performance.statistics;

import java.util.List;

public interface DistributionSupplier<T> {

    List<T> getDistribution(long randomSeed, int points);

}
