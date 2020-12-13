package edu.nefu.myblog.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tb_categories")
public class Category {
    @Id
    private String id;
    // 分类名称
    private String name;
    // 分类拼音
    private String pinyin;
    // 分类描述
    private String description;
    // 分类状态
    private String status;
    // 分类顺序
    @Column(name = "`order`")
    private int order;

    private Date createTime;
    private Date updateTime;

    public Category() {
    }

    public Category(String id, String name, String pinyin, String description, String status, int order, Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.pinyin = pinyin;
        this.description = description;
        this.status = status;
        this.order = order;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Category(int order, Date createTime) {
        this.order = order;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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
