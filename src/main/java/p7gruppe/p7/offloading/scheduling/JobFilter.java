package p7gruppe.p7.offloading.scheduling;

import p7gruppe.p7.offloading.data.enitity.JobEntity;

public interface JobFilter {

    boolean accept(JobEntity jobEntity);

}
