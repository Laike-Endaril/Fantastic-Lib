package com.fantasticsource.fantasticlib;

import com.fantasticsource.fantasticlib.api.INBTCap;
import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.*;
import com.fantasticsource.mctools.aw.ForcedAWSkinOverrides;
import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.mctools.aw.TransientAWSkinHandler;
import com.fantasticsource.mctools.component.path.CPathFollowEntity;
import com.fantasticsource.mctools.data.CModpackDataHandler;
import com.fantasticsource.mctools.data.CWorldDataHandler;
import com.fantasticsource.mctools.event.GametypeChangedEvent;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.mctools.gui.screen.TestGUI;
import com.fantasticsource.mctools.nbtcap.NBTCap;
import com.fantasticsource.mctools.nbtcap.NBTCapStorage;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.component.path.CPathConstant;
import com.fantasticsource.tools.component.path.CPathLinear;
import com.fantasticsource.tools.component.path.CPathSinuous;
import com.fantasticsource.tools.datastructures.ColorImmutable;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = FantasticLib.MODID, name = FantasticLib.NAME, version = FantasticLib.VERSION, acceptableRemoteVersions = "*")
public class FantasticLib
{
    public static final String MODID = "fantasticlib";
    public static final String DOMAIN = "flib";
    public static final String NAME = "Fantastic Lib";
    public static final String VERSION = "1.12.2.044zg";


    public static long serverStartTime = -1;
    public static boolean isClient = false;
    public static final boolean DEV_ENV = ReflectionTool.getField(ItemStack.class, "stackSize") != null;

    static
    {
        ColorImmutable.init();
    }

    public FantasticLib()
    {
        MinecraftForge.EVENT_BUS.register(FantasticLib.class);

        MinecraftForge.EVENT_BUS.register(NBTCap.class);

        Network.init();

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            isClient = true;
            if (FantasticConfig.entityRenderFixer) MinecraftForge.EVENT_BUS.register(EntityRenderFixer.class);
            MinecraftForge.EVENT_BUS.register(TooltipFixer.class);
            if (DEV_ENV) MinecraftForge.EVENT_BUS.register(TestGUI.class);
        }

        MinecraftForge.EVENT_BUS.register(PlayerData.class);
    }


    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void syncConfig(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ImprovedRayTracing.reloadConfigs();
    }


    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PlayerData.load();

        CapabilityManager.INSTANCE.register(INBTCap.class, new NBTCapStorage(), () -> null);

        if (event.getSide() == Side.CLIENT) Render.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        CModpackDataHandler.load(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) throws IllegalAccessException
    {
        DataFiles.output();
        Compat.betterportals = (Loader.isModLoaded("betterportals"));
        Compat.smoothfont = (Loader.isModLoaded("smoothfont"));
        Compat.baubles = (Loader.isModLoaded("baubles"));
        Compat.tiamatinventory = (Loader.isModLoaded("tiamatinventory"));
        ImprovedRayTracing.reloadConfigs();

        if (Loader.isModLoaded("armourers_workshop"))
        {
            RenderModes.init();

            MinecraftForge.EVENT_BUS.register(TransientAWSkinHandler.class);
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) ForcedAWSkinOverrides.clientInit();
        }
    }


    @EventHandler
    public static void serverAboutToStart(FMLServerAboutToStartEvent event)
    {
        MCTools.serverStart(event);
    }

    @EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        CWorldDataHandler.load(event);

        event.registerServerCommand(new Commands());
        event.registerServerCommand(new CmdGive());
    }

    @EventHandler
    public static void serverStarted(FMLServerStartedEvent event)
    {
        serverStartTime = System.nanoTime();
    }

    @EventHandler
    public static void serverStopped(FMLServerStoppedEvent event)
    {
        serverStartTime = -1;
        MCTools.serverStop(event);
        CWorldDataHandler.clear(event);
        GametypeChangedEvent.PLAYER_GAMETYPES.clear();
    }

    @SubscribeEvent
    public static void inventorySyncFix(InventoryChangedEvent event)
    {
        if (FantasticConfig.inventoryDesyncFixer && event.getEntity() instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
            if (!player.isCreative())
            {
                player.sendAllContents(player.inventoryContainer, player.inventoryContainer.getInventory());
                player.inventoryContainer.detectAndSendChanges();

                if (player.openContainer != null && player.openContainer != player.inventoryContainer)
                {
                    player.sendAllContents(player.openContainer, player.openContainer.getInventory());
                    player.openContainer.detectAndSendChanges();
                }
            }
        }
    }


//    @SubscribeEvent
//    public static void test(PlayerInteractEvent.RightClickItem event)
//    {
//        EntityPlayer player = event.getEntityPlayer();
//
//        Vec3d lookVec = player.getLookVec();
//
//        VectorN
//                x1 = new VectorN(1, 0, 0),
//                z1 = new VectorN(0, 0, 1),
//                y3 = new VectorN(0, 3, 0),
//                xNeg3 = x1.copy().scale(-3),
//                zNeg3 = z1.copy().scale(-3);
//
//        CPath fromEyes = new CPathLinear(new VectorN(lookVec.x, lookVec.y, lookVec.z).scale(3));
//        CPath follow = new CPathFollowEntity(player);
//        CPath up = new CPathLinear(y3);
//
//        CPath x1P = new CPathConstant(x1);
//        CPath z1P = new CPathConstant(z1);
//        CPath hCirclePart1 = new CPathSinuous(x1P, 0.5);
//        CPath hCirclePart2 = new CPathSinuous(z1P, 0.5, 0.25);
//
//        CPath x1PerSec = new CPathLinear(x1);
//        CPath z1PerSec = new CPathLinear(z1);
//        CPath hSpiralOutPart1 = new CPathSinuous(x1PerSec, 0.5);
//        CPath hSpiralOutPart2 = new CPathSinuous(z1PerSec, 0.5, 0.25);
//
//        CPath xNeg1PerSec = new CPathLinear(xNeg3, x1);
//        CPath zNeg1PerSec = new CPathLinear(zNeg3, z1);
//        CPath hSpiralInPart1 = new CPathSinuous(xNeg1PerSec, 0.5);
//        CPath hSpiralInPart2 = new CPathSinuous(zNeg1PerSec, 0.5, 0.25);
//
//        if (player.world.isRemote)
//        {
////            new PathedParticle(player.world, player.posX, player.posY + player.eyeHeight, player.posZ, fromEyes);
////            new PathedParticle(player.world, 1, player.eyeHeight, 0, follow);
////            new PathedParticle(player.world, player.posX, player.posY + player.height / 2, player.posZ, hCirclePart1);
////            new PathedParticle(player.world, player.posX, player.posY + player.height / 2, player.posZ, hCirclePart2);
////            new PathedParticle(player.world, 0, player.height / 2, 0, follow, hCirclePart1, hCirclePart2, up);
////            new PathedParticle(player.world, 0, player.height / 2, 0, follow, hSpiralOutPart1, hSpiralOutPart2);
//            new PathedParticle(player.world, 0, player.height / 2, 0, follow, hSpiralInPart1, hSpiralInPart2);
//        }
//    }
}
