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
public class MessageBean {
    //整个状态变化如下，
    //玩家请求 code=1---> 服务器决定谁先，先者黑棋，响应 code=2/3---> 
    //如果ai先，则同时返回第一步走棋棋盘
    //玩家走棋请求，code=8 ---> ai 走棋响应, code=9
    //如此往复，若玩家胜出，code=15, 若ai胜出, code=16
    
    /**
     * error code
     **/
    final public static int CODE_ERROR=-1;
    //start game code, request by player
    final public static int CODE_START_GAME = 1;
    //who first, response by server
    //player first
    final public static int CODE_PLAYER_FIRST = 2;
    //ai first
    final public static int CODE_AI_FIRST = 3;
    //turn
    //player has turned, request by player
    final public static int CODE_PLAYER_DONE = 8;
    //ai has turned, response by ai
    final public static int CODE_AI_DONE = 9;
    //win
    //player win
    final public static int CODE_PLAYER_WIN = 15;
    //ai win
    final public static int CODE_AI_WIN = 16;

    //session id
    private int id;
    //state code
    private int code;
    //player name
    private String name;
    //next row
    private int row;
    //next col
    private int col;
    
    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @param row the row to set
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return the col
     */
    public int getCol() {
        return col;
    }

    /**
     * @param col the col to set
     */
    public void setCol(int col) {
        this.col = col;
    }
    
    /**
     * bean factory
     * @param session_id
     * @param code
     * @param name
     * @param row
     * @param col
     * @return 
     */
    public static MessageBean createBean(int session_id, int code,String name, int row, int col){
         MessageBean bean=new MessageBean();
         bean.setCode(code);
         bean.setId(session_id);
         bean.setName(name);
         bean.setRow(row);
         bean.setCol(col);
         return bean;
    }
    
    @Override
    public String toString(){
        return String.format("class{session_id=%d, code=%d, name=%s, row=%d, col=%d}", this.id,this.code,this.name, this.row, this.col);
    }
    
}
