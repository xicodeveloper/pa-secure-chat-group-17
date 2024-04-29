import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private static final String SERVER_IP = "localhost";

    private Properties properties;
    private int port;
    private boolean running = true;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private String name;

    public Client() {
        // Inicialização das propriedades
        try {
            properties = new Properties();
            properties.load(new FileInputStream("project.properties"));
            port = Integer.parseInt(properties.getProperty("port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(String name) {
        this.name = name;
        criarInterface();
        conectarAoServidor();
    }

    private void conectarAoServidor() {
        try {
            socket = new Socket(SERVER_IP, port);
            System.out.println("Connected to server...");

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // Envie o nome do cliente para o servidor
            writer.write(name);
            writer.newLine();
            writer.flush();

            // Thread para receber mensagens do servidor


// Thread para receber mensagens do servidor
            new Thread(() -> {
                try {
                    String message;
                    while (running && (message = reader.readLine()) != null) {
                        System.out.println("Mensagem recebida do servidor: " + message);
                        receberMensagem(message);
                    }
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Erro ao ler mensagem do servidor: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JFrame clienteFrame;
    private JTextArea mensagemEnviar;
    private JTextArea mensagensRecebidas;

    private void criarInterface() {
        // Criação da janela para o cliente
        clienteFrame = new JFrame("Interface do " + this.name);
        clienteFrame.setSize(400, 300);

        // Adicionando manipulador de eventos para o fechamento da janela
        clienteFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        clienteFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sairDoChat();
            }
        });

        // Layout
        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());

        // Área para exibir mensagens recebidas
        mensagensRecebidas = new JTextArea(10, 30);
        mensagensRecebidas.setEditable(false);
        JScrollPane scrollRecebidas = new JScrollPane(mensagensRecebidas);
        painel.add(scrollRecebidas, BorderLayout.NORTH);

        // Área para escrever mensagens
        mensagemEnviar = new JTextArea(5, 30);
        JScrollPane scrollEnviar = new JScrollPane(mensagemEnviar);
        painel.add(scrollEnviar, BorderLayout.CENTER);

        // Botão enviar
        JButton botaoEnviar = new JButton("Enviar");
        botaoEnviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enviarMensagem();
            }
        });
        painel.add(botaoEnviar, BorderLayout.SOUTH);

        clienteFrame.add(painel);

        // Exibindo a janela
        clienteFrame.setVisible(true);
    }

    private void enviarMensagem() {
        // Loop para enviar mensagens para o servidor
        String mensagem = mensagemEnviar.getText();
        String mensagemComNome = name + ": " + mensagem;
        if (!mensagem.isEmpty()) {
            try {
                mensagemEnviar.setText(""); // Limpar a área de texto após o envio
                mensagensRecebidas.append(getTimeStamp() + " Tu: " + mensagem + "\n");
                writer.write(mensagem);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para receber mensagem (podes chamar esse método quando receber uma mensagem)
    private void receberMensagem(String mensagem) {
        mensagensRecebidas.append(getTimeStamp() + " " + mensagem + "\n");
    }

    // Método para obter um timestamp formatado
    private String getTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");
        return dateFormat.format(new Date());
    }

    // Método para sair do chat

    // Método para sair do chat
    private void sairDoChat() {
        try {
            // Enviar uma mensagem especial para informar ao servidor que o cliente está saindo
            String mensagemSaida = "@exit"; // Mensagem especial para indicar saída
            writer.write(mensagemSaida);
            writer.newLine();
            writer.flush();
            // Exemplo: quando o cliente sai do chat

            running = false;
            socket.close(); // Certifique-se de fechar o socket corretamente

            // Fechar streams e socket específicos do cliente
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        // Fechar apenas a janela do cliente atual
        clienteFrame.dispose();
    }

}
