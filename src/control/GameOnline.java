package control;

import model.GameCenter;
import model.MySocket;
import model.Player;
import view.OnlineView;

public class GameOnline {
	public static void reStart() {
        GameCenter.reStart();
        GameCenter.setMode(GameCenter.MODE_ONLINE);
    }

    //监控线程，监控对方是否下线，监控的是isStart，当信息发送接收出现问题时isStart就会置为false
    public static void monitor() {
        //用于检测游戏是否结束
        new Thread(() -> {
            while (MySocket.isStart) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            OnlineView.monitor();//连接断开了
            System.out.println("在线监控线程 已终止！");
        }).start();
    }

    //获取游戏玩家的信息
    public static void getOtherAddress() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (MySocket.isStart) {
                    Player.otherPlayer.setAddress(MySocket.peAddress);
                }
            } catch (InterruptedException e) {
            }

        }).start();
    }
}
