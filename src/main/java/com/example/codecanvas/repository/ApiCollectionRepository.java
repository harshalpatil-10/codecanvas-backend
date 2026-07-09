package com.example.codecanvas.repository;

import com.example.codecanvas.entity.ApiCollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApiCollectionRepository extends JpaRepository<ApiCollectionItem, Long> {
    List<ApiCollectionItem> findByUserId(Long userId);
    @Query("SELECT a FROM ApiCollectionItem a WHERE a.user.id = :userId AND " +
    	       "(LOWER(a.url) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    	List<ApiCollectionItem> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}