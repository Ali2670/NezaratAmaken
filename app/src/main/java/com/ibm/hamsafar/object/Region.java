package com.ibm.hamsafar.object;

import java.io.Serializable;

/**
 * Created by maryam on 1/2/2019.
 */
public class Region implements Serializable {

    private Integer id;
    private String title;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
