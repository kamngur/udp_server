/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver;

import java.sql.Time;
import java.util.Comparator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * @brief Klasa reprezentujaca zadanie. Ma przechowywa dane startowe i wynik
 * zadania. Ma za zadanie rowniez przechowywac dane statystyczne (czas poczatku
 * i konca zadania)
 * @author kangur
 */
class Task
{

    public class TaskComparator implements Comparator<Task>
    {

        @Override
        public int compare(Task t1, Task t2)
        {
            if (t2.getId() == t1.getId())
            {
                return 0;
            }
            else if (t1.getId() > t2.getId())
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
    }

    @Override
    public String toString()
    {
        //  return "Task{" + "startTime=" + startTime + ", endTime=" + endTime + ", inData=" + inData + ", outData=" + outData + ", id=" + id + ", taskType=" + taskType + '}';
        return "Task{" + "id=" + id + ", taskType=" + taskType +", duration=" + getDurationString() +" ms, startTime=" + startTime + ", endTime=" + endTime + '}';
    }

    public Task(byte[] inData, UUID taskType)
    {
        Task.objectCounter++;
        this.inData = inData;
        this.id = Task.objectCounter;
        this.taskType = taskType;
    }

    private static final Logger LOG = Logger.getLogger(Server.class.getName());

    public DateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(DateTime startTime)
    {
        this.startTime = startTime;
    }

    public void setStartTime()
    {
        this.startTime = DateTime.now();
    }

    public void setEndTime()
    {
        this.endTime = DateTime.now();
    }

    public DateTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime(DateTime endTime)
    {
        this.endTime = endTime;
    }

    public long getDuration()
    {
        try
        {

            Interval interval;
            if (endTime == null)
            {
                interval = new Interval( startTime,DateTime.now());
            }
            else
            {
                interval = new Interval(startTime,endTime);
            }
            return interval.toDurationMillis();
        }
        catch (IllegalArgumentException ex)
        {
           LOG.log(Level.WARNING, "Error : " + ex.getLocalizedMessage());
           return 0;
        }

    }

    public String getDurationString()
    {
        Interval interval;
        if (endTime == null)
        {
            interval = new Interval(startTime, DateTime.now());
        }
        else
        {
            interval = new Interval(startTime, endTime);
        }
        return Long.toString(interval.toDurationMillis());
    }

    public byte[] getInData()
    {
        return inData;
    }

    public void setInData(byte[] inData)
    {
        this.inData = inData;
    }

    public byte[] getOutData()
    {
        return outData;
    }

    public void setOutData(byte[] outData)
    {
        this.outData = outData;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public UUID getTaskType()
    {
        return taskType;
    }

    public void setTaskType(UUID taskType)
    {
        this.taskType = taskType;
    }

    private UUID clientId;
    private UUID taskType;
    private byte[] inData;
    private byte[] outData;
    private int id;
    private DateTime startTime;
    private DateTime endTime;
    private static int objectCounter = 0;
}
