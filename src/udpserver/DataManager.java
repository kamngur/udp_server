/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @class DataManager
 * @brief Glowna klasa opdowiedzialna za zarzadzanie wszystkimi obiektami jaki i
 * calym serwerem
 * @author Kangur
 */
public class DataManager
{

    private static final Logger LOG = Logger.getLogger(Server.class.getName());
    private Server server;
    private ClientManager clients;
    private BlockingQueue<Task>  queue;

    public BlockingQueue<Task> getQueue()
    {
        return queue;
    }

    public void setQueue(SynchronousQueue<Task> queue)
    {
        this.queue = queue;
    }
    private TaskManager taskManager;
    

    public Server getServer()
    {
        return server;
    }

    public void setServer(Server server)
    {
        this.server = server;
    }

    public ClientManager getClients()
    {
        return clients;
    }

    public void setClients(ClientManager clients)
    {
        this.clients = clients;
    }

    public DataManager(Server server, ClientManager clients)
    {
        this.server = server;
        this.clients = clients;
        this.server.setDataManager(this);
        this.queue = new ArrayBlockingQueue<Task>(200);
        
        this.taskManager = new TaskManager(queue);
        
    }

    public void handleIncomingData(NcpMessage message)
    {
        if (message.getType() == NcpMessage.NCP_REGISTER)
        {
            //Client newClient = new Client(message.);
            LOG.log(Level.INFO, "Recived result from: " + message.getClient().toString() + " " + message.getTask());

        }
    }

    public void handleResults(NcpMessage message)
    {
        LOG.log(Level.INFO, "Recived result from: " + message.getClient().toString() + " " + message.getTask());
        Client client;
        try
        {
            client = clients.findClient(message);
            client.handleResult(message);
            
        }
        catch (NoSuchElementException e)
        {
            LOG.log(Level.WARNING, "Exception: "  + e.getLocalizedMessage());
        }
            
    }
    
    
    public void startProcessing()
    {
       
       taskManager.addLoopbackTasks(100);
       clients.runClients();
       
 
        
    }
    //public void addCl
}
