import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;

public class client extends Thread {

	Socket sock;
	DataInputStream din;
	DataOutputStream dout;
	JFrame jf;
	JTextArea tt;
	JTextField t;
	JButton send;
	JButton file;
	JButton save;
	JScrollPane scroll;

	public client(Socket sock) throws UnknownHostException, IOException {

		this.sock = sock;
		
		this.dout = new DataOutputStream(this.sock.getOutputStream());
		this.din = new DataInputStream(this.sock.getInputStream());
		System.out.println("socket created");

	}

	public void make() {

		this.jf = new JFrame();
		this.jf.setTitle("jMessenger v1.03.269");

		this.tt = new JTextArea();
		this.tt.setLineWrap(true);
		this.tt.setEditable(false);
		this.jf.add(tt);

		this.scroll = new JScrollPane(this.tt);
		this.scroll.setBounds(10, 10, 365, 350);
		this.scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.jf.add(this.scroll);

		this.t = new JTextField();
		this.t.setBounds(10, 370, 365, 30);		
		this.jf.add(t);

		this.send = new JButton("send");
		this.send.setBounds(10, 410, 100, 40);
		this.send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String msg = t.getText();

				if (!msg.isEmpty()) {
					t.setText("");
					tt.append("YOU:	"+msg + "\n");

					try {

						dout.writeUTF(msg);
						System.out.println(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("message send success");
				}

			}
		});
		this.jf.add(send);

		this.file = new JButton("file");
		this.file.setBounds(140, 410, 100, 40);
		this.file.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser jfc = new JFileChooser();
				jfc.showOpenDialog(null);
				File file = jfc.getSelectedFile();
				tt.append("YOU:	<<<sending file>>>" + file.getName() + "\n");

				try {
					
					dout.writeUTF("gdfgdfg1"); 				// signal
					
					dout.writeUTF(file.getName());		 // filename
					System.out.println("send:  " + "file name	" + file.getName());
					
					long s = file.length();
					String size = Long.toString(s);					
					dout.writeUTF(size); 				// fileSize
					System.out.println("send:  " + "filesize	" + s);
					
					FileInputStream fin = new FileInputStream(file);
					long l = (long)fin.available();
					System.out.println(l);
					byte[] buffer = new byte[(64*1024)];
					
					int i =0,j=0;
					
					while ((i = fin.read(buffer))!= -1){						
						dout.write(buffer,0,i);//											
					}					
					fin.close();				
					tt.append("YOU:	<<<sending file Successful>>>" + file.getName() + "\n");

				} 
				
				catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

		this.jf.add(file);

		this.save = new JButton("Save");
		this.save.setBounds(275, 410, 100, 40);

		this.jf.add(save);

		this.jf.setLayout(null);
		this.jf.setSize(400, 500);
		this.jf.setDefaultCloseOperation(this.jf.EXIT_ON_CLOSE);
		this.jf.setVisible(true);
	}

	public static void main(String[] args) throws UnknownHostException, IOException {

		Socket socket = new Socket("127.0.0.1", 9999);

		client c = new client(socket);
		c.make();
		Thread t1 = new Thread(c);
		t1.start();

	}

	public void run() {
		System.out.println("receiving intiated");
		while (true) {		
			try {
				System.out.println("in the beginning of recieving" + this.din);	
				
				String msg = this.din.readUTF();
				System.out.println("signal     " +  "   " + msg);
				
				String check = "gdfgdfg1";

				if (msg.equals(check)) {
				
						String filename = din.readUTF();				
						System.out.println(filename);	
						
						tt.append("Received:	" + "File recieving " + filename +"\n" );
												
						File file = new File(filename);				
						FileOutputStream fout = new FileOutputStream(file);
												
						String size = din.readUTF();
						long s = Long.parseLong(size);
						//System.out.println("size of the file is " + s);
												
						byte[] buffer = new byte[64*1024];
												
						System.out.println("created file");						
						int l = 0;	
						int j =0;
						while(file.length() != s){							
							l = din.read(buffer);							
							fout.write(buffer,0,l);
//							fout.flush();							
//							if(file.length() >= s){								
//								System.out.println("                     " + file.length());
//								break;
//							}								
						}						
						fout.close();
						System.out.println(j);
						tt.append("Receiver:	" + "File recieved " + filename +"\n" );
				}				
				else {
					tt.append("Receiver:	" + msg + "\n");
					tt.setCaretPosition(tt.getDocument().getLength());					
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
