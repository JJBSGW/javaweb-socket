package javasocket_server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Server extends JFrame{
    private static final int PORT = 5000;
    private JPanel jp1 = new JPanel();
    private static Map<String , String> Users = new HashMap<>();
    private static Map<String , Socket> UserSockets = new HashMap<>();
    private static final Map<String, ArrayList<String>> history = new HashMap<>();
    private String delete_who = new String();
    private static int yes;
    //private static List<ClientHandler> clients = new ArrayList<>();
    
    
    public static void main(String[] args){
        new Server().startServer();
    }
    
    public void startServer() {
    	
    	JFrame frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);

        JPanel panel = new JPanel();
        JButton jb0 = new JButton("Delete Online Users");
        panel.add(jb0 ,  BorderLayout.CENTER);
        jb0.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	            	openNewWindow();
	            }
	        });
        JButton jb1 = new JButton("Send Message");
        panel.add(jb1 ,  BorderLayout.CENTER);
        
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                Socket clientsocket = serverSocket.accept();
                new ClientHandler(clientsocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void openNewWindow() {
    	JFrame newWindow = new JFrame("Delete");
        newWindow.setSize(400, 500);
        newWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newWindow.setVisible(true);
        
        jp1.setSize(400, 500);
        jp1.setBackground(Color.white);
        jp1.setLocation(0, 0);
        jp1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        newWindow.add(jp1);
        
        for (Entry<String, String> entry : Users.entrySet()) {
    		String name = entry.getKey();
    		SwingUtilities.invokeLater(() -> addPanelWithCenteredText(name));
    	}
        
        newWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow(newWindow); // 当窗口关闭时调用的方法
            }
        });
    }
    
    private void closeWindow(JFrame window) {
        // 从window中删除所有组件
        Component[] components = window.getContentPane().getComponents();
        for (Component component : components) {
            window.getContentPane().remove(component);
        }
        
        // 可选：如果需要完全清理窗口并允许JVM垃圾回收器回收窗口资源
        window.dispose();
    }
    
    public void addPanelWithCenteredText(String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout()); // 设置面板的布局管理器为BorderLayout

        // 创建一个JLabel并设置文本
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER); // 水平居中文本
        label.setVerticalAlignment(SwingConstants.CENTER); // 垂直居中文本
        
        panel.add(label, BorderLayout.CENTER);
        panel.setOpaque(true); // 使面板背景可见
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(300, 100)); // 设置面板的首选大小
        
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	int res = JOptionPane.showConfirmDialog(null, "Are you sure?","START",JOptionPane.YES_NO_OPTION);
            	if (res == JOptionPane.YES_OPTION) {
            		yes = 1;
            		delete_who = text;
            	}else {
            		
            	}
            }
        });

        Box box = null;
        for (Component comp : jp1.getComponents()) {
            if (comp instanceof Box) {
                box = (Box) comp;
                break;
            }
        }
        if (box == null) {
            box = Box.createVerticalBox(); // 创建一个新的 Box
            jp1.add(box, BorderLayout.CENTER); // 添加 Box 到 jp0 的中心位置
            jp1.revalidate(); // 重新验证以更新UI
        }	
        
        // 将JLabel添加到JPanel中
        //panel.add(label, BorderLayout.CENTER);

        // 将JPanel添加到MainFrame的主面板中
        //jp0.add(panel, BorderLayout.CENTER);
        box.add(panel);
        box.revalidate(); // 重新验证 Box 以更新UI
        jp1.revalidate(); // 重新验证以更新UI
    }
    
    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private BufferedReader in_1;
        private PrintWriter out_1;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                String message;
                String[] parts;
                String username;

               	out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
                message = in.readLine();
               	parts = message.split(";" , 2);
               	username = parts[0];
               	System.out.println(username + " joined the chat.");
                
               	Users.put(parts[0],parts[1]);
               	UserSockets.put(username, socket);
               	out.println("connected");
               	
               	out.flush();
               	//out.newLine();
               	
               	
               	//in.close();
               	//out.close();

                while (true) {
                	
                	//System.out.println(yes);
                	if (yes == 1) {
                   		yes = 0;
                   		//System.out.println("1");
                   		out.println("close;" + delete_who);
                   		String keyToRemove = delete_who;
                   		String removedValue = Users.remove(keyToRemove);
                   		if (removedValue != null) {
                   		    System.out.println("键值对已删除: " + keyToRemove + " -> " + removedValue);
                   		} else {
                   		    System.out.println("没有找到要删除的键: " + keyToRemove);
                   		}
                   	}
                	
                	message = in.readLine();
               		while (message == null) {
               			message = in.readLine();
               		}
               		String[] Parts = message.split(";");
               		System.out.println(Parts[0]);
               		switch (Parts[0]) {
               			case "private":p2psend(Parts[1] , Parts[2] , Parts[3]);break;
               			case "public":broadcast(Parts[1] , Parts[2] , Parts[3]);break;
               			case "self":p2psendself(Parts[1] , Parts[2] , Parts[3]);break;
               			case "getOnlineUsers":getonlineusers(Parts[1] , Parts[2] , Parts[3]);break;
               			case "gethistory":gethistory(Parts[1] , Parts[2] , Parts[3]);break;
               		}
                   	if (Parts[0].equals("break")) {
                   		break;
                   	}
               	}
                	
                	Users.remove(username);
                	UserSockets.remove(username);
                	System.out.println(username + " left the chat.");
                

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void sendMessage(String message , Socket socket , String sender ,int flag) throws IOException {
    	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    	//System.out.println(flag);
    	switch (flag) {
    		case 0:
    			out.println("Show_Message;" + "  <private message>  " + sender + " : " + message);out.flush();break;
    		case 1:
    			out.println("Show_Message;" + "  <public message>  " + sender + " : " + message);out.flush();break;
    		case 2:
    			out.println(message);out.flush();break;
    		case 3:
    			out.println(message);out.flush();break;
    		case 1000:
    			out.println(message);
    		default :
    			out.println();
    	}
    	
    }
    
    public void p2psend(String username , String message , String toname) throws IOException {
    	for (Map.Entry<String, Socket> entry : UserSockets.entrySet()) {
    		String name = entry.getKey();
    		Socket socket = entry.getValue();
    		if (name.equals(toname)) {
    			sendMessage(message , socket, username , 0);
    			return;
    		}
    	}
    }
    
    public void p2psendself(String username , String message , String toname) throws IOException {
    	for (Map.Entry<String, Socket> entry : UserSockets.entrySet()) {
    		String name = entry.getKey();
    		Socket socket = entry.getValue();
    		if (name.equals(username)) {
    			sendMessage(message , socket, username , 0);
    			return;
    		}
    	}
    }
    
    public void broadcast(String username , String message , String toname) throws IOException {
    	for (Map.Entry<String, Socket> entry : UserSockets.entrySet()) {
    		
    		String name = entry.getKey();
    		//System.out.println(username + ";"+ name);
    		Socket socket = entry.getValue();
    		//name.compareTo(sender) != 0
    		sendMessage(message , socket , username , 1);
    		addToWorldChannel("  <public message>  " + username + " : " + message);
    	}
    }
    
    public void getonlineusers(String username , String message , String toname)throws IOException {
    	for (Map.Entry<String, Socket> entry : UserSockets.entrySet()) {
    		
    		String name = entry.getKey();
    		//System.out.println(username + ";"+ name);
    		Socket socket = entry.getValue();
    		
    			ArrayList<String> parts = new ArrayList<>();
    			parts.add("ADD_PANEL;");
    			for (Map.Entry<String, Socket> entry_1 : UserSockets.entrySet()) {
    				String name_1 = entry_1.getKey();
    				parts.add(name_1 + ";");
    			}String result = String.join("", parts);
    			sendMessage(result , socket , username , 2);
    	}
    }
    public void gethistory(String username , String message , String toname)throws IOException {
    	for (Map.Entry<String, Socket> entry : UserSockets.entrySet()) {
    		String name = entry.getKey();
    		Socket socket = entry.getValue();
    		//System.out.println(name);
    		//System.out.println(username + ";");
    		if (name.equals(username)) {
    			
    			String result = worldChannelListToString(history);
    			//System.out.println(result);
    			sendMessage("get_history;" + result , socket, username , 3);
    			return;
    		}
    	}
    }
    
    public String worldChannelListToString(Map<String, ArrayList<String>> history) {
        // 定义WorldChannel键
        String worldChannelKey = "WorldChannel";

        // 检查Map中是否存在WorldChannel键
        if (history.containsKey(worldChannelKey)) {
            // 取出WorldChannel键对应的ArrayList
            ArrayList<String> worldChannelList = history.get(worldChannelKey);
            // 将ArrayList中的所有内容转换为字符串，元素之间用分隔符分隔
            return listToStringWithDelimiter(worldChannelList, "<");
        } else {
            // 如果Map中没有WorldChannel键，返回空字符串或相应的提示信息
            return "  welcome!\n";
        }
    }
    
    public String listToStringWithDelimiter(ArrayList<String> stringList, String delimiter) {
        if (stringList == null || stringList.isEmpty()) {
            return ""; // 如果列表为null或空，返回空字符串
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stringList.size(); i++) {
            stringBuilder.append(stringList.get(i));
            // 如果不是最后一个元素，添加分隔符
            if (i < stringList.size() - 1) {
                stringBuilder.append(delimiter);
            }
        }
        return stringBuilder.toString();
    }
    
    public void addToWorldChannel(String message) {
        // 定义WorldChannel键
        String worldChannelKey = "WorldChannel";
        
        // 检查"WorldChannel"键是否已存在
        if (history.containsKey(worldChannelKey)) {
            // 如果存在，获取现有的ArrayList
            ArrayList<String> existingList = history.get(worldChannelKey);
            // 向现有的ArrayList添加新消息
            existingList.add(message);
        } else {
            // 如果不存在，创建新的ArrayList，添加消息，并与WorldChannel键关联
            ArrayList<String> newList = new ArrayList<>();
            newList.add(message);
            history.put(worldChannelKey, newList);
        }
        /*
        // 打印WorldChannel键对应的ArrayList中的所有项（可选，用于调试）
        for (String item : history.get(worldChannelKey)) {
            System.out.println(item);
        }*/
    }
       
    }