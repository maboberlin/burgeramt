package de.bitsandbooks.examples.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class SelfCheckTask {

    @Scheduled(fixedRate = 900000)
    public void checkTask() {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final String host = System.getenv("HOST");
            final HttpGet httpget = new HttpGet(String.format("%s/abc", host));
            httpclient.execute(httpget);
        } catch (IOException e) {
            log.error("Error performing self check service", e);
        }
    }

}
