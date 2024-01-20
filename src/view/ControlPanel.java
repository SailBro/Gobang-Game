package view;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import control.GameCouple;
import control.GameOnline;
import control.GameRobot;
import control.SpotsControl;
import model.MessageWork;
import model.GameCenter;
//import control.GameCouple;
//import control.GameOnline;
//import control.GameRobot;
//import control.TableData;
//import model.DataSocket;
//import model.GameCenter;
//import model.MySocket;
import model.MySocket;
import model.Player;
import model.PlayerAI;
import model.Spot;

//右侧控制按钮面板
@SuppressWarnings("removal")
public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static ControlPanel my;
    private JButton btnBegin,btnAllwin,btnStop,btnBack,btnLose,btnAgain;
    static public JLabel colorLabel,timeLabel,playerLabel=null;
    static private String ColorStr;
    static public final int timmer = 15;
    static private int ms=timmer;
    static public boolean TimeFlag = false;


    public ControlPanel() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.setBackground(new Color(245,245,245));
        
        btnBegin = new JButton("开始游戏");
        btnAgain = new JButton("退出本局游戏");
        
//        btnBegin.setBackground(Color.yellow);
//        btnBegin.setBackground(new Color(240,255,240));
        
        btnAllwin = new JButton("请求平局");
        btnStop = new JButton("请求暂停");
//        btnAllwin.setBackground(Color.blue);
        
        btnBack = new JButton("悔棋");
        btnLose = new JButton("认输");
        
        JLabel null0 = new JLabel("                                                                    "
        		+ "                                                                                    ");
        		
        
//        btnCouple.setBackground(new Color(248,248,255));
//        btnRobot.setBackground(new Color(248,248,255));
//        btnOnline.setBackground(new Color(248,248,255));
        

        
//        btnBack.setBackground(new Color(224,255,255));
        ColorStr = Spot.ColorString(SpotsControl.getLocalColor());
        colorLabel = new JLabel("当前棋子颜色："+ColorStr);
        colorLabel.setFont(new Font("宋体", Font.BOLD, 20));
        
        JLabel null1 = new JLabel("                                  ");
        
        JLabel null2 = new JLabel("                                                                    "
        		+ "                                                                                    ");
        
        timeLabel = new JLabel("倒计时："+ms+"秒");
        timeLabel.setFont(new Font("宋体", Font.BOLD, 20));
        // TODO
        this.add(null0);
        this.add(btnBegin);
        this.add(btnAgain);
        this.add(btnAllwin);
        this.add(btnStop);
        this.add(btnBack);
        this.add(btnLose);
        this.add(null2);
        this.add(colorLabel);
        this.add(null1);
        this.add(timeLabel);
        
