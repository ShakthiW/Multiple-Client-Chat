/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tutorial_week05;


import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/* This is a public class named Server. */
public class Server {

    /* PORT and EXIT_COMMAND are private static final variables. They are constants used to specify the server's port and the command to exit the chat. */
    private static final int PORT = 12345;
    private static final String EXIT_COMMAND = "exit";

    /* clients and clientNames are private static List variables. They are used to keep track of the connected clients and their names. */
    private static List<Socket> clients = new ArrayList<>();
    private static List<String> clientNames = new ArrayList<>();

    /* The main method is the entry point of any Java application. The 'throws IOException' clause indicates that this method may throw an IOException. */
    public static void main(String[] args) throws IOException {
        /* A new ServerSocket object named 'serverSocket' is created to listen for client connections on the specified port. */
        ServerSocket serverSocket = new ServerSocket(PORT);
        /* A message is printed to the console indicating the server has started. */
        System.out.println("Server started on port " + PORT);

        /* This is an infinite loop where the server continuously accepts new client connections. */
        while (true) {
            /* A new Socket object named 'clientSocket' is created when a client connects to the server. */
            Socket clientSocket = serverSocket.accept();
            /* The new client's socket is added to the list of clients. */
            clients.add(clientSocket);

            /* A new Thread is created to handle the connected client. The 'handleClient' method is passed as the task for the thread. */
            Thread thread = new Thread(() -> handleClient(clientSocket));
            /* The thread is started, which invokes the 'handleClient' method. */
            thread.start();
        }
    }

    /* The 'handleClient' method is a private static method that takes a Socket object as 
    an argument. It handles the communication with the connected client. */
    private static void handleClient(Socket clientSocket) {
        try {
            /* A BufferedReader object named 'input' is created to read data from the client. It is initialized with an InputStreamReader which is constructed with the input stream of the client's socket. */
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            /* A PrintWriter object named 'output' is created to send data to the client. 
            It is initialized with the output stream of the client's socket. 
            The second argument 'true' passed to the PrintWriter indicates that the 
            PrintWriter should automatically flush its buffer after every write operation. */
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            /* The client is asked to enter their name. */
            output.println("Please enter your name: ");
            /* The client's name is read from the input and added to the list of 
            client names. */
            String name = input.readLine();
            clientNames.add(name);

            /* A message is created to notify other clients about the new user. */
            String message = name + " has connected to the server at " + getCurrentTime();
            /* The connection message is printed to the console and broadcasted to all other clients. */
            System.out.println(message);
            broadcastMessage(message, clientSocket);

            
            String clientMessage;
            /* This is a loop where the server continuously reads messages 
            from the client. */
            while((clientMessage = input.readLine()) != null) {
                /* If the client sends the exit command, the loop breaks 
                and the client is disconnected. */
                if (clientMessage.equalsIgnoreCase(EXIT_COMMAND)) {
                    break;
                }

                /* The client's message is prefixed with their name and broadcasted to all other clients. */
                message = name + ": " + clientMessage;
                System.out.println(message);
                broadcastMessage(message, clientSocket);
            }

            /* When the client disconnects, they are removed from the list of 
            clients and client names. A message is created to notify other clients 
            about the disconnection. */
            clients.remove(clientSocket);
            clientNames.remove(name);
            message = name + " has left the room.";
            /* The disconnection message is printed to the console and broadcasted to all other clients. */
            System.out.println(message);
            broadcastMessage(message, null);

            /* All resources are closed after use. */
            clientSocket.close();
            output.close();
            input.close();
        } catch (IOException e) {
            /* Any IOExceptions are caught and their stack trace is printed to the console. */
            e.printStackTrace();
        }
    }

    /* The 'broadcastMessage' method is a private static method that takes a 
    String called message 
    and a Socket as arguments called excludeClient. It sends the message to all connected 
    clients, excluding the client whose socket is passed as the second argument. */
    private static void broadcastMessage(String message, Socket excludeClient) {
        /* This is a loop that iterates over all connected clients. */
        for(Socket client : clients) {
            /* If the current client is not the excluded client, the message is sent to them. */
            if (client != excludeClient) {
                try {
                    /* A PrintWriter object named 'output' is created to send data to the client. It is initialized with the output stream of the client's socket. The second argument 'true' passed to the PrintWriter indicates that the PrintWriter should automatically flush its buffer after every write operation. */
                    PrintWriter output = new PrintWriter(client.getOutputStream(), true);
                    /* The message is sent to the client. */
                    output.println(message);
                } catch (IOException e) {
                    /* Any IOExceptions are caught and their stack trace is printed to the console. */
                    e.printStackTrace();
                }
            }
        }
    }

    /* The 'getCurrentTime' method is a private static method that returns the current date and time as a String. */
    private static String getCurrentTime() {
        /* A SimpleDateFormat object named 'sdf' is created to format the date and time. */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /* The current date and time is formatted and returned as a String. */
        return sdf.format(new Date());
    }
}
