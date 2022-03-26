package com.nik.yourcodereview.controller;

import com.nik.yourcodereview.mapper.LinkMapper;
import com.nik.yourcodereview.model.dto.Link;
import com.nik.yourcodereview.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticService statisticService;

    private final LinkMapper linkMapper;

    /**
     * GET /stats : Get statistic about links
     *
     * @param page  номер страницы
     * @param count число записей, отображаемых на странице, максимальное возможное значение 100 (включительно)
     * @return ok (status code 200)
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/stats",
            produces = {"application/json"}
    )
    public ResponseEntity<List<Link>> getStats(@Valid @RequestParam(value = "page", required = false, defaultValue = "1") @Min(1) Integer page,
                                               @Valid @RequestParam(value = "count", required = false, defaultValue = "2")
                                               @Min(1) @Max(100) Integer count) {
        return ResponseEntity.ok(linkMapper.mapToLinkDTO(statisticService.getLinksByPageParameters(page, count)));
    }


    /**
     * GET /stats/{link} : Get statistic about this link
     *
     * @param link short generated link (required)
     * @return ok (status code 200)
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/stats/{link}",
            produces = {"application/json"}
    )
    public ResponseEntity<Link> getStatsByUrl(@NotNull @Valid @PathVariable("link") String link) {
        return ResponseEntity.ok(linkMapper.mapToLinkDTO(statisticService.getLinkInfoWithRankByShortLink(link)));
    }
}
