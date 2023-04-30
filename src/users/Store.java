package users;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Store {
    private Seller seller;
    private String storeName;
    private static final String STORE_LIST_PATH = "file" + File.separator + "store.txt";

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
        try (BufferedReader bf = new BufferedReader(new FileReader(new File(STORE_LIST_PATH)));
                PrintWriter pw = new PrintWriter(new FileOutputStream(new File(STORE_LIST_PATH), true))) {
            if (!bf.lines().map(s -> s.split(";;")[0]).filter(s -> s.equals(this.getStoreName())).findAny()
                    .isPresent()) {
                pw.write(getStoreName() + ";;" + getSeller());
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
        try (BufferedReader bf = new BufferedReader(new FileReader(new File(STORE_LIST_PATH)))) {
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