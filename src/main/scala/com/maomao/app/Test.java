package com.maomao.app;


import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Test {
    public static void main(String[] args) {
        Timestamp timestamp = new Timestamp(999999999999l);

        System.out.println(timestamp.toString());

        LocalDateTime time = LocalDateTime.of(2106, 30, 1, 0, 0);


    }
}
