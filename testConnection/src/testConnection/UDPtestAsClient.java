package testConnection;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPtestAsClient {

	public static void main(String[] args) {
		String s = "Send message";
		byte[] buf = s.getBytes();
		Scanner sc = new Scanner(System.in);
		try {
			DatagramSocket socket = new DatagramSocket(4000); // 송/수신 포트를 지정. 여기서는 보낼 포트를 지정
			InetAddress address = InetAddress.getByAddress(new byte[] {127, 0, 0, 1}); // 인터넷 주소를 저장 getByName으로 해도 상관없으나 효율성 위해 getByAddress로
			DatagramPacket packet = new DatagramPacket(buf, buf.length,address,5000); // 보낼 준비, 파라미터는 순서대로 1. 보낼 바이트 배열, 2. 그 길이, 3. 보낼 위치, 4. 보낼 포트
			socket.send(packet);
			System.out.println("Sended");
			buf = sc.nextLine().getBytes();
			packet.setData(buf);
			socket.send(packet);
			socket.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		sc.close();
	}

}
