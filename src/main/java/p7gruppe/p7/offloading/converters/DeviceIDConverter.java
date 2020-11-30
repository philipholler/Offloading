package p7gruppe.p7.offloading.converters;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import p7gruppe.p7.offloading.model.DeviceId;

@Component
class DeviceIDConverter implements Converter<String, DeviceId> {

    @Override
    public DeviceId convert(String source) {
        DeviceId deviceId = new DeviceId();
        // Arrives as: DeviceId(imei=null)
        String value = source.substring(source.indexOf("=") + 1, source.length() - 1);
        deviceId.setImei(value);
        return deviceId;
    }
}
