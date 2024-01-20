package view;

import java.awt.Desktop;
import java.awt.Font;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class MenuBar extends JMenuBar{
	//菜单项
    JMenuItem checkIP, exit, about,setTime, word,helpOnline;

    public MenuBar() {
        UIManager.put("Menu.font", new Font("宋体", Font.BOLD, 18));
        UIManager.put("MenuItem.font", new Font("宋体", Font.BOLD, 18));

        JMenu help = new JMenu("帮助");
        
        checkIP = new JMenuItem("查看本机IP");
        word = new JMenuItem("注意事项");
        
        help.add(word);
        help.add(checkIP);

        this.add(help);
        
        addListener();
    }

    public void addListener() {
        
        checkIP.addActionListener(event -> {
            String localIP = "本机IP地址:";
            InetAddress addr;
			try {
				addr = InetAddress.getLocalHost();
				String hostName=addr.getHostName();
				localIP = localIP +"\n"+hostName+ "\n" + addr.getHostAddress();
				JOptionPane.showMessageDialog(MainFrame.mainFrame, localIP, "查看本机IP", 
						JOptionPane.INFORMATION_MESSAGE);
			} catch (UnknownHostException e) {
				System.out.println("wrong in check ip");
			}                      
        });
                
        word.addActionListener(event -> JOptionPane
                .showMessageDialog(
                        MainFrame.mainFrame,
                        "如果需要更改头像，请在view.UserInformation类中，将打开文件的路径重新设置！",
                        "注意事项", JOptionPane.INFORMATION_MESSAGE));
        

    }
};
