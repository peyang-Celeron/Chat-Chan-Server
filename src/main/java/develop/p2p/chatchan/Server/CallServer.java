package develop.p2p.chatchan.Server;

import develop.p2p.chatchan.Interface.ServerThreadBase;
import develop.p2p.chatchan.Main;
import develop.p2p.chatchan.Server.Thread.CallThread;

import java.io.*;
import java.net.*;

public class CallServer implements ServerThreadBase
{
    @Override
    public void start(int port)
    {
        try (ServerSocket listener = new ServerSocket())
        {
            listener.setReuseAddress(true);
            listener.bind(new InetSocketAddress(port));
            while (true)
            {
                CallThread thread  = new CallThread(listener.accept());
                thread.start();
            }
        }
        catch (IOException e)
        {
            Main.logger.error("[CALL] Error: ");
            e.printStackTrace();
        }
    }

}
