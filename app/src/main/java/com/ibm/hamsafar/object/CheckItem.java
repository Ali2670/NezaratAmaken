package com.ibm.hamsafar.object;

import java.io.Serializable;

import hamsafar.ws.common.ReminderDto;

/**
 * Created by maryam on 12/19/2018.
 */
public class CheckItem implements Serializable {

    private Integer id;
    private String topic;
    private String date;
    private String time;
    private boolean checked;
    private Integer tripId;

    private Byte reminderFlag;
    private ReminderDto reminderDto;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public Byte getReminderFlag() {
        return reminderFlag;
    }

    public void setReminderFlag(Byte reminderFlag) {
        this.reminderFlag = reminderFlag;
    }

    public ReminderDto getReminderDto() {
        return reminderDto;
    }

    public void setReminderDto(ReminderDto reminderDto) {
        this.reminderDto = reminderDto;
    }
}
