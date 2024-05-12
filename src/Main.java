import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A classe principal que inicia o servidor e cria clientes.
 */
public class Main {

    /**
     * O método principal que inicia o servidor em uma thread separada.
     * @param args Os argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        Lock trinco = new ReentrantLock();

        // Iniciar o servidor em uma thread separada
        new Thread(() -> {
            Server server = new Server();
            server.start(trinco);
        }).start();
    }

    /**
     * Um método para criar um cliente em uma thread separada.
     * @param nomeCliente O nome do cliente.
     * @param numeroClientes O número total de clientes.
     */
    public static void criarCliente(String nomeCliente, int numeroClientes) {
        new Thread(() -> {
            Client client = new Client();
            client.start(nomeCliente, numeroClientes);
        }).start();
    }
}
