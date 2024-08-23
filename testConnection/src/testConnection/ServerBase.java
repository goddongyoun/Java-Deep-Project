package testConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerBase {
	
	static boolean SEND_LOCKED = false;
	
	static boolean needSend = false;
	static String stringToBeSended;
	static InetAddress addressToSend;
	
	public static void main(String[] args) {
		try {
			System.out.println("Server trying to start...");
			
			//TCP
			ServerSocket tcpSock = new ServerSocket(1235);
			Socket tcpClientSock = new Socket();
			BufferedReader tcpReader;
			BufferedWriter tcpWriter;
			//final String MULTICAST_IP = "239.0.0.1";
			//final int MULTICAST_PORT = 4000;
			DatagramSocket udpSock = new DatagramSocket(4000);
			System.out.println("Port is " + udpSock.getPort());
			
			System.out.println("Server is receiving.");
			
			//멀티 스레드 고정 설정
			ExecutorService executorService = Executors.newFixedThreadPool(3);
			
			executorService.execute(()->{
				try {
					byte[] recvBuf = new byte[256];
					DatagramPacket udpRecvPack = new DatagramPacket(recvBuf, recvBuf.length);
					udpSock.receive(udpRecvPack);
					String recvMessage = new String(udpRecvPack.getData()).trim();
					if(recvMessage.equals("ConnectionTest") && SEND_LOCKED == false) {
						System.out.println("UDP received Connection Test " + udpRecvPack.getAddress());
						addressToSend = udpRecvPack.getAddress();
						stringToBeSended = "ConnectionGood";
						needSend = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			executorService.execute(()->{
				try {
					//System.out.println("0 came");
					byte[] sendBuf = new byte[255];
					DatagramPacket udpSendPack = new DatagramPacket(sendBuf, sendBuf.length);
					udpSendPack.setPort(4001);
					while(true){
						Thread.sleep(10);
						//System.out.println("Looping");
						if(needSend == true) {
							//System.out.println("1 came");
							if(addressToSend != null && stringToBeSended != null) {
								for(int i = 0; i<sendBuf.length; i++) {
									sendBuf[i] = 0;
								}
								sendBuf = stringToBeSended.getBytes();
								udpSendPack.setAddress(addressToSend);
								udpSendPack.setData(sendBuf);
								if(SEND_LOCKED == false) {
									//System.out.println("Sended");
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
			
			//UDP multicast
			/*executorService.execute(()->{
				try {
					byte[] sendBuf = new byte[256];
					MulticastSocket ms = new MulticastSocket();
					ms.setTimeToLive(255);
					System.out.println("TTL is " + ms.getTimeToLive() + " Is Closed = " + ms.isClosed());
					InetAddress addr = InetAddress.getByName(MULTICAST_IP);
					DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr, 4000);
					while(true) {
						sendBuf = "Send".getBytes();
						sendPacket.setData(sendBuf);
						ms.send(sendPacket);
						//System.out.println(new String(sendBuf) + " Sended");
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});*/
			
			//UDP BroadCast Test
			/*executorService.execute(()->{
				DatagramSocket socket = null;
				try {
		            // 브로드캐스트를 위한 소켓 생성
		            socket = new DatagramSocket();
		            socket.setBroadcast(true); // 브로드캐스트 허용 설정
		            
		            // 브로드캐스트 메시지
		            String message = "Hello, Broadcast!";
		            byte[] buffer = message.getBytes();
		            
		            // 브로드캐스트 주소 및 포트
		            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
		            int port = 9876;
		            
		            // DatagramPacket 생성
		            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, port);
		            
		            // 데이터 송신
		            while(true) {
			            socket.send(packet);
			            System.out.println("Broadcast message sent!");
			            Thread.sleep(1000);
		            }
		            
		        } catch (Exception e) {
		            e.printStackTrace();
		        } finally {
		            if (socket != null && !socket.isClosed()) {
		                socket.close();
		            }
		        }
			});*/
			
			
			//TCP receiver
			while(true) {
				tcpClientSock = tcpSock.accept();
				tcpReader = new BufferedReader(new InputStreamReader(tcpClientSock.getInputStream()));
				tcpWriter = new BufferedWriter(new OutputStreamWriter(tcpClientSock.getOutputStream()));
				String saver = tcpReader.readLine();
				if(saver.equals("ConnectionTest")) {
					System.out.println("TCP received Connection Test " + tcpClientSock.getInetAddress());
					tcpWriter.write("Connection Good"); tcpWriter.newLine(); tcpWriter.flush();
				}
				else {
					System.out.println("? " + saver + tcpClientSock.getInetAddress());
					tcpWriter.write("Unknown Request."); tcpWriter.newLine(); tcpWriter.flush();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
