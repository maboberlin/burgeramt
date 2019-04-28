package de.bitsandbooks.examples.util;

import de.bitsandbooks.examples.core.PageUpdateChecker;
import de.bitsandbooks.examples.data.PageData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttachmentCreator {

    private static final int TIMEOUT_MILLIS = 60 * 1000;

    @Value("${updatechecker.url}")
    private String baseLink;

    @NonNull
    private PageUpdateChecker pageUpdateChecker;

    public Map<String, BodyPart> getAttachmentData() {
        final Map<String, BodyPart> result = new HashMap<>();

        final PageData pageData = pageUpdateChecker.getOldPageData();
        final Elements dayElements = pageData.getDayElements();
        final Elements links = dayElements.select("a[href]");

        for (Element link: links) {
            createBodyPart(result, link);
        }

        return result;
    }

    private void createBodyPart(Map<String, BodyPart> result, Element link) {
        try {
            final BodyPart bodyPart = new MimeBodyPart();
            final String linkUrl = link.attr("href");

            final URL url = new URL(new URL(baseLink), linkUrl);
            final String html = Jsoup.parse(url, TIMEOUT_MILLIS).html();
            final ByteArrayDataSource dataSource = new ByteArrayDataSource(html, "application/octet-stream");
            final DataHandler dataHandler = new DataHandler(dataSource);
            bodyPart.setDataHandler(dataHandler);

            result.put(url.toString(), bodyPart);
        } catch (Exception exception) {
            log.error("Error extracting attachment for link {}", link);
        }
    }

}
