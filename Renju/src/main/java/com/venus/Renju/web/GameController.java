/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.web;

import com.venus.Renju.StreamLoader;
import org.springframework.web.bind.annotation.*;
import com.venus.Renju.core.*;
import java.awt.Point;
import java.util.*;

/**
 *
 * @author happy
 */
@CrossOrigin(origins = "*")
@RestController
public class GameController {

    /**
     * controller api
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/renju/play")
    public MessageBean ai(@RequestBody MessageBean request) {
        LogCat.log(GameController.class, "get request:" + request);
        //get PlayerMap instance
        PlayMap map = PlayMap.getInstance();
        //get state code
        if (request.getCode() == MessageBean.CODE_START_GAME) {
            LogCat.log(GameController.class, "start game, user=" + request.getName());
            //start game
            //who first
            boolean ai_first = this.aiFirst();
            if (ai_first == true) {
                //ai black
                int session_id = map.createGame(Referee.BLACK_PLAYER, request.getLevel());
                LogCat.log(GameController.class, "ai first, session=" + session_id + ", user=" + request.getName());
                try {
                    //ai turn first
                    //如果ai_only为true,即第一步ai走棋,则参数row和col无意义
                    //获取ai走棋位置
                    //第一步走棋不可能直接胜利
                    Point ai_point = map.turn(session_id, true, 0, 0);
                    LogCat.log(GameController.class, "ai turn, point=" + ai_point + ", session=" + session_id + ", user=" + request.getName());
                    //response
                    MessageBean response = MessageBean.createBean(session_id, MessageBean.CODE_AI_FIRST, request.getName(), request.getLevel(), ai_point.x, ai_point.y);
                    return response;
                } catch (Exception exc) {
                    exc.printStackTrace();
                    return MessageBean.createBean(request.getId(), MessageBean.CODE_ERROR, request.getName(), request.getLevel(), 0, 0);
                }
            } else {
                //ai white
                int session_id = map.createGame(Referee.WHITE_PLAYER, request.getLevel());
                LogCat.log(GameController.class, "player first, session=" + session_id + ", user=" + request.getName());
                //response
                MessageBean response = MessageBean.createBean(session_id, MessageBean.CODE_PLAYER_FIRST, request.getName(), request.getLevel(), 0, 0);
                return response;
            }
        } else if (request.getCode() == MessageBean.CODE_PLAYER_DONE) {
            LogCat.log(GameController.class, "player turn, point=" + new Point(request.getRow(), request.getCol()) + ", session=" + request.getId() + ", user=" + request.getName());
            try {
                //player 走棋完成，轮到ai走棋
                Point ai_point = map.turn(request.getId(), false, request.getRow(), request.getCol());
                if (ai_point != null) {
                    LogCat.log(GameController.class, "ai turn, point=" + ai_point + ", session=" + request.getId() + ", user=" + request.getName());
                    return MessageBean.createBean(request.getId(), MessageBean.CODE_AI_DONE, request.getName(), request.getLevel(), ai_point.x, ai_point.y);
                } else {
                    LogCat.log(GameController.class, "useless request, session=" + request.getId() + ", user=" + request.getName());
                    return MessageBean.createBean(request.getId(), MessageBean.CODE_ERROR, request.getName(), request.getLevel(), 0, 0);
                }
            } catch (PlayMap.PlayerWinException exc) {
                LogCat.log(GameController.class, "player win, session=" + request.getId() + ", user=" + request.getName());
                this.saveScore(request.getName(), request.getLevel());
                LogCat.log(GameController.class, "player win, session=" + request.getId() + ", user=" + request.getName());
                //玩家胜出
                return MessageBean.createBean(request.getId(), MessageBean.CODE_PLAYER_WIN, request.getName(), request.getLevel(), 0, 0);
            } catch (PlayMap.AiWinException exc) {
                Point win_point = exc.getWin_point();
                LogCat.log(GameController.class, "ai turn, point=" + win_point + ", session=" + request.getId() + ", user=" + request.getName());
                //ai胜出
                LogCat.log(GameController.class, "ai win, session=" + request.getId() + ", user=" + request.getName());
                //目前规则，玩家获胜的一分，失败不扣分
                this.saveScore(request.getName(), 0);
                LogCat.log(GameController.class, "player " + request.getName() + " +0");
                return MessageBean.createBean(request.getId(), MessageBean.CODE_AI_WIN, request.getName(), request.getLevel(), win_point.x, win_point.y);
            }
        } else {
            LogCat.log(GameController.class, "unknown code, session=" + request.getId() + ", user=" + request.getName());
            return MessageBean.createBean(request.getId(), MessageBean.CODE_ERROR, request.getName(), request.getLevel(), 0, 0);
        }
    }

    /**
     * is ai first, the first is black
     *
     * @return
     */
    private boolean aiFirst() {
        return Math.random() >= 0.5;
    }

    /**
     *
     * @param user
     * @param score
     */
    private void saveScore(String user, int score) {
        Map<String, String> map = StreamLoader.readMap("../data/rank.txt", ",");
        String score_sum = map.get(user);
        if (score_sum == null) {
            map.put(user, String.valueOf(score));
        } else {
            map.replace(user, score_sum, String.valueOf(Integer.parseInt(score_sum) + score));
        }
        StreamLoader.writeMap(map, ",", "../data/rank.txt");
    }

}
