package com.example.codecanvas.repository;

import com.example.codecanvas.entity.SqlQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SqlQueryRepository extends JpaRepository<SqlQuery, Long> {
    List<SqlQuery> findByUserId(Long userId);
    @Query("SELECT s FROM SqlQuery s WHERE s.user.id = :userId AND " +
    	       "(LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    	List<SqlQuery> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}