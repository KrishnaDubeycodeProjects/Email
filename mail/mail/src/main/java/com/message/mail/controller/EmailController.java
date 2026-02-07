package com.message.mail.controller;


import com.message.mail.Service.EmailService;
import com.message.mail.model.EmailAttachment;
import com.message.mail.model.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/email")
@CrossOrigin("*")
public class EmailController{

    @Autowired
    private EmailService emailService;

    @RequestMapping("/view")
    public ResponseEntity<List<EmailMessage>> view(){
       try{
           return new ResponseEntity<>(emailService.getAllMessage(),HttpStatus.OK);
       }catch(Exception e){
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
       }
    }

    @GetMapping("/email/view/{id}")
    public ResponseEntity<EmailMessage> detail(@PathVariable("id") int id){
        try {
          return  new ResponseEntity<>(emailService.getMessageByNumber(id), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/views/{id}/attachment/{index}")
    public ResponseEntity<byte[]> attachment(@PathVariable("id") int id , @PathVariable("index") int index,@RequestParam("action") String action){
        try {
            HttpHeaders header = new HttpHeaders();
            EmailAttachment attachment = emailService.getMessageAttachment(id, index);
            header.setContentType(MediaType.parseMediaType(attachment.getContentType()));
            header.setContentDispositionFormData("attachment",attachment.getName());
            header.setContentLength(attachment.getSize());
            return new ResponseEntity<>(attachment.getContent(),header,HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/views/unreadCount")
    public ResponseEntity<Integer> Unread(){
        try {
            return ResponseEntity.ok(emailService.unreadCount());
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/views/{id}/read")
    public ResponseEntity<Void> Unread(@PathVariable("id") int id){
        try {
            emailService.seen(id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("views/search")
    public ResponseEntity<List<EmailMessage>> search(@RequestParam("q") String q){
        try{
            List<EmailMessage> mm = emailService.search(q);
            return new ResponseEntity<>(mm,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @PostMapping("compose")
//    public void compose(@RequestParam String emailS , String to , )



}