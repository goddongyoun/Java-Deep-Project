package testConnection;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.*;

class User{
	String name;
	InetAddress ip;
	
	User(String _name, InetAddress _ip){
		name = _name;
		ip = _ip;
	}
}

class Room{
	int gameRecogPort = 0;
	User users[] = new User[5];
	final int MAX_USER = 5; 
	int curUserNum = 0;
	
	Room(int gameRecogPort){
		this.gameRecogPort = gameRecogPort;
		return;
	}
	
	/** 
	 * -1 Too Many users, -2 same Ip found, 0 success
	 * 
	 */
	int newUserCome(String name, InetAddress ip) {
		if(curUserNum >= 5) {
			return -1;
		}
		for(int i = 0; i <= curUserNum; i++) {
			if(users[i] != null) {
				if(users[i].ip.equals(ip)) {
					return -2;
				}
			}
		}
		users[curUserNum++] = new User(name, ip);
		return 0;
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
	int RecogPort = -1; // -1 means unconnected to any romm yet
	Room connectedRoom;
	
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
			String saver = tcpReader.readLine();
			
			if(saver.equals("MakeConnection")) { // first Connection Check / Unstructured Request Sockets will be closed
				saver = tcpReader.readLine();
				if(saver.equals("plsSend")) {
					isSender = 1;
					System.out.println(SysoutColors.GREEN + "Connection made to Send " + sock.getRemoteSocketAddress() + SysoutColors.RESET);
				}
				else if(saver.equals("plsReceive")) {
					isSender = 0;
					System.out.println(SysoutColors.GREEN + "Connection made to Receive " + sock.getRemoteSocketAddress() + SysoutColors.RESET);
				}
				tcpWriter.write("Connection Good"); tcpWriter.newLine(); tcpWriter.flush();
			}
			else {
				System.out.println(SysoutColors.RED + "Unstructured Request. Socket will be closed -> " + sock.toString() + SysoutColors.RESET);
				tcpWriter.write("Undefined Request. Try Again; Do not attack, your connection log will be saved"); tcpWriter.newLine(); tcpWriter.flush();
				sock.close();
				return;
			}
			
			if(isSender == 0) {
				while((saver = tcpReader.readLine()) != null) {
					System.out.println("TCP received "+ saver + sock.getRemoteSocketAddress());
					
					if(saver.equals("MakeGame")) {
						if(RecogPort == -1) {
							RecogPort = ServerBase.MakeNewGame();
							String temp = tcpReader.readLine();
							System.out.println("Name : " + temp);
							ServerBase.rooms.get(RecogPort).newUserCome(temp, sock.getInetAddress());
							System.out.println("New Room Init RecogPort is " + (RecogPort));
							tcpWriter.write("Game Init RecogPort is " + (RecogPort)); tcpWriter.newLine(); tcpWriter.flush();
							System.out.println("Sended. new game recog port");
						}
						else {
							tcpReader.readLine();
							tcpWriter.write("AlreadyConnected"); tcpWriter.newLine(); tcpWriter.flush();
						}
					}
					else if(saver.equals("JoinGame")) {
						if(RecogPort == -1) {
							int tryingPort = Integer.parseInt(tcpReader.readLine());
							String temp = tcpReader.readLine();
							try {
								int res = ServerBase.rooms.get(tryingPort).newUserCome(temp, sock.getInetAddress());
								if(res == -1) {
									tcpWriter.write("TooManyUsers"); tcpWriter.newLine(); tcpWriter.flush();
								}
								else if(res == -2) {
									tcpWriter.write("SameIpFound"); tcpWriter.newLine(); tcpWriter.flush();
								}
								else if(res == 0) {
									this.RecogPort = tryingPort;
									tcpWriter.write("SuccessfullyJoind"); tcpWriter.newLine(); tcpWriter.flush();
								}
								else { //Invalid res value
									tcpWriter.write("ERROR SB_137"); tcpWriter.newLine(); tcpWriter.flush();
								}
							} catch(IndexOutOfBoundsException e) {
								tcpWriter.write("InvalidRecogPort"); tcpWriter.newLine(); tcpWriter.flush();
							}
						}
						else {
							tcpWriter.write("AlreadyConnected"); tcpWriter.newLine(); tcpWriter.flush();
							tcpReader.readLine(); tcpReader.readLine();
						}
					}
					else {
						System.out.println("? " + saver + sock.getInetAddress());
						tcpWriter.write("Unknown Request."); tcpWriter.newLine(); tcpWriter.flush();
					}
				}
			}
			else if(isSender == 1) {
				
			}
			else {
				System.out.println(SysoutColors.RED + "isSender has Wrong value. Socket will be closed -> " + sock.toString() + SysoutColors.RESET);
				sock.close();
				return;
			}
		}
		catch(SocketException e) {
			System.out.println(SysoutColors.RED + "!! Unexpected Disconnected with " + sock.getRemoteSocketAddress() + " " + e.getMessage() + SysoutColors.RESET);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println(SysoutColors.RED + "Disconnected with " + sock.getRemoteSocketAddress() + " Closed Socket" + SysoutColors.RESET);
		}
	}
	
}

public class ServerBase {
	
	static int RecogPortNext = 0;
	public static List<Room> rooms = new ArrayList<>();
	
	static int RecogPortIncre() {
		RecogPortNext++;
		return RecogPortNext-1;
	}
	
	static int MakeNewGame() {
		rooms.add(new Room(RecogPortIncre()));
		return RecogPortNext-1;
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
			@SuppressWarnings("unused")
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
			
			executorService.execute(()->{
				@SuppressWarnings("resource")
				Scanner sc = new Scanner(System.in);
				while(true) {
					String scInput = sc.nextLine();
					if(scInput.equals("list")) {
						System.out.println("Printing list of rooms..");
						for(int i = 0; i< RecogPortNext; i++) {
							System.out.println("Room Num : " + i);
							Room tempRoom = rooms.get(i);
							for(int j = 0; j<tempRoom.curUserNum; j++) {
								System.out.println(tempRoom.users[j].name + " " + tempRoom.users[j].ip);
							}
							System.out.println();
						}
						System.out.println("End of list of Rooms");
					}
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
