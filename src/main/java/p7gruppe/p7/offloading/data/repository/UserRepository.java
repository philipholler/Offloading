package p7gruppe.p7.offloading.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.model.UserCredentials;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    @Query(value = "SELECT EXISTS (SELECT * FROM user_entity WHERE user_name = ?1)", nativeQuery = true)
    boolean userExists(String username);

    @Query(value = "SELECT EXISTS (SELECT * FROM user_entity WHERE user_name = ?1 && password = ?2)", nativeQuery = true)
    boolean isPasswordCorrect(String username, String password);

    @Query(value = "SELECT user_id FROM user_entity WHERE user_name = ?1", nativeQuery = true)
    long getUserID(String username);

    @Query(value = "SELECT * FROM user_entity WHERE user_name = ?1", nativeQuery = true)
    UserEntity getUserByUsername(String username);
}
