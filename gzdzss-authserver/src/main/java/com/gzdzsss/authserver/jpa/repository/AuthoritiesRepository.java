package com.gzdzsss.authserver.jpa.repository;

import com.gzdzsss.authserver.jpa.entity.Authorities;
import org.springframework.data.repository.CrudRepository;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/24
 */

public interface AuthoritiesRepository extends CrudRepository<Authorities, Long> {
}
