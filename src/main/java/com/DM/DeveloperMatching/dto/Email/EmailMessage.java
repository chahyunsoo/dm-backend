package com.DM.DeveloperMatching.dto.Email;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Builder
public class EmailMessage {

    private String receiver;
    private String title;
    private String content;

    public void setEmailContents(String receiver, String title, String content) {
        this.receiver = content;
        this.title = title;
        this.content = content;
    }

}