package com.zoro.email.integration.config;

import com.zoro.email.integration.service.MailReceiverService;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.Message;

import java.util.Properties;

@Configuration
@EnableIntegration
@Slf4j
public class EmailIntegrationConfig {

    private final MailReceiverService receiveMailService;

    @Value("${mail.imap.username}")
    private String username;

    @Value("${mail.imap.password}")
    private String password;

    public EmailIntegrationConfig(MailReceiverService receiveMailService) {
        this.receiveMailService = receiveMailService;
    }

    @ServiceActivator(inputChannel = "receiveEmailChannel")
    public void receive(Message message) {
        receiveMailService.processEmail(message);
        log.info("Messages received " + message.toString());
    }

    @Bean("receiveEmailChannel")
    public DirectChannel defaultChannel() {
        return new DirectChannel();
    }

    @Bean()
    @InboundChannelAdapter(
            channel = "receiveEmailChannel",
            poller = @Poller(fixedDelay = "5000", taskExecutor = "asyncTaskExecutor")
    )
    public MailReceivingMessageSource mailMessageSource(MailReceiver mailReceiver) {
        return new MailReceivingMessageSource(mailReceiver);
    }

    @Bean
    public MailReceiver imapMailReceiver() {

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.put("mail.imap.socketFactory.fallback", false);
        javaMailProperties.put("mail.store.protocol", "imaps");
        javaMailProperties.put("mail.debug", true);

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        Session session = Session.getInstance(javaMailProperties,auth);

        ImapMailReceiver imapMailReceiver = new ImapMailReceiver("imaps://imap.gmail.com:993/inbox");
        imapMailReceiver.setShouldMarkMessagesAsRead(true);
        imapMailReceiver.setShouldDeleteMessages(false);
        imapMailReceiver.setMaxFetchSize(10);
        imapMailReceiver.setAutoCloseFolder(false);

        imapMailReceiver.setJavaMailProperties(javaMailProperties);
        imapMailReceiver.setSession(session);

        return imapMailReceiver;
    }
}
