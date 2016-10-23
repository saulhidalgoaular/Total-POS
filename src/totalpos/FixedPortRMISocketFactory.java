package totalpos;

import java.rmi.server.RMISocketFactory;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

/*
 * User: Tim Goffings
 * Date: Oct 3, 2002 - 3:51:34 PM
 */

public class FixedPortRMISocketFactory extends RMISocketFactory {

    /**
     * Creates a client socket connected to the specified host and port and writes out debugging info
     * @param  host   the host name
     * @param  port   the port number
     * @return a socket connected to the specified host and port.
     * @exception IOException if an I/O error occurs during socket creation
     * @since JDK1.1
     */
    public Socket createSocket(String host, int port)
            throws IOException {
        System.out.println("creating socket to host : " + host + "on port " + port);
        return new Socket(host, port);
    }

    /**
     * Create a server socket on the specified port (port 0 indicates
     * an anonymous port) and writes out some debugging info
     * @param  port the port number
     * @return the server socket on the specified port
     * @exception IOException if an I/O error occurs during server socket
     * creation
     * @since JDK1.1
     */
    public ServerSocket createServerSocket(int port)
            throws IOException {
        port = (port == 0 ? 1090 : port);
        System.out.println("creating ServerSocket on port " + port);
        return new ServerSocket(port);

    }
}
