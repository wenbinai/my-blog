package edu.nefu.myblog.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tb_settings")
public class Setting {
    @Id
    private String id;
    private String keyLabel;
    private String value;
    private Date createTime;
    private Date updateTime;

    @Override
    public String toString() {
        return "Setting{" +
                "id='" + id + '\'' +
                ", key='" + keyLabel + '\'' +
                ", value='" + value + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyLabel() {
        return keyLabel;
    }

    public void setKeyLabel(String key) {
        this.keyLabel = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
