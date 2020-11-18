package p7gruppe.p7.offloading.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-11-18T11:02:06.033+01:00[Europe/Copenhagen]")

@Controller
@RequestMapping("${openapi.offloading.base-path:}")
public class UsersApiController implements UsersApi {

    @Autowired
    JobScheduler jobScheduler;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeviceRepository deviceRepository;

    private final NativeWebRequest request;

    @Override
    public ResponseEntity<UserCredentials> createUser(UserCredentials userCredentials) {
        if (userRepository.userExists(userCredentials.getUsername())){
            return ResponseEntity.badRequest().build();
        }
        UserEntity user = new UserEntity(userCredentials.getUsername(), userCredentials.getPassword());
        userRepository.save(user);

        return ResponseEntity.ok(userCredentials);
    }

    @Override
    public ResponseEntity<UserCredentials> deleteUser(UserCredentials userCredentials) {
        return null;
    }

    @Override
    public ResponseEntity<UserCredentials> login(UserCredentials userCredentials) {
        return null;
    }




    @org.springframework.beans.factory.annotation.Autowired
    public UsersApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
}
