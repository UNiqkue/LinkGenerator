package com.nik.yourcodereview.service;

import com.nik.yourcodereview.model.bo.LinkBO;

public interface LinkGeneratorService {
    LinkBO createShortLinkIfNotExists(LinkBO linkBO);

    String getOriginalLinkByShort(String link);
}
