package edu.nefu.myblog.dao;

import edu.nefu.myblog.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, String> {
    User findOneByUserName(String userName);

    User findOneByEmail(String email);
}
