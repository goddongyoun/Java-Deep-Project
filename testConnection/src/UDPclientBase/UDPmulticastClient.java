package UDPclientBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
//import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPmulticastClient {

	static byte[] sendBuf = new byte[256];
	
	public static void main(String[] args) {
		System.out.println("[LOG]");
		//final String SERVER_ADDRESS = "124.55.106.99";
		final String SERVER_ADDRESS = "127.0.0.1";
		final int SERVER_PORT = 1235;

		Scanner sc = new Scanner(System.in);
		try {
			ExecutorService executorService = Executors.newFixedThreadPool(3);
			executorService.execute(()->{
				try {
					sendBuf = "ConnectionTest".getBytes();
					DatagramSocket udpSock = new DatagramSocket(4001);
					DatagramPacket udpSendPack = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getByName(SERVER_ADDRESS), 4000);
					udpSock.send(udpSendPack);
					System.out.println("Sended");
					udpSock.receive(udpSendPack);
					System.out.println("UDP received From Server -> " + new String(udpSendPack.getData()).trim());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			//MultiCast Connect
			/*executorService.execute(()->{
				System.out.println("ExecutorService Init");
				DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
				try {
					MulticastSocket ms = new MulticastSocket(4000);
					ms.setReuseAddress(true);
					ms.setSoTimeout(1000*60);
					System.out.println(ms.getSoTimeout());
					SocketAddress Saddr = new InetSocketAddress(InetAddress.getByName("239.0.0.1"), 4000);
					ms.joinGroup(Saddr, null);
					while(true) {
						ms.receive(recvPacket);
						String recvSaver = new String(recvPacket.getData()).trim();
						System.out.println("UDP received From Server -> " + recvSaver + recvPacket.getAddress() +" : "+recvPacket.getPort());
						if(recvSaver.equals("KILL THIS SOCKET")) {
							break;
						}
					}
					ms.leaveGroup(Saddr, null);
					ms.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});*/
			
			//BroadCast TEST
			/*DatagramSocket socket = null;
	        try {
	            // 수신을 위한 소켓 생성
	            socket = new DatagramSocket(9876); // 송신자와 동일한 포트 사용

	            byte[] buffer = new byte[1024];
	            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

	            System.out.println("Waiting for broadcast messages...");

	            // 메시지 수신
	            socket.receive(packet);
	            String receivedMessage = new String(packet.getData(), 0, packet.getLength());

	            // 수신된 메시지 출력
	            System.out.println("Received broadcast message: " + receivedMessage);

	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (socket != null && !socket.isClosed()) {
	                socket.close();
	            }
	        }*/
			
			
			//TCP Connect
			Socket tcpSock = new Socket(SERVER_ADDRESS, SERVER_PORT);
			BufferedWriter tcpWriter = new BufferedWriter(new OutputStreamWriter(tcpSock.getOutputStream()));
			BufferedReader tcpReader = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
			tcpWriter.write("ConnectionTest"); tcpWriter.newLine(); tcpWriter.flush();
			String readSaver = tcpReader.readLine();
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
