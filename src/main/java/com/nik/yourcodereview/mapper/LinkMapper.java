package com.nik.yourcodereview.mapper;

import com.nik.yourcodereview.model.bo.LinkBO;
import com.nik.yourcodereview.model.dto.Link;
import com.nik.yourcodereview.model.dto.LinkPost;
import com.nik.yourcodereview.model.dto.ShortLink;
import com.nik.yourcodereview.model.entity.LinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface LinkMapper {

    @Mapping(source = "original", target = "originalLink")
    LinkBO mapToLinkBO(LinkPost linkPost);

    LinkBO mapToLinkBO(LinkEntity linkEntity);

    @Mapping(source = "shortLink", target = "link")
    ShortLink mapToShortLinkDTO(LinkBO linkBO);

    @Mappings({
            @Mapping(source = "shortLink", target = "link"),
            @Mapping(source = "originalLink", target = "original"),
            @Mapping(source = "visitsCount", target = "count")
    })
    Link mapToLinkDTO(LinkBO linkBO);

    @Mappings({
            @Mapping(source = "shortLink", target = "link"),
            @Mapping(source = "originalLink", target = "original"),
            @Mapping(source = "visitsCount", target = "count")
    })
    List<Link> mapToLinkDTO(List<LinkBO> linkBOList);

    LinkEntity mapToEntity(LinkBO linkBO);
}
