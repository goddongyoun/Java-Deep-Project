package Dummy;

public class Dummies {
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
	
	
	// AS CLIENT
	
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
}	