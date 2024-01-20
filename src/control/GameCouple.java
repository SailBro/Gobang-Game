package control;

import model.GameCenter;
import model.Player;
import model.Spot;

public class GameCouple {
	public static void reStart() {
        GameCenter.reStart();
        GameCenter.setMode(GameCenter.MODE_COUPLE);
        // 设置自己是黑色（先手）
        Player.myPlayer.start(Spot.BLACK);
        Player.otherPlayer.start(Spot.WHITE);
    }
}
