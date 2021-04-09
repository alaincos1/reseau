package fr.ul.miage.reseau.server;

import fr.ul.miage.reseau.api.Controller;
import fr.ul.miage.reseau.exception.ApiException;
import fr.ul.miage.reseau.communication.Request;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
        InetAddress adrLocale = null;
        try {
            adrLocale = InetAddress.getLocalHost();
            bfRead = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = socket.getOutputStream();

            String line = bfRead.readLine();
            List<String> list = new ArrayList<>();
            while (line.length() > 0) {
                list.add(line);
                line = bfRead.readLine();
            }
            try {
                request = Request.builder()
                        .list(list)
                        .out(out)
                        .build();
            }catch(ApiException e){
                log.error(e.getMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.info("Ip du client : " + adrLocale.getHostAddress() + " Requête : " + request.toString());
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
