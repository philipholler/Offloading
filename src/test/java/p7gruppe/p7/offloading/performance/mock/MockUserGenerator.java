package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.performance.APISupplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MockUserGenerator {

    private double proportionOfMaliciousUsers = 0.0;
    private final APISupplier apiSupplier;
    private int nextId = 0;

    public MockUserGenerator(APISupplier apiSupplier) {
        this.apiSupplier = apiSupplier;
    }

    public List<MockUser> generateUsers(int amountOfUsers, long randomSeed){
        int amountOfMaliciousUsers = (int) Math.round(amountOfUsers * proportionOfMaliciousUsers);
        List<MockUser> users = new ArrayList<>();

        int numberOfUsers = nextId + amountOfUsers;
        for (int i = nextId; i < numberOfUsers; i++) {
            UserCredentials userCredentials = new UserCredentials().username("user" + nextId).password("password");
            boolean isMalicious = i < amountOfMaliciousUsers;
            users.add(new MockUser(isMalicious, userCredentials, apiSupplier));
            nextId++;
        }

        Collections.shuffle(users, new Random(randomSeed));
        return users;
    }

    public void setProportionOfMaliciousUsers(double proportionOfMaliciousUsers) {
        this.proportionOfMaliciousUsers = proportionOfMaliciousUsers;
    }
}
