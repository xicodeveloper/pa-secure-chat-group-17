import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Uma interface simples para login com campos de usuário e senha.
 */
public class LoginInterface {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private boolean loginSuccessful;
    private String storedUsername;
    private Properties properties;

    /**
     * Construtor da classe LoginInterface.
     * Inicializa a interface gráfica para o login.
     */
    public LoginInterface() {
        // Configuração da janela de login
        frame = new JFrame("Login");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuração do painel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        // Campo de entrada de usuário
        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);
        usernameField = new JTextField();
        panel.add(usernameField);

        // Campo de entrada de senha
        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);
        passwordField = new JPasswordField();
        panel.add(passwordField);

        // Botão de login
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Verificar as credenciais de login
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Obter as credenciais de login do arquivo de configuração
                String storedUsername = properties.getProperty("username");
                String storedPassword = properties.getProperty("password");

                // Verificar se as credenciais fornecidas correspondem às armazenadas no arquivo de configuração
                if (username.equals(storedUsername) && password.equals(storedPassword)) {
                    setLogin(true);
                    setStoredUsername(storedUsername);
                } else {
                    JOptionPane.showMessageDialog(frame, "Credenciais inválidas. Tente novamente.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(loginButton);

        // Adicionando o painel à janela
        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Define o nome de usuário armazenado após o login bem-sucedido.
     * @param storedUsername O nome de usuário armazenado.
     */
    public void setStoredUsername(String storedUsername) {
        this.storedUsername = storedUsername;
    }

    /**
     * Obtém o nome de usuário armazenado após o login bem-sucedido.
     * @return O nome de usuário armazenado.
     */
    public String getStoredUsername() {
        return this.storedUsername;
    }

    /**
     * Configura o status de login.
     * @param success O status de login (true se o login foi bem-sucedido, false caso contrário).
     */
    public synchronized void setLogin(boolean success) {
        loginSuccessful = success;
        notifyAll(); // Notificar todas as threads que estão esperando pelo login
    }

    /**
     * Obtém o status de login.
     * @return true se o login foi bem-sucedido, false caso contrário.
     */
    public synchronized boolean getLogin() {
        return loginSuccessful;
    }

    /**
     * Aguarda até que o login seja bem-sucedido.
     * @return true se o login foi bem-sucedido, false caso contrário.
     */
    public synchronized boolean waitForLogin() {
        while (!getLogin()) {
            try {
                wait(); // Esperar até que o login seja bem-sucedido
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return loginSuccessful;
    }
}
