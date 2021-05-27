package com.fantasticsource.fantasticlib;

import com.fantasticsource.fantasticlib.api.INBTCap;
import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.*;
import com.fantasticsource.mctools.animation.CBipedAnimation;
import com.fantasticsource.mctools.animation.ModelPlayerEdit;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;
import com.fantasticsource.mctools.data.CModpackDataHandler;
import com.fantasticsource.mctools.data.CWorldDataHandler;
import com.fantasticsource.mctools.event.GametypeChangedEvent;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.mctools.gui.screen.TestGUI;
import com.fantasticsource.mctools.nbtcap.NBTCap;
import com.fantasticsource.mctools.nbtcap.NBTCapStorage;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.ColorImmutable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
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
    public static final String DOMAIN = "flib"; //Referenced in some other mods
    public static final String NAME = "Fantastic Lib";
    public static final String VERSION = "1.12.2.044zzzzi";


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
        MinecraftForge.EVENT_BUS.register(BetterAttributeMod.class);
        Network.init();

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            isClient = true;
            if (FantasticConfig.entityRenderFixer) MinecraftForge.EVENT_BUS.register(EntityRenderFixer.class);
            MinecraftForge.EVENT_BUS.register(TooltipFixer.class);
            MinecraftForge.EVENT_BUS.register(TooltipAlterer.class);
            MinecraftForge.EVENT_BUS.register(ModelPlayerEdit.class);

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
    public void postInit(FMLPostInitializationEvent event)
    {
        Compat.betterportals = (Loader.isModLoaded("betterportals"));
        Compat.smoothfont = (Loader.isModLoaded("smoothfont"));
        Compat.baubles = (Loader.isModLoaded("baubles"));
        Compat.tiamatinventory = (Loader.isModLoaded("tiamatinventory"));
        ImprovedRayTracing.reloadConfigs();

        if (event.getSide() == Side.CLIENT) CBipedAnimation.init(event);

        DataFiles.output();
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


//    private static VectorN
//            v1 = new VectorN(1, 1, 1),
//            vX1 = new VectorN(1, 0, 0),
//            vY1 = new VectorN(0, 1, 0);
//
//    private static CPath
//            p1 = new CPathConstant(v1),
//            pX1YZ2 = new CPathConstant(new VectorN(1, 2, 2)),
//            p3 = new CPathConstant(v1.copy().scale(3)),
//            p7 = new CPathConstant(v1.copy().scale(7)),
//            pNeg1 = new CPathConstant(v1.copy().scale(-1)),
//            pIncreasing = new CPathLinear(new VectorN(0.2, 0.2, 0.2)),
//            pXIncreasing = new CPathLinear(vX1),
//            pX1 = new CPathConstant(vX1),
//            pY1 = new CPathConstant(vY1),
//            pYNeg1 = new CPathConstant(vY1.copy().scale(-1)),
//            pOneToInfintesimal = new CPathLinear(v1.copy().scale(5)).add(p1).power(pNeg1),
//            pNegOneToNegInfintesimal = new CPathLinear(v1.copy().scale(5)).add(p1).power(pNeg1).mult(pNeg1),
//            pOneToInfintesimalInv = p1.copy().add(pNegOneToNegInfintesimal),
//            pVSpiralIn = new CPathSinuous(pX1, 0.25).add(new CPathSinuous(pY1, 0.25, 0.25)).mult(pOneToInfintesimal).mult(p3);
////    vSpiralIn = new CPathSinuous(x1PerSec.copy().add(pXNeg3), 0.5).add(new CPathSinuous(y1PerSec.copy().add(pYNeg3), 0.5, 0.25));
//
//    @SubscribeEvent
//    public static void particleTest(TickEvent.ClientTickEvent event)
//    {
//        if (event.phase != TickEvent.Phase.END) return;
//
//        EntityPlayer player = Minecraft.getMinecraft().player;
//        if (player == null) return;
//
//        CPath follow = new CPathFollowEntity(player).add(new CPathConstant(new VectorN(0, player.eyeHeight, 0)));
//
//        CPath yaw = new CPathEntityYaw(player), pitch = new CPathEntityPitch(player);
//        CPath directionalSpiral = pVSpiralIn.copy().rotate(pX1, pitch).rotate(pYNeg1, yaw);
//        CPath look = new CPathEntityLook(player).mult(p7);
//
//        if (player.world.isRemote)
//        {
//            for (int i = 0; i < 10; i++)
//            {
//                double offset = Math.PI * 2 * Math.random();
//                CPathSinuous path = (CPathSinuous) directionalSpiral.copy();
//                path.thetaOffset = offset;
//                ((CPathSinuous) path.transforms.get(0).paths[0]).thetaOffset += offset;
//                PathedParticle particle = new PathedParticle(follow, path.add(look));
//                particle.setAlphaF(0.2f);
//                particle.hsvPath(pXIncreasing.copy().add(new CPathConstant(new VectorN(Math.random(), 1, 1))).mod(pX1YZ2));
//            }
//        }
//    }


    //Staff spin based on the dual-lightsaber skin in AW; a vanilla sword would not match up correctly unless you added an offset constant path to items
//    static CBipedAnimation staffSpin = new CBipedAnimation();
//
//    static
//    {
//        //Standard hand-swap code
//        staffSpin.leftItem.yScalePath.path = new CPathConstant(new VectorN(-1));
//
//        //Staff spin arms
//        staffSpin.rightArm.zRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.5));
//        staffSpin.rightArm.yRotPath.path = new CPathConstant(new VectorN(0));
//        staffSpin.rightArm.xRotPath.path = new CPathSinuous(new CPathConstant(new VectorN(Math.PI * 0.7)), 0.5);
//        staffSpin.leftArm.zRotPath.path = new CPathConstant(new VectorN(-Math.PI * 0.5));
//        staffSpin.leftArm.yRotPath.path = new CPathConstant(new VectorN(0));
//        staffSpin.leftArm.xRotPath.path = new CPathSinuous(new CPathConstant(new VectorN(Math.PI * 0.7)), 0.5);
//
//        //Staff spin hand swap
//        staffSpin.handItemSwap.path = new CPathSinuous(new CPathConstant(new VectorN(1)), 0.5, 0.75);
//
//        //Staff spin item rotation correction
//        staffSpin.rightItem.xRotPath.path = new CPathSinuous(new CPathConstant(new VectorN(Math.PI * 0.2)), 0.5);
//        staffSpin.leftItem.xRotPath.path = new CPathSinuous(new CPathConstant(new VectorN(Math.PI * 0.2)), 0.5, 0.5);
//
//        //Actual staff spin
//        staffSpin.rightItem.zRotPath.path = new CPathLinear(new VectorN(Math.PI * 2)).add(new CPathConstant(new VectorN(Math.PI)));
//        staffSpin.leftItem.zRotPath.path = new CPathLinear(new VectorN(-Math.PI * 2)).add(new CPathConstant(new VectorN(Math.PI)));
//    }
//
//    @SubscribeEvent
//    public static void animationTest(EntityJoinWorldEvent event)
//    {
//        Entity entity = event.getEntity();
//        if (!(entity instanceof EntityPlayerMP)) return;
//
//        CBipedAnimation.addAnimation(entity, staffSpin);
//    }


    //Swing animation
//    static CBipedAnimation swing = new CBipedAnimation();
//
//    static
//    {
//        swing.duration = 500;
//        swing.bodyFacesLookDirection = true;
//
//        swing.leftArm.zRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.25));
//        swing.leftArm.yRotPath.path = new CPathConstant(new VectorN(0));
//        swing.leftArm.xRotPath.path = new CPathConstant(new VectorN(Math.PI * 1.75)).add(
//                new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
//                        .add(new CPathConstant(new VectorN(0.5)))
//                        .power(new CPathConstant(new VectorN(4)))
//                        .mult(new CPathConstant(new VectorN(-Math.PI)))
//        );
//
//        swing.leftItem.zRotPath.path = new CPathConstant(new VectorN(Math.PI));
//        swing.leftItem.xRotPath.path = new CPathConstant(new VectorN(Math.PI * -0.15)).add(
//                new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
//                        .add(new CPathConstant(new VectorN(0.5)))
//                        .power(new CPathConstant(new VectorN(4)))
//                        .mult(new CPathConstant(new VectorN(Math.PI * -0.6)))
//        );
//    }
//
//    @SubscribeEvent
//    public static void swordSwingTest(PlayerInteractEvent.RightClickItem event)
//    {
//        Entity entity = event.getEntity();
//        if (!(entity instanceof EntityPlayerMP)) return;
//
//        CBipedAnimation animation = (CBipedAnimation) swing.copy();
//        animation.setAllStartTimes(System.currentTimeMillis());
//        CBipedAnimation.addAnimation(entity, animation);
//    }
}
