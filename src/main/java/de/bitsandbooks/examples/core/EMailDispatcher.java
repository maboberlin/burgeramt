package de.bitsandbooks.examples.core;

import de.bitsandbooks.examples.util.AttachmentCreator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EMailDispatcher {

    @Value("${updatechecker.sender}")
    private String user;

    @Value("${updatechecker.url}")
    private String link;

    @Value("#{'${updatechecker.recipients}'.split(';')}")
    private List<String> recipients;

    @NonNull
    private Session session;

    @NonNull
    private AttachmentCreator attachmentCreator;

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void dispatchEMail() throws Exception {
        try {
            log.info("Sending message email ...");

            final String messageText = String.format("Hi Lecia,%nEs scheint jetzt Termine auf der Seite vom Amt zu geben!%n%s", link);

            final Message msg = new MimeMessage(session);
            msg.setContent(messageText, "text/plain");

            configureAndSendMessage(msg);
        } catch (Exception exception) {
            log.error("Error dispatching emails after page change", exception);
            throw exception;
        }
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void dispatchAttachments() throws Exception {
        try {
            log.info("Sending attachment email ...");

            final Message msg = new MimeMessage(session);

            final MimeMultipart content = new MimeMultipart();

            final Map<String, BodyPart> data = attachmentCreator.getAttachmentData();

            final String textString = data.keySet().stream().collect(Collectors.joining("\n"));
            final MimeBodyPart text = new MimeBodyPart();
            text.setText(textString);
            content.addBodyPart(text);

            for (BodyPart bodyPart: data.values()) {
                content.addBodyPart(bodyPart);
            }

            msg.setContent(content);

            configureAndSendMessage(msg);
        } catch (Exception exception) {
            log.error("Error dispatching attachments after page change", exception);
            throw exception;
        }
    }

    private void configureAndSendMessage(Message msg) throws MessagingException {
        final String messageSubject = "Termine available now!!!";

        final InternetAddress fromAddress = new InternetAddress(user);
        final InternetAddress[] toAddresses = getInternetAddresses();

        msg.setFrom(fromAddress);
        msg.setRecipients(RecipientType.TO, toAddresses);
        msg.setSubject(messageSubject);
        msg.setText("Hi Katy and Lisa! Something has changed in the calendar for appointments at Standesamt Pankow. Check this URL: " + link);

        Transport.send(msg);
    }

    private InternetAddress[] getInternetAddresses() {
        return recipients.stream()
                .map(address -> {
                    try {
                        return new InternetAddress(address);
                    } catch (AddressException e) {
                        log.error("Unable to create e-mail address for address {}", address, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(InternetAddress[]::new);
    }

}
