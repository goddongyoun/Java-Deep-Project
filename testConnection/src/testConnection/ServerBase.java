package testConnection;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.*;

class User{
	String name;
	float[] loc = new float[2];
	InetAddress ip;
	boolean isDead = false;
	
	User(String _name, InetAddress _ip){
		name = _name;
		ip = _ip;
	}
	
	void setLocation(float x, float y) {
		loc[0] = x;
		loc[1] = y;
	}
	
	float[] getLocation() {
		return loc;
	}
}

class Room{
	public boolean unconnectable = false;
	int gameRecogPort = 0;
	User users[] = new User[5];
	final int MAX_USER = 5; 
	int curUserNum = 0;
	public List<String> chatLog = new ArrayList<>();
	public int chatNum = -1;
	String roomName = null;
	boolean start = false;
	int bossInd = -1;
	
	Room(int gameRecogPort, String roomName){
		this.gameRecogPort = gameRecogPort;
		this.roomName = roomName;
		return;
	}
	
	/** 
	 * -1 Too Many users, -2 same Ip found, -3 Unconnectable, -4 Same Name Found, 0 success
	 * 
	 */
	int newUserCome(String name, InetAddress ip) {
		if(unconnectable == true) {
			return -3;
		}
		if(curUserNum >= 5) {
			return -1;
		}
		for(int i = 0; i < curUserNum; i++) {
			if(users[i] != null) {
				/*if(users[i].ip.equals(ip)) {
					return -2;
				}
				else*/ if(users[i].name.equals(name)) {
					System.out.println("Same Name");
					return -4;
				}
			}
		}
		users[curUserNum++] = new User(name, ip);
		System.out.println("Now curUserNum is " + curUserNum + " from " + gameRecogPort);
		return 0;
	}
	
	/**
	 * Request to user out
	 * @param name
	 * @return -1 Couldn't found, 0 success, -4 No one in this room, pls remove this room
	 */
	int outOfUser(String name) {
		boolean found = false;
		for(int i = 0; i<MAX_USER; i++) {
			if(users[i] == null) {
				continue;
			}
			if(users[i].name.equals(name)) {
				found = true;
				users[i] = null;
				curUserNum--;
				System.gc();
			}
		}
		if(found == false) {
			return -1;
		}
		if(curUserNum <= 0) {
			System.out.println(gameRecogPort + " now been resetted");
			curUserNum = 0;
			this.unconnectable = true;
			chatNum = -1;
			start = false;
			bossInd = -1;
			return -4;
		}
		return 0;
	}
	
	float[][] getLocations() {
		float[][] locations = new float[curUserNum][2];
		int idx = 0;
		for (int i = 0; i < users.length; i++) {
			if (users[i] != null) {
				locations[idx++] = users[i].getLocation();
			}
		}
		return locations;
	}
	
	public String getLocToSortString() {
		StringBuilder sb = null;
		boolean again = true;
        while(again == true) {
        	again = false;
    		sb = new StringBuilder();
            sb.append(curUserNum);
        	for (int i = 0; i < MAX_USER; i++) {
    			try {
    				User user = users[i];
    				if(user != null) {
    					sb.append(" ").append(user.name).append("/").append(user.getLocation()[0]).append("/")
						.append(user.getLocation()[1]).append("/").append(user.isDead);
    				}
    			} catch (Exception e) {
    				//e.printStackTrace();
    				//again = true;
    				break;
    			}
    		}
        }

        return sb.toString();
	}
	
