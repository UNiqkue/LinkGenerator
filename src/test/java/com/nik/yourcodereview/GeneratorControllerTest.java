package com.nik.yourcodereview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nik.yourcodereview.dao.LinkRepository;
import com.nik.yourcodereview.model.dto.LinkPost;
import com.nik.yourcodereview.model.dto.ShortLink;
import com.nik.yourcodereview.model.entity.LinkEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.nik.yourcodereview.builder.TestUtils.buildHttpHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
public class GeneratorControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LinkRepository linkRepository;

    @AfterEach
    public void clearDB() {
        linkRepository.deleteAll();
    }

    @Test
    @DisplayName("Проверка генерирования и сохранения короткой ссылки")
    public void generatePostTest() throws Exception {
        LinkPost linkPost = new LinkPost()
                .original("https://some-server.com/some/url?some_param=1");

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post("/generate")
                        .content(objectMapper.writeValueAsString(linkPost)).headers(buildHttpHeaders()))
                .andExpect(status().is(200))
                .andReturn().getResponse();

        ShortLink shortLink = objectMapper.readValue(response.getContentAsString(), ShortLink.class);
        Assertions.assertEquals("VxnhdbulA", shortLink.getLink());

        List<LinkEntity> linkEntityList = linkRepository.findAll();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, linkEntityList.size()),
                () -> Assertions.assertEquals(linkPost.getOriginal(), linkEntityList.get(0).getOriginalLink()),
                () -> Assertions.assertEquals(shortLink.getLink(), linkEntityList.get(0).getShortLink()),
                () -> Assertions.assertEquals(0L, linkEntityList.get(0).getVisitsCount()),
                () -> Assertions.assertNotNull(linkEntityList.get(0).getCreateAt())
        );

        //проверка повторной генерации того же урла - должен отработать кеш
        MockHttpServletResponse response2 = mvc.perform(MockMvcRequestBuilders.post("/generate")
                        .content(objectMapper.writeValueAsString(linkPost)).headers(buildHttpHeaders()))
                .andExpect(status().is(200))
                .andReturn().getResponse();

        ShortLink shortLink2 = objectMapper.readValue(response2.getContentAsString(), ShortLink.class);
        Assertions.assertEquals("VxnhdbulA", shortLink2.getLink());
        List<LinkEntity> linkEntityList2 = linkRepository.findAll();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, linkEntityList2.size()),
                () -> Assertions.assertEquals(linkPost.getOriginal(), linkEntityList2.get(0).getOriginalLink()),
                () -> Assertions.assertEquals(shortLink.getLink(), linkEntityList2.get(0).getShortLink()),
                () -> Assertions.assertEquals(0L, linkEntityList2.get(0).getVisitsCount()),
                () -> Assertions.assertNotNull(linkEntityList2.get(0).getCreateAt())
        );
    }

    @Test
    @DisplayName("Проверка передачи null or empty значения для генерирования")
    public void generatePostIfNullOrEmptyValueNegativeTest() throws Exception {
        MockHttpServletResponse responseForNull = mvc.perform(MockMvcRequestBuilders.post("/generate")
                        .content(objectMapper.writeValueAsString(
                                new LinkPost()
                                        .original(null)))
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(400))
                .andReturn().getResponse();

        Error errorForNull = objectMapper.readValue(responseForNull.getContentAsString(), Error.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals("Not valid request parameter: original - must not be blank", errorForNull.getMessage()),
                () -> Assertions.assertTrue(linkRepository.findAll().isEmpty())
        );

        //проверка для empty
        MockHttpServletResponse responseForEmpty = mvc.perform(MockMvcRequestBuilders.post("/generate")
                        .content(objectMapper.writeValueAsString(
                                new LinkPost()
                                        .original("")))
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(400))
                .andReturn().getResponse();

        Error error = objectMapper.readValue(responseForEmpty.getContentAsString(), Error.class);
        Assertions.assertAll(
                () -> Assertions.assertTrue(error.getMessage().contains("Not valid request parameter:")),
                () -> Assertions.assertTrue(error.getMessage().contains("original - must not be blank")),
                () -> Assertions.assertTrue(error.getMessage().contains("original - size must be between 1 and 2048")),
                () -> Assertions.assertTrue(linkRepository.findAll().isEmpty())
        );
    }

    @Test
    @DisplayName("Проверка передачи невалидного значения для генерирования")
    public void generatePostIfInvalidValueNegativeTest() throws Exception {
        LinkPost linkPost = new LinkPost()
                .original("htt/null.");
        MockHttpServletResponse responseForNull = mvc.perform(MockMvcRequestBuilders.post("/generate")
                        .content(objectMapper.writeValueAsString(linkPost))
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn().getResponse();

        Error errorForNull = objectMapper.readValue(responseForNull.getContentAsString(), Error.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals("У ссылки неверный формат http url", errorForNull.getMessage()),
                () -> Assertions.assertTrue(linkRepository.findAll().isEmpty())
        );
    }

    @Test
    @DisplayName("Проверка передачи очень большого урла для генерирования")
    public void generatePostIfVeryLargeValueNegativeTest() throws Exception {
        LinkPost linkPost = new LinkPost()
                .original("https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1%80%D0%BB+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BE%D0%B9+%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B5+2048+%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D0%BE%D0%B2&lr=157https://yandex.by/search/?text=%D1%83%D1");
        MockHttpServletResponse responseForNull = mvc.perform(MockMvcRequestBuilders.post("/generate")
                        .content(objectMapper.writeValueAsString(linkPost))
                        .headers(buildHttpHeaders()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn().getResponse();

        Error errorForNull = objectMapper.readValue(responseForNull.getContentAsString(), Error.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals("Not valid request parameter: original - size must be between 1 and 2048", errorForNull.getMessage()),
                () -> Assertions.assertTrue(linkRepository.findAll().isEmpty())
        );
    }

}
