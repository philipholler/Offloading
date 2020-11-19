package p7gruppe.p7.offloading.api;


import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestUserGeneration {

    public static void createTestUser(MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/users/username,user1,password,password1"));
        mockMvc.perform(post("/users/username,user2,password,password1"));
        mockMvc.perform(post("/users/username,user3,password,password1"));
        mockMvc.perform(post("/users/username,user4,password,password1"));
    }


}
