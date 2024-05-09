import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import static org.junit.jupiter.api.Assertions.*;

class CertificadoUtilTest {

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

    @Test
    void verificarAssinatura() {

        CertificadoUtil certificadoUtil = new CertificadoUtil("Alice", "123456", null, "SHA256withRSA");


        String resultado = certificadoUtil.verificarAssinatura();


        assertEquals("Estado da Assinatura: Negado não assinado", resultado);
    }

    @Test
    void calcularHashCertificado() throws NoSuchAlgorithmException {

        CertificadoUtil certificadoUtil = new CertificadoUtil("Alice", "123456", null, "SHA256withRSA");
        String certificado = "Certificado de teste";


        byte[] hash = certificadoUtil.calcularHashCertificado(certificado);


        assertNotNull(hash);
    }

    @Test
    void assinarCertificado() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        CertificadoUtil certificadoUtil = new CertificadoUtil("Alice", "123456", null, "SHA256withRSA");
        byte[] dados = "Dados de teste".getBytes();
        PrivateKey chavePrivada = generatePrivateKey();


        byte[] assinatura = certificadoUtil.assinarCertificado(dados, chavePrivada);


        assertNotNull(assinatura);
    }


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
