package com.nik.yourcodereview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nik.yourcodereview.dao.LinkRepository;
import com.nik.yourcodereview.model.dto.Error;
import com.nik.yourcodereview.model.dto.Link;
import com.nik.yourcodereview.model.entity.LinkEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.nik.yourcodereview.builder.TestUtils.buildHttpHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatisticsControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LinkRepository linkRepository;

    private final String shortLink = "VxnhdbulA";

    private final LinkEntity linkEntity = new LinkEntity()
            .setShortLink(shortLink)
            .setOriginalLink("https://some-server.com/some/url?some_param=1")
            .setVisitsCount(10L)
            .setCreateAt(LocalDateTime.now());

    @BeforeEach
    public void fillDB() {
        linkRepository.save(linkEntity);
    }

    @AfterEach
    public void clearDB() {
        linkRepository.deleteAll();
    }

    @Test
    @Sql("/sql/insert_links.sql")
    @DisplayName("Get statistic about this link by short link")
    public void getStatisticByLinkTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/stats/" + shortLink)
                        .headers(buildHttpHeaders()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Link link = objectMapper.readValue(response.getContentAsString(), Link.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(linkEntity.getOriginalLink(), link.getOriginal()),
                () -> Assertions.assertEquals(linkEntity.getShortLink(), link.getLink()),
                () -> Assertions.assertEquals(linkEntity.getVisitsCount(), link.getCount()),
                () -> Assertions.assertEquals(2L, link.getRank())
        );
    }

    @Test
    @DisplayName("Get statistic about this link by short link if not exists")
    public void getStatisticByLinkNotFoundTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/stats/" + "someInvalidHash")
                        .headers(buildHttpHeaders()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();

        Error error = objectMapper.readValue(response.getContentAsString(), Error.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals("This short link not found",
                        error.getMessage())
        );
    }

    @Test
    @Sql("/sql/insert_links.sql")
    @DisplayName("Get statistic about this link by short link for null path parameter - invoking default /stats?page=1 and count=2")
    public void getStatisticByLinkForNullValueTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/stats/")
                        .headers(buildHttpHeaders()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        List<Link> links = objectMapper.readValue(response.getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, Link.class));

        Assertions.assertAll(
                () -> Assertions.assertEquals("https://github.com/qcha/JBook/blob/master/other/garbage_collector.md",
                        links.get(0).getOriginal()),
                () -> Assertions.assertEquals("LNntW6HKp", links.get(0).getLink()),
                () -> Assertions.assertEquals(11, links.get(0).getCount()),
                () -> Assertions.assertEquals(1, links.get(0).getRank()),

                () -> Assertions.assertEquals(linkEntity.getOriginalLink(), links.get(1).getOriginal()),
                () -> Assertions.assertEquals(linkEntity.getShortLink(), links.get(1).getLink()),
                () -> Assertions.assertEquals(linkEntity.getVisitsCount(), links.get(1).getCount()),
                () -> Assertions.assertEquals(2, links.get(1).getRank())
        );
    }

    @Test
    @Sql("/sql/insert_links.sql")
    @DisplayName("Get statistic about links")
    public void getStatisticsTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(UriComponentsBuilder.fromPath("/stats")
                                .queryParam("page", 1)
                                .queryParam("count", 4).toUriString())
                        .headers(buildHttpHeaders()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        List<Link> links = objectMapper.readValue(response.getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, Link.class));

        Assertions.assertAll(
                () -> Assertions.assertEquals(4, links.size()),

                () -> Assertions.assertEquals("https://github.com/qcha/JBook/blob/master/other/garbage_collector.md",
                        links.get(0).getOriginal()),
                () -> Assertions.assertEquals("LNntW6HKp", links.get(0).getLink()),
                () -> Assertions.assertEquals(11, links.get(0).getCount()),
                () -> Assertions.assertEquals(1, links.get(0).getRank()),

                () -> Assertions.assertEquals(linkEntity.getOriginalLink(), links.get(1).getOriginal()),
                () -> Assertions.assertEquals(linkEntity.getShortLink(), links.get(1).getLink()),
                () -> Assertions.assertEquals(linkEntity.getVisitsCount(), links.get(1).getCount()),
                () -> Assertions.assertEquals(2, links.get(1).getRank()),

                () -> Assertions.assertEquals("https://stackoverflow.com/questions/38711871/load-different-application-yml-in-springboot-test",
                        links.get(2).getOriginal()),
                () -> Assertions.assertEquals("5jDuNwTVe", links.get(2).getLink()),
                () -> Assertions.assertEquals(8, links.get(2).getCount()),
                () -> Assertions.assertEquals(3, links.get(2).getRank()),

                () -> Assertions.assertEquals("https://ru.wikipedia.org/wiki/SOLID_(%D0%BE%D0%B1%D1%8A%D0%B5%D0%BA%D1%82%D0%BD%D0%BE-%D0%BE%D1%80%D0%B8%D0%B5%D0%BD%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%BD%D0%BE%D0%B5_%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5)",
                        links.get(3).getOriginal()),
                () -> Assertions.assertEquals("vJXU2MIK1", links.get(3).getLink()),
                () -> Assertions.assertEquals(4, links.get(3).getCount()),
                () -> Assertions.assertEquals(4, links.get(3).getRank())
        );
    }

    @Test
    @DisplayName("Get statistic about links if parameters less or more than need")
    public void getStatisticsInvalidParametersTest() throws Exception {
        String uri = UriComponentsBuilder.fromUriString("/stats")
                .queryParam("page", 0)
                .queryParam("count", 102).toUriString();
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .headers(buildHttpHeaders()))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        Error error = objectMapper.readValue(response.getContentAsString(), Error.class);
        Assertions.assertAll(
                () -> Assertions.assertTrue(
                        error.getMessage().contains("getStats.page: должно быть не меньше 1")),
                () -> Assertions.assertTrue(
                        error.getMessage().contains("getStats.count: должно быть не больше 100"))
        );
    }

}
