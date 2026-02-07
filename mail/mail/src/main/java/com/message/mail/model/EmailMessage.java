package com.message.mail.model;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;

@Configuration
@Getter
@Setter
public class EmailMessage{
   public String id;
 public String from;
 public String to;
 public Date date;
 public String subject;
 public List<EmailAttachment> obj;
    public String content;
   public boolean hashtml;
   public boolean hasdocs;
   public boolean read;
}