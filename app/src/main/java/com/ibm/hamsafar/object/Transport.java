package com.ibm.hamsafar.object;

import java.io.Serializable;

/**
 * Created by maryam on 1/8/2019.
 */
public class Transport implements Serializable {

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
