package model;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle.Control;

import javax.swing.JOptionPane;

import control.SpotsControl;
import view.ChatRoom;
import view.ChessBoard;
import view.ControlPanel;
import view.MainFrame;


//将数据处理成字符串而后进行接收，发送
@SuppressWarnings("removal")
public class MessageWork {
	public final static String RETRACT = "retract";
	public final static String LOSE = "lose";
	public final static String IFALLWIN = "ifallwin";
	public final static String CANALLWIN = "canallwin";
	public final static String CANNOTALLWIN = "cannotallwin";
	public final static String STOP = "stop";
	public final static String CONTINUE = "continue";
	//接收发送的类型
	public final static String TYPE_chat = "chat";
    public final static String TYPE_spot = "spot";

    //处理数据后发送
    public static void send(Object object) {
        List<String> list = new ArrayList<>();

        //棋子信息
        if (object instanceof Spot) {
            list.add(TYPE_spot);
            int row = ((Spot) object).getRow();
            int col = ((Spot) object).getCol();

            if (row < 10) {
                list.add("0" + row);
            } else {
                list.add("" + row);
            }

            if (col < 10) {
                list.add("0" + col);
            } else {
                list.add("" + col);
            }
            MySocket.sendData(list);
        }
        //聊天内容
        if (object instanceof String) {
            list.add(TYPE_chat);
            list.add((String) object);
            MySocket.sendData(list);// 调用底层的socket的发送函数
        }
    }

    //接收，解析数据
    public static void receive(List<String> list) {
        String str = list.get(0);//第一个字符串判断数据类型
        switch (str) {
            case TYPE_chat:
            	if(list.get(1).equals(RETRACT)) {//悔棋通过特定字符串发送过来
            		JOptionPane.showMessageDialog(MainFrame.mainFrame,
        	                "对方悔棋", "wait",
        	                JOptionPane.CANCEL_OPTION);
            		if(SpotsControl.backChess()) {
        	        	ChessBoard.myBroad.repaint();	
            		}
            	}
            	else if(list.get(1).equals(LOSE)) {
            		// 对方认输了
            		JOptionPane.showMessageDialog(MainFrame.mainFrame,
        	                "对方认输，您方获胜", "wait",
        	                JOptionPane.CANCEL_OPTION);
            		SpotsControl.FinishFlag = true;
//            		GameCenter.gameModel = GameCenter.MODE_END;
//            		String colorStr = Spot.ColorString(Player.myPlayer.getColor());
//		    		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏结束，"+colorStr+"胜利！","游戏结束",JOptionPane.CANCEL_OPTION);

            	}
            	else if(list.get(1).equals(IFALLWIN)) {
            		// 需要弹出窗口，询问是否同意
            		
            		// 创建按钮的文本数组
            	    Object[] options = { "同意", "拒绝"};

            	    int result = JOptionPane.showOptionDialog(null,
            	            "对方向您发来了平局请求，您是否同意", "平局申请",
            	            JOptionPane.YES_NO_CANCEL_OPTION,
            	            JOptionPane.QUESTION_MESSAGE,
            	            null,
            	            options,
            	            options[0]);

            	    // 根据用户选择的按钮执行相应操作
            	    switch (result) {
            	        case JOptionPane.YES_OPTION: // 按钮1被点击
            	        	// 发送给对方同意
            	        	MessageWork.send(MessageWork.CANALLWIN);
            	        	// 游戏结束
            	        	SpotsControl.FinishFlag = true;
                    		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏结束，双方平局！","游戏结束",JOptionPane.CANCEL_OPTION);
            	        	ControlPanel.closeTimmer();
            	            break;
            	        case JOptionPane.NO_OPTION: // 按钮2被点击
            	        	// 发送给对方不同意
            	        	MessageWork.send(MessageWork.CANNOTALLWIN);
            	        	// TODO:此过程需要关闭或开启计时？
            	            break;
            	        default:
            	            break;
            	    }
            	}
            	else if(list.get(1).equals(CANALLWIN)) {
            		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏结束，双方平局！","游戏结束",JOptionPane.CANCEL_OPTION);
            		SpotsControl.FinishFlag = true;
    	        	ControlPanel.closeTimmer();

            	}
            	else if(list.get(1).equals(CANNOTALLWIN)) {
            		JOptionPane.showMessageDialog(MainFrame.mainFrame, "对方拒绝了您的平局请求，请继续游戏！","游戏结束",JOptionPane.CANCEL_OPTION);
            	}
            	else if(list.get(1).equals(STOP)) {
            		// 停下
            		ControlPanel.stopTimer();
            	}
            	else if(list.get(1).equals(CONTINUE)) {
            		// 继续
            		ControlPanel.continueTimer();
            	}
            	else {
            		ChatRoom.addText(list.get(1), ChatRoom.peText);//对方发送的聊天内容
            	}
                break;
            case TYPE_spot:
                int row = Integer.valueOf(list.get(1));
                int col = Integer.valueOf(list.get(2));
                int color = Player.otherPlayer.getColor();
                Spot other = new Spot(row, col, color);
                SpotsControl.putChess(other);
                ChessBoard.submitPaint(other);
                System.out.println("接收到棋子信息且更新面板了欸！！！！");
                // 判断是否有胜者
				if(SpotsControl.WhoWin()!=Spot.SPACE) {
					SpotsControl.FinishFlag=true;
					String resColor = Spot.ColorString(other.getColor());
		    		// 然后绘制连线
		    		GameCenter.gameModel = GameCenter.MODE_END;
		    		ChessBoard.paintResult();
		    		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏结束，"+resColor+"胜利！","游戏结束",JOptionPane.CANCEL_OPTION);
		    		ControlPanel.closeTimmer();
				}
                break;
            default:
                System.out.println("DataSocket 收到未知数据！" + str);
                break;
        }
    }
}
