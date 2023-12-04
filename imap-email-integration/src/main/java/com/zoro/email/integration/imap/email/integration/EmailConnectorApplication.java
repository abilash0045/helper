package com.zoro.email.integration.imap.email.integration;

import com.zoro.email.integration.imap.email.integration.listener.EmailListener;
import com.zoro.email.integration.imap.email.integration.config.EmailConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@SpringBootApplication
public class EmailConnectorApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(EmailConnectorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(EmailConfig.class);
		EmailListener emailListener = context.getBean(EmailListener.class);
		emailListener.startListening();
	}
}
