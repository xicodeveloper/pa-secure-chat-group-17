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
        // Iniciar os clientes em threads separadas
        String[] nomes ={"Cliente1", "Cliente2", "Cliente3", "Cliente4"};
        int numeroDeClientes=nomes.length;
        numeroDeClientes--;
        System.out.println(numeroDeClientes);
        for (int i = 0; i < numeroDeClientes+1; i++) {
            String finalName =nomes[i];
            int finalNumeroDeClientes = numeroDeClientes;
            new Thread(() -> {
                Client client = new Client();
                client.start(finalName, finalNumeroDeClientes);
            }).start();
        }
    }
}