package com.gzdzsss.authserver.jpa.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/24
 */

@Table(name = "gzdzss_user")
@Entity
@Data
@ToString(callSuper = true)
public class User extends AbstractEntity {

    private String username;

    private String password;

    private String avatarUrl;

    private String nickName;

    private Boolean enabled;

    private String githubId;

    private String githubToken;

    @OneToMany(targetEntity = Authorities.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private Collection<Authorities> authoritiesList;

}
