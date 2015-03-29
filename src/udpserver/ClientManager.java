/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.Predicate;
import org.omg.CosNaming.NamingContextPackage.NotFound;

/**
 *
 * @author Kangur
 */
public class ClientManager
{

    private static final Logger LOG = Logger.getLogger(ClientManager.class.getName());
    private List<Client> clients;
    private ForkJoinPool thredPool;

    public ClientManager()
    {
        clients = new LinkedList<Client>();
    }

    public Client getClient(UUID client)
    {

        ListIterator<Client> iter = clients.listIterator();

        while (iter.hasNext())
        {
            Client cl = iter.next();
            if (cl.getClient().compareTo(client) == 0)
            {
                return cl;
            }
        }

        return null;
    }

    public void addClient(Client client)
    {

        clients.add(client);
        LOG.log(Level.FINE, "Added");
       
    }

    public void addClient(DatagramPacket packiet,DatagramSocket socket, BlockingQueue<Task> queue)
    {
        try
        {
            Client client = new Client(packiet,socket,queue);
            clients.add(client);
        }
        catch (ExceptionInInitializerError ex)
        {
            LOG.log(Level.SEVERE, "Exception during adding new client", ex);
        }

    }
    
    public Client findClient(UUID clientUuid) throws NoSuchElementException
    {
        LOG.log(Level.FINEST, "Finding client: " + clientUuid.toString());
        for(Client c:clients )
        {
            LOG.log(Level.FINEST, "\t comparing with:" + c.getClient().toString());
            if(c.getClient().compareTo(clientUuid) == 0)
            {
                LOG.log(Level.FINEST, "Found client: " + clientUuid.toString());
                return c;
            }
        }
        throw new NoSuchElementException("Couldn't find client: " + clientUuid.toString());


    }

    /**
     *
     * @param message
     * @return
     * @throws NoSuchElementException
     */
    public Client findClient(NcpMessage message) throws NoSuchElementException
    {
        return findClient(message.getClient());
    }
    
    public void runClients()
    {
        thredPool = new ForkJoinPool(15);
        ListIterator<Client> client = clients.listIterator();
        
        while (client.hasNext())
        {
            
            thredPool.execute(client.next());
        }
    }
    

}


