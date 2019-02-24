/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.core;

import java.util.*;
import com.venus.Renju.StreamLoader;

/**
 *
 * @author happy
 */
public class RefereeSaver {

    //the matrax is been stored as s string: [1,2,3;4,5,6;...]
    final public static String LEFT_BRACKET = "[";
    final public static String RIGHT_BRACKET = "]";
    final public static String COLUMN_SPLIT = ", ";
    final public static String ROW_SPLIT = "; ";
    //back list
    final private List<byte[][]> backup_list;

    //time line
    final private List<Long> time_line;

    /**
     * constrcutor.
     */
    public RefereeSaver() {
        //create backup list
        this.backup_list = new ArrayList<>();
        //create time line
        this.time_line = new ArrayList<>();
    }

    /**
     * backup the board and add time line， 该方法会复制矩阵而不是把引用保存.
     *
     * @param board
     */
    public void backup(byte[][] board) {
        //add time line
        this.time_line.add(System.currentTimeMillis());
        //add board
        this.backup_list.add(Referee.copyMatrix(board));
    }
    
    /**
     * if i==-1, the latest history board will be returned
     * @param i
     * @return 
     */
    public byte[][] getHistoryBoard(int i){
        if(i<0){
            return Referee.copyMatrix(this.backup_list.get(this.backup_list.size()-1));
        }else{
            return Referee.copyMatrix(this.backup_list.get(i));
        }
    }

    /**
     * get the last board， 该方法会返回复制数组，不会只是返回引用。
     *
     * @return
     */
    public byte[][] redo() {
        if (this.backup_list.isEmpty() == true) {
            return null;
        } else {
            //return the last board
            byte[][] last = this.backup_list.get(this.backup_list.size() - 1);
            //delete from list
            this.backup_list.remove(last);
            //get time line
            long time = this.time_line.get(this.time_line.size() - 1);
            //delete time line
            this.time_line.remove(time);
            return Referee.copyMatrix(last);
        }
    }

    /**
     * 清空备份.
     */
    public void clear() {
        //clear backup
        this.backup_list.clear();
        //clear time line
        this.time_line.clear();
    }

    /**
     *
     * @param path
     */
    public void save(String path) {
        //create string builder
        StringBuilder builder = new StringBuilder();
        //save each record
        for (int i = 0; i < this.backup_list.size(); i++) {
            builder.append(RefereeSaver.matraxToString(this.backup_list.get(i)));
            //next line
            builder.append("\n");
        }
        //save time line
        builder.append(this.time_line);
        //save 
        StreamLoader.write(builder.toString(), path);
    }
    
    /**
     * 
     * @param path 
     */
    public void load(String path){
        //clear history
        this.clear();
        //load lines
        String[] lines=StreamLoader.readLines(path);
        //parse backup list
        for(int i=0;i<lines.length-1;i++){
            this.backup_list.add(RefereeSaver.StringToMatrax(lines[i]));
        }
        //parse time line
        String time_str=lines[lines.length-1];
        time_str=time_str.substring(1, time_str.length()-1);
        String[] time_array=time_str.split(RefereeSaver.COLUMN_SPLIT);
        for(int i=0;i<time_array.length;i++){
            this.time_line.add(Long.parseLong(time_array[i]));
        }
        System.out.println(this.time_line);
        
    }

    /**
     * 把矩阵转换成字符串，转换规则如下：1,2,3;4,5,6;...
     *
     * @param matrax
     * @return
     */
    public static String matraxToString(byte[][] matrax) {
        //string builder
        StringBuilder str_builder = new StringBuilder(RefereeSaver.LEFT_BRACKET);
        //scan
        for (int i = 0; i < matrax.length; i++) {
            for (int j = 0; j < matrax[0].length; j++) {
                str_builder.append(matrax[i][j]);
                if (j != matrax[0].length - 1) {
                    str_builder.append(RefereeSaver.COLUMN_SPLIT);
                }
            }
            if (i != matrax.length - 1) {
                str_builder.append(RefereeSaver.ROW_SPLIT);
            }
        }
        str_builder.append(RefereeSaver.RIGHT_BRACKET);
        return str_builder.toString();
    }
    
    /**
     * 
     * @param str
     * @return 
     */
    public static byte[][] StringToMatrax(String str){
        str=str.substring(1, str.length()-1);
        //parse row
        String[] row=str.split(RefereeSaver.ROW_SPLIT);
        //parse column length
        int col_len=row[0].split(RefereeSaver.COLUMN_SPLIT).length;
        //create matrax
        byte[][] mat=new byte[row.length][col_len];
        //parse column
        for(int i=0;i<row.length;i++){
            String[] column=row[i].split(RefereeSaver.COLUMN_SPLIT);
            for(int j=0;j<column.length;j++){
                mat[i][j]=Byte.parseByte(column[j]);
            }
        }
        return mat;
    }

}
