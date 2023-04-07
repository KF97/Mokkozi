package com.b303.mokkozi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserFollow extends BaseEntity{

    @ManyToOne
    @JoinColumn(name="from_user_id")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name="to_user_id")
    private User toUser;

    @Builder
    UserFollow(User fromUser,User toUser){
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

}
