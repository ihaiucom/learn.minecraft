package net.minecraft.client.network.play;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BeeAngrySound;
import net.minecraft.client.audio.BeeFlightSound;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MinecartTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.CommandBlockScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadTerrainScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.EntityAIDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateDebugRenderer;
import net.minecraft.client.renderer.debug.PointOfInterestDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptsDebugRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.IMutableSearchTree;
import net.minecraft.client.util.NBTQueryManager;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.dispenser.Position;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.EyeOfEnderEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.item.minecart.HopperMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartCommandBlockEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.item.minecart.SpawnerMinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SCooldownPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.network.play.server.SSpawnGlobalEntityPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.network.play.server.SStatisticsPacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientPlayNetHandler implements IClientPlayNetHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private final NetworkManager netManager;
   private final GameProfile profile;
   private final Screen guiScreenServer;
   private Minecraft client;
   private ClientWorld world;
   private boolean doneLoadingTerrain;
   private final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
   private final ClientAdvancementManager advancementManager;
   private final ClientSuggestionProvider clientSuggestionProvider;
   private NetworkTagManager networkTagManager = new NetworkTagManager();
   private final NBTQueryManager nbtQueryManager = new NBTQueryManager(this);
   private int field_217287_m = 3;
   private final Random avRandomizer = new Random();
   private CommandDispatcher<ISuggestionProvider> commandDispatcher = new CommandDispatcher();
   private final RecipeManager recipeManager = new RecipeManager();
   private final UUID field_217289_q = UUID.randomUUID();

   public ClientPlayNetHandler(Minecraft p_i46300_1_, Screen p_i46300_2_, NetworkManager p_i46300_3_, GameProfile p_i46300_4_) {
      this.client = p_i46300_1_;
      this.guiScreenServer = p_i46300_2_;
      this.netManager = p_i46300_3_;
      this.profile = p_i46300_4_;
      this.advancementManager = new ClientAdvancementManager(p_i46300_1_);
      this.clientSuggestionProvider = new ClientSuggestionProvider(this, p_i46300_1_);
   }

   public ClientSuggestionProvider getSuggestionProvider() {
      return this.clientSuggestionProvider;
   }

   public void cleanup() {
      this.world = null;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public void handleJoinGame(SJoinGamePacket p_147282_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147282_1_, this, (ThreadTaskExecutor)this.client);
      this.client.playerController = new PlayerController(this.client, this);
      this.field_217287_m = p_147282_1_.func_218728_h();
      this.world = new ClientWorld(this, new WorldSettings(p_147282_1_.func_229742_c_(), p_147282_1_.getGameType(), false, p_147282_1_.isHardcoreMode(), p_147282_1_.getWorldType()), p_147282_1_.getDimension(), this.field_217287_m, this.client.getProfiler(), this.client.worldRenderer);
      this.client.loadWorld(this.world);
      if (this.client.player == null) {
         this.client.player = this.client.playerController.createPlayer(this.world, new StatisticsManager(), new ClientRecipeBook(this.world.getRecipeManager()));
         this.client.player.rotationYaw = -180.0F;
         if (this.client.getIntegratedServer() != null) {
            this.client.getIntegratedServer().setPlayerUuid(this.client.player.getUniqueID());
         }
      }

      this.client.debugRenderer.func_217737_a();
      this.client.player.preparePlayerToSpawn();
      ClientHooks.firePlayerLogin(this.client.playerController, this.client.player, this.client.getConnection().getNetworkManager());
      int i = p_147282_1_.getPlayerId();
      this.world.addPlayer(i, this.client.player);
      this.client.player.movementInput = new MovementInputFromOptions(this.client.gameSettings);
      this.client.playerController.setPlayerCapabilities(this.client.player);
      this.client.renderViewEntity = this.client.player;
      this.client.player.dimension = p_147282_1_.getDimension();
      this.client.displayGuiScreen(new DownloadTerrainScreen());
      this.client.player.setEntityId(i);
      this.client.player.setReducedDebug(p_147282_1_.isReducedDebugInfo());
      this.client.player.func_228355_a_(p_147282_1_.func_229743_k_());
      this.client.playerController.setGameType(p_147282_1_.getGameType());
      NetworkHooks.sendMCRegistryPackets(this.netManager, "PLAY_TO_SERVER");
      this.client.gameSettings.sendSettingsToServer();
      this.netManager.sendPacket(new CCustomPayloadPacket(CCustomPayloadPacket.BRAND, (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));
      this.client.getMinecraftGame().func_216814_a();
   }

   public void handleSpawnObject(SSpawnObjectPacket p_147235_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147235_1_, this, (ThreadTaskExecutor)this.client);
      double d0 = p_147235_1_.getX();
      double d1 = p_147235_1_.getY();
      double d2 = p_147235_1_.getZ();
      EntityType<?> entitytype = p_147235_1_.getType();
      Object entity;
      if (entitytype == EntityType.CHEST_MINECART) {
         entity = new ChestMinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.FURNACE_MINECART) {
         entity = new FurnaceMinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.TNT_MINECART) {
         entity = new TNTMinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.SPAWNER_MINECART) {
         entity = new SpawnerMinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.HOPPER_MINECART) {
         entity = new HopperMinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.COMMAND_BLOCK_MINECART) {
         entity = new MinecartCommandBlockEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.MINECART) {
         entity = new MinecartEntity(this.world, d0, d1, d2);
      } else {
         Entity entity4;
         if (entitytype == EntityType.FISHING_BOBBER) {
            entity4 = this.world.getEntityByID(p_147235_1_.getData());
            if (entity4 instanceof PlayerEntity) {
               entity = new FishingBobberEntity(this.world, (PlayerEntity)entity4, d0, d1, d2);
            } else {
               entity = null;
            }
         } else if (entitytype == EntityType.ARROW) {
            entity = new ArrowEntity(this.world, d0, d1, d2);
            entity4 = this.world.getEntityByID(p_147235_1_.getData());
            if (entity4 != null) {
               ((AbstractArrowEntity)entity).setShooter(entity4);
            }
         } else if (entitytype == EntityType.SPECTRAL_ARROW) {
            entity = new SpectralArrowEntity(this.world, d0, d1, d2);
            entity4 = this.world.getEntityByID(p_147235_1_.getData());
            if (entity4 != null) {
               ((AbstractArrowEntity)entity).setShooter(entity4);
            }
         } else if (entitytype == EntityType.TRIDENT) {
            entity = new TridentEntity(this.world, d0, d1, d2);
            entity4 = this.world.getEntityByID(p_147235_1_.getData());
            if (entity4 != null) {
               ((AbstractArrowEntity)entity).setShooter(entity4);
            }
         } else if (entitytype == EntityType.SNOWBALL) {
            entity = new SnowballEntity(this.world, d0, d1, d2);
         } else if (entitytype == EntityType.LLAMA_SPIT) {
            entity = new LlamaSpitEntity(this.world, d0, d1, d2, p_147235_1_.func_218693_g(), p_147235_1_.func_218695_h(), p_147235_1_.func_218692_i());
         } else if (entitytype == EntityType.ITEM_FRAME) {
            entity = new ItemFrameEntity(this.world, new BlockPos(d0, d1, d2), Direction.byIndex(p_147235_1_.getData()));
         } else if (entitytype == EntityType.LEASH_KNOT) {
            entity = new LeashKnotEntity(this.world, new BlockPos(d0, d1, d2));
         } else if (entitytype == EntityType.ENDER_PEARL) {
            entity = new EnderPearlEntity(this.world, d0, d1, d2);
         } else if (entitytype == EntityType.EYE_OF_ENDER) {
            entity = new EyeOfEnderEntity(this.world, d0, d1, d2);
         } else if (entitytype == EntityType.FIREWORK_ROCKET) {
            entity = new FireworkRocketEntity(this.world, d0, d1, d2, ItemStack.EMPTY);
         } else if (entitytype == EntityType.FIREBALL) {
            entity = new FireballEntity(this.world, d0, d1, d2, p_147235_1_.func_218693_g(), p_147235_1_.func_218695_h(), p_147235_1_.func_218692_i());
         } else if (entitytype == EntityType.DRAGON_FIREBALL) {
            entity = new DragonFireballEntity(this.world, d0, d1, d2, p_147235_1_.func_218693_g(), p_147235_1_.func_218695_h(), p_147235_1_.func_218692_i());
         } else if (entitytype == EntityType.SMALL_FIREBALL) {
            entity = new SmallFireballEntity(this.world, d0, d1, d2, p_147235_1_.func_218693_g(), p_147235_1_.func_218695_h(), p_147235_1_.func_218692_i());
         } else if (entitytype == EntityType.WITHER_SKULL) {
            entity = new WitherSkullEntity(this.world, d0, d1, d2, p_147235_1_.func_218693_g(), p_147235_1_.func_218695_h(), p_147235_1_.func_218692_i());
         } else if (entitytype == EntityType.SHULKER_BULLET) {
            entity = new ShulkerBulletEntity(this.world, d0, d1, d2, p_147235_1_.func_218693_g(), p_147235_1_.func_218695_h(), p_147235_1_.func_218692_i());
         } else if (entitytype == EntityType.EGG) {
            entity = new EggEntity(this.world, d0, d1, d2);
         } else if (entitytype == EntityType.EVOKER_FANGS) {
            entity = new EvokerFangsEntity(this.world, d0, d1, d2, 0.0F, 0, (LivingEntity)null);
         } else if (entitytype == EntityType.POTION) {
            entity = new PotionEntity(this.world, d0, d1, d2);
         } else if (entitytype == EntityType.EXPERIENCE_BOTTLE) {
            entity = new ExperienceBottleEntity(this.world, d0, d1, d2);
         } else if (entitytype == EntityType.BOAT) {
            entity = new BoatEntity(this.world, d0, d1, d2);
         } else if (entitytype == EntityType.TNT) {
            entity = new TNTEntity(this.world, d0, d1, d2, (LivingEntity)null);
         } else if (entitytype == EntityType.ARMOR_STAND) {
            entity = new ArmorStandEntity(this.world, d0, d1, d2);
         } else if (entitytype == EntityType.END_CRYSTAL) {
            entity = new EnderCrystalEntity(this.world, d0, d1, d2);
         } else if (entitytype == EntityType.ITEM) {
            entity = new ItemEntity(this.world, d0, d1, d2);
         } else if (entitytype == EntityType.FALLING_BLOCK) {
            entity = new FallingBlockEntity(this.world, d0, d1, d2, Block.getStateById(p_147235_1_.getData()));
         } else if (entitytype == EntityType.AREA_EFFECT_CLOUD) {
            entity = new AreaEffectCloudEntity(this.world, d0, d1, d2);
         } else {
            entity = null;
         }
      }

      if (entity != null) {
         int i = p_147235_1_.getEntityID();
         ((Entity)entity).func_213312_b(d0, d1, d2);
         ((Entity)entity).rotationPitch = (float)(p_147235_1_.getPitch() * 360) / 256.0F;
         ((Entity)entity).rotationYaw = (float)(p_147235_1_.getYaw() * 360) / 256.0F;
         ((Entity)entity).setEntityId(i);
         ((Entity)entity).setUniqueId(p_147235_1_.getUniqueId());
         this.world.addEntity(i, (Entity)entity);
         if (entity instanceof AbstractMinecartEntity) {
            this.client.getSoundHandler().play(new MinecartTickableSound((AbstractMinecartEntity)entity));
         }
      }

   }

   public void handleSpawnExperienceOrb(SSpawnExperienceOrbPacket p_147286_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147286_1_, this, (ThreadTaskExecutor)this.client);
      double d0 = p_147286_1_.getX();
      double d1 = p_147286_1_.getY();
      double d2 = p_147286_1_.getZ();
      Entity entity = new ExperienceOrbEntity(this.world, d0, d1, d2, p_147286_1_.getXPValue());
      entity.func_213312_b(d0, d1, d2);
      entity.rotationYaw = 0.0F;
      entity.rotationPitch = 0.0F;
      entity.setEntityId(p_147286_1_.getEntityID());
      this.world.addEntity(p_147286_1_.getEntityID(), entity);
   }

   public void handleSpawnGlobalEntity(SSpawnGlobalEntityPacket p_147292_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147292_1_, this, (ThreadTaskExecutor)this.client);
      double d0 = p_147292_1_.getX();
      double d1 = p_147292_1_.getY();
      double d2 = p_147292_1_.getZ();
      if (p_147292_1_.getType() == 1) {
         LightningBoltEntity lightningboltentity = new LightningBoltEntity(this.world, d0, d1, d2, false);
         lightningboltentity.func_213312_b(d0, d1, d2);
         lightningboltentity.rotationYaw = 0.0F;
         lightningboltentity.rotationPitch = 0.0F;
         lightningboltentity.setEntityId(p_147292_1_.getEntityId());
         this.world.addLightning(lightningboltentity);
      }

   }

   public void handleSpawnPainting(SSpawnPaintingPacket p_147288_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147288_1_, this, (ThreadTaskExecutor)this.client);
      PaintingEntity paintingentity = new PaintingEntity(this.world, p_147288_1_.getPosition(), p_147288_1_.getFacing(), p_147288_1_.getType());
      paintingentity.setEntityId(p_147288_1_.getEntityID());
      paintingentity.setUniqueId(p_147288_1_.getUniqueId());
      this.world.addEntity(p_147288_1_.getEntityID(), paintingentity);
   }

   public void handleEntityVelocity(SEntityVelocityPacket p_147244_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147244_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_147244_1_.getEntityID());
      if (entity != null) {
         entity.setVelocity((double)p_147244_1_.getMotionX() / 8000.0D, (double)p_147244_1_.getMotionY() / 8000.0D, (double)p_147244_1_.getMotionZ() / 8000.0D);
      }

   }

   public void handleEntityMetadata(SEntityMetadataPacket p_147284_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147284_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_147284_1_.getEntityId());
      if (entity != null && p_147284_1_.getDataManagerEntries() != null) {
         entity.getDataManager().setEntryValues(p_147284_1_.getDataManagerEntries());
      }

   }

   public void handleSpawnPlayer(SSpawnPlayerPacket p_147237_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147237_1_, this, (ThreadTaskExecutor)this.client);
      double d0 = p_147237_1_.getX();
      double d1 = p_147237_1_.getY();
      double d2 = p_147237_1_.getZ();
      float f = (float)(p_147237_1_.getYaw() * 360) / 256.0F;
      float f1 = (float)(p_147237_1_.getPitch() * 360) / 256.0F;
      int i = p_147237_1_.getEntityID();
      RemoteClientPlayerEntity remoteclientplayerentity = new RemoteClientPlayerEntity(this.client.world, this.getPlayerInfo(p_147237_1_.getUniqueId()).getGameProfile());
      remoteclientplayerentity.setEntityId(i);
      remoteclientplayerentity.func_226286_f_(d0, d1, d2);
      remoteclientplayerentity.func_213312_b(d0, d1, d2);
      remoteclientplayerentity.setPositionAndRotation(d0, d1, d2, f, f1);
      this.world.addPlayer(i, remoteclientplayerentity);
   }

   public void handleEntityTeleport(SEntityTeleportPacket p_147275_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147275_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_147275_1_.getEntityId());
      if (entity != null) {
         double d0 = p_147275_1_.getX();
         double d1 = p_147275_1_.getY();
         double d2 = p_147275_1_.getZ();
         entity.func_213312_b(d0, d1, d2);
         if (!entity.canPassengerSteer()) {
            float f = (float)(p_147275_1_.getYaw() * 360) / 256.0F;
            float f1 = (float)(p_147275_1_.getPitch() * 360) / 256.0F;
            if (Math.abs(entity.func_226277_ct_() - d0) < 0.03125D && Math.abs(entity.func_226278_cu_() - d1) < 0.015625D && Math.abs(entity.func_226281_cx_() - d2) < 0.03125D) {
               entity.setPositionAndRotationDirect(entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), f, f1, 3, true);
            } else {
               entity.setPositionAndRotationDirect(d0, d1, d2, f, f1, 3, true);
            }

            entity.onGround = p_147275_1_.isOnGround();
         }
      }

   }

   public void handleHeldItemChange(SHeldItemChangePacket p_147257_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147257_1_, this, (ThreadTaskExecutor)this.client);
      if (PlayerInventory.isHotbar(p_147257_1_.getHeldItemHotbarIndex())) {
         this.client.player.inventory.currentItem = p_147257_1_.getHeldItemHotbarIndex();
      }

   }

   public void handleEntityMovement(SEntityPacket p_147259_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147259_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = p_147259_1_.getEntity(this.world);
      if (entity != null && !entity.canPassengerSteer()) {
         float f3;
         if (p_147259_1_.func_229745_h_()) {
            entity.serverPosX += (long)p_147259_1_.getX();
            entity.serverPosY += (long)p_147259_1_.getY();
            entity.serverPosZ += (long)p_147259_1_.getZ();
            Vec3d vec3d = SEntityPacket.func_218744_a(entity.serverPosX, entity.serverPosY, entity.serverPosZ);
            f3 = p_147259_1_.isRotating() ? (float)(p_147259_1_.getYaw() * 360) / 256.0F : entity.rotationYaw;
            float f1 = p_147259_1_.isRotating() ? (float)(p_147259_1_.getPitch() * 360) / 256.0F : entity.rotationPitch;
            entity.setPositionAndRotationDirect(vec3d.x, vec3d.y, vec3d.z, f3, f1, 3, false);
         } else if (p_147259_1_.isRotating()) {
            float f2 = (float)(p_147259_1_.getYaw() * 360) / 256.0F;
            f3 = (float)(p_147259_1_.getPitch() * 360) / 256.0F;
            entity.setPositionAndRotationDirect(entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), f2, f3, 3, false);
         }

         entity.onGround = p_147259_1_.getOnGround();
      }

   }

   public void handleEntityHeadLook(SEntityHeadLookPacket p_147267_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147267_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = p_147267_1_.getEntity(this.world);
      if (entity != null) {
         float f = (float)(p_147267_1_.getYaw() * 360) / 256.0F;
         entity.setHeadRotation(f, 3);
      }

   }

   public void handleDestroyEntities(SDestroyEntitiesPacket p_147238_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147238_1_, this, (ThreadTaskExecutor)this.client);

      for(int i = 0; i < p_147238_1_.getEntityIDs().length; ++i) {
         int j = p_147238_1_.getEntityIDs()[i];
         this.world.removeEntityFromWorld(j);
      }

   }

   public void handlePlayerPosLook(SPlayerPositionLookPacket p_184330_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184330_1_, this, (ThreadTaskExecutor)this.client);
      PlayerEntity playerentity = this.client.player;
      Vec3d vec3d = playerentity.getMotion();
      boolean flag = p_184330_1_.getFlags().contains(SPlayerPositionLookPacket.Flags.X);
      boolean flag1 = p_184330_1_.getFlags().contains(SPlayerPositionLookPacket.Flags.Y);
      boolean flag2 = p_184330_1_.getFlags().contains(SPlayerPositionLookPacket.Flags.Z);
      double d0;
      double d1;
      if (flag) {
         d0 = vec3d.getX();
         d1 = playerentity.func_226277_ct_() + p_184330_1_.getX();
         playerentity.lastTickPosX += p_184330_1_.getX();
      } else {
         d0 = 0.0D;
         d1 = p_184330_1_.getX();
         playerentity.lastTickPosX = d1;
      }

      double d2;
      double d3;
      if (flag1) {
         d2 = vec3d.getY();
         d3 = playerentity.func_226278_cu_() + p_184330_1_.getY();
         playerentity.lastTickPosY += p_184330_1_.getY();
      } else {
         d2 = 0.0D;
         d3 = p_184330_1_.getY();
         playerentity.lastTickPosY = d3;
      }

      double d4;
      double d5;
      if (flag2) {
         d4 = vec3d.getZ();
         d5 = playerentity.func_226281_cx_() + p_184330_1_.getZ();
         playerentity.lastTickPosZ += p_184330_1_.getZ();
      } else {
         d4 = 0.0D;
         d5 = p_184330_1_.getZ();
         playerentity.lastTickPosZ = d5;
      }

      playerentity.func_226288_n_(d1, d3, d5);
      playerentity.prevPosX = d1;
      playerentity.prevPosY = d3;
      playerentity.prevPosZ = d5;
      playerentity.setMotion(d0, d2, d4);
      float f = p_184330_1_.getYaw();
      float f1 = p_184330_1_.getPitch();
      if (p_184330_1_.getFlags().contains(SPlayerPositionLookPacket.Flags.X_ROT)) {
         f1 += playerentity.rotationPitch;
      }

      if (p_184330_1_.getFlags().contains(SPlayerPositionLookPacket.Flags.Y_ROT)) {
         f += playerentity.rotationYaw;
      }

      playerentity.setPositionAndRotation(d1, d3, d5, f, f1);
      this.netManager.sendPacket(new CConfirmTeleportPacket(p_184330_1_.getTeleportId()));
      this.netManager.sendPacket(new CPlayerPacket.PositionRotationPacket(playerentity.func_226277_ct_(), playerentity.func_226278_cu_(), playerentity.func_226281_cx_(), playerentity.rotationYaw, playerentity.rotationPitch, false));
      if (!this.doneLoadingTerrain) {
         this.doneLoadingTerrain = true;
         this.client.displayGuiScreen((Screen)null);
      }

   }

   public void handleMultiBlockChange(SMultiBlockChangePacket p_147287_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147287_1_, this, (ThreadTaskExecutor)this.client);
      SMultiBlockChangePacket.UpdateData[] var2 = p_147287_1_.getChangedBlocks();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SMultiBlockChangePacket.UpdateData smultiblockchangepacket$updatedata = var2[var4];
         this.world.invalidateRegionAndSetBlock(smultiblockchangepacket$updatedata.getPos(), smultiblockchangepacket$updatedata.getBlockState());
      }

   }

   public void handleChunkData(SChunkDataPacket p_147263_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147263_1_, this, (ThreadTaskExecutor)this.client);
      int i = p_147263_1_.getChunkX();
      int j = p_147263_1_.getChunkZ();
      Chunk chunk = this.world.getChunkProvider().func_228313_a_(i, j, p_147263_1_.func_229739_i_(), p_147263_1_.getReadBuffer(), p_147263_1_.getHeightmapTags(), p_147263_1_.getAvailableSections());
      if (chunk != null && p_147263_1_.isFullChunk()) {
         this.world.addEntitiesToChunk(chunk);
      }

      for(int k = 0; k < 16; ++k) {
         this.world.markSurroundingsForRerender(i, k, j);
      }

      Iterator var9 = p_147263_1_.getTileEntityTags().iterator();

      while(var9.hasNext()) {
         CompoundNBT compoundnbt = (CompoundNBT)var9.next();
         BlockPos blockpos = new BlockPos(compoundnbt.getInt("x"), compoundnbt.getInt("y"), compoundnbt.getInt("z"));
         TileEntity tileentity = this.world.getTileEntity(blockpos);
         if (tileentity != null) {
            tileentity.handleUpdateTag(compoundnbt);
         }
      }

   }

   public void processChunkUnload(SUnloadChunkPacket p_184326_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184326_1_, this, (ThreadTaskExecutor)this.client);
      int i = p_184326_1_.getX();
      int j = p_184326_1_.getZ();
      ClientChunkProvider clientchunkprovider = this.world.getChunkProvider();
      clientchunkprovider.unloadChunk(i, j);
      WorldLightManager worldlightmanager = clientchunkprovider.getLightManager();

      for(int k = 0; k < 16; ++k) {
         this.world.markSurroundingsForRerender(i, k, j);
         worldlightmanager.updateSectionStatus(SectionPos.of(i, k, j), true);
      }

      worldlightmanager.func_215571_a(new ChunkPos(i, j), false);
   }

   public void handleBlockChange(SChangeBlockPacket p_147234_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147234_1_, this, (ThreadTaskExecutor)this.client);
      this.world.invalidateRegionAndSetBlock(p_147234_1_.getPos(), p_147234_1_.getState());
   }

   public void handleDisconnect(SDisconnectPacket p_147253_1_) {
      this.netManager.closeChannel(p_147253_1_.getReason());
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
      this.client.func_213254_o();
      if (this.guiScreenServer != null) {
         if (this.guiScreenServer instanceof RealmsScreenProxy) {
            this.client.displayGuiScreen((new DisconnectedRealmsScreen(((RealmsScreenProxy)this.guiScreenServer).getScreen(), "disconnect.lost", p_147231_1_)).getProxy());
         } else {
            this.client.displayGuiScreen(new DisconnectedScreen(this.guiScreenServer, "disconnect.lost", p_147231_1_));
         }
      } else {
         this.client.displayGuiScreen(new DisconnectedScreen(new MultiplayerScreen(new MainMenuScreen()), "disconnect.lost", p_147231_1_));
      }

   }

   public void sendPacket(IPacket<?> p_147297_1_) {
      this.netManager.sendPacket(p_147297_1_);
   }

   public void handleCollectItem(SCollectItemPacket p_147246_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147246_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_147246_1_.getCollectedItemEntityID());
      LivingEntity livingentity = (LivingEntity)this.world.getEntityByID(p_147246_1_.getEntityID());
      if (livingentity == null) {
         livingentity = this.client.player;
      }

      if (entity != null) {
         if (entity instanceof ExperienceOrbEntity) {
            this.world.playSound(entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.35F + 0.9F, false);
         } else {
            this.world.playSound(entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 1.4F + 2.0F, false);
         }

         if (entity instanceof ItemEntity) {
            ((ItemEntity)entity).getItem().setCount(p_147246_1_.getAmount());
         }

         this.client.particles.addEffect(new ItemPickupParticle(this.client.getRenderManager(), this.client.func_228019_au_(), this.world, entity, (Entity)livingentity));
         this.world.removeEntityFromWorld(p_147246_1_.getCollectedItemEntityID());
      }

   }

   public void handleChat(SChatPacket p_147251_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147251_1_, this, (ThreadTaskExecutor)this.client);
      ITextComponent message = ForgeEventFactory.onClientChat(p_147251_1_.getType(), p_147251_1_.getChatComponent());
      if (message != null) {
         this.client.ingameGUI.addChatMessage(p_147251_1_.getType(), message);
      }
   }

   public void handleAnimation(SAnimateHandPacket p_147279_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147279_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_147279_1_.getEntityID());
      if (entity != null) {
         LivingEntity livingentity1;
         if (p_147279_1_.getAnimationType() == 0) {
            livingentity1 = (LivingEntity)entity;
            livingentity1.swingArm(Hand.MAIN_HAND);
         } else if (p_147279_1_.getAnimationType() == 3) {
            livingentity1 = (LivingEntity)entity;
            livingentity1.swingArm(Hand.OFF_HAND);
         } else if (p_147279_1_.getAnimationType() == 1) {
            entity.performHurtAnimation();
         } else if (p_147279_1_.getAnimationType() == 2) {
            PlayerEntity playerentity = (PlayerEntity)entity;
            playerentity.func_225652_a_(false, false);
         } else if (p_147279_1_.getAnimationType() == 4) {
            this.client.particles.addParticleEmitter(entity, ParticleTypes.CRIT);
         } else if (p_147279_1_.getAnimationType() == 5) {
            this.client.particles.addParticleEmitter(entity, ParticleTypes.ENCHANTED_HIT);
         }
      }

   }

   public void handleSpawnMob(SSpawnMobPacket p_147281_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147281_1_, this, (ThreadTaskExecutor)this.client);
      double d0 = p_147281_1_.getX();
      double d1 = p_147281_1_.getY();
      double d2 = p_147281_1_.getZ();
      float f = (float)(p_147281_1_.getYaw() * 360) / 256.0F;
      float f1 = (float)(p_147281_1_.getPitch() * 360) / 256.0F;
      LivingEntity livingentity = (LivingEntity)EntityType.create(p_147281_1_.getEntityType(), this.client.world);
      if (livingentity != null) {
         livingentity.func_213312_b(d0, d1, d2);
         livingentity.renderYawOffset = (float)(p_147281_1_.getHeadPitch() * 360) / 256.0F;
         livingentity.rotationYawHead = (float)(p_147281_1_.getHeadPitch() * 360) / 256.0F;
         if (livingentity instanceof EnderDragonEntity) {
            EnderDragonPartEntity[] aenderdragonpartentity = ((EnderDragonEntity)livingentity).func_213404_dT();

            for(int i = 0; i < aenderdragonpartentity.length; ++i) {
               aenderdragonpartentity[i].setEntityId(i + p_147281_1_.getEntityID());
            }
         }

         livingentity.setEntityId(p_147281_1_.getEntityID());
         livingentity.setUniqueId(p_147281_1_.getUniqueId());
         livingentity.setPositionAndRotation(d0, d1, d2, f, f1);
         livingentity.setMotion((double)((float)p_147281_1_.getVelocityX() / 8000.0F), (double)((float)p_147281_1_.getVelocityY() / 8000.0F), (double)((float)p_147281_1_.getVelocityZ() / 8000.0F));
         this.world.addEntity(p_147281_1_.getEntityID(), livingentity);
         if (livingentity instanceof BeeEntity) {
            boolean flag = ((BeeEntity)livingentity).func_226427_ez_();
            Object beesound;
            if (flag) {
               beesound = new BeeAngrySound((BeeEntity)livingentity);
            } else {
               beesound = new BeeFlightSound((BeeEntity)livingentity);
            }

            this.client.getSoundHandler().play((ISound)beesound);
         }
      } else {
         LOGGER.warn("Skipping Entity with id {}", p_147281_1_.getEntityType());
      }

   }

   public void handleTimeUpdate(SUpdateTimePacket p_147285_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147285_1_, this, (ThreadTaskExecutor)this.client);
      this.client.world.setGameTime(p_147285_1_.getTotalWorldTime());
      this.client.world.setDayTime(p_147285_1_.getWorldTime());
   }

   public void handleSpawnPosition(SSpawnPositionPacket p_147271_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147271_1_, this, (ThreadTaskExecutor)this.client);
      this.client.player.func_226560_a_(p_147271_1_.getSpawnPos(), true, false);
      this.client.world.getWorldInfo().setSpawn(p_147271_1_.getSpawnPos());
   }

   public void handleSetPassengers(SSetPassengersPacket p_184328_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184328_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_184328_1_.getEntityId());
      if (entity == null) {
         LOGGER.warn("Received passengers for unknown entity");
      } else {
         boolean flag = entity.isRidingOrBeingRiddenBy(this.client.player);
         entity.removePassengers();
         int[] var4 = p_184328_1_.getPassengerIds();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            int i = var4[var6];
            Entity entity1 = this.world.getEntityByID(i);
            if (entity1 != null) {
               entity1.startRiding(entity, true);
               if (entity1 == this.client.player && !flag) {
                  this.client.ingameGUI.setOverlayMessage(I18n.format("mount.onboard", this.client.gameSettings.field_228046_af_.getLocalizedName()), false);
               }
            }
         }
      }

   }

   public void handleEntityAttach(SMountEntityPacket p_147243_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147243_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_147243_1_.getEntityId());
      if (entity instanceof MobEntity) {
         ((MobEntity)entity).func_213381_d(p_147243_1_.getVehicleEntityId());
      }

   }

   private static ItemStack func_217282_a(PlayerEntity p_217282_0_) {
      Hand[] var1 = Hand.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Hand hand = var1[var3];
         ItemStack itemstack = p_217282_0_.getHeldItem(hand);
         if (itemstack.getItem() == Items.TOTEM_OF_UNDYING) {
            return itemstack;
         }
      }

      return new ItemStack(Items.TOTEM_OF_UNDYING);
   }

   public void handleEntityStatus(SEntityStatusPacket p_147236_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147236_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = p_147236_1_.getEntity(this.world);
      if (entity != null) {
         if (p_147236_1_.getOpCode() == 21) {
            this.client.getSoundHandler().play(new GuardianSound((GuardianEntity)entity));
         } else if (p_147236_1_.getOpCode() == 35) {
            int i = true;
            this.client.particles.emitParticleAtEntity(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
            this.world.playSound(entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
            if (entity == this.client.player) {
               this.client.gameRenderer.displayItemActivation(func_217282_a(this.client.player));
            }
         } else {
            entity.handleStatusUpdate(p_147236_1_.getOpCode());
         }
      }

   }

   public void handleUpdateHealth(SUpdateHealthPacket p_147249_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147249_1_, this, (ThreadTaskExecutor)this.client);
      this.client.player.setPlayerSPHealth(p_147249_1_.getHealth());
      this.client.player.getFoodStats().setFoodLevel(p_147249_1_.getFoodLevel());
      this.client.player.getFoodStats().setFoodSaturationLevel(p_147249_1_.getSaturationLevel());
   }

   public void handleSetExperience(SSetExperiencePacket p_147295_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147295_1_, this, (ThreadTaskExecutor)this.client);
      this.client.player.setXPStats(p_147295_1_.getExperienceBar(), p_147295_1_.getTotalExperience(), p_147295_1_.getLevel());
   }

   public void handleRespawn(SRespawnPacket p_147280_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147280_1_, this, (ThreadTaskExecutor)this.client);
      DimensionType dimensiontype = p_147280_1_.getDimension();
      ClientPlayerEntity clientplayerentity = this.client.player;
      int i = clientplayerentity.getEntityId();
      if (dimensiontype != clientplayerentity.dimension) {
         this.doneLoadingTerrain = false;
         Scoreboard scoreboard = this.world.getScoreboard();
         this.world = new ClientWorld(this, new WorldSettings(p_147280_1_.func_229747_c_(), p_147280_1_.getGameType(), false, this.client.world.getWorldInfo().isHardcore(), p_147280_1_.getWorldType()), p_147280_1_.getDimension(), this.field_217287_m, this.client.getProfiler(), this.client.worldRenderer);
         this.world.setScoreboard(scoreboard);
         this.client.loadWorld(this.world);
         this.client.displayGuiScreen(new DownloadTerrainScreen());
      }

      this.world.setInitialSpawnLocation();
      this.world.removeAllEntities();
      String s = clientplayerentity.getServerBrand();
      this.client.renderViewEntity = null;
      ClientPlayerEntity clientplayerentity1 = this.client.playerController.createPlayer(this.world, clientplayerentity.getStats(), clientplayerentity.getRecipeBook());
      clientplayerentity1.setEntityId(i);
      clientplayerentity1.dimension = dimensiontype;
      this.client.player = clientplayerentity1;
      this.client.renderViewEntity = clientplayerentity1;
      clientplayerentity1.getDataManager().setEntryValues(clientplayerentity.getDataManager().getAll());
      clientplayerentity1.getAttributes().func_226303_a_(clientplayerentity.getAttributes());
      clientplayerentity1.updateSyncFields(clientplayerentity);
      clientplayerentity1.preparePlayerToSpawn();
      clientplayerentity1.setServerBrand(s);
      ClientHooks.firePlayerRespawn(this.client.playerController, clientplayerentity, clientplayerentity1, clientplayerentity1.connection.getNetworkManager());
      this.world.addPlayer(i, clientplayerentity1);
      clientplayerentity1.rotationYaw = -180.0F;
      clientplayerentity1.movementInput = new MovementInputFromOptions(this.client.gameSettings);
      this.client.playerController.setPlayerCapabilities(clientplayerentity1);
      clientplayerentity1.setReducedDebug(clientplayerentity.hasReducedDebug());
      clientplayerentity1.func_228355_a_(clientplayerentity.func_228353_F_());
      if (this.client.currentScreen instanceof DeathScreen) {
         this.client.displayGuiScreen((Screen)null);
      }

      this.client.playerController.setGameType(p_147280_1_.getGameType());
   }

   public void handleExplosion(SExplosionPacket p_147283_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147283_1_, this, (ThreadTaskExecutor)this.client);
      Explosion explosion = new Explosion(this.client.world, (Entity)null, p_147283_1_.getX(), p_147283_1_.getY(), p_147283_1_.getZ(), p_147283_1_.getStrength(), p_147283_1_.getAffectedBlockPositions());
      explosion.doExplosionB(true);
      this.client.player.setMotion(this.client.player.getMotion().add((double)p_147283_1_.getMotionX(), (double)p_147283_1_.getMotionY(), (double)p_147283_1_.getMotionZ()));
   }

   public void func_217271_a(SOpenHorseWindowPacket p_217271_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217271_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_217271_1_.func_218703_d());
      if (entity instanceof AbstractHorseEntity) {
         ClientPlayerEntity clientplayerentity = this.client.player;
         AbstractHorseEntity abstracthorseentity = (AbstractHorseEntity)entity;
         Inventory inventory = new Inventory(p_217271_1_.func_218702_c());
         HorseInventoryContainer horseinventorycontainer = new HorseInventoryContainer(p_217271_1_.func_218704_b(), clientplayerentity.inventory, inventory, abstracthorseentity);
         clientplayerentity.openContainer = horseinventorycontainer;
         this.client.displayGuiScreen(new HorseInventoryScreen(horseinventorycontainer, clientplayerentity.inventory, abstracthorseentity));
      }

   }

   public void func_217272_a(SOpenWindowPacket p_217272_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217272_1_, this, (ThreadTaskExecutor)this.client);
      ScreenManager.openScreen(p_217272_1_.getContainerType(), this.client, p_217272_1_.getWindowId(), p_217272_1_.getTitle());
   }

   public void handleSetSlot(SSetSlotPacket p_147266_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147266_1_, this, (ThreadTaskExecutor)this.client);
      PlayerEntity playerentity = this.client.player;
      ItemStack itemstack = p_147266_1_.getStack();
      int i = p_147266_1_.getSlot();
      this.client.getTutorial().handleSetSlot(itemstack);
      if (p_147266_1_.getWindowId() == -1) {
         if (!(this.client.currentScreen instanceof CreativeScreen)) {
            playerentity.inventory.setItemStack(itemstack);
         }
      } else if (p_147266_1_.getWindowId() == -2) {
         playerentity.inventory.setInventorySlotContents(i, itemstack);
      } else {
         boolean flag = false;
         if (this.client.currentScreen instanceof CreativeScreen) {
            CreativeScreen creativescreen = (CreativeScreen)this.client.currentScreen;
            flag = creativescreen.getSelectedTabIndex() != ItemGroup.INVENTORY.getIndex();
         }

         if (p_147266_1_.getWindowId() == 0 && p_147266_1_.getSlot() >= 36 && i < 45) {
            if (!itemstack.isEmpty()) {
               ItemStack itemstack1 = playerentity.container.getSlot(i).getStack();
               if (itemstack1.isEmpty() || itemstack1.getCount() < itemstack.getCount()) {
                  itemstack.setAnimationsToGo(5);
               }
            }

            playerentity.container.putStackInSlot(i, itemstack);
         } else if (p_147266_1_.getWindowId() == playerentity.openContainer.windowId && (p_147266_1_.getWindowId() != 0 || !flag)) {
            playerentity.openContainer.putStackInSlot(i, itemstack);
         }
      }

   }

   public void handleConfirmTransaction(SConfirmTransactionPacket p_147239_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147239_1_, this, (ThreadTaskExecutor)this.client);
      Container container = null;
      PlayerEntity playerentity = this.client.player;
      if (p_147239_1_.getWindowId() == 0) {
         container = playerentity.container;
      } else if (p_147239_1_.getWindowId() == playerentity.openContainer.windowId) {
         container = playerentity.openContainer;
      }

      if (container != null && !p_147239_1_.wasAccepted()) {
         this.sendPacket(new CConfirmTransactionPacket(p_147239_1_.getWindowId(), p_147239_1_.getActionNumber(), true));
      }

   }

   public void handleWindowItems(SWindowItemsPacket p_147241_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147241_1_, this, (ThreadTaskExecutor)this.client);
      PlayerEntity playerentity = this.client.player;
      if (p_147241_1_.getWindowId() == 0) {
         playerentity.container.setAll(p_147241_1_.getItemStacks());
      } else if (p_147241_1_.getWindowId() == playerentity.openContainer.windowId) {
         playerentity.openContainer.setAll(p_147241_1_.getItemStacks());
      }

   }

   public void handleSignEditorOpen(SOpenSignMenuPacket p_147268_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147268_1_, this, (ThreadTaskExecutor)this.client);
      TileEntity tileentity = this.world.getTileEntity(p_147268_1_.getSignPosition());
      if (!(tileentity instanceof SignTileEntity)) {
         tileentity = new SignTileEntity();
         ((TileEntity)tileentity).func_226984_a_(this.world, p_147268_1_.getSignPosition());
      }

      this.client.player.openSignEditor((SignTileEntity)tileentity);
   }

   public void handleUpdateTileEntity(SUpdateTileEntityPacket p_147273_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147273_1_, this, (ThreadTaskExecutor)this.client);
      if (this.client.world.isBlockLoaded(p_147273_1_.getPos())) {
         TileEntity tileentity = this.client.world.getTileEntity(p_147273_1_.getPos());
         int i = p_147273_1_.getTileEntityType();
         boolean flag = i == 2 && tileentity instanceof CommandBlockTileEntity;
         if (i == 1 && tileentity instanceof MobSpawnerTileEntity || flag || i == 3 && tileentity instanceof BeaconTileEntity || i == 4 && tileentity instanceof SkullTileEntity || i == 6 && tileentity instanceof BannerTileEntity || i == 7 && tileentity instanceof StructureBlockTileEntity || i == 8 && tileentity instanceof EndGatewayTileEntity || i == 9 && tileentity instanceof SignTileEntity || i == 11 && tileentity instanceof BedTileEntity || i == 5 && tileentity instanceof ConduitTileEntity || i == 12 && tileentity instanceof JigsawTileEntity || i == 13 && tileentity instanceof CampfireTileEntity || i == 14 && tileentity instanceof BeehiveTileEntity) {
            tileentity.read(p_147273_1_.getNbtCompound());
         } else {
            if (tileentity == null) {
               LOGGER.error("Received invalid update packet for null tile entity at {} with data: {}", p_147273_1_.getPos(), p_147273_1_.getNbtCompound());
               return;
            }

            tileentity.onDataPacket(this.netManager, p_147273_1_);
         }

         if (flag && this.client.currentScreen instanceof CommandBlockScreen) {
            ((CommandBlockScreen)this.client.currentScreen).updateGui();
         }
      }

   }

   public void handleWindowProperty(SWindowPropertyPacket p_147245_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147245_1_, this, (ThreadTaskExecutor)this.client);
      PlayerEntity playerentity = this.client.player;
      if (playerentity.openContainer != null && playerentity.openContainer.windowId == p_147245_1_.getWindowId()) {
         playerentity.openContainer.updateProgressBar(p_147245_1_.getProperty(), p_147245_1_.getValue());
      }

   }

   public void handleEntityEquipment(SEntityEquipmentPacket p_147242_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147242_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_147242_1_.getEntityID());
      if (entity != null) {
         entity.setItemStackToSlot(p_147242_1_.getEquipmentSlot(), p_147242_1_.getItemStack());
      }

   }

   public void handleCloseWindow(SCloseWindowPacket p_147276_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147276_1_, this, (ThreadTaskExecutor)this.client);
      this.client.player.closeScreenAndDropStack();
   }

   public void handleBlockAction(SBlockActionPacket p_147261_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147261_1_, this, (ThreadTaskExecutor)this.client);
      this.client.world.addBlockEvent(p_147261_1_.getBlockPosition(), p_147261_1_.getBlockType(), p_147261_1_.getData1(), p_147261_1_.getData2());
   }

   public void handleBlockBreakAnim(SAnimateBlockBreakPacket p_147294_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147294_1_, this, (ThreadTaskExecutor)this.client);
      this.client.world.sendBlockBreakProgress(p_147294_1_.getBreakerId(), p_147294_1_.getPosition(), p_147294_1_.getProgress());
   }

   public void handleChangeGameState(SChangeGameStatePacket p_147252_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147252_1_, this, (ThreadTaskExecutor)this.client);
      PlayerEntity playerentity = this.client.player;
      int i = p_147252_1_.getGameState();
      float f = p_147252_1_.getValue();
      int j = MathHelper.floor(f + 0.5F);
      if (i >= 0 && i < SChangeGameStatePacket.MESSAGE_NAMES.length && SChangeGameStatePacket.MESSAGE_NAMES[i] != null) {
         playerentity.sendStatusMessage(new TranslationTextComponent(SChangeGameStatePacket.MESSAGE_NAMES[i], new Object[0]), false);
      }

      if (i == 1) {
         this.world.getWorldInfo().setRaining(true);
         this.world.setRainStrength(0.0F);
      } else if (i == 2) {
         this.world.getWorldInfo().setRaining(false);
         this.world.setRainStrength(1.0F);
      } else if (i == 3) {
         this.client.playerController.setGameType(GameType.getByID(j));
      } else if (i == 4) {
         if (j == 0) {
            this.client.player.connection.sendPacket(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
            this.client.displayGuiScreen(new DownloadTerrainScreen());
         } else if (j == 1) {
            this.client.displayGuiScreen(new WinGameScreen(true, () -> {
               this.client.player.connection.sendPacket(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
            }));
         }
      } else if (i == 5) {
         GameSettings gamesettings = this.client.gameSettings;
         if (f == 0.0F) {
            this.client.displayGuiScreen(new DemoScreen());
         } else if (f == 101.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("demo.help.movement", new Object[]{gamesettings.keyBindForward.getLocalizedName(), gamesettings.keyBindLeft.getLocalizedName(), gamesettings.keyBindBack.getLocalizedName(), gamesettings.keyBindRight.getLocalizedName()}));
         } else if (f == 102.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("demo.help.jump", new Object[]{gamesettings.keyBindJump.getLocalizedName()}));
         } else if (f == 103.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("demo.help.inventory", new Object[]{gamesettings.keyBindInventory.getLocalizedName()}));
         } else if (f == 104.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("demo.day.6", new Object[]{gamesettings.keyBindScreenshot.getLocalizedName()}));
         }
      } else if (i == 6) {
         this.world.playSound(playerentity, playerentity.func_226277_ct_(), playerentity.func_226280_cw_(), playerentity.func_226281_cx_(), SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18F, 0.45F);
      } else if (i == 7) {
         this.world.setRainStrength(f);
      } else if (i == 8) {
         this.world.setThunderStrength(f);
      } else if (i == 9) {
         this.world.playSound(playerentity, playerentity.func_226277_ct_(), playerentity.func_226278_cu_(), playerentity.func_226281_cx_(), SoundEvents.ENTITY_PUFFER_FISH_STING, SoundCategory.NEUTRAL, 1.0F, 1.0F);
      } else if (i == 10) {
         this.world.addParticle(ParticleTypes.ELDER_GUARDIAN, playerentity.func_226277_ct_(), playerentity.func_226278_cu_(), playerentity.func_226281_cx_(), 0.0D, 0.0D, 0.0D);
         this.world.playSound(playerentity, playerentity.func_226277_ct_(), playerentity.func_226278_cu_(), playerentity.func_226281_cx_(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F);
      } else if (i == 11) {
         this.client.player.func_228355_a_(f == 0.0F);
      }

   }

   public void handleMaps(SMapDataPacket p_147264_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147264_1_, this, (ThreadTaskExecutor)this.client);
      MapItemRenderer mapitemrenderer = this.client.gameRenderer.getMapItemRenderer();
      String s = FilledMapItem.func_219993_a(p_147264_1_.getMapId());
      MapData mapdata = this.client.world.func_217406_a(s);
      if (mapdata == null) {
         mapdata = new MapData(s);
         if (mapitemrenderer.getMapInstanceIfExists(s) != null) {
            MapData mapdata1 = mapitemrenderer.getData(mapitemrenderer.getMapInstanceIfExists(s));
            if (mapdata1 != null) {
               mapdata = mapdata1;
            }
         }

         this.client.world.func_217399_a(mapdata);
      }

      p_147264_1_.setMapdataTo(mapdata);
      mapitemrenderer.updateMapTexture(mapdata);
   }

   public void handleEffect(SPlaySoundEventPacket p_147277_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147277_1_, this, (ThreadTaskExecutor)this.client);
      if (p_147277_1_.isSoundServerwide()) {
         this.client.world.playBroadcastSound(p_147277_1_.getSoundType(), p_147277_1_.getSoundPos(), p_147277_1_.getSoundData());
      } else {
         this.client.world.playEvent(p_147277_1_.getSoundType(), p_147277_1_.getSoundPos(), p_147277_1_.getSoundData());
      }

   }

   public void handleAdvancementInfo(SAdvancementInfoPacket p_191981_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_191981_1_, this, (ThreadTaskExecutor)this.client);
      this.advancementManager.read(p_191981_1_);
   }

   public void handleSelectAdvancementsTab(SSelectAdvancementsTabPacket p_194022_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_194022_1_, this, (ThreadTaskExecutor)this.client);
      ResourceLocation resourcelocation = p_194022_1_.getTab();
      if (resourcelocation == null) {
         this.advancementManager.setSelectedTab((Advancement)null, false);
      } else {
         Advancement advancement = this.advancementManager.getAdvancementList().getAdvancement(resourcelocation);
         this.advancementManager.setSelectedTab(advancement, false);
      }

   }

   public void handleCommandList(SCommandListPacket p_195511_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_195511_1_, this, (ThreadTaskExecutor)this.client);
      this.commandDispatcher = new CommandDispatcher(p_195511_1_.getRoot());
   }

   public void handleStopSound(SStopSoundPacket p_195512_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_195512_1_, this, (ThreadTaskExecutor)this.client);
      this.client.getSoundHandler().stop(p_195512_1_.getName(), p_195512_1_.getCategory());
   }

   public void handleTabComplete(STabCompletePacket p_195510_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_195510_1_, this, (ThreadTaskExecutor)this.client);
      this.clientSuggestionProvider.handleResponse(p_195510_1_.getTransactionId(), p_195510_1_.getSuggestions());
   }

   public void handleUpdateRecipes(SUpdateRecipesPacket p_199525_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_199525_1_, this, (ThreadTaskExecutor)this.client);
      this.recipeManager.func_223389_a(p_199525_1_.getRecipes());
      IMutableSearchTree<RecipeList> imutablesearchtree = this.client.func_213253_a(SearchTreeManager.RECIPES);
      imutablesearchtree.func_217871_a();
      ClientRecipeBook clientrecipebook = this.client.player.getRecipeBook();
      clientrecipebook.rebuildTable();
      clientrecipebook.getRecipes().forEach(imutablesearchtree::func_217872_a);
      imutablesearchtree.recalculate();
      ForgeHooksClient.onRecipesUpdated(this.recipeManager);
   }

   public void handlePlayerLook(SPlayerLookPacket p_200232_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_200232_1_, this, (ThreadTaskExecutor)this.client);
      Vec3d vec3d = p_200232_1_.getTargetPosition(this.world);
      if (vec3d != null) {
         this.client.player.lookAt(p_200232_1_.getSourceAnchor(), vec3d);
      }

   }

   public void handleNBTQueryResponse(SQueryNBTResponsePacket p_211522_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_211522_1_, this, (ThreadTaskExecutor)this.client);
      if (!this.nbtQueryManager.handleResponse(p_211522_1_.getTransactionId(), p_211522_1_.getTag())) {
         LOGGER.debug("Got unhandled response to tag query {}", p_211522_1_.getTransactionId());
      }

   }

   public void handleStatistics(SStatisticsPacket p_147293_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147293_1_, this, (ThreadTaskExecutor)this.client);
      Iterator var2 = p_147293_1_.getStatisticMap().entrySet().iterator();

      while(var2.hasNext()) {
         Entry<Stat<?>, Integer> entry = (Entry)var2.next();
         Stat<?> stat = (Stat)entry.getKey();
         int i = (Integer)entry.getValue();
         this.client.player.getStats().setValue(this.client.player, stat, i);
      }

      if (this.client.currentScreen instanceof IProgressMeter) {
         ((IProgressMeter)this.client.currentScreen).onStatsUpdated();
      }

   }

   public void handleRecipeBook(SRecipeBookPacket p_191980_1_) {
      ClientRecipeBook clientrecipebook;
      PacketThreadUtil.checkThreadAndEnqueue(p_191980_1_, this, (ThreadTaskExecutor)this.client);
      clientrecipebook = this.client.player.getRecipeBook();
      clientrecipebook.setGuiOpen(p_191980_1_.isGuiOpen());
      clientrecipebook.setFilteringCraftable(p_191980_1_.isFilteringCraftable());
      clientrecipebook.setFurnaceGuiOpen(p_191980_1_.isFurnaceGuiOpen());
      clientrecipebook.setFurnaceFilteringCraftable(p_191980_1_.isFurnaceFilteringCraftable());
      SRecipeBookPacket.State srecipebookpacket$state = p_191980_1_.getState();
      Iterator var4;
      ResourceLocation resourcelocation2;
      label45:
      switch(srecipebookpacket$state) {
      case REMOVE:
         var4 = p_191980_1_.getRecipes().iterator();

         while(true) {
            if (!var4.hasNext()) {
               break label45;
            }

            resourcelocation2 = (ResourceLocation)var4.next();
            this.recipeManager.getRecipe(resourcelocation2).ifPresent(clientrecipebook::lock);
         }
      case INIT:
         var4 = p_191980_1_.getRecipes().iterator();

         while(var4.hasNext()) {
            resourcelocation2 = (ResourceLocation)var4.next();
            this.recipeManager.getRecipe(resourcelocation2).ifPresent(clientrecipebook::unlock);
         }

         var4 = p_191980_1_.getDisplayedRecipes().iterator();

         while(true) {
            if (!var4.hasNext()) {
               break label45;
            }

            resourcelocation2 = (ResourceLocation)var4.next();
            this.recipeManager.getRecipe(resourcelocation2).ifPresent(clientrecipebook::markNew);
         }
      case ADD:
         var4 = p_191980_1_.getRecipes().iterator();

         while(var4.hasNext()) {
            resourcelocation2 = (ResourceLocation)var4.next();
            this.recipeManager.getRecipe(resourcelocation2).ifPresent((p_lambda$handleRecipeBook$1_2_) -> {
               clientrecipebook.unlock(p_lambda$handleRecipeBook$1_2_);
               clientrecipebook.markNew(p_lambda$handleRecipeBook$1_2_);
               RecipeToast.addOrUpdate(this.client.getToastGui(), p_lambda$handleRecipeBook$1_2_);
            });
         }
      }

      clientrecipebook.getRecipes().forEach((p_lambda$handleRecipeBook$2_1_) -> {
         p_lambda$handleRecipeBook$2_1_.updateKnownRecipes(clientrecipebook);
      });
      if (this.client.currentScreen instanceof IRecipeShownListener) {
         ((IRecipeShownListener)this.client.currentScreen).recipesUpdated();
      }

   }

   public void handleEntityEffect(SPlayEntityEffectPacket p_147260_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147260_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_147260_1_.getEntityId());
      if (entity instanceof LivingEntity) {
         Effect effect = Effect.get(p_147260_1_.getEffectId() & 255);
         if (effect != null) {
            EffectInstance effectinstance = new EffectInstance(effect, p_147260_1_.getDuration(), p_147260_1_.getAmplifier(), p_147260_1_.getIsAmbient(), p_147260_1_.doesShowParticles(), p_147260_1_.shouldShowIcon());
            effectinstance.setPotionDurationMax(p_147260_1_.isMaxDuration());
            ((LivingEntity)entity).addPotionEffect(effectinstance);
         }
      }

   }

   public void handleTags(STagsListPacket p_199723_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_199723_1_, this, (ThreadTaskExecutor)this.client);
      this.networkTagManager = p_199723_1_.getTags();
      if (!this.netManager.isLocalChannel()) {
         BlockTags.setCollection(this.networkTagManager.getBlocks());
         ItemTags.setCollection(this.networkTagManager.getItems());
         FluidTags.setCollection(this.networkTagManager.getFluids());
         EntityTypeTags.setCollection(this.networkTagManager.getEntityTypes());
      }

      this.client.func_213253_a(SearchTreeManager.field_215360_b).recalculate();
      MinecraftForge.EVENT_BUS.post(new TagsUpdatedEvent(this.networkTagManager));
   }

   public void handleCombatEvent(SCombatPacket p_175098_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175098_1_, this, (ThreadTaskExecutor)this.client);
      if (p_175098_1_.eventType == SCombatPacket.Event.ENTITY_DIED) {
         Entity entity = this.world.getEntityByID(p_175098_1_.playerId);
         if (entity == this.client.player) {
            if (this.client.player.func_228353_F_()) {
               this.client.displayGuiScreen(new DeathScreen(p_175098_1_.deathMessage, this.world.getWorldInfo().isHardcore()));
            } else {
               this.client.player.respawnPlayer();
            }
         }
      }

   }

   public void handleServerDifficulty(SServerDifficultyPacket p_175101_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175101_1_, this, (ThreadTaskExecutor)this.client);
      this.client.world.getWorldInfo().setDifficulty(p_175101_1_.getDifficulty());
      this.client.world.getWorldInfo().setDifficultyLocked(p_175101_1_.isDifficultyLocked());
   }

   public void handleCamera(SCameraPacket p_175094_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175094_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = p_175094_1_.getEntity(this.world);
      if (entity != null) {
         this.client.setRenderViewEntity(entity);
      }

   }

   public void handleWorldBorder(SWorldBorderPacket p_175093_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175093_1_, this, (ThreadTaskExecutor)this.client);
      p_175093_1_.apply(this.world.getWorldBorder());
   }

   public void handleTitle(STitlePacket p_175099_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175099_1_, this, (ThreadTaskExecutor)this.client);
      STitlePacket.Type stitlepacket$type = p_175099_1_.getType();
      String s = null;
      String s1 = null;
      String s2 = p_175099_1_.getMessage() != null ? p_175099_1_.getMessage().getFormattedText() : "";
      switch(stitlepacket$type) {
      case TITLE:
         s = s2;
         break;
      case SUBTITLE:
         s1 = s2;
         break;
      case ACTIONBAR:
         this.client.ingameGUI.setOverlayMessage(s2, false);
         return;
      case RESET:
         this.client.ingameGUI.displayTitle("", "", -1, -1, -1);
         this.client.ingameGUI.setDefaultTitlesTimes();
         return;
      }

      this.client.ingameGUI.displayTitle(s, s1, p_175099_1_.getFadeInTime(), p_175099_1_.getDisplayTime(), p_175099_1_.getFadeOutTime());
   }

   public void handlePlayerListHeaderFooter(SPlayerListHeaderFooterPacket p_175096_1_) {
      this.client.ingameGUI.getTabList().setHeader(p_175096_1_.getHeader().getFormattedText().isEmpty() ? null : p_175096_1_.getHeader());
      this.client.ingameGUI.getTabList().setFooter(p_175096_1_.getFooter().getFormattedText().isEmpty() ? null : p_175096_1_.getFooter());
   }

   public void handleRemoveEntityEffect(SRemoveEntityEffectPacket p_147262_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147262_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = p_147262_1_.getEntity(this.world);
      if (entity instanceof LivingEntity) {
         ((LivingEntity)entity).removeActivePotionEffect(p_147262_1_.getPotion());
      }

   }

   public void handlePlayerListItem(SPlayerListItemPacket p_147256_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147256_1_, this, (ThreadTaskExecutor)this.client);
      Iterator var2 = p_147256_1_.getEntries().iterator();

      while(var2.hasNext()) {
         SPlayerListItemPacket.AddPlayerData splayerlistitempacket$addplayerdata = (SPlayerListItemPacket.AddPlayerData)var2.next();
         if (p_147256_1_.getAction() == SPlayerListItemPacket.Action.REMOVE_PLAYER) {
            this.playerInfoMap.remove(splayerlistitempacket$addplayerdata.getProfile().getId());
         } else {
            NetworkPlayerInfo networkplayerinfo = (NetworkPlayerInfo)this.playerInfoMap.get(splayerlistitempacket$addplayerdata.getProfile().getId());
            if (p_147256_1_.getAction() == SPlayerListItemPacket.Action.ADD_PLAYER) {
               networkplayerinfo = new NetworkPlayerInfo(splayerlistitempacket$addplayerdata);
               this.playerInfoMap.put(networkplayerinfo.getGameProfile().getId(), networkplayerinfo);
            }

            if (networkplayerinfo != null) {
               switch(p_147256_1_.getAction()) {
               case ADD_PLAYER:
                  networkplayerinfo.setGameType(splayerlistitempacket$addplayerdata.getGameMode());
                  networkplayerinfo.setResponseTime(splayerlistitempacket$addplayerdata.getPing());
                  networkplayerinfo.setDisplayName(splayerlistitempacket$addplayerdata.getDisplayName());
                  break;
               case UPDATE_GAME_MODE:
                  networkplayerinfo.setGameType(splayerlistitempacket$addplayerdata.getGameMode());
                  break;
               case UPDATE_LATENCY:
                  networkplayerinfo.setResponseTime(splayerlistitempacket$addplayerdata.getPing());
                  break;
               case UPDATE_DISPLAY_NAME:
                  networkplayerinfo.setDisplayName(splayerlistitempacket$addplayerdata.getDisplayName());
               }
            }
         }
      }

   }

   public void handleKeepAlive(SKeepAlivePacket p_147272_1_) {
      this.sendPacket(new CKeepAlivePacket(p_147272_1_.getId()));
   }

   public void handlePlayerAbilities(SPlayerAbilitiesPacket p_147270_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147270_1_, this, (ThreadTaskExecutor)this.client);
      PlayerEntity playerentity = this.client.player;
      playerentity.abilities.isFlying = p_147270_1_.isFlying();
      playerentity.abilities.isCreativeMode = p_147270_1_.isCreativeMode();
      playerentity.abilities.disableDamage = p_147270_1_.isInvulnerable();
      playerentity.abilities.allowFlying = p_147270_1_.isAllowFlying();
      playerentity.abilities.setFlySpeed(p_147270_1_.getFlySpeed());
      playerentity.abilities.setWalkSpeed(p_147270_1_.getWalkSpeed());
   }

   public void handleSoundEffect(SPlaySoundEffectPacket p_184327_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184327_1_, this, (ThreadTaskExecutor)this.client);
      this.client.world.playSound(this.client.player, p_184327_1_.getX(), p_184327_1_.getY(), p_184327_1_.getZ(), p_184327_1_.getSound(), p_184327_1_.getCategory(), p_184327_1_.getVolume(), p_184327_1_.getPitch());
   }

   public void func_217266_a(SSpawnMovingSoundEffectPacket p_217266_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217266_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_217266_1_.func_218762_d());
      if (entity != null) {
         this.client.world.playMovingSound(this.client.player, entity, p_217266_1_.func_218763_b(), p_217266_1_.func_218760_c(), p_217266_1_.func_218764_e(), p_217266_1_.func_218761_f());
      }

   }

   public void handleCustomSound(SPlaySoundPacket p_184329_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184329_1_, this, (ThreadTaskExecutor)this.client);
      this.client.getSoundHandler().play(new SimpleSound(p_184329_1_.getSoundName(), p_184329_1_.getCategory(), p_184329_1_.getVolume(), p_184329_1_.getPitch(), false, 0, ISound.AttenuationType.LINEAR, (float)p_184329_1_.getX(), (float)p_184329_1_.getY(), (float)p_184329_1_.getZ(), false));
   }

   public void handleResourcePack(SSendResourcePackPacket p_175095_1_) {
      String s = p_175095_1_.getURL();
      String s1 = p_175095_1_.getHash();
      if (this.validateResourcePackUrl(s)) {
         if (s.startsWith("level://")) {
            try {
               String s2 = URLDecoder.decode(s.substring("level://".length()), StandardCharsets.UTF_8.toString());
               File file1 = new File(this.client.gameDir, "saves");
               File file2 = new File(file1, s2);
               if (file2.isFile()) {
                  this.func_217283_a(CResourcePackStatusPacket.Action.ACCEPTED);
                  CompletableFuture<?> completablefuture = this.client.getPackFinder().func_217816_a(file2);
                  this.func_217279_a(completablefuture);
                  return;
               }
            } catch (UnsupportedEncodingException var8) {
            }

            this.func_217283_a(CResourcePackStatusPacket.Action.FAILED_DOWNLOAD);
         } else {
            ServerData serverdata = this.client.getCurrentServerData();
            if (serverdata != null && serverdata.getResourceMode() == ServerData.ServerResourceMode.ENABLED) {
               this.func_217283_a(CResourcePackStatusPacket.Action.ACCEPTED);
               this.func_217279_a(this.client.getPackFinder().func_217818_a(s, s1));
            } else if (serverdata != null && serverdata.getResourceMode() != ServerData.ServerResourceMode.PROMPT) {
               this.func_217283_a(CResourcePackStatusPacket.Action.DECLINED);
            } else {
               this.client.execute(() -> {
                  this.client.displayGuiScreen(new ConfirmScreen((p_lambda$null$3_3_) -> {
                     this.client = Minecraft.getInstance();
                     ServerData serverdata1 = this.client.getCurrentServerData();
                     if (p_lambda$null$3_3_) {
                        if (serverdata1 != null) {
                           serverdata1.setResourceMode(ServerData.ServerResourceMode.ENABLED);
                        }

                        this.func_217283_a(CResourcePackStatusPacket.Action.ACCEPTED);
                        this.func_217279_a(this.client.getPackFinder().func_217818_a(s, s1));
                     } else {
                        if (serverdata1 != null) {
                           serverdata1.setResourceMode(ServerData.ServerResourceMode.DISABLED);
                        }

                        this.func_217283_a(CResourcePackStatusPacket.Action.DECLINED);
                     }

                     ServerList.saveSingleServer(serverdata1);
                     this.client.displayGuiScreen((Screen)null);
                  }, new TranslationTextComponent("multiplayer.texturePrompt.line1", new Object[0]), new TranslationTextComponent("multiplayer.texturePrompt.line2", new Object[0])));
               });
            }
         }
      }

   }

   private boolean validateResourcePackUrl(String p_189688_1_) {
      try {
         URI uri = new URI(p_189688_1_);
         String s = uri.getScheme();
         boolean flag = "level".equals(s);
         if (!"http".equals(s) && !"https".equals(s) && !flag) {
            throw new URISyntaxException(p_189688_1_, "Wrong protocol");
         } else if (flag && (p_189688_1_.contains("..") || !p_189688_1_.endsWith("/resources.zip"))) {
            throw new URISyntaxException(p_189688_1_, "Invalid levelstorage resourcepack path");
         } else {
            return true;
         }
      } catch (URISyntaxException var5) {
         this.func_217283_a(CResourcePackStatusPacket.Action.FAILED_DOWNLOAD);
         return false;
      }
   }

   private void func_217279_a(CompletableFuture<?> p_217279_1_) {
      p_217279_1_.thenRun(() -> {
         this.func_217283_a(CResourcePackStatusPacket.Action.SUCCESSFULLY_LOADED);
      }).exceptionally((p_lambda$func_217279_a$6_1_) -> {
         this.func_217283_a(CResourcePackStatusPacket.Action.FAILED_DOWNLOAD);
         return null;
      });
   }

   private void func_217283_a(CResourcePackStatusPacket.Action p_217283_1_) {
      this.netManager.sendPacket(new CResourcePackStatusPacket(p_217283_1_));
   }

   public void handleUpdateBossInfo(SUpdateBossInfoPacket p_184325_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184325_1_, this, (ThreadTaskExecutor)this.client);
      this.client.ingameGUI.getBossOverlay().read(p_184325_1_);
   }

   public void handleCooldown(SCooldownPacket p_184324_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184324_1_, this, (ThreadTaskExecutor)this.client);
      if (p_184324_1_.getTicks() == 0) {
         this.client.player.getCooldownTracker().removeCooldown(p_184324_1_.getItem());
      } else {
         this.client.player.getCooldownTracker().setCooldown(p_184324_1_.getItem(), p_184324_1_.getTicks());
      }

   }

   public void handleMoveVehicle(SMoveVehiclePacket p_184323_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184323_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.client.player.getLowestRidingEntity();
      if (entity != this.client.player && entity.canPassengerSteer()) {
         entity.setPositionAndRotation(p_184323_1_.getX(), p_184323_1_.getY(), p_184323_1_.getZ(), p_184323_1_.getYaw(), p_184323_1_.getPitch());
         this.netManager.sendPacket(new CMoveVehiclePacket(entity));
      }

   }

   public void func_217268_a(SOpenBookWindowPacket p_217268_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217268_1_, this, (ThreadTaskExecutor)this.client);
      ItemStack itemstack = this.client.player.getHeldItem(p_217268_1_.getHand());
      if (itemstack.getItem() == Items.WRITTEN_BOOK) {
         this.client.displayGuiScreen(new ReadBookScreen(new ReadBookScreen.WrittenBookInfo(itemstack)));
      }

   }

   public void handleCustomPayload(SCustomPayloadPlayPacket p_147240_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147240_1_, this, (ThreadTaskExecutor)this.client);
      ResourceLocation resourcelocation = p_147240_1_.getChannelName();
      PacketBuffer packetbuffer = null;

      try {
         packetbuffer = p_147240_1_.getBufferData();
         if (SCustomPayloadPlayPacket.BRAND.equals(resourcelocation)) {
            this.client.player.setServerBrand(packetbuffer.readString(32767));
         } else {
            int j2;
            if (SCustomPayloadPlayPacket.DEBUG_PATH.equals(resourcelocation)) {
               j2 = packetbuffer.readInt();
               float f = packetbuffer.readFloat();
               Path path = Path.read(packetbuffer);
               this.client.debugRenderer.pathfinding.addPath(j2, path, f);
            } else if (SCustomPayloadPlayPacket.DEBUG_NEIGHBORS_UPDATE.equals(resourcelocation)) {
               long l1 = packetbuffer.readVarLong();
               BlockPos blockpos8 = packetbuffer.readBlockPos();
               ((NeighborsUpdateDebugRenderer)this.client.debugRenderer.neighborsUpdate).addUpdate(l1, blockpos8);
            } else {
               ArrayList list3;
               int i6;
               BlockPos blockpos7;
               int l3;
               if (SCustomPayloadPlayPacket.DEBUG_CAVES.equals(resourcelocation)) {
                  blockpos7 = packetbuffer.readBlockPos();
                  l3 = packetbuffer.readInt();
                  List<BlockPos> list1 = Lists.newArrayList();
                  list3 = Lists.newArrayList();

                  for(i6 = 0; i6 < l3; ++i6) {
                     list1.add(packetbuffer.readBlockPos());
                     list3.add(packetbuffer.readFloat());
                  }

                  this.client.debugRenderer.cave.addCave(blockpos7, list1, list3);
               } else {
                  int j6;
                  int j5;
                  if (SCustomPayloadPlayPacket.DEBUG_STRUCTURES.equals(resourcelocation)) {
                     DimensionType dimensiontype = DimensionType.getById(packetbuffer.readInt());
                     MutableBoundingBox mutableboundingbox = new MutableBoundingBox(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt());
                     j5 = packetbuffer.readInt();
                     list3 = Lists.newArrayList();
                     List<Boolean> list4 = Lists.newArrayList();

                     for(j6 = 0; j6 < j5; ++j6) {
                        list3.add(new MutableBoundingBox(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt()));
                        list4.add(packetbuffer.readBoolean());
                     }

                     this.client.debugRenderer.structure.func_223454_a(mutableboundingbox, list3, list4, dimensiontype);
                  } else if (SCustomPayloadPlayPacket.DEBUG_WORLDGEN_ATTEMPT.equals(resourcelocation)) {
                     ((WorldGenAttemptsDebugRenderer)this.client.debugRenderer.worldGenAttempts).addAttempt(packetbuffer.readBlockPos(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat());
                  } else if (SCustomPayloadPlayPacket.DEBUG_VILLAGE_SECTIONS.equals(resourcelocation)) {
                     j2 = packetbuffer.readInt();

                     for(l3 = 0; l3 < j2; ++l3) {
                        this.client.debugRenderer.field_217741_m.func_217701_a(packetbuffer.readSectionPos());
                     }

                     l3 = packetbuffer.readInt();

                     for(j5 = 0; j5 < l3; ++j5) {
                        this.client.debugRenderer.field_217741_m.func_217700_b(packetbuffer.readSectionPos());
                     }
                  } else {
                     String s9;
                     if (SCustomPayloadPlayPacket.DEBUG_POI_ADDED.equals(resourcelocation)) {
                        blockpos7 = packetbuffer.readBlockPos();
                        s9 = packetbuffer.readString();
                        j5 = packetbuffer.readInt();
                        PointOfInterestDebugRenderer.POIInfo pointofinterestdebugrenderer$poiinfo = new PointOfInterestDebugRenderer.POIInfo(blockpos7, s9, j5);
                        this.client.debugRenderer.field_217741_m.func_217691_a(pointofinterestdebugrenderer$poiinfo);
                     } else if (SCustomPayloadPlayPacket.DEBUG_POI_REMOVED.equals(resourcelocation)) {
                        blockpos7 = packetbuffer.readBlockPos();
                        this.client.debugRenderer.field_217741_m.func_217698_a(blockpos7);
                     } else if (SCustomPayloadPlayPacket.DEBUG_POI_TICKET_COUNT.equals(resourcelocation)) {
                        blockpos7 = packetbuffer.readBlockPos();
                        l3 = packetbuffer.readInt();
                        this.client.debugRenderer.field_217741_m.func_217706_a(blockpos7, l3);
                     } else if (SCustomPayloadPlayPacket.DEBUG_GOAL_SELECTOR.equals(resourcelocation)) {
                        blockpos7 = packetbuffer.readBlockPos();
                        l3 = packetbuffer.readInt();
                        j5 = packetbuffer.readInt();
                        list3 = Lists.newArrayList();

                        for(i6 = 0; i6 < j5; ++i6) {
                           j6 = packetbuffer.readInt();
                           boolean flag = packetbuffer.readBoolean();
                           String s = packetbuffer.readString(255);
                           list3.add(new EntityAIDebugRenderer.Entry(blockpos7, j6, s, flag));
                        }

                        this.client.debugRenderer.field_217742_n.func_217682_a(l3, list3);
                     } else if (SCustomPayloadPlayPacket.DEBUG_RAIDS.equals(resourcelocation)) {
                        j2 = packetbuffer.readInt();
                        Collection<BlockPos> collection = Lists.newArrayList();

                        for(j5 = 0; j5 < j2; ++j5) {
                           collection.add(packetbuffer.readBlockPos());
                        }

                        this.client.debugRenderer.field_222927_n.func_222906_a(collection);
                     } else {
                        int k6;
                        int i7;
                        int l7;
                        String s11;
                        double d1;
                        double d3;
                        double d5;
                        Position iposition1;
                        UUID uuid1;
                        int k8;
                        if (SCustomPayloadPlayPacket.DEBUG_BRAIN.equals(resourcelocation)) {
                           d1 = packetbuffer.readDouble();
                           d3 = packetbuffer.readDouble();
                           d5 = packetbuffer.readDouble();
                           iposition1 = new Position(d1, d3, d5);
                           uuid1 = packetbuffer.readUniqueId();
                           k6 = packetbuffer.readInt();
                           String s1 = packetbuffer.readString();
                           String s2 = packetbuffer.readString();
                           int i1 = packetbuffer.readInt();
                           String s3 = packetbuffer.readString();
                           boolean flag1 = packetbuffer.readBoolean();
                           Path path1;
                           if (flag1) {
                              path1 = Path.read(packetbuffer);
                           } else {
                              path1 = null;
                           }

                           boolean flag2 = packetbuffer.readBoolean();
                           PointOfInterestDebugRenderer.BrainInfo pointofinterestdebugrenderer$braininfo = new PointOfInterestDebugRenderer.BrainInfo(uuid1, k6, s1, s2, i1, iposition1, s3, path1, flag2);
                           i7 = packetbuffer.readInt();

                           for(l7 = 0; l7 < i7; ++l7) {
                              s11 = packetbuffer.readString();
                              pointofinterestdebugrenderer$braininfo.field_217751_e.add(s11);
                           }

                           l7 = packetbuffer.readInt();

                           for(k8 = 0; k8 < l7; ++k8) {
                              String s5 = packetbuffer.readString();
                              pointofinterestdebugrenderer$braininfo.field_217752_f.add(s5);
                           }

                           k8 = packetbuffer.readInt();

                           int i9;
                           for(i9 = 0; i9 < k8; ++i9) {
                              String s6 = packetbuffer.readString();
                              pointofinterestdebugrenderer$braininfo.field_217753_g.add(s6);
                           }

                           i9 = packetbuffer.readInt();

                           int k9;
                           for(k9 = 0; k9 < i9; ++k9) {
                              BlockPos blockpos = packetbuffer.readBlockPos();
                              pointofinterestdebugrenderer$braininfo.field_217754_h.add(blockpos);
                           }

                           k9 = packetbuffer.readInt();

                           for(int l9 = 0; l9 < k9; ++l9) {
                              String s7 = packetbuffer.readString();
                              pointofinterestdebugrenderer$braininfo.field_223457_m.add(s7);
                           }

                           this.client.debugRenderer.field_217741_m.func_217692_a(pointofinterestdebugrenderer$braininfo);
                        } else if (SCustomPayloadPlayPacket.field_229727_m_.equals(resourcelocation)) {
                           d1 = packetbuffer.readDouble();
                           d3 = packetbuffer.readDouble();
                           d5 = packetbuffer.readDouble();
                           iposition1 = new Position(d1, d3, d5);
                           uuid1 = packetbuffer.readUniqueId();
                           k6 = packetbuffer.readInt();
                           boolean flag4 = packetbuffer.readBoolean();
                           BlockPos blockpos9 = null;
                           if (flag4) {
                              blockpos9 = packetbuffer.readBlockPos();
                           }

                           boolean flag5 = packetbuffer.readBoolean();
                           BlockPos blockpos10 = null;
                           if (flag5) {
                              blockpos10 = packetbuffer.readBlockPos();
                           }

                           int l6 = packetbuffer.readInt();
                           boolean flag6 = packetbuffer.readBoolean();
                           Path path2 = null;
                           if (flag6) {
                              path2 = Path.read(packetbuffer);
                           }

                           BeeDebugRenderer.Bee beedebugrenderer$bee = new BeeDebugRenderer.Bee(uuid1, k6, iposition1, path2, blockpos9, blockpos10, l6);
                           i7 = packetbuffer.readInt();

                           for(l7 = 0; l7 < i7; ++l7) {
                              s11 = packetbuffer.readString();
                              beedebugrenderer$bee.field_229005_h_.add(s11);
                           }

                           l7 = packetbuffer.readInt();

                           for(k8 = 0; k8 < l7; ++k8) {
                              BlockPos blockpos11 = packetbuffer.readBlockPos();
                              beedebugrenderer$bee.field_229006_i_.add(blockpos11);
                           }

                           this.client.debugRenderer.field_229017_n_.func_228964_a_(beedebugrenderer$bee);
                        } else {
                           int l5;
                           if (SCustomPayloadPlayPacket.field_229728_n_.equals(resourcelocation)) {
                              blockpos7 = packetbuffer.readBlockPos();
                              s9 = packetbuffer.readString();
                              j5 = packetbuffer.readInt();
                              l5 = packetbuffer.readInt();
                              boolean flag3 = packetbuffer.readBoolean();
                              BeeDebugRenderer.Hive beedebugrenderer$hive = new BeeDebugRenderer.Hive(blockpos7, s9, j5, l5, flag3, this.world.getGameTime());
                              this.client.debugRenderer.field_229017_n_.func_228966_a_(beedebugrenderer$hive);
                           } else if (SCustomPayloadPlayPacket.field_229730_p_.equals(resourcelocation)) {
                              this.client.debugRenderer.field_229018_q_.func_217675_a();
                           } else if (SCustomPayloadPlayPacket.field_229729_o_.equals(resourcelocation)) {
                              blockpos7 = packetbuffer.readBlockPos();
                              l3 = packetbuffer.readInt();
                              String s10 = packetbuffer.readString();
                              l5 = packetbuffer.readInt();
                              this.client.debugRenderer.field_229018_q_.func_229022_a_(blockpos7, l3, s10, l5);
                           } else if (!NetworkHooks.onCustomPayload(p_147240_1_, this.netManager)) {
                              LOGGER.warn("Unknown custom packet identifier: {}", resourcelocation);
                           }
                        }
                     }
                  }
               }
            }
         }
      } finally {
         if (packetbuffer != null) {
         }

      }

   }

   public void handleScoreboardObjective(SScoreboardObjectivePacket p_147291_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147291_1_, this, (ThreadTaskExecutor)this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      String s = p_147291_1_.getObjectiveName();
      if (p_147291_1_.getAction() == 0) {
         scoreboard.addObjective(s, ScoreCriteria.DUMMY, p_147291_1_.getDisplayName(), p_147291_1_.getRenderType());
      } else if (scoreboard.hasObjective(s)) {
         ScoreObjective scoreobjective = scoreboard.getObjective(s);
         if (p_147291_1_.getAction() == 1) {
            scoreboard.removeObjective(scoreobjective);
         } else if (p_147291_1_.getAction() == 2) {
            scoreobjective.setRenderType(p_147291_1_.getRenderType());
            scoreobjective.setDisplayName(p_147291_1_.getDisplayName());
         }
      }

   }

   public void handleUpdateScore(SUpdateScorePacket p_147250_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147250_1_, this, (ThreadTaskExecutor)this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      String s = p_147250_1_.getObjectiveName();
      switch(p_147250_1_.getAction()) {
      case CHANGE:
         ScoreObjective scoreobjective = scoreboard.getOrCreateObjective(s);
         Score score = scoreboard.getOrCreateScore(p_147250_1_.getPlayerName(), scoreobjective);
         score.setScorePoints(p_147250_1_.getScoreValue());
         break;
      case REMOVE:
         scoreboard.removeObjectiveFromEntity(p_147250_1_.getPlayerName(), scoreboard.getObjective(s));
      }

   }

   public void handleDisplayObjective(SDisplayObjectivePacket p_147254_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147254_1_, this, (ThreadTaskExecutor)this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      String s = p_147254_1_.getName();
      ScoreObjective scoreobjective = s == null ? null : scoreboard.getOrCreateObjective(s);
      scoreboard.setObjectiveInDisplaySlot(p_147254_1_.getPosition(), scoreobjective);
   }

   public void handleTeams(STeamsPacket p_147247_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147247_1_, this, (ThreadTaskExecutor)this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      ScorePlayerTeam scoreplayerteam;
      if (p_147247_1_.getAction() == 0) {
         scoreplayerteam = scoreboard.createTeam(p_147247_1_.getName());
      } else {
         scoreplayerteam = scoreboard.getTeam(p_147247_1_.getName());
      }

      if (p_147247_1_.getAction() == 0 || p_147247_1_.getAction() == 2) {
         scoreplayerteam.setDisplayName(p_147247_1_.getDisplayName());
         scoreplayerteam.setColor(p_147247_1_.getColor());
         scoreplayerteam.setFriendlyFlags(p_147247_1_.getFriendlyFlags());
         Team.Visible team$visible = Team.Visible.getByName(p_147247_1_.getNameTagVisibility());
         if (team$visible != null) {
            scoreplayerteam.setNameTagVisibility(team$visible);
         }

         Team.CollisionRule team$collisionrule = Team.CollisionRule.getByName(p_147247_1_.getCollisionRule());
         if (team$collisionrule != null) {
            scoreplayerteam.setCollisionRule(team$collisionrule);
         }

         scoreplayerteam.setPrefix(p_147247_1_.getPrefix());
         scoreplayerteam.setSuffix(p_147247_1_.getSuffix());
      }

      Iterator var6;
      String s1;
      if (p_147247_1_.getAction() == 0 || p_147247_1_.getAction() == 3) {
         var6 = p_147247_1_.getPlayers().iterator();

         while(var6.hasNext()) {
            s1 = (String)var6.next();
            scoreboard.addPlayerToTeam(s1, scoreplayerteam);
         }
      }

      if (p_147247_1_.getAction() == 4) {
         var6 = p_147247_1_.getPlayers().iterator();

         while(var6.hasNext()) {
            s1 = (String)var6.next();
            scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
         }
      }

      if (p_147247_1_.getAction() == 1) {
         scoreboard.removeTeam(scoreplayerteam);
      }

   }

   public void handleParticles(SSpawnParticlePacket p_147289_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147289_1_, this, (ThreadTaskExecutor)this.client);
      if (p_147289_1_.getParticleCount() == 0) {
         double d0 = (double)(p_147289_1_.getParticleSpeed() * p_147289_1_.getXOffset());
         double d2 = (double)(p_147289_1_.getParticleSpeed() * p_147289_1_.getYOffset());
         double d4 = (double)(p_147289_1_.getParticleSpeed() * p_147289_1_.getZOffset());

         try {
            this.world.addParticle(p_147289_1_.getParticle(), p_147289_1_.isLongDistance(), p_147289_1_.getXCoordinate(), p_147289_1_.getYCoordinate(), p_147289_1_.getZCoordinate(), d0, d2, d4);
         } catch (Throwable var17) {
            LOGGER.warn("Could not spawn particle effect {}", p_147289_1_.getParticle());
         }
      } else {
         for(int i = 0; i < p_147289_1_.getParticleCount(); ++i) {
            double d1 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getXOffset();
            double d3 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getYOffset();
            double d5 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getZOffset();
            double d6 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getParticleSpeed();
            double d7 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getParticleSpeed();
            double d8 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getParticleSpeed();

            try {
               this.world.addParticle(p_147289_1_.getParticle(), p_147289_1_.isLongDistance(), p_147289_1_.getXCoordinate() + d1, p_147289_1_.getYCoordinate() + d3, p_147289_1_.getZCoordinate() + d5, d6, d7, d8);
            } catch (Throwable var16) {
               LOGGER.warn("Could not spawn particle effect {}", p_147289_1_.getParticle());
               return;
            }
         }
      }

   }

   public void handleEntityProperties(SEntityPropertiesPacket p_147290_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147290_1_, this, (ThreadTaskExecutor)this.client);
      Entity entity = this.world.getEntityByID(p_147290_1_.getEntityId());
      if (entity != null) {
         if (!(entity instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
         }

         AbstractAttributeMap abstractattributemap = ((LivingEntity)entity).getAttributes();
         Iterator var4 = p_147290_1_.getSnapshots().iterator();

         while(var4.hasNext()) {
            SEntityPropertiesPacket.Snapshot sentitypropertiespacket$snapshot = (SEntityPropertiesPacket.Snapshot)var4.next();
            IAttributeInstance iattributeinstance = abstractattributemap.getAttributeInstanceByName(sentitypropertiespacket$snapshot.getName());
            if (iattributeinstance == null) {
               iattributeinstance = abstractattributemap.registerAttribute(new RangedAttribute((IAttribute)null, sentitypropertiespacket$snapshot.getName(), 0.0D, -1.7976931348623157E308D, Double.MAX_VALUE));
            }

            iattributeinstance.setBaseValue(sentitypropertiespacket$snapshot.getBaseValue());
            iattributeinstance.removeAllModifiers();
            Iterator var7 = sentitypropertiespacket$snapshot.getModifiers().iterator();

            while(var7.hasNext()) {
               AttributeModifier attributemodifier = (AttributeModifier)var7.next();
               iattributeinstance.applyModifier(attributemodifier);
            }
         }
      }

   }

   public void handlePlaceGhostRecipe(SPlaceGhostRecipePacket p_194307_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_194307_1_, this, (ThreadTaskExecutor)this.client);
      Container container = this.client.player.openContainer;
      if (container.windowId == p_194307_1_.getWindowId() && container.getCanCraft(this.client.player)) {
         this.recipeManager.getRecipe(p_194307_1_.getRecipeId()).ifPresent((p_lambda$handlePlaceGhostRecipe$7_2_) -> {
            if (this.client.currentScreen instanceof IRecipeShownListener) {
               RecipeBookGui recipebookgui = ((IRecipeShownListener)this.client.currentScreen).func_194310_f();
               recipebookgui.setupGhostRecipe(p_lambda$handlePlaceGhostRecipe$7_2_, container.inventorySlots);
            }

         });
      }

   }

   public void handleUpdateLight(SUpdateLightPacket p_217269_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217269_1_, this, (ThreadTaskExecutor)this.client);
      int i = p_217269_1_.getChunkX();
      int j = p_217269_1_.getChunkZ();
      WorldLightManager worldlightmanager = this.world.getChunkProvider().getLightManager();
      int k = p_217269_1_.getSkyLightUpdateMask();
      int l = p_217269_1_.getSkyLightResetMask();
      Iterator<byte[]> iterator = p_217269_1_.getSkyLightData().iterator();
      this.setLightData(i, j, worldlightmanager, LightType.SKY, k, l, iterator);
      int i1 = p_217269_1_.getBlockLightUpdateMask();
      int j1 = p_217269_1_.getBlockLightResetMask();
      Iterator<byte[]> iterator1 = p_217269_1_.getBlockLightData().iterator();
      this.setLightData(i, j, worldlightmanager, LightType.BLOCK, i1, j1, iterator1);
   }

   public void func_217273_a(SMerchantOffersPacket p_217273_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217273_1_, this, (ThreadTaskExecutor)this.client);
      Container container = this.client.player.openContainer;
      if (p_217273_1_.func_218732_b() == container.windowId && container instanceof MerchantContainer) {
         ((MerchantContainer)container).func_217044_a(new MerchantOffers(p_217273_1_.func_218733_c().func_222199_a()));
         ((MerchantContainer)container).func_217052_e(p_217273_1_.func_218734_e());
         ((MerchantContainer)container).func_217043_f(p_217273_1_.func_218731_d());
         ((MerchantContainer)container).func_217045_a(p_217273_1_.func_218735_f());
         ((MerchantContainer)container).func_223431_b(p_217273_1_.func_223477_g());
      }

   }

   public void func_217270_a(SUpdateViewDistancePacket p_217270_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217270_1_, this, (ThreadTaskExecutor)this.client);
      this.field_217287_m = p_217270_1_.func_218758_b();
      this.world.getChunkProvider().setViewDistance(p_217270_1_.func_218758_b());
   }

   public void func_217267_a(SUpdateChunkPositionPacket p_217267_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217267_1_, this, (ThreadTaskExecutor)this.client);
      this.world.getChunkProvider().setCenter(p_217267_1_.func_218755_b(), p_217267_1_.func_218754_c());
   }

   public void func_225312_a(SPlayerDiggingPacket p_225312_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_225312_1_, this, (ThreadTaskExecutor)this.client);
      this.client.playerController.func_225323_a(this.world, p_225312_1_.func_225374_c(), p_225312_1_.func_225375_b(), p_225312_1_.func_225377_e(), p_225312_1_.func_225376_d());
   }

   private void setLightData(int p_217284_1_, int p_217284_2_, WorldLightManager p_217284_3_, LightType p_217284_4_, int p_217284_5_, int p_217284_6_, Iterator<byte[]> p_217284_7_) {
      for(int i = 0; i < 18; ++i) {
         int j = -1 + i;
         boolean flag = (p_217284_5_ & 1 << i) != 0;
         boolean flag1 = (p_217284_6_ & 1 << i) != 0;
         if (flag || flag1) {
            p_217284_3_.setData(p_217284_4_, SectionPos.of(p_217284_1_, j, p_217284_2_), flag ? new NibbleArray((byte[])((byte[])((byte[])p_217284_7_.next()).clone())) : new NibbleArray());
            this.world.markSurroundingsForRerender(p_217284_1_, j, p_217284_2_);
         }
      }

   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
      return this.playerInfoMap.values();
   }

   @Nullable
   public NetworkPlayerInfo getPlayerInfo(UUID p_175102_1_) {
      return (NetworkPlayerInfo)this.playerInfoMap.get(p_175102_1_);
   }

   @Nullable
   public NetworkPlayerInfo getPlayerInfo(String p_175104_1_) {
      Iterator var2 = this.playerInfoMap.values().iterator();

      NetworkPlayerInfo networkplayerinfo;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         networkplayerinfo = (NetworkPlayerInfo)var2.next();
      } while(!networkplayerinfo.getGameProfile().getName().equals(p_175104_1_));

      return networkplayerinfo;
   }

   public GameProfile getGameProfile() {
      return this.profile;
   }

   public ClientAdvancementManager getAdvancementManager() {
      return this.advancementManager;
   }

   public CommandDispatcher<ISuggestionProvider> func_195515_i() {
      return this.commandDispatcher;
   }

   public ClientWorld getWorld() {
      return this.world;
   }

   public NetworkTagManager getTags() {
      return this.networkTagManager;
   }

   public NBTQueryManager getNBTQueryManager() {
      return this.nbtQueryManager;
   }

   public UUID func_217277_l() {
      return this.field_217289_q;
   }
}
