package com.b303.mokkozi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ChatMessage extends BaseEntity{
    private String message;
    private Date regDate;

    @ManyToOne
    @JoinColumn(name = "room_id",nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @PrePersist
    public void createdAt() {
        this.regDate = new Date();
    }
}
