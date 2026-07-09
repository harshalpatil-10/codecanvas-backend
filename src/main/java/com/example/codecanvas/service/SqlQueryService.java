package com.example.codecanvas.service;

import com.example.codecanvas.dto.SqlQueryRequest;
import com.example.codecanvas.entity.SqlQuery;
import com.example.codecanvas.entity.User;
import com.example.codecanvas.repository.SqlQueryRepository;
import com.example.codecanvas.repository.UserRepository;
import com.example.codecanvas.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SqlQueryService {

    @Autowired
    private SqlQueryRepository sqlQueryRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public SqlQuery createQuery(SqlQueryRequest request) {
        User user = getCurrentUser();
        SqlQuery sqlQuery = new SqlQuery(request.getTitle(), request.getQuery(), request.getCategory(), user);
        return sqlQueryRepository.save(sqlQuery);
    }

    public List<SqlQuery> getAllQueries() {
        User user = getCurrentUser();
        return sqlQueryRepository.findByUserId(user.getId());
    }

    public SqlQuery getQueryById(Long id) {
        SqlQuery sqlQuery = sqlQueryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Query not found"));
        sqlQuery.setLastViewed(LocalDateTime.now());
        return sqlQueryRepository.save(sqlQuery);
    }

    public SqlQuery updateQuery(Long id, SqlQueryRequest request) {
        SqlQuery sqlQuery = sqlQueryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Query not found"));
        sqlQuery.setTitle(request.getTitle());
        sqlQuery.setQuery(request.getQuery());
        sqlQuery.setCategory(request.getCategory());
        return sqlQueryRepository.save(sqlQuery);
    }

    public void deleteQuery(Long id) {
        sqlQueryRepository.deleteById(id);
    }

    public SqlQuery toggleFavorite(Long id) {
        SqlQuery sqlQuery = sqlQueryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Query not found"));
        sqlQuery.setFavorite(!sqlQuery.isFavorite());
        return sqlQueryRepository.save(sqlQuery);
    }
}