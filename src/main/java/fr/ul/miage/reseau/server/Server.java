package fr.ul.miage.reseau.server;

import fr.ul.miage.reseau.api.Controller;
import fr.ul.miage.reseau.parser.Parser;
import fr.ul.miage.reseau.parser.Request;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class Server implements Runnable {
    private final Socket socket;
    private static int port;
    private static String repositoryPath;

    public Server(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        getProperties();
        log.debug("Main launched with port " + port);

        try (ServerSocket srv = new ServerSocket(port)) {
            while (true) {
                Server myOwnServer = new Server(srv.accept());
                log.debug("Server started on port " + port);
                //Un thread accueille un client
                Thread thread = new Thread(myOwnServer);
                thread.start();
            }
        }
    }

    private static void getProperties() throws IOException {
        File file = new File("config.properties");
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        port = Integer.parseInt(properties.getProperty("port"));
        repositoryPath = properties.getProperty("repository");
    }

    public void run() {
        log.debug("New thread");
        BufferedReader bfRead = null;
        OutputStream out = null;
        Request request = null;
        try {
            InetAddress adrLocale = InetAddress.getLocalHost();
            bfRead = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            int skip = 0;
            while (request == null) {
                request = Parser.parseRequest(bfRead.readLine(), bfRead.readLine());
                log.info("Ip du client : " + adrLocale.getHostAddress() + " Requête : " + request.toString());
                skip++;
                if (skip == 5) {
                    return;
                }
            }
            out = socket.getOutputStream();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        Controller controller = new Controller(out, repositoryPath);
        controller.dispatch(request);
        try {
            bfRead.close();
            socket.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
