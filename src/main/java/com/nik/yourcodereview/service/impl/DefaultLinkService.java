package com.nik.yourcodereview.service.impl;

import com.nik.yourcodereview.dao.LinkRepository;
import com.nik.yourcodereview.exception.LinkException;
import com.nik.yourcodereview.mapper.LinkMapper;
import com.nik.yourcodereview.model.bo.LinkBO;
import com.nik.yourcodereview.service.LinkService;
import com.nik.yourcodereview.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultLinkService implements LinkService, StatisticService {
    private final LinkRepository linkRepository;

    private final LinkMapper linkMapper;

    private static final Long ONE_INCREMENT_VALUE = 1L;

    @Override
    public LinkBO addLink(LinkBO link) {
        return linkMapper.mapToLinkBO(linkRepository
                .save(linkMapper.mapToEntity(link)));
    }

    @Override
    public Optional<LinkBO> getLinkByShort(String shortLink) {
        return linkRepository.findById(shortLink)
                .map(linkMapper::mapToLinkBO);
    }

    @Override
    @Transactional
    public void updateLinkVisitsCount(String link) {
        linkRepository.incrementVisitsCount(link, ONE_INCREMENT_VALUE);
    }

    @Override
    public LinkBO getLinkInfoWithRankByShortLink(String link) {
        return linkRepository.findById(link)
                .map(linkEntity -> linkMapper.mapToLinkBO(linkEntity)
                        .setRank(linkRepository
                                .countByVisitsCountIsGreaterThanEqual(linkEntity.getVisitsCount())))
                .orElseThrow(() -> {
                    log.error("По link = " + link + " запись не найдена");
                    return new LinkException("This short link not found", HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public List<LinkBO> getLinksByPageParameters(Integer page, Integer count) {
        AtomicLong rankIncrementor = new AtomicLong(ONE_INCREMENT_VALUE);
        return linkRepository
                .findAll(PageRequest.of(page - 1, count, Sort.by("visitsCount").descending()))
                .stream()
                .map(linkEntity -> linkMapper.mapToLinkBO(linkEntity)
                        .setRank(rankIncrementor.getAndIncrement()))
                .collect(Collectors.toList());
    }
}
