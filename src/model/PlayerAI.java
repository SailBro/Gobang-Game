package model;

import java.util.ArrayList;
import java.util.Random;

import control.SpotsControl;

public class PlayerAI extends Player{
	public PlayerAI() {
        this.name = "电脑AI";
    }

    //人机下棋
    public static Spot getBestChess(int mColor) {
    	if(Spot.SPACE==mColor) {
            System.out.println("playChess 颜色设置错误！");
            return null;
        }

        //获得最大权值点
        Spot maxWeightSpot = maxSpot(mColor);

        //如果未得到最大值，或者最大值的权值为零，则随机下棋
        if (maxWeightSpot == null) {
            System.out.println("AI算法结果为空！随机下棋");
            return playRandom(mColor);
        }

        //同时计算对手的权值，获得权值最大的点
        int matchColor = -mColor;//对手的棋色
        Spot matchSpot = maxSpot(matchColor);

        int a = getWeight(maxWeightSpot, mColor);
        int b = getWeight(matchSpot, matchColor);
        if (b - a > 0) {
            maxWeightSpot = matchSpot;//围堵对方
        }
        int row = maxWeightSpot.getRow();
        int col = maxWeightSpot.getCol();
        return new Spot(row, col, mColor);
    }
    
    public static Spot playRandom(int mColor) {
        Spot spot;
        int col;
        int row;
        while (true) {
            col = new Random().nextInt(19);
            row = new Random().nextInt(19);
            spot = SpotsControl.getSpot(row, col);
            int color = spot.getColor();
            if (Spot.SPACE==color) {//随机找到一个可以下棋的点
                break;
            }
        }
        return new Spot(row, col, mColor);
    }

    //计算棋子权重
    public static int getWeight(Spot spot, int mColor) {
        if (Spot.SPACE==mColor) {
            return 0;
        }
        
        //此点的权重，默认为零
        int weight = 0;
        //获取此点的行列
        int row = spot.getRow();
        int col = spot.getCol();
        int len[] = {10000, 8000, 5000, 2000, 1000};//自己周围同色棋子越多权值越大

        for (int i = 1; i < 5; i++) {
            //该已经存在棋子，则不计算权值，默认为0
            if (SpotsControl.getSpot(row, col).getColor() != Spot.SPACE) {
                return 0;
            }
            //下
            if (row + i < 19) {
                spot = SpotsControl.getSpot(row + i, col);
                if (spot.getColor() == mColor)
                    weight += len[i - 1];
            }
            //上
            if (row - i >= 0) {
                spot = SpotsControl.getSpot(row - i, col);
                if (spot.getColor() == mColor)
                    weight += len[i - 1];
            }
            //右
            if (col + i < 19) {
                spot = SpotsControl.getSpot(row, col + i);
                if (spot.getColor() == mColor)
                    weight += len[i - 1];
            }
            //左
            if (col - i >= 0) {
                spot = SpotsControl.getSpot(row, col - i);
                if (spot.getColor() == mColor)
                    weight += len[i - 1];
            }
        }
        weight += (int) (Math.random() * 500) + 1;
        return weight;
    }
    
    //返回当前棋色最大权值点
    public static Spot maxSpot(int mColor) {
        if (Spot.SPACE==mColor) {
            System.out.println("maxWeight 颜色设置错误！");
            return null;
        }
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Spot> listSpot = new ArrayList<>();

        //遍历空位置，计算当前棋色下此点的权值
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                Spot spot = SpotsControl.getSpot(i, j);
                if (Spot.SPACE==spot.getColor()) {
                    int w = getWeight(spot, mColor);
                    list.add(w);
                    listSpot.add(spot);
                }
            }
        }

        if (list.size() < 1) {
            return null;
        }

        //找到最大下标
        int max = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(max) < list.get(i + 1)) {
                max = i + 1;
            }
        }

        return listSpot.get(max);
    }
}
