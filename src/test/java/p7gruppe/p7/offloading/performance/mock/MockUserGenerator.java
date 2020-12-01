package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.performance.APISupplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MockUserGenerator {

    private long randomSeed = 0L;
    private double proportionOfMaliciousUsers = 0.0;

    public MockUserGenerator() {}

    public MockUserGenerator(long randomSeed, double proportionOfMaliciousUsers) {
        this.randomSeed = randomSeed;
        this.proportionOfMaliciousUsers = proportionOfMaliciousUsers;
    }

    public List<MockUser> generateUsers(int amountOfUsers, APISupplier apiSupplier){
        int amountOfMaliciousUsers = (int) Math.round(amountOfUsers * proportionOfMaliciousUsers);
        List<MockUser> users = new ArrayList<>();

        for (int i = 0; i < amountOfUsers; i++) {
            UserCredentials userCredentials = new UserCredentials().username("user" + i).password("password");
            boolean isMalicious = i < amountOfMaliciousUsers;
            users.add(new MockUser(isMalicious, userCredentials, apiSupplier));
        }

        Collections.shuffle(users, new Random(randomSeed));
        return users;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public void setProportionOfMaliciousUsers(float proportionOfMaliciousUsers) {
        this.proportionOfMaliciousUsers = proportionOfMaliciousUsers;
    }
}
