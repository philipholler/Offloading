package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.api.UsersApi;
import p7gruppe.p7.offloading.model.UserCredentials;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MockUser {

    public final boolean isMalicious;
    public final UserCredentials userCredentials;

    private final APISupplier apiSupplier;

    public MockUser(boolean isMalicious, UserCredentials userCredentials, APISupplier apiSupplier) {
        this.isMalicious = isMalicious;
        this.userCredentials = userCredentials;
        this.apiSupplier = apiSupplier;
    }

    public void register(){
        apiSupplier.usersApi.createUser(userCredentials);
    }

    @Override
    public String toString() {
        return "User : " + userCredentials.toString() + "\n Malicious: " + isMalicious;
    }
}
