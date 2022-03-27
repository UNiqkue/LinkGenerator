package com.nik.yourcodereview.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "links")
@DynamicUpdate
@Data
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class LinkEntity {

    @Id
    @Column(name = "short_link", unique = true, length = 15)
    private String shortLink;

    @Column(name = "original_link", length = 2048)
    private String originalLink;

    @Column(name = "visits_count")
    private Long visitsCount;

    @Column(name = "create_at")
    private LocalDateTime createAt;
}
