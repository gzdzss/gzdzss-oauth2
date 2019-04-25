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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
            return getDetails(optionalUser.get());
        }
        throw new UsernameNotFoundException("用户名不存在");
    }


    @Transactional(rollbackFor = Exception.class)
    public GzdzssUserDetails loadUserByGithubId(String githubId, String githubToken, String avatarUrl, String nickName) {
        Optional<User> optionalUser = userRepository.findByGithubId(githubId);
        if (optionalUser.isPresent()) {
            return getDetails(optionalUser.get());
        } else {
            User user  = new User();
            user.setGithubId(githubId);
            user.setEnabled(true);
            user.setGithubToken(githubToken);
            user.setAvatarUrl(avatarUrl);
            user.setNickName(nickName);
            List<Authorities> authorities = new ArrayList<>(1);
            authorities.add(new Authorities("USER"));
            user.setAuthoritiesList(authorities);
            userRepository.save(user);
            return getDetails(user);
        }

    }


    private GzdzssUserDetails getDetails(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Authorities auth : user.getAuthoritiesList()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(auth.getAuthority());
            authorities.add(grantedAuthority);
        }
        return new GzdzssUserDetails(user.getId(), user.getUsername(), user.getPassword(), user.getEnabled(), authorities, user.getAvatarUrl(), user.getNickName());
    }


}
