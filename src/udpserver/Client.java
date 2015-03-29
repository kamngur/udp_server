/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kangur
 */
public final class Client implements Runnable
{

    public static final long TIMEOUT = 5000; //5s timeout

    public Client(DatagramPacket packiet, DatagramSocket socket, BlockingQueue<Task> tasks) throws ExceptionInInitializerError
    {
        this.socket = socket;
        address = packiet.getSocketAddress();
        try
        {
            NcpMessage registerMessage = new NcpMessage(packiet);
            if (registerMessage.getType() != NcpMessage.NCP_REGISTER) //Register
            {
                LOG.log(Level.WARNING, "Register massage type :" + Integer.toHexString(registerMessage.getType()) + " don't match value:" + Integer.toHexString(NcpMessage.NCP_REGISTER));
                throw new ExceptionInInitializerError("Wrong register message type" + Integer.toHexString(registerMessage.getType()));
            }
            client = registerMessage.getClient();
            project = registerMessage.getProject();
            task = registerMessage.getTask();
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
            throw new ExceptionInInitializerError(ex.toString());
        }
        LOG.log(Level.FINE, "Created client: " + this.toString());
        pendingTaskQueue = tasks;
        LOG.log(Level.INFO, "Created Client :"  + this.toString());
                
    }

    /**
     *
     * @return
     */
    public SocketAddress getAddress()
    {
        return address;
    }

    public void setAddress(SocketAddress address)
    {
        this.address = address;
    }

    public UUID getProject()
    {
        return project;
    }

    public void setProject(UUID project)
    {
        this.project = project;
    }

    public UUID getTask()
    {
        return task;
    }

    public void setTask(UUID task)
    {
        this.task = task;
    }

    public UUID getClient()
    {
        return client;
    }

    public void setClient(UUID client)
    {
        this.client = client;
    }

    @Override
    public String toString()
    {
        return "Client UUID: " + client.toString() + ", project : " + project.toString() + " adress: " + address.toString();
    }

    @Override
    public void run()
    {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        running = false;
        try
        {
            while (true)
            {
                if (running)
                {
                    Thread.sleep(10); //sleep 10 ms
                    if (currentTask.getDuration() > Client.TIMEOUT)
                    {
                         currentTask.setEndTime();
                        LOG.log(Level.WARNING, "Timeout durnig task  " + currentTask.toString());
                        running = false;

                    }
                }
                else
                {
                    currentTask = pendingTaskQueue.remove();
                    NcpMessage message = new NcpMessage(NcpMessage.NCP_DATA, currentTask.getId(), 0, 0, this.client, this.project, currentTask.getTaskType());
                    message.setData(currentTask.getInData());
                    DatagramPacket packiet;
                    try
                    {
                        packiet = message.getDatagram();
                        packiet.setSocketAddress(address);
                        currentTask.setStartTime();
                        socket.send(packiet);
                        running = true;
                    }
                     catch (IOException ex)
                    {
                        LOG.log(Level.WARNING, "Failed to convert NcpMessage to DatagramPackiet, exception:" + ex.getLocalizedMessage());
                    }

                }

            }

        }
        catch (Exception e)
        {
             LOG.log(Level.WARNING, "Failed to convert NcpMessage to DatagramPackiet, exception:" + e.getLocalizedMessage());
                    
        }
    }

    void handleResult(NcpMessage message)
    {
       // LOG.log(Level.INFO, "Recived result:" + message.toString());
        currentTask.setEndTime();
        LOG.log(Level.INFO, "Task ended. Duration: " + currentTask.getDurationString());
        running = false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private SocketAddress address;
    private DatagramSocket socket;
    private UUID project;
    private UUID task;
    private UUID client;
    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    private BlockingQueue<Task> pendingTaskQueue;
    private ArrayList<Task> doneTasks;
    private Task currentTask;
    private boolean running;

}
