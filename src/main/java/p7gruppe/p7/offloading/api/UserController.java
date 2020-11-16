package p7gruppe.p7.offloading.api;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import p7gruppe.p7.offloading.database.ConnectionManager;
import p7gruppe.p7.offloading.database.DataManager;
import p7gruppe.p7.offloading.database.QueryManager;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.model.UserCredentials;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;
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
        try {
          List<Job> listOfJobs = DataManager.getJobsBelongingToUser(username.getUsername());
           return new ResponseEntity<List<Job>>(listOfJobs, HttpStatus.OK);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
