package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.performance.JobStatistic;

import java.util.ArrayList;
import java.util.List;

public class UserBase {

    private final List<MockUser> users;
    private final List<MockEmployer> employers;
    private final List<MockWorker> workers;

    private final List<Simulatable> clients;

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

    public void startSimulation(){
        for (Simulatable simulatedClient : clients) simulatedClient.start();
    }

    public void update(){
        for (Simulatable simulatedClient : clients) simulatedClient.update();
    }

    public void stopSimulation(){
        for (Simulatable simulatedClient : clients) simulatedClient.stop();
    }

    public List<JobStatistic> getJobStatistics() {
        List<JobStatistic> statistics = new ArrayList<>();
        for (MockEmployer mockEmployer : employers) statistics.addAll(mockEmployer.getJobsStatistics());
        return statistics;
    }

    public List<MockUser> getUsers() {
        return users;
    }

    public List<MockEmployer> getEmployers() {
        return employers;
    }

    public List<MockWorker> getWorkers() {
        return workers;
    }
}
