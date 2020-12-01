package p7gruppe.p7.offloading.performance.mock;

import java.util.Optional;

public interface JobSpawner {



    Optional<MockJob> pollJob();

}
