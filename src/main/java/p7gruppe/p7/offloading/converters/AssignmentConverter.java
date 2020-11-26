package p7gruppe.p7.offloading.converters;

import jdk.jfr.ContentType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNullApi;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import p7gruppe.p7.offloading.model.Assignment;
import p7gruppe.p7.offloading.model.DeviceId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Component
public class AssignmentConverter implements Converter<Assignment, String> {
    @Override
    public String convert(Assignment source) {
        return "jkldsfjklasdfjlk";
    }
}

