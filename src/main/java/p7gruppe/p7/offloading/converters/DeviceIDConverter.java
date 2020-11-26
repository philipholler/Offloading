package p7gruppe.p7.offloading.converters;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import p7gruppe.p7.offloading.model.DeviceId;

@Component
class DeviceIDConverter implements Converter<String, DeviceId> {

    @Override
    public DeviceId convert(String source) {
        DeviceId deviceId = new DeviceId();
        deviceId.setImei(source.substring(source.indexOf(',') + 1));
        return deviceId;
    }
}
