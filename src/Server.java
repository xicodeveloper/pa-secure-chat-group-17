import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);

                ClientHandler clientHandler = new ClientHandler(socket, this); // Passa uma referência de Server para ClientHandler
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Remove um cliente da lista de clientes
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    // Envia uma mensagem para todos os clientes, exceto um cliente específico
    public void broadcast(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    // Envia uma mensagem para clientes específicos
    // Envia uma mensagem para clientes específicos
    public void sendMessageToClients(String message, List<String> recipientNames) {
        for (Iterator<ClientHandler> iterator = clients.iterator(); iterator.hasNext();) {
            ClientHandler client = iterator.next();
            if (recipientNames.contains(client.getClientName())) {
                if (client.isSocketConnected()) {
                    client.sendMessage(message);
                } else {
                    clients.remove(client);
                    iterator.remove();
                }
            }
        }
    }


    class ClientHandler extends Thread {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final String clientName;
    private final Server server; // Referência para a instância de Server

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.clientName = reader.readLine();
        this.server = server; // Atribui a referência de Server
        System.out.println(clientName + " connected: " + socket);
        sendMessage(" "+clientName+" Juntou-se ao chat.");
    }

    public String getClientName() {
        return clientName;
    }
        public boolean isSocketConnected() {
            return socket != null && socket.isConnected() && !socket.isClosed();
        }
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equals("@exit")) {
                    break;
                }
                System.out.println("Received from client " + clientName + ": " + message);
                if (message.startsWith("@")) {
                    int spaceIndex = message.indexOf(" ");
                    if (spaceIndex != -1) {
                        String recipientsString = message.substring(1, spaceIndex);
                        String messageContent = message.substring(spaceIndex + 1);
                        List<String> recipientNames = Arrays.asList(recipientsString.split(","));
                        System.out.println("Received private message for clients " + recipientNames + ": " + messageContent);
                        server.sendMessageToClients(clientName + ": " + messageContent, recipientNames); // Usa a referência de Server
                    } else {
                        System.err.println("Invalid format for private message: " + message);
                    }
                } else {
                    server.broadcast(clientName + ": " + message, this); // Usa a referência de Server
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println("Client disconnected: " + socket);
                server.removeClient(this); // Usa a referência de Server para remover o cliente
                server.broadcast(clientName + " saiu do chat.", this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            if (socket.isConnected() && !socket.isClosed()) { // Verifica se o socket está conectado e não fechado
                writer.write(message);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
}


