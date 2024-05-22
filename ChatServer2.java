package uf3act1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

// Clase principal que inicia el servidor de chat
public class ChatServer2 {
    private static final int port = 5533; // Puerto en el que el servidor escuchará las conexiones entrantes
    private static AtomicBoolean running = new AtomicBoolean(true); // Variable de control para el estado del servidor

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(port)) { // Crea un ServerSocket que espera conexiones de clientes en el puerto especificado
            System.out.println("Iniciando servidor... OK");

            // Bucle para aceptar conexiones de clientes mientras el servidor esté en funcionamiento
            while (running.get()) {
                try {
                    // Configuración de timeout para aceptar conexiones
                    serverSocket.setSoTimeout(1000); // 1 segundo de timeout
                    Socket clientSocket = serverSocket.accept(); // Espera a que un cliente se conecte y acepta la conexión
                    System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());

                    // Crea y arranca un nuevo hilo para manejar la conexión con el cliente
                    new ClientHandler(clientSocket).start();
                } catch (IOException e) {
                    // Maneja el timeout de la aceptación de conexiones
                    if (!running.get()) {
                        System.out.println("Servidor cerrado...");
                        break; // Sale del bucle si el servidor está marcado para detenerse
                    }
                }
            }
        } catch (IOException e) {
            // Maneja cualquier excepción que ocurra en el servidor
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    // Clase que maneja la comunicación con un cliente específico
    static class ClientHandler extends Thread {
        private static final Object lock = new Object(); // Objeto estático para sincronizar el acceso 
        private Socket clientSocket; // Socket para comunicarse con el cliente
        private BufferedReader in; // Lector para recibir mensajes del cliente
        private PrintWriter out; // Escritor para enviar mensajes al cliente

        // Constructor que inicializa el socket del cliente
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                // Inicializa los lectores y escritores para la comunicación con el cliente
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //lee el mensaje del cliente
                out = new PrintWriter(clientSocket.getOutputStream(), true);// envia el mensaje
                BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in)); // lee el mensaje enviado al cliente

                String userInput;
                // Bucle para manejar la comunicación con el cliente
                while ((userInput = in.readLine()) != null) {
                    // Sección para asegurar que solo un cliente hable con el servidor a la vez
                    synchronized (lock) {
                        System.out.println("Cliente: " + userInput);
                        // Si el cliente envía "adios", se termina la conexión
                        if (userInput.equalsIgnoreCase("adios")) {
                            out.println("adios");
                            break;
                        }

                        // El servidor responde al cliente
                        System.out.print("Servidor: ");
                        String response = consoleInput.readLine();
                        out.println(response);
                        // Si el servidor envía "adios", se termina la conexión y se cierra el servidor
                        if (response.equalsIgnoreCase("adios")) {
                            running.set(false); // Marca que el servidor debe detenerse
                            out.println("adios");
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error en el cliente: " + e.getMessage());
            } finally {
                // Asegura que los recursos se liberen correctamente
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error cerrando recursos: " + e.getMessage());
                }
                System.out.println("Conexión con el cliente cerrada");
            }
        }
    }
}
