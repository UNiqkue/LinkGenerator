package com.nik.yourcodereview.dao;

import com.nik.yourcodereview.model.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LinkRepository extends JpaRepository<LinkEntity, String> {

    Long countByVisitsCountIsGreaterThanEqual(Long visitsCount);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE LinkEntity l SET l.visitsCount = (l.visitsCount + :value) WHERE l.shortLink = :link")
    void incrementVisitsCount(@Param("link") String shortLink, @Param("value") Long incrementValue);
}
