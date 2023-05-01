import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Session {
	private static final int PORT = 8888;
	private static final int MAX_CONN = 10;
	private static final String ACCOUNT_INFO_PATH = "file" + File.separator + "account_list.txt";
	private static final String MESS_PATH = "file" + File.separator + "message.txt";
	private static final String STORE_LIST_PATH = "file" + File.separator + "store.txt";
	private static final File account = new File(ACCOUNT_INFO_PATH);
	private static final File message = new File(MESS_PATH);
	private static final File store = new File(STORE_LIST_PATH);

	public static void main(String[] args) {
		try (ServerSocket server = new ServerSocket(PORT)) {
			int connection = 0;
			for (;;) {
				if (connection >= MAX_CONN)
					continue;

				Socket ss = server.accept();
				connection++;
				new Thread(() -> {
					try (BufferedReader br = new BufferedReader(new InputStreamReader(ss.getInputStream()));
							PrintWriter pw = new PrintWriter(new OutputStreamWriter(ss.getOutputStream()))) {
						String file = br.readLine();
						int rw = Integer.parseInt(br.readLine());
						File toRead = null;
						switch (file) {
							case "account" -> toRead = account;
							case "message" -> toRead = message;
							case "store" -> toRead = store;
						}
						switch (rw) {
							case 0 -> read(toRead, pw);
							case 1 -> write(toRead, br, true);
							case 2 -> write(toRead, br, false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static synchronized void read(File targetFile, PrintWriter pw) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(targetFile));
		br.lines().forEach(pw::println);
		pw.flush();
		pw.close();
		br.close();
	}

	private static synchronized void write(File readFile, BufferedReader br, boolean append) throws Exception {
		PrintWriter pw = new PrintWriter(new FileWriter(readFile, append));
		for (int i = 0; i < Integer.parseInt(br.readLine()); i++) 
			pw.println(br.readLine());
		pw.close();
	}
}
