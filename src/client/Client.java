package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import users.User;

public class Client extends Socket {
	private BufferedReader reader;
	private PrintWriter writer;

	public Client(String ip, int port) throws IOException {
		super(ip, port);
		this.reader = new BufferedReader(new InputStreamReader(getInputStream()));
		this.writer = new PrintWriter(new OutputStreamWriter(getOutputStream()));
	}

	/**
	 * take parameters that needed for message protocol, and send the
	 * protocol-followed string to server.
	 * 
	 * @return
	 */
	public boolean sendMessage() {
		// TODO
		return false;
	}

	public List<String> updateMessage() {
		return null;
	}

	public void messageOperations() {

	}

	/**
	 * take parameters that needed for login protocol with uid and account, and send
	 * the protocol-followed string to server.
	 * 
	 * @return return user account and
	 */


	public void userOperations() {

	}
	
	public User login() {
		// TODO
		return null;
	}

	public boolean block() {
		return false;
	}

	public String[] dashboard() {
		return null;
	}

	/**
	 * record import and export here.
	 */

}
