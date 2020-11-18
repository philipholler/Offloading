package p7gruppe.p7.offloading.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.model.UserCredentials;

@Component
class JobConverter implements Converter<String, Job> {

    @Override
    public Job convert(String source) {
        Job job = new Job();
        // TODO: 16/11/2020 Parse this object correctly! - Philip
        job.setAssignedUser(source);
        job.setId((int)System.currentTimeMillis());
        job.setJobpath(source);
        job.setName(source);
        return new Job();
    }
}