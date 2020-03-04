package com.tafh.tugasakhir.notifications;

public class Data {

    private String user, body, title, sent, noKK;
    private Integer icon;

    public Data() {
    }

    public Data(String user, String body, String title, String sent, String noKK, Integer icon) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.noKK = noKK;
        this.icon = icon;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getNoKK() {
        return noKK;
    }

    public void setNoKK(String noKK) {
        this.noKK = noKK;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }
}
