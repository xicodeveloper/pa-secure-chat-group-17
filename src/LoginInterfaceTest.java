import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginInterfaceTest {

    @Test
    void testSetStoredUsername() {
        LoginInterface loginInterface = new LoginInterface();
        String username = "testUser";
        loginInterface.setstoredUsername(username);
        assertEquals(username, loginInterface.getstoredUsername());
    }

    @Test
    void testGetStoredUsername() {
        LoginInterface loginInterface = new LoginInterface();
        String username = "testUser";
        loginInterface.setstoredUsername(username);
        assertEquals(username, loginInterface.getstoredUsername());
    }

    @Test
    void testSetLogin() {
        LoginInterface loginInterface = new LoginInterface();
        loginInterface.setLogin(true);
        assertTrue(loginInterface.getLogin());
    }

    @Test
    void testGetLogin() {
        LoginInterface loginInterface = new LoginInterface();
        loginInterface.setLogin(true);
        assertTrue(loginInterface.getLogin());
    }

    @Test
    void testWaitForLogin() {
        LoginInterface loginInterface = new LoginInterface();


        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loginInterface.setLogin(true);
        });


        thread.start();


        assertTrue(loginInterface.waitForLogin());
    }
}
