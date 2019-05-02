package de.bitsandbooks.examples.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PageUpdateCheckerTask implements Runnable {

    @Value("${updatechecker.url}")
    private String pageToCheckUrl;

    @Value("${updatechecker.selector}")
    private String selector;

    @NonNull
    private final PageUpdateChecker pageUpdateChecker;

    @NonNull
    private final EMailDispatcher dispatcher;

    @Override
    public void run() {
        boolean hasChanged = pageUpdateChecker.hasPageUpdated(pageToCheckUrl, selector);
        if (hasChanged) {
            sendInformations();
        }
    }

    private void sendInformations() {
        try {
            dispatcher.dispatchEMail();
            // dispatcher.dispatchAttachments();
        } catch (Exception e) {
            log.error("Error sending emails after retry limit exceeded!", e);
        }
    }

    private void beep() {
        try {
            Runtime.getRuntime().exec("beep");
        } catch (IOException e) {
            log.error("Error creating beep", e);
        }
    }

}
