package edu.nefu.myblog.dao;

import edu.nefu.myblog.pojo.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface ImageDao extends JpaRepository<Image, String> {
    @Modifying
    @Query(nativeQuery = true, value = "update `tb_images` set `state` = '0' where id = ?")
    int deleteImageByUpdateState(String imageId);
}
