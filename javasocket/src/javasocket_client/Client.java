package javasocket_client;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.*;

public class Client{
	public static class UI extends JFrame{
		//聊天对象和对应的聊天记录
		private static final Map<String, ArrayList<String>> history = new HashMap<>();
		private JPanel jp0 = new JPanel();
		private JPanel jp00 = new JPanel();
		private JPanel jp11 = new JPanel();
		private JTextArea textArea = new JTextArea();
		private String Now_TO = new String();
		
		public UI(Socket socket) throws IOException {
			setTitle("Online");
			setSize(1000 , 500);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			login(socket);
			//initUI(socket);
		}
		
		private void login(Socket socket) throws IOException{
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			jp00.setLayout(null);
			this.add(jp00);
			
			JLabel jl0 = new JLabel("Please input your name");
			jl0.setSize(200, 50);
			jl0.setLocation(400, 0);
			jl0.setForeground(Color.black);
			jl0.setOpaque(true);
			jl0.setBackground(Color.white);
			jl0.setHorizontalAlignment(JLabel.CENTER);
			jp00.add(jl0);
			
			JTextField jt0 = new JTextField();
			jt0.setSize(400, 50);
			jt0.setLocation(300, 100);
			jt0.requestFocus();
			jp00.add(jt0);
			
			JLabel jl1 = new JLabel("Username: ");
			jl1.setSize(100, 50);
			jl1.setLocation(200, 100);
			
			jp00.add(jl1);
			
			JTextField jt1 = new JTextField();
			jt1.setSize(400, 50);
			jt1.setLocation(300, 200);
			jt1.requestFocus();
			jp00.add(jt1);

			JLabel jl2 = new JLabel("Password: ");
			jl2.setSize(100, 50);
			jl2.setLocation(200, 200);
			jp00.add(jl2);
			
			JButton jb0 = new JButton("Sign in");
			jb0.setSize(100, 50);
			jb0.setLocation(450, 300);
			jp00.add(jb0);
			
			jb0.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Username = jt0.getText();
					Password = jt1.getText();
					out.println(Username + ";" + Password);
					dispose();
					try {
						initUI(socket , in , out);
					} catch (IOException e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
					}
				}
			});

		}

		private void initUI(Socket socket , BufferedReader in , PrintWriter out) throws IOException {
			Now_TO = null;
			
			JFrame newWindow = new JFrame("Online");
	        newWindow.setSize(400, 500);
	        newWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        newWindow.setVisible(true);
			
			startServerListenerThread(socket , in , out);
			
	        jp0.setLayout(new BorderLayout()); // 使用BorderLayout作为布局管理器

	        jp11.setLayout(new BorderLayout());
	        jp11.setPreferredSize(new Dimension(50, 50)); // 设置首选大小
	        jp11.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	        jp11.setBackground(Color.WHITE);
	        
	        JLabel jp1_1 = new JLabel("World Channel");
	        jp1_1.setHorizontalAlignment(SwingConstants.CENTER); // 水平居中文本
	        jp1_1.setVerticalAlignment(SwingConstants.CENTER); // 垂直居中文本
	        jp11.add(jp1_1 , BorderLayout.CENTER);
	        jp0.add(jp11, BorderLayout.NORTH); // 添加到中心位置
	        
	        jp11.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	            	if (Now_TO == null) {
	            		Now_TO = "World Channel";
	            		if (SwingUtilities.isLeftMouseButton(e)) {
	                    openNewWindow("World Channel" , socket , in , out);
	            		}
	            	}else {
	            		JOptionPane.showMessageDialog(null, "您已经打开了一个对话框，请先关闭对话框");
	            	}
	                
	            }
	        });
	        
	        JButton jb1 = new JButton("Refresh");
	        jp0.add(jb1 , BorderLayout.SOUTH);
	        
	        jb1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					commandControl(socket ,in ,out , "refresh" , Username + ";" + "getOnlineUsers"+  ";" + "online");
				}
			});
	        
	        newWindow.add(jp0);
	        //pack(); // 自动调整窗口大小以适应其子组件的preferredSize
	    }
		
		
		private void openNewWindow(String title , Socket socket , BufferedReader in , PrintWriter out) {
	        // 创建并显示新窗口
	        JFrame newWindow = new JFrame(title);
	        newWindow.setSize(800, 500);
	        newWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        newWindow.setVisible(true);
	        
	        JPanel jp1 = new JPanel();
	        jp1.setSize(800, 300);
	        jp1.setBackground(Color.white);
	        jp1.setLocation(0, 0);
	        jp1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	        newWindow.add(jp1);
	        
	        // 创建JTextArea用于显示服务器信息
	        textArea.setEditable(false); // 设置为不可编辑
	        textArea.setLineWrap(true); // 设置自动换行
	        textArea.setWrapStyleWord(true); // 设置单词换行
	        JScrollPane scrollPane = new JScrollPane(textArea);
	        scrollPane.setPreferredSize(new Dimension(800, 290)); // 设置滚动面板的首选大小
	        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

	        jp1.add(scrollPane, BorderLayout.NORTH); // 将JTextArea添加到JPanel jp1中
	        
	        JPanel jp2 = new JPanel();
	        jp2.setLayout(new BorderLayout());
	        jp2.setSize(800, 200);
	        jp2.setBackground(Color.white);
	        jp2.setLocation(0, 0);
	        jp2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	        newWindow.add(jp2 , BorderLayout.SOUTH);
	        
	        JTextField textField = new JTextField();
	        textField.setPreferredSize(new Dimension(800 , 200)); // 设置期望的宽度和高度
	        // 将JTextField添加到JPanel jp2的北部
	        jp2.add(textField, BorderLayout.NORTH);

	        
	        JButton jb1 = new JButton("send");
	        jp2.add(jb1 , BorderLayout.SOUTH);
	        
	        jb1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (title.equals("World Channel")) {
						String message = textField.getText();//message组成<消息格式> + 发送者 + 内容
						//System.out.println(message);
						message ="public>" + Username + ";" + message + ";" + title;//parts[1] , parts[2] , parts[3]
						commandControl(socket ,in ,out , "send" , message);
					}
					else {
						String message = textField.getText();//message组成<消息格式> + 发送者 + 内容
						//System.out.println(message);
						message ="private>" + Username + ";" + message + ";" + title;//parts[1] , parts[2] , parts[3]
						commandControl(socket ,in ,out , "send" , message);
					}
				}
			});
	        
	        newWindow.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	                Now_TO = null;
	                textArea.setText("");
	                newWindow.dispose(); // 调用dispose来关闭窗口
	            }
	        });
	        if (title.equals("World Channel")){
	        	commandControl(socket ,in ,out , "get_history" , "get_history>" + Username + ";" + "not important" + ";" + title);
	        	//System.out.println("get_history>" + ";" + Username + ";" + "not important" + ";" + title);
	        	//appendHistoryToTextAreaWorld(title);
	        }else {
	        	appendHistoryToTextArea(title);
	        }
	        
	    }
		
		public void commandControl(Socket socket , BufferedReader in , PrintWriter out , String command , String message) {
			switch (command) {
				case "send" :send(socket , in , out , message);break;
				case "refresh" : getOnlineUsers(socket , in , out , message);break;
				case "get_history" :send(socket , in , out , message);break;
			}
		}
		
		private void getOnlineUsers(Socket socket, BufferedReader in, PrintWriter out, String message) {
			out.println("getOnlineUsers;" + message);
		}

		public void send(Socket socket , BufferedReader in , PrintWriter out , String message) {
			String[] parts = message.split(">");
			switch (parts[0]) {
			case "public" :out.println("public" + ";" + parts[1]);break;
			case "private" : out.println("private" + ";" + parts[1]);out.println("self" + ";" + parts[1]);break;
			case "get_history" :out.println("gethistory" + ";" + parts[1]);System.out.println(parts[1]);break;
			}
		}
		
		private void startServerListenerThread(Socket socket , BufferedReader in , PrintWriter out) {
	        new Thread(() -> {
	            try {
	                while (!Thread.currentThread().isInterrupted()) {
	                    String[] command = receiveServerCommand(socket , in , out); // 模拟从服务器接收命令
	                    if ("ADD_PANEL".equals(command[0])) {
	                    	for (int i= 1 ;i < command.length ;i++) {
	                    		String now = command[i];
	                    		if (now.equals(Username)) {}
	                    		else {
	                    			SwingUtilities.invokeLater(() -> addPanelWithCenteredText(now , socket , in , out));
	                    		}
	                    		
	                    	}
	                    }
	                    else if ("Show_Message".equals(command[0])) {
	                    	SwingUtilities.invokeLater(() -> showMessageOnTextArea(command[1]));
	                    	StoreMessage(command[1]);
	                    }
	                    else if ("get_history".equals(command[0])) {
	                    	String[] parts = command[1].split("<");
	                    	for (int i=1 ; i< parts.length ; i+=4) {
	                    		int now = i;
	                    		//System.out.println(parts[i]);
	                    		SwingUtilities.invokeLater(() -> showMessageOnTextArea("   <" + parts[now]));
	                    	}
	                    	//System.out.println(command[1]);
	                    }else if ("close".equals(command[0])) {
	                    	System.out.println("1");
	                    	if (command[1].equals(command[1])) {
	                    		//System.out.println("2");
	                    		in.close();
	                    		out.close();
	                    		socket.close();
	                    		System.exit(0);
	                    	}
	                    }
	                    
	                    Thread.sleep(1000); // 模拟网络延迟
	                }
	            } catch (InterruptedException | IOException e) {
	                Thread.currentThread().interrupt();
	            }
	        }).start();
	    }

	    // 模拟从服务器接收命令的方法
	    private String[] receiveServerCommand(Socket socket , BufferedReader in , PrintWriter out) throws IOException {
	        String message = in.readLine();
	        String[] parts = message.split(";");//先是命令，再是内容
	        System.out.println(parts[0]);
	        return parts;
	    }
		
		public void addPanelWithCenteredText(String text , Socket socket , BufferedReader in , PrintWriter out) {
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
	        panel.setPreferredSize(new Dimension(200, 100)); // 设置面板的首选大小
	        
	        panel.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	            	if (Now_TO == null) {
	            		Now_TO = text;
	            		if (SwingUtilities.isLeftMouseButton(e)) {
	                    openNewWindow(text , socket , in , out);
	            		}
	            	}else {
	            		JOptionPane.showMessageDialog(null, "您已经打开了一个对话框，请先关闭对话框");
	            	}
	            }
	        });

	        Box box = null;
	        for (Component comp : jp0.getComponents()) {
	            if (comp instanceof Box) {
	                box = (Box) comp;
	                break;
	            }
	        }
	        if (box == null) {
	            box = Box.createVerticalBox(); // 创建一个新的 Box
	            jp0.add(box, BorderLayout.CENTER); // 添加 Box 到 jp0 的中心位置
	            // 确保 "World Channel" 面板 (jp1) 位于顶部
	            jp0.add(jp11, BorderLayout.NORTH);
	            jp0.revalidate(); // 重新验证以更新UI
	        }	
	        
	        // 将JLabel添加到JPanel中
	        //panel.add(label, BorderLayout.CENTER);

	        // 将JPanel添加到MainFrame的主面板中
	        //jp0.add(panel, BorderLayout.CENTER);
	        box.add(panel);
	        box.revalidate(); // 重新验证 Box 以更新UI
	        jp0.revalidate(); // 重新验证以更新UI
	    }
		
		public void StoreMessage(String text) {
			if (Now_TO.equals("World Channel")) {}
			else {
				ArrayList<String> list = new ArrayList<>();
			
		    list.add(text + "\n");

		    // 检查Now_TO键是否已存在
		    if (history.containsKey(Now_TO)) {
		        // 如果存在，获取现有的ArrayList并追加新的消息
		        ArrayList<String> existingList = history.get(Now_TO);
		        existingList.addAll(list);
		    } else {
		    	System.out.println("1");
		        // 如果不存在，将新创建的ArrayList添加到Map中
		        history.put(Now_TO, list);
		    }
		    /*
		    for (String item : history.get(Now_TO)) {
		        System.out.println(item);
		    }*/
			}
			
		}
		
		public void showMessageOnTextArea(String text) {
			textArea.append(text + "\n");
		}
		
		private void appendHistoryToTextArea(String title) {
	        // 遍历Map中的每个键值对
	        for (Map.Entry<String, ArrayList<String>> entry : history.entrySet()) {
	            String key = entry.getKey();
	            ArrayList<String> valueList = entry.getValue();
	            
	            if (key.equals(title)) {
	            	 // 遍历ArrayList，将每个元素追加到文本区域
	            	for (String item : valueList) {
	            		textArea.append(item);
	            	}
	            }
	           
	        }
	    }
		
		public void addToWorldChannel(String content) {
			ArrayList<String> list = new ArrayList<>();
			
		    list.add(content + "\n");

		    // 检查Now_TO键是否已存在
		    if (history.containsKey("World Channel")) {
		        // 如果存在，获取现有的ArrayList并追加新的消息
		        ArrayList<String> existingList = history.get("World Channel");
		        existingList.addAll(list);
		    } else {
		    	//System.out.println("1");
		        // 如果不存在，将新创建的ArrayList添加到Map中
		        history.put("World Channel", list);
		    }
		    /*
		    for (String item : history.get(Now_TO)) {
		        System.out.println(item);
		    }*/
		}
}
		

	private static final int PORT = 5000;
	private static String Username;
	private static String Password;
	public static String[] Chat_history;
	
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("localhost" , PORT);
			SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	                try {
						new UI(socket).setVisible(true);
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
	            }
	        });
		} catch (UnknownHostException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}