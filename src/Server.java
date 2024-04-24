import java.util.Objects;

public class Server {
    public Server() {
    }
    public void clientes(Client cliente1,Client cliente2,Client cliente3) {
        this.cliente1=cliente1;
        this.cliente2=cliente2;
        this.cliente3=cliente3;
    }
    private String msgRecebida;
    private Client cliente1;
    private Client cliente2;
    private Client cliente3;
    public void recebemsg(String nomeCliente,String mensagem){
        if(Objects.equals(nomeCliente,this.cliente1.nomeCliente)){
            cliente2.receberMensagem(nomeCliente,mensagem);
            cliente3.receberMensagem(nomeCliente,mensagem);
        } else if (Objects.equals(nomeCliente,this.cliente2.nomeCliente)) {
            cliente1.receberMensagem(nomeCliente,mensagem);
            cliente3.receberMensagem(nomeCliente,mensagem);
        }else{
            cliente1.receberMensagem(nomeCliente,mensagem);
            cliente2.receberMensagem(nomeCliente,mensagem);
        }
    }
}
