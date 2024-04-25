public class Main {
    public static void main(String[] args) {
        // Iniciar o servidor em uma thread separada
        new Thread(() -> {
            Server server = new Server();
            server.start();
        }).start();
        String name="Cliente1";
        String name1="Cliente2";
        String name2="Cliente3";
        // Iniciar os clientes em threads separadas
        for (int i = 0; i < 3; i++) {
            String finalName = name;
            new Thread(() -> {
                Client client = new Client();

                client.start(finalName);
            }).start();
            name=name1;
            if(i==1){
                name=name2;
            }
        }
    }
}