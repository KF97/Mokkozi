package com.b303.mokkozi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class UserInterest extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String interest;

}
