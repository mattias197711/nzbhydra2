package org.nzbhydra.indexers;

import lombok.Getter;
import lombok.Setter;
import org.nzbhydra.mapping.newznab.NewznabAttribute;
import org.nzbhydra.mapping.newznab.RssItem;
import org.nzbhydra.searching.SearchResultItem;
import org.nzbhydra.searching.SearchResultItem.DownloadType;
import org.nzbhydra.searching.SearchResultItem.HasNfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Torznab extends Newznab {

    private static final Logger logger = LoggerFactory.getLogger(Torznab.class);

    protected SearchResultItem createSearchResultItem(RssItem item) {
        item.getRssGuid().setPermaLink(true); //Not set in RSS but actually always true
        SearchResultItem searchResultItem = super.createSearchResultItem(item);

        //TODO: Category is in main category tag, not in attributes

        searchResultItem.setGrabs(item.getGrabs());
        for (NewznabAttribute attribute : item.getTorznabAttributes()) {
            searchResultItem.getAttributes().put(attribute.getName(), attribute.getValue());
            if (attribute.getName().equals("grabs")) {
                searchResultItem.setGrabs(Integer.valueOf(attribute.getValue()));
            } else if (attribute.getName().equals("guid")) {
                searchResultItem.setIndexerGuid(attribute.getValue());
            }
        }
        searchResultItem.setHasNfo(HasNfo.NO);
        searchResultItem.setDownloadType(DownloadType.TORRENT);

        return searchResultItem;
    }

    protected Logger getLogger() {
        return logger;
    }


}
