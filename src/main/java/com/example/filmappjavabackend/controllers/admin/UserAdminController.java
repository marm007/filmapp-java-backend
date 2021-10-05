package com.example.filmappjavabackend.controllers.admin;

import com.example.filmappjavabackend.models.User;
import com.example.filmappjavabackend.models.updates.AdminActiveUpdate;
import com.example.filmappjavabackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Tag(name = "UserAdmin")
@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return new ResponseEntity<>(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable String id,
                                                   @Valid @RequestBody AdminActiveUpdate userAdminUpdate) {

        User _user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (userAdminUpdate.getIsActive().equals("active") || userAdminUpdate.getIsActive().equals("disable")) {

            boolean _isActive = userAdminUpdate.getIsActive().equals("active");
            if (_user.isActive() == _isActive) {
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            } else {
                _user.setActive(_isActive);
                return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class UserNotFoundException extends RuntimeException
    {
        /**
         *
         */
        private static final long serialVersionUID = -5716022460846861836L;

        public UserNotFoundException(String exception) {
            super(exception);
        }
    }
}
