package users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private Seller testSeller;
    private Customer testCustomer;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        testSeller = new Seller("seller@example.com", Roles.SELLER);
        testCustomer = new Customer("customer@example.com", Roles.CUSTOMER);
        testMessage = new Message(testSeller, testCustomer, "Test message", true, true, true);
    }

    @Test
    void fileExport() {
        String expected = "seller@example.com;;customer@example.com;;true;;Test message;;" + testMessage.getTime()
                + ";;true;;true\n";
        assertEquals(expected, testMessage.fileExport());
    }

    @Test
    void writeToRecord(@TempDir Path tempDir) throws IOException {
        File tempFile = Files.createFile(tempDir.resolve("message.txt")).toFile();
        Message.MESS_PATH = tempFile.getAbsolutePath();
        testMessage.writeToRecord();

        String content = new String(Files.readAllBytes(tempFile.toPath()));
        assertEquals(testMessage.fileExport(), content);
    }

    @Test
    void read() {
        assertEquals(testMessage.toString(), testMessage.read(testSeller));
        assertEquals(testMessage.toString(), testMessage.read(testCustomer));
    }

    @Test
    void notRead() {
        testMessage.setSellerVis(false);
        testMessage.setCustomerVis(false);

        assertNull(testMessage.read(testSeller));
        assertNull(testMessage.read(testCustomer));
    }

    @Test
    void equals() {
        Message sameMessage = new Message(testSeller, testCustomer, "Another message", true, true, true);
        sameMessage.time = testMessage.time;

        assertEquals(testMessage, sameMessage);
    }

    @Test
    void notEquals() {
        Message differentMessage = new Message(testSeller, testCustomer, "Another message", true, true, true);

        assertNotEquals(testMessage, differentMessage);
    }
}
