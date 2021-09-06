package com.example.filmappjavabackend.security.services;

import com.example.filmappjavabackend.models.User;
import com.example.filmappjavabackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetailsImpl loadUserByUsername(String value) throws UsernameNotFoundException {
        User user = null;

        if (value.contains("@"))
            user = userRepository.findByEmail(value)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + value));
        else
            user = userRepository.findByUsername(value)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + value));
        if (!user.isActive())
            throw new UsernameNotFoundException("User Is Not Active with username: " + value);
        return UserDetailsImpl.build(user);
    }
}
