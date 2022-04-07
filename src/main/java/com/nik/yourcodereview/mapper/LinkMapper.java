package com.nik.yourcodereview.mapper;

import com.nik.yourcodereview.model.bo.LinkBO;
import com.nik.yourcodereview.model.dto.Link;
import com.nik.yourcodereview.model.dto.LinkPost;
import com.nik.yourcodereview.model.dto.ShortLink;
import com.nik.yourcodereview.model.entity.LinkEntity;
import com.nik.yourcodereview.utils.UrlUtils;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public abstract class LinkMapper {

    @Mapping(source = "original", target = "originalLink")
    public abstract LinkBO mapToLinkBO(LinkPost linkPost);

    public abstract LinkBO mapToLinkBO(LinkEntity linkEntity);

    @Mapping(source = "shortLink", target = "link")
    public abstract ShortLink mapToShortLinkDTO(LinkBO linkBO);

    @AfterMapping
    protected void addRedirectPrefixToShortLink(LinkBO linkBO, @MappingTarget ShortLink shortLink) {
        shortLink.setLink(UrlUtils.addRedirectPrefix(linkBO.getShortLink()));
    }

    @Mappings({
            @Mapping(source = "shortLink", target = "link"),
            @Mapping(source = "originalLink", target = "original"),
            @Mapping(source = "visitsCount", target = "count")
    })
    public abstract Link mapToLinkDTO(LinkBO linkBO);

    @AfterMapping
    protected void addRedirectPrefixToShortLink(LinkBO linkBO, @MappingTarget Link link) {
        link.setLink(UrlUtils.addRedirectPrefix(linkBO.getShortLink()));
    }

    @InheritConfiguration
    public abstract List<Link> mapToLinkDTO(List<LinkBO> linkBOList);

    public abstract LinkEntity mapToEntity(LinkBO linkBO);
}
