package com.nik.yourcodereview.service;

import com.nik.yourcodereview.model.bo.LinkBO;

import java.util.List;

public interface StatisticService {
    LinkBO getLinkInfoWithRankByShortLink(String link);

    List<LinkBO> getLinksByPageParameters(Integer page, Integer count);
}
