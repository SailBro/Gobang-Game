package control;

import javax.swing.JOptionPane;

import model.GameCenter;
import model.Player;
import model.PlayerAI;
import model.Spot;
import view.ChessBoard;
import view.ControlPanel;
import view.MainFrame;

public class GameRobot {
	public static void reStart() {
        GameCenter.reStart();
        GameCenter.setMode(GameCenter.MODE_ROBOT);
        
        Player.myPlayer.start(Spot.BLACK);
        final PlayerAI playerAI = new PlayerAI();
        playerAI.start(Spot.WHITE);

        //下棋线程
        new Thread(() -> {
            int color = playerAI.getColor();
            while (!GameCenter.isEnd()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("robotThread 睡眠异常！");
                }
                //轮到人机下棋了
                if (SpotsControl.getLocalColor()==color&&!GameCenter.isEnd()) {
                    Spot spot = PlayerAI.getBestChess(color);//获取最佳下棋位置
                    SpotsControl.putChess(spot);
                    ChessBoard.submitPaint(spot);
                    if(SpotsControl.WhoWin()!=Spot.SPACE) {
                    	SpotsControl.FinishFlag=true;
    					String resColor = Spot.ColorString(spot.getColor());
    		    		// 然后绘制连线
    		    		GameCenter.gameModel = GameCenter.MODE_END;
    		    		ChessBoard.paintResult();
    		    		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏结束，"+resColor+"胜利！","游戏结束",JOptionPane.CANCEL_OPTION);
    		    		ControlPanel.closeTimmer();
                    }
                }
            }
            System.out.println("电脑下棋线程已停止");
        }).start();
    }
}
