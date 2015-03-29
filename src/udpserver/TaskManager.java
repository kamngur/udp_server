/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 * @class TaskManager
 * @brief TaskManager zarzadza i dodaje zadania do wykonania do kolejki zadan
 * @author kangur
 */
public class TaskManager implements Runnable
{

    private  final Logger LOG = Logger.getLogger(Server.class.getName());
    private  BlockingQueue<Task> queue;
    private  int taskCount = 0;
    private  int maxTaskCount = 200;

    private   String LOOPBACK_DATA = "abcdefghijABCDEFGHIJ0123456789abcdefghijABCDEFGHIJ0123456789abcdefghijABCDEFGHIJ0123456789";
    private   UUID LOOPBACK_UUID = new UUID(0, 0);

    TaskManager(BlockingQueue<Task> q)
    {
        
        
        queue = q;
        //Tutaj mozna dodac zadania jesli od razu chce dodac wszystkie.
    }

    /**
     *
     */
    @Override
    public void run()
    {
        try
        {
            while (taskCount < maxTaskCount)
            {
                queue.put(produceLoopbackTask());
            }
        }
        catch (InterruptedException ex)
        {

            LOG.log(Level.SEVERE, "Exception:" + ex.getMessage());
        }
        catch (NullPointerException ex)
        {
            LOG.log(Level.WARNING, "Exception:" + ex.getMessage());
        }
    }

    private Task produce()
    {

        return null;//new Task();

    }

    private Task produceLoopbackTask()
    {

        return new Task(LOOPBACK_DATA.getBytes(), LOOPBACK_UUID);

    }

    private void produceAllTask()
    {

    }

    public  int getMaxTaskCount()
    {
        return maxTaskCount;
    }

    public  void setMaxTaskCount(int maxTaskCount)
    {
        maxTaskCount = maxTaskCount;
    }

     public void addLoopbackTasks(int taskCount)
    {
        try
        {
            queue.clear();
            int i = 0;
            while (i < taskCount)
            {
                queue.add(produceLoopbackTask());
                i++;
            }

        }
       // catch (InterruptedException ex)
       // {
//
      //      LOG.log(Level.SEVERE, "Exception:" + ex.getMessage());
      //  }
        catch (IllegalStateException ex)
        {
              LOG.log(Level.WARNING, "Exception (probably no clients):" + ex.getMessage());
        }
        catch (NullPointerException ex)
        {
            LOG.log(Level.WARNING, "Exception:" + ex.getMessage());
        }
    }

}
