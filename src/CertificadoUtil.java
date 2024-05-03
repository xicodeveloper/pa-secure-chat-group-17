import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class CertificadoUtil {
    private String identificacaoRequerente;
    private String identificacaoCertificado;
    private PublicKey chavePublica;
    private String algoritmoAssinatura;
    private PrivateKey chavePrivada;
    public boolean assinado;

    public CertificadoUtil(String identificacaoRequerente, String identificacaoCertificado, PublicKey chavePublica, String algoritmoAssinatura) {
        this.identificacaoRequerente = identificacaoRequerente;
        this.identificacaoCertificado = identificacaoCertificado;
        this.chavePublica = chavePublica;
        this.algoritmoAssinatura = algoritmoAssinatura;

        this.assinado=false;
    }

    // Elabora o certificado no formato PEM com informações adicionais
    // Elabora o certificado no formato PEM com informações adicionais
    public String gerarCertificadoPEM() {
        String estado = assinado ? "Assinado" : "Ainda por assinar";
        String certificadoPEM = "-----BEGIN CERTIFICATE-----\n";
        certificadoPEM += "Identificação do Requerente: " + identificacaoRequerente + "\n";
        certificadoPEM += "Identificação do Certificado: " + identificacaoCertificado + "\n";
        certificadoPEM += "Chave Pública do Requerente: " + encodeToBase64(chavePublica.getEncoded()) + "\n";
        certificadoPEM += "Algoritmo de Assinatura: " + algoritmoAssinatura + "\n";
        certificadoPEM += "Estado: " + estado + "\n"; // Estado do certificado
        certificadoPEM += "-----END CERTIFICATE-----";
        return certificadoPEM;
    }
public String verificarAssinatura(){
        String result="Estado da Assinatura: ";
        if(assinado==true){
            result+= "Assinado";
        }else{
            result+="Negado não assinado";
        }
      return result;
}

    // Método auxiliar para codificar em Base64
    private String encodeToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    // Salva o certificado em formato PEM em um diretório específico
    public void salvarCertificado(String diretorio, byte[] assinatura) {
        String nomeArquivo = "certificado_" + identificacaoRequerente + ".pem";
        String caminhoArquivo = diretorio + File.separator + nomeArquivo;

        File arquivo = new File(caminhoArquivo);

        try (FileWriter writer = new FileWriter(arquivo)) {
            String certificadoPEM = gerarCertificadoPEM();
            int indexEndCertificate = certificadoPEM.lastIndexOf("-----END CERTIFICATE-----");
            if (indexEndCertificate != -1) {
                StringBuilder certificadoComAssinatura = new StringBuilder(certificadoPEM);
                certificadoComAssinatura.insert(indexEndCertificate, "\nAssinatura\n");
                certificadoComAssinatura.insert(indexEndCertificate + 1, Base64.getEncoder().encodeToString(assinatura) + "\n");
                writer.write(certificadoComAssinatura.toString());
            } else {
                writer.write("\nAssinatura\n");
                writer.write(certificadoPEM);
                writer.write(Base64.getEncoder().encodeToString(assinatura)); // Adiciona a assinatura ao final do arquivo
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Calcular o valor hash do certificado ainda por assinar usando SHA256
    public byte[] calcularHashCertificado(String certificado) throws NoSuchAlgorithmException {
        byte[] certificadoBytes = certificado.getBytes();
        return calcularHash(certificadoBytes);
    }

    // Produzir a assinatura digital do certificado usando a chave privada da CA e o algoritmo RSA
    public byte[] assinarCertificado(byte[] dados, PrivateKey chavePrivada) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return assinar(dados, chavePrivada);
    }

    // Método para calcular o valor hash usando SHA256
    private byte[] calcularHash(byte[] dados) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(dados);
    }

    // Método para produzir a assinatura digital usando a chave privada da CA e o algoritmo RSA
    private byte[] assinar(byte[] dados, PrivateKey chavePrivada) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature assinatura = Signature.getInstance("SHA256withRSA");
        assinatura.initSign(chavePrivada);
        assinatura.update(dados);
        assinado=true;
        return assinatura.sign();
    }
}
