package com.insurebroker.security;

import com.insurebroker.entity.User;
import com.insurebroker.entity.UserRoleEntity;
import com.insurebroker.repository.UserRepository;
import com.insurebroker.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<UserRoleEntity> roles = userRoleRepository.findByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name()))
                .collect(Collectors.toList());

        return new UserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash(), authorities, user.getActive());
    }
}