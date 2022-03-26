package com.nik.yourcodereview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nik.yourcodereview.dao.LinkRepository;
import com.nik.yourcodereview.model.dto.Error;
import com.nik.yourcodereview.model.entity.LinkEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static com.nik.yourcodereview.builder.TestUtils.buildHttpHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
public class RedirectControllerTest extends AbstractTest {

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
            .setVisitsCount(1L)
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
    @DisplayName("Redirection by new short link")
    public void redirectTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/l/" + shortLink)
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(302))
                .andReturn().getResponse();

        LinkEntity linkEntityActual = linkRepository.findById(shortLink).orElseThrow();
        Assertions.assertAll(
                "Проверка, что содержит нужный урл для редиректа и данные в Entity поменялись только для счётчика",
                () -> Assertions.assertEquals(linkEntity.getOriginalLink(), response.getRedirectedUrl()),

                () -> Assertions.assertEquals(linkEntity.getOriginalLink(), linkEntityActual.getOriginalLink()),
                () -> Assertions.assertEquals(linkEntity.getShortLink(), linkEntityActual.getShortLink()),
                () -> Assertions.assertEquals(linkEntity.getVisitsCount() + 1, linkEntityActual.getVisitsCount()),
                () -> Assertions.assertNotNull(linkEntityActual.getCreateAt())
        );
    }

    @Test
    @DisplayName("Redirection by new short link if this link does not exist")
    public void redirectWhenShortLinkNotFoundTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/l/" + "someInvalidHash")
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(404))
                .andReturn().getResponse();

        Error error = objectMapper.readValue(response.getContentAsString(), Error.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals("This short link not found",
                        error.getMessage())
        );
    }

    @Test
    @DisplayName("Redirection by new short link for null path parameter")
    public void redirectWhenShortLinkForNullValueTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/l")
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(404))
                .andReturn().getResponse();

        Assertions.assertAll(
                () -> Assertions.assertTrue(response.getContentAsString().isEmpty())
        );
    }

}

