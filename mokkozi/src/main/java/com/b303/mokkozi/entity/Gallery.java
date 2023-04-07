package com.b303.mokkozi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Gallery extends BaseEntity{

    @Column(nullable = false)
    private String filePath;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String sort;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id",nullable = true)
    private Board board;

}
