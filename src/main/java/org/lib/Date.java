package org.lib;

public class Date {

    private int day;
    private int month;
    private int year;

    public Date() {

    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDate() {
        String day;
        if (this.day < 10)
            day = '0' + String.valueOf(this.day);
        else
            day = String.valueOf(this.day);

        String month;
        if (this.month < 10)
            month = '0' + String.valueOf(this.month);
        else
            month = String.valueOf(this.month);

        String year;
        if (this.year < 10) {
            year = "000" + String.valueOf(this.year);
        } else if (this.year < 100) {
            year = "00" + String.valueOf(this.year);
        } else if (this.year < 1000) {
            year = '0' + String.valueOf(this.year);
        } else {
            year = String.valueOf(this.year);
        }

        return month + "/" + day + "/" + year;
    }
}
