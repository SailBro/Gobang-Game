package model;

import control.SpotsControl;

public class GameCenter {
	// 当前游戏状态
    public static int gameModel;
    
    //游戏结束
    public final static int MODE_END = 0;
    //双人对战
    public final static int MODE_COUPLE = 1;
    //人机对战
    public final static int MODE_ROBOT = 2;
    //联机对战
    public final static int MODE_ONLINE = 3;

    //开始游戏
    public static void reStart() {
        //初始化棋子
        SpotsControl.reset();
        gameModel = MODE_END;
    }

    

    public static int getMode() {
        return gameModel;
    }

    public static void setMode(int mode) {
        gameModel = mode;
    }

    public static boolean isEnd() {
        return gameModel == MODE_END;
    }
}
