package com.nik.yourcodereview.service.impl;

import com.nik.yourcodereview.exception.LinkException;
import com.nik.yourcodereview.model.bo.LinkBO;
import com.nik.yourcodereview.service.LinkGeneratorService;
import com.nik.yourcodereview.service.LinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheableLinkGeneratorService implements LinkGeneratorService {

    private final LinkService linkService;

    private final Function<String, Hashids> hashGenerator;

    private static final Long ZERO_VISITS_COUNT = 0L;

    @Value("${link.generator.hash.keys}")
    private long[] keys;

    @Override
    @Cacheable(value = "generate_links", key = "#linkBO.originalLink", cacheManager = "generateLinks")
    public LinkBO createShortLinkIfNotExists(LinkBO linkBO) {
        linkBO.setShortLink(hashGenerator.apply(linkBO.getOriginalLink()).encode(keys));
        return linkService.getLinkByShort(linkBO.getShortLink())
                .orElseGet(() -> {
                    linkBO.setVisitsCount(ZERO_VISITS_COUNT)
                            .setCreateAt(LocalDateTime.now());
                    return linkService.addLink(linkBO);
                });
    }

    @Override
    @Cacheable(value = "links", key = "#shortLink", cacheManager = "links")
    public String getOriginalLinkByShort(String shortLink) {
        return linkService.getLinkByShort(shortLink)
                .map(linkBO -> {
                    linkService.updateLinkVisitsCount(shortLink);
                    return linkBO.getOriginalLink();
                })
                .orElseThrow(() -> new LinkException("This short link not found", HttpStatus.NOT_FOUND));
    }
}
