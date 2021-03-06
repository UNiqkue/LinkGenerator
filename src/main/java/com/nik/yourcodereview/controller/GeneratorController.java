/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.3.1).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.nik.yourcodereview.controller;

import com.nik.yourcodereview.exception.ValidationException;
import com.nik.yourcodereview.mapper.LinkMapper;
import com.nik.yourcodereview.model.dto.LinkPost;
import com.nik.yourcodereview.model.dto.ShortLink;
import com.nik.yourcodereview.service.LinkGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
public class GeneratorController {

    private final LinkGeneratorService linkGeneratorService;

    private final LinkMapper linkMapper;

    private static final String URL_REGEX = "^https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)$";

    /**
     * POST /generate : Generate new short link by given link
     *
     * @param linkPost - содержит оригининальную ссылку
     * @return {@link ShortLink} ok (status code 200)
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/generate",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<ShortLink> postGenerate(@Valid @RequestBody LinkPost linkPost) {
        if (!linkPost.getOriginal().matches(URL_REGEX)) {
            throw new ValidationException("У ссылки неверный формат http url", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(linkMapper.mapToShortLinkDTO(linkGeneratorService
                .createShortLinkIfNotExists(linkMapper.mapToLinkBO(linkPost))));
    }

}
