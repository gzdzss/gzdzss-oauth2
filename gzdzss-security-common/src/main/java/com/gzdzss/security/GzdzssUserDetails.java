package com.gzdzss.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/24
 */

@ToString
public class GzdzssUserDetails implements UserDetails {

    @Getter
    private final Long id;
    @Getter
    @Setter
    private String avatarUrl;
    @Getter
    @Setter
    private String nickName;
    @Setter
    private String password;
    private final String username;
    private final boolean enabled;
    @Setter
    private Set<GrantedAuthority> authorities;


    public GzdzssUserDetails(Long id, String username, String password, boolean enabled, Set<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
        this.password = password;
        this.authorities = authorities;
    }

    public GzdzssUserDetails(Long id, String username, String password, boolean enabled, Set<GrantedAuthority> authorities, String avatarUrl, String nickName) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
        this.password = password;
        this.authorities = authorities;
        this.avatarUrl = avatarUrl;
        this.nickName = nickName;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
