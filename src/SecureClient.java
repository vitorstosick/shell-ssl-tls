import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class SecureClient {
    public static void main(String[] args) throws Exception {
        // o ideal seria esconder a senha em uma variavel ambiente, mas como é apenas para fins avaliativos vou deixar exposta
        char[] password = "1234".toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream("src/keystore.p12")) {
            keyStore.load(fis, password);
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        SSLSocketFactory ssf = sslContext.getSocketFactory();
        SSLSocket sslSocket = (SSLSocket) ssf.createSocket("localhost", 8443);

        BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream()));

        out.write("Cliente: Conexão segura estabelecida com sucesso!\n");
        out.flush();

        // Ler a mensagem do servidor
        String serverMessage = in.readLine();
        System.out.println("Mensagem do servidor: " + serverMessage);

        // Fechar as conexões
        in.close();
        out.close();
        sslSocket.close();
    }
}