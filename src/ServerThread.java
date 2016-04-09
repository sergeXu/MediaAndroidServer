
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ServerThread implements Runnable {
	// define Socket
	private Socket socket = null;
	// deal with putin steam
	private BufferedReader br = null;

	private DataOutputStream toclient;

	// private DataInputStream fromclient;
	public ServerThread(Socket socket) throws IOException {
		this.socket = socket;
		//initailise Socket input
		br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
		// fromclient = new DataInputStream(socket.getInputStream());
	}

	@Override
	public void run() {
		try {
			String content = null;
			//reading data from client Socket
			while ((content = readFromClient()) != null) {
				// send to all socket
				for (Socket s : ServerFrame.socketList) {
					OutputStream os = s.getOutputStream();
					// os.write((content + "\n").getBytes("utf-8"));
					toclient = new DataOutputStream(os);
					toclient.writeUTF((content + "\n"));
				}
				System.out.println("sent packege finish");
			}
		} catch (Exception e) {
			ServerFrame.socketList.remove(socket);
			e.printStackTrace();
			System.out.println("sent packege malfunctional");
		}
		// thread end
		System.out.println("sent packege thread end");
	}

	/**
	 * define reading method
	 * 
	 * @return
	 */
	private String readFromClient() {

		try {

			String content = "";
			String temp;
			// head message
			char code[] = { '1', '1', '1', '1' };
			while (((temp = br.readLine()) != null) && !(temp.equals("%%##%%"))) {
				//
				content += temp + "\n";
				// System.out.println("temp="+temp);
				temp = "";
			}
			// void data
			if (content.equals(""))
				return null;
			System.out.println("content=" + content);
			if ((content.charAt(0) == code[0]) && (content.charAt(1) == code[1]) && (content.charAt(2) == code[2])
					&& (content.charAt(3) == code[3])) {
				return content;
			} else {
				System.out.println("Invalid informations");
				return null;
			}
			// return br.readLine();

			// return fromclient.readUTF();
		}
		// if catch exception£¬Socket should be ended
		catch (Exception e) {
			// delete Socket
			ServerFrame.socketList.remove(socket);
			e.printStackTrace();
			System.out.println("readFromClient malfunctional");
		}
		return null;
	}
}