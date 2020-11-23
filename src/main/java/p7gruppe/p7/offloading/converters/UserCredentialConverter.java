package p7gruppe.p7.offloading.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import p7gruppe.p7.offloading.model.UserCredentials;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class UserCredentialConverter implements Converter<String, UserCredentials> {

    @Override
    public UserCredentials convert(String source) {
        // /UserCredentials(username=user1,%20password=password1)

        if (source.contains("username=")) {
            Pattern pattern = Pattern.compile(".*username=([^,]*),.*password=([^)]*).*");
            Matcher matcher = pattern.matcher(source);
            boolean found = matcher.find();
            if(found){
                UserCredentials credentials = new UserCredentials();
                credentials.setUsername(matcher.group(1));
                credentials.password(matcher.group(2));
                return credentials;
            } else {
                throw new RuntimeException("Pattern for usercredential did not match");
            }

        } else {
            UserCredentials credentials = new UserCredentials();
            String[] elements = source.split(",");

            credentials.setUsername(elements[1]);
            credentials.setPassword(elements[3]);

            return credentials;
        }
    }
}
