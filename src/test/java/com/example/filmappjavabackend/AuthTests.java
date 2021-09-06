package com.example.filmappjavabackend;

import com.example.filmappjavabackend.models.User;
import com.example.filmappjavabackend.models.enums.ERole;
import com.example.filmappjavabackend.repositories.UserRepository;
import com.example.filmappjavabackend.security.jwt.JWTUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application.properties")
public class AuthTests {

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

    @Test(expected = BadCredentialsException.class)
    public void testLoginDisabledUser() {
        User user = new User( "test@test.com",
                encoder.encode("password"),
                "test"
        );

        user.setRole(ERole.ROLE_USER);
        user.setActive(false);
        userRepository.save(user);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("test", "password"));
    }

    @Test(expected = BadCredentialsException.class)
    public void testBadPasswordProvided() {
        User user = new User( "test@test.com",
                encoder.encode("password"),
                "test"
        );

        user.setRole(ERole.ROLE_USER);
        userRepository.save(user);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("test", "password_bad"));
    }

}
