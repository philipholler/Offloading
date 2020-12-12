package p7gruppe.p7.offloading.converters;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import p7gruppe.p7.offloading.model.DeviceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class DeviceIDConverter implements Converter<String, DeviceId> {

    @Override
    public DeviceId convert(String source) {
        DeviceId deviceId = new DeviceId();
        if (source.contains("uuid=")){
            // Arrives as: DeviceId(uuid=*val*)
            String value = source.substring(source.indexOf("=") + 1, source.length() - 1);
            if(!value.isEmpty()){
                deviceId.setUuid(value);
            }
            return deviceId;

        }else {
            // If arrived as: uuid,*val*
            Pattern pattern = Pattern.compile(".*uuid,([^,]*)");
            Matcher matcher = pattern.matcher(source);
            boolean found = matcher.find();
            if (found) {
                deviceId.setUuid(matcher.group(1));
                return deviceId;
            }
            else {
                throw new RuntimeException("Pattern for deviceid did not match");
            }
        }
    }
}