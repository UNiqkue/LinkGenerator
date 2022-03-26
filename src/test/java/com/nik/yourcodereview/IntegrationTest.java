package com.nik.yourcodereview;

import com.nik.yourcodereview.model.dto.Link;
import com.nik.yourcodereview.model.dto.LinkPost;
import com.nik.yourcodereview.model.dto.ShortLink;
import com.nik.yourcodereview.model.entity.LinkEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.nik.yourcodereview.builder.TestUtils.buildHttpHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IntegrationTest extends AbstractTest {

    @Test
    @Sql(value = "/sql/insert_links.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Тест на взаимодействие запросов друг с другом")
    public void runAllRequestsTest() throws Exception {
        // POST /generate
        LinkPost linkPost = new LinkPost()
                .original("https://batterycare.net/en/guide.html#descTot");

        MockHttpServletResponse responseGenerate = mvc.perform(MockMvcRequestBuilders.post("/generate")
                        .content(objectMapper.writeValueAsString(linkPost)).headers(buildHttpHeaders()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        ShortLink shortLink = objectMapper.readValue(responseGenerate.getContentAsString(), ShortLink.class);
        Assertions.assertEquals("4lWInbfrj", shortLink.getLink());

        LinkEntity linkEntity = linkRepository.findById(shortLink.getLink()).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertEquals(linkPost.getOriginal(), linkEntity.getOriginalLink()),
                () -> Assertions.assertEquals(shortLink.getLink(), linkEntity.getShortLink()),
                () -> Assertions.assertEquals(0L, linkEntity.getVisitsCount()),
                () -> Assertions.assertNotNull(linkEntity.getCreateAt())
        );

        // GET /l - redirect
        MockHttpServletResponse responseRedirect = mvc.perform(MockMvcRequestBuilders.get("/l/" + shortLink.getLink())
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(302))
                .andReturn().getResponse();

        LinkEntity linkEntityActualRedirect = linkRepository.findById(shortLink.getLink()).orElseThrow();
        Assertions.assertAll(
                "Проверка, что содержит нужный урл для редиректа и данные в Entity поменялись только для счётчика",
                () -> Assertions.assertEquals(linkEntity.getOriginalLink(), responseRedirect.getRedirectedUrl()),

                () -> Assertions.assertEquals(linkEntity.getOriginalLink(), linkEntityActualRedirect.getOriginalLink()),
                () -> Assertions.assertEquals(linkEntity.getShortLink(), linkEntityActualRedirect.getShortLink()),
                () -> Assertions.assertEquals(linkEntity.getVisitsCount() + 1, linkEntityActualRedirect.getVisitsCount()),
                () -> Assertions.assertNotNull(linkEntityActualRedirect.getCreateAt())
        );

        //GET /stats/:link
        MockHttpServletResponse responseStatsByLink = mvc.perform(MockMvcRequestBuilders.get("/stats/" + shortLink.getLink())
                        .headers(buildHttpHeaders()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Link link = objectMapper.readValue(responseStatsByLink.getContentAsString(), Link.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(linkEntity.getOriginalLink(), link.getOriginal()),
                () -> Assertions.assertEquals(linkEntity.getShortLink(), link.getLink()),
                () -> Assertions.assertEquals(linkEntityActualRedirect.getVisitsCount(), link.getCount()),
                () -> Assertions.assertEquals(8L, link.getRank())
        );
    }
}
