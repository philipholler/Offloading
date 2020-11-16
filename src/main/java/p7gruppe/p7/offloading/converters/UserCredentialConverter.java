package p7gruppe.p7.offloading.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import p7gruppe.p7.offloading.model.UserCredentials;

@Component
class UserCredentialConverter implements Converter<String, UserCredentials> {

    @Override
    public UserCredentials convert(String source) {
        UserCredentials credentials = new UserCredentials();
        credentials.setUsername(source);
        return credentials;
    }
}
