package com.library.dto;

public class AnalyticsResponse {
    private long totalBooks;
    private long totalUsers;
    private long activeBorrows;

    public AnalyticsResponse() {}

    public AnalyticsResponse(long totalBooks, long totalUsers, long activeBorrows) {
        this.totalBooks = totalBooks;
        this.totalUsers = totalUsers;
        this.activeBorrows = activeBorrows;
    }

    public long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(long totalBooks) {
        this.totalBooks = totalBooks;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveBorrows() {
        return activeBorrows;
    }

    public void setActiveBorrows(long activeBorrows) {
        this.activeBorrows = activeBorrows;
    }
}
