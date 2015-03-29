/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kangur
 */
public class NcpMessage
{

    public final static int NCP_REGISTER = 0x52475354;
    public final static int NCP_RESULT = 0x52534c54;
    public final static int NCP_DATA = 0x44415441;
    public final static int NCP_PROGRES = 0x52474250;

    //   #define NCP_REGISTER	0x54534752 // 52475354 //RGST /*register*/
//#define NCP_DATA		0x41544144 // 44415441 //DATA //data
//#define NCP_RESULT		0x544c5352 // 52534c54 //RSLT //result
//#define NCP_PROGGRES	0x52474250 // 50424752 //PRGR //proggress
    public NcpMessage()
    {
        this.type = 0;
        this.number = 0;
        this.options1 = 0;
        this.options2 = 0;
        this.client = new UUID(0, 0);
        this.project = new UUID(0, 0);
        this.task = new UUID(0, 0);
        this.len = 40;
    }

    public NcpMessage(int type, int number, int options1, int options2, UUID client, UUID project, UUID task)
    {
        this.type = type;
        this.number = number;
        this.options1 = options1;
        this.options2 = options2;
        this.client = client;
        this.project = project;
        this.task = task;
        this.len = 40;
    }

    public NcpMessage(DatagramPacket packet) throws IOException
    {
        byte[] packData = packet.getData();
        int m_len = packet.getLength();
        DataInputStream input = new DataInputStream(new ByteArrayInputStream(packData));
        try
        {
            this.type = input.readInt(); //4
            this.number = input.readInt(); //4
            this.options1 = input.readInt();//4
            this.options2 = input.readInt();//4
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, "Creating ncp message exception 1", ex);
        }
        try
        {
            this.client = new UUID(input.readLong(), input.readLong());//8
            this.project = new UUID(input.readLong(), input.readLong());//8
            this.task = new UUID(input.readLong(), input.readLong());//8
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, "Creating ncp message exception 2", ex);
        }
        LOG.log(Level.FINE, "Created NCP message:" + "Type: " + type
            + ", Numer: " + number + ", Options 1: " + options1
            + ", Options 2: " + options2 + ", Client: " + client.toString()
            + ", Project: " + project.toString() + ", Task: " + task.toString());
        this.len = m_len;
        if (m_len > 40)
        {

            data = new byte[m_len - 40];
            String string = new String(data);
            LOG.log(Level.FINE, "HAS data: " + string);

        }

    }

 
    
    
    public DatagramPacket getDatagram() throws IOException
    {
        byte [] packiet = new byte[len];
        DataOutputStream output;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(len + 1);
        output = new DataOutputStream(byteStream);
        
        output.writeInt(type);//4
        output.writeInt(number);//4
        output.writeInt(options1);//4
        output.writeInt(options2);//4
        
        output.writeLong(client.getMostSignificantBits()); //4
        output.writeLong(client.getLeastSignificantBits()); //4
        
        output.writeLong(project.getMostSignificantBits()); //4
        output.writeLong(project.getLeastSignificantBits());//4
        
        output.writeLong(task.getMostSignificantBits());//4
        output.writeLong(task.getLeastSignificantBits());//4
        if(data != null)
            output.write(data);
        
            
           
            
        
        return   new DatagramPacket(byteStream.toByteArray(), len);
    }
    
    private static final Logger LOG = Logger.getLogger(NcpMessage.class.getName());

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public int getOptions1()
    {
        return options1;
    }

    public void setOptions1(int options1)
    {
        this.options1 = options1;
    }

    public int getOptions2()
    {
        return options2;
    }

    public void setOptions2(int options2)
    {
        this.options2 = options2;
    }

    public UUID getClient()
    {
        return client;
    }

    public void setClient(UUID client)
    {
        this.client = client;
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

    private int type;
    private int number;
    private int options1;
    private int options2;
    private UUID client;
    private UUID project;
    private UUID task;
    private byte[] data;
    private int len;

    public void setData(byte[] data)
    {
        len = data.length + len;
        this.data = data;
    }
 

    public byte[] getData()
    {
        return data;
    }
}
