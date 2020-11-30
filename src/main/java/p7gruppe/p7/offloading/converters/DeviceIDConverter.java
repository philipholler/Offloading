package p7gruppe.p7.offloading.converters;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import p7gruppe.p7.offloading.model.DeviceId;
import p7gruppe.p7.offloading.model.UserCredentials;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class DeviceIDConverter implements Converter<String, DeviceId> {

    @Override
    public DeviceId convert(String source) {
        DeviceId deviceId = new DeviceId();
        if (source.contains("imei=")){
            // Arrives as: DeviceId(imei=*val*)
            String value = source.substring(source.indexOf("=") + 1, source.length() - 1);
            if(!value.isEmpty()){
                deviceId.setImei(value);
            }
            return deviceId;

        }else {
            // If arrived as: imei,*val*
            Pattern pattern = Pattern.compile(".*imei,([^,]*)");
            Matcher matcher = pattern.matcher(source);
            boolean found = matcher.find();
            if (found) {
                deviceId.setImei(matcher.group(1));
                return deviceId;
            }
            else {
                throw new RuntimeException("Pattern for deviceid did not match");
            }
        }
    }
}