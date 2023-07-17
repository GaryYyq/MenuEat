package com.garyyyq.menueats.resource;

import lombok.Data;

@Data
public class Email {
    private String to;
    private String subject;
    private String text;
}
