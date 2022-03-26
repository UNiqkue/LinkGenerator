package com.nik.yourcodereview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nik.yourcodereview.dao.LinkRepository;
import com.nik.yourcodereview.model.dto.Error;
import com.nik.yourcodereview.model.dto.Link;
import com.nik.yourcodereview.model.entity.LinkEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.nik.yourcodereview.builder.TestUtils.buildHttpHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
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
    @DisplayName("Get statistic about this link by short link")
    public void getStatisticByLinkTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/stats/" + shortLink)
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(200))
                .andReturn().getResponse();

        Link link = objectMapper.readValue(response.getContentAsString(), Link.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(linkEntity.getOriginalLink(), link.getOriginal()),
                () -> Assertions.assertEquals(linkEntity.getShortLink(), link.getLink()),
                () -> Assertions.assertEquals(linkEntity.getVisitsCount(), link.getCount()),
                () -> Assertions.assertEquals(1L, link.getRank())
        );
    }

    @Test
    @DisplayName("Get statistic about this link by short link if not exists")
    public void getStatisticByLinkNotFoundTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/stats/" + "someInvalidHash")
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(404))
                .andReturn().getResponse();

        Error error = objectMapper.readValue(response.getContentAsString(), Error.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals("This short link not found",
                        error.getMessage())
        );
    }

    @Disabled
    @Test
    @DisplayName("Get statistic about this link by short link for null path parameter")
    public void getStatisticByLinkForNullValueTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/stats/")
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(400))
                .andReturn().getResponse();

        Error error = objectMapper.readValue(response.getContentAsString(), Error.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals("This ",
                        error.getMessage())
        );
    }

    @Test
    @DisplayName("Get statistic about links")
    public void getStatisticsTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(UriComponentsBuilder.fromPath("/stats")
                                .queryParam("page", 1)
                                .queryParam("count", 2).toUriString())
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(200))
                .andReturn().getResponse();

        List<Link> links = objectMapper.readValue(response.getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, Link.class));

//        Assertions.assertAll(
//                () -> Assertions.assertEquals(linkEntity.getOriginalLink(), link.getOriginal()),
//                () -> Assertions.assertEquals(linkEntity.getShortLink(), link.getLink()),
//                () -> Assertions.assertEquals(linkEntity.getCountVisit(), link.getCount()),
//                () -> Assertions.assertNotNull(link.getRank())
//        );
    }

}
