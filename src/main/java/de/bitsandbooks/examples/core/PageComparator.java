package de.bitsandbooks.examples.core;

import de.bitsandbooks.examples.data.PageData;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class PageComparator {

    public boolean pagesEqual(final PageData page1, final PageData page2) {
        if (page1 == null || page2 == null) {
            return false; // for #1 deployment test
        }

        if (page1.getDayWithAtributeMap().size() != page2.getDayWithAtributeMap().size()) {
            return false;
        }

        for (final String key: page1.getDayWithAtributeMap().keySet()) {
            if (!page2.getDayWithAtributeMap().containsKey(key)) {
                return false;
            }

            if (!CollectionUtils.containsAll(page1.getDayWithAtributeMap().get(key), page2.getDayWithAtributeMap().get(key)) || !CollectionUtils.containsAll(page2.getDayWithAtributeMap().get(key), page1.getDayWithAtributeMap().get(key))) {
                return false;
            }
        }

        return true;
    }

}
