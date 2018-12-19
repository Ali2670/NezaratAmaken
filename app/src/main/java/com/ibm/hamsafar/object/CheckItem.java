package com.ibm.hamsafar.object;

import java.io.Serializable;

/**
 * Created by maryam on 12/19/2018.
 */
public class CheckItem implements Serializable {

    private String topic;
    private String date;
    private String time;


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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
