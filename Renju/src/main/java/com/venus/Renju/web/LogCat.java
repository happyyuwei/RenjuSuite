/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.web;

/**
 *
 * @author happy
 */
public class LogCat {
    
    final static private String log_format="Logcat, %s, from class=[%s], %s";
    
    /**
     * log the format is data class and msg
     * @param msg 
     * @param c
     */
    public static void log(Class c, String msg){
        //log
//        System.out.println(String.format(log_format, new java.util.Date(),c.getName(), msg));
    }
    
}
