import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class server extends Thread{
	public static ArrayList<Socket> client = new ArrayList<Socket>();	
	
	Socket sock;
	DataInputStream din;
	DataOutputStream dout;

	private static ServerSocket server;
	
	public server(Socket sock){		
		this.sock = sock;
		client.add(this.sock);
		System.out.println(this.sock + "         is added");
	}
		
	public void run(){
		try {
			
		this.din = new DataInputStream(this.sock.getInputStream());
		
		byte[] buffer = new byte[(64*1024)];
		int l=0,j=0;		
			while(true){
			l=din.read(buffer);
//			j+=1;	
//			System.out.println(l);
			
				for (Socket socket : client) {
					if (socket == this.sock){
						continue;
					}
				dout = new DataOutputStream(socket.getOutputStream());
				dout.write(buffer,0,l);
				}
//				dout.flush();
			}
		}catch(SocketException e1){
			client.remove(this.sock);
			System.out.println( this.sock + "  is REMOVED ");
		}catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	public static void main(String[] args) throws IOException {
		
		server = new ServerSocket(9999);
		
		while(true){
			System.out.println("waiting.............");
			Socket sock = server.accept();			
			server s1 = new server(sock);
			Thread t1 = new Thread(s1);
			t1.start();			
		}
		
	}
}
