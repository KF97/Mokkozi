package com.b303.mokkozi.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Board extends BaseEntity{
    @Column(columnDefinition = "BLOB")
    private String content;
    private Date regDate;
    private String active; //활동, 정지

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void createdAt() {
        this.regDate = new Date();
    }

}
