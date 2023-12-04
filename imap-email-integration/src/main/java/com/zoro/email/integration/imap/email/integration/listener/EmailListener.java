package com.zoro.email.integration.imap.email.integration.listener;

import com.sun.mail.imap.IMAPFolder;
import com.zoro.email.integration.imap.email.integration.service.EmailConnectorService;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.io.IOException;

public class EmailListener extends MessageCountAdapter {
    private Session session;
    private String username;
    private String password;
    private EmailConnectorService emailConnectorService;

    public EmailListener(Session session, String username, String password,EmailConnectorService emailConnectorService) {
        this.session = session;
        this.username = username;
        this.password = password;
        this.emailConnectorService = emailConnectorService;
    }

    public void startListening() throws MessagingException, InterruptedException, IOException {
        Store store = session.getStore("imaps");
        store.connect(username, password);



        IMAPFolder inbox = (IMAPFolder)store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        // Create a new thread to keep the connection alive
        Thread keepAliveThread = new Thread(new KeepAliveRunnable(inbox), "IdleConnectionKeepAlive");
        keepAliveThread.start();

        inbox.addMessageCountListener(new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent event) {
                Message[] messages = event.getMessages();
                for (Message message : messages) {
                    try {
                        System.out.println("New email received: " + message.getSubject());
                        String response = emailConnectorService.processMessage(message);
                        System.out.println(response);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        while (!Thread.interrupted()) {
            try {
                System.out.println("Starting IDLE");
                inbox.idle();
            } catch (MessagingException e) {
                System.out.println("Messaging exception during IDLE");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        if (keepAliveThread.isAlive()) {
            keepAliveThread.interrupt();
        }
    }
}
