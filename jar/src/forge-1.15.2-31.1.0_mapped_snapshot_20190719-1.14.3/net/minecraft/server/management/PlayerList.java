package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorld;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList {
   public static final File FILE_PLAYERBANS = new File("banned-players.json");
   public static final File FILE_IPBANS = new File("banned-ips.json");
   public static final File FILE_OPS = new File("ops.json");
   public static final File FILE_WHITELIST = new File("whitelist.json");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer server;
   private final List<ServerPlayerEntity> players = Lists.newArrayList();
   private final Map<UUID, ServerPlayerEntity> uuidToPlayerMap = Maps.newHashMap();
   private final BanList bannedPlayers;
   private final IPBanList bannedIPs;
   private final OpList ops;
   private final WhiteList whiteListedPlayers;
   private final Map<UUID, ServerStatisticsManager> playerStatFiles;
   private final Map<UUID, PlayerAdvancements> advancements;
   private IPlayerFileData playerDataManager;
   private boolean whiteListEnforced;
   protected final int maxPlayers;
   private int viewDistance;
   private GameType gameType;
   private boolean commandsAllowedForAll;
   private int playerPingIndex;
   private final List<ServerPlayerEntity> playersView;

   public PlayerList(MinecraftServer p_i50688_1_, int p_i50688_2_) {
      this.bannedPlayers = new BanList(FILE_PLAYERBANS);
      this.bannedIPs = new IPBanList(FILE_IPBANS);
      this.ops = new OpList(FILE_OPS);
      this.whiteListedPlayers = new WhiteList(FILE_WHITELIST);
      this.playerStatFiles = Maps.newHashMap();
      this.advancements = Maps.newHashMap();
      this.playersView = Collections.unmodifiableList(this.players);
      this.server = p_i50688_1_;
      this.maxPlayers = p_i50688_2_;
      this.getBannedPlayers().setLanServer(true);
      this.getBannedIPs().setLanServer(true);
   }

   public void initializeConnectionToPlayer(NetworkManager p_72355_1_, ServerPlayerEntity p_72355_2_) {
      GameProfile gameprofile = p_72355_2_.getGameProfile();
      PlayerProfileCache playerprofilecache = this.server.getPlayerProfileCache();
      GameProfile gameprofile1 = playerprofilecache.getProfileByUUID(gameprofile.getId());
      String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
      playerprofilecache.addEntry(gameprofile);
      CompoundNBT compoundnbt = this.readPlayerDataFromFile(p_72355_2_);
      ServerWorld serverworld = this.server.getWorld(p_72355_2_.dimension);
      if (serverworld == null) {
         p_72355_2_.dimension = DimensionType.OVERWORLD;
         serverworld = this.server.getWorld(p_72355_2_.dimension);
         p_72355_2_.setPosition((double)serverworld.getWorldInfo().getSpawnX(), (double)serverworld.getWorldInfo().getSpawnY(), (double)serverworld.getWorldInfo().getSpawnZ());
      }

      p_72355_2_.setWorld(serverworld);
      p_72355_2_.interactionManager.setWorld((ServerWorld)p_72355_2_.world);
      String s1 = "local";
      if (p_72355_1_.getRemoteAddress() != null) {
         s1 = p_72355_1_.getRemoteAddress().toString();
      }

      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", p_72355_2_.getName().getString(), s1, p_72355_2_.getEntityId(), p_72355_2_.func_226277_ct_(), p_72355_2_.func_226278_cu_(), p_72355_2_.func_226281_cx_());
      WorldInfo worldinfo = serverworld.getWorldInfo();
      this.setPlayerGameTypeBasedOnOther(p_72355_2_, (ServerPlayerEntity)null, serverworld);
      ServerPlayNetHandler serverplaynethandler = new ServerPlayNetHandler(this.server, p_72355_1_, p_72355_2_);
      NetworkHooks.sendMCRegistryPackets(p_72355_1_, "PLAY_TO_CLIENT");
      NetworkHooks.sendDimensionDataPacket(p_72355_1_, p_72355_2_);
      GameRules gamerules = serverworld.getGameRules();
      boolean flag = gamerules.getBoolean(GameRules.field_226683_z_);
      boolean flag1 = gamerules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
      serverplaynethandler.sendPacket(new SJoinGamePacket(p_72355_2_.getEntityId(), p_72355_2_.interactionManager.getGameType(), WorldInfo.func_227498_c_(worldinfo.getSeed()), worldinfo.isHardcore(), serverworld.dimension.getType(), this.getMaxPlayers(), worldinfo.getGenerator(), this.viewDistance, flag1, !flag));
      serverplaynethandler.sendPacket(new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.BRAND, (new PacketBuffer(Unpooled.buffer())).writeString(this.getServer().getServerModName())));
      serverplaynethandler.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
      serverplaynethandler.sendPacket(new SPlayerAbilitiesPacket(p_72355_2_.abilities));
      serverplaynethandler.sendPacket(new SHeldItemChangePacket(p_72355_2_.inventory.currentItem));
      serverplaynethandler.sendPacket(new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
      serverplaynethandler.sendPacket(new STagsListPacket(this.server.getNetworkTagManager()));
      this.updatePermissionLevel(p_72355_2_);
      p_72355_2_.getStats().markAllDirty();
      p_72355_2_.getRecipeBook().init(p_72355_2_);
      this.sendScoreboard(serverworld.getScoreboard(), p_72355_2_);
      this.server.refreshStatusNextTick();
      TranslationTextComponent itextcomponent;
      if (p_72355_2_.getGameProfile().getName().equalsIgnoreCase(s)) {
         itextcomponent = new TranslationTextComponent("multiplayer.player.joined", new Object[]{p_72355_2_.getDisplayName()});
      } else {
         itextcomponent = new TranslationTextComponent("multiplayer.player.joined.renamed", new Object[]{p_72355_2_.getDisplayName(), s});
      }

      this.sendMessage(itextcomponent.applyTextStyle(TextFormatting.YELLOW));
      serverplaynethandler.setPlayerLocation(p_72355_2_.func_226277_ct_(), p_72355_2_.func_226278_cu_(), p_72355_2_.func_226281_cx_(), p_72355_2_.rotationYaw, p_72355_2_.rotationPitch);
      this.addPlayer(p_72355_2_);
      this.uuidToPlayerMap.put(p_72355_2_.getUniqueID(), p_72355_2_);
      this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, new ServerPlayerEntity[]{p_72355_2_}));

      for(int i = 0; i < this.players.size(); ++i) {
         p_72355_2_.connection.sendPacket(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, new ServerPlayerEntity[]{(ServerPlayerEntity)this.players.get(i)}));
      }

      serverworld.addNewPlayer(p_72355_2_);
      this.server.getCustomBossEvents().onPlayerLogin(p_72355_2_);
      this.sendWorldInfo(p_72355_2_, serverworld);
      if (!this.server.getResourcePackUrl().isEmpty()) {
         p_72355_2_.loadResourcePack(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
      }

      Iterator var22 = p_72355_2_.getActivePotionEffects().iterator();

      while(var22.hasNext()) {
         EffectInstance effectinstance = (EffectInstance)var22.next();
         serverplaynethandler.sendPacket(new SPlayEntityEffectPacket(p_72355_2_.getEntityId(), effectinstance));
      }

      if (compoundnbt != null && compoundnbt.contains("RootVehicle", 10)) {
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("RootVehicle");
         Entity entity1 = EntityType.func_220335_a(compoundnbt1.getCompound("Entity"), serverworld, (p_lambda$initializeConnectionToPlayer$0_1_) -> {
            return !serverworld.summonEntity(p_lambda$initializeConnectionToPlayer$0_1_) ? null : p_lambda$initializeConnectionToPlayer$0_1_;
         });
         if (entity1 != null) {
            UUID uuid = compoundnbt1.getUniqueId("Attach");
            Iterator var20;
            Entity entity2;
            if (entity1.getUniqueID().equals(uuid)) {
               p_72355_2_.startRiding(entity1, true);
            } else {
               var20 = entity1.getRecursivePassengers().iterator();

               while(var20.hasNext()) {
                  entity2 = (Entity)var20.next();
                  if (entity2.getUniqueID().equals(uuid)) {
                     p_72355_2_.startRiding(entity2, true);
                     break;
                  }
               }
            }

            if (!p_72355_2_.isPassenger()) {
               LOGGER.warn("Couldn't reattach entity to player");
               serverworld.removeEntity(entity1);
               var20 = entity1.getRecursivePassengers().iterator();

               while(var20.hasNext()) {
                  entity2 = (Entity)var20.next();
                  serverworld.removeEntity(entity2);
               }
            }
         }
      }

      p_72355_2_.addSelfToInternalCraftingInventory();
      BasicEventHooks.firePlayerLoggedIn(p_72355_2_);
   }

   protected void sendScoreboard(ServerScoreboard p_96456_1_, ServerPlayerEntity p_96456_2_) {
      Set<ScoreObjective> set = Sets.newHashSet();
      Iterator var4 = p_96456_1_.getTeams().iterator();

      while(var4.hasNext()) {
         ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)var4.next();
         p_96456_2_.connection.sendPacket(new STeamsPacket(scoreplayerteam, 0));
      }

      for(int i = 0; i < 19; ++i) {
         ScoreObjective scoreobjective = p_96456_1_.getObjectiveInDisplaySlot(i);
         if (scoreobjective != null && !set.contains(scoreobjective)) {
            Iterator var6 = p_96456_1_.getCreatePackets(scoreobjective).iterator();

            while(var6.hasNext()) {
               IPacket<?> ipacket = (IPacket)var6.next();
               p_96456_2_.connection.sendPacket(ipacket);
            }

            set.add(scoreobjective);
         }
      }

   }

   public void func_212504_a(ServerWorld p_212504_1_) {
      this.playerDataManager = p_212504_1_.getSaveHandler();
      p_212504_1_.getWorldBorder().addListener(new IBorderListener() {
         public void onSizeChanged(WorldBorder p_177694_1_, double p_177694_2_) {
            PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(p_177694_1_, SWorldBorderPacket.Action.SET_SIZE));
         }

         public void onTransitionStarted(WorldBorder p_177692_1_, double p_177692_2_, double p_177692_4_, long p_177692_6_) {
            PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(p_177692_1_, SWorldBorderPacket.Action.LERP_SIZE));
         }

         public void onCenterChanged(WorldBorder p_177693_1_, double p_177693_2_, double p_177693_4_) {
            PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(p_177693_1_, SWorldBorderPacket.Action.SET_CENTER));
         }

         public void onWarningTimeChanged(WorldBorder p_177691_1_, int p_177691_2_) {
            PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(p_177691_1_, SWorldBorderPacket.Action.SET_WARNING_TIME));
         }

         public void onWarningDistanceChanged(WorldBorder p_177690_1_, int p_177690_2_) {
            PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(p_177690_1_, SWorldBorderPacket.Action.SET_WARNING_BLOCKS));
         }

         public void onDamageAmountChanged(WorldBorder p_177696_1_, double p_177696_2_) {
         }

         public void onDamageBufferChanged(WorldBorder p_177695_1_, double p_177695_2_) {
         }
      });
   }

   @Nullable
   public CompoundNBT readPlayerDataFromFile(ServerPlayerEntity p_72380_1_) {
      CompoundNBT compoundnbt = this.server.getWorld(DimensionType.OVERWORLD).getWorldInfo().getPlayerNBTTagCompound();
      CompoundNBT compoundnbt1;
      if (p_72380_1_.getName().getString().equals(this.server.getServerOwner()) && compoundnbt != null) {
         compoundnbt1 = compoundnbt;
         p_72380_1_.read(compoundnbt);
         LOGGER.debug("loading single player");
         ForgeEventFactory.firePlayerLoadingEvent(p_72380_1_, (IPlayerFileData)this.playerDataManager, p_72380_1_.getUniqueID().toString());
      } else {
         compoundnbt1 = this.playerDataManager.readPlayerData(p_72380_1_);
      }

      return compoundnbt1;
   }

   protected void writePlayerData(ServerPlayerEntity p_72391_1_) {
      if (p_72391_1_.connection != null) {
         this.playerDataManager.writePlayerData(p_72391_1_);
         ServerStatisticsManager serverstatisticsmanager = (ServerStatisticsManager)this.playerStatFiles.get(p_72391_1_.getUniqueID());
         if (serverstatisticsmanager != null) {
            serverstatisticsmanager.saveStatFile();
         }

         PlayerAdvancements playeradvancements = (PlayerAdvancements)this.advancements.get(p_72391_1_.getUniqueID());
         if (playeradvancements != null) {
            playeradvancements.save();
         }

      }
   }

   public void playerLoggedOut(ServerPlayerEntity p_72367_1_) {
      BasicEventHooks.firePlayerLoggedOut(p_72367_1_);
      ServerWorld serverworld = p_72367_1_.getServerWorld();
      p_72367_1_.addStat(Stats.LEAVE_GAME);
      this.writePlayerData(p_72367_1_);
      if (p_72367_1_.isPassenger()) {
         Entity entity = p_72367_1_.getLowestRidingEntity();
         if (entity.isOnePlayerRiding()) {
            LOGGER.debug("Removing player mount");
            p_72367_1_.stopRiding();
            serverworld.removeEntity(entity);
            Iterator var4 = entity.getRecursivePassengers().iterator();

            while(var4.hasNext()) {
               Entity entity1 = (Entity)var4.next();
               serverworld.removeEntity(entity1);
            }

            serverworld.getChunk(p_72367_1_.chunkCoordX, p_72367_1_.chunkCoordZ).markDirty();
         }
      }

      p_72367_1_.detach();
      serverworld.removePlayer(p_72367_1_);
      p_72367_1_.getAdvancements().dispose();
      this.removePlayer(p_72367_1_);
      this.server.getCustomBossEvents().onPlayerLogout(p_72367_1_);
      UUID uuid = p_72367_1_.getUniqueID();
      ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)this.uuidToPlayerMap.get(uuid);
      if (serverplayerentity == p_72367_1_) {
         this.uuidToPlayerMap.remove(uuid);
         this.playerStatFiles.remove(uuid);
         this.advancements.remove(uuid);
      }

      this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.REMOVE_PLAYER, new ServerPlayerEntity[]{p_72367_1_}));
   }

   @Nullable
   public ITextComponent canPlayerLogin(SocketAddress p_206258_1_, GameProfile p_206258_2_) {
      TranslationTextComponent itextcomponent;
      if (this.bannedPlayers.isBanned(p_206258_2_)) {
         ProfileBanEntry profilebanentry = (ProfileBanEntry)this.bannedPlayers.getEntry(p_206258_2_);
         itextcomponent = new TranslationTextComponent("multiplayer.disconnect.banned.reason", new Object[]{profilebanentry.getBanReason()});
         if (profilebanentry.getBanEndDate() != null) {
            itextcomponent.appendSibling(new TranslationTextComponent("multiplayer.disconnect.banned.expiration", new Object[]{DATE_FORMAT.format(profilebanentry.getBanEndDate())}));
         }

         return itextcomponent;
      } else if (!this.canJoin(p_206258_2_)) {
         return new TranslationTextComponent("multiplayer.disconnect.not_whitelisted", new Object[0]);
      } else if (this.bannedIPs.isBanned(p_206258_1_)) {
         IPBanEntry ipbanentry = this.bannedIPs.getBanEntry(p_206258_1_);
         itextcomponent = new TranslationTextComponent("multiplayer.disconnect.banned_ip.reason", new Object[]{ipbanentry.getBanReason()});
         if (ipbanentry.getBanEndDate() != null) {
            itextcomponent.appendSibling(new TranslationTextComponent("multiplayer.disconnect.banned_ip.expiration", new Object[]{DATE_FORMAT.format(ipbanentry.getBanEndDate())}));
         }

         return itextcomponent;
      } else {
         return this.players.size() >= this.maxPlayers && !this.bypassesPlayerLimit(p_206258_2_) ? new TranslationTextComponent("multiplayer.disconnect.server_full", new Object[0]) : null;
      }
   }

   public ServerPlayerEntity createPlayerForUser(GameProfile p_148545_1_) {
      UUID uuid = PlayerEntity.getUUID(p_148545_1_);
      List<ServerPlayerEntity> list = Lists.newArrayList();

      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)this.players.get(i);
         if (serverplayerentity.getUniqueID().equals(uuid)) {
            list.add(serverplayerentity);
         }
      }

      ServerPlayerEntity serverplayerentity2 = (ServerPlayerEntity)this.uuidToPlayerMap.get(p_148545_1_.getId());
      if (serverplayerentity2 != null && !list.contains(serverplayerentity2)) {
         list.add(serverplayerentity2);
      }

      Iterator var8 = list.iterator();

      while(var8.hasNext()) {
         ServerPlayerEntity serverplayerentity1 = (ServerPlayerEntity)var8.next();
         serverplayerentity1.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.duplicate_login", new Object[0]));
      }

      Object playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
      } else {
         playerinteractionmanager = new PlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
      }

      return new ServerPlayerEntity(this.server, this.server.getWorld(DimensionType.OVERWORLD), p_148545_1_, (PlayerInteractionManager)playerinteractionmanager);
   }

   public ServerPlayerEntity recreatePlayerEntity(ServerPlayerEntity p_72368_1_, DimensionType p_72368_2_, boolean p_72368_3_) {
      ServerWorld world = this.server.getWorld(p_72368_2_);
      if (world == null) {
         p_72368_2_ = p_72368_1_.getSpawnDimension();
      } else if (!world.getDimension().canRespawnHere()) {
         p_72368_2_ = world.getDimension().getRespawnDimension(p_72368_1_);
      }

      if (this.server.getWorld(p_72368_2_) == null) {
         p_72368_2_ = DimensionType.OVERWORLD;
      }

      this.removePlayer(p_72368_1_);
      p_72368_1_.getServerWorld().removePlayer(p_72368_1_, true);
      BlockPos blockpos = p_72368_1_.getBedLocation(p_72368_2_);
      boolean flag = p_72368_1_.isSpawnForced(p_72368_2_);
      p_72368_1_.dimension = p_72368_2_;
      Object playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(this.server.getWorld(p_72368_1_.dimension));
      } else {
         playerinteractionmanager = new PlayerInteractionManager(this.server.getWorld(p_72368_1_.dimension));
      }

      ServerPlayerEntity serverplayerentity = new ServerPlayerEntity(this.server, this.server.getWorld(p_72368_1_.dimension), p_72368_1_.getGameProfile(), (PlayerInteractionManager)playerinteractionmanager);
      serverplayerentity.connection = p_72368_1_.connection;
      serverplayerentity.copyFrom(p_72368_1_, p_72368_3_);
      p_72368_1_.remove(false);
      serverplayerentity.dimension = p_72368_2_;
      serverplayerentity.setEntityId(p_72368_1_.getEntityId());
      serverplayerentity.setPrimaryHand(p_72368_1_.getPrimaryHand());
      Iterator var9 = p_72368_1_.getTags().iterator();

      while(var9.hasNext()) {
         String s = (String)var9.next();
         serverplayerentity.addTag(s);
      }

      ServerWorld serverworld = this.server.getWorld(p_72368_1_.dimension);
      this.setPlayerGameTypeBasedOnOther(serverplayerentity, p_72368_1_, serverworld);
      if (blockpos != null) {
         Optional<Vec3d> optional = PlayerEntity.func_213822_a(this.server.getWorld(p_72368_1_.dimension), blockpos, flag);
         if (optional.isPresent()) {
            Vec3d vec3d = (Vec3d)optional.get();
            serverplayerentity.setLocationAndAngles(vec3d.x, vec3d.y, vec3d.z, 0.0F, 0.0F);
            serverplayerentity.setSpawnPoint(blockpos, flag, false, p_72368_2_);
         } else {
            serverplayerentity.connection.sendPacket(new SChangeGameStatePacket(0, 0.0F));
         }
      }

      while(!serverworld.func_226669_j_(serverplayerentity) && serverplayerentity.func_226278_cu_() < 256.0D) {
         serverplayerentity.setPosition(serverplayerentity.func_226277_ct_(), serverplayerentity.func_226278_cu_() + 1.0D, serverplayerentity.func_226281_cx_());
      }

      WorldInfo worldinfo = serverplayerentity.world.getWorldInfo();
      NetworkHooks.sendDimensionDataPacket(serverplayerentity.connection.netManager, serverplayerentity);
      serverplayerentity.connection.sendPacket(new SRespawnPacket(serverplayerentity.dimension, WorldInfo.func_227498_c_(worldinfo.getSeed()), worldinfo.getGenerator(), serverplayerentity.interactionManager.getGameType()));
      BlockPos blockpos1 = serverworld.getSpawnPoint();
      serverplayerentity.connection.setPlayerLocation(serverplayerentity.func_226277_ct_(), serverplayerentity.func_226278_cu_(), serverplayerentity.func_226281_cx_(), serverplayerentity.rotationYaw, serverplayerentity.rotationPitch);
      serverplayerentity.connection.sendPacket(new SSpawnPositionPacket(blockpos1));
      serverplayerentity.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
      serverplayerentity.connection.sendPacket(new SSetExperiencePacket(serverplayerentity.experience, serverplayerentity.experienceTotal, serverplayerentity.experienceLevel));
      this.sendWorldInfo(serverplayerentity, serverworld);
      this.updatePermissionLevel(serverplayerentity);
      serverworld.addRespawnedPlayer(serverplayerentity);
      this.addPlayer(serverplayerentity);
      this.uuidToPlayerMap.put(serverplayerentity.getUniqueID(), serverplayerentity);
      serverplayerentity.addSelfToInternalCraftingInventory();
      serverplayerentity.setHealth(serverplayerentity.getHealth());
      BasicEventHooks.firePlayerRespawnEvent(serverplayerentity, p_72368_3_);
      return serverplayerentity;
   }

   public void updatePermissionLevel(ServerPlayerEntity p_187243_1_) {
      GameProfile gameprofile = p_187243_1_.getGameProfile();
      int i = this.server.getPermissionLevel(gameprofile);
      this.sendPlayerPermissionLevel(p_187243_1_, i);
   }

   public void tick() {
      if (++this.playerPingIndex > 600) {
         this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_LATENCY, this.players));
         this.playerPingIndex = 0;
      }

   }

   public void sendPacketToAllPlayers(IPacket<?> p_148540_1_) {
      for(int i = 0; i < this.players.size(); ++i) {
         ((ServerPlayerEntity)this.players.get(i)).connection.sendPacket(p_148540_1_);
      }

   }

   public void sendPacketToAllPlayersInDimension(IPacket<?> p_148537_1_, DimensionType p_148537_2_) {
      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)this.players.get(i);
         if (serverplayerentity.dimension == p_148537_2_) {
            serverplayerentity.connection.sendPacket(p_148537_1_);
         }
      }

   }

   public void sendMessageToAllTeamMembers(PlayerEntity p_177453_1_, ITextComponent p_177453_2_) {
      Team team = p_177453_1_.getTeam();
      if (team != null) {
         Iterator var4 = team.getMembershipCollection().iterator();

         while(var4.hasNext()) {
            String s = (String)var4.next();
            ServerPlayerEntity serverplayerentity = this.getPlayerByUsername(s);
            if (serverplayerentity != null && serverplayerentity != p_177453_1_) {
               serverplayerentity.sendMessage(p_177453_2_);
            }
         }
      }

   }

   public void sendMessageToTeamOrAllPlayers(PlayerEntity p_177452_1_, ITextComponent p_177452_2_) {
      Team team = p_177452_1_.getTeam();
      if (team == null) {
         this.sendMessage(p_177452_2_);
      } else {
         for(int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)this.players.get(i);
            if (serverplayerentity.getTeam() != team) {
               serverplayerentity.sendMessage(p_177452_2_);
            }
         }
      }

   }

   public String[] getOnlinePlayerNames() {
      String[] astring = new String[this.players.size()];

      for(int i = 0; i < this.players.size(); ++i) {
         astring[i] = ((ServerPlayerEntity)this.players.get(i)).getGameProfile().getName();
      }

      return astring;
   }

   public BanList getBannedPlayers() {
      return this.bannedPlayers;
   }

   public IPBanList getBannedIPs() {
      return this.bannedIPs;
   }

   public void addOp(GameProfile p_152605_1_) {
      this.ops.addEntry(new OpEntry(p_152605_1_, this.server.getOpPermissionLevel(), this.ops.bypassesPlayerLimit(p_152605_1_)));
      ServerPlayerEntity serverplayerentity = this.getPlayerByUUID(p_152605_1_.getId());
      if (serverplayerentity != null) {
         this.updatePermissionLevel(serverplayerentity);
      }

   }

   public void removeOp(GameProfile p_152610_1_) {
      this.ops.removeEntry(p_152610_1_);
      ServerPlayerEntity serverplayerentity = this.getPlayerByUUID(p_152610_1_.getId());
      if (serverplayerentity != null) {
         this.updatePermissionLevel(serverplayerentity);
      }

   }

   private void sendPlayerPermissionLevel(ServerPlayerEntity p_187245_1_, int p_187245_2_) {
      if (p_187245_1_.connection != null) {
         byte b0;
         if (p_187245_2_ <= 0) {
            b0 = 24;
         } else if (p_187245_2_ >= 4) {
            b0 = 28;
         } else {
            b0 = (byte)(24 + p_187245_2_);
         }

         p_187245_1_.connection.sendPacket(new SEntityStatusPacket(p_187245_1_, b0));
      }

      this.server.getCommandManager().send(p_187245_1_);
   }

   public boolean canJoin(GameProfile p_152607_1_) {
      return !this.whiteListEnforced || this.ops.hasEntry(p_152607_1_) || this.whiteListedPlayers.hasEntry(p_152607_1_);
   }

   public boolean canSendCommands(GameProfile p_152596_1_) {
      return this.ops.hasEntry(p_152596_1_) || this.server.func_213199_b(p_152596_1_) && this.server.getWorld(DimensionType.OVERWORLD).getWorldInfo().areCommandsAllowed() || this.commandsAllowedForAll;
   }

   @Nullable
   public ServerPlayerEntity getPlayerByUsername(String p_152612_1_) {
      Iterator var2 = this.players.iterator();

      ServerPlayerEntity serverplayerentity;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         serverplayerentity = (ServerPlayerEntity)var2.next();
      } while(!serverplayerentity.getGameProfile().getName().equalsIgnoreCase(p_152612_1_));

      return serverplayerentity;
   }

   public void sendToAllNearExcept(@Nullable PlayerEntity p_148543_1_, double p_148543_2_, double p_148543_4_, double p_148543_6_, double p_148543_8_, DimensionType p_148543_10_, IPacket<?> p_148543_11_) {
      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)this.players.get(i);
         if (serverplayerentity != p_148543_1_ && serverplayerentity.dimension == p_148543_10_) {
            double d0 = p_148543_2_ - serverplayerentity.func_226277_ct_();
            double d1 = p_148543_4_ - serverplayerentity.func_226278_cu_();
            double d2 = p_148543_6_ - serverplayerentity.func_226281_cx_();
            if (d0 * d0 + d1 * d1 + d2 * d2 < p_148543_8_ * p_148543_8_) {
               serverplayerentity.connection.sendPacket(p_148543_11_);
            }
         }
      }

   }

   public void saveAllPlayerData() {
      for(int i = 0; i < this.players.size(); ++i) {
         this.writePlayerData((ServerPlayerEntity)this.players.get(i));
      }

   }

   public WhiteList getWhitelistedPlayers() {
      return this.whiteListedPlayers;
   }

   public String[] getWhitelistedPlayerNames() {
      return this.whiteListedPlayers.getKeys();
   }

   public OpList getOppedPlayers() {
      return this.ops;
   }

   public String[] getOppedPlayerNames() {
      return this.ops.getKeys();
   }

   public void reloadWhitelist() {
   }

   public void sendWorldInfo(ServerPlayerEntity p_72354_1_, ServerWorld p_72354_2_) {
      WorldBorder worldborder = this.server.getWorld(DimensionType.OVERWORLD).getWorldBorder();
      p_72354_1_.connection.sendPacket(new SWorldBorderPacket(worldborder, SWorldBorderPacket.Action.INITIALIZE));
      p_72354_1_.connection.sendPacket(new SUpdateTimePacket(p_72354_2_.getGameTime(), p_72354_2_.getDayTime(), p_72354_2_.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
      BlockPos blockpos = p_72354_2_.getSpawnPoint();
      p_72354_1_.connection.sendPacket(new SSpawnPositionPacket(blockpos));
      if (p_72354_2_.isRaining()) {
         p_72354_1_.connection.sendPacket(new SChangeGameStatePacket(1, 0.0F));
         p_72354_1_.connection.sendPacket(new SChangeGameStatePacket(7, p_72354_2_.getRainStrength(1.0F)));
         p_72354_1_.connection.sendPacket(new SChangeGameStatePacket(8, p_72354_2_.getThunderStrength(1.0F)));
      }

   }

   public void sendInventory(ServerPlayerEntity p_72385_1_) {
      p_72385_1_.sendContainerToPlayer(p_72385_1_.container);
      p_72385_1_.setPlayerHealthUpdated();
      p_72385_1_.connection.sendPacket(new SHeldItemChangePacket(p_72385_1_.inventory.currentItem));
   }

   public int getCurrentPlayerCount() {
      return this.players.size();
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public boolean isWhiteListEnabled() {
      return this.whiteListEnforced;
   }

   public void setWhiteListEnabled(boolean p_72371_1_) {
      this.whiteListEnforced = p_72371_1_;
   }

   public List<ServerPlayerEntity> getPlayersMatchingAddress(String p_72382_1_) {
      List<ServerPlayerEntity> list = Lists.newArrayList();
      Iterator var3 = this.players.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var3.next();
         if (serverplayerentity.getPlayerIP().equals(p_72382_1_)) {
            list.add(serverplayerentity);
         }
      }

      return list;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public CompoundNBT getHostPlayerData() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void setGameType(GameType p_152604_1_) {
      this.gameType = p_152604_1_;
   }

   private void setPlayerGameTypeBasedOnOther(ServerPlayerEntity p_72381_1_, ServerPlayerEntity p_72381_2_, IWorld p_72381_3_) {
      if (p_72381_2_ != null) {
         p_72381_1_.interactionManager.setGameType(p_72381_2_.interactionManager.getGameType());
      } else if (this.gameType != null) {
         p_72381_1_.interactionManager.setGameType(this.gameType);
      }

      p_72381_1_.interactionManager.initializeGameType(p_72381_3_.getWorldInfo().getGameType());
   }

   @OnlyIn(Dist.CLIENT)
   public void setCommandsAllowedForAll(boolean p_72387_1_) {
      this.commandsAllowedForAll = p_72387_1_;
   }

   public void removeAllPlayers() {
      for(int i = 0; i < this.players.size(); ++i) {
         ((ServerPlayerEntity)this.players.get(i)).connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.server_shutdown", new Object[0]));
      }

   }

   public void sendMessage(ITextComponent p_148544_1_, boolean p_148544_2_) {
      this.server.sendMessage(p_148544_1_);
      ChatType chattype = p_148544_2_ ? ChatType.SYSTEM : ChatType.CHAT;
      this.sendPacketToAllPlayers(new SChatPacket(p_148544_1_, chattype));
   }

   public void sendMessage(ITextComponent p_148539_1_) {
      this.sendMessage(p_148539_1_, true);
   }

   public ServerStatisticsManager getPlayerStats(PlayerEntity p_152602_1_) {
      UUID uuid = p_152602_1_.getUniqueID();
      ServerStatisticsManager serverstatisticsmanager = uuid == null ? null : (ServerStatisticsManager)this.playerStatFiles.get(uuid);
      if (serverstatisticsmanager == null) {
         File file1 = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), "stats");
         File file2 = new File(file1, uuid + ".json");
         if (!file2.exists()) {
            File file3 = new File(file1, p_152602_1_.getName().getString() + ".json");
            if (file3.exists() && file3.isFile()) {
               file3.renameTo(file2);
            }
         }

         serverstatisticsmanager = new ServerStatisticsManager(this.server, file2);
         this.playerStatFiles.put(uuid, serverstatisticsmanager);
      }

      return serverstatisticsmanager;
   }

   public PlayerAdvancements getPlayerAdvancements(ServerPlayerEntity p_192054_1_) {
      UUID uuid = p_192054_1_.getUniqueID();
      PlayerAdvancements playeradvancements = (PlayerAdvancements)this.advancements.get(uuid);
      if (playeradvancements == null) {
         File file1 = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), "advancements");
         File file2 = new File(file1, uuid + ".json");
         playeradvancements = new PlayerAdvancements(this.server, file2, p_192054_1_);
         this.advancements.put(uuid, playeradvancements);
      }

      playeradvancements.setPlayer(p_192054_1_);
      return playeradvancements;
   }

   public void setViewDistance(int p_217884_1_) {
      this.viewDistance = p_217884_1_;
      this.sendPacketToAllPlayers(new SUpdateViewDistancePacket(p_217884_1_));
      Iterator var2 = this.server.getWorlds().iterator();

      while(var2.hasNext()) {
         ServerWorld serverworld = (ServerWorld)var2.next();
         if (serverworld != null) {
            serverworld.getChunkProvider().func_217219_a(p_217884_1_);
         }
      }

   }

   public List<ServerPlayerEntity> getPlayers() {
      return this.playersView;
   }

   @Nullable
   public ServerPlayerEntity getPlayerByUUID(UUID p_177451_1_) {
      return (ServerPlayerEntity)this.uuidToPlayerMap.get(p_177451_1_);
   }

   public boolean bypassesPlayerLimit(GameProfile p_183023_1_) {
      return false;
   }

   public void reloadResources() {
      Iterator var1 = this.advancements.values().iterator();

      while(var1.hasNext()) {
         PlayerAdvancements playeradvancements = (PlayerAdvancements)var1.next();
         playeradvancements.reload();
      }

      this.sendPacketToAllPlayers(new STagsListPacket(this.server.getNetworkTagManager()));
      SUpdateRecipesPacket supdaterecipespacket = new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());
      Iterator var5 = this.players.iterator();

      while(var5.hasNext()) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var5.next();
         serverplayerentity.connection.sendPacket(supdaterecipespacket);
         serverplayerentity.getRecipeBook().init(serverplayerentity);
      }

   }

   public boolean commandsAllowedForAll() {
      return this.commandsAllowedForAll;
   }

   public boolean addPlayer(ServerPlayerEntity p_addPlayer_1_) {
      return DimensionManager.rebuildPlayerMap(this, this.players.add(p_addPlayer_1_));
   }

   public boolean removePlayer(ServerPlayerEntity p_removePlayer_1_) {
      return DimensionManager.rebuildPlayerMap(this, this.players.remove(p_removePlayer_1_));
   }
}
