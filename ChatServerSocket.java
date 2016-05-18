package com.socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerSocket {
	private Socket s=null;
	private DataInputStream dis=null;
	private ServerSocket ss=null;
	private List<DataThread> clients=new ArrayList<DataThread>();
	
	public static void main(String[] args) {
            new ChatServerSocket().startServer();
	}

	public void startServer(){
		boolean flag=false;
		try {
			ss=new ServerSocket(9999);
			flag=true;
			while(flag){
				s=ss.accept();
System.out.println("a client connected!");	
                DataThread dt=new DataThread(s);
                new Thread(dt).start(); 
                clients.add(dt);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class DataThread implements Runnable{
		boolean connected =false;
		DataInputStream dis;
		DataOutputStream dos;
		Socket s;
		DataThread(Socket s){
			this.s=s;
            try {
				dis=new DataInputStream(s.getInputStream());
				dos=new DataOutputStream(s.getOutputStream());
				connected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String str){
				try {
					dos.writeUTF(str);
					dos.flush();
				} catch (IOException e) {
					clients.remove(this);
					System.out.println("一个客户端已经退出了 ！");
				}
		}
		
		@Override
		public void run() {
	    		try {
	    			while(connected){ 
		    			String str=dis.readUTF();
System.out.println(str);
	                    for(int i=0;i<clients.size();i++){
	                    	DataThread dt=clients.get(i);
	                    	dt.send(str);
	                    }
	    			}  
				}catch (SocketException e1){
System.out.println("a client closed --server");
					System.exit(0);
				} catch(EOFException e){
					System.out.println("a client closed --server");
				}
				catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						if(dis != null) dis.close();
						if(dos != null) dos.close();
						if(s != null) s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}					
				}
	        
		}
		
	}

	
}
