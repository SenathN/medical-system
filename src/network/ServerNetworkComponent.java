/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JOptionPane;

/**
 *
 * @author Sen
 */
public class ServerNetworkComponent {

    /*
    This is a completely static class which handles all thread activities by itself. 
    Never instantiate this class.
    
    this class deploys threads to communicate with the counterpart program on the same network
     */
    private static boolean isEnabled;
    public static String host = "localhost";
    public static int port = 27015;

    private static BufferedReader reader;
    private static PrintWriter writer;

    private static String _CONNECTION_START = "_CONNECTION_START";

    public static boolean enableNetwoking(boolean state) {
        isEnabled = state;
        if (isEnabled) {
            listenForMessages(port);
        }
        return isEnabled;
    }

    /* following function opens a thread, which listens to client messages send them to 
     * handleRequest() function and automatically respond
     */
    public static void listenForMessages(int onPort) {
        port = onPort;
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);

            // Start a new thread to handle incoming messages from the client
            // since the program frezzes at .accept() therefore should be added to the seperate thread
            Thread recieverThread = new Thread(() -> {
                while (true) {
                    try {
                        // Wait for a client to connect
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("Client connected: " + clientSocket.getInetAddress());

                        // Create input and output streams and make assign to static variables
                        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        writer = new PrintWriter(clientSocket.getOutputStream(), true);

                        String clientMessage = _CONNECTION_START;

                        // continously get messages and handles them
                        while ((clientMessage = reader.readLine()) != null) {
                            System.out.println("REQUEST: " + clientMessage);

                            // Process the client message as needed
                            ClientResponder cr = new ClientResponder();
                            String response = cr.handleRequest(clientMessage);

                            // checking for bad request indicator
                            if (response.charAt(response.length()-1) == '\0' )
                                continue;
                            
                            // Send a response back to the client
                            writer.println(response);
                            System.out.println("RESPONSE: " + response);

                        }
                    } catch (SocketException e) {
                        String errorMessage = "Connection Lost. " + e;
                        JOptionPane.showMessageDialog(null, errorMessage, "Connection lost", JOptionPane.WARNING_MESSAGE);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                System.out.println(Thread.currentThread().getName() + " terminated.");
            });
            recieverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(String message) {
        sendMessage(host, port, message);
    }

    /* the following function sends a custom message to the client via the open writer variable, 
     * only if it is initialized already, else return
     */
    public static void sendMessage(String host, int port, String message) {
        Thread senderThread = new Thread(() -> {
            try {
                // check if reader and writer are initiated
                if (writer == null) {
                    System.out.println("Writer variable null");
                    return;
                }
                writer.println(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " terminated.");
        });
        senderThread.start();
    }
}
