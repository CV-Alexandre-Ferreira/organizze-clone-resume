package com.example.organizze.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String currentDate(){
       long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = simpleDateFormat.format(date);

        return dateString;

    }

    public static String chosenMonthYearDate(String data){

        String dateReturn[] = data.split("/");

        String day = dateReturn[0]; //dia 23
        String month = dateReturn[1]; // mes 01
        String year = dateReturn[2]; // ano 2018

        String monthYear = month + year;
        return monthYear;
    }
}
