package develop.p2p.chatchan;

import develop.p2p.chatchan.Config.Config;
import develop.p2p.chatchan.Config.Whitelist;
import develop.p2p.chatchan.Message.EncryptManager;
import develop.p2p.chatchan.Player.Player;
import develop.p2p.chatchan.Player.PlayerList;
import develop.p2p.chatchan.Server.CallServer;
import develop.p2p.chatchan.Server.ChatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.Socket;
import java.util.*;

public class Main
{
    public static Logger logger = LoggerFactory.getLogger("Main");
    public static int chatPort = 37564;
    public static int commandPort = 46573;
    public static int callPort = 41410;
    public static int keyLength = 24;
    static Config config = new Config();
    private static Whitelist whiteList = new Whitelist();
    public static ArrayList<String> whiteLst = new ArrayList<>();
    public static PlayerList playerList;
    public static void main(String[] arg)
    {
        try
        {
            logger.info("[SYSTEM] Loading library...");
            playerList = new PlayerList();
            final CallServer callServer = new CallServer();
            final ChatServer chatServer = new ChatServer();
            System.out.println("OK");
            logger.info("[SYSTEM] Loading ConfigFiles...");
            if (!config.saveDefaultConfig())
            {
                System.out.println("FAILED");
                logger.error("Failed Save Config File.\n");
                logger.error("[FATAL] Stopping server...\n");
                System.exit(1);
            }
            System.out.println("OK");
            logger.info("[SYSTEM] Parsing ConfigFiles...");
            try
            {
                chatPort = Integer.parseInt(config.getString("chatPort", "37564"));
                commandPort = Integer.parseInt(config.getString("commandPort", "46573"));
                callPort = Integer.parseInt(config.getString("callPort", "41410"));
                keyLength = Integer.parseInt(config.getString("keyLength", "24"));
                System.out.println("OK");
            }
            catch (Exception ignored)
            {
                System.out.println("FAILED");
                logger.error("Failed ParseInt Config Files.\n");
                logger.error("[FATAL] Stopping system...\n");
                System.exit(1);
            }
            logger.info("[SYSTEM] Loading WhiteListFiles...");
            if (!whiteList.saveDefaultConfig())
            {
                System.out.println("FAILED");
                logger.error("Failed Save Whitelist File.\n");
                logger.error("[FATAL] Stopping system...\n");
                System.exit(1);
            }
            System.out.println("OK");
            logger.info("[SYSTEM] Reading WhiteListFiles...");
            int i = 0;
            while (true)
            {
                i += 1;
                String wh = whiteList.getString("wh" + i, "NONE");
                if (wh.equals("NONE"))
                {
                    break;
                }
                whiteLst.add(wh);
            }
            System.out.println("OK");
            logger.info("[SYSTEM] Definition call server...");
            final Thread callServerThread = new Thread()
            {
                @Override
                public void run()
                {
                    callServer.call(callPort);
                }
            };
            System.out.println("OK");
            logger.info("[SYSTEM] Definition chat server...");
            final Thread chatServerThread = new Thread()
            {
                @Override
                public void run()
                {
                    chatServer.chat(chatPort);
                }
            };
            System.out.println("OK");
            logger.info("[SYSTEM] Starting call server...");
            callServerThread.start();
            System.out.println("OK");
            logger.info("[SYSTEM] Starting chat server...");
            chatServerThread.start();
            System.out.println("OK");
            logger.info("[SYSTEM] See help command for showing help.\n");
            logger.info("[SYSTEM] Ready\n");
            Scanner scanner = new Scanner(System.in);
            while (true)
            {
                String[] args = scanner.nextLine().split(" ");
                switch (args[0])
                {
                    case "stop":
                        logger.info("[CALL] Stopping server...");
                        callServerThread.stop();
                        System.out.println("OK");
                        logger.info("[CHAT] Stopping server...");
                        chatServerThread.stop();
                        System.out.println("OK");
                        logger.info("[FATAL] Stopping system...\n");
                        System.exit(0);
                        break;
                    case "help":
                        logger.info("[SYSTEM] ---HELP---\n");
                        logger.info("[SYSTEM] # help -- showing help.\n");
                        logger.info("[SYSTEM] # stop -- stopping all server.\n");
                        logger.info("[SYSTEM] # kick <PlayerName> -- player kick from all server.\n");
                        break;
                    case "list":
                        StringBuilder response = new StringBuilder();
                        response.append("[SYSTEM]");
                        int len = 0;
                        for (Player player : playerList.getPlayers())
                        {
                            String name = player.name;
                            String ip = player.ip;
                            response.append(String.format(" %s(%s)", name, ip));
                            len += 1;
                        }
                        if (response.toString().equals("[SYSTEM]"))
                            response = new StringBuilder().append("[SYSTEM] N/A\n");
                        else
                            response.append("\n");
                        logger.info("[SYSTEM] ---PlayerList---\n");
                        logger.info(response.toString());
                        logger.info("[SYSTEM] " + len + "/2\n");
                        break;
                    case "kick":
                        if (args.length != 2)
                        {
                            logger.info("[SYSTEM] Unknown Args. See help command for showing help.\n");
                            break;
                        }
                        Player player = playerList.getPlayerFromName(args[1]);
                        if (player != null)
                        {
                            Socket chatSocket = player.chatSocket;
                            Socket commandSocket = player.commandSocket;
                            Socket callSocket = player.callSocket;
                            boolean chatFlag = false;
                            boolean commandFlag = false;
                            boolean callFlag = false;
                            if (chatSocket != null)
                            {
                                chatSocket.close();
                                chatFlag = true;
                            }
                            if (commandSocket != null)
                            {
                                commandSocket.close();
                                commandFlag = true;
                            }
                            if (callSocket != null)
                            {
                                callSocket.close();
                                callFlag = true;
                            }
                            logger.info(String.format("[SYSTEM] Player(%s) kicked from %s%s%s\n", args[1], chatFlag ? "ChatServer ": "", commandFlag ? "CommandServer ": "", callFlag ? "CallServer ": ""));
                        }
                        else
                        {
                            logger.error("[SYSTEM] Player not found.\n");
                        }
                        break;
                    default:
                        logger.info("[SYSTEM] Unknown Command. See help command for showing help.\n");
                        break;
                }
                logger.info("[RESULT] OK.\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.info("[ALL] Stopping server...");
            logger.info("[FATAL] Stopping system...\n");

            System.exit(1);
        }
    }

}
