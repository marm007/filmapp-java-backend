package com.example.youtubeclonezti.controllers;

import com.example.youtubeclonezti.models.Film;
import com.example.youtubeclonezti.models.Playlist;
import com.example.youtubeclonezti.models.User;
import com.example.youtubeclonezti.models.UserForgotPassword;
import com.example.youtubeclonezti.models.UserResetPassword;
import com.example.youtubeclonezti.payloads.response.ErrorResponse;
import com.example.youtubeclonezti.payloads.response.MessageResponse;
import com.example.youtubeclonezti.repositories.FilmRepository;
import com.example.youtubeclonezti.repositories.PlaylistRepository;
import com.example.youtubeclonezti.repositories.UserRepository;
import com.mongodb.MongoWriteException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    PasswordEncoder encoder;

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

    @GetMapping("/me")
    public ResponseEntity<User> getMyData() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User _user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userDetails.getUsername()));

        return new ResponseEntity<>(_user, HttpStatus.OK);
    }

    @GetMapping("/me/playlists")
    public ResponseEntity<List<Playlist>> getMyPlaylists() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User _user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userDetails.getUsername()));


        return new ResponseEntity<>(playlistRepository.findAllByAuthorID(_user.getId()), HttpStatus.OK);
    }

    @GetMapping("/me/films")
    public ResponseEntity<List<Film>> getMyFilms() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User _user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userDetails.getUsername()));


        return new ResponseEntity<>(filmRepository.findAllByAuthorID(_user.getId()), HttpStatus.OK);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<String> sendResetToken(@Valid @RequestBody UserForgotPassword userForgotPassword) throws MessagingException, IOException {


        User _user = userRepository.findByEmail(userForgotPassword.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userForgotPassword.getEmail()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 5);

        Date tokenExpiredDate = calendar.getTime();
        String resetPasswordToken = RandomStringUtils.randomAlphanumeric(32);

        // ustawienie tokena do resetowania hasła oraz jego ważnosci

        _user.setResetPasswordExpires(tokenExpiredDate);
        _user.setResetPasswordToken(resetPasswordToken);

        userRepository.save(_user);

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(_user.getEmail());
        helper.setSubject("Reset your password");
        helper.setText(resetPasswordToken, true);
        javaMailSender.send(msg);

        return new ResponseEntity<>("Token sent to your email", HttpStatus.OK);
    }

    @PostMapping("/password/reset/{token}")
    public ResponseEntity<?> resetPassword(@Valid @PathVariable String token, @Valid @RequestBody UserResetPassword userResetPassword) {

        User _user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new UserNotFoundException("Token not valid!"));

        Date tokenExpiredDate = _user.getResetPasswordExpires();

        if (tokenExpiredDate == null || _user.getResetPasswordToken() == null) {
            return new ResponseEntity<>(new ErrorResponse("User has no active reset requests!"), HttpStatus.BAD_REQUEST);
        }

        if (tokenExpiredDate.compareTo(new Date()) < 0) {
            return new ResponseEntity<>(new ErrorResponse("Token expired!"), HttpStatus.BAD_REQUEST);
        }

        if (!_user.getResetPasswordToken().equals(token)) {
            return new ResponseEntity<>(new ErrorResponse("Bad token provided!"), HttpStatus.BAD_REQUEST);
        }

        _user.setPassword(encoder.encode(userResetPassword.getPassword()));
        _user.setResetPasswordToken(null);
        _user.setResetPasswordExpires(null);
        userRepository.save(_user);
        return new ResponseEntity<>(new ErrorResponse("Password reseated successfully!"), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<?> mongoWriteException(MongoWriteException e) throws IOException {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("User with this credentials already exists!"));

    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class UserNotFoundException extends RuntimeException
    {
        /**
         *
         */
        private static final long serialVersionUID = 5987484849652172400L;

        public UserNotFoundException(String exception) {
            super(exception);
        }
    }
}
