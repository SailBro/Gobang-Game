package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.MessageWork;
import model.MySocket;
import model.Player;

public class ChatRoom extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static JTextArea jArea;
    private static JTextField jText;
    private static JButton btClear, btSend;
    public static ChatRoom myRoom;
    private JComboBox<String> quickPhrases;
    
    public final static int myText = 0;
    public final static int peText = 1;

    public ChatRoom() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBackground(new Color(245,245,245));
        jArea = new JTextArea(26, 24);
        jText = new JTextField(15);
        
        btClear = new JButton("清空");
        btSend = new JButton("发送");
        btClear.setBackground(new Color(135,206,235));
        btSend.setBackground(new Color(135,206,235));
        
        // 下拉列表，用于选择快捷短语
        String[] phrases = {"你好！:)", "再见！:(","我等的花都快谢了！" , "我爱学Java鸭~"};
        quickPhrases = new JComboBox<>(phrases);
        quickPhrases.addActionListener(e -> {
            String selectedPhrase = (String) quickPhrases.getSelectedItem();
            jText.setText(selectedPhrase); // 将选择的短语放入文本框
        });
        
        jArea.setEnabled(false);
        jArea.setLineWrap(false);//不自动换行
        jArea.setText("******** 聊天区 ********\n");
        jArea.setFont(new Font("楷体", Font.BOLD, 13));
        //滚轮包装
        JScrollPane jsp = new JScrollPane(jArea);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);//过长就用滚轮
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(jsp);
        
        this.add(quickPhrases);

        this.add(jText);

        this.add(btClear);
        this.add(btSend);
        
        
        addListener();
        myRoom = this;
    }

    public void addListener() {
        btClear.addActionListener(event -> jArea.setText("******** 聊天区 ********\n"));//清空聊天区
        btSend.addActionListener(event -> {
            if (MySocket.isStart) {
                String text = jText.getText();
                if (text.length() > 0) {
                    addText(text, myText);
                    MessageWork.send(text);
                }
            } else {
            	// 直接显示在屏幕上就行
            	String text = jText.getText();
            	addText(text, myText);
            }
        });
    }

    public static void addText(String text, int who) {
        switch (who) {
            case myText:
                text = "我["+UserInformation.name+"]: \n" + text;
                break;
            case peText:
                text = "对方: \n" + text;
                break;
            default:
                break;
        }
        jArea.append(text+"\n");
    }
}
