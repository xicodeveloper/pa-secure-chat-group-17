import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5000;

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private String name;

    public void start(String namee) {
        this.name= namee;
        criarInterface();
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server...");

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // Envie o nome do cliente para o servidor
            writer.write(name);
            writer.newLine();
            writer.flush();

            // Thread para receber mensagens do servidor
            new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        receberMensagem(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
        clienteFrame = new JFrame("Interface do "+this.name);
        clienteFrame.setSize(400, 300);
        clienteFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
            String mensagemComNome= name +": "+ mensagem;
            if (!mensagem.isEmpty()) {
                try {
                    mensagemEnviar.setText(""); // Limpar a área de texto após o envio
                    mensagensRecebidas.append("Tu: " + mensagem + "\n");
                    writer.write(mensagemComNome);
                    writer.newLine();
                    writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                }
            }
    }

    // Método para receber mensagem (você pode chamar esse método quando receber uma mensagem)
    private void receberMensagem(String mensagem) {
        mensagensRecebidas.append(mensagem + "\n");
    }
}

