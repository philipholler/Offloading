package p7gruppe.p7.offloading.performance.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArbitraryCPUDistributionSupplier implements DistributionSupplier<Double>{

    @Override
    public List<Double> getDistribution(long randomSeed, int points) {
        Random random = new Random();

        List<Double> distribution = new ArrayList<>();
        for (int i = 0; i < points; i++) {
            distribution.add(1.0 + random.nextGaussian() * 0.5);
        }

        return distribution;
    }



}
