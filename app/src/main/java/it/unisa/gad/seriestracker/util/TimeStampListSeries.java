package it.unisa.gad.seriestracker.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.unisa.gad.seriestracker.Domain.Series;

/**
 * Created by ludimar on 21/01/16.
 */
public class TimeStampListSeries implements Serializable {

    private ArrayList<Series> list;
    private Date timeStamp;

    public TimeStampListSeries(ArrayList<Series> list) {
        this.list = list;
        //init Date
        timeStamp = new Date();
    }

    public ArrayList<Series> getList() {
        return list;
    }

    public void setList(ArrayList<Series> list) {
        this.list = list;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
