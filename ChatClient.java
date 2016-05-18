package com.page;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends Frame{
	
	private TextField tf=new TextField();
	private TextArea  ta=new TextArea();
	private Socket s=null;
	private DataOutputStream dos=null;
	private DataInputStream dis=null;
	private volatile boolean bConnected=false;
	
	private Thread gThread=new Thread(new GetData());

	public static void main(String[] args) {
        new ChatClient().launchChat();
	}
	
	public void launchChat(){
		this.setBounds(300,300,600,300);
		
		this.add(tf,BorderLayout.SOUTH);
		this.add(ta,BorderLayout.NORTH);
		this.pack();
		this.addWindowListener(new WindowAdapter(){

			@Override
			public void windowClosing(WindowEvent e) {
System.out.println(" close the window");				
				disConnect();
				System.exit(0);
			}
			
		});
		tf.addActionListener(new TFListener());
		this.setVisible(true);
		connect();
		
		gThread.start();
	}
	
	public void connect(){
		try {
			s=new Socket("192.168.0.116",9999);
			dos=new DataOutputStream(s.getOutputStream());
			dis=new DataInputStream(s.getInputStream());
System.out.println(" connecting  --client");	
            bConnected=true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	private class TFListener implements ActionListener{
	
		@Override
		public void actionPerformed(ActionEvent e) {
			String str = tf.getText();		
			tf.setText("");			
			try {
				dos.writeUTF(str);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}
				
	}
	
	public void disConnect(){
		System.out.println("now is the time to closing ");
		try {
			bConnected=false;
System.out.println(bConnected);
			//gThread.join();	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				dos.close();
				dis.close();
				s.close();
System.out.println(" a client is closed ---client");				
			}catch (IOException e) {
				e.printStackTrace();
			}			
		}
    }
	
    private class GetData implements Runnable{
        
		@Override
		public void run() {
			try { 
				 while(bConnected){
					 String str = dis.readUTF();
					 ta.setText(ta.getText() + str +'\n');
				 }
			}catch (SocketException e){
				System.out.println("我退出了");
				//System.exit(0);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