	int setLocation(String name, float x, float y) {
		for (int i = 0; i < curUserNum; i++) {
			if (users[i] != null && users[i].name.equals(name)) {
				users[i].setLocation(x, y);
				return 0;
			}
		}
		return -1;
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
	private int isSender = -1;
	int RecogPort = -1; // -1 means unconnected to any room yet
	Room connectedRoom;
	InetAddress connectedIP = null;
	int currentChatNum = 0;
	String name = "undefined";
	
	Clients(Socket _sock){
		sock = _sock;
		connectedIP = sock.getInetAddress();
	}
	
	@Override
	public void run() {
		BufferedReader tcpReader;
		BufferedWriter tcpWriter;
		try {
			tcpReader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
			tcpWriter = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
			
			String saver = tcpReader.readLine();
			System.out.println("first message => '" + saver + "' From "+ sock.getRemoteSocketAddress());
			
			if(saver.equals("MakeConnection")) { // first Connection Check ; Unstructured Request Sockets will be closed
				saver = tcpReader.readLine();
				if(saver.equals("plsSend")) {
					isSender = 1;
					saver = tcpReader.readLine();
					String[] parts = saver.split("/");
			        if (parts.length > 1) {
			            RecogPort = ServerBase.strToI_map.get(parts[1]);
			            System.out.println("RecogPort(Int): " + RecogPort);
			        }
					System.out.println(SysoutColors.GREEN + "Connection made to Send " + sock.getRemoteSocketAddress() + SysoutColors.RESET);
				}
				else if(saver.equals("plsReceive")) {
					isSender = 0;
					System.out.println(SysoutColors.GREEN + "Connection made to Receive " + sock.getRemoteSocketAddress() + SysoutColors.RESET);
				}
				tcpWriter.write("Connection Good, Mode is "+isSender); tcpWriter.newLine(); tcpWriter.flush();
			}
			else {
				System.out.println(SysoutColors.RED + "Unstructured Request. Socket will be closed -> " + sock.toString() + SysoutColors.RESET);
				tcpWriter.write("Undefined Request. Try Again"); tcpWriter.newLine(); tcpWriter.flush();
				sock.close();
				return;
			}
			
			if(isSender == 0) {
				while((saver = tcpReader.readLine()) != null) {
					if(!(saver.equals("NewLoc") || saver.equals("GetLoc"))) {
						System.out.println("TCP received "+ saver + sock.getRemoteSocketAddress());
					}
					
					if(saver.equals("MakeGame")) {
						if(RecogPort == -1) {
							String temp = tcpReader.readLine();
							RecogPort = ServerBase.MakeNewGame(temp);
							temp = tcpReader.readLine();
							System.out.println("Name : " + temp);
							name = temp;
							ServerBase.rooms.get(RecogPort).newUserCome(temp, sock.getInetAddress());
							System.out.println("New Room Init RecogPort is " + (RecogPort));
							tcpWriter.write("Game Init RecogPort is /" + (ServerBase.iToStr_map.get(RecogPort))); tcpWriter.newLine(); 
							tcpWriter.flush();
							System.out.println("Sended. new game recog port");
						}
						else {
							tcpReader.readLine();
							tcpWriter.write("AlreadyConnected"); tcpWriter.newLine(); tcpWriter.flush();
						}
					}
					else if(saver.equals("JoinGame")) {
						if(RecogPort == -1) {
							String tryingPort = tcpReader.readLine();
							String temp = tcpReader.readLine();
							System.out.println("trying port is "+ tryingPort);
							try {
								int res = ServerBase.rooms.get(ServerBase.strToI_map.get(tryingPort)).newUserCome(temp, connectedIP);
								if(res == -1) {
									tcpWriter.write("TooManyUsers"); tcpWriter.newLine(); tcpWriter.flush();
								}
								else if(res == -2) {
									tcpWriter.write("SameIpFound"); tcpWriter.newLine(); tcpWriter.flush();
								}
								else if(res == 0) {
									this.RecogPort = ServerBase.strToI_map.get(tryingPort);
									tcpWriter.write("SuccessfullyJoind"); tcpWriter.newLine(); tcpWriter.flush();
									name = temp;
								}
								else if(res == -3) { // tried to connect to unconnectable room
									tcpWriter.write("InvalidRecogPort"); tcpWriter.newLine(); tcpWriter.flush();
								}
								else if(res == -4) {
									tcpWriter.write("SameNameFound"); tcpWriter.newLine(); tcpWriter.flush();
								}
								else { //Invalid res value
									tcpWriter.write("ERROR SB_137"); tcpWriter.newLine(); tcpWriter.flush();
								}
							} catch(IndexOutOfBoundsException e) {
								tcpWriter.write("InvalidRecogPort"); tcpWriter.newLine(); tcpWriter.flush();
							} catch(NullPointerException e) {
								tcpWriter.write("InvalidRecogPort"); tcpWriter.newLine(); tcpWriter.flush();
							}
						}
						else {
							tcpWriter.write("AlreadyConnected"); tcpWriter.newLine(); tcpWriter.flush();
							tcpReader.readLine(); tcpReader.readLine();
						}
					}
					else if(saver.equals("OutGame")) {
						if(RecogPort == -1) {
							tcpWriter.write("NotJoinedYet"); tcpWriter.newLine(); tcpWriter.flush();
						} else {
							int res = ServerBase.rooms.get(RecogPort).outOfUser(name);
							if(res == -1) {
								tcpWriter.write("Couldn't found"); tcpWriter.newLine(); tcpWriter.flush();
							} else if(res == 0) {
								RecogPort = -1;
								tcpWriter.write("Success"); tcpWriter.newLine(); tcpWriter.flush();
							} else if(res == -4) {
								ServerBase.rooms.get(RecogPort).unconnectable = true;
								RecogPort = -1;
								tcpWriter.write("Success"); tcpWriter.newLine(); tcpWriter.flush();
							}
							else {
								tcpWriter.write("???"); tcpWriter.newLine(); tcpWriter.flush();
							}
						}
					}
					else if(saver.equals("NewText")) {
						if(RecogPort == -1) {
							tcpWriter.write("NotJoinedYet"); tcpWriter.newLine(); tcpWriter.flush();
							tcpReader.readLine();
						}
						else {
							saver = tcpReader.readLine();
							ServerBase.rooms.get(RecogPort).chatLog.add(name + " : " + saver);
							ServerBase.rooms.get(RecogPort).chatNum++;
							tcpWriter.write("Success"); tcpWriter.newLine(); tcpWriter.flush();
						}
					}
					else if(saver.equals("NewLoc")) {
						if(RecogPort == -1) {
							tcpWriter.write("NotJoinedYet"); tcpWriter.newLine(); tcpWriter.flush();
							tcpReader.readLine();tcpReader.readLine();
						}
						else {
							float tempx = Float.parseFloat(tcpReader.readLine());
							float tempy = Float.parseFloat(tcpReader.readLine());
							String tempName = tcpReader.readLine();
							int res = ServerBase.rooms.get(RecogPort).setLocation(tempName, tempx, tempy);
							if(res == 0) {
								tcpWriter.write("Success"); tcpWriter.newLine(); tcpWriter.flush();
							}
							else {
								tcpWriter.write("Failed"); tcpWriter.newLine(); tcpWriter.flush();
							}
						}
					}
					else if(saver.equals("GetLoc")) {
						if(RecogPort == -1) {
							tcpWriter.write("NotJoinedYet"); tcpWriter.newLine(); tcpWriter.flush();
							tcpReader.readLine();tcpReader.readLine();
						}
						else {
							saver = ServerBase.rooms.get(RecogPort).getLocToSortString();
							tcpWriter.write(saver); tcpWriter.newLine(); tcpWriter.flush();
						}
					}
					else if(saver.equals("PlsStart")) {
						if(RecogPort == -1) {
							tcpWriter.write("NotJoinedYet"); tcpWriter.newLine(); tcpWriter.flush();
						}
						else {
							int i = -1;
							while (ServerBase.rooms.get(RecogPort).users[(i = (int)(Math.random() * 5))] == null);
							ServerBase.rooms.get(RecogPort).bossInd = i;
							Thread.sleep(5);
							ServerBase.rooms.get(RecogPort).start = true;
							tcpWriter.write("NowStartIsTrue"); tcpWriter.newLine(); tcpWriter.flush();
						}
					}
					else if(saver.equals("SetDead")) {
						if(RecogPort == -1) {
							tcpWriter.write("NotJoinedYet"); tcpWriter.newLine(); tcpWriter.flush();
							tcpReader.readLine();
						}
						else {
							Room tempRoom = ServerBase.rooms.get(RecogPort);
							saver = tcpReader.readLine();
							for(int i = 0; i< tempRoom.MAX_USER; i++) {
								if(tempRoom.users[i] != null) {
									if(tempRoom.users[i].name.equals(saver)) {
										tempRoom.users[i].isDead = true;
										tcpWriter.write("Success"); tcpWriter.newLine(); tcpWriter.flush();
									}
								}
							}
						}
					}
					else {
						System.out.println("? " + saver + sock.getInetAddress());
						tcpWriter.write("Unknown Request."); tcpWriter.newLine(); tcpWriter.flush();
					}
				}
			}
			else if(isSender == 1) {
				boolean isStarted = false;
				while(true) {
					Thread.sleep(10);
					if(RecogPort != -1) {
						if(currentChatNum <= ServerBase.rooms.get(RecogPort).chatNum) {
							tcpWriter.write("Chat"); tcpWriter.newLine(); tcpWriter.flush();
							for(; currentChatNum <= ServerBase.rooms.get(RecogPort).chatNum; currentChatNum++) {
								tcpWriter.write(ServerBase.rooms.get(RecogPort).chatLog.get(currentChatNum)); tcpWriter.newLine(); tcpWriter.flush();
							}
							tcpWriter.write("End"); tcpWriter.newLine(); tcpWriter.flush();
							System.out.println("chat shooted");
						}
						else if(ServerBase.rooms.get(RecogPort).start == true && isStarted == false) {
							isStarted = true;
							tcpWriter.write("StartGame"); tcpWriter.newLine();
							
							Room tempR = ServerBase.rooms.get(RecogPort);
							if(tempR.users[tempR.bossInd] == null) {
								tcpWriter.write("BossIndError"); tcpWriter.newLine();
							}
							else {
								tcpWriter.write(tempR.users[tempR.bossInd].name); tcpWriter.newLine();
							}
							tcpWriter.flush();
						}
						else {
							tcpWriter.write("KA"); tcpWriter.newLine(); tcpWriter.flush(); // KA means Keep Alive
						}
					}
				}
				// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! empty
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
			if(RecogPort != -1) {
				if(isSender != 1) {
					int res = ServerBase.rooms.get(RecogPort).outOfUser(name);
					if(res == 0) {
						System.out.println("Successfully out " + connectedIP);
					}
					else if(res == -1) {
						System.out.println("Couldn't found" + connectedIP);
					}
				}
			}
		}
	}
	
}

public class ServerBase {
	
	static int RecogPortNext = 0;
	public static List<Room> rooms = new ArrayList<>();
	public static Map<String, Integer> strToI_map = new HashMap<>();
	public static Map<Integer, String> iToStr_map = new HashMap<>();
	
	private static String setRanStr() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);
        
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
	
	static void RecogPortDecre() {
		if((RecogPortNext-1) >= 0) {
			RecogPortNext--;
		}
		return;
	}
	
	static int RecogPortIncre() {
		RecogPortNext++;
		return RecogPortNext-1;
	}
	
	static int MakeNewGame(String nameOfRoom) {
		for(int i = 0; i < RecogPortNext; i++) {
			if(rooms.get(i).unconnectable == true) {
				rooms.get(i).unconnectable = false;
				return i;
			}
		}
		rooms.add(new Room(RecogPortIncre(), nameOfRoom));
		return RecogPortNext-1;
	}
	
	static boolean SEND_LOCKED = false;
	
	static boolean needSend = false;
	static String stringToBeSended;
	static InetAddress addressToSend;
	
	public static void main(String[] args) {
		try {
			System.setOut(new PrintStream(System.out, true, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.out.println("setOutError - 184");
			e.printStackTrace();
		}
		try {/*
			System.out.println("Trying to connect with DB...");
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/javadeep", "root", "521869");
			@SuppressWarnings("unused")
			Statement stmt = conn.createStatement();
			System.out.println("connect success with DB");*/
			
			for (int i = 0; i <= 99; i++) {
				String tempSaver = setRanStr();
	            strToI_map.put(tempSaver, i);
	            iToStr_map.put(i, tempSaver);
	        }
			System.out.println("Completed to reset roomcodes...");
			
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
							if(tempRoom.unconnectable == true) {
								System.out.println("Unconnectable Now");
							}
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
