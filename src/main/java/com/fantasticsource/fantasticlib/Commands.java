package com.fantasticsource.fantasticlib;

import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.mctools.aw.RenderModes;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;
import java.util.*;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;
import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.WHITE;

public class Commands extends CommandBase
{
    private static LinkedHashMap<String, Integer> subcommands = new LinkedHashMap<>();

    static
    {
        subcommands.put("generate", 2);
    }


    @Override
    public String getName()
    {
        return MODID;
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
        if (sender.canUseCommand(subcommands.get("generate"), getName()))
        {
            result += AQUA + "/" + getName() + " rendermodes <playername> [renderchannel] [mode]" + WHITE + " - " + I18n.translateToLocalFormatted(MODID + ".cmd.rendermodes.comment");
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
        else if (args.length == 2)
        {
            switch (args[0])
            {
                case "rendermodes":
                    result.addAll(Arrays.asList(server.getPlayerList().getOnlinePlayerNames()));
                    break;
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
            case "rendermodes":
                if (args.length < 2)
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    return;
                }

                EntityPlayer player = PlayerData.getEntity(args[1]);
                if (player == null)
                {
                    notifyCommandListener(sender, this, "commands.generic.playerEntityNotFound");
                    return;
                }

                if (args.length >= 4)
                {
                    RenderModes.setRenderMode(player, args[2], args[3]);
                    notifyCommandListener(sender, this, MODID + ".cmd.rendermodes.get.comment", TextFormatting.GOLD + args[2] + TextFormatting.RESET, TextFormatting.GOLD + args[3] + TextFormatting.RESET, "" + TextFormatting.GOLD + player.getDisplayName() + TextFormatting.RESET);
                }
                else if (args.length == 3)
                {
                    String result = RenderModes.getRenderMode(player, args[2]);
                    if (result == null) notifyCommandListener(sender, this, MODID + ".cmd.rendermodes.notSet.comment", TextFormatting.GOLD + args[2] + TextFormatting.RESET, "" + TextFormatting.GOLD + player.getDisplayName() + TextFormatting.RESET);
                    notifyCommandListener(sender, this, MODID + ".cmd.rendermodes.get.comment", TextFormatting.GOLD + args[2] + TextFormatting.RESET, TextFormatting.GOLD + result + TextFormatting.RESET, "" + TextFormatting.GOLD + player.getDisplayName() + TextFormatting.RESET);
                }
                else //2
                {
                    LinkedHashMap<String, String> result = RenderModes.getRenderModes(player);
                    if (result == null) notifyCommandListener(sender, this, MODID + ".cmd.rendermodes.noneSet.comment", "" + TextFormatting.GOLD + player.getDisplayName() + TextFormatting.RESET);
                    else
                    {
                        for (Map.Entry<String, String> entry : result.entrySet())
                        {
                            notifyCommandListener(sender, this, MODID + ".cmd.rendermodes.get.comment", TextFormatting.GOLD + entry.getKey() + TextFormatting.RESET, TextFormatting.GOLD + entry.getValue() + TextFormatting.RESET, "" + TextFormatting.GOLD + player.getDisplayName() + TextFormatting.RESET);
                        }
                    }
                }
                break;


            default:
                notifyCommandListener(sender, this, getUsage(sender));
                break;
        }
    }
}
