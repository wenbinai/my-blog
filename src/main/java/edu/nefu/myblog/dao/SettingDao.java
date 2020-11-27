package edu.nefu.myblog.dao;

import edu.nefu.myblog.pojo.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingDao extends JpaRepository<Setting, String> {
    Setting findOneByKeyLabel(String keyLabel);
}
