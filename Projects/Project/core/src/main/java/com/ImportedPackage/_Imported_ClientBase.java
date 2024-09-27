package com.ImportedPackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class _Imported_ClientBase {

	static byte[] sendBuf;
	static String SERVER_ADDRESS = "219.254.146.234"; // final dms dkslwlaks qusrudehlaus dksehlqslek. qusrudehlaus chltjsdmfekgo vjdvjd dnf wktls dlTtmqslek. --snrnsrk
	final static int SERVER_PORT_TCP = 1235;
	final static int SERVER_PORT_UDP  = 4000;
	static ExecutorService executorService = Executors.newFixedThreadPool(3);
	static Future<String> future_UDP;
	static Socket tcpSock_toSend;
	static Socket tcpSock_toRecv;
	static BufferedWriter tcpWriter_toSend;
	static BufferedReader tcpReader_toSend;
	static BufferedWriter tcpWriter_toRecv;
	static BufferedReader tcpReader_toRecv;
	
	static boolean isReceiverOut = true;
	
	static String name = null;
	
	static Scanner sc = new Scanner(System.in);
	
	/**
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * Do not use this method, This method should only be used inside the client. the Method will exist as public considering various situations, but do not use it.
	 */
	public static void checkLoopBack() {
		try {
            String apiUrl = "https://ifconfig.me/ip";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String externalIp = reader.readLine();
            reader.close();
            if(externalIp.equals("219.254.146.234")) {
            	SERVER_ADDRESS = "127.0.0.1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[LOG] Loopback인지 확인할 수 없었으나 개발자가 아니라면 상관없으니 신경쓰지 마십시오.");
        }
	}
	
	/**
	 * 
	 * @param roomName literally
	 * @return
	 */
	public static String MakeGame_TCP(String roomName) {
		//TCP Connect
		try {
			tcpWriter_toSend.write("MakeGame"); tcpWriter_toSend.newLine(); tcpWriter_toSend.write(roomName); tcpWriter_toSend.newLine(); tcpWriter_toSend.write(name); tcpWriter_toSend.newLine(); tcpWriter_toSend.flush();
			System.out.println("MakeGame Sended");
			String readSaver = tcpReader_toSend.readLine();
			System.out.println("[LOG] TCP received From Server -> " + readSaver);
			System.out.println(readSaver + "[LOG] ");
			int Port = readSaver.charAt(readSaver.length()-1) - '0';
			
			tcpSock_toRecv = new Socket(SERVER_ADDRESS, SERVER_PORT_TCP);
			tcpWriter_toRecv = new BufferedWriter(new OutputStreamWriter(tcpSock_toRecv.getOutputStream(), "UTF-8"));
			tcpReader_toRecv = new BufferedReader(new InputStreamReader(tcpSock_toRecv.getInputStream(), "UTF-8"));
			tcpWriter_toRecv.write("MakeConnection"); tcpWriter_toRecv.newLine(); tcpWriter_toRecv.write("plsSend"); tcpWriter_toRecv.newLine(); tcpWriter_toRecv.write("Port is "+Port); tcpWriter_toRecv.newLine(); tcpWriter_toRecv.flush();
			System.out.println("[LOG] TCP received From Server(1 == as Sender, 0 == as Receiver) -> " + tcpReader_toRecv.readLine());
			return "Success makeGame";
		} 
		catch (SocketException e) {
			if(e.getMessage().equals("Connection reset")) {
				System.out.println("[LOG] 서버가 연결을 끊었습니다.");
			}
			return "Failed makeGame";
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed makeGame";
		}
	}
	
	/**
	 * DUMMY
	 */
	public static void connectionTest_UDP() {
		//UDP
		future_UDP = executorService.submit(()->{
			try {
				sendBuf = "ConnectionTest".getBytes();
				byte[] recvBuf = new byte[256];
				DatagramSocket udpSock = new DatagramSocket(4001);
				udpSock.setSoTimeout(5000);
				DatagramPacket udpSendPack = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT_UDP);
				DatagramPacket udpRecvPack = new DatagramPacket(recvBuf, recvBuf.length);
				udpSock.send(udpSendPack);
				System.out.println("Sended");
				udpSock.receive(udpRecvPack);
				System.out.println("[LOG] UDP received From Server -> " + new String(udpRecvPack.getData()).trim());
				udpSock.close();
				return "End";
			} catch (Exception e) {
				e.printStackTrace();
				return "End with ERR";
			}
		});
	}
	
	/**
	 * 
	 * @param Port gameRoomPort
	 * @param name what is your name to be displayed
	 * @return
	 */
	public static String joinGame(int Port, String name) {
		try {
			tcpWriter_toSend.write("JoinGame"); tcpWriter_toSend.newLine(); 
			tcpWriter_toSend.write(Integer.toString(Port)); tcpWriter_toSend.newLine(); 
			tcpWriter_toSend.write(name); tcpWriter_toSend.newLine();
			tcpWriter_toSend.flush();
			
			String saver = tcpReader_toSend.readLine();
			if(saver.equals("TooManyUsers")) {
				System.out.println("[LOG] 유저 꽉 참");
				return saver;
			}
			else if(saver.equals("SameIpFound")) {
				System.out.println("[LOG] 같은 아이피가 발견되었습니다.");
				return saver;
			}
			else if(saver.equals("ERROR SB_137")) {
				System.out.println("[LOG] 오류가 발견되었습니다 SB_137");
				return saver;
			}
			else if(saver.equals("InvalidRecogPort")) {
				System.out.println("[LOG] 존재하지 않는 방 포트 입니다.");
				return saver;
			}
			else if(saver.equals("AlreadyConnected")) {
				System.out.println("[LOG] 이미 연결되어있습니다.");
				return saver;
			}
			else if(saver.equals("SuccessfullyJoind")) {
				tcpSock_toRecv = new Socket(SERVER_ADDRESS, SERVER_PORT_TCP);
				tcpWriter_toRecv = new BufferedWriter(new OutputStreamWriter(tcpSock_toRecv.getOutputStream(), "UTF-8"));
				tcpReader_toRecv = new BufferedReader(new InputStreamReader(tcpSock_toRecv.getInputStream(), "UTF-8"));
				tcpWriter_toRecv.write("MakeConnection"); tcpWriter_toRecv.newLine(); tcpWriter_toRecv.write("plsSend"); tcpWriter_toRecv.newLine(); tcpWriter_toRecv.write("Port is "+Port); tcpWriter_toRecv.newLine(); tcpWriter_toRecv.flush();
				System.out.println("[LOG] TCP received From Server(1 == as Sender, 0 == as Receiver) -> " + tcpReader_toRecv.readLine());
				System.out.println("[LOG] 방에 접속하였습니다.");
				return saver;
			}
			else { // anjdi Tlqkf?
				System.out.println("[LOG] ??? ERROR CLI_133 " + saver);
				return saver;
			}
		}
		catch (SocketException e) {
			if(e.getMessage().equals("Connection reset")) {
				System.out.println("[LOG] 서버가 연결을 끊었습니다.");
			}
			return "Failed joinGame";
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed joinGame";
		}
	}
	
	/**
	 * 
	 * @return "Failed outGame" means failed
	 */
	public static String outGame() {
		try {
			tcpWriter_toSend.write("OutGame"); tcpWriter_toSend.newLine(); tcpWriter_toSend.flush();
			String saver = tcpReader_toSend.readLine();
			System.out.println(saver + " from Server [LOG] ");
			tcpReader_toRecv = null;
			return saver;
		} catch (IOException e) {
			e.printStackTrace();
			return "Failed outGame";
		}
	}
	
	/**
	 * 
	 * @param what
	 * @return "Success" means success, "Faild sendChat" means failed
	 */
	public static String sendChat(String what) {
		try {
			tcpWriter_toSend.write("NewText"); tcpWriter_toSend.newLine(); tcpWriter_toSend.write(what); tcpWriter_toSend.newLine(); tcpWriter_toSend.flush();
			String saver = tcpReader_toSend.readLine();
			System.out.println(saver + " from Server [LOG] ");
			return saver;
		} catch (IOException e) {
			e.printStackTrace();
			return "Faild sendChat";
		} finally {
		}
	}
	
	public static int SHUTDOWN() {
		executorService.shutdown();
		return 1;
	}
	
	public static void run(String playerName) throws Exception {
		System.out.println("[LOG] <-- means 'from ClientBase'");
		name = playerName;
		if(name == null) {
			System.out.println("[LOG] Invalid Name.. returning the method... ");
			return;
		}
		checkLoopBack();
		try {
			System.setOut(new PrintStream(System.out, true, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.out.println("[LOG] setOutError - 184");
			e.printStackTrace();
		}
		
		try {
			tcpSock_toSend = new Socket(SERVER_ADDRESS, SERVER_PORT_TCP);
			tcpWriter_toSend = new BufferedWriter(new OutputStreamWriter(tcpSock_toSend.getOutputStream(), "UTF-8"));
			tcpReader_toSend = new BufferedReader(new InputStreamReader(tcpSock_toSend.getInputStream(), "UTF-8"));
			tcpWriter_toSend.write("MakeConnection"); tcpWriter_toSend.newLine(); tcpWriter_toSend.write("plsReceive"); tcpWriter_toSend.newLine(); tcpWriter_toSend.flush();
			System.out.println("[LOG] TCP received From Server(1 == as Sender, 0 == as Receiver) -> " + tcpReader_toSend.readLine());
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("[LOG] 서버가 꺼졌거나 문제가 생겼습니다. 관리자에게 문의하십시오.");
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[LOG] 서버가 꺼졌거나 문제가 생겼습니다. 관리자에게 문의하십시오.");
			throw e;
		} finally {
		}
		
		final int EXIT = -44;
		
		System.out.println("[LOG] ClientBase Started");
		
		//!!! Important!! this method should be import to The Real Game client's code
		executorService.execute(()->{
			String saver = null;
			isReceiverOut = false;
			while(true) {
				try {
					Thread.sleep(1);
					if(tcpReader_toRecv != null) {
						while((saver = tcpReader_toRecv.readLine()) != null) {
							System.out.println("\n" + saver + " / [LOG] chat");
						}
					}
				} catch (IOException | InterruptedException e) {
					System.out.println("[LOG] 채팅 연결 종료");
				} finally {
					tcpReader_toRecv = null;
					isReceiverOut = true;
				}
			}
		});
		System.out.println("[LOG] ExecutorServices Started");
		//!!! Important ends!!
		
		//System.exit(0);
	}

}
