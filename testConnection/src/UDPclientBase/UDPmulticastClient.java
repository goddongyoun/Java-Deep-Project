package UDPclientBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPmulticastClient {

	public static void main(String[] args) {
		System.out.println("[LOG]");
		final String SERVER_ADDRESS = "127.0.0.1"; 
		final int SERVER_PORT = 1235;

		
		byte[] recvBuf = new byte[256];
		Scanner sc = new Scanner(System.in);
		try {
			
			//MultiCast Connect
			ExecutorService executorService = Executors.newFixedThreadPool(2);
			executorService.execute(()->{
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
						System.out.println("UDP received From Server -> " + new String(recvPacket.getData()).trim() + recvPacket.getAddress() +" : "+recvPacket.getPort());
						
					}
					//ms.leaveGroup(Saddr, null);
					//ms.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			
			//TCP Connect
			Socket tcpSock = new Socket(SERVER_ADDRESS, SERVER_PORT);
			BufferedWriter tcpWriter = new BufferedWriter(new OutputStreamWriter(tcpSock.getOutputStream()));
			BufferedReader tcpReader = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
			tcpWriter.write("ConnectionTest"); tcpWriter.newLine(); tcpWriter.flush();
			System.out.println("TCP received From Server -> " + tcpReader.readLine());
		}catch(Exception e) {
			e.printStackTrace();
		}
		sc.close();
	}

}
