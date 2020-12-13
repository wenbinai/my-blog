package edu.nefu.myblog.dao;

import edu.nefu.myblog.pojo.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenDao extends JpaRepository<RefreshToken, String> {
    int deleteAllByUserId(String userId);

    RefreshToken findOneByTokenKey(String tokenKey);
}
