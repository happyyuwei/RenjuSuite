/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju;

import java.io.*;
import java.util.*;

/**
 *
 * @author happy
 */
public class StreamLoader {
    
    
    /**
     * 
     * @param dir
     * @param split
     * @return 
     */
    public static Map<String,String> readMap(String dir,String split){
        String[] lines=StreamLoader.readLines(dir);
        Map<String,String> map=new HashMap<>();
        for(int i=0;i<lines.length;i++){
            String[] line=lines[i].split(split);
            if(line.length>=2){
                map.put(line[0].trim(), line[1].trim());
            }
        }
        return map;
    }
    
    /**
     * readline by file path
     * @param dir
     * @return 
     */
    public static String[] readLines(String dir){
        try{
            BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(dir)));
            return StreamLoader.readLines(in);
        }catch(IOException exc){
            exc.printStackTrace();
            return null;
        }
    }

    /**
     * readlines in buffer
     *
     * @param in
     * @return
     */
    public static String[] readLines(BufferedReader in) {
        try {
            String line;
            List<String> list = new ArrayList<>();
            //read all lines
            while ((line = in.readLine()) != null) {
                list.add(line);
            }
            in.close();
            //to array
            String[] array = new String[list.size()];
            return list.toArray(array);
        } catch (IOException exc) {
            exc.printStackTrace();
            return null;
        }
    }
    
    /**
     * write string to a file
     * @param str
     * @param file 
     */
    public static void write(String str, String file){
        try{
             PrintStream out=new PrintStream(file);
             out.print(str);
             out.close();
         }catch(IOException exc){
             exc.printStackTrace();
         }
    }
    
    /**
     * 
     * @param byte_file
     * @param file_name 
     * @throws java.lang.Exception 
     */
    public static void writeBytes(byte[] byte_file, String file_name) throws Exception{
        BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(file_name));
        out.write(byte_file);
        out.flush();
        out.close();
    }

}
