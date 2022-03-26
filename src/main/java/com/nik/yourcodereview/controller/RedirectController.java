package com.nik.yourcodereview.controller;

import com.nik.yourcodereview.service.LinkGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class RedirectController {
    private final LinkGeneratorService linkGeneratorService;

    /**
     * GET /l/{link} : Redirection by new short link
     *
     * @param link short generated link (required)
     * @return redirection (status code 302)
     */
    @GetMapping(
            value = "/l/{link}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public RedirectView getRedirect(@Valid @NotBlank @PathVariable("link") String link) {
        String originalLink = linkGeneratorService.getOriginalLinkByShort(link);
        log.info("Response: Redirection to {}", originalLink);
        return new RedirectView(originalLink);
    }

}
