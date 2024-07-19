package com.java.liyao.entity;

public class AiSummaryInfo {
    private int summaryId;
    private String uniqueId;
    private String aiSummary;

    public AiSummaryInfo(int summaryId, String uniqueId, String aiSummary) {
        this.summaryId = summaryId;
        this.uniqueId = uniqueId;
        this.aiSummary = aiSummary;
    }

    public int getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(int summaryId) {
        this.summaryId = summaryId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }
}
