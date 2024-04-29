import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoginInterface {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private boolean loginSuccessful;
    private String storedUsername;
    private Properties properties;

    public LoginInterface() {
        try {
            // Carregar as configurações do arquivo password.config
            properties = new Properties();
            properties.load(new FileInputStream("password.config"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame = new JFrame("Login");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);

        usernameField = new JTextField();
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        panel.add(passwordField);

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
                    setstoredUsername(storedUsername);
                } else {
                    JOptionPane.showMessageDialog(frame, "Credenciais inválidas. Tente novamente.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(loginButton);

        frame.add(panel);
        frame.setVisible(true);
    }
public void setstoredUsername(String storedUsername){
         this.storedUsername=storedUsername;
}
public String getstoredUsername(){
        return this.storedUsername;
}
    // Método para configurar o status de login
    public synchronized void setLogin(boolean success) {
        loginSuccessful = success;
        notifyAll(); // Notificar todas as threads que estão esperando pelo login
    }

    // Método para obter o status de login
    public synchronized boolean getLogin() {
        return loginSuccessful;
    }

    // Método para aguardar o login ser bem-sucedido
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
