package ClientBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
//import java.net.SocketAddress;
//import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientBase {

	static byte[] sendBuf;
	static String SERVER_ADDRESS = "219.254.146.234";
	
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
	
	public static void main(String[] args) {
		System.out.println("[LOG]");
		final int SERVER_PORT = 1235;
		
		checkLoopBack();
		
		Scanner sc = new Scanner(System.in);
		try {
			ExecutorService executorService = Executors.newFixedThreadPool(3);
			
			//UDP
			executorService.execute(()->{
				try {
					sendBuf = "ConnectionTest".getBytes();
					byte[] recvBuf = new byte[256];
					DatagramSocket udpSock = new DatagramSocket(4001);
					DatagramPacket udpSendPack = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getByName(SERVER_ADDRESS), 4000);
					DatagramPacket udpRecvPack = new DatagramPacket(recvBuf, recvBuf.length);
					udpSock.send(udpSendPack);
					System.out.println("Sended");
					udpSock.receive(udpRecvPack);
					System.out.println("UDP received From Server -> " + new String(udpRecvPack.getData()).trim());
					udpSock.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			//TCP Connect
			Socket tcpSock = new Socket(SERVER_ADDRESS, SERVER_PORT);
			BufferedWriter tcpWriter = new BufferedWriter(new OutputStreamWriter(tcpSock.getOutputStream()));
			BufferedReader tcpReader = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
			
			tcpWriter.write("ConnectionTest"); tcpWriter.newLine(); tcpWriter.flush();
			String readSaver = tcpReader.readLine();
			System.out.println("TCP received From Server -> " + readSaver);
			
			tcpSock.close();
			
			tcpSock = new Socket(SERVER_ADDRESS, SERVER_PORT);
			tcpWriter = new BufferedWriter(new OutputStreamWriter(tcpSock.getOutputStream()));
			tcpReader = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
			tcpWriter.write("MakeGame"); tcpWriter.newLine(); tcpWriter.flush();
			System.out.println("MakeGame Sended");
			readSaver = tcpReader.readLine();
			System.out.println("TCP received From Server -> " + readSaver);
			
			tcpSock.close();
			//if(readSaver.equals("KILL THIS SOCKET")) {
			//}
		}catch(Exception e) {
			e.printStackTrace();
		}
		sc.close();
	}

}
