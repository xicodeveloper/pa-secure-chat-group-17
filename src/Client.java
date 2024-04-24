import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client {
    private String nomeCliente;

    public Client(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    private JFrame clienteFrame;
    private JTextArea mensagemEnviar;
    private JTextArea mensagensRecebidas;

    public void criarInterface() {
        // Criação da janela para o cliente
        clienteFrame = new JFrame("Interface do " + nomeCliente);
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

    // Método para enviar mensagem
    private void enviarMensagem() {
        String mensagem = mensagemEnviar.getText();
        if (!mensagem.isEmpty()) {
            mensagensRecebidas.append("Você: " + mensagem + "\n");
            // Adicione aqui a lógica para enviar a mensagem para o destinatário
            mensagemEnviar.setText(""); // Limpar a área de texto após o envio
        }
    }

    // Método para receber mensagem (você pode chamar esse método quando receber uma mensagem)
    private void receberMensagem(String mensagem) {
        mensagensRecebidas.append("Eles: " + mensagem + "\n");
    }
}

