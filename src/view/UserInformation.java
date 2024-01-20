package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Player;



//状态面板，建立线程实时刷新
public class UserInformation extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static JLabel PlayerImgLabel,PlayerNameLabel,PlayerWordLabel,Null1,Null2,Null3;
	public static String name="阿联酋不爱篮球";
	public static String word="我爱学习";
    public static UserInformation my;
    public static ImageIcon icon;
    private static JButton changeButton;

    public UserInformation() {
    	
//    	this.setLayout(new BorderLayout());
    	this.setLayout(new FlowLayout());
    	
    	
    	icon = new ImageIcon("fig/默认头像.jpg");
    	
        PlayerImgLabel = new JLabel(icon);
        PlayerNameLabel = new JLabel("玩家昵称："+name);
        PlayerWordLabel = new JLabel("个人签名："+word);
        
        Null1 = new JLabel("                                   ");
        Null2 = new JLabel("                                   ");
        
        changeButton = new JButton("点击修改个人信息");
//        changeButton.setSize(10, 10);
        
        this.setBackground(new Color(245,245,245));
        
        this.add(PlayerImgLabel);
        this.add(Null1);
        this.add(PlayerNameLabel);
//        this.add(Null2);
        this.add(PlayerWordLabel);
        this.add(changeButton);
        

        addListener();
        
        my = this;
        
    }


    // 在这填一下个人信息的监听
    private void addListener() {
		changeButton.addActionListener(event->{
			// 创建一个面板用于容纳两个文本框
		    JPanel panel = new JPanel();
		    panel.setLayout(new GridLayout(2, 2)); // 2行2列的网格布局

		    // 向面板中添加两个文本标签和两个文本框
		    panel.add(new JLabel("用户昵称："));
		    JTextField textField1 = new JTextField(10);
		    panel.add(textField1);

		    panel.add(new JLabel("个人签名:"));
		    JTextField textField2 = new JTextField(10);
		    panel.add(textField2);

		    // 弹出消息框，包含自定义的面板
		    int result = JOptionPane.showConfirmDialog(
		            null, panel, "用户信息修改",
		            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		    // 处理用户点击确认按钮的情况
		    if (result == JOptionPane.OK_OPTION) {
		        // 获取用户输入的两个字符串
		        String userInput1 = textField1.getText();
		        String userInput2 = textField2.getText();
		        
		        // 新设置信息
		        setName(userInput1);
		        setWord(userInput2);
		        
		        // 记得在player中也修改一下信息！
		        Player.myPlayer.setName(userInput1);

		        // 执行相应操作，使用这两个字符串
		        System.out.println("用户昵称：" + userInput1);
		        System.out.println("用户签名：" + userInput2);
		    } else {
		        // 用户取消了输入或点击了取消按钮
		        System.out.println("用户取消输入或点击了取消按钮");
		    }
		    
		});
		
		// 添加头像的鼠标双击监听
		PlayerImgLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
                    JFileChooser fileChooser = new JFileChooser();
                    // 设置初始目录
                    String initialDirectory = "D:\\study\\大三上\\Java\\fig\\头像";
                    fileChooser.setCurrentDirectory(new File(initialDirectory));
                    
                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
                        PlayerImgLabel.setIcon(imageIcon);
                    }
                }
			}
			
			
		});
			

	}
    
    @Override
    public void setName(String name) {
    	UserInformation.name= name ;
    	PlayerNameLabel.setText("玩家昵称："+name);
    }
    
    public static void setWord(String word) {
		UserInformation.word = word;
		PlayerWordLabel.setText("个性签名："+word);
	}
    
    
    
    
}
