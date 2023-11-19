package server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    private static final int SERVER_PORT = 1234;
    private static final int BUFFER_SIZE = 1024;

    private static List<InetSocketAddress> clients = new ArrayList<>(); // Liste pour stocker les clients connectés

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT); // Création du socket serveur

            byte[] receiveData = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket); // Réception du message du client

                InetAddress clientAddress = receivePacket.getAddress(); // Adresse IP du client
                int clientPort = receivePacket.getPort(); // Port du client
                InetSocketAddress client = new InetSocketAddress(clientAddress, clientPort); // Création de l'adresse du client

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength()); // Conversion du message en chaîne de caractères
                if (message.startsWith("/join")) {
                    // Nouveau client rejoint la discussion
                    clients.add(client); // Ajout du client à la liste des clients connectés
                    System.out.println("Nouveau client rejoint : " + client);
                } else {
                    // Diffuser le message à tous les autres clients
                    broadcast(message, client, serverSocket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Affichage de l'erreur en cas de problème de lecture ou d'écriture
        }
    }

    // Fonction pour diffuser un message à tous les clients sauf à l'expéditeur
    private static void broadcast(String message, InetSocketAddress sender, DatagramSocket serverSocket) throws IOException {
        for (InetSocketAddress client : clients) {
            if (!client.equals(sender)) {
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client.getAddress(), client.getPort());
                serverSocket.send(sendPacket); // Envoi du message à tous les clients sauf à l'expéditeur
            }
        }
    }
}