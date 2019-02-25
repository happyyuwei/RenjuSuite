/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//
//var url = "http://180.174.81.173:3389/renju/play";
var url = "/renju/play";
//玩家颜色，black或white，先走的为black
var player_black = "black";
var player_white = "white";

/**
 * ajax前后台交互，已封装至以下函数
 * @param {type} method
 * @param {type} message
 * @param {type} receive
 * @returns {undefined}
 */
function sendJson(method, message, receive) {
    //创建http对象
    var xmlhttp = new XMLHttpRequest();
    //接受响应回调
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            //接受并解析
            receive(xmlhttp.responseText);
        }
    };
    //建立连接
    xmlhttp.open(method, url, true);
    //设置json
    xmlhttp.setRequestHeader("Content-type", "application/json;charset=UTF-8");
    if (message !== null) {
        //发送
        xmlhttp.send(message);
    }
}

/**
 * Game类构造函数，应用核心类
 * @returns {undefined}
 */
var Game = function () {
    /**
     * 初始化棋盘
     * @returns {Array}
     */
    function initBoard() {
        var board = new Array();
        for (var i = 0; i < 15; i++) {
            board[i] = new Array();
            for (var j = 0; j < 15; j++) {
                board[i][j] = true;
            }
        }
        return board;
    }
    ;

    //15*15棋盘，true代表能走，false代表不能走
    var board_enable = initBoard();
    //计入当前玩家是否可以走，当玩家已走且AI未走时，玩家不能走两次
    var player_enable = true;

    //玩家，默认黑棋
    var player = player_black;
    //ai，默认白棋
    var ai = player_white;

    //定义宽度与高度
    var board_width = 600;
    //格子宽度
    var grid_width = board_width / 16;
    //棋子宽度
    var chess_width = grid_width * 0.45;
    //获取画布
    var canvas = document.getElementById("board");
    //定义画布大小
    canvas.width = board_width;
    canvas.height = board_width;
    //获取画布内容
    var context = canvas.getContext("2d");
    //session id
    var session_id = 0;

    //画棋
    function paintChess(row, col, player) {
        //输入行与列，转换成坐标
        var x = col * grid_width + grid_width;
        var y = row * grid_width + grid_width;
        //画棋子
        context.beginPath();
        context.arc(x, y, chess_width, 0, 2 * Math.PI);
        context.closePath();
        //增加渐变效果
        var gradient = context.createRadialGradient(x, y, chess_width, x, y, chess_width * 0.45);
        //渲染黑棋
        if (player === player_black) {
            gradient.addColorStop(0, "#0a0a0a");
            gradient.addColorStop(1, "#636766");
        } else {
            //渲染白棋
            gradient.addColorStop(0, "#D1D1D1");
            gradient.addColorStop(1, "#F9F9F9");
        }
        //填充
        context.fillStyle = gradient;
        context.fill();
    }
    ;
    /*
     * 第一步，发送开始游戏请求
     * msg={
     *  id:0
     *  code:1
     *  name:dora
     *  row=0
     *  col=0
     *  }
     * 
     * @returns {undefined}
     */
    function startGame() {
        //封装发送请求
        var msg = {
            id: 0, //code=1时，id无效
            //code=1, 请求启动游戏
            code: 1,
            name: "batman",
            row: 0, //code=1时，row无效
            col: 0 //code=1时，col无效
        };
        //发送json
        sendJson("POST", JSON.stringify(msg), function (body) {
            //转对象
            var response_msg = JSON.parse(body);
            //获取session
            session_id = response_msg.id;
            //获取谁先走
            //code=2,玩家先走
            //先走拿黑色
            if (response_msg.code === 2) {
                player = player_black;
                ai = player_white;
                alert("玩家先走");
            }
            //code=3，ai先走
            else if (response_msg.code === 3) {
                player = player_white;
                ai = player_black;
//                player = player_black;
//                ai = player_white;
                alert("机器先走");
                //获取机器第一步走的位置
                var row = response_msg.row;
                var col = response_msg.col;
                //机器落子
                paintChess(row, col, ai);
                //记录棋盘
                board_enable[row][col] = false;
            } else {
                alert("ai坏了，游戏结束。");
            }
        });
    }
    ;
    //初始化立即执行
    (function () {
        //创建背景图像实例
        var img = new Image();
        //加载图像
        img.src = "background.png";
        //图像加载完后，将图像绘制在画布上
        img.onload = function () {
            //把背景画在画布上
            context.drawImage(img, 0, 0, board_width, board_width);
            //设置线宽
            context.lineWidth = 3;
            //画边界框
            context.moveTo(grid_width, grid_width);
            context.lineTo(board_width - grid_width, grid_width);
            context.stroke();
            context.moveTo(grid_width, grid_width);
            context.lineTo(grid_width, board_width - grid_width);
            context.stroke();
            context.moveTo(grid_width, board_width - grid_width);
            context.lineTo(board_width - grid_width, board_width - grid_width);
            context.stroke();
            context.moveTo(board_width - grid_width, grid_width);
            context.lineTo(board_width - grid_width, board_width - grid_width);
            context.stroke();
            //画格子
            context.lineWidth = 2;
            //初始宽度
            var current = 2 * grid_width;
            for (var i = 0; i < 13; i++) {
                //通过循环画网格             
                //水平线
                context.moveTo(grid_width, current);
                context.lineTo(board_width - grid_width, current);
                context.stroke();
                //垂直线
                context.moveTo(current, grid_width);
                context.lineTo(current, board_width - grid_width);
                context.stroke();
                //下一条
                current = current + grid_width;
            }
            //启动游戏
            startGame();
        };
        //添加按键事件
        canvas.onclick = function (e) {
            if (player_enable === true) {
                //玩家走完一步，只有等ai走完后才能走第二步
                player_enable = false;
                //当鼠标按下棋盘，获取按下位置坐标
                //矫正坐标
                var x = (e.offsetX - grid_width) / grid_width;
                var y = (e.offsetY - grid_width) / grid_width;
                //计算行列
                var col = Math.round(x);
                var row = Math.round(y);
                //只有没走过的地方才能走
                if (board_enable[row][col] === true) {
                    //如果玩家是白棋，调用绘制白棋
                    //如果玩家是黑棋，调用绘制黑棋
                    paintChess(row, col, player);
                    board_enable[row][col] = false;
                    //封装玩家落子
                    var msg = {
                        id: session_id,
                        //code=8，玩家走棋完成
                        code: 8,
                        name: "batman",
                        row: row,
                        col: col
                    };
                    //发送
                    sendJson("POST", JSON.stringify(msg), function (response) {
                        //接收ai落子
                        var response_msg = JSON.parse(response);
                        //判断玩家有没有赢
                        //code=15,玩家赢
                        if (response_msg.code === 15) {
                            alert("玩家赢");
                            return;
                        }
                        //获取机器第一步走的位置
                        var ai_row = response_msg.row;
                        var ai_col = response_msg.col;
                        //绘制ai走棋
                        paintChess(ai_row, ai_col, ai);
                        board_enable[ai_row][ai_col] = false;
                        //ai走完，玩家继续走
                        player_enable = true;
                        //判断AI有没有赢
                        //code=16,AI赢
                        if (response_msg.code === 16) {
                            alert("AI赢");
                        }

                    });
                }else{
                    alert("非法走棋，不能走在重复的位置上");
                    player_enable=true;
                }
            } else {
                alert("等待AI走棋");
            }
        };

    })();
};

