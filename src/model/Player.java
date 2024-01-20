package model;

import java.awt.Color;

public class Player {
	// 玩家姓名，IP和棋子颜色
	public String name;
    public String address = "无IP地址(本地玩家)";
    public int color = Spot.SPACE;
    
    // 两个玩家
    public static Player myPlayer, otherPlayer;

    public Player() {
        this.name = "玩家";
    }

    //初始化两个玩家
    public static void init() {
        myPlayer = new Player();
        otherPlayer = new Player();
    }

    //获取棋色
    public void start(int color) {
        this.color = color;
        System.out.println(name + " 获得棋色 " + Spot.ColorString(color));
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    
}
