package com.zoro.email.integration.imap.email.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class EmailConnectorService {

    public String processMessage(Message message) throws MessagingException, IOException {

        log.info(message.getSubject().toString());
        log.info(message.getContent().toString());

        Map<String, List<String>> data = extractData(message);

        for (String dataType : data.keySet()){
            data.get(dataType).forEach(extractedData -> {
                log.info(extractedData);
            });
        }
        return "email processed";
    }

    private Map<String, List<String>> extractData(Message message) {
        try {
            String content = message.getContent().toString();
            log.info(content);
            Pattern pattern = Pattern.compile("(https?://\\S+)");
            Matcher matcher = pattern.matcher(content);

            List<String> urls = new ArrayList<>();
            while (matcher.find()) {
                urls.add(matcher.group(1));
                System.out.println(matcher.group(1));
            }

            Map<String, List<String>> data = new HashMap<>();
            data.put("dataUrls", urls);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
