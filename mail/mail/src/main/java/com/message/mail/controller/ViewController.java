package com.message.mail.controller;


import com.message.mail.model.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller("/")
public class ViewController {

    @Autowired
    private EmailController obj;

    //view
    @RequestMapping("/")
    public String view(Model model) {
        try {
            ResponseEntity<List<EmailMessage>> o = obj.view();
            if (o.getStatusCode() == HttpStatus.OK) {
                model.addAttribute("all", o.getBody());
                System.out.println(o.getBody());
                model.addAttribute("size", o.getBody().size());
            } else {
                model.addAttribute("error", "Failed to load");
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }

    @RequestMapping("/view/{id}")
    public String view(@PathVariable int id, Model model) {
        try {
            ResponseEntity<EmailMessage> o = obj.detail(id);
            if (o.getStatusCode() == HttpStatus.OK) {
                model.addAttribute("email", o.getBody());
            } else {
                model.addAttribute("error", "Failed to load");
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "view";
    }


}