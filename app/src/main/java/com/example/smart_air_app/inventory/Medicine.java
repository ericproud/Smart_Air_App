package com.example.smart_air_app.inventory;

public class Medicine {
    private String name;
    private int remaining;
    private int total;
    private String lastPurchased;
    private String expires;
    private String reportedBy;

    @Override
    public String
    toString() {
        return "Medicine{" +
                "name='" + name + '\'' +
                ", remaining=" + remaining +
                ", total=" + total +
                ", lastPurchased='" + lastPurchased + '\'' +
                ", expires='" + expires + '\'' +
                ", reportedBy='" + reportedBy + '\'' +
                '}';
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public String getExpires() {
        return expires;
    }

    public String getLastPurchased() {
        return lastPurchased;
    }

    public int getTotal() {
        return total;
    }

    public int getRemaining() {
        return remaining;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setLastPurchased(String lastPurchased) {
        this.lastPurchased = lastPurchased;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }
}
