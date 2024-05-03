import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class CertificadoUtil {
    private  String nome;
    private PublicKey chavePublica;
    private PrivateKey chavePrivada;
CertificadoUtil(String nome, PublicKey chavePublica, PrivateKey chavePrivada){
    this.nome=nome;
    this.chavePrivada=chavePrivada;
    this.chavePublica=chavePublica;

}

        // Elabora o certificado
    public String Cert(){
        String certificado = "-----BEGIN CERTIFICATE-----\n";
        certificado += "Identificação: " + nome + "\n";
        certificado += "Chave Pública: " + Base64.getEncoder().encodeToString(chavePublica.getEncoded()) + "\n";
        certificado += "-----END CERTIFICATE-----";
    return certificado;

    }
}
