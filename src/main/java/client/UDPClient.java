package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class UDPClient {
    private static final String SERVER_ADDRESS = "localhost"; // Adresse du serveur
    private static final int SERVER_PORT = 1234; // Port du serveur
    private static final int BUFFER_SIZE = 1024; // Taille du tampon pour les données reçues

    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket(); // Création du socket client
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS); // Résolution de l'adresse IP du serveur

            // Rejoindre le chat en envoyant un message "/join" au serveur
            String joinMessage = "/join";
            byte[] joinData = joinMessage.getBytes();
            DatagramPacket joinPacket = new DatagramPacket(joinData, joinData.length, serverAddress, SERVER_PORT);
            clientSocket.send(joinPacket); // Envoi du message de jointure

            // Thread pour recevoir les messages du serveur en parallèle
            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        byte[] receiveData = new byte[BUFFER_SIZE];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket); // Réception des données du serveur

                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println("Message reçu : " + message); // Affichage du message reçu du serveur
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start(); // Démarrage du thread de réception

            // Envoi des messages depuis la console
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String messageToSend = reader.readLine(); // Lecture du message depuis la console
                byte[] sendData = messageToSend.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                clientSocket.send(sendPacket); // Envoi du message au serveur
            }
        } catch (IOException e) {
            e.printStackTrace(); // Gestion des exceptions
        }
    }
}