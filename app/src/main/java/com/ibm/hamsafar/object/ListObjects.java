package com.ibm.hamsafar.object;

public class ListObjects {
    Long id;
    String title;

    public ListObjects() {
    }

    public ListObjects(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
