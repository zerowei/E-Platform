package com.mmall.test;


import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Test {

    static class Student {
        String name;
        Date date;

        Student () {super();}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    @org.junit.Test
    public void test1() {
        BigDecimal n1 = new BigDecimal(0.5);
        BigDecimal n2 = new BigDecimal(0.1);
        System.out.println(n1.add(n2));
    }

    @org.junit.Test
    public void test2() {
        String s = "abcd";
        String[] t = s.split("_");
        System.out.println(t[0]);
    }

    @org.junit.Test
    public void test3() {
        List<String> temp = new ArrayList<>(Arrays.asList("1", "2", "3"));
        List<Integer> productIdList = temp.stream().map(Integer::parseInt).collect(Collectors.toList());
        System.out.println(productIdList);
        Student student = new Student();
        student.setName("Wei");
        System.out.println(student.getDate());
        DateTime dateTime = new DateTime(student.getDate());
        System.out.println(dateTime);

    }
}
