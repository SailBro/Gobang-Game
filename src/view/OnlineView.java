package view;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import control.GameOnline;
import model.MySocket;
import model.Player;
import model.Spot;

//自定义对话框，弹出对话框进行网络连接，对方下线弹出对话框
@SuppressWarnings("removal")
public class OnlineView extends JDialog {
	private static final long serialVersionUID = 1L;
	
	JButton button;
    JLabel jLabel, upLabel;
    JPanel p1, p2, p3;
    JFrame jFrame;
    private static OnlineView my;
    private static String address = "10.130.48.223";// 默认是本机的IP地址

    OnlineView(JFrame jFrame, String title) {
        super(jFrame, title);
        //关闭上一个自定义窗口
        try {
            my.dispose();
        } catch (Exception e) {
        }
        my = this;
        this.jFrame = jFrame;

        jFrame.setEnabled(false);//模态对话框
        p1 = new JPanel();
        p2 = new JPanel();
        p3 = new JPanel();
        
        upLabel = new JLabel();//提示内容，静态文本，默认不显示，只在需要时弹出
        upLabel.setVisible(false);
        upLabel.setFont(new Font("宋体", Font.BOLD, 16));
        jLabel = new JLabel("自定义窗口,未设置内容....");//提示内容，动态文本
        jLabel.setFont(new Font("宋体", Font.BOLD, 16));
        button = new JButton("取消");
        
        p1.add(upLabel);
        p2.add(jLabel);
        p3.add(button);
        
        this.setSize(300, 300);
        //添加三个面板
        this.add(BorderLayout.NORTH, p1);
        this.add("Center", p2);
        this.add("South", p3);
        //设置到屏幕中间
        int sWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int sHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setLocation((sWidth - WIDTH) / 2, (sHeight - HEIGHT) / 2);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        
        button.addActionListener(event -> {
            jFrame.setEnabled(true);
            dispose();
        });
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            jFrame.setEnabled(true);
            dispose();
        }
    }

    public void setUpText(String str) {
        upLabel.setVisible(!"".equals(str));
        upLabel.setText("<html>" + str + "<html>");
    }

    public void setText(String str) {
        jLabel.setText("<html>" + str + "<html>");
    }

    public void setBtText(String str) {
        button.setText(str);
    }


    //对方下线弹出自定义对话框
    public static void monitor() {
        final OnlineView myDialog = new OnlineView(MainFrame.mainFrame, "连接断开");
        myDialog.setVisible(true);
        myDialog.setText("连接已经断开<br>我方或对方已下线!");
        myDialog.setBtText("确定");
    }

    //选择连接模式，直接弹出对话框
    public static void online() {
        Object[] options = {"创建房间", "加入房间"};
        int m = JOptionPane.showOptionDialog(null, "请选择联机对战方式", "联机对战",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);
        if (m == 0) {
            createRoom();
        }
        if (m == 1) {
            joinRoom();
        }
    }

    //创建房间窗口
    public static void createRoom() {
        // 加入房间则自己第一个出棋
        Player.myPlayer = new Player();
        Player.myPlayer.setName(UserInformation.name);
        Player.myPlayer.start(Spot.BLACK);

        Player.otherPlayer = new Player();
        Player.otherPlayer.setName("玩家");
        Player.otherPlayer.start(Spot.WHITE);

        final OnlineView myDialog = new OnlineView(MainFrame.mainFrame, "创建房间");

        final Thread thread = new Thread(() -> {
            try {
                System.out.println("正在创建房间");
                MySocket.serverConnect();// 作为服务器端等待连接
                //启动监控线程，监控对方是否下线
                GameOnline.monitor();
                GameOnline.getOtherAddress();
            } catch (Exception e) {
            }
        });
        thread.start();

        //线程2负责窗体维护，监控创建房间的线程是否正常运转，对应的修改提示对话框
        Thread thread2 = new Thread(() -> {
            while (true) {//一直循环执行直到return
                if (!thread.isAlive()) {
                    //创建房间线程server已停止
                    if (MySocket.isStart) {
                        //线程正常终止，对方加入房间成功
                        myDialog.setText("有玩家加入！开始游戏。");
                        myDialog.setBtText("开始游戏");
                    } else {
                        //加入房间线程异常终止，即加入房间失败
                        myDialog.setText("创建房间失败！可能是端口被占用！");
                        myDialog.setBtText("确定");
                    }
                    return;
                }

                //创建房间窗体关闭否
                if (!myDialog.isDisplayable()) {
                    //执行到这里说明，线程1创建房间未终止，窗口却关闭了，
                    //说明取消房间创建,则需要终止server
                    try {
                        System.out.print("等待窗口被手动关闭，强行终止accept！");
                        MySocket.server.close();
                        System.out.println("终止成功！");
                        //等待thread线程结束，用于后面判断线程状态！
                        thread.join(100);
                        break;
                    } catch (Exception e2) {
                    }
                } else {// 窗口未关闭
//                    try {
//                        myDialog.setUpText("其他玩家输入你的IP地址加入房间");
                        myDialog.setBtText("取消等待!");
                        
//                        myDialog.setText("正在等待玩家加入.");
//                        Thread.sleep(500);
//                        myDialog.setText("正在等待玩家加入..");
//                        Thread.sleep(500);
                        myDialog.setText("正在等待玩家加入...");
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
                    }
//                }
            }

            System.out.println("createRoom() 创建房间线程线程状态:" + thread.getState());
        });
        thread2.start();
    }

    //加入房间
    public static void joinRoom() {
        //加入房间则自己第二个出棋
        Player.otherPlayer = new Player();
        Player.otherPlayer.setName("房主");
        Player.otherPlayer.start(Spot.BLACK);

        Player.myPlayer = new Player();
        Player.myPlayer.setName(UserInformation.name);
        Player.myPlayer.start(Spot.WHITE);

        address = JOptionPane.showInputDialog("请输入房主IP地址", address);
        if (address == null) {
            return;
        }
        final OnlineView myDialog = new OnlineView(MainFrame.mainFrame, "加入房间");
        myDialog.setBtText("取消加入");
        myDialog.setText("正在加入房间，请等待..");

        //加入房间线程
        final Thread thread = new Thread(() -> {
            try {
                System.out.println("正在加入房间");
                MySocket.clientConnect(address);// 作为客户端发起连接
                GameOnline.monitor();
                GameOnline.getOtherAddress();
            } catch (Exception e1) {
            }
        });
        thread.start();

        //加入房间窗体线程
        Thread thread2 = new Thread(() -> {
            while (true) {
                if (!thread.isAlive()) {
                    //加入房间线程getSocket已停止
                    if (MySocket.isStart) {
                        //加入房间线程正常终止，加入房间成功
                        myDialog.setText("已加入房间！开始游戏。");
                        myDialog.setBtText("开始游戏");
                    } else {
                        //加入房间线程异常终止，加入房间失败
                        myDialog.setText("加入房间超时!!<br>可能是IP输入错误，请重新输入！");
                        myDialog.setBtText("确定");
                    }
                    return;
                }

                //加入房间线程未停止
                if (!myDialog.isDisplayable()) {//窗体被关闭
                    //执行到这里，则说明，线程1未终止，窗口却关闭了，
                    //即窗口被手动关闭，加入房间,则需要终止socket
                    try {
                        System.out.print("等待窗口被手动关闭，强行终止socket！");
                        MySocket.socket.close();
                        System.out.println(" 终止成功！");
                        //等待线程结束，用于后面判断线程状态！
                        thread.join(100);
                        break;
                    } catch (Exception e2) {
                    }
                } else {//窗口未关闭
//                    try {
                        myDialog.setBtText("取消等待!");
                        
//                        myDialog.setText("正在加入房间，请等待.");
//                        Thread.sleep(500);
//                        myDialog.setText("正在加入房间，请等待..");
//                        Thread.sleep(500);
//                        myDialog.setText("正在加入房间，请等待...");
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                    }
                }
            }
            System.out.println("joinRoom() 加入房间线程状态:" + thread.getState());
	        });
	        thread2.start();
	    }
}
