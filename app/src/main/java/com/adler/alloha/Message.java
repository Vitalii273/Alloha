package com.adler.alloha;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Message {
    public String userName;
    public String textMessage;
    private long messageTime;

    public Message() {
    }

    public Message(String userName, String textMessage) {
        this.userName = userName;
        this.textMessage = textMessage;
        this.messageTime = new Date().getTime();
    }
}
