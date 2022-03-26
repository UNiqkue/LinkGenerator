package com.nik.yourcodereview;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = SpringLinkGeneratorApplication.class)
@AutoConfigureMockMvc
public abstract class AbstractTest {
}
