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
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.*;

class Room{
	int gameRecogPort = 0;
	
	Room(int gameRecogPort){
		this.gameRecogPort = gameRecogPort;
		return;
	}
	
}

class SysoutColors{
	public static final String RESET = "\033[0m";  // 색상 리셋
	public static final String RED = "\033[31m";   // 빨간색
	public static final String GREEN = "\033[32m"; // 초록색
	public static final String YELLOW = "\033[33m"; // 노란색
}

class Clients implements Runnable{
	Socket sock;
	int isSender = -1;
	int RecogPort = 0;
	
	Clients(Socket _sock){
		sock = _sock;
	}
	
	@Override
	public void run() {
		BufferedReader tcpReader;
		BufferedWriter tcpWriter;
		try {
			tcpReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			tcpWriter = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			String saver;
			while((saver = tcpReader.readLine()) != null) {
				if(saver.equals("ConnectionTest")) {
					System.out.println("TCP received Connection Test " + sock.getInetAddress());
					tcpWriter.write("Connection Good"); tcpWriter.newLine(); tcpWriter.flush();
				}
				else if(saver.equals("MakeGame")) {
					System.out.println("TCP received Make Game");
					RecogPort = ServerBase.MakeNewGame();
					System.out.println("New Room Init RecogPort is " + (RecogPort-1));
					tcpWriter.write("Game Init RecogPort is " + (RecogPort-1)); tcpWriter.newLine(); tcpWriter.flush();
					System.out.println("Sended. new game recog port");
				}
				else {
					System.out.println("? " + saver + sock.getInetAddress());
					tcpWriter.write("Unknown Request."); tcpWriter.newLine(); tcpWriter.flush();
				}
			}
		}
		catch(SocketException e) {
			System.out.println(SysoutColors.RED + "!! Unexpected Disconnected with " + sock.getRemoteSocketAddress() + e.getMessage() + SysoutColors.RESET);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println(SysoutColors.RED + "Disconnected with " + sock.getRemoteSocketAddress() + "Closed Socket" + SysoutColors.RESET);
		}
	}
	
}

public class ServerBase {
	
	static int RecogPortNext = 0;
	static List<Room> rooms = new ArrayList<>();
	
	static int RecogPortIncre() {
		RecogPortNext++;
		return RecogPortNext-1;
	}
	
	static int MakeNewGame() {
		rooms.add(new Room(RecogPortIncre()));
		return RecogPortNext;
	}
	
	static boolean SEND_LOCKED = false;
	
	static boolean needSend = false;
	static String stringToBeSended;
	static InetAddress addressToSend;
	
	public static void main(String[] args) {
		try {
			System.out.println("Trying to connect with DB...");
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/javadeep", "root", "521869");
			Statement stmt = conn.createStatement();
			System.out.println("connect success with DB");
			
			System.out.println("Server trying to start...");
			//TCP
			@SuppressWarnings("resource")
			ServerSocket tcpSock = new ServerSocket(1235);
			Socket tcpClientSock = new Socket();
			//final String MULTICAST_IP = "239.0.0.1";
			//final int MULTICAST_PORT = 4000;
			@SuppressWarnings("resource")
			DatagramSocket udpSock = new DatagramSocket(4000);
			//System.out.println("Port is " + udpSock.getPort());
			
			System.out.println("Server is receiving.");
			
			//멀티 스레드 고정 설정
			ExecutorService executorService = Executors.newCachedThreadPool();
			
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
							//System.out.println(addressToSend);
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
									System.out.println("Sended");
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
				System.out.println(SysoutColors.GREEN + "Client Connected From " + tcpClientSock.getRemoteSocketAddress() + SysoutColors.RESET);
				executorService.execute(new Clients(tcpClientSock));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
