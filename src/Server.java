import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A classe que representa o servidor de chat.
 */
public class Server {
    /** A lista de manipuladores de cliente conectados ao servidor. */
    public static final LinkedList<ClientHandler> clients = new LinkedList<>();

    private JFrame serverFrame;
    private JButton criarClienteButton;
    private JTextArea mensagemEnviar;
    private static int nrClientesMenos1;

    String nomeClienteNovo;

    /**
     * Método para exibir a interface gráfica do servidor.
     */
    private void serverInterface() {
        serverFrame = new JFrame("Interface do Server");
        serverFrame.setSize(300, 150);
        serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Cria o botão "criar novo cliente"
        criarClienteButton = new JButton("Criar Novo Cliente");

        // Adiciona um ouvinte de evento ao botão
        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());

        // Área para escrever mensagens
        mensagemEnviar = new JTextArea(5, 30);
        painel.add(mensagemEnviar);

        criarClienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nomeClienteNovo = mensagemEnviar.getText();
                Main.criarCliente(nomeClienteNovo, nrClientesMenos1);
                mensagemEnviar.setText("");
                nrClientesMenos1++;
            }
        });
        painel.add(criarClienteButton, BorderLayout.SOUTH);

        // Adiciona o painel à interface do servidor
        serverFrame.add(painel);

        // Exibe a interface do servidor
        serverFrame.setVisible(true);
    }

    /**
     * Método para decrementar o número de clientes.
     */
    public static void downnrClientes() {
        nrClientesMenos1--;
    }

    /**
     * Método para iniciar o servidor.
     * @param trinco O trinco usado para controle de concorrência.
     */
    public void start(Lock trinco) {
        try {
            serverInterface();
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
                broadcastGlobalExcept(nomeClienteNovo + ": entrou no chat", clientHandler);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para enviar a chave pública a todos os clientes, exceto um cliente específico.
     * @param publicKey A chave pública a ser enviada.
     * @param clienttName O nome do cliente associado à chave pública.
     * @param excludeClient O cliente a ser excluído da lista de destinatários.
     */
    public static void broadcastKey(PublicKey publicKey, String clienttName, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendKey(publicKey, clienttName);
            }
        }
    }

    /**
     * Método para enviar uma mensagem a um cliente específico.
     * @param message A mensagem a ser enviada.
     * @param cliente O cliente de destino.
     */
    public static void broadcast(String message, String cliente) {
        for (ClientHandler client : clients) {
            if (Objects.equals(client.getClientName(), cliente)) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * Método para enviar uma mensagem para todos os clientes conectados.
     * @param message A mensagem a ser enviada.
     */
    public static void broadcastGlobal(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    /**
     * Método para enviar uma mensagem para todos os clientes conectados, exceto um cliente específico.
     * @param message A mensagem a ser enviada.
     * @param excludeClient O cliente a ser excluído da lista de destinatários.
     */
    public static void broadcastGlobalExcept(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }
}

/**
 * A classe que lida com a comunicação com cada cliente individual.
 */
class ClientHandler extends Thread {
    private final ObjectInputStream reader;
    private final ObjectOutputStream writer;
    private String clientName;
    private final Lock trinco;
    private final LocalDateTime joinTime;

    /**
     * Construtor para inicializar o manipulador do cliente.
     * @param socket O soquete de comunicação com o cliente.
     * @param trinco O trinco usado para controle de concorrência.
     * @throws IOException Se ocorrer um erro de entrada/saída.
     * @throws ClassNotFoundException Se a classe não puder ser encontrada.
     */
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

    /**
     * Método para formatar o tempo.
     * @param time O tempo a ser formatado.
     * @return Uma string representando o tempo formatado.
     */
    private String formatTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(formatter);
    }

    /**
     * Método para obter o nome do cliente associado a este manipulador.
     * @return O nome do cliente.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * O loop de execução do manipulador do cliente.
     */
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
                    String message = (String) input;
                    if (Objects.equals(message, "@newUser")) {
                        Server.broadcastGlobalExcept("@newUser", this);
                    } else {
                        System.out.println("Received from client " + clientName + ": " + message);
                        // Mensagem não é uma mensagem privada, envie para todos os clientes
                        String[] partes = message.split(":", 2);
                        String cliente = partes[0];
                        String mensagem = partes[1];
                        System.out.println(mensagem);
                        if (mensagem.equals("@exit")) {
                            System.out.println("Client " + cliente + " exited.");
                            Iterator<ClientHandler> iterator = Server.clients.iterator();
                            while (iterator.hasNext()) {
                                ClientHandler client = iterator.next();
                                if (client.getClientName().equals(cliente)) {
                                    iterator.remove(); // Remove o cliente da lista de clientes
                                }
                            }
                            Server.broadcastGlobal(cliente + ": saiu do chat");
                            Server.downnrClientes();
                            break; // Sai do loop e encerra a thread
                        } else {
                            Server.broadcast(clientName + ":" + mensagem, cliente);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return;
    }

    /**
     * Método para enviar a chave pública a este cliente.
     * @param publicKey A chave pública a ser enviada.
     * @param clienttName O nome do cliente associado à chave pública.
     */
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

    /**
     * Método para enviar uma mensagem para este cliente.
     * @param message A mensagem a ser enviada.
     */
    public void sendMessage(String message) {
        try {
            writer.writeObject(message);
            writer.flush();
        } catch (IOException e) {
            if (e instanceof SocketException) {
                System.out.println("Erro ao enviar mensagem para o cliente " + clientName + ": conexão fechada pelo cliente.");
            } else {
                e.printStackTrace();
            }
        }
    }
}
