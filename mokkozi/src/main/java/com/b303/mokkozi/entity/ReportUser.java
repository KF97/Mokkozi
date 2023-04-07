package com.b303.mokkozi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ReportUser extends BaseEntity{

    private String content;
    private Date regDate;
    @Column(nullable = false)
    private Long targetId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @PrePersist
    public void createdAt() {
        this.regDate = new Date();
    }

}
