package com.b303.mokkozi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ReportBoard extends BaseEntity{

    private String content;
    private Date regDate;
    private String result;


    @ManyToOne
    @JoinColumn(name = "board_id",nullable = false)
    private Board board;

    @PrePersist
    public void createdAt() {
        this.regDate = new Date();
    }

}
