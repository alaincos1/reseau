package fr.ul.miage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer implements Runnable {

    static final int port = 9003;
    private Socket socket;
    private String homepageRequest = "GET / HTTP/1.1";
    private String homepage = "index.html";
    private String resourcesName = "resources/";

    public HttpServer(Socket socket){
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Main lauched with port " +port);
        ServerSocket srv = new ServerSocket(port);
        while(true){
            HttpServer myownserver = new HttpServer(srv.accept());
            System.out.println("Server tarted on port " +port);
            //Un thread accueille un client
            Thread thread = new Thread(myownserver);
            thread.start();
        }
    }

    public void run(){
        System.out.println("New thread");
        String pwd = System.getProperty("user.dir");
        String requete;
        String[] partie;
        byte[] tableau;
        String index = "GET / HTTP/1.1";
        String nomFichier;
        BufferedReader rd;

        try{
            rd = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf8"));
            DataOutputStream data = new DataOutputStream(socket.getOutputStream());
            DataInputStream fichier;
            requete = null;
            int skip = 0;
            while(requete == null){
                requete = rd.readLine();
                System.out.println("Request is : ");
                System.out.println(requete);
                skip++;
                if(skip == 5){
                    return;
                }
            }
            //SI on ne met pas d'URL on donne la homepage
            if(requete.equals(homepageRequest)){
                nomFichier = "index.html";
            } else{
                partie = requete.split(" ")

                nomFichier = partie[1];

                char someChar = '/';
                int count  =0;

                for(int i = 0; i < nomFichier.length(); i++){
                    if (nomFichier.charAt(i) == someChar){
                        count++;
                    }
                }

                if(nomFichier.startsWith("/") && (count == 1  count ==2){
                    nomFichier = nomFichier.substring(1);
                }
            }
            nomFichier = resourcesName + nomFichier;

            System.out.println(nomFichier);

            Path chemin = Paths.get(nomFichier);

            fichier = new DataInputStream(new FileInputStream(pwd+"/"+nomFichier));

            System.out.println("chemin");
            System.out.println(chemin);
            tableau = Files.readAllBytes(chemin);

            data.writeBytes("HTTP/1.1 200 OK\r\n");
            if(nomFichier.endsWith(".html")){
                data.writeBytes("Content-Type: text/html\r\n");
            }

            if(nomFichier.endsWith(".gif") || nomFichier.endsWith(".jpg")){
                data.writeBytes("Content-Type: image/gif\r\n");
            }

            data.writeBytes("Content-Length: "+ fichier.available() + "\r\n");
            data.writeBytes("\r\n");
            data.write(tableau);
            data.flush();
            rd.close();
            data.close();
            fichier.close();
        } catch(IOException e){
            System.err.println("IOException");
            e.printStackTrace();
        }
    }
}
