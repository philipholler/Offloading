package p7gruppe.p7.offloading.api;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.model.UserCredentials;

import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController implements UsersApi{
    @Override
    public ResponseEntity<UserCredentials> createUser(@Valid UserCredentials userCredentials) {
        return null;
    }

    @Override
    public ResponseEntity<UserCredentials> deleteUser(UserCredentials username) {
        return null;
    }

    @Override
    public ResponseEntity<List<Job>> getJobsForUser(UserCredentials username) {
        return null;
    }
}
