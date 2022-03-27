package com.nik.yourcodereview.controller;

import com.nik.yourcodereview.exception.LinkException;
import com.nik.yourcodereview.service.LinkGeneratorService;
import com.nik.yourcodereview.service.LinkService;
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

import static com.nik.yourcodereview.utils.UrlUtils.L_PATH;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class RedirectController {
    private final LinkGeneratorService linkGeneratorService;

    private final LinkService linkService;

    /**
     * GET /l/{link} : Redirection by new short link
     *
     * @param shortLink - short generated link (required)
     * @return redirection (status code 302)
     * @throws LinkException - если запись не найдена
     */
    @GetMapping(
            value = L_PATH + "{link}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public RedirectView getRedirect(@Valid @NotBlank @PathVariable("link") String shortLink) {
        String originalLink = linkGeneratorService.getOriginalLinkByShort(shortLink);
        linkService.updateLinkVisitsCount(shortLink);
        log.info("Response: Redirection to {}", originalLink);
        return new RedirectView(originalLink);
    }

}
