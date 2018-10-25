package ftp;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.util.Random;

public class ClientThread extends Thread{

	private Socket s;
    private String name;
    private boolean isLogin;
	private int hport;
	private int lport;
	private BufferedReader  reader ;
	private PrintWriter writer ;
	private File catalog = new File("E:/eclipse/projects/network/");
	private String add = "E:/eclipse/projects/network/";
	private Socket data;
	private DataInputStream  dataReader ;
	private DataOutputStream dataWriter ;
	private boolean flag = false;
	
	public ClientThread(Socket s) {
		// TODO Auto-generated constructor stub
		this.s = s;
		try {
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void run(){
			try {
				while(!flag){
				String comd = reader.readLine();
				System.out.println("comd arrive service "+comd);
				//System.out.println("service");
				if(comd.startsWith("USER")){
					   login(comd); 
				}else if(comd.startsWith("PASS ")){
					//System.out.println("in pass");
					pass(comd);
				}else if(comd.startsWith("PASV")){
					//System.out.println("service");
					pasv();
				}else if(comd.startsWith("STOR")){
					stor(comd);
				}else if(comd.startsWith("RETR")){
					retr(comd);
				}else if(comd.startsWith("QUIT")){
					quit();
				}else if(comd.startsWith("CWD")){
					cwd(comd);
				}else if(comd.startsWith("LIST")){
					list();
				}else{
					writer.println("no this function");
					writer.flush();
				}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
		}
      }
	
	
	private void list() {
		// TODO Auto-generated method stub
		if(isLogin){
		if(!catalog.isDirectory()){
			writer.println("-1");
			writer.flush();
		}else{
			writer.println("0");
			writer.flush();
			File[] list = catalog.listFiles();
			writer.println(list.length);
			writer.flush();
			for(int i = 0;i<list.length;i++){
				String t = ""+list[i].getName()+" "+list[i].length()+"  "+new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(list[i].lastModified()));
				System.out.println(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(list[i].lastModified())));
				writer.println(t);
				writer.flush();
			}	
		}
		}else{
			writer.println("1");
			writer.flush();
		}
	}

	private void cwd(String comd) {
		// TODO Auto-generated method stub
		if(isLogin) {
			
		String[] s = comd.split(" ");
		File f = new File(add+s[1]);
		if(f.isDirectory()) {
			catalog =f;
			add=f.getAbsolutePath();
			writer.println("0");
			writer.flush();
		}else {
			writer.println("1");
			writer.flush();
		}
		
		}else {
			writer.println("2");
			writer.flush();
		}
	}

	private void quit() {
		// TODO Auto-generated method stub
		if(isLogin) {
		writer.println("0");
		writer.flush();
		try {
			data.close();
			s.close();
			flag=true;
			this.interrupt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}else {
			writer.println("-1");
			writer.flush();
		}
	}

	//下载
	private void retr(String comd) {
		if(isLogin) {
		String[] s=comd.split(" ");
		File f=new File(add+s[1]);
		if(f.exists()){
			if(f.isFile()){
				writer.println("0");
				writer.flush();
				try {
					File ff = new File(add+s[1]);
					writer.println(ff.length());
					writer.flush();
					 DataInputStream in = new DataInputStream(new FileInputStream(add+s[1]));
					for(int i=0;i<ff.length();i++){
					     dataWriter.writeByte(in.readByte());
					     dataWriter.flush();
				   }
				    in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}else if(f.isDirectory()) {
				writer.println("1");
				writer.flush();
				File[] list = f.listFiles();
				int len =list.length;
				writer.println(len);
				writer.flush();
				String tname = add+s[1]+"\\";
				for(int k=0;k<len;k++) {
					try {
						writer.println(list[k].getName());
						writer.flush();
						File ff = new File(tname+list[k].getName());//add+list[k].getName()
						writer.println(list[k].length());
						writer.flush();
						 DataInputStream in = new DataInputStream(new FileInputStream(tname+list[k].getName()));
						for(int i=0;i<list[k].length();i++){
						     dataWriter.writeByte(in.readByte());
						     dataWriter.flush();
					   }
					    in.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
			}else{
				writer.println("-5");
				writer.flush();
			}
		}else{//file not exit
			writer.println("-3");
			writer.flush();
		}
		}else {
			writer.println("-2");
			writer.flush();
		}
	}
   //上传
	private void stor(String comd) {
	   if(isLogin) {
		String[] s = comd.split(" ");
		String[] ss = s[1].split("/");
			try {
				
			int fs = Integer.parseInt(reader.readLine());
			if(fs==1) {
			FileOutputStream out = new FileOutputStream(add+"\\"+ss[ss.length-1]);
			writer.println("0");
			writer.flush();
			byte b ;
			long o = Long.parseLong(reader.readLine());
			for(int i=0;i<o;i++){
				b = dataReader.readByte();
					out.write(b);
					out.flush();
			}
			System.out.println("finish"+o+catalog.getAbsolutePath()+ss[ss.length-1]);
			out.close();
			}else if(fs==2) {
			
			String[] nadd = ss[ss.length-1].split(":");
			String tadd = add+"\\"+nadd[nadd.length-1];
			File jia = new File(tadd);
			if(jia.exists()||jia.mkdirs()) {
		    int l =	Integer.parseInt(reader.readLine());
			String tname =null;
			FileOutputStream out=null;
			for(int k=0;k<l;k++) {
				    tname = reader.readLine();
				    out = new FileOutputStream(tadd+"\\"+tname);
					byte b ;
					long o = Long.parseLong(reader.readLine());
					writer.println("0");
					writer.flush();
					for(int i=0;i<o;i++){
						    b = dataReader.readByte();
							out.write(b);
							out.flush();
							
					}//一个文件的写入
					out.close();
			}//遍历每个文件	
			
			
			       }else {//文件夹创建失败
			    	   
			       }
		}//文件夹
			
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}else{
		writer.println("Service : login first");
		writer.flush();
	}
}

	

	private void pasv() {
		// TODO Auto-generated method stub
		
		if(isLogin){
			
			
			try {
				ServerSocket ss = new ServerSocket(0);
				int port = ss.getLocalPort();
				writer.println("227 Entering Passive Mode (127,0,0,1," + (int)port/256 + "," + (int)port%256 + ")");
				writer.flush();
				data = ss.accept();
				System.out.println("has accept");
				
				dataReader = new DataInputStream(data.getInputStream());
				dataWriter = new DataOutputStream(data.getOutputStream());
				writer.println("Service :dataLink is ok");
				writer.flush();
			} catch (IOException e) {
				try {
					reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
					writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			writer.println("Service : login first");
			writer.flush();
		}
	}

	private void pass(String comd) {
		if(name!=null) {
		String[] s = comd.split(" ");
		if(name!=null&&name.equals("123")&&s[1].equals("123")){
			isLogin=true;
			writer.println("service :login successful ");
			writer.flush();
		}else{
			writer.println("pass error");
			writer.flush();
		}
		}else {
			writer.println("input name first");
			writer.flush();
		}
	}

	private void login(String comd) {
		// TODO Auto-generated method stub
		//System.out.println("come in login");
		String[] s = comd.split(" ");
		name=s[1];
		writer.println("service : name "+name);
		writer.flush();
	}
	
	
	
	
	}


