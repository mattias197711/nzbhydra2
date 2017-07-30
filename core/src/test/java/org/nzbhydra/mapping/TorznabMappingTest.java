package org.nzbhydra.mapping;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nzbhydra.mapping.newznab.Enclosure;
import org.nzbhydra.mapping.newznab.NewznabAttribute;
import org.nzbhydra.mapping.newznab.RssChannel;
import org.nzbhydra.mapping.newznab.RssGuid;
import org.nzbhydra.mapping.newznab.RssItem;
import org.nzbhydra.mapping.newznab.RssRoot;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
public class TorznabMappingTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMappingFromXml() throws Exception {
        RssRoot rssRoot = getRssRootFromXml("cardigann.xml");
        RssChannel channel = rssRoot.getRssChannel();
        assertThat(channel.getTitle(), is("some-torrents"));
        assertThat(channel.getLink(), is("https://some-torrents.com/"));
        assertThat(channel.getLanguage(), is("en-us"));


        List<RssItem> items = channel.getItems();
        assertThat(items.size(), is(2));

        RssItem item = items.get(0);
        assertThat(item.getLink(), is("http://127.0.0.1:5060/download/111.torrent"));
        assertThat(item.getPubDate(), is(Instant.ofEpochSecond(1493900064)));
        assertThat(item.getComments(), is("https://some-torrents.com/details.php?id=111&page=0#startcomments"));

        RssGuid rssGuid = item.getRssGuid();
        assertThat(rssGuid.getGuid(), is("https://some-torrents.com/details.php?id=111"));

        Enclosure enclosure = item.getEnclosure();
        assertThat(enclosure.getUrl(), is("http://127.0.0.1:5060/download/111.torrent"));

        List<NewznabAttribute> attributes = item.getTorznabAttributes();
        assertThat(attributes.size(), is(8));
        assertThat(attributes.get(1).getName(), is("seeders"));
        assertThat(attributes.get(1).getValue(), is("11"));
        assertThat(attributes.get(5).getName(), is("size"));
        assertThat(attributes.get(5).getValue(), is("620000000"));
    }


    private RssRoot getRssRootFromXml(String xmlFileName) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(requestTo("/api")).andRespond(withSuccess(Resources.toString(Resources.getResource(TorznabMappingTest.class, xmlFileName), Charsets.UTF_8), MediaType.APPLICATION_XML));

        return restTemplate.getForObject("/api", RssRoot.class);
    }


}