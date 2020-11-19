package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.*;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(unique=true)
    private String userName;



    private String password;

    protected UserEntity(){}

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




}
