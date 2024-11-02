package org.topalovic.backend.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.repository.UserRepository;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserProfile> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            UserProfile myUser = userOptional.get();
            return User.builder()
                    .username(myUser.getUsername())
                    .password(myUser.getPassword())
                    .roles(myUser.getRoles().split(","))
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
