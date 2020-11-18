package p7gruppe.p7.offloading.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import p7gruppe.p7.offloading.model.UserCredentials;

@Component
class UserCredentialConverter implements Converter<String, UserCredentials> {

    @Override
    public UserCredentials convert(String source) {
        UserCredentials credentials = new UserCredentials();
        String[] elements = source.split(",");

        credentials.setUsername(elements[1]);
        credentials.setPassword(elements[3]);

        return credentials;
    }
}
