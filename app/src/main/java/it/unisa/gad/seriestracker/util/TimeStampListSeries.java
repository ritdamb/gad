package it.unisa.gad.seriestracker.util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.unisa.gad.seriestracker.Domain.Series;

/**
 * Created by ludimar on 21/01/16.
 */
public class TimeStampListSeries<E> implements Serializable {

    private ArrayList<E> list;
    private Date timeStamp;

    public TimeStampListSeries(ArrayList<E> list) {
        this.list = list;
        //init Date
        java.util.Date date= new java.util.Date();
        timeStamp= new Timestamp(date.getTime());
    }

    public ArrayList<E> getList() {
        return list;
    }

    public void setList(ArrayList<E> list) {
        this.list = list;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
