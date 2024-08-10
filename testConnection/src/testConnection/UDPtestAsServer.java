package testConnection;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPtestAsServer {

	public static void main(String[] args) {
		byte[] buf = new byte[256]; // 서버가 받을 수 있는 최대 바이트 수를 지정
		try {
			System.out.println("Server trying to start...");
			DatagramSocket socket = new DatagramSocket(5000); // 송/수신 포트를 지정. 여기서는 수신용
			DatagramPacket packet = new DatagramPacket(buf, buf.length); // 수신된 정보를 패킷에 저장할 바이트 변수 위치와 길이 알려주기
			System.out.println("Server is receiving.");
			while(!(buf.toString().equals("KILL SERVER"))) {
				socket.receive(packet); // 수신 실행 (멀티 스레드가 아닌 이상 프로그램은 수신이 올 때까지 일시 중단)
				System.out.println(new String(packet.getData())+", "+ packet.getAddress() +", "+ packet.getPort());
				for(int i = 0; i<buf.length; i++) {
					buf[i] = 0;
				}
			}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
