import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {


    public static final LinkedList<ClientHandler> clients = new LinkedList<>();

    public void start(Lock trinco) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("project.properties"));
            int port = Integer.parseInt(properties.getProperty("port"));
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is running on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, trinco);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastKey(PublicKey publicKey, String clienttName, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendKey(publicKey, clienttName);
            }
        }
    }

    public static void broadcast(String message, String cliente) {
        for (ClientHandler client : clients) {
            if (Objects.equals(client.getClientName(), cliente)) {
                client.sendMessage(message);
            }
        }
    }
}

class ClientHandler extends Thread {
    private final ObjectInputStream reader;
    private final ObjectOutputStream writer;
    private String clientName;
    private final Lock trinco;
    private final LocalDateTime joinTime;

    public ClientHandler(Socket socket, Lock trinco) throws IOException, ClassNotFoundException {
        this.trinco = trinco;
        this.reader = new ObjectInputStream(socket.getInputStream());
        this.writer = new ObjectOutputStream(socket.getOutputStream());
        // Ler o nome do cliente quando a conexão é estabelecida
        Object input = reader.readObject();
        if (input instanceof String) {
            String name = (String) input;
            this.clientName = name;
        }
        this.joinTime = LocalDateTime.now(); // Record the time when the client joined
        System.out.println((String) clientName + " connected: " + socket + " at " + formatTime(joinTime));
    }

    private String formatTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(formatter);
    }

    public String getClientName() {
        return clientName;
    }

    public void run() {
        while (true) {
            try {
                Object input = reader.readObject();
                if (input instanceof PublicKey) {
                    trinco.lock();
                    PublicKey publicKey = (PublicKey) input;
                    Server.broadcastKey(publicKey, clientName, this);
                    try {
                        // Faz a thread dormir por 1 segundo (1000 milissegundos)
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    trinco.unlock(); // Libera o trinco
                }
                if (input instanceof String) {
                    String messagee = (String) input;
                    System.out.println("Received from client " + clientName + ": " + messagee);
                    String message = messagee;
                    if (messagee.equals("@exit")) {
                        System.out.println("Client " + clientName + " exited.");
                        Server.clients.remove(this); // Remove o cliente da lista de clientes
                        Server.broadcast(clientName + " saiu do chat", clientName);
                        break; // Sai do loop e encerra a thread
                    }
                    while (message != null) {
                        // Mensagem não é uma mensagem privada, envie para todos os clientes
                        String[] partes = message.split(":", 2);
                        String cliente = partes[0];
                        String mensagem = partes[1];
                        Server.broadcast(clientName + ":" + mensagem, cliente);
                        message = null;
                    }

                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }finally {
                Server.broadcast(clientName , " saiu do chat.");
            }
        }
    }

    public void sendKey(PublicKey publicKey, String clienttName) {
        try {
            writer.writeObject(publicKey);
            writer.flush();
            writer.writeObject(clienttName);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            writer.writeObject(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
