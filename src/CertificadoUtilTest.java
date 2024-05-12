import org.junit.jupiter.api.Test;
import java.security.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para a classe CertificadoUtil.
 */
class CertificadoUtilTest {

    /**
     * Testa o método gerarCertificadoPEM da classe CertificadoUtil.
     */
    @Test
    void gerarCertificadoPEM() {
        String identificacaoRequerente = "Alice";
        String identificacaoCertificado = "123456";
        PublicKey chavePublica = generatePublicKey();
        String algoritmoAssinatura = "SHA256withRSA";

        CertificadoUtil certificadoUtil = new CertificadoUtil(identificacaoRequerente, identificacaoCertificado, chavePublica, algoritmoAssinatura);

        String certificadoPEM = certificadoUtil.gerarCertificadoPEM();

        assertNotNull(certificadoPEM);
        assertTrue(certificadoPEM.contains(identificacaoRequerente));
        assertTrue(certificadoPEM.contains(identificacaoCertificado));
        assertTrue(certificadoPEM.contains(algoritmoAssinatura));
    }

    /**
     * Testa o método verificarAssinatura da classe CertificadoUtil.
     */
    @Test
    void verificarAssinatura() {
        CertificadoUtil certificadoUtil = new CertificadoUtil("Alice", "123456", null, "SHA256withRSA");

        String resultado = certificadoUtil.verificarAssinatura();

        assertEquals("Estado da Assinatura: Negado não assinado", resultado);
    }

    /**
     * Testa o método calcularHashCertificado da classe CertificadoUtil.
     *
     * @throws NoSuchAlgorithmException Caso o algoritmo de hash não seja encontrado.
     */
    @Test
    void calcularHashCertificado() throws NoSuchAlgorithmException {
        CertificadoUtil certificadoUtil = new CertificadoUtil("Alice", "123456", null, "SHA256withRSA");
        String certificado = "Certificado de teste";

        byte[] hash = certificadoUtil.calcularHashCertificado(certificado);

        assertNotNull(hash);
    }

    /**
     * Testa o método assinarCertificado da classe CertificadoUtil.
     *
     * @throws NoSuchAlgorithmException Caso o algoritmo de assinatura não seja encontrado.
     * @throws InvalidKeyException      Caso a chave privada seja inválida.
     * @throws SignatureException       Caso ocorra algum erro durante a assinatura.
     */
    @Test
    void assinarCertificado() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        CertificadoUtil certificadoUtil = new CertificadoUtil("Alice", "123456", null, "SHA256withRSA");
        byte[] dados = "Dados de teste".getBytes();
        PrivateKey chavePrivada = generatePrivateKey();

        byte[] assinatura = certificadoUtil.assinarCertificado(dados, chavePrivada);

        assertNotNull(assinatura);
    }

    /**
     * Método auxiliar para gerar uma chave pública para os testes.
     *
     * @return A chave pública gerada.
     */
    private PublicKey generatePublicKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyGen.generateKeyPair();
            return keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Método auxiliar para gerar uma chave privada para os testes.
     *
     * @return A chave privada gerada.
     */
    private PrivateKey generatePrivateKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyGen.generateKeyPair();
            return keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
