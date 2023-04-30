package users;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

public class Message {
	private static final String MESS_PATH = "file" + File.separator + "message.txt";
	private Seller seller;
	private Customer customer;
	private boolean sender; // true for seller, false for customers
	private String message;
	private long time;
	private boolean sellerVis;
	private boolean customerVis;

	public Message(String record) {
		String[] args = record.split(";;");
		this.seller = new Seller(args[0]);
		this.customer = new Customer(args[1]);
		this.sender = args[2].equals("true");
		this.message = args[3];
		this.time = Long.parseLong(args[4]);
		this.sellerVis = args[5].equals("true");
		this.customerVis = args[6].equals("true");
	}

	public Message(Seller seller, Customer customer, String message, boolean sellerVis,
			boolean customerVis, boolean sender) {
		this.seller = seller;
		this.customer = customer;
		this.message = message;
		this.time = Timestamp.from(Instant.now()).getTime();
		this.sellerVis = sellerVis;
		this.customerVis = customerVis;
		this.sender = sender;
	}

	public Seller getSeller() {
		return this.seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTime() {
		return this.time;
	}

	public boolean isSellerVis() {
		return this.sellerVis;
	}

	public boolean getSellerVis() {
		return this.sellerVis;
	}

	public void setSellerVis(boolean sellerVis) {
		this.sellerVis = sellerVis;
	}

	public boolean isCustomerVis() {
		return this.customerVis;
	}

	public boolean getCustomerVis() {
		return this.customerVis;
	}

	public void setCustomerVis(boolean customerVis) {
		this.customerVis = customerVis;
	}

	public boolean isSender() {
		return sender;
	}

	public void writeToRecord() {
		try (PrintWriter pw = new PrintWriter(new FileOutputStream(new File(MESS_PATH), true))) {
			pw.println(fileExport());
		} catch (FileNotFoundException e) {
			System.out.println("Error writing the message, contact administrator!");
		}
	}

	public String fileExport() {
		return getSeller() + ";;"
				+ getCustomer() + ";;"
				+ isSender() + ";;"
				+ getMessage() + ";;"
				+ getTime() + ";;"
				+ isSellerVis() + ";;"
				+ isCustomerVis() + "\n";
	}

	@Override
	public String toString() {
		return (isSender() ? getSeller() : getCustomer()) + " -> " + (isSender() ? getCustomer() : getSeller()) + "\n\t"
				+ this.getMessage();
	}

	public String read(User user) {
		if (user.equals(seller) && sellerVis)
			return toString();
		if (user.equals(customer) && customerVis)
			return toString();

		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Message)) {
			return false;
		}
		Message message = (Message) o;
		return message.getTime() == this.getTime() && message.isSender() == this.isSender();
	}

	public static boolean tidy() {
		ArrayList<Message> messages = new ArrayList<>();
		try (BufferedReader bf = new BufferedReader(new FileReader(new File(MESS_PATH)))) {
			bf.lines().filter(s -> !s.isBlank()).forEach(s -> {
				Message m = new Message(s);
				if (messages.contains(m))
					messages.set(messages.indexOf(m), m);
				else 
					messages.add(m);
			});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		try (PrintWriter pw = new PrintWriter(new FileOutputStream(new File(MESS_PATH), false), true)) {
			messages.forEach(c -> pw.print(c.fileExport()));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}