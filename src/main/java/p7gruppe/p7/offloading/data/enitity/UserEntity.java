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

    private long cpuTimeContributedInMillis;
    private long cpuTimeSpentInMillis;

    protected UserEntity() {
    }

    public UserEntity(String userName, String password) {
        this.userName = userName;
        this.password = password;

        this.cpuTimeContributedInMillis = 0;
        this.cpuTimeSpentInMillis = 0;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public long getCpuTimeContributedInMillis() {
        return cpuTimeContributedInMillis;
    }

    public void setCpuTimeContributedInMillis(long cpuTimeContributedMillis) {
        this.cpuTimeContributedInMillis = cpuTimeContributedMillis;
        ServerStatistic.addCPUTimeDataPoint(cpuTimeContributedMillis - cpuTimeSpentInMillis, userName);
    }

    public long getCpuTimeSpentInMillis() {
        return cpuTimeSpentInMillis;
    }

    public void setCpuTimeSpentInMillis(long cpuTimeSpent) {
        cpuTimeSpent =
        this.cpuTimeSpentInMillis = cpuTimeSpent;
        ServerStatistic.addCPUTimeDataPoint(cpuTimeContributedInMillis - cpuTimeSpent, userName);
    }

    public Long getUserId() {
        return userId;
    }
}
