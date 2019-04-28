package de.bitsandbooks.examples.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
@EnableScheduling
@EnableAsync
@EnableRetry
public class PageUpdateCheckerConfig {

    private static final int POOL_SIZE = 1;

    private static final byte[] pwd = "QaYXsW135!,".getBytes();

    @Value("${updatechecker.sender}")
    private String user;

    @Bean
    public Session mailSession() {
        final Properties properties = new Properties();

        properties.setProperty("mail.smtp.host", "smtp.googlemail.com");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true") ;
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");

        return Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, new String(pwd, StandardCharsets.UTF_8));
            }
        });
    }

    @Bean
    public TaskScheduler pageUpdateCheckerScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(POOL_SIZE);
        scheduler.setThreadNamePrefix("PageUpdateCheckerSchedulerThread");
        return scheduler;
    }

}
