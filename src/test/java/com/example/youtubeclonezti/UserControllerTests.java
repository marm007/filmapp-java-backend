package com.example.youtubeclonezti;

import com.example.youtubeclonezti.models.User;
import com.example.youtubeclonezti.models.enums.ERole;
import com.example.youtubeclonezti.repositories.UserRepository;
import com.example.youtubeclonezti.security.jwt.JWTUtils;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application.properties")
public class UserControllerTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTUtils jwtUtils;

    @Before
    public void setup() throws Exception {
        mongoTemplate.remove(new Query(), User.class);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(), User.class);
    }

    @Test
    public void testGetExistingUserById() throws Exception {
        User user =new User("test@test.com",
                encoder.encode("password"),
                "test1");
        user.setId(new ObjectId("ae9ace903d59322d1b45a743"));
        userRepository.save(user);
        assertEquals(user, userRepository.findById(user.getId()).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + user.getId())));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testGetNonExistingUser() throws Exception {
        User user =new User("test@test.com",
                encoder.encode("password"),
                "test");
        user.setId(new ObjectId("ae9ace903d59322d1b45a743"));
        userRepository.save(user);
        userRepository.findById("ae9ace903d59322d1b45a741").orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + user.getId()));
    }

    @Test
    public void testGetUserByUsername() throws Exception {
        User user =new User("test@test.com",
                encoder.encode("password"),
                "test");
        user.setId(new ObjectId("ae9ace903d59322d1b45a743"));
        userRepository.save(user);
        assertEquals(user, userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + user.getId())));
    }

    @Test
    public void testExistsByUsername() throws Exception {
       assertEquals(userRepository.existsByUsername("none_existing_username"), false);
    }

    @Test
    public void testExistsByEmail() throws Exception {
        User user =new User("test@test.com",
                encoder.encode("password"),
                "test");
        userRepository.save(user);

        assertEquals(userRepository.existsByEmail("test@test.com"), true);
    }

    @Test
    public void testCreateUser() throws Exception {
        ResponseEntity<User> postResponse = new ResponseEntity<>(userRepository.save(new User("test@test.com",
                encoder.encode("password"),
                "test")), HttpStatus.OK);
        assertNotNull(postResponse);
        assertNotNull(postResponse.getBody());
        assertEquals(postResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test(expected = DuplicateKeyException.class)
    public void testCreateUserWithExistingEmail() throws Exception {
        User user1 =new User("test@test.com",
                encoder.encode("password"),
                "test1");

         User user2 =new User("test@test.com",
                encoder.encode("password"),
                "test2");

       userRepository.save(user1);
       userRepository.save(user2);
    }

    @Test(expected = DuplicateKeyException.class)
    public void testCreateUserWithExistingUsername() throws Exception {
        User user1 =new User("test1@test.com",
                encoder.encode("password"),
                "test");

         User user2 =new User("test2@test.com",
                encoder.encode("password"),
                "test");

       userRepository.save(user1);
       userRepository.save(user2);
    }

    @Test(expected = DuplicateKeyException.class)
    public void testCreateUserWithExistingUsernameAndEmail() throws Exception {
        User user1 =new User("test@test.com",
                encoder.encode("password"),
                "test");

         User user2 =new User("test@test.com",
                encoder.encode("password"),
                "test");

       userRepository.save(user1);
       userRepository.save(user2);
    }

    @Test
    public void testUpdateUserById() {
        User user = new User( "test@test.com",
                encoder.encode("password"),
                "test"
        );

        user.setRole(ERole.ROLE_USER);
        user.setId(new ObjectId("ae9ace903d59322d1b45a743"));
        userRepository.save(user);
        user = userRepository.findById("ae9ace903d59322d1b45a743").orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        assertNotNull(user);
        user.setUsername("test_changed");
        ResponseEntity<User> response = new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        user = userRepository.findById("ae9ace903d59322d1b45a743").orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        assertEquals(user.getUsername(), "test_changed");
    }

    @Test
    public void testDeleteUserById() {
        User user = new User( "test@test.com",
                encoder.encode("password"),
                "test"
        );

        user.setRole(ERole.ROLE_USER);
        user.setId(new ObjectId("ae9ace903d59322d1b45a743"));
        userRepository.save(user);
        userRepository.deleteById(user.getId());
        try {
            user = userRepository.findById(user.getId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        } catch (final HttpClientErrorException e) {
            assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

    @Test
    public void testGetAllUsers() {
        User user1 =new User("test1@test.com",
                encoder.encode("password"),
                "test1");

        User user2 =new User("test2@test.com",
                encoder.encode("password"),
                "test2");

        userRepository.save(user1);
        userRepository.save(user2);
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        assertEquals(userRepository.findAll(), users);
    }

}
