package edu.brajovic.products.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.brajovic.products.data.UsersRepository;
import edu.brajovic.products.models.UserEntity;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UsersRepository userRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.userRepository = usersRepository;
    }

    public UserDetails loadUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return null;
    }
}
