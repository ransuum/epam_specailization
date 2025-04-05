package org.epam.security.userconfiguration;

import lombok.RequiredArgsConstructor;
import org.epam.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserManagerConfig implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .map(UserConfig::new)
                .orElseThrow(() -> new UsernameNotFoundException("UserEmail: " + username + " does not exist"));
    }
}
