import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 5000;
    public static final List<ClientHandler> clients = new ArrayList<>();

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);

                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }
    // Método para enviar uma mensagem para um ou mais clientes especificados
    public static void sendMessageToClients(String message, List<String> recipientNames) {
        for (ClientHandler client : clients) {
            if (recipientNames.contains(client.getClientName())) {
                client.sendMessage(message);
            }
        }
    }
}

class ClientHandler extends Thread {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private String clientName;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        // Ler o nome do cliente quando a conexão é estabelecida
        this.clientName = reader.readLine();
        System.out.println(clientName + " connected: " + socket);
    }
    public String getClientName() {
        return clientName;
    }

    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received from client " + clientName + ": " + message);
                if (message.startsWith("@")) {
                    // Extrair os nomes dos destinatários da mensagem
                    int spaceIndex = message.indexOf(" ");
                    if (spaceIndex != -1) {
                        String recipientsString = message.substring(1, spaceIndex);
                        String messageContent = message.substring(spaceIndex + 1);
                        List<String> recipientNames = Arrays.asList(recipientsString.split(","));
                        System.out.println("Received private message for clients " + recipientNames + ": " + messageContent);
                        Server.sendMessageToClients(clientName + ": " + messageContent, recipientNames);
                    } else {
                        System.err.println("Invalid format for private message: " + message);
                    }
                } else {
                    // Mensagem não é uma mensagem privada, envie para todos os clientes
                    Server.broadcast(clientName + ": " + message, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println("Client disconnected: " + socket);
                Server.clients.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

