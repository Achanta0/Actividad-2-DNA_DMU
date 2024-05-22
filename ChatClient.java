import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

  public static void main(String[] args) {

    // Dirección IP del servidor (pasada como argumento)
    String serverIP = args[0];
    // Puerto en el que el servidor está escuchando
    int port = 5533;

    try {
      // Mensaje indicando que se está intentando conectar al servidor
      System.out.println("Connecting to server at " + serverIP + " on port " + port);
      // Creación del socket para conectarse al servidor
      Socket socket = new Socket(serverIP, port);
      // Mensaje indicando que la conexión se ha establecido correctamente
      System.out.println("Connection established successfully.");

      // Crear un BufferedReader para leer datos enviados desde el servidor
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // Crear un PrintWriter para enviar datos al servidor
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      // Crear un BufferedReader para leer la entrada del usuario desde la consola
      BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

      String userInput;
      // Bucle para leer la entrada del usuario y enviar datos al servidor
      while ((userInput = consoleInput.readLine()) != null) {
        // Enviar la entrada del usuario al servidor
        out.println(userInput);
        // Si el usuario escribe "adios", romper el bucle y cerrar la conexión
        if (userInput.equalsIgnoreCase("adios")) {
          break;
        }

        // Leer la respuesta del servidor
        String response = in.readLine();
        // Mostrar la respuesta del servidor en la consola
        System.out.println("Servidor: " + response);
        // Si el servidor responde con "adios", romper el bucle y cerrar la conexión
        if (response.equalsIgnoreCase("adios")) {
          break;
        }
      }

      // Cerrar los recursos de entrada y salida
      in.close();
      out.close();
      socket.close();
      // Mensaje indicando que el cliente se está cerrando
      System.out.println("Closing client... OK");

    } catch (IOException e) {
      // Manejo de excepciones, mostrando el mensaje de error
      System.err.println("Error in client: " + e.getMessage());
    }
  }
}
