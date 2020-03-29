package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.AbstractMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ServerRecipeBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ServerCooldownTracker;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity implements IContainerListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private String language = "en_US";
   public ServerPlayNetHandler connection;
   public final MinecraftServer server;
   public final PlayerInteractionManager interactionManager;
   private final List<Integer> entityRemoveQueue = Lists.newLinkedList();
   private final PlayerAdvancements advancements;
   private final ServerStatisticsManager stats;
   private float lastHealthScore = Float.MIN_VALUE;
   private int lastFoodScore = Integer.MIN_VALUE;
   private int lastAirScore = Integer.MIN_VALUE;
   private int lastArmorScore = Integer.MIN_VALUE;
   private int lastLevelScore = Integer.MIN_VALUE;
   private int lastExperienceScore = Integer.MIN_VALUE;
   private float lastHealth = -1.0E8F;
   private int lastFoodLevel = -99999999;
   private boolean wasHungry = true;
   private int lastExperience = -99999999;
   private int respawnInvulnerabilityTicks = 60;
   private ChatVisibility chatVisibility;
   private boolean chatColours = true;
   private long playerLastActiveTime = Util.milliTime();
   private Entity spectatingEntity;
   private boolean invulnerableDimensionChange;
   private boolean seenCredits;
   private final ServerRecipeBook recipeBook;
   private Vec3d levitationStartPos;
   private int levitatingSince;
   private boolean disconnected;
   @Nullable
   private Vec3d enteredNetherPosition;
   private SectionPos managedSectionPos = SectionPos.of(0, 0, 0);
   public int currentWindowId;
   public boolean isChangingQuantityOnly;
   public int ping;
   public boolean queuedEndExit;

   public ServerPlayerEntity(MinecraftServer p_i45285_1_, ServerWorld p_i45285_2_, GameProfile p_i45285_3_, PlayerInteractionManager p_i45285_4_) {
      super(p_i45285_2_, p_i45285_3_);
      p_i45285_4_.player = this;
      this.interactionManager = p_i45285_4_;
      this.server = p_i45285_1_;
      this.recipeBook = new ServerRecipeBook(p_i45285_1_.getRecipeManager());
      this.stats = p_i45285_1_.getPlayerList().getPlayerStats(this);
      this.advancements = p_i45285_1_.getPlayerList().getPlayerAdvancements(this);
      this.stepHeight = 1.0F;
      this.func_205734_a(p_i45285_2_);
   }

   private void func_205734_a(ServerWorld p_205734_1_) {
      BlockPos blockpos = p_205734_1_.getSpawnPoint();
      if (p_205734_1_.dimension.hasSkyLight() && p_205734_1_.getWorldInfo().getGameType() != GameType.ADVENTURE) {
         int i = Math.max(0, this.server.getSpawnRadius(p_205734_1_));
         int j = MathHelper.floor(p_205734_1_.getWorldBorder().getClosestDistance((double)blockpos.getX(), (double)blockpos.getZ()));
         if (j < i) {
            i = j;
         }

         if (j <= 1) {
            i = 1;
         }

         long k = (long)(i * 2 + 1);
         long l = k * k;
         int i1 = l > 2147483647L ? Integer.MAX_VALUE : (int)l;
         int j1 = this.func_205735_q(i1);
         int k1 = (new Random()).nextInt(i1);

         for(int l1 = 0; l1 < i1; ++l1) {
            int i2 = (k1 + j1 * l1) % i1;
            int j2 = i2 % (i * 2 + 1);
            int k2 = i2 / (i * 2 + 1);
            BlockPos blockpos1 = p_205734_1_.getDimension().findSpawn(blockpos.getX() + j2 - i, blockpos.getZ() + k2 - i, false);
            if (blockpos1 != null) {
               this.moveToBlockPosAndAngles(blockpos1, 0.0F, 0.0F);
               if (p_205734_1_.func_226669_j_(this)) {
                  break;
               }
            }
         }
      } else {
         this.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);

         while(!p_205734_1_.func_226669_j_(this) && this.func_226278_cu_() < 255.0D) {
            this.setPosition(this.func_226277_ct_(), this.func_226278_cu_() + 1.0D, this.func_226281_cx_());
         }
      }

   }

   private int func_205735_q(int p_205735_1_) {
      return p_205735_1_ <= 16 ? p_205735_1_ - 1 : 17;
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("playerGameType", 99)) {
         if (this.getServer().getForceGamemode()) {
            this.interactionManager.setGameType(this.getServer().getGameType());
         } else {
            this.interactionManager.setGameType(GameType.getByID(p_70037_1_.getInt("playerGameType")));
         }
      }

      if (p_70037_1_.contains("enteredNetherPosition", 10)) {
         CompoundNBT compoundnbt = p_70037_1_.getCompound("enteredNetherPosition");
         this.enteredNetherPosition = new Vec3d(compoundnbt.getDouble("x"), compoundnbt.getDouble("y"), compoundnbt.getDouble("z"));
      }

      this.seenCredits = p_70037_1_.getBoolean("seenCredits");
      if (p_70037_1_.contains("recipeBook", 10)) {
         this.recipeBook.read(p_70037_1_.getCompound("recipeBook"));
      }

      if (this.isSleeping()) {
         this.wakeUp();
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("playerGameType", this.interactionManager.getGameType().getID());
      p_213281_1_.putBoolean("seenCredits", this.seenCredits);
      if (this.enteredNetherPosition != null) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putDouble("x", this.enteredNetherPosition.x);
         compoundnbt.putDouble("y", this.enteredNetherPosition.y);
         compoundnbt.putDouble("z", this.enteredNetherPosition.z);
         p_213281_1_.put("enteredNetherPosition", compoundnbt);
      }

      Entity entity1 = this.getLowestRidingEntity();
      Entity entity = this.getRidingEntity();
      if (entity != null && entity1 != this && entity1.isOnePlayerRiding()) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         CompoundNBT compoundnbt2 = new CompoundNBT();
         entity1.writeUnlessPassenger(compoundnbt2);
         compoundnbt1.putUniqueId("Attach", entity.getUniqueID());
         compoundnbt1.put("Entity", compoundnbt2);
         p_213281_1_.put("RootVehicle", compoundnbt1);
      }

      p_213281_1_.put("recipeBook", this.recipeBook.write());
   }

   public void func_195394_a(int p_195394_1_) {
      float f = (float)this.xpBarCap();
      float f1 = (f - 1.0F) / f;
      this.experience = MathHelper.clamp((float)p_195394_1_ / f, 0.0F, f1);
      this.lastExperience = -1;
   }

   public void func_195399_b(int p_195399_1_) {
      this.experienceLevel = p_195399_1_;
      this.lastExperience = -1;
   }

   public void addExperienceLevel(int p_82242_1_) {
      super.addExperienceLevel(p_82242_1_);
      this.lastExperience = -1;
   }

   public void onEnchant(ItemStack p_192024_1_, int p_192024_2_) {
      super.onEnchant(p_192024_1_, p_192024_2_);
      this.lastExperience = -1;
   }

   public void addSelfToInternalCraftingInventory() {
      this.openContainer.addListener(this);
   }

   public void sendEnterCombat() {
      super.sendEnterCombat();
      this.connection.sendPacket(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTER_COMBAT));
   }

   public void sendEndCombat() {
      super.sendEndCombat();
      this.connection.sendPacket(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.END_COMBAT));
   }

   protected void onInsideBlock(BlockState p_191955_1_) {
      CriteriaTriggers.ENTER_BLOCK.trigger(this, p_191955_1_);
   }

   protected CooldownTracker createCooldownTracker() {
      return new ServerCooldownTracker(this);
   }

   public void tick() {
      this.interactionManager.tick();
      --this.respawnInvulnerabilityTicks;
      if (this.hurtResistantTime > 0) {
         --this.hurtResistantTime;
      }

      this.openContainer.detectAndSendChanges();
      if (!this.world.isRemote && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.container;
      }

      while(!this.entityRemoveQueue.isEmpty()) {
         int i = Math.min(this.entityRemoveQueue.size(), Integer.MAX_VALUE);
         int[] aint = new int[i];
         Iterator<Integer> iterator = this.entityRemoveQueue.iterator();
         int j = 0;

         while(iterator.hasNext() && j < i) {
            aint[j++] = (Integer)iterator.next();
            iterator.remove();
         }

         this.connection.sendPacket(new SDestroyEntitiesPacket(aint));
      }

      Entity entity = this.getSpectatingEntity();
      if (entity != this) {
         if (entity.isAlive()) {
            this.setPositionAndRotation(entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), entity.rotationYaw, entity.rotationPitch);
            this.getServerWorld().getChunkProvider().updatePlayerPosition(this);
            if (this.func_226564_dU_()) {
               this.setSpectatingEntity(this);
            }
         } else {
            this.setSpectatingEntity(this);
         }
      }

      CriteriaTriggers.TICK.trigger(this);
      if (this.levitationStartPos != null) {
         CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.ticksExisted - this.levitatingSince);
      }

      this.advancements.flushDirty(this);
   }

   public void playerTick() {
      try {
         if (!this.isSpectator() || this.world.isBlockLoaded(new BlockPos(this))) {
            super.tick();
         }

         for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (itemstack.getItem().isComplex()) {
               IPacket<?> ipacket = ((AbstractMapItem)itemstack.getItem()).getUpdatePacket(itemstack, this.world, this);
               if (ipacket != null) {
                  this.connection.sendPacket(ipacket);
               }
            }
         }

         if (this.getHealth() != this.lastHealth || this.lastFoodLevel != this.foodStats.getFoodLevel() || this.foodStats.getSaturationLevel() == 0.0F != this.wasHungry) {
            this.connection.sendPacket(new SUpdateHealthPacket(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
            this.lastHealth = this.getHealth();
            this.lastFoodLevel = this.foodStats.getFoodLevel();
            this.wasHungry = this.foodStats.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.lastHealthScore) {
            this.lastHealthScore = this.getHealth() + this.getAbsorptionAmount();
            this.updateScorePoints(ScoreCriteria.HEALTH, MathHelper.ceil(this.lastHealthScore));
         }

         if (this.foodStats.getFoodLevel() != this.lastFoodScore) {
            this.lastFoodScore = this.foodStats.getFoodLevel();
            this.updateScorePoints(ScoreCriteria.FOOD, MathHelper.ceil((float)this.lastFoodScore));
         }

         if (this.getAir() != this.lastAirScore) {
            this.lastAirScore = this.getAir();
            this.updateScorePoints(ScoreCriteria.AIR, MathHelper.ceil((float)this.lastAirScore));
         }

         if (this.getTotalArmorValue() != this.lastArmorScore) {
            this.lastArmorScore = this.getTotalArmorValue();
            this.updateScorePoints(ScoreCriteria.ARMOR, MathHelper.ceil((float)this.lastArmorScore));
         }

         if (this.experienceTotal != this.lastExperienceScore) {
            this.lastExperienceScore = this.experienceTotal;
            this.updateScorePoints(ScoreCriteria.XP, MathHelper.ceil((float)this.lastExperienceScore));
         }

         if (this.experienceLevel != this.lastLevelScore) {
            this.lastLevelScore = this.experienceLevel;
            this.updateScorePoints(ScoreCriteria.LEVEL, MathHelper.ceil((float)this.lastLevelScore));
         }

         if (this.experienceTotal != this.lastExperience) {
            this.lastExperience = this.experienceTotal;
            this.connection.sendPacket(new SSetExperiencePacket(this.experience, this.experienceTotal, this.experienceLevel));
         }

         if (this.ticksExisted % 20 == 0) {
            CriteriaTriggers.LOCATION.trigger(this);
         }

      } catch (Throwable var4) {
         CrashReport crashreport = CrashReport.makeCrashReport(var4, "Ticking player");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Player being ticked");
         this.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   private void updateScorePoints(ScoreCriteria p_184849_1_, int p_184849_2_) {
      this.getWorldScoreboard().forAllObjectives(p_184849_1_, this.getScoreboardName(), (p_lambda$updateScorePoints$0_1_) -> {
         p_lambda$updateScorePoints$0_1_.setScorePoints(p_184849_2_);
      });
   }

   public void onDeath(DamageSource p_70645_1_) {
      if (!ForgeHooks.onLivingDeath(this, p_70645_1_)) {
         boolean flag = this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
         if (flag) {
            ITextComponent itextcomponent = this.getCombatTracker().getDeathMessage();
            this.connection.sendPacket(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent), (p_lambda$onDeath$2_2_) -> {
               if (!p_lambda$onDeath$2_2_.isSuccess()) {
                  int i = true;
                  String s = itextcomponent.getStringTruncated(256);
                  ITextComponent itextcomponent1 = new TranslationTextComponent("death.attack.message_too_long", new Object[]{(new StringTextComponent(s)).applyTextStyle(TextFormatting.YELLOW)});
                  ITextComponent itextcomponent2 = (new TranslationTextComponent("death.attack.even_more_magic", new Object[]{this.getDisplayName()})).applyTextStyle((p_lambda$null$1_1_) -> {
                     p_lambda$null$1_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent1));
                  });
                  this.connection.sendPacket(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent2));
               }

            });
            Team team = this.getTeam();
            if (team != null && team.getDeathMessageVisibility() != Team.Visible.ALWAYS) {
               if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OTHER_TEAMS) {
                  this.server.getPlayerList().sendMessageToAllTeamMembers(this, itextcomponent);
               } else if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OWN_TEAM) {
                  this.server.getPlayerList().sendMessageToTeamOrAllPlayers(this, itextcomponent);
               }
            } else {
               this.server.getPlayerList().sendMessage(itextcomponent);
            }
         } else {
            this.connection.sendPacket(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED));
         }

         this.spawnShoulderEntities();
         if (!this.isSpectator()) {
            this.spawnDrops(p_70645_1_);
         }

         this.getWorldScoreboard().forAllObjectives(ScoreCriteria.DEATH_COUNT, this.getScoreboardName(), Score::incrementScore);
         LivingEntity livingentity = this.getAttackingEntity();
         if (livingentity != null) {
            this.addStat(Stats.ENTITY_KILLED_BY.get(livingentity.getType()));
            livingentity.awardKillScore(this, this.scoreValue, p_70645_1_);
            this.func_226298_f_(livingentity);
         }

         this.world.setEntityState(this, (byte)3);
         this.addStat(Stats.DEATHS);
         this.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
         this.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
         this.extinguish();
         this.setFlag(0, false);
         this.getCombatTracker().reset();
      }
   }

   public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
      if (p_191956_1_ != this) {
         super.awardKillScore(p_191956_1_, p_191956_2_, p_191956_3_);
         this.addScore(p_191956_2_);
         String s = this.getScoreboardName();
         String s1 = p_191956_1_.getScoreboardName();
         this.getWorldScoreboard().forAllObjectives(ScoreCriteria.TOTAL_KILL_COUNT, s, Score::incrementScore);
         if (p_191956_1_ instanceof PlayerEntity) {
            this.addStat(Stats.PLAYER_KILLS);
            this.getWorldScoreboard().forAllObjectives(ScoreCriteria.PLAYER_KILL_COUNT, s, Score::incrementScore);
         } else {
            this.addStat(Stats.MOB_KILLS);
         }

         this.handleTeamKill(s, s1, ScoreCriteria.TEAM_KILL);
         this.handleTeamKill(s1, s, ScoreCriteria.KILLED_BY_TEAM);
         CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, p_191956_1_, p_191956_3_);
      }

   }

   private void handleTeamKill(String p_195398_1_, String p_195398_2_, ScoreCriteria[] p_195398_3_) {
      ScorePlayerTeam scoreplayerteam = this.getWorldScoreboard().getPlayersTeam(p_195398_2_);
      if (scoreplayerteam != null) {
         int i = scoreplayerteam.getColor().getColorIndex();
         if (i >= 0 && i < p_195398_3_.length) {
            this.getWorldScoreboard().forAllObjectives(p_195398_3_[i], p_195398_1_, Score::incrementScore);
         }
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         boolean flag = this.server.isDedicatedServer() && this.canPlayersAttack() && "fall".equals(p_70097_1_.damageType);
         if (!flag && this.respawnInvulnerabilityTicks > 0 && p_70097_1_ != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (p_70097_1_ instanceof EntityDamageSource) {
               Entity entity = p_70097_1_.getTrueSource();
               if (entity instanceof PlayerEntity && !this.canAttackPlayer((PlayerEntity)entity)) {
                  return false;
               }

               if (entity instanceof AbstractArrowEntity) {
                  AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)entity;
                  Entity entity1 = abstractarrowentity.getShooter();
                  if (entity1 instanceof PlayerEntity && !this.canAttackPlayer((PlayerEntity)entity1)) {
                     return false;
                  }
               }
            }

            return super.attackEntityFrom(p_70097_1_, p_70097_2_);
         }
      }
   }

   public boolean canAttackPlayer(PlayerEntity p_96122_1_) {
      return !this.canPlayersAttack() ? false : super.canAttackPlayer(p_96122_1_);
   }

   private boolean canPlayersAttack() {
      return this.server.isPVPEnabled();
   }

   @Nullable
   public Entity changeDimension(DimensionType p_changeDimension_1_, ITeleporter p_changeDimension_2_) {
      if (!ForgeHooks.onTravelToDimension(this, p_changeDimension_1_)) {
         return null;
      } else {
         this.invulnerableDimensionChange = true;
         DimensionType dimensiontype = this.dimension;
         if (dimensiontype == DimensionType.THE_END && p_changeDimension_1_ == DimensionType.OVERWORLD) {
            this.detach();
            this.getServerWorld().removePlayer(this, true);
            if (!this.queuedEndExit) {
               this.queuedEndExit = true;
               this.connection.sendPacket(new SChangeGameStatePacket(4, this.seenCredits ? 0.0F : 1.0F));
               this.seenCredits = true;
            }

            return this;
         } else {
            ServerWorld serverworld = this.server.getWorld(dimensiontype);
            this.dimension = p_changeDimension_1_;
            ServerWorld serverworld1 = this.server.getWorld(p_changeDimension_1_);
            WorldInfo worldinfo = serverworld1.getWorldInfo();
            NetworkHooks.sendDimensionDataPacket(this.connection.netManager, this);
            this.connection.sendPacket(new SRespawnPacket(p_changeDimension_1_, WorldInfo.func_227498_c_(worldinfo.getSeed()), worldinfo.getGenerator(), this.interactionManager.getGameType()));
            this.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
            PlayerList playerlist = this.server.getPlayerList();
            playerlist.updatePermissionLevel(this);
            serverworld.removeEntity(this, true);
            this.revive();
            Entity e = p_changeDimension_2_.placeEntity(this, serverworld, serverworld1, this.rotationYaw, (p_lambda$changeDimension$3_5_) -> {
               double d0 = this.func_226277_ct_();
               double d1 = this.func_226278_cu_();
               double d2 = this.func_226281_cx_();
               float f = this.rotationPitch;
               float f1 = this.rotationYaw;
               double d3 = 8.0D;
               float f2 = f1;
               serverworld.getProfiler().startSection("moving");
               double moveFactor = serverworld.getDimension().getMovementFactor() / serverworld1.getDimension().getMovementFactor();
               d0 *= moveFactor;
               d2 *= moveFactor;
               if (dimensiontype == DimensionType.OVERWORLD && p_changeDimension_1_ == DimensionType.THE_NETHER) {
                  this.enteredNetherPosition = this.getPositionVec();
               } else if (dimensiontype == DimensionType.OVERWORLD && p_changeDimension_1_ == DimensionType.THE_END) {
                  BlockPos blockpos = serverworld1.getSpawnCoordinate();
                  d0 = (double)blockpos.getX();
                  d1 = (double)blockpos.getY();
                  d2 = (double)blockpos.getZ();
                  f1 = 90.0F;
                  f = 0.0F;
               }

               this.setLocationAndAngles(d0, d1, d2, f1, f);
               serverworld.getProfiler().endSection();
               serverworld.getProfiler().startSection("placing");
               double d7 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().minX() + 16.0D);
               double d4 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().minZ() + 16.0D);
               double d5 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().maxX() - 16.0D);
               double d6 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().maxZ() - 16.0D);
               d0 = MathHelper.clamp(d0, d7, d5);
               d2 = MathHelper.clamp(d2, d4, d6);
               this.setLocationAndAngles(d0, d1, d2, f1, f);
               if (p_changeDimension_1_ == DimensionType.THE_END) {
                  int i = MathHelper.floor(this.func_226277_ct_());
                  int j = MathHelper.floor(this.func_226278_cu_()) - 1;
                  int k = MathHelper.floor(this.func_226281_cx_());
                  int l = true;
                  int i1 = false;

                  for(int j1 = -2; j1 <= 2; ++j1) {
                     for(int k1 = -2; k1 <= 2; ++k1) {
                        for(int l1 = -1; l1 < 3; ++l1) {
                           int i2 = i + k1 * 1 + j1 * 0;
                           int j2 = j + l1;
                           int k2 = k + k1 * 0 - j1 * 1;
                           boolean flag = l1 < 0;
                           serverworld1.setBlockState(new BlockPos(i2, j2, k2), flag ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
                        }
                     }
                  }

                  this.setLocationAndAngles((double)i, (double)j, (double)k, f1, 0.0F);
                  this.setMotion(Vec3d.ZERO);
               } else if (p_lambda$changeDimension$3_5_ && !serverworld1.getDefaultTeleporter().func_222268_a(this, f2)) {
                  serverworld1.getDefaultTeleporter().makePortal(this);
                  serverworld1.getDefaultTeleporter().func_222268_a(this, f2);
               }

               serverworld.getProfiler().endSection();
               this.setWorld(serverworld1);
               serverworld1.func_217447_b(this);
               this.func_213846_b(serverworld);
               this.connection.setPlayerLocation(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), f1, f);
               return this;
            });
            if (e != this) {
               throw new IllegalArgumentException(String.format("Teleporter %s returned not the player entity but instead %s, expected PlayerEntity %s", p_changeDimension_2_, e, this));
            } else {
               this.interactionManager.setWorld(serverworld1);
               this.connection.sendPacket(new SPlayerAbilitiesPacket(this.abilities));
               playerlist.sendWorldInfo(this, serverworld1);
               playerlist.sendInventory(this);
               Iterator var9 = this.getActivePotionEffects().iterator();

               while(var9.hasNext()) {
                  EffectInstance effectinstance = (EffectInstance)var9.next();
                  this.connection.sendPacket(new SPlayEntityEffectPacket(this.getEntityId(), effectinstance));
               }

               this.connection.sendPacket(new SPlaySoundEventPacket(1032, BlockPos.ZERO, 0, false));
               this.lastExperience = -1;
               this.lastHealth = -1.0F;
               this.lastFoodLevel = -1;
               BasicEventHooks.firePlayerChangedDimensionEvent(this, dimensiontype, p_changeDimension_1_);
               return this;
            }
         }
      }
   }

   private void func_213846_b(ServerWorld p_213846_1_) {
      DimensionType dimensiontype = p_213846_1_.dimension.getType();
      DimensionType dimensiontype1 = this.world.dimension.getType();
      CriteriaTriggers.CHANGED_DIMENSION.trigger(this, dimensiontype, dimensiontype1);
      if (dimensiontype == DimensionType.THE_NETHER && dimensiontype1 == DimensionType.OVERWORLD && this.enteredNetherPosition != null) {
         CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
      }

      if (dimensiontype1 != DimensionType.THE_NETHER) {
         this.enteredNetherPosition = null;
      }

   }

   public boolean isSpectatedByPlayer(ServerPlayerEntity p_174827_1_) {
      if (p_174827_1_.isSpectator()) {
         return this.getSpectatingEntity() == this;
      } else {
         return this.isSpectator() ? false : super.isSpectatedByPlayer(p_174827_1_);
      }
   }

   private void sendTileEntityUpdate(TileEntity p_147097_1_) {
      if (p_147097_1_ != null) {
         SUpdateTileEntityPacket supdatetileentitypacket = p_147097_1_.getUpdatePacket();
         if (supdatetileentitypacket != null) {
            this.connection.sendPacket(supdatetileentitypacket);
         }
      }

   }

   public void onItemPickup(Entity p_71001_1_, int p_71001_2_) {
      super.onItemPickup(p_71001_1_, p_71001_2_);
      this.openContainer.detectAndSendChanges();
   }

   public Either<PlayerEntity.SleepResult, Unit> trySleep(BlockPos p_213819_1_) {
      return super.trySleep(p_213819_1_).ifRight((p_lambda$trySleep$4_1_) -> {
         this.addStat(Stats.SLEEP_IN_BED);
         CriteriaTriggers.SLEPT_IN_BED.trigger(this);
      });
   }

   public void func_225652_a_(boolean p_225652_1_, boolean p_225652_2_) {
      if (this.isSleeping()) {
         this.getServerWorld().getChunkProvider().sendToTrackingAndSelf(this, new SAnimateHandPacket(this, 2));
      }

      super.func_225652_a_(p_225652_1_, p_225652_2_);
      if (this.connection != null) {
         this.connection.setPlayerLocation(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rotationYaw, this.rotationPitch);
      }

   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      Entity entity = this.getRidingEntity();
      if (!super.startRiding(p_184205_1_, p_184205_2_)) {
         return false;
      } else {
         Entity entity1 = this.getRidingEntity();
         if (entity1 != entity && this.connection != null) {
            this.connection.setPlayerLocation(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rotationYaw, this.rotationPitch);
         }

         return true;
      }
   }

   public void stopRiding() {
      Entity entity = this.getRidingEntity();
      super.stopRiding();
      Entity entity1 = this.getRidingEntity();
      if (entity1 != entity && this.connection != null) {
         this.connection.setPlayerLocation(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rotationYaw, this.rotationPitch);
      }

   }

   public boolean isInvulnerableTo(DamageSource p_180431_1_) {
      return super.isInvulnerableTo(p_180431_1_) || this.isInvulnerableDimensionChange() || this.abilities.disableDamage && p_180431_1_ == DamageSource.WITHER;
   }

   protected void updateFallState(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   protected void frostWalk(BlockPos p_184594_1_) {
      if (!this.isSpectator()) {
         super.frostWalk(p_184594_1_);
      }

   }

   public void handleFalling(double p_71122_1_, boolean p_71122_3_) {
      BlockPos blockpos = this.func_226268_ag_();
      if (this.world.isBlockLoaded(blockpos)) {
         BlockState blockstate = this.world.getBlockState(blockpos);
         super.updateFallState(p_71122_1_, p_71122_3_, blockstate, blockpos);
      }

   }

   public void openSignEditor(SignTileEntity p_175141_1_) {
      p_175141_1_.setPlayer(this);
      this.connection.sendPacket(new SOpenSignMenuPacket(p_175141_1_.getPos()));
   }

   public void getNextWindowId() {
      this.currentWindowId = this.currentWindowId % 100 + 1;
   }

   public OptionalInt openContainer(@Nullable INamedContainerProvider p_213829_1_) {
      if (p_213829_1_ == null) {
         return OptionalInt.empty();
      } else {
         if (this.openContainer != this.container) {
            this.closeScreen();
         }

         this.getNextWindowId();
         Container container = p_213829_1_.createMenu(this.currentWindowId, this.inventory, this);
         if (container == null) {
            if (this.isSpectator()) {
               this.sendStatusMessage((new TranslationTextComponent("container.spectatorCantOpen", new Object[0])).applyTextStyle(TextFormatting.RED), true);
            }

            return OptionalInt.empty();
         } else {
            this.connection.sendPacket(new SOpenWindowPacket(container.windowId, container.getType(), p_213829_1_.getDisplayName()));
            container.addListener(this);
            this.openContainer = container;
            MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(this, this.openContainer));
            return OptionalInt.of(this.currentWindowId);
         }
      }
   }

   public void func_213818_a(int p_213818_1_, MerchantOffers p_213818_2_, int p_213818_3_, int p_213818_4_, boolean p_213818_5_, boolean p_213818_6_) {
      this.connection.sendPacket(new SMerchantOffersPacket(p_213818_1_, p_213818_2_, p_213818_3_, p_213818_4_, p_213818_5_, p_213818_6_));
   }

   public void openHorseInventory(AbstractHorseEntity p_184826_1_, IInventory p_184826_2_) {
      if (this.openContainer != this.container) {
         this.closeScreen();
      }

      this.getNextWindowId();
      this.connection.sendPacket(new SOpenHorseWindowPacket(this.currentWindowId, p_184826_2_.getSizeInventory(), p_184826_1_.getEntityId()));
      this.openContainer = new HorseInventoryContainer(this.currentWindowId, this.inventory, p_184826_2_, p_184826_1_);
      this.openContainer.addListener(this);
      MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(this, this.openContainer));
   }

   public void openBook(ItemStack p_184814_1_, Hand p_184814_2_) {
      Item item = p_184814_1_.getItem();
      if (item == Items.WRITTEN_BOOK) {
         if (WrittenBookItem.resolveContents(p_184814_1_, this.getCommandSource(), this)) {
            this.openContainer.detectAndSendChanges();
         }

         this.connection.sendPacket(new SOpenBookWindowPacket(p_184814_2_));
      }

   }

   public void openCommandBlock(CommandBlockTileEntity p_184824_1_) {
      p_184824_1_.setSendToClient(true);
      this.sendTileEntityUpdate(p_184824_1_);
   }

   public void sendSlotContents(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
      if (!(p_71111_1_.getSlot(p_71111_2_) instanceof CraftingResultSlot)) {
         if (p_71111_1_ == this.container) {
            CriteriaTriggers.INVENTORY_CHANGED.trigger(this, this.inventory);
         }

         if (!this.isChangingQuantityOnly) {
            this.connection.sendPacket(new SSetSlotPacket(p_71111_1_.windowId, p_71111_2_, p_71111_3_));
         }
      }

   }

   public void sendContainerToPlayer(Container p_71120_1_) {
      this.sendAllContents(p_71120_1_, p_71120_1_.getInventory());
   }

   public void sendAllContents(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
      this.connection.sendPacket(new SWindowItemsPacket(p_71110_1_.windowId, p_71110_2_));
      this.connection.sendPacket(new SSetSlotPacket(-1, -1, this.inventory.getItemStack()));
   }

   public void sendWindowProperty(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
      this.connection.sendPacket(new SWindowPropertyPacket(p_71112_1_.windowId, p_71112_2_, p_71112_3_));
   }

   public void closeScreen() {
      this.connection.sendPacket(new SCloseWindowPacket(this.openContainer.windowId));
      this.closeContainer();
   }

   public void updateHeldItem() {
      if (!this.isChangingQuantityOnly) {
         this.connection.sendPacket(new SSetSlotPacket(-1, -1, this.inventory.getItemStack()));
      }

   }

   public void closeContainer() {
      this.openContainer.onContainerClosed(this);
      MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Close(this, this.openContainer));
      this.openContainer = this.container;
   }

   public void setEntityActionState(float p_110430_1_, float p_110430_2_, boolean p_110430_3_, boolean p_110430_4_) {
      if (this.isPassenger()) {
         if (p_110430_1_ >= -1.0F && p_110430_1_ <= 1.0F) {
            this.moveStrafing = p_110430_1_;
         }

         if (p_110430_2_ >= -1.0F && p_110430_2_ <= 1.0F) {
            this.moveForward = p_110430_2_;
         }

         this.isJumping = p_110430_3_;
         this.func_226284_e_(p_110430_4_);
      }

   }

   public void addStat(Stat<?> p_71064_1_, int p_71064_2_) {
      this.stats.increment(this, p_71064_1_, p_71064_2_);
      this.getWorldScoreboard().forAllObjectives(p_71064_1_, this.getScoreboardName(), (p_lambda$addStat$5_1_) -> {
         p_lambda$addStat$5_1_.increaseScore(p_71064_2_);
      });
   }

   public void takeStat(Stat<?> p_175145_1_) {
      this.stats.setValue(this, p_175145_1_, 0);
      this.getWorldScoreboard().forAllObjectives(p_175145_1_, this.getScoreboardName(), Score::reset);
   }

   public int unlockRecipes(Collection<IRecipe<?>> p_195065_1_) {
      return this.recipeBook.add(p_195065_1_, this);
   }

   public void unlockRecipes(ResourceLocation[] p_193102_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();
      ResourceLocation[] var3 = p_193102_1_;
      int var4 = p_193102_1_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ResourceLocation resourcelocation = var3[var5];
         this.server.getRecipeManager().getRecipe(resourcelocation).ifPresent(list::add);
      }

      this.unlockRecipes((Collection)list);
   }

   public int resetRecipes(Collection<IRecipe<?>> p_195069_1_) {
      return this.recipeBook.remove(p_195069_1_, this);
   }

   public void giveExperiencePoints(int p_195068_1_) {
      super.giveExperiencePoints(p_195068_1_);
      this.lastExperience = -1;
   }

   public void disconnect() {
      this.disconnected = true;
      this.removePassengers();
      if (this.isSleeping()) {
         this.func_225652_a_(true, false);
      }

   }

   public boolean hasDisconnected() {
      return this.disconnected;
   }

   public void setPlayerHealthUpdated() {
      this.lastHealth = -1.0E8F;
   }

   public void sendStatusMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
      this.connection.sendPacket(new SChatPacket(p_146105_1_, p_146105_2_ ? ChatType.GAME_INFO : ChatType.CHAT));
   }

   protected void onItemUseFinish() {
      if (!this.activeItemStack.isEmpty() && this.isHandActive()) {
         this.connection.sendPacket(new SEntityStatusPacket(this, (byte)9));
         super.onItemUseFinish();
      }

   }

   public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
      super.lookAt(p_200602_1_, p_200602_2_);
      this.connection.sendPacket(new SPlayerLookPacket(p_200602_1_, p_200602_2_.x, p_200602_2_.y, p_200602_2_.z));
   }

   public void lookAt(EntityAnchorArgument.Type p_200618_1_, Entity p_200618_2_, EntityAnchorArgument.Type p_200618_3_) {
      Vec3d vec3d = p_200618_3_.apply(p_200618_2_);
      super.lookAt(p_200618_1_, vec3d);
      this.connection.sendPacket(new SPlayerLookPacket(p_200618_1_, p_200618_2_, p_200618_3_));
   }

   public void copyFrom(ServerPlayerEntity p_193104_1_, boolean p_193104_2_) {
      if (p_193104_2_) {
         this.inventory.copyInventory(p_193104_1_.inventory);
         this.setHealth(p_193104_1_.getHealth());
         this.foodStats = p_193104_1_.foodStats;
         this.experienceLevel = p_193104_1_.experienceLevel;
         this.experienceTotal = p_193104_1_.experienceTotal;
         this.experience = p_193104_1_.experience;
         this.setScore(p_193104_1_.getScore());
         this.lastPortalPos = p_193104_1_.lastPortalPos;
         this.lastPortalVec = p_193104_1_.lastPortalVec;
         this.teleportDirection = p_193104_1_.teleportDirection;
      } else if (this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || p_193104_1_.isSpectator()) {
         this.inventory.copyInventory(p_193104_1_.inventory);
         this.experienceLevel = p_193104_1_.experienceLevel;
         this.experienceTotal = p_193104_1_.experienceTotal;
         this.experience = p_193104_1_.experience;
         this.setScore(p_193104_1_.getScore());
      }

      this.xpSeed = p_193104_1_.xpSeed;
      this.enterChestInventory = p_193104_1_.enterChestInventory;
      this.getDataManager().set(PLAYER_MODEL_FLAG, p_193104_1_.getDataManager().get(PLAYER_MODEL_FLAG));
      this.lastExperience = -1;
      this.lastHealth = -1.0F;
      this.lastFoodLevel = -1;
      this.recipeBook.copyFrom(p_193104_1_.recipeBook);
      this.entityRemoveQueue.addAll(p_193104_1_.entityRemoveQueue);
      this.seenCredits = p_193104_1_.seenCredits;
      this.enteredNetherPosition = p_193104_1_.enteredNetherPosition;
      this.setLeftShoulderEntity(p_193104_1_.getLeftShoulderEntity());
      this.setRightShoulderEntity(p_193104_1_.getRightShoulderEntity());
      this.spawnPosMap = p_193104_1_.spawnPosMap;
      this.spawnForcedMap = p_193104_1_.spawnForcedMap;
      if (p_193104_1_.dimension != DimensionType.OVERWORLD) {
         this.spawnPos = p_193104_1_.spawnPos;
         this.spawnForced = p_193104_1_.spawnForced;
      }

      CompoundNBT old = p_193104_1_.getPersistentData();
      if (old.contains("PlayerPersisted")) {
         this.getPersistentData().put("PlayerPersisted", old.get("PlayerPersisted"));
      }

      ForgeEventFactory.onPlayerClone(this, p_193104_1_, !p_193104_2_);
   }

   protected void onNewPotionEffect(EffectInstance p_70670_1_) {
      super.onNewPotionEffect(p_70670_1_);
      this.connection.sendPacket(new SPlayEntityEffectPacket(this.getEntityId(), p_70670_1_));
      if (p_70670_1_.getPotion() == Effects.LEVITATION) {
         this.levitatingSince = this.ticksExisted;
         this.levitationStartPos = this.getPositionVec();
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onChangedPotionEffect(EffectInstance p_70695_1_, boolean p_70695_2_) {
      super.onChangedPotionEffect(p_70695_1_, p_70695_2_);
      this.connection.sendPacket(new SPlayEntityEffectPacket(this.getEntityId(), p_70695_1_));
      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onFinishedPotionEffect(EffectInstance p_70688_1_) {
      super.onFinishedPotionEffect(p_70688_1_);
      this.connection.sendPacket(new SRemoveEntityEffectPacket(this.getEntityId(), p_70688_1_.getPotion()));
      if (p_70688_1_.getPotion() == Effects.LEVITATION) {
         this.levitationStartPos = null;
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   public void setPositionAndUpdate(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
      this.connection.setPlayerLocation(p_70634_1_, p_70634_3_, p_70634_5_, this.rotationYaw, this.rotationPitch);
   }

   public void func_225653_b_(double p_225653_1_, double p_225653_3_, double p_225653_5_) {
      this.connection.setPlayerLocation(p_225653_1_, p_225653_3_, p_225653_5_, this.rotationYaw, this.rotationPitch);
      this.connection.captureCurrentPosition();
   }

   public void onCriticalHit(Entity p_71009_1_) {
      this.getServerWorld().getChunkProvider().sendToTrackingAndSelf(this, new SAnimateHandPacket(p_71009_1_, 4));
   }

   public void onEnchantmentCritical(Entity p_71047_1_) {
      this.getServerWorld().getChunkProvider().sendToTrackingAndSelf(this, new SAnimateHandPacket(p_71047_1_, 5));
   }

   public void sendPlayerAbilities() {
      if (this.connection != null) {
         this.connection.sendPacket(new SPlayerAbilitiesPacket(this.abilities));
         this.updatePotionMetadata();
      }

   }

   public ServerWorld getServerWorld() {
      return (ServerWorld)this.world;
   }

   public void setGameType(GameType p_71033_1_) {
      this.interactionManager.setGameType(p_71033_1_);
      this.connection.sendPacket(new SChangeGameStatePacket(3, (float)p_71033_1_.getID()));
      if (p_71033_1_ == GameType.SPECTATOR) {
         this.spawnShoulderEntities();
         this.stopRiding();
      } else {
         this.setSpectatingEntity(this);
      }

      this.sendPlayerAbilities();
      this.markPotionsDirty();
   }

   public boolean isSpectator() {
      return this.interactionManager.getGameType() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      return this.interactionManager.getGameType() == GameType.CREATIVE;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      this.sendMessage(p_145747_1_, ChatType.SYSTEM);
   }

   public void sendMessage(ITextComponent p_195395_1_, ChatType p_195395_2_) {
      this.connection.sendPacket(new SChatPacket(p_195395_1_, p_195395_2_), (p_lambda$sendMessage$6_3_) -> {
         if (!p_lambda$sendMessage$6_3_.isSuccess() && (p_195395_2_ == ChatType.GAME_INFO || p_195395_2_ == ChatType.SYSTEM)) {
            int i = true;
            String s = p_195395_1_.getStringTruncated(256);
            ITextComponent itextcomponent = (new StringTextComponent(s)).applyTextStyle(TextFormatting.YELLOW);
            this.connection.sendPacket(new SChatPacket((new TranslationTextComponent("multiplayer.message_not_delivered", new Object[]{itextcomponent})).applyTextStyle(TextFormatting.RED), ChatType.SYSTEM));
         }

      });
   }

   public String getPlayerIP() {
      String s = this.connection.netManager.getRemoteAddress().toString();
      s = s.substring(s.indexOf("/") + 1);
      s = s.substring(0, s.indexOf(":"));
      return s;
   }

   public void handleClientSettings(CClientSettingsPacket p_147100_1_) {
      this.language = p_147100_1_.getLang();
      this.chatVisibility = p_147100_1_.getChatVisibility();
      this.chatColours = p_147100_1_.isColorsEnabled();
      this.getDataManager().set(PLAYER_MODEL_FLAG, (byte)p_147100_1_.getModelPartFlags());
      this.getDataManager().set(MAIN_HAND, (byte)(p_147100_1_.getMainHand() == HandSide.LEFT ? 0 : 1));
   }

   public ChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public void loadResourcePack(String p_175397_1_, String p_175397_2_) {
      this.connection.sendPacket(new SSendResourcePackPacket(p_175397_1_, p_175397_2_));
   }

   protected int getPermissionLevel() {
      return this.server.getPermissionLevel(this.getGameProfile());
   }

   public void markPlayerActive() {
      this.playerLastActiveTime = Util.milliTime();
   }

   public ServerStatisticsManager getStats() {
      return this.stats;
   }

   public ServerRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void removeEntity(Entity p_152339_1_) {
      if (p_152339_1_ instanceof PlayerEntity) {
         this.connection.sendPacket(new SDestroyEntitiesPacket(new int[]{p_152339_1_.getEntityId()}));
      } else {
         this.entityRemoveQueue.add(p_152339_1_.getEntityId());
      }

   }

   public void addEntity(Entity p_184848_1_) {
      this.entityRemoveQueue.remove(p_184848_1_.getEntityId());
   }

   protected void updatePotionMetadata() {
      if (this.isSpectator()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(true);
      } else {
         super.updatePotionMetadata();
      }

   }

   public Entity getSpectatingEntity() {
      return (Entity)(this.spectatingEntity == null ? this : this.spectatingEntity);
   }

   public void setSpectatingEntity(Entity p_175399_1_) {
      Entity entity = this.getSpectatingEntity();
      this.spectatingEntity = (Entity)(p_175399_1_ == null ? this : p_175399_1_);
      if (entity != this.spectatingEntity) {
         this.connection.sendPacket(new SCameraPacket(this.spectatingEntity));
         this.setPositionAndUpdate(this.spectatingEntity.func_226277_ct_(), this.spectatingEntity.func_226278_cu_(), this.spectatingEntity.func_226281_cx_());
      }

   }

   protected void decrementTimeUntilPortal() {
      if (this.timeUntilPortal > 0 && !this.invulnerableDimensionChange) {
         --this.timeUntilPortal;
      }

   }

   public void attackTargetEntityWithCurrentItem(Entity p_71059_1_) {
      if (this.interactionManager.getGameType() == GameType.SPECTATOR) {
         this.setSpectatingEntity(p_71059_1_);
      } else {
         super.attackTargetEntityWithCurrentItem(p_71059_1_);
      }

   }

   public long getLastActiveTime() {
      return this.playerLastActiveTime;
   }

   @Nullable
   public ITextComponent getTabListDisplayName() {
      return null;
   }

   public void swingArm(Hand p_184609_1_) {
      super.swingArm(p_184609_1_);
      this.resetCooldown();
   }

   public boolean isInvulnerableDimensionChange() {
      return this.invulnerableDimensionChange;
   }

   public void clearInvulnerableDimensionChange() {
      this.invulnerableDimensionChange = false;
   }

   public PlayerAdvancements getAdvancements() {
      return this.advancements;
   }

   public void teleport(ServerWorld p_200619_1_, double p_200619_2_, double p_200619_4_, double p_200619_6_, float p_200619_8_, float p_200619_9_) {
      this.setSpectatingEntity(this);
      this.stopRiding();
      if (p_200619_1_ == this.world) {
         this.connection.setPlayerLocation(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
      } else if (ForgeHooks.onTravelToDimension(this, p_200619_1_.dimension.getType())) {
         DimensionType oldDimension = this.dimension;
         ServerWorld serverworld = this.getServerWorld();
         this.dimension = p_200619_1_.dimension.getType();
         WorldInfo worldinfo = p_200619_1_.getWorldInfo();
         NetworkHooks.sendDimensionDataPacket(this.connection.netManager, this);
         this.connection.sendPacket(new SRespawnPacket(this.dimension, WorldInfo.func_227498_c_(worldinfo.getSeed()), worldinfo.getGenerator(), this.interactionManager.getGameType()));
         this.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
         this.server.getPlayerList().updatePermissionLevel(this);
         serverworld.removePlayer(this, true);
         this.revive();
         this.setLocationAndAngles(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
         this.setWorld(p_200619_1_);
         p_200619_1_.func_217446_a(this);
         this.func_213846_b(serverworld);
         this.connection.setPlayerLocation(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
         this.interactionManager.setWorld(p_200619_1_);
         this.server.getPlayerList().sendWorldInfo(this, p_200619_1_);
         this.server.getPlayerList().sendInventory(this);
         BasicEventHooks.firePlayerChangedDimensionEvent(this, oldDimension, this.dimension);
      }

   }

   public void sendChunkLoad(ChunkPos p_213844_1_, IPacket<?> p_213844_2_, IPacket<?> p_213844_3_) {
      this.connection.sendPacket(p_213844_3_);
      this.connection.sendPacket(p_213844_2_);
   }

   public void sendChunkUnload(ChunkPos p_213845_1_) {
      if (this.isAlive()) {
         this.connection.sendPacket(new SUnloadChunkPacket(p_213845_1_.x, p_213845_1_.z));
      }

   }

   public SectionPos getManagedSectionPos() {
      return this.managedSectionPos;
   }

   public void setManagedSectionPos(SectionPos p_213850_1_) {
      this.managedSectionPos = p_213850_1_;
   }

   public void func_213823_a(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
      this.connection.sendPacket(new SPlaySoundEffectPacket(p_213823_1_, p_213823_2_, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), p_213823_3_, p_213823_4_));
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnPlayerPacket(this);
   }

   public ItemEntity dropItem(ItemStack p_146097_1_, boolean p_146097_2_, boolean p_146097_3_) {
      ItemEntity itementity = super.dropItem(p_146097_1_, p_146097_2_, p_146097_3_);
      if (itementity == null) {
         return null;
      } else {
         if (this.captureDrops() != null) {
            this.captureDrops().add(itementity);
         } else {
            this.world.addEntity(itementity);
         }

         ItemStack itemstack = itementity.getItem();
         if (p_146097_3_) {
            if (!itemstack.isEmpty()) {
               this.addStat(Stats.ITEM_DROPPED.get(itemstack.getItem()), p_146097_1_.getCount());
            }

            this.addStat(Stats.DROP);
         }

         return itementity;
      }
   }
}
