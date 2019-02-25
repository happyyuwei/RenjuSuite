/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju;

import com.venus.Renju.core.*;
import com.venus.Renju.gui.*;

/**
 *
 * @author happy
 */
public class Renju {

    /**
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println(Renju.help());
        } else {
            if (args[0].equals("-w")) {
                //start web game
                WebGame game = new WebGame();
                game.start(args);
            }else if(args[0].equals("-l")){
                //start gui client game
                 MenuWindow menu_window=new MenuWindow();
            }else if(args[0].equals("-c")){
                //start a command game
                CmdAIGame game=new CmdAIGame();
                game.start();
            }else{
                System.out.println("useless command:"+args[0]+"\n\n");
                Renju.help();
            }
                
        }
    }

    /**
     * 
     * @return 
     */
    public static String help() {
        return "Renju\nversion 1.2\n@since 2019.2.1\n@author yuwei and dora\n"
                + "command:\n-w create a web server\n-l create a local game gui client\n"
                + "-c create a command line game with no gui\nwish you good luck.";
    }

}
