/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver;

import udpserver.NcpMessage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kangur
 */
public class Server implements Runnable
{

    private static final Logger LOG = Logger.getLogger(Server.class.getName());

    public Server(int port)
    {
        LOG.log(Level.INFO, "Creating Server");
        this.port = port;
        try
        {
            this.socket = new DatagramSocket(port, InetAddress.getByName("192.168.229.135"));

        }
        catch (SocketException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
        catch (UnknownHostException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    private boolean running;
    private int port;
    private DatagramSocket socket;
    private DataManager dataManager;

    @Override
    public void run()
    {

        //       LOG.log(Level.INFO, "Listening on " + socket.getInetAddress().getHostAddress());
        while (true)
        {
            byte[] buff = new byte[2000];
            DatagramPacket packiet = new DatagramPacket(buff, 2000);

            try
            {
                socket.receive(packiet);

            }
            catch (IOException ex)
            {
                LOG.log(Level.SEVERE, null, ex);
            }
            handlePackiet(packiet);
        }

    }

    public void sendMessage(Object obj)
    {
        DatagramPacket packiet = new DatagramPacket(obj.toString().getBytes(), obj.toString().length());
        try
        {
            socket.send(packiet);
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(Object obj, Client client)
    {
        DatagramPacket packiet = new DatagramPacket(obj.toString().getBytes(), obj.toString().length());
        packiet.setSocketAddress(client.getAddress());
        try
        {
            socket.send(packiet);
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private void handlePackiet(DatagramPacket packiet)
    {
        LOG.log(Level.INFO, "Handling new packiet");
        try
        {
            NcpMessage message = new NcpMessage(packiet);
            switch (message.getType())
            {
                case NcpMessage.NCP_REGISTER:
                    dataManager.getClients().addClient(packiet,socket,dataManager.getQueue());
                    break;
                case NcpMessage.NCP_RESULT:
                    dataManager.handleResults(message);
                    break;

                case NcpMessage.NCP_DATA:
                    LOG.log(Level.WARNING, "Recived unsuported command: NCP_DATA");
                    break;
                case NcpMessage.NCP_PROGRES:
                    LOG.log(Level.WARNING, "Recived unsuported command: NCP_PROGRES");
                    break;
                default:
                    LOG.log(Level.WARNING, "Recived unknown command: " + message.getType());
            }
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
        catch (ExceptionInInitializerError ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the dataManager
     */
    public DataManager getDataManager()
    {
        return dataManager;
    }

    /**
     * @param dataManager the dataManager to set
     */
    public void setDataManager(DataManager dataManager)
    {
        this.dataManager = dataManager;
    }
}
