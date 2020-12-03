package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.*;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(unique = true)
    private String userName;


    private String password;

    private long cpuTime;

    protected UserEntity() {
    }

    public UserEntity(String userName, String password) {
        this.userName = userName;

        this.password = password;

        this.cpuTime = 0;
    }


    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public long getCpuTime() {
        return cpuTime;
    }

    public void setCpuTime(long cpuTime) {
        this.cpuTime = cpuTime;
    }
}
