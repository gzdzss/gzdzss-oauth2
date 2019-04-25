package com.gzdzsss.authserver.config.security;

import com.gzdzss.security.GzdzssUserDetails;
import com.gzdzsss.authserver.jpa.entity.Authorities;
import com.gzdzsss.authserver.jpa.entity.User;
import com.gzdzsss.authserver.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/24
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public GzdzssUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Set<GrantedAuthority> authorities = new HashSet<>();
            for (Authorities auth: user.getAuthoritiesList()) {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(auth.getAuthority());
                authorities.add(grantedAuthority);
            }
            return new GzdzssUserDetails(user.getId(), user.getUsername(), user.getPassword(), user.getEnabled(), authorities);
        }
        return null;
    }
}
