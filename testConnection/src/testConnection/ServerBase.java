package testConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Room{
	int gameRecogPort = 0;
	
	Room(int gameRecogPort){
		this.gameRecogPort = gameRecogPort;
		return;
	}
	
}

public class ServerBase {
	
	static int RecogPortNext = 0;
	static List<Room> rooms = new ArrayList<>();
	
	static int RecogPortIncre() {
		RecogPortNext++;
		return RecogPortNext-1;
	}
	
	static void MakeNewGame() {
		rooms.add(new Room(RecogPortIncre()));
	}
	
	static boolean SEND_LOCKED = false;
	
	static boolean needSend = false;
	static String stringToBeSended;
	static InetAddress addressToSend;
	
	public static void main(String[] args) {
		try {
			System.out.println("Server trying to start...");
			
			//TCP
			@SuppressWarnings("resource")
			ServerSocket tcpSock = new ServerSocket(1235);
			Socket tcpClientSock = new Socket();
			BufferedReader tcpReader;
			BufferedWriter tcpWriter;
			//final String MULTICAST_IP = "239.0.0.1";
			//final int MULTICAST_PORT = 4000;
			@SuppressWarnings("resource")
			DatagramSocket udpSock = new DatagramSocket(4000);
			System.out.println("Port is " + udpSock.getPort());
			
			System.out.println("Server is receiving.");
			
			//멀티 스레드 고정 설정
			ExecutorService executorService = Executors.newFixedThreadPool(3);
			
			// UDP receiver
			executorService.execute(()->{
				try {
					byte[] recvBuf = new byte[256];
					DatagramPacket udpRecvPack = new DatagramPacket(recvBuf, recvBuf.length);
					while(true) {
						udpSock.receive(udpRecvPack);
						String recvMessage = new String(udpRecvPack.getData()).trim();
						if(recvMessage.equals("ConnectionTest") && SEND_LOCKED == false) {
							System.out.println("UDP received Connection Test " + udpRecvPack.getAddress());
							addressToSend = udpRecvPack.getAddress();
							stringToBeSended = "Connection Good";
							needSend = true;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			// UDP sender
			executorService.execute(()->{
				try {
					//System.out.println("0 came");
					byte[] sendBuf = new byte[1];
					DatagramPacket udpSendPack = new DatagramPacket(sendBuf, sendBuf.length);
					udpSendPack.setPort(4001);
					while(true){
						Thread.sleep(10);
						//System.out.println("Looping");
						if(needSend == true) {
							//System.out.println("1 came");
							if(addressToSend != null && stringToBeSended != null) {
								sendBuf = stringToBeSended.getBytes();
								udpSendPack.setAddress(addressToSend);
								udpSendPack.setData(sendBuf);
								//System.out.println(new String(sendBuf) + "108");
								if(SEND_LOCKED == false) {
									//System.out.println("Sended");
									udpSock.send(udpSendPack);
								}
								needSend = false;
							}
						}
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
				else if(saver.equals("MakeGame")) {
					System.out.println("TCP received Make Game");
					MakeNewGame();
					System.out.println("New Room Init RecogPort is " + (RecogPortNext-1));
					tcpWriter.write("Game Init RecogPort is " + (RecogPortNext-1)); tcpWriter.flush();
				}
				else {
					System.out.println("? " + saver + tcpClientSock.getInetAddress());
					tcpWriter.write("Unknown Request."); tcpWriter.newLine(); tcpWriter.flush();
				}
				tcpReader.close();
				tcpWriter.close();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
