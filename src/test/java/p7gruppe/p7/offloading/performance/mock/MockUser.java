package p7gruppe.p7.offloading.performance.mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import p7gruppe.p7.offloading.api.UsersApi;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.performance.APISupplier;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MockUser {

    public final boolean isMalicious;
    public final UserCredentials userCredentials;

    private final APISupplier apiSupplier;
    private Runnable onRegistered;

    public MockUser(boolean isMalicious, UserCredentials userCredentials, APISupplier apiSupplier) {
        this.isMalicious = isMalicious;
        this.userCredentials = userCredentials;
        this.apiSupplier = apiSupplier;
    }

    public void register(){
        ResponseEntity<UserCredentials> response = apiSupplier.usersApi.createUser(userCredentials);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("User registration failed for " + userCredentials.toString());
        }

        if (onRegistered != null) {
            onRegistered.run();
        }


    }

    @Override
    public String toString() {
        return "User : " + userCredentials.toString() + "\n Malicious: " + isMalicious;
    }

    public void setOnRegistered(Runnable onRegistered) {
        if(this.onRegistered != null)
            throw new IllegalStateException("Onregistered set twice for same user");
        this.onRegistered = onRegistered;
    }
}
