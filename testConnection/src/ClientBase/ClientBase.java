package ClientBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
//import java.net.DatagramSocket;
import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.MulticastSocket;
import java.net.Socket;
import java.net.URL;
//import java.net.UnknownHostException;
//import java.net.SocketAddress;
//import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientBase {

	static byte[] sendBuf;
	static String SERVER_ADDRESS = "219.254.146.234";
	final static int SERVER_PORT_TCP = 1235;
	final static int SERVER_PORT_UDP  = 4000;
	static ExecutorService executorService = Executors.newFixedThreadPool(3);
	static Future<String> future_UDP;
	
	static void checkLoopBack() {
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
        }
	}
	
	static void connectionTest_TCP() {
		//TCP Connect
		Socket tcpSock;
		try {
			tcpSock = new Socket(SERVER_ADDRESS, SERVER_PORT_TCP);
			BufferedWriter tcpWriter = new BufferedWriter(new OutputStreamWriter(tcpSock.getOutputStream()));
			BufferedReader tcpReader = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
			
			tcpWriter.write("ConnectionTest"); tcpWriter.newLine(); tcpWriter.flush();
			String readSaver = tcpReader.readLine();
			System.out.println("TCP received From Server -> " + readSaver);
			
			tcpSock.close();
			
			tcpSock = new Socket(SERVER_ADDRESS, SERVER_PORT_TCP);
			tcpWriter = new BufferedWriter(new OutputStreamWriter(tcpSock.getOutputStream()));
			tcpReader = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
			tcpWriter.write("MakeGame"); tcpWriter.newLine(); tcpWriter.flush();
			System.out.println("MakeGame Sended");
			readSaver = tcpReader.readLine();
			System.out.println("TCP received From Server -> " + readSaver);
			
			tcpSock.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void connectionTest_UDP() {
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
				System.out.println("UDP received From Server -> " + new String(udpRecvPack.getData()).trim());
				udpSock.close();
				return "End";
			} catch (Exception e) {
				e.printStackTrace();
				return "End with ERR";
			}
		});
	}
	
	public static void main(String[] args) {
		System.out.println("[LOG]");
		checkLoopBack();
		
		final int EXIT = 4;
		
		Scanner sc = new Scanner(System.in);
		System.out.println("1. Connection Test(UDP)\n2. Connection Test(TCP)\n3. ?\n4. Exit\n");
		
		while(true) {
			System.out.print("input >> ");
			int user = sc.nextInt();
			if(user == 1) {
				connectionTest_UDP();
			}
			else if(user == 2) {
				connectionTest_TCP();
			}
			else if(user == 3) {
				System.out.println("?");
			}
			else if(user == EXIT) {
				break;
			}
			else {
				System.out.println("?");
			}
			
			if(future_UDP != null) {
				while(future_UDP.isDone() == false) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		executorService.shutdown();
		sc.close();
	}

}
