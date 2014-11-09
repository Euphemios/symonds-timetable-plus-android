package com.psyngo.michael.symondstimetableplus;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Michael on 09/11/2014.
 */
public class FriendDatabaseObject {
    List<Calendar[]> Monday;
    List<Calendar[]> Tuesday;
    List<Calendar[]> Wednesday;
    List<Calendar[]> Thursday;
    List<Calendar[]> Friday;

    public FriendDatabaseObject(List<Calendar[]> monday, List<Calendar[]> tuesday, List<Calendar[]> wednesday, List<Calendar[]> thursday, List<Calendar[]> friday) {
        Monday = monday;
        Tuesday = tuesday;
        Wednesday = wednesday;
        Thursday = thursday;
        Friday = friday;
    }

    public FriendDatabaseObject(){

    }

    public List<Calendar[]> getMonday() {
        return Monday;
    }

    public void setMonday(List<Calendar[]> monday) {
        Monday = monday;
    }

    public List<Calendar[]> getTuesday() {
        return Tuesday;
    }

    public void setTuesday(List<Calendar[]> tuesday) {
        Tuesday = tuesday;
    }

    public List<Calendar[]> getWednesday() {
        return Wednesday;
    }

    public void setWednesday(List<Calendar[]> wednesday) {
        Wednesday = wednesday;
    }

    public List<Calendar[]> getThursday() {
        return Thursday;
    }

    public void setThursday(List<Calendar[]> thursday) {
        Thursday = thursday;
    }

    public List<Calendar[]> getFriday() {
        return Friday;
    }

    public void setFriday(List<Calendar[]> friday) {
        Friday = friday;
    }
}


