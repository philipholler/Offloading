package p7gruppe.p7.offloading.performance;

import java.util.ArrayList;
import java.util.List;

public class MockEmployerGenerator {

    public List<MockEmployer> generateEmployers(int amountOfEmployers, List<MockUser> users, APISupplier apiSupplier){
        List<MockEmployer> employers = new ArrayList<>();
        for (int i = 0; i < amountOfEmployers; i++) employers.add(new MockEmployer(users.get(i), apiSupplier));
        return employers;
    }

}
