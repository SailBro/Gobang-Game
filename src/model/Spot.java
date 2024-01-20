package model;

public class Spot {
	// 成员变量
	// 常量——final，相当于define
	public static final int BLACK = 1;
	public static final int WHITE = -1;
	public static final int SPACE = 0;
	
	private int color;
    private int row;
    private int col;
    
    public Spot(int row,int col,int color) {
    	this.color = color;
    	this.row = row;
    	this.col = col;
	}
    
    public int getColor() {
		return color;
	}
    
    public void setColor(int color) {
		this.color = color;
	}
    
    public int getRow() {
		return row;
	}
    
    public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
	
	// int->String转换
	public static String ColorString(int color) {
        if(color==BLACK)
        	return "黑色";
        else if(color==WHITE)
        	return "白色";
        else if(color==SPACE)
        	return "空";
        else
        	return null;
    }
	
	
}
