package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(unique = true)
    private String userName;
    private String password;
    private BigInteger cpuTime;


    protected UserEntity() {
    }

    public UserEntity(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }


    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setCpuTime(BigInteger cpuTime) {
        this.cpuTime = cpuTime;
    }

    public BigInteger getCpuTime() {
        return cpuTime;


    }
}
