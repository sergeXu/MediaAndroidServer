import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//For no Frame
class workThreads {
	public static ArrayList<Socket> socketList = new ArrayList<Socket>();
	public static ArrayList<PubChannelInfo> pubVideoInfo = new ArrayList<PubChannelInfo>();
	public static ArrayList<ChaterInfo> chaterInfo = new ArrayList<ChaterInfo>();
	private static Object lock = new Object();
	private static Object userlock = new Object();
	public static Object socketlock = new Object();
	// private static final int ToChatPort = 35001;
	private static Logger log = new Logger();

	public static void serverRun() throws IOException {
		// listen clinent chatting thread
		new Thread(new Runnable() {
			private static final int LISTEN_CHAT_PORT = 20000;

			@Override
			public void run() {
				try {
					int count = 1;
					ServerSocket s = new ServerSocket(LISTEN_CHAT_PORT);
					System.out.println("服务器创建成功！\n");
					while (true) {
						Socket incoming = s.accept();
						System.out.println(incoming.getRemoteSocketAddress() + "连接上服务器\n");
						System.out.println("Spawning " + count + "\n");
						// add to list
						synchronized (socketlock) {
							socketList.add(incoming);
						}
						Runnable r = new ThreadedEchoHandler(incoming);
						Thread t = new Thread(r);
						t.start();
						count++;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("io exception in Chatting start\n");
				}
			}
		}).start();
		// listen client play thread
		new Thread(new Runnable() {
			private static final int LISTEN_CHAT_PORT = 35030;
			private byte[] msg = new byte[2048];
			private boolean life = true;

			@Override
			public void run() {
				try {
					DatagramSocket dSocket = null;
					DatagramPacket dPacket = new DatagramPacket(msg, msg.length);
					String isol = "%%##";
					System.out.println("客户端观看信息服务器创建成功！\n");
					dSocket = new DatagramSocket(LISTEN_CHAT_PORT);
					while (life) {
						dSocket.receive(dPacket);// put in packet
						String datas = new String(dPacket.getData(), 0, dPacket.getLength(), "UTF-8");
						System.out.println("playing msg received：" + datas + "\n");
						// client heartbeat
						if (datas.charAt(0) == '4' && datas.charAt(1) == '4' && datas.charAt(2) == '4'
								&& datas.charAt(3) == '4') {
							String[] infos = datas.split(isol);
							InetAddress clientIP = null;
							clientIP = dPacket.getAddress();
							int portNum = dPacket.getPort();
							// int portNum = Integer.parseInt(infos[1]);
							ChaterInfo infoNsg = new ChaterInfo(clientIP, portNum);
							synchronized (userlock) {
								if (!(chaterInfo.contains(infoNsg))) {
									chaterInfo.add(infoNsg);
									log.logTofile(time()+"add player "+clientIP);
								} else {
									// update heartbeat
									for (int i = 0; i < chaterInfo.size(); i++) {
										if (chaterInfo.get(i).GetIpInfo().toString().equals(clientIP.toString())) {
											chaterInfo.get(i).SetHeartBeat(10);
											System.out.println(i + "  heartBeat has been update\n");
										}
									}
								}
							}
						}
						//
						// else if (datas.charAt(0) == '1' && datas.charAt(1) ==
						// '1' && datas.charAt(2) == '1'
						// && datas.charAt(3) == '1')
						// {
						// DatagramSocket dSocketSent = null;
						// byte[] msgSend = datas.getBytes("utf-8");
						// DatagramPacket dPacketSend = null;
						// synchronized (userlock) {
						// for(ChaterInfo info :chaterInfo )
						// {
						// InetAddress clientIP = info.GetIpInfo();
						//
						//
						// int clientPort = info.getPort();
						// dPacketSend = new DatagramPacket(msgSend,
						// msgSend.length, clientIP , clientPort);
						// try {
						// dSocket.send(dPacketSend);
						// messages.append(msgSend+"sent to "+ clientIP+ "
						// "+clientPort);
						// } catch (IOException e) {
						// e.printStackTrace();
						// }
						// }
						// }
						//
						// }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		// listen client publish info thread
		new Thread(new Runnable() {
			private static final int LISTEN_PUB_PORT = 35032;

			private byte[] msg = new byte[1024];

			private boolean life = true;

			@Override
			public void run() {
				DatagramSocket dSocket = null;
				DatagramPacket dPacket = new DatagramPacket(msg, msg.length);
				System.out.println("监听频道信息服务器创建成功！\n");
				try {
					dSocket = new DatagramSocket(LISTEN_PUB_PORT);
					while (life) {
						try {
							// dSocket.receive(dPacket);
							// byte[] data = dPacket.getData();
							// System.out.println("msg sever received："+new
							// String(dPacket.getData()));
							dSocket.receive(dPacket);
							// clean useless data in the end
							// String datas = new String(dPacket.getData(), 0,
							// dPacket.getLength());
							String datas = new String(dPacket.getData(), 0, dPacket.getLength(), "UTF-8");
							System.out.println("publish msg received：" + datas + "\n");
							// head message test
							if (datas.charAt(0) == '2' && datas.charAt(1) == '2' && datas.charAt(2) == '2'
									&& datas.charAt(3) == '2') {
								PubChannelInfo infoNsg = new PubChannelInfo(datas);
								synchronized (lock) {
									if (!(pubVideoInfo.contains(infoNsg))) {
										pubVideoInfo.add(infoNsg);
										log.logTofile(time()+"add pubber "+infoNsg.GetinfoMsg());
									} else {
										// update heartbead
										for (int i = 0; i < pubVideoInfo.size(); i++) {
											if (pubVideoInfo.get(i).GetinfoMsg().equals(datas)) {
												pubVideoInfo.get(i).SetHeartBeat(5);
												System.out.println(i + "  heartBeat has been update\n");
											}
										}
									}
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
				}

			}
		}).start();

		// listen request from client thread
		new Thread(new Runnable() {
			private static final int VideoInfoQuestPORT = 35034;
			private boolean life = true;
			private byte[] msgRec = new byte[512];
			private byte[] msgSend = new byte[1024];
			private String isol_big = "%####";

			@Override
			public void run() {
				DatagramSocket dSocket = null;
				DatagramPacket dPacket = new DatagramPacket(msgRec, msgRec.length);
				DatagramPacket dPacketSend = new DatagramPacket(msgSend, msgSend.length);
				System.out.println("监听客户端请求频道信息服务器创建成功！\n");
				try {
					dSocket = new DatagramSocket(VideoInfoQuestPORT);
					while (life) {
						try {
							dSocket.receive(dPacket);

							String datas = new String(dPacket.getData(), 0, dPacket.getLength());
							// System.out.println("msg sever received：" +
							// datas);
							// 3333is the head info
							if (datas.charAt(0) == '3' && datas.charAt(1) == '3' && datas.charAt(2) == '3'
									&& datas.charAt(3) == '3')
							// send message
							{
								StringBuilder sb = new StringBuilder("");
								sb.append(pubVideoInfo.size());
								if (pubVideoInfo.size() > 0) {
									synchronized (lock) {
										for (PubChannelInfo str : pubVideoInfo) {
											sb.append(isol_big);
											sb.append(str.GetinfoMsg());
										}
									}
								} else {
									sb.append(isol_big);
									sb.append(" ");
								}
								System.out.println("return msg is : " + sb.toString() + "\n");
								// send message
								msgSend = sb.toString().getBytes("utf-8");
								InetAddress clientIP = null;
								// get client IP
								clientIP = dPacket.getAddress();
								// get client port
								int clientPort = dPacket.getPort();

								System.out.println("@@@@" + clientIP + "  " + clientPort + "\n");
								// try {
								//
								// destination =
								// InetAddress.getByName("192.168.1.5"); //the
								// destinate purpose address
								// } catch (UnknownHostException e) {
								// messages.append("Cannot open findhost!");
								// System.exit(1);
								// }
								dPacketSend = new DatagramPacket(msgSend, msgSend.length, clientIP, clientPort);

								try {
									dSocket.send(dPacketSend); // sending
								} catch (IOException e) {
								}
							}

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
				}
			}
		}).start();

		// publish list clean thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("发布端更新线程启动！\n");
					while (true) {
						synchronized (lock) {
							for (int i = 0; i < pubVideoInfo.size(); i++) {
								// minus heartbeat
								pubVideoInfo.get(i).minHeartBeat();
								if (pubVideoInfo.get(i).GetHeartBeat() < 0) {
									System.out.println(pubVideoInfo.get(i).GetinfoMsg() + "has been removed\n");					
									log.logTofile(time()+"del pubber "+pubVideoInfo.get(i).GetinfoMsg());
									pubVideoInfo.remove(i);
								}
							}
						}
						// UI threads protect

						// System.out.println("puber num :::
						// "+pubVideoInfo.size()+"\n");
						System.out.println("发布视频用户: " + pubVideoInfo.size());

						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		// publish list clean chat user thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("播放用户更新线程启动！\n");
					while (true) {
						synchronized (userlock) {
							if (chaterInfo.size() > 0)
								System.out.println(time() + "\n");
							for (int i = 0; i < chaterInfo.size(); i++) {
								// minus heartbeat
								chaterInfo.get(i).minHeartBeat();
								// show play client list
								System.out.println("log :   " + chaterInfo.get(i).GetIpInfo().toString() + "  "
										+ chaterInfo.get(i).getPort() + "\n");
								if (chaterInfo.get(i).GetHeartBeat() < 0) {
									System.out.println(chaterInfo.get(i).GetIpInfo() + "has been removed\n");
									log.logTofile(time()+"del player "+chaterInfo.get(i).GetIpInfo());
									chaterInfo.remove(i);
								}
							}
						}
						// UI threads protect

						// System.out.println("user num :::
						// "+chaterInfo.size()+"\n");
						System.out.println("当前观看视频用户: " + chaterInfo.size());

						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static String time() {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(date);
		return time;
	}
}