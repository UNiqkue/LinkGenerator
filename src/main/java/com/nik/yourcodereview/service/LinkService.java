package com.nik.yourcodereview.service;

import com.nik.yourcodereview.model.bo.LinkBO;

import javax.transaction.Transactional;
import java.util.Optional;

public interface LinkService {
    LinkBO addLink(LinkBO linkBO);

    Optional<LinkBO> getLinkByShort(String link);

    void updateLinkVisitsCount(String link);
}
