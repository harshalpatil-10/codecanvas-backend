package com.example.codecanvas.controller;

import com.example.codecanvas.dto.DashboardStats;
import com.example.codecanvas.dto.TimelineItem;
import com.example.codecanvas.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public DashboardStats getDashboard() {
        return dashboardService.getStats();
    }

    @GetMapping("/timeline")
    public List<TimelineItem> getTimeline() {
        return dashboardService.getTimeline();
    }

    @GetMapping("/revision-due")
    public List<Object> getRevisionDue() {
        return dashboardService.getRevisionDueItems();
    }
}