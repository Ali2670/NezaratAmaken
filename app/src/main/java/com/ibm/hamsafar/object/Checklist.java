package com.ibm.hamsafar.object;

import java.io.Serializable;

/**
 * Created by maryam on 12/29/2018.
 */
public class Checklist implements Serializable {

    private String title;
    private String item_one;
    private String item_two;
    private String item_three;
    private Integer id;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getItem_one() {
        return item_one;
    }

    public void setItem_one(String item_one) {
        this.item_one = item_one;
    }

    public String getItem_two() {
        return item_two;
    }

    public void setItem_two(String item_two) {
        this.item_two = item_two;
    }

    public String getItem_three() {
        return item_three;
    }

    public void setItem_three(String item_three) {
        this.item_three = item_three;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
