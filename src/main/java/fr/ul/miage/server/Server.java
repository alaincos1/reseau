package fr.ul.miage.server;

import fr.ul.miage.api.Controller;
import fr.ul.miage.parser.Parser;
import fr.ul.miage.parser.Request;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Server implements Runnable{
    private final Socket socket;
    static final int PORT = 8000;

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
        String rawRequest = null;
        BufferedReader bfRead = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            bfRead = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            int skip = 0;
            while (rawRequest == null) {
                rawRequest = bfRead.readLine();
                log.debug("Request is : " + rawRequest);
                skip++;
                if (skip == 5) {
                    return;
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        Request request = Parser.parseRequest(rawRequest);
        Controller controller = new Controller(out);

        out = controller.dispatch(request);

        try {
            out.flush();
            out.close();
            in.close();
            bfRead.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
