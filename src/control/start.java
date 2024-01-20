package control;

import view.MainFrame;

public class start {
	public static void main(String[] args) {
		// 新建主面板对象，设置可见并初始化
		MainFrame mf = new MainFrame();
		mf.setVisible(true);
		MainFrame.init();
	}
}
