package com.message.mail.Service;

import com.message.mail.model.EmailAttachment;
import com.message.mail.model.EmailMessage;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.SearchTerm;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;


@Service
public class EmailService{
   private final String username = "rahuldivedi221@gmail.com";
   private final String password = "epqgsbjaudqsauir";

   // returning the Session object reConfiguring
    private Properties properties(){
        Properties o = new Properties();
        o.put("mail.store.protocol","imaps");
        o.put("mail.imaps.host","imap.gmail.com");
        o.put("mail.imaps.port","993");
        o.put("mail.imaps.starttls.enable","true");
        o.put("mail.imaps.auth","true");
    return o;
    }

    public List<EmailMessage> getAllMessage() throws IOException, MessagingException{
        Properties o = new Properties();
        o.put("mail.store.protocol","imaps");
        o.put("mail.imaps.host","imap.gmail.com");
        o.put("mail.imaps.port","993");
        o.put("mail.imaps.starttls.enable","true");
        o.put("mail.imaps.auth","true");
        Session session  = Session.getInstance(o);
        List<EmailMessage> obj = new ArrayList<>();
        try(Store store = session.getStore("imaps")){
            store.connect(username,password);
           Folder folder = store.getFolder("inbox");
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.getMessages();
            for (Message i:messages) {
                obj.add(getMessage(i));
            }
            obj.sort((m1, m2) -> m2.getDate().compareTo(m1.getDate()));
        }
        return obj;
    }

    // for my use as well as user to view a depth message one
    private EmailMessage getMessage(Message message) throws MessagingException , IOException {
        EmailMessage obj = new EmailMessage();
        obj.setId(message.getMessageNumber()+"");
        obj.setFrom(InternetAddress.toString(message.getFrom()));
        obj.setTo(InternetAddress.toString(message.getReplyTo()));
        obj.setDate(message.getSentDate());
        obj.setSubject(message.getSubject());
        obj.setRead(message.getFlags().contains(Flags.Flag.SEEN));
        Object content = message.getContent();
        if(content instanceof String){
            obj.setContent((String) content);
        }
        else if(content instanceof Multipart o){
            List<EmailAttachment> attachments = new ArrayList<>();
            for (int i = 0 ; i<o.getCount()  ;i++) {
                BodyPart bp =  o.getBodyPart(i);
               Object object= bp.getContent();
               if(Part.ATTACHMENT.equalsIgnoreCase(bp.getDisposition())){
                   obj.setHasdocs(true);
                   attachments.add(getAttachment(bp));
               }
               else{
                   if(bp.getContentType().contains("text/html")){
                       obj.setHashtml(true);

                   }else{
                       obj.setHashtml(false);
                   }
                   obj.setContent(object+"");
               }
            }
            obj.setObj(attachments);
        }
        return obj;
    }

    // to get indexed attachment
    public EmailAttachment getMessageAttachment(int msg, int index) throws MessagingException, IOException {
         EmailMessage o = getMessageByNumber(msg);
         return o.getObj().get(index);
    }




    // we can get message by number
    public EmailMessage getMessageByNumber(int n) throws MessagingException, IOException {
        Properties o  = properties();
        Session session  = Session.getInstance(o);
        List<EmailMessage> obj = new ArrayList<>();
        Store store = session.getStore("imaps");
            store.connect(username, password);
            Folder folder = store.getFolder("inbox");
            folder.open(1);

      return getMessage(folder.getMessage(n));
    }

    private EmailAttachment getAttachment(BodyPart bp) throws MessagingException , IOException{
        EmailAttachment obj = new EmailAttachment();
        obj.setName(bp.getFileName());
        obj.setSize(bp.getSize());
        obj.setContentType(bp.getContentType());

        try(ByteArrayOutputStream o = new ByteArrayOutputStream()){
            bp.getDataHandler().writeTo(o);
            obj.setContent(o.toByteArray());
        }
        return obj;
    }


    public void seen(int number) throws MessagingException {
        Properties o  = properties();
        Session session  = Session.getInstance(o);
        Store s = session.getStore("imaps");
       Folder folder= s.getFolder("inbox");
      Message message = folder.getMessage(number);
      message.setFlag(Flags.Flag.SEEN,true);
    }

    public int unreadCount() throws MessagingException {
        Properties o  = properties();
        Session session  = Session.getInstance(o);
        Store s = session.getStore("imaps");
        Folder folder= s.getFolder("inbox");
     return folder.getUnreadMessageCount();
    }

    // Searching
    public List<EmailMessage> search(String q) throws MessagingException, IOException {
        List<EmailMessage> m = new ArrayList<>();
        Properties o  = properties();
        Session session  = Session.getInstance(o);
        Store store = session.getStore("imaps");
        Folder folder = store.getFolder("inbox");
        SearchTerm n =new SearchTerm() {
            @Override
            public boolean match(Message message) {
                try {

              EmailMessage mm = getMessage(message);
              return mm.getFrom().contains(q) || mm.getContent().contains(q);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        };
       Message[] messages = folder.search(n);
        for (Message messages1:messages) {
            m.add(getMessage(messages1));
        }
        return m;
    }

    public void compose(String to , String subject, String header , List<EmailAttachment> emailAttachments) throws MessagingException {
        Properties o = new Properties();
        o.put("mail.store.protocol","imaps");
        o.put("mail.imaps.host","imap.gmail.com");
        o.put("mail.imaps.port","993");
        o.put("mail.imaps.starttls.enable","true");
        o.put("mail.imaps.auth","true");
        Session session = Session.getInstance(o);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(username);
        message.setRecipients(Message.RecipientType.TO,to);
        message.setSubject(subject);
        MimeMultipart p = new MimeMultipart();
        for (int i = 0; i < emailAttachments.size() ; i++) {
            p.addBodyPart(emailAttachments.get(i).);
        }

    }
}