package com.zoro.email.integration.service;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.imap.IMAPMessage;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Slf4j
public class MailReceiverService{

    public void processEmail(Message message) {

        Object payload = message.getPayload();

        if (payload instanceof IMAPMessage) {
            IMAPMessage imapMessage = (IMAPMessage) payload;

            Address[] senders;
            try {
                senders = imapMessage.getFrom();
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            for (Address sender : senders) {
                log.info("Sender: " + sender.toString());
            }

            try {
                Object content = imapMessage.getContent();
                if (content instanceof String) {
                    String body = (String) content;
                    log.info("Body : {}",body);
                }
                log.info("Subject: {}" , imapMessage.getSubject());

            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
