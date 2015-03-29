/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Kangur
 */
public class UdpServer
{

    /**
     * @param args the command line arguments
     */
    public static void main2(String[] args)
    {
        // TODO code application logic here
        FileHandler handler;
        try
        {
            handler = new FileHandler("test.log", true);
            Formatter format = new SimpleFormatter();
            handler.setFormatter(format);
            Logger.getLogger("").addHandler(handler);
        }
        catch (IOException ex)
        {
            Logger.getLogger(UdpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SecurityException ex)
        {
            Logger.getLogger(UdpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static final Logger LOG = Logger.getLogger(UdpServer.class.getName());
}
