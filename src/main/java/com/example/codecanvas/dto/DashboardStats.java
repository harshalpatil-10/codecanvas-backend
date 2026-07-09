package com.example.codecanvas.dto;

public class DashboardStats {
    private long totalSnippets;
    private long totalNotes;
    private long totalApis;
    private long totalSqlQueries;
    private long revisionDue;

    public DashboardStats(long totalSnippets, long totalNotes, long totalApis, long totalSqlQueries, long revisionDue) {
        this.totalSnippets = totalSnippets;
        this.totalNotes = totalNotes;
        this.totalApis = totalApis;
        this.totalSqlQueries = totalSqlQueries;
        this.revisionDue = revisionDue;
    }

    public long getTotalSnippets() { return totalSnippets; }
    public long getTotalNotes() { return totalNotes; }
    public long getTotalApis() { return totalApis; }
    public long getTotalSqlQueries() { return totalSqlQueries; }
    public long getRevisionDue() { return revisionDue; }
}