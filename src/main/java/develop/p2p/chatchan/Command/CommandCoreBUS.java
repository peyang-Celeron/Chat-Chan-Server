package develop.p2p.chatchan.Command;

import develop.p2p.chatchan.Enum.EnumCommandOutput;
import develop.p2p.chatchan.Interface.CommandBase;
import develop.p2p.chatchan.Player.Player;
import org.slf4j.Logger;

import java.util.ArrayList;

public class CommandCoreBUS
{
    private int length = 0;
    private ArrayList<CommandBase> list = new ArrayList<>();
    private CommandBase defaultCommand;
    public <T extends CommandBase> void listen(T cmdCls)
    {
        list.add(cmdCls);
        length += 1;
    }

    public <T extends CommandBase> void remove (T cmdCls)
    {
        list.remove(cmdCls);
        length -= 1;
    }

    public int getSize()
    {
        return length;
    }

    public EnumCommandOutput run(Player sender, String commandName, ArrayList<String> args, Logger logger) throws Exception
    {
        EnumCommandOutput output = null;
        for (CommandBase command: list)
        {
            if (command.getName().equals(commandName))
                output = command.execute(sender, commandName, args, logger);
        }

        if (output == null && defaultCommand != null)
        {
            logger.error("[SYNTAX] Command not found. show help.");
            output = defaultCommand.execute(sender, commandName, args, logger);
        }
        else if (output == null)
            output = EnumCommandOutput.NOTFOUND;
        return output;
    }

    public <T extends CommandBase> void setDefault(T cmdCls)
    {
        defaultCommand = cmdCls;
    }
    public ArrayList<CommandBase> getCommandList()
    {
        return list;
    }

}
