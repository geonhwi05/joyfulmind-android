package com.yh04.joyfulmindapp.model;

import java.util.Date;

public class Diary {
    private int id;
    private String title;
    private String content;
    private Date date;
    private Date createdAt;
    private Date updatedAt;

    public Diary(int id, String title, String content, Date date, Date createdAt, Date updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
