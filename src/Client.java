import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client {
    public String nomeCliente;
    private Server chat1;

    public Client(String nomeCliente,Server chat1) {
        this.nomeCliente = nomeCliente;
        this.chat1=chat1;
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

    private void enviarMensagem() {
        String mensagem = mensagemEnviar.getText();
        if (!mensagem.isEmpty()) {
            mensagensRecebidas.append("Tu: " + mensagem + "\n");
            chat1.recebemsg(this.nomeCliente,mensagem);
            // Adicione aqui a lógica para enviar a mensagem para o destinatário
            mensagemEnviar.setText(""); // Limpar a área de texto após o envio
        }
    }

    // Método para receber mensagem (você pode chamar esse método quando receber uma mensagem)
    public void receberMensagem(String nomeClienteRecebe,String mensagem) {
        mensagensRecebidas.append(nomeClienteRecebe +": " + mensagem + "\n");
    }
}

