package testConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPtestAsClient {

	public static void main(String[] args) {
		String s = "Connection Test";
		byte[] buf = s.getBytes();
		byte[] recvBuf = new byte[256];
		Scanner sc = new Scanner(System.in);
		DatagramSocket socket;
		try {
			socket = new DatagramSocket(4000); // 송/수신 포트를 지정. 여기서는 보낼 포트를 지정
			InetAddress addr = InetAddress.getByAddress(new byte[] {127,0,0,1});
			DatagramPacket packet = new DatagramPacket(buf, buf.length, addr, 5000); // 보낼 준비, 파라미터는 순서대로 1. 보낼 바이트 배열, 2. 그 길이, 3. 보낼 위치, 4. 보낼 포트
			ExecutorService executorService = Executors.newFixedThreadPool(2);
			executorService.execute(()->{
				System.out.println("ExecutorService Init");
				DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
				try {
					socket.receive(recvPacket);
					System.out.println(new String(recvPacket.getData()).trim() + " Received" + recvPacket.getAddress() +" : "+recvPacket.getPort());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			socket.send(packet);
			System.out.println("Sended");
			System.out.println("Input string to send");
			buf = sc.nextLine().getBytes();
			packet.setData(buf);
			socket.send(packet);
			socket.close();
			System.out.println("closed");
		}catch(Exception e) {
			e.printStackTrace();
		}
		sc.close();
	}

}
