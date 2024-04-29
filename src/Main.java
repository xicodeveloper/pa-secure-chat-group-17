import java.io.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            // Carregar configurações do arquivo project.properties
            Properties properties = new Properties();
            properties.load(new FileInputStream("project.properties"));
            int port = Integer.parseInt(properties.getProperty("port"));

            // Iniciar o servidor em uma thread separada
            new Thread(() -> {
                Server server = new Server(port);
                server.start();
            }).start();

            // Nomes dos clientes
            String[] names = {"Cliente1", "Cliente2", "Cliente3", "Cliente4"};

            // Iniciar os clientes em threads separadas
            for (int i = 0; i < 4; i++) {
                final String finalName = names[i];
                new Thread(() -> {
                    Client client = new Client();
                    client.start(finalName);
                }).start();
            }
            new LoginInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
