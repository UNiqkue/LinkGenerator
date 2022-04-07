package com.nik.yourcodereview.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class LinkBO {
    private String shortLink;

    private String originalLink;

    private Long rank;

    private Long visitsCount;

    private LocalDateTime createAt;
}
