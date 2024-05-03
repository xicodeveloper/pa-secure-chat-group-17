import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Main {
    public static void main(String[] args) {
        Lock trinco = new ReentrantLock();


        // Iniciar o servidor em uma thread separada
        new Thread(() -> {
            Server server = new Server();
            server.start(trinco);
        }).start();
    }

    public static void criarCliente(String nomeCliente,int numeroClientes) {

        new Thread(() -> {
            Client client = new Client();
            client.start(nomeCliente, numeroClientes);
        }).start();

    }
}