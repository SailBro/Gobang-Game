package control;

import java.util.Iterator;

import javax.swing.JOptionPane;

import model.Spot;
import view.ChessBoard;
import view.ControlPanel;
import view.MainFrame;

public class SpotsControl {
	
	private final static Spot[][] spots = new Spot[19][19];// 棋盘
	public static int localColor = Spot.BLACK;// 现在的颜色，默认黑色先下棋
	public static boolean FinishFlag = false;
	// 需要记录最后一次下棋的棋子（为了悔棋）
	public static Spot lastSpot;
	
	// 记录一下连线的棋子
	public static Spot spotEnd;// 能确认连线的最后一个棋子
	public static int tempThread = 0;// 哪条线
	public static final int HENG = 1;
	public static final int SHU = 2;
	public static final int ZUOXIE = 3;
	public static final int YOUXIE = 4;
	
	// 下棋函数
	public static int putChess(Spot spot) {
		// 判断一下游戏是否结束
		if(FinishFlag)
			return 0;
		// 判断一下是不是对应的玩家在下棋（权限问题）
		if(spot.getColor()!=localColor) {
			System.out.println("当前玩家没有下棋权限");
			return 1;
		}
		// 判断一下这里有没有被下过
		if(hasPut(spot.getRow(),spot.getCol())) {
			System.out.println("该位置已经被下过棋了！");
			return 2;
		}
		// 可以下棋
		if(spots[spot.getRow()][spot.getCol()]==null)
			spots[spot.getRow()][spot.getCol()]=spot;
		spots[spot.getRow()][spot.getCol()].setColor(spot.getColor());
		// 翻转颜色
		localColor = -localColor;
		ControlPanel.updataColorLabel();// 更新一下面板
		// 重新开始计时
		ControlPanel.resetTime();
		// 记录棋子
		lastSpot = spot;
		return 3;
	}

	public static boolean hasPut(int row,int col) {
		if(spots[row][col]==null)
			return false;
		if(spots[row][col].getColor()==Spot.SPACE)
			return false;
		return true;
	}

	// 悔棋函数
	public static boolean backChess() {
		// 如果游戏结束或者悔棋之后还没下棋，就不能再悔棋了
		if(FinishFlag||lastSpot==null) {
			System.out.println("不能悔棋了！");
			// 弹出消息框
			JOptionPane.showMessageDialog(MainFrame.mainFrame, "不能悔棋了！","游戏提示",JOptionPane.CANCEL_OPTION);
			return false;
		}
		int row = lastSpot.getRow();
		int col = lastSpot.getCol();
		spots[row][col].setColor(Spot.SPACE);
		// 转换颜色，主要是因为悔棋的A在B下棋时悔棋，所以权限要从B转到A
		localColor = - localColor;
		ControlPanel.updataColorLabel();
		// 重新开始计时
		ControlPanel.resetTime();
		lastSpot = null;
		return true;
	}
	
	// 对外提供一个找棋子的接口
	public static Spot getSpot(int row,int col) {
		return spots[row][col];
	}
	
	// 以及一个获取现在color的接口
	public static int getLocalColor() {
		return localColor;
	}
	
	public static int WhoWin() {
		if(FinishFlag)
			return lastSpot.getColor();
		
		if(lastSpot==null)
			return Spot.SPACE;
		
		int lastRow = lastSpot.getRow();
		int lastCol = lastSpot.getCol();
		int lastColor = lastSpot.getColor();
		// 从当前下的位置开始判断
		// 思路是根据最后一个棋子所在位置找对应的行列斜
		// 先检查行
		int temp = 0;
		for (int i = 0; i < spots.length; i++) {
			if(spots[lastRow][i]!=null&&spots[lastRow][i].getColor() == lastColor) {
				temp += 1;
				if(temp == 5) {
					spotEnd = spots[lastRow][lastCol];
					tempThread = HENG;
					return lastColor;
				}
			}
			else {
				temp = 0;
			}
		}
		
		// 再检查列
		temp = 0;
		for (int i = 0; i < spots.length; i++) {
			if(spots[i][lastCol]!=null&&spots[i][lastCol].getColor() == lastColor) {
				temp += 1;
				if(temp == 5) {
					spotEnd = spots[i][lastCol];
					tempThread = SHU;
					return lastColor;
				}
			}
			else {
				temp = 0;
			}
		}
		
		// 检查左上到右下这个斜线
		temp = 0;
		for (int i = 0; i < spots.length; i++) {
			int j = i+lastCol-lastRow;
			if (j < 0)
				continue;
			else if (j >= spots.length) {
				break;
			}
			else {
				// 合理范围内
				if(spots[i][j]!=null&&spots[i][j].getColor() == lastColor) {
					temp += 1;
					if(temp == 5) {
						spotEnd = spots[i][j];
						tempThread = ZUOXIE;
						return lastColor;
					}
				}
				else {
					temp = 0;
				}
			}
		}
		
		// 检查右上到左下这个斜线
		temp = 0;
		for (int i = 0; i < spots.length; i++) {
			int j = lastCol+lastRow-i;
			if (j < 0)
				break;// i++;j--，总会减小到0
			else if (j >= spots.length) {
				continue;
			}
			else {
				// 合理范围内
				if(spots[i][j]!=null&&spots[i][j].getColor() == lastColor) {
					temp += 1;
					if(temp == 5) {
						spotEnd = spots[i][j];
						tempThread = YOUXIE;
						return lastColor;
					}
				}
				else {
					temp = 0;
				}
			}
		}
		
		return Spot.SPACE;
			
	}
	
	// 重新开始游戏
	public static void reset() {
		//初始化棋桌数据
        for (int i = 0; i < spots.length; i++) {
            for (int j = 0; j < spots[0].length; j++) {
                spots[i][j] = new Spot(i, j, Spot.SPACE);
            }
        }
        FinishFlag = false;
        //黑棋先下
        localColor = Spot.BLACK;
        ControlPanel.updataColorLabel();
        tempThread = 0;
        spotEnd = null;
        
        lastSpot=null;
        System.out.println("已初始化棋盘数据");
	}
	
	
}
