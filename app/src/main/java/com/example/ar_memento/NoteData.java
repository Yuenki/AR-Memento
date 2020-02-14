package com.example.ar_memento;

public class NoteData {
    private long id;
    private String title;
    private String content;
    private String date;
    private String time;

    NoteData(){}

    NoteData(String title, String content, String date, String time) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.time= time;
    }

    NoteData(long id, String title, String content, String date, String time) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.time= time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
