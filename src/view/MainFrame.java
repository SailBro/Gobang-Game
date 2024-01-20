package view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import model.Player;

public class MainFrame extends JFrame{
	// 主面板
    public static MainFrame mainFrame;
	
	// 窗口大小
	public static int width = 800;
	public static int height = 800;
	// 棋盘大小等
	public static int chessBoradWidth = 600;
	public static int statePanelHeight = 200;
	
	public MainFrame() {
		mainFrame = this;
		
		this.setTitle("五子棋小游戏");
		this.setSize(width,height);
		this.setResizable(false);// 用户不能拉伸窗口
//		this.setLayout(new BorderLayout()); // 设置布局管理器为居中显示
		this.setLayout(null); // 使用绝对布局
        this.setLocationRelativeTo(null); // 居中显示窗口
//        this.setBackground(Color.white);
        
        // TODO:字体设置
		
        // 添加一些组件到面板上
        addTool();
		
	}
	
	private void addTool() {
		
		this.setJMenuBar(new MenuBar());
//        this.getContentPane().setBackground(Color.white);
        
        getContentPane().add(new ControlPanel());
        ControlPanel.my.setBounds(1, chessBoradWidth+1, chessBoradWidth, height-chessBoradWidth);


        getContentPane().add(new ChessBoard());
        ChessBoard.myBroad.setBounds(1, 1, chessBoradWidth, chessBoradWidth);
        
        getContentPane().add(new UserInformation());
        UserInformation.my.setBounds(chessBoradWidth+1,1, width-chessBoradWidth,statePanelHeight);
        
        getContentPane().add(new ChatRoom());
        ChatRoom.myRoom.setBounds(chessBoradWidth+7,statePanelHeight+1,width-chessBoradWidth,height-statePanelHeight);

        
	}

	public static void init() {
        ChessBoard.init();
        Player.init();
        ControlPanel.init();
	}

	public static Object close() {
		return null;
	}

}
