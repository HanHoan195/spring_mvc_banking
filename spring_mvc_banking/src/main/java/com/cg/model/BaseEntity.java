package com.cg.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.validation.Errors;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public abstract class BaseEntity {
    @Column(columnDefinition = "boolean default false")
    private boolean deleted;
    @CreationTimestamp
    @Column(name = "create_At", updatable = false)
    private Date createdAt;
    @CreatedBy
    @Column(name = "created_by")
    private Long createBy;

    @UpdateTimestamp
    @Column(name = "update_at")
    private Date updateAt;

    @LastModifiedBy
    @Column(name = "update_by")
    private Long updateBy;

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public boolean isDeleted() {
        return deleted;
    }


    public abstract boolean supports(Class<?> aClass);

    public abstract void validate(Object target, Errors errors);
}
