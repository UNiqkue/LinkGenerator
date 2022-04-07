package com.nik.yourcodereview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nik.yourcodereview.dao.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = SpringLinkGeneratorApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractTest {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected LinkRepository linkRepository;
}
