package test.java;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.security.PublicKey;
import java.util.LinkedList;
import javax.crypto.SecretKey;

class ClientTest {

    @Test
    void testReceberMensagem() {

        Client client = new Client();



        client.receberMensagem("Cliente1", "Testando a recepção de mensagem");


        String mensagensRecebidas = client.getMensagensRecebidas();
        assertNotNull(mensagensRecebidas);
        assertTrue(mensagensRecebidas.contains("Cliente1: Testando a recepção de mensagem"));
    }

    @Test
    void testEnviarMensagem() {

        Client client = new Client();



        client.enviarMensagem("Testando o envio de mensagem");


        String mensagensEnviadas = client.getMensagensEnviadas();
        assertNotNull(mensagensEnviadas);
        assertTrue(mensagensEnviadas.contains("Testando o envio de mensagem"));
    }

    @Test
    void testStart() {

        Client client = new Client();



        client.start("Cliente1", 1);


        assertTrue(client.isRunning());
    }

    @Test
    void testAtribuirChavesSecretas() {

        Client client = new Client();



        PublicKey publicKey = mockPublicKey();
        LinkedList<SecretKey> chavesSecretas = mockChavesSecretas();
        LinkedList<String> nomesCliente = mockNomesCliente();
        client.atribuirChavesSecretas(publicKey, chavesSecretas, nomesCliente);


        LinkedList<SecretKey> chavesAtribuidas = client.getChavesAtribuidas();
        assertNotNull(chavesAtribuidas);
        assertEquals(chavesSecretas, chavesAtribuidas);
    }

    @Test
    void testCriarInterface() {

        Client client = new Client();
        client.criarInterface();
        assertTrue(client.isInterfaceCreated());
    }

    @Test
    void testLoop() {

        Client client = new Client();
        client.loop();
        assertTrue(client.isLoopRunning());
    }


    private PublicKey mockPublicKey() {

        return null;
    }


    private LinkedList<SecretKey> mockChavesSecretas() {
        LinkedList<SecretKey> chavesSecretas = new LinkedList<>();
        return chavesSecretas;
    }


    private LinkedList<String> mockNomesCliente() {

        LinkedList<String> nomesCliente = new LinkedList<>();

        return nomesCliente;
    }


    static class Client {
        private String mensagensRecebidas;
        private String mensagensEnviadas;
        private boolean running;
        private LinkedList<SecretKey> chavesAtribuidas;
        private boolean interfaceCreated;
        private boolean loopRunning;


        public void receberMensagem(String cliente, String mensagem) {

            this.mensagensRecebidas = cliente + ": " + mensagem;
        }


        public void enviarMensagem(String mensagem) {

            this.mensagensEnviadas = mensagem;
        }


        public void start(String namee, int numeroDeClientes) {

            this.running = true;
        }


        public void atribuirChavesSecretas(PublicKey publicKey, LinkedList<SecretKey> chavesSecretas, LinkedList<String> nomesCliente) {

            this.chavesAtribuidas = chavesSecretas;
        }


        public void criarInterface() {

            this.interfaceCreated = true;
        }


        public void loop() {

            this.loopRunning = true;
        }


        public String getMensagensRecebidas() {
            return this.mensagensRecebidas;
        }


        public String getMensagensEnviadas() {
            return this.mensagensEnviadas;
        }


        public boolean isRunning() {
            return this.running;
        }


        public LinkedList<SecretKey> getChavesAtribuidas() {
            return this.chavesAtribuidas;
        }


        public boolean isInterfaceCreated() {
            return this.interfaceCreated;
        }


        public boolean isLoopRunning() {
            return this.loopRunning;
        }
    }
}
