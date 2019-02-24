/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju;

import com.venus.Renju.core.*;
import java.util.*;
import java.awt.*;

/**
 *
 * @author happy
 */
public class CmdAIGame {

    final private Referee referee;

    final private SearchEngine search_engine;

    public CmdAIGame(int border_width) {
        this.referee = new Referee(border_width);
        this.search_engine = new SearchEngine(Referee.WHITE_PLAYER, 3, this.referee, new SimpleValueModel());

    }

    public void start() {
        Scanner scan = new Scanner(System.in);
        while (true) {

            System.out.println("请输入走棋位置：(row,col)");
            String line = scan.next();
            String[] value = line.split(",");
            if (value.length != 2) {
                System.out.println("输入格式错误");
            } else {
                int x = Integer.parseInt(value[0]);
                int y = Integer.parseInt(value[1]);
                Point p = new Point(x, y);
                int win = referee.turn(p, this.referee.getWhite_id());
                if (win == -1) {
                    System.out.println("非法走棋:" + p);
                    continue;
                }
                System.out.println("你的位置：" + p);
                System.out.println("当前棋盘：");
                Referee.print(this.referee.getBoard());
                if (win >= 5) {
                    System.out.println("你胜利了！");
                    break;
                }
                //ai turns
                System.out.println("AI 正在计算...");
                Point ai_p = this.search_engine.search();
                this.referee.turn(ai_p, referee.getBlack_id());
                System.out.println("当前棋盘：");
                Referee.print(this.referee.getBoard());
            }

        }
    }

}
