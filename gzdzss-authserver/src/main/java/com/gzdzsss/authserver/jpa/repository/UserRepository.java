package com.gzdzsss.authserver.jpa.repository;

import com.gzdzsss.authserver.jpa.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/24
 */

public interface UserRepository extends CrudRepository<User, Long> {


    Optional<User> findByUsername(String username);
}
