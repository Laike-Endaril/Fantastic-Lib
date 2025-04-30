package com.fantasticsource.fantasticlib;

import com.fantasticsource.fantasticlib.api.INBTCap;
import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.*;
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
    public static final String VERSION = "1.12.2.058";


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
            MinecraftForge.EVENT_BUS.register(TooltipAlterer.class);

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
            MCTools.syncInventory((EntityPlayerMP) event.getEntity());
        }
    }


    //Staff spin based on the dual-lightsaber skin in AW; a vanilla sword would not match up correctly unless you added an offset constant path to items
    //Requires running of CBipedAnimation.init()
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


    //Animation tests
    //Requires running of CBipedAnimation.init()
//    static CBipedAnimation animation = new CBipedAnimation();
//
//    static
//    {
//        animation.removeAt = 500;
//        animation.bodyFacesLookDirection = true;
//
//        animation.rightLeg.zRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.15));
//        animation.rightLeg.yRotPath.path = new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
//                .add(new CPathConstant(new VectorN(0.5)))
//                .power(new CPathConstant(new VectorN(8)))
//                .mult(new CPathConstant(new VectorN(Math.PI * -0.05)));
//        animation.rightLeg.xRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.25)).add(
//                new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
//                        .add(new CPathConstant(new VectorN(0.5)))
//                        .power(new CPathConstant(new VectorN(8)))
//                        .mult(new CPathConstant(new VectorN(Math.PI * -0.75)))
//        );
//
//        animation.leftLeg.zRotPath.path = new CPathConstant(new VectorN(0));
//        animation.leftLeg.yRotPath.path = new CPathConstant(new VectorN(0));
//        animation.leftLeg.xRotPath.path = new CPathConstant(new VectorN(0));
//    }
//
//    @SubscribeEvent
//    public static void animationTest(PlayerInteractEvent.RightClickItem event)
//    {
//        Entity entity = event.getEntity();
//        if (!(entity instanceof EntityPlayerMP)) return;
//
//        CBipedAnimation animation = (CBipedAnimation) FantasticLib.animation.copy();
//        animation.setAllStartTimes(System.currentTimeMillis());
//        CBipedAnimation.addAnimation(entity, animation);
//    }


//    @SideOnly(Side.CLIENT)
//    @SubscribeEvent
//    public static void particleTest(TickEvent.ClientTickEvent event)
//    {
//        World world = Minecraft.getMinecraft().world;
//        if (event.phase != TickEvent.Phase.END || world == null) return;
//
//
//        for (Entity entity : Minecraft.getMinecraft().world.loadedEntityList)
//        {
//            if (entity instanceof EntityLivingBase) particlesTextureTest((EntityLivingBase) entity);
//        }
//    }
//
//    public static PathedParticleSharedRenderData particleRenderData;
//    public static SpriteMetaData spriteMetaData = new SpriteMetaData(128, 128, 57, 73, 64, 80);
//    public static VectorN
//            v1_1 = new VectorN(1),
//            v1_n1 = new VectorN(-1),
//            v3_0 = new VectorN(0, 0, 0),
//            v3_1_05_0 = new VectorN(1, 0.5, 0),
//            v3_0_1_0 = new VectorN(0, 1, 0),
//            v3_0_001_0 = new VectorN(0, 0.01, 0);
//    public static CPath
//            pathNone = new CPathConstant(v3_0),
//            pathFalling = new CPathLinear(new VectorN(0, -1d * 1000 / 20, 0)),
//            pathRotation = new CPathLinear(new VectorN(0, 0, Math.PI * 100)).add(new CPathConstant(new VectorN(0, Math.PI * 0.5, 0))),
//            pathAlphaLinearFadeout = new CPathConstant(v1_1).add(new CPathLinear(v1_n1));
//
//    public static PathedParticleFactory particleFactory = null, particleFactory2;
//
//    public static void particlesTextureTest(EntityLivingBase entityLivingBase)
//    {
//        if (particleFactory == null)
//        {
//            particleRenderData = new PathedParticleSharedRenderData(true, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, "minecraft:textures/particle/particles.png");
//
//
//            particleFactory = args ->
//            {
//                PathedParticle particle = new PathedParticle(particleRenderData, pathNone);
//                particle.spriteMetaData = spriteMetaData;
//
//                particle.setMaxAge(200);
//                particle.alphaPath(pathAlphaLinearFadeout);
//
//                if (args != null)
//                {
//                    PathedParticle parent = (PathedParticle) args[0];
//                    Vec3d deathPos = (Vec3d) parent.extraDeathArgs[0];
//                    particle.applyPath(new CPathConstant(new VectorN(deathPos.x, deathPos.y, deathPos.z).add(v3_0_001_0)));
//
//                    particle.rotationPath(new CPathConstant(parent.rotationPath.getRelativePosition(parent.currentRenderMillis(0)).copy()));
//                }
//
//                return particle;
//            };
//
//
//            particleFactory2 = args ->
//            {
//                PathedParticle particle = particleFactory.create(null);
//                particle.setMaxAge(1000);
//
//                //Spawn randomly in ring around player at half player height
//                EntityLivingBase livingBase = (EntityLivingBase) args[1];
//                particle.applyPath(new CPathConstant(new VectorN(livingBase.posX, livingBase.posY, livingBase.posZ).add(new VectorN(livingBase.width, livingBase.height, livingBase.width).multiply(v3_1_05_0).rotate(v3_0_1_0, Tools.random(Math.PI * 2)))));
//
//                //Fall at a certain speed
//                particle.applyPath(pathFalling);
//
//                //Rotate at a certain speed
//                particle.rotationPath(pathRotation);
//
//                //Die when touching ground, and spawn a fading copy of self that holds still
//                particle.dieOnSolidsAndLiquids();
//                particle.addOnDeathParticles(particleFactory);
//                return particle;
//            };
//        }
//
//        if (!Minecraft.getMinecraft().isGamePaused()) particleFactory2.create(null, entityLivingBase);
//    }
}
