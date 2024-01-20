package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;


public class MySocket {
	public static Socket socket;
    public static ServerSocket server;
    public final static int port = 8090;

    public static boolean isStart = false;
    // 对方IP地址
    public static String peAddress;

    // 服务器端等待连接
    public static void serverConnect() throws IOException {
    	// 创建服务器端套接字接收连接请求
        server = new ServerSocket(port);
        MySocket.socket = server.accept();
        startGetData();// 开始接收数据
        isStart = true;
        peAddress = socket.getInetAddress().getHostAddress();
    }

    // 客户端连接
    public static void clientConnect(final String address) throws Exception {
        // 创建客户端套接字（连接到服务器端的IP地址）
    	socket = new Socket(address,port);
        startGetData();//成功加入房间，准备接收数据
        isStart = true;
        peAddress = socket.getInetAddress().getHostAddress();
    }

    // 发送数据
    public static void sendData(final Object object) {
    	System.out.println("send了");
        new Thread(() -> {
            try {
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                os.writeObject(object);// 对象写入输出流中
            } catch (Exception e) {
            	//e.printStackTrace();
                System.out.println("putData()数据发送异常！终止连接");
                isStart = false;
            }
        }).start();
    }

    // 接收数据
    public static void startGetData() {
        new Thread(() -> {
            while (true) {
                try {
                	// 获取输出流
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    Object object = in.readObject();
                    if (object instanceof List) {
                        @SuppressWarnings("unchecked")
						List<String> list = (List<String>) object;
                        MessageWork.receive(list);// 对接收的进行处理
                    }
                } catch (Exception e) {
                    System.out.println("getData()数据接收异常，终止连接");
                    isStart = false;//接收数据异常也会把isStart置为false
                    return;
                }
            }
        }).start();
    }

    public static void close() {
        try {
            server.close();
            socket.close();
            isStart = false;
        } catch (Exception e) {
        	System.out.println("wrong in closing socket");
        }
    }
}
