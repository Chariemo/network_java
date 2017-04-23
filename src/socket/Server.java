package socket;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket = new ServerSocket(3456);
		while(true){
			Socket socket = serverSocket.accept();
			new Thread(new ServerThread(socket)).start();}}}
class ServerThread implements Runnable{
	private Socket socket;
	private File file = new File("src/socket/data.txt");
	BufferedReader buff = null, input = null;
	public ServerThread(Socket socket) throws IOException {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		buff = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	private String[] fileCon(String[] fileString) throws IOException{
		String tmp = null, string = "";
		while((tmp = buff.readLine()) != null)
			string += tmp;
		fileString = string.split(",");
		string = null;
		return fileString;}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			String[] fileContent = null;
			String content = null;
			fileContent = fileCon(fileContent);
			while((content = input.readLine()) != null){
				System.out.println(content);
				PrintStream pStream = new PrintStream(socket.getOutputStream());
				try{
						String[] tmp = content.split(",");
						for(int i = 0; i < tmp.length; i++)
							pStream.println(fileContent[Integer.parseInt(tmp[i])]);
				} catch (Exception e) {
					// TODO: handle exception
					pStream.println("The index is null, please input another index.");}}
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("The client has closed");}}	}

