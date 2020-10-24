package com.app.tiktok.model;

public class ProcessPost {

    private String processPostUrl;
    private String processCaption;


    public ProcessPost(String processPostUrl, String processCaption) {
        this.processPostUrl = processPostUrl;
        this.processCaption = processCaption;
    }

    public String getProcessPostUrl() {
        return processPostUrl;
    }

    public void setProcessPostUrl(String processPostUrl) {
        this.processPostUrl = processPostUrl;
    }

    public String getProcessCaption() {
        return processCaption;
    }

    public void setProcessCaption(String processCaption) {
        this.processCaption = processCaption;
    }
}
