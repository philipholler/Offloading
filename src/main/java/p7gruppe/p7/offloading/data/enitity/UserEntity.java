package p7gruppe.p7.offloading.data.enitity;

import p7gruppe.p7.offloading.statistics.ServerStatistic;

import javax.persistence.*;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(unique = true)
    private String userName;


    private String password;

    private long cpuTimeContributedInMs;
    private long cpuTimeSpentInMs;

    protected UserEntity() {
    }

    public UserEntity(String userName, String password) {
        this.userName = userName;
        this.password = password;

        this.cpuTimeContributedInMs = 0;
        this.cpuTimeSpentInMs = 0;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public long getCpuTimeContributedInMs() {
        return cpuTimeContributedInMs;
    }

    public void setCpuTimeContributedInMs(long cpuTimeContributed) {
        this.cpuTimeContributedInMs = cpuTimeContributed;
        ServerStatistic.addCPUTimeDataPoint(cpuTimeContributed - cpuTimeSpentInMs, userName);
    }

    public long getCpuTimeSpentInMs() {
        return cpuTimeSpentInMs;
    }

    public void setCpuTimeSpentInMs(long cpuTimeSpent) {
        this.cpuTimeSpentInMs = cpuTimeSpent;
        ServerStatistic.addCPUTimeDataPoint(cpuTimeContributedInMs - cpuTimeSpent, userName);
    }

    public Long getUserId() {
        return userId;
    }
}