//        if(GameCenter.gameModel==GameCenter.MODE_ONLINE) {
//        	String colorStr = Spot.ColorString(Player.myPlayer.getColor());
//        	playerLabel = new JLabel("玩家棋色："+colorStr);
//        	this.add(playerLabel);
//        }
        

        my = this;
        addListener();
        CountDown(15); 
         
    }

    public static void init() {
        my.repaint();
    }
    
    // 为按钮添加监听
    private void addListener() {
    	// 开始
    	btnBegin.addActionListener(event->{
    		// 创建按钮的文本数组
    	    Object[] options = { "经典双人模式", "AI对战模式", "联机对战模式" };

    	    int result = JOptionPane.showOptionDialog(null,
    	            "请选择游戏模式", "模式选择",
    	            JOptionPane.YES_NO_CANCEL_OPTION,
    	            JOptionPane.QUESTION_MESSAGE,
    	            null,
    	            options,
    	            options[0]);

    	    // 根据用户选择的按钮执行相应操作
    	    switch (result) {
    	        case JOptionPane.YES_OPTION: // 按钮1被点击
    	        	GameCouple.reStart();
    	            break;
    	        case JOptionPane.NO_OPTION: // 按钮2被点击
    	        	GameRobot.reStart();
    	            break;
    	        case JOptionPane.CANCEL_OPTION: // 按钮3被点击
    	        	GameOnline.reStart();
    	            OnlineView.online();
    	            break;
    	        default:
    	            // 默认情况，可能是关闭了对话框
    	            break;
    	    }
    	    
    	    // 开始计时器
    	    if(GameCenter.gameModel==GameCenter.MODE_COUPLE)
    	    	openTimmer();
    	});
    	
    	
    	// 重开
    	btnAgain.addActionListener(event->{
    		
    		
    		// 创建按钮的文本数组
    	    Object[] options = { "确定", "取消"};

    	    int result = JOptionPane.showOptionDialog(null,
    	            "确定要退出本局游戏？", "退出游戏",
    	            JOptionPane.YES_NO_CANCEL_OPTION,
    	            JOptionPane.QUESTION_MESSAGE,
    	            null,
    	            options,
    	            options[0]);

    	    // 根据用户选择的按钮执行相应操作
    	    switch (result) {
    	        case JOptionPane.YES_OPTION: // 按钮1被点击
    	        	GameCenter.reStart();
    	            ChessBoard.myBroad.repaint();
    	            MySocket.close();
    	            try {
    	                MySocket.socket.close();
    	            } catch (Exception e) {
    	            }
    	            break;
    	        case JOptionPane.NO_OPTION: // 按钮2被点击
    	            break;
    	        default:
    	            break;
    	    }
    		closeTimmer();
    	    
    	});
    	
    	// 悔棋
    	btnBack.addActionListener(event->{
    		if(!SpotsControl.backChess()) {
    			System.out.println("悔棋失败！");
    		}
    		
    		//联机模式得让对面收到悔棋操作
        	if(GameCenter.getMode()==GameCenter.MODE_ONLINE) {
        		MessageWork.send(MessageWork.RETRACT);
        	}
        	ChessBoard.myBroad.repaint();	
    	});
    	
    	// 认输
    	btnLose.addActionListener(event->{
    		// 直接游戏结束了
    		SpotsControl.FinishFlag = true;
    		closeTimmer();
    		if(SpotsControl.lastSpot==null)
        		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏已结束！","游戏结束",JOptionPane.CANCEL_OPTION);

    		// 获取赢棋颜色
    		String resColor = Spot.ColorString(SpotsControl.lastSpot.getColor());
    		// 消息框提示
    		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏结束，"+resColor+"胜利！","游戏结束",JOptionPane.CANCEL_OPTION);
    		
    		//联机模式得让对面收到悔棋操作
        	if(GameCenter.getMode()==GameCenter.MODE_ONLINE) {
        		MessageWork.send(MessageWork.LOSE);
        	}
        	
//        	GameCenter.reStart();
//            ChessBoard.myBroad.repaint();
//            MySocket.close();
//            try {
//                MySocket.socket.close();
//            } catch (Exception e) {
//            }
    		
    	});
    	
    	// 请求平局
    	btnAllwin.addActionListener(event->{
    		// 如果是AI或者普通模式，直接平局
    		if(GameCenter.gameModel!=GameCenter.MODE_ONLINE) {
    			SpotsControl.FinishFlag = true;
	        	ControlPanel.closeTimmer();
        		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏结束，双方平局！","游戏结束",JOptionPane.CANCEL_OPTION);
    		}
    		else {
    			MessageWork.send(MessageWork.IFALLWIN);
    		}
    	});
    	
    	btnStop.addActionListener(event->{
    		if(!TimeFlag) {
    			// 恢复成“暂停”并打开
    			btnStop.setText("请求暂停");
    			continueTimer();
    			// 也需要通知
    			if(GameCenter.gameModel==GameCenter.MODE_ONLINE) {
					MessageWork.send(MessageWork.CONTINUE);
				}
    		}
    		else {
	    		// 如果是AI或者普通模式，直接暂停就行
	    		stopTimer();
				// 然后按键变为continue
				btnStop.setText("点击继续");
				// 不然需要通知一下
				if(GameCenter.gameModel==GameCenter.MODE_ONLINE) {
					MessageWork.send(MessageWork.STOP);
				}
    		}
    		
    	});
    	
    	 
    	
    	
    }
    
    public static void updataColorLabel() {
    	ColorStr = Spot.ColorString(SpotsControl.getLocalColor());
    	colorLabel.setText("当前棋子颜色："+ColorStr);
	}
    
    public void CountDown(int min) {
    	 
        //开始时间
        long start = System.currentTimeMillis();
        //结束时间
        final long end = start + min * 60 * 1000;
     
        final Timer timer = new Timer();
        //延迟0毫秒（即立即执行）开始，每隔1000毫秒执行一次
        timer.schedule(new TimerTask() {
            public void run() {
            	// 判断一下计时器有没有开启
            	if(!TimeFlag)
            		return;
                ms--;
                timeLabel.setText("倒计时："+ms+"秒");
//                System.out.println("倒计时"+ms+"秒");
                // 如果是倒计时为0了
                if(ms==0) {
//                	JOptionPane.showMessageDialog(MainFrame.mainFrame, "超时！！","超时提示",JOptionPane.CANCEL_OPTION);
                	// TODO：区分一下是联机还是单机！！！！！！
                	if(GameCenter.gameModel==GameCenter.MODE_ONLINE&&Player.myPlayer.getColor()!=SpotsControl.getLocalColor())
                		return;
                    Spot spot = PlayerAI.getBestChess(SpotsControl.getLocalColor());//获取最佳下棋位置
                    SpotsControl.putChess(spot);
                    ChessBoard.submitPaint(spot);
                    
                    // 如果是联机下棋中下棋的一方，需要给对方传递消息
                    if(GameCenter.gameModel==GameCenter.MODE_ONLINE)
                    	MessageWork.send(spot);
                    if(SpotsControl.WhoWin()!=Spot.SPACE) {
                    	SpotsControl.FinishFlag=true;
    					String resColor = Spot.ColorString(spot.getColor());
    		    		// 然后绘制连线
    		    		GameCenter.gameModel = GameCenter.MODE_END;
    		    		ChessBoard.paintResult();
    		    		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏结束，"+resColor+"胜利！","游戏结束",JOptionPane.CANCEL_OPTION);
    		    		ControlPanel.closeTimmer();
                    }
                	
//                	// 权限转换
//                	SpotsControl.localColor = -SpotsControl.localColor;
//                	updataColorLabel();
                    else{
	                	// 重新倒计时
	                	resetTime();
                    }
                }
            }
        }, 0, 1000);
        //计时结束时候，停止全部timer计时计划任务
        timer.schedule(new TimerTask() {
            public void run() {
                timer.cancel();
            }
     
        }, new Date(end));
    }
    
    
    public static void closeTimmer() {
    	TimeFlag = false;
    	ms =timmer;
    }
    
    public static void openTimmer() {
    	TimeFlag = true;
    	ms = timmer;
    }
    
    public static void resetTime() {
    	TimeFlag = true;
		ms = timmer;
	}
    
    public static void stopTimer() {
    	TimeFlag = false;
    }
    
    public static void continueTimer() {
    	TimeFlag = true;
	}
    
};







