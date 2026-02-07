package com.message.mail.model;


import lombok.Data;

@Data
public class EmailAttachment{
    byte[] content;
    int size;
    String name;
    String contentType;
}