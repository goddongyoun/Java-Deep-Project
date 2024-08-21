package testConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerBase {

	public static void main(String[] args) {
		try {
			System.out.println("Server trying to start...");
			
			//TCP
			ServerSocket tcpSock = new ServerSocket(1235);
			Socket tcpClientSock = new Socket();
			BufferedReader tcpReader;
			BufferedWriter tcpWriter;
			final String MULTICAST_IP = "239.0.0.1";
			final int MULTICAST_PORT = 4000;
			
			
			System.out.println("Server is receiving.");
			
			//멀티 스레드 고정 설정
			ExecutorService executorService = Executors.newFixedThreadPool(3);
			
			//UDP multicast
			executorService.execute(()->{
				try {
					byte[] sendBuf = new byte[256];
					MulticastSocket ms = new MulticastSocket(MULTICAST_PORT);
					ms.setTimeToLive(255);
					System.out.println("TTL is " + ms.getTimeToLive() + " Is Closed = " + ms.isClosed());
					InetAddress addr = InetAddress.getByName(MULTICAST_IP);
					DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr, 4000);
					while(true) {
						sendBuf = "Send".getBytes();
						sendPacket.setData(sendBuf);
						ms.send(sendPacket);
						//System.out.println(new String(sendBuf) + " Sended");
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			//TCP receiver
			while(true) {
				tcpClientSock = tcpSock.accept();
				tcpReader = new BufferedReader(new InputStreamReader(tcpClientSock.getInputStream()));
				tcpWriter = new BufferedWriter(new OutputStreamWriter(tcpClientSock.getOutputStream()));
				String saver = tcpReader.readLine();
				if(saver.equals("ConnectionTest")) {
					System.out.println("TCP received Connection Test " + tcpClientSock.getInetAddress());
					tcpWriter.write("Connection Good"); tcpWriter.newLine(); tcpWriter.flush();
				}
				else {
					System.out.println("? " + saver + tcpClientSock.getInetAddress());
					tcpWriter.write("Unknown Request."); tcpWriter.newLine(); tcpWriter.flush();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
