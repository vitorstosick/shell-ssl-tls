import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class SecureServer {
    public static void main(String[] args) throws Exception {
        // o ideal seria esconder a senha em uma variavel ambiente, mas como é apenas para fins avaliativos vou deixar exposta
        char[] password = "1234".toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream("src/keystore.p12")) {
            keyStore.load(fis, password);
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, password);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(8443);

        System.out.println("Servidor SSL iniciado e aguardando conexões...");

        SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        out.write("Conexão segura estabelecida!\n");
        out.flush();

        String clientMessage = in.readLine();
        System.out.println("Mensagem do cliente: " + clientMessage);

        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
