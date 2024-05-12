import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Uma classe de teste para testar os métodos da classe LoginInterface.
 */
class LoginInterfaceTest {

    /**
     * Testa o método setStoredUsername da classe LoginInterface.
     */
    @Test
    void testSetStoredUsername() {
        LoginInterface loginInterface = new LoginInterface();
        String username = "testUser";
        loginInterface.setStoredUsername(username);
        assertEquals(username, loginInterface.getStoredUsername());
    }

    /**
     * Testa o método getStoredUsername da classe LoginInterface.
     */
    @Test
    void testGetStoredUsername() {
        LoginInterface loginInterface = new LoginInterface();
        String username = "testUser";
        loginInterface.setStoredUsername(username);
        assertEquals(username, loginInterface.getStoredUsername());
    }

    /**
     * Testa o método setLogin da classe LoginInterface.
     */
    @Test
    void testSetLogin() {
        LoginInterface loginInterface = new LoginInterface();
        loginInterface.setLogin(true);
        assertTrue(loginInterface.getLogin());
    }

    /**
     * Testa o método getLogin da classe LoginInterface.
     */
    @Test
    void testGetLogin() {
        LoginInterface loginInterface = new LoginInterface();
        loginInterface.setLogin(true);
        assertTrue(loginInterface.getLogin());
    }

    /**
     * Testa o método waitForLogin da classe LoginInterface.
     */
    @Test
    void testWaitForLogin() {
        LoginInterface loginInterface = new LoginInterface();

        // Cria uma thread para simular o processo de login
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000); // Simula um atraso de 1 segundo para o processo de login
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loginInterface.setLogin(true); // Define o login como bem-sucedido após o atraso
        });

        thread.start(); // Inicia a thread

        assertTrue(loginInterface.waitForLogin()); // Verifica se o login foi bem-sucedido
    }
}
