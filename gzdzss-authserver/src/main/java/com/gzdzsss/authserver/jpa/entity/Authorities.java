package com.gzdzsss.authserver.jpa.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/24
 */

@Table(name = "gzdzss_authorities")
@Entity
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class Authorities extends AbstractEntity {


    public Authorities(String authority) {
        this.authority = authority;
    }

    private String userId;

    private String authority;
}
