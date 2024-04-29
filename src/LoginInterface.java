import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginInterface {
    private JFrame loginFrame;
    private JTextField nomeField;
    private JButton entrarButton;

    public LoginInterface() {
        // Criação da janela de login
        loginFrame = new JFrame("Login");
        loginFrame.setSize(300, 150);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        // Campo de texto para o nome
        JLabel nameLabel = new JLabel("Nome:");
        nomeField = new JTextField();
        panel.add(nameLabel);
        panel.add(nomeField);

        // Botão para entrar
        entrarButton = new JButton("Entrar");
        entrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nome = nomeField.getText().trim();
                if (!nome.isEmpty()) {
                    // Fecha a janela de login e inicia o cliente com o nome fornecido
                    loginFrame.dispose();
                    new Client().start(nome);
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Por favor, insira um nome válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(entrarButton);

        // Adiciona o painel à janela de login
        loginFrame.add(panel);

        // Exibe a janela de login
        loginFrame.setVisible(true);
    }
}
