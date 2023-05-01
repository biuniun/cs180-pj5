import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Store {
    private Seller seller;
    private String storeName;

    public Store(Seller seller, String storeName) {
        this.seller = seller;
        this.storeName = storeName;
        saveStore();
    }

    public Seller getSeller() {
        return this.seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public boolean saveStore() {

        try (Socket socket = new Socket("127.0.0.1", 0);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(
                        new OutputStreamWriter(new Socket("127.0.0.1", 1).getOutputStream()));
                PrintWriter ph = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            ph.print("store\n0\n");
            pw.print("store\n1\n");
            pw.flush();
            ph.flush();
            if (!br.lines().map(s -> s.split(";;")[0]).filter(s -> s.equals(this.getStoreName())).findAny()
                    .isPresent()) {
                pw.write(getStoreName() + ";;" + getSeller());
                pw.flush();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    public static Map<String, String> getStores() {
        Map<String, String> map = new HashMap<>();
        try (Socket socket = new Socket("127.0.0.1", 0);
            BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            pw.write("store\n0\n");
            pw.flush();
            bf.lines().forEach(l -> {
                String[] args = l.split(";;");
                map.put(args[0], args[1]);
            });
            return map;
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

}