import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Classe utilitária para manipulação de certificados digitais.
 */
public class CertificadoUtil {
    private String identificacaoRequerente;
    private String identificacaoCertificado;
    private PublicKey chavePublica;
    private String algoritmoAssinatura;
    private PrivateKey chavePrivada;
    public boolean assinado;

    /**
     * Construtor da classe CertificadoUtil.
     *
     * @param identificacaoRequerente Identificação do requerente.
     * @param identificacaoCertificado Identificação do certificado.
     * @param chavePublica Chave pública do requerente.
     * @param algoritmoAssinatura Algoritmo de assinatura utilizado.
     */
    public CertificadoUtil(String identificacaoRequerente, String identificacaoCertificado, PublicKey chavePublica, String algoritmoAssinatura) {
        this.identificacaoRequerente = identificacaoRequerente;
        this.identificacaoCertificado = identificacaoCertificado;
        this.chavePublica = chavePublica;
        this.algoritmoAssinatura = algoritmoAssinatura;

        this.assinado = false;
    }

    /**
     * Método para gerar um certificado no formato PEM.
     *
     * @return O certificado gerado no formato PEM.
     */
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

    public boolean isAssinado() {
        return assinado;
    }

    /**
     * Método para verificar o estado da assinatura.
     *
     * @return O estado da assinatura.
     */
    public String verificarAssinatura() {
        String result = "Estado da Assinatura: ";
        if (assinado) {
            result += "Assinado";
        } else {
            result += "Negado, não assinado";
        }
        return result;
    }

    /**
     * Método auxiliar para codificar em Base64.
     *
     * @param bytes Array de bytes a ser codificado.
     * @return A string codificada em Base64.
     */
    private String encodeToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Método para salvar o certificado em formato PEM em um diretório específico.
     *
     * @param diretorio Diretório de destino.
     * @param assinatura Assinatura a ser adicionada ao certificado.
     */
    public void salvarCertificado(String diretorio, byte[] assinatura) {
        File diretorioArquivo = new File(diretorio);
        if (!diretorioArquivo.exists()) {
            diretorioArquivo.mkdirs();
        }

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
                writer.write(Base64.getEncoder().encodeToString(assinatura));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para calcular o valor hash do certificado ainda por assinar usando SHA256.
     *
     * @param certificado Certificado a ser hashado.
     * @return O valor hash do certificado.
     * @throws NoSuchAlgorithmException Caso o algoritmo de hash não seja encontrado.
     */
    public byte[] calcularHashCertificado(String certificado) throws NoSuchAlgorithmException {
        byte[] certificadoBytes = certificado.getBytes();
        return calcularHash(certificadoBytes);
    }

    /**
     * Método para produzir a assinatura digital do certificado usando a chave privada da CA e o algoritmo RSA.
     *
     * @param dados Dados a serem assinados.
     * @param chavePrivada Chave privada utilizada para assinar.
     * @return A assinatura digital do certificado.
     * @throws NoSuchAlgorithmException Caso o algoritmo de assinatura não seja encontrado.
     * @throws InvalidKeyException Caso a chave privada seja inválida.
     * @throws SignatureException Caso ocorra algum erro durante a assinatura.
     */
    public byte[] assinarCertificado(byte[] dados, PrivateKey chavePrivada) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return assinar(dados, chavePrivada);
    }

    /**
     * Método para calcular o valor hash usando SHA256.
     *
     * @param dados Dados a serem hashados.
     * @return O valor hash dos dados.
     * @throws NoSuchAlgorithmException Caso o algoritmo de hash não seja encontrado.
     */
    private byte[] calcularHash(byte[] dados) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(dados);
    }

    /**
     * Método para produzir a assinatura digital usando a chave privada da CA e o algoritmo RSA.
     *
     * @param dados Dados a serem assinados.
     * @param chavePrivada Chave privada utilizada para assinar.
     * @return A assinatura digital dos dados.
     * @throws NoSuchAlgorithmException Caso o algoritmo de assinatura não seja encontrado.
     * @throws InvalidKeyException Caso a chave privada seja inválida.
     * @throws SignatureException Caso ocorra algum erro durante a assinatura.
     */
    private byte[] assinar(byte[] dados, PrivateKey chavePrivada) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature assinatura = Signature.getInstance("SHA256withRSA");
        assinatura.initSign(chavePrivada);
        assinatura.update(dados);
        assinado = true;
        return assinatura.sign();
    }
}
