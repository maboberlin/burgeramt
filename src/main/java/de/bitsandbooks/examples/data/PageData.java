package de.bitsandbooks.examples.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class PageData {

    private Date lastCheck;

    private String fullHtml;

    private long fullHtmlHashCode;

    private Elements dayElements;

    private Map<String, List<String>> dayWithAtributeMap;
}
