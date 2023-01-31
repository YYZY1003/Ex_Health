package com.yy.reggie.demo;

public class CodeDemo {
    public static void main(String[] args) {
        String phone = "18585595238";
        String code = phone.substring(7, 11);
        System.out.println("code = " + code);
    }
}
