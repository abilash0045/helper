package com.zoro.email.integration.imap.email.integration.config;

import com.zoro.email.integration.imap.email.integration.listener.EmailListener;
import com.zoro.email.integration.imap.email.integration.service.EmailConnectorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.*;
import java.util.Properties;


@Configuration
public class EmailConfig {

    private String emailHost = "imap.gmail.com";

    private String emailPort = "993";

    private String emailUsername = "abilashsl2001@gmail.com";

    private String emailPassword = "lryr pcqp gikm btrk";

    EmailConnectorService emailConnectorService;

    @Bean
    public Session mailSession() {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.host", emailHost);
        props.setProperty("mail.imaps.port", emailPort);

        // Create a new session with the properties
        Session session = Session.getInstance(props);
        session.setDebug(true); // Enable debug mode for troubleshooting

        return session;
    }

    @Bean
    public EmailListener emailListener() {
        return new EmailListener(mailSession(), emailUsername, emailPassword,emailConnectorService);
    }
}
