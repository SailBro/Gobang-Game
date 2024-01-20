package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import control.SpotsControl;
import model.MessageWork;
import model.GameCenter;
import model.Player;
import model.Spot;



// 棋盘的view
public class ChessBoard extends JPanel {
	private static final long serialVersionUID = 1L;
    protected static int chessSize;	//棋子大小
    public static ChessBoard myBroad;
    
    // 用线程来解决棋盘会覆盖掉棋子
    static Thread gThread, allChessThread;
    
	ImageIcon icon;
	Image img;

    public ChessBoard() {
        this.setVisible(true);
        this.setBackground(Color.white);
        
		icon=new ImageIcon("fig/棋盘.jpg");
		img=icon.getImage();   
        
        this.addListener();
        myBroad = this;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintTable();
        paintAllChess();
        paintResult();
		g.drawImage(img, 0, 0,this.getWidth(), this.getHeight(), this);
    }

    public static void paintResult() {
		// 绘制赢了棋的连线
    	
    	// 需要判断一下是不是胜利
    	if(GameCenter.isEnd()) {
    		// 需要判断一下是不是空
    		if(SpotsControl.spotEnd==null) {
    			System.out.println("空的居然");
    			return;
    		}
    		int beginX,beginY;
    		int endX = SpotsControl.spotEnd.getCol();
    		int endY = SpotsControl.spotEnd.getRow();
    		// 判断一下是哪个方向，然后找到对应的起始坐标
    		switch (SpotsControl.tempThread) {
			case SpotsControl.HENG:
    			beginX = endX-4;
    			beginY = endY;
				break;
			case SpotsControl.SHU:
				beginX = endX;
				beginY = endY-4;
				break;
			case SpotsControl.ZUOXIE:
				beginX = endX-4;
				beginY = endY-4;
				break;
			case SpotsControl.YOUXIE:
				beginX = endX+4;
				beginY = endY-4;
				break;
			default:
				return;
			}
    		
    		Graphics2D g = (Graphics2D) myBroad.getGraphics();
    		int x1 = colToX(beginX+1)+7;
    		int y1 = rowToY(beginY+1)+7;
    		int x2 = colToX(endX+1)+7;
    		int y2 = rowToY(endY+1)+7;
    		System.out.println("画线位置："+endX+" "+endY);
    		g.setColor(Color.yellow);
            g.setStroke(new BasicStroke(5.0f));
            g.setFont(new Font("黑体", Font.BOLD, 150));
    		g.drawLine(x1, y1, x2, y2);
    		System.out.println("线画完了");
    	}
		
	}

	public static void paintAllChess() {
		//绘制所有棋子线程，没有线程时棋子可能绘制失败！
        allChessThread = new Thread(() -> {
            try {//等待棋桌绘制完成
                gThread.join();
            } catch (InterruptedException e) {
            }
            for (int i = 0; i < 19; i++) {
                for (int j = 0; j < 19; j++) {
                    Spot spot = SpotsControl.getSpot(i, j);
                    paintChessImages(spot);
                }
            }
        });
        allChessThread.start();
	}

	//初始化
    public static void init() {
        chessSize = myBroad.getWidth() / 19;
        myBroad.repaint();
    }

