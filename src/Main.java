public class Main {
    public static void main(String[] args) {

        Server chat1 = new Server();
        // Criando interfaces para os clientes
        Client cliente1 = new Client("Cliente 1",chat1);
        cliente1.criarInterface();

        Client cliente2 = new Client("Cliente 2",chat1);
        cliente2.criarInterface();

        Client cliente3 = new Client("Cliente 3",chat1);
        cliente3.criarInterface();
        chat1.clientes(cliente1,cliente2,cliente3);
    }
}





