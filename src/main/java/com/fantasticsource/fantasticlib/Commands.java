package com.fantasticsource.fantasticlib;

import com.fantasticsource.tools.Tools;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;
import java.util.*;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;
import static net.minecraft.util.text.TextFormatting.AQUA;

public class Commands extends CommandBase
{
    private static LinkedHashMap<String, Integer> subcommands = new LinkedHashMap<>();

    static
    {
        subcommands.put("uptime", 3);
        subcommands.put("heapdump", 3);
    }


    @Override
    public String getName()
    {
        return MODID;
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("flib");
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        String result = "";

        if (sender.canUseCommand(subcommands.get("uptime"), getName()))
        {
            result += "\n" + AQUA + "/" + getName() + " uptime";
        }
        if (sender.canUseCommand(subcommands.get("heapdump"), getName()))
        {
            result += "\n" + AQUA + "/" + getName() + " heapdump";
        }

        return !result.equals("") ? result : I18n.translateToLocalFormatted("commands.generic.permission");
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 0) sender.getCommandSenderEntity().sendMessage(new TextComponentString(getUsage(sender)));
        else subCommand(sender, args);
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        ArrayList<String> result = new ArrayList<>();

        String partial = args[args.length - 1];
        if (args.length == 1)
        {
            for (Map.Entry<String, Integer> entry : subcommands.entrySet())
            {
                if (sender.canUseCommand(entry.getValue(), getName())) result.add(entry.getKey());
            }
        }

        if (partial.length() != 0) result.removeIf(k -> partial.length() > k.length() || !k.substring(0, partial.length()).equalsIgnoreCase(partial));
        return result;
    }

    private void subCommand(ICommandSender sender, String[] args)
    {
        String cmd = args[0];

        if (!sender.canUseCommand(subcommands.get(cmd), getName()))
        {
            notifyCommandListener(sender, this, "commands.generic.permission");
            return;
        }

        switch (cmd)
        {
            case "uptime":
                if (FantasticLib.serverStartTime == -1)
                {
                    notifyCommandListener(sender, this, MODID + ".cmd.uptime.noserver");
                }
                else
                {
                    long n = System.nanoTime() - FantasticLib.serverStartTime;
                    int s = (int) ((n / 1000_000_000L) % 60);
                    int m = (int) ((n / 1000_000_000L / 60L) % 60);
                    int h = (int) ((n / 1000_000_000L / 60L / 60L) % 24);
                    int d = (int) (n / 1000_000_000L / 60L / 60L / 24L);
                    notifyCommandListener(sender, this, MODID + ".cmd.uptime", d, h, m, s);
                }
                break;


            case "heapdump":
                Tools.heapdump();
                break;


            default:
                notifyCommandListener(sender, this, getUsage(sender));
                break;
        }
    }
}