    // 一些绘制的函数和鼠标监听
    //绘制棋盘
    private static void paintTable() {
        final Graphics graphics = myBroad.getGraphics();
        graphics.setFont(new Font("黑体", Font.BOLD, 20));
        graphics.setColor(Color.white);
        //在线程中绘制棋盘
        gThread = new Thread(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
	        for (int i = 0; i < 19; i++) {
	            graphics.drawString("" + (i + 1),1, 25 + chessSize * i);
	            graphics.drawLine(chessSize / 2 +7, chessSize / 2 + chessSize * i +7,
	                    chessSize / 2 + chessSize * 18 +7, chessSize / 2
	                            + chessSize * i +7);
	
	            graphics.drawString("" + (i + 1), chessSize * i + 13, 15);
	            graphics.drawLine(chessSize / 2 + chessSize * i +7, chessSize / 2 +7,
	                    chessSize / 2 + chessSize * i +7, chessSize / 2
	                            + chessSize * 18 +7);
	        }
        });
        gThread.start();
    }
    
    // 添加监听
    private void addListener() {
		// 鼠标事件
    	this.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			super.mouseClicked(e);
    			// 查找鼠标点击坐标
    			int x = e.getX();
    			int y = e.getY();
    			System.out.println(x);
    			System.out.println(y);
    			// 根据鼠标坐标，获得行列的位置
    			int row = yToRow(y);
    			int col = xToCol(x);
    			Spot spot = new Spot(row-1,col-1,SpotsControl.getLocalColor());
    			
    			// 判断一下能不能下棋
    			boolean canPut = ifCanPutChess(spot);
    			System.out.println("spot:"+SpotsControl.getLocalColor());
    			if(canPut) {

    				//联机模式发送棋子信息
                    if (GameCenter.getMode() == GameCenter.MODE_ONLINE) {
                        MessageWork.send(spot);
                    }
    				
    				// 自己下棋
        			SpotsControl.putChess(spot);
    				// 绘制
    				submitPaint(spot);
    				System.out.println("画完了");
    				// 判断是否有胜者
    				if(SpotsControl.WhoWin()!=Spot.SPACE) {
    					SpotsControl.FinishFlag=true;
    					String resColor = Spot.ColorString(spot.getColor());
    		    		// 然后绘制连线
    		    		GameCenter.gameModel = GameCenter.MODE_END;
    		    		paintResult();
    		    		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏结束，"+resColor+"胜利！","游戏结束",JOptionPane.CANCEL_OPTION);
    		    		ControlPanel.closeTimmer();
    				}
    			}
    		}
    	});

	}
    
    
    
    private boolean ifCanPutChess(Spot spot) {
    	// case0:是否开始游戏
        if (GameCenter.isEnd()) {
            JOptionPane.showMessageDialog(null,
                    "游戏未开始或已结束，请进入游戏", "游戏未开始",
                    JOptionPane.CANCEL_OPTION);
            return false;
        }
    	
    	// case1:是否已经有胜者了
    	if(SpotsControl.FinishFlag) {
    		JOptionPane.showMessageDialog(MainFrame.mainFrame, "游戏已结束，不能再下棋！","游戏提示",JOptionPane.CANCEL_OPTION);
    		return false;
    	}
    	
    	// case2:当前是否轮该用户下棋
    	//如果是联机对战，界面上只能下自己颜色的棋子
        if (GameCenter.getMode() == GameCenter.MODE_ONLINE) {
            if (spot.getColor()!=Player.myPlayer.getColor()) {
            	System.out.println("my:"+Player.myPlayer.getColor());
                JOptionPane.showMessageDialog(MainFrame.mainFrame,
                        "联机对战中，请先等待对方下棋", "请等待",
                        JOptionPane.CANCEL_OPTION);
//                System.out.println(Player.myPlayer.getColor() + ":" + SpotsControl.getLocalColor());
                return false;
            }
        }
    	// case3:这个地方有没有棋子
    	if(SpotsControl.hasPut(spot.getRow(), spot.getCol())) {
    		System.out.println("此处已经有棋子了！");
    		return false;
    	}
    	
    	
		return true;
	}
    
    public static void submitPaint(Spot spot) {
		paintChessImages(spot);
		// 判断游戏是否结束
		int winner = SpotsControl.WhoWin();
		if(winner != Spot.SPACE) {
			System.out.println("已经有胜者了");
			
		}
	}
    
    //绘制棋子
  	private static void paintChessImages(Spot spot) {
          if (spot != null) {
          	
              int row = spot.getRow() + 1;
              int col = spot.getCol() + 1;

              int cx = colToX(col);
              int cy = rowToY(row);
              Graphics g = myBroad.getGraphics();
              int color = spot.getColor();
              switch (color) {
                  case Spot.BLACK:
                      g.setColor(Color.black);
                      break;
                  case Spot.WHITE:
                      g.setColor(Color.white);
                      break;
                  default:
                      return;
              }
              g.fillOval(cx - chessSize / 2+7, cy - chessSize / 2+7, chessSize,
                      chessSize);
          }
      }
  	
    //将行转换为Y坐标
    public static int rowToY(int row) {
        return ChessBoard.chessSize * (row - 1) + ChessBoard.chessSize / 2;
    }

    //将列转换为X坐标
    public static int colToX(int col) {
        return ChessBoard.chessSize * (col - 1) + ChessBoard.chessSize / 2;
    }

    //Y坐标转换为行
    public static int yToRow(int y) {
        return (y + ChessBoard.chessSize) / ChessBoard.chessSize;
    }

    //将X坐标转换为列
    public static int xToCol(int x) {
        return (x + ChessBoard.chessSize) / ChessBoard.chessSize;
    }
    
    
    
    
    
    

}
