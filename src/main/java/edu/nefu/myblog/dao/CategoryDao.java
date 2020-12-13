package edu.nefu.myblog.dao;

import edu.nefu.myblog.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CategoryDao extends JpaRepository<Category, String> {
    Category findOneById(String categoryId);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_categories` set `status` = '0' where id = ?")
    int deleteByUpdateState(String categoryId);
}
