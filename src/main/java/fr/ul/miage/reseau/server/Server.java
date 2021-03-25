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
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Server implements Runnable {
    private final Socket socket;
    static final int PORT = 80;

    public Server(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {

        log.debug("Main lauched with port " + PORT);
        try (ServerSocket srv = new ServerSocket(PORT)) {
            while (true) {
                Server myOwnServer = new Server(srv.accept());
                log.debug("Server started on port " + PORT);
                //Un thread accueille un client
                Thread thread = new Thread(myOwnServer);
                thread.start();
            }
        }
    }

    public void run() {
        log.debug("New thread");
        BufferedReader bfRead = null;
        InputStream in = null;
        OutputStream out = null;
        Request request = null;
        try {
            InetAddress adrLocale = InetAddress.getLocalHost();
            in = socket.getInputStream();
            out = socket.getOutputStream();
            bfRead = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            int skip = 0;
            while (request == null) {
                request = Parser.parseRequest(bfRead.readLine(), bfRead.readLine());
                log.info("Ip du client : " +adrLocale.getHostAddress() + " Requête : "+request.toString());
                skip++;
                if (skip == 5) {
                    return;
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        Controller controller = new Controller(out);
        out = controller.dispatch(request);

        try {
            out.flush();
            out.close();
            in.close();
            bfRead.close();
            socket.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
