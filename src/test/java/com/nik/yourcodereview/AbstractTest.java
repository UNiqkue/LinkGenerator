package com.nik.yourcodereview;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = SpringLinkGeneratorApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractTest {
}
