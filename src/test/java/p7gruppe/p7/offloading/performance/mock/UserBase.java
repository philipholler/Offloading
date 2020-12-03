package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.performance.APISupplier;

import java.util.ArrayList;
import java.util.List;

public class UserBase {

    private final List<MockUser> users;
    private final List<MockEmployer> employers;
    private final List<MockWorker> workers;

    private final List<Updatable> clients;

    public UserBase(List<MockUser> users, List<MockEmployer> employers, List<MockWorker> workers) {
        this.users = users;
        this.employers = employers;
        this.workers = workers;

        clients = new ArrayList<>(workers);
        clients.addAll(employers);
    }

    public void initializeUserBase(){
        for (MockUser user : users) user.register();
        for (MockWorker worker : workers) worker.login();
    }

    public void update(){
        for (Updatable simulatedClient : clients) simulatedClient.update();
    }

}
