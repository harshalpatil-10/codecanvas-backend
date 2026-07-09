package com.example.codecanvas.controller;

import com.example.codecanvas.dto.SqlQueryRequest;
import com.example.codecanvas.entity.SqlQuery;
import com.example.codecanvas.service.SqlQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/sql")
public class SqlQueryController {

    @Autowired
    private SqlQueryService sqlQueryService;

    @PostMapping
    public SqlQuery createQuery(@RequestBody SqlQueryRequest request) {
        return sqlQueryService.createQuery(request);
    }

    @GetMapping
    public List<SqlQuery> getAllQueries() {
        return sqlQueryService.getAllQueries();
    }

    @GetMapping("/{id}")
    public SqlQuery getQuery(@PathVariable Long id) {
        return sqlQueryService.getQueryById(id);
    }

    @PutMapping("/{id}")
    public SqlQuery updateQuery(@PathVariable Long id, @RequestBody SqlQueryRequest request) {
        return sqlQueryService.updateQuery(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteQuery(@PathVariable Long id) {
        sqlQueryService.deleteQuery(id);
        return "Query deleted successfully";
    }

    @PostMapping("/{id}/favorite")
    public SqlQuery toggleFavorite(@PathVariable Long id) {
        return sqlQueryService.toggleFavorite(id);
    }
}