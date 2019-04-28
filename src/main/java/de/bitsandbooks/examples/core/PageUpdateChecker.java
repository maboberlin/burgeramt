package de.bitsandbooks.examples.core;

import de.bitsandbooks.examples.data.PageData;
import de.bitsandbooks.examples.util.HashUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class PageUpdateChecker {

    private static final int TIMEOUT_MILLIS = 60 * 1000;

    @Getter
    private PageData oldPageData;

    @Autowired
    private PageComparator pageComparator;

    public boolean hasPageUpdated(String urlString, String selector) {
        log.info("Checking page update ...");

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet httpget = new HttpGet(urlString);

            try (CloseableHttpResponse response = httpclient.execute(httpget)) {
                final String html = IOUtils.toString(response.getEntity().getContent());

                final Document doc = Jsoup.parse(html);
                final Elements elements = doc.select(selector);
                if (elements.size() > 1) {
                    final Map<String, List<String>> dayWithAttributeMap = getDayWithAttributeMap(elements);

                    final long fullHtmlHashcode = HashUtil.getHashCode(html);

                    final PageData newPageData = new PageData(new Date(), html, fullHtmlHashcode, elements, dayWithAttributeMap);

                    boolean equals = pageComparator.pagesEqual(oldPageData, newPageData);

                    oldPageData = newPageData;

                    return !equals;
                } else {
                    throw new Exception("Error reading html. Table elements size unequal 1");
                }
            }
        } catch (HttpStatusException exception) {
            log.error("Error checking page update. Returning update is false. HttpStatus: {}", exception.getStatusCode(), exception);
            return false;
        } catch (Exception exception) {
            log.error("Error checking page update. Returning update is false", exception);
            return false;
        }

    }

    private Map<String, List<String>> getDayWithAttributeMap(Elements elements) {
        final List<Element> dayList = elements
                .stream()
                .filter(el -> StringUtils.isEmpty(el.attr("colspan")))
                .filter(el -> !el.attr("class").contains("heutemarkierung"))
                .filter(el -> StringUtils.isNotEmpty(el.attr("class")))
                .collect(Collectors.toList());
        final Map<String, List<String>> dayWithAttributeMap = new HashMap<>();
        for (Element element: dayList) {
            final String day = element.text();
            if (dayWithAttributeMap.containsKey(day)) {
                dayWithAttributeMap.get(day).add(element.attr("class"));
            } else {
                dayWithAttributeMap.put(day, Stream.of(element.attr("class")).collect(Collectors.toList()));
            }
        }
        return dayWithAttributeMap;
    }

}
