package net.minecraft.world.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class Raid {
   private static final TranslationTextComponent RAID = new TranslationTextComponent("event.minecraft.raid", new Object[0]);
   private static final TranslationTextComponent VICTORY = new TranslationTextComponent("event.minecraft.raid.victory", new Object[0]);
   private static final TranslationTextComponent DEFEAT = new TranslationTextComponent("event.minecraft.raid.defeat", new Object[0]);
   private static final ITextComponent RAID_VICTORY;
   private static final ITextComponent RAID_DEFEAT;
   private final Map<Integer, AbstractRaiderEntity> leaders = Maps.newHashMap();
   private final Map<Integer, Set<AbstractRaiderEntity>> raiders = Maps.newHashMap();
   private final Set<UUID> heroes = Sets.newHashSet();
   private long ticksActive;
   private BlockPos center;
   private final ServerWorld world;
   private boolean started;
   private final int id;
   private float totalHealth;
   private int badOmenLevel;
   private boolean active;
   private int groupsSpawned;
   private final ServerBossInfo bossInfo;
   private int postRaidTicks;
   private int preRaidTicks;
   private final Random random;
   private final int numGroups;
   private Raid.Status status;
   private int field_221361_y;
   private Optional<BlockPos> field_221362_z;

   public Raid(int p_i50144_1_, ServerWorld p_i50144_2_, BlockPos p_i50144_3_) {
      this.bossInfo = new ServerBossInfo(RAID, BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_10);
      this.random = new Random();
      this.field_221362_z = Optional.empty();
      this.id = p_i50144_1_;
      this.world = p_i50144_2_;
      this.active = true;
      this.preRaidTicks = 300;
      this.bossInfo.setPercent(0.0F);
      this.center = p_i50144_3_;
      this.numGroups = this.getWaves(p_i50144_2_.getDifficulty());
      this.status = Raid.Status.ONGOING;
   }

   public Raid(ServerWorld p_i50145_1_, CompoundNBT p_i50145_2_) {
      this.bossInfo = new ServerBossInfo(RAID, BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_10);
      this.random = new Random();
      this.field_221362_z = Optional.empty();
      this.world = p_i50145_1_;
      this.id = p_i50145_2_.getInt("Id");
      this.started = p_i50145_2_.getBoolean("Started");
      this.active = p_i50145_2_.getBoolean("Active");
      this.ticksActive = p_i50145_2_.getLong("TicksActive");
      this.badOmenLevel = p_i50145_2_.getInt("BadOmenLevel");
      this.groupsSpawned = p_i50145_2_.getInt("GroupsSpawned");
      this.preRaidTicks = p_i50145_2_.getInt("PreRaidTicks");
      this.postRaidTicks = p_i50145_2_.getInt("PostRaidTicks");
      this.totalHealth = p_i50145_2_.getFloat("TotalHealth");
      this.center = new BlockPos(p_i50145_2_.getInt("CX"), p_i50145_2_.getInt("CY"), p_i50145_2_.getInt("CZ"));
      this.numGroups = p_i50145_2_.getInt("NumGroups");
      this.status = Raid.Status.func_221275_b(p_i50145_2_.getString("Status"));
      this.heroes.clear();
      if (p_i50145_2_.contains("HeroesOfTheVillage", 9)) {
         ListNBT lvt_3_1_ = p_i50145_2_.getList("HeroesOfTheVillage", 10);

         for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.size(); ++lvt_4_1_) {
            CompoundNBT lvt_5_1_ = lvt_3_1_.getCompound(lvt_4_1_);
            UUID lvt_6_1_ = lvt_5_1_.getUniqueId("UUID");
            this.heroes.add(lvt_6_1_);
         }
      }

   }

   public boolean func_221319_a() {
      return this.isVictory() || this.isLoss();
   }

   public boolean func_221334_b() {
      return this.func_221297_c() && this.getRaiderCount() == 0 && this.preRaidTicks > 0;
   }

   public boolean func_221297_c() {
      return this.groupsSpawned > 0;
   }

   public boolean isStopped() {
      return this.status == Raid.Status.STOPPED;
   }

   public boolean isVictory() {
      return this.status == Raid.Status.VICTORY;
   }

   public boolean isLoss() {
      return this.status == Raid.Status.LOSS;
   }

   public World getWorld() {
      return this.world;
   }

   public boolean func_221301_k() {
      return this.started;
   }

   public int func_221315_l() {
      return this.groupsSpawned;
   }

   private Predicate<ServerPlayerEntity> getParticipantsPredicate() {
      return (p_221302_1_) -> {
         BlockPos lvt_2_1_ = new BlockPos(p_221302_1_);
         return p_221302_1_.isAlive() && this.world.findRaid(lvt_2_1_) == this;
      };
   }

   private void updateBossInfoVisibility() {
      Set<ServerPlayerEntity> lvt_1_1_ = Sets.newHashSet(this.bossInfo.getPlayers());
      List<ServerPlayerEntity> lvt_2_1_ = this.world.getPlayers(this.getParticipantsPredicate());
      Iterator var3 = lvt_2_1_.iterator();

      ServerPlayerEntity lvt_4_2_;
      while(var3.hasNext()) {
         lvt_4_2_ = (ServerPlayerEntity)var3.next();
         if (!lvt_1_1_.contains(lvt_4_2_)) {
            this.bossInfo.addPlayer(lvt_4_2_);
         }
      }

      var3 = lvt_1_1_.iterator();

      while(var3.hasNext()) {
         lvt_4_2_ = (ServerPlayerEntity)var3.next();
         if (!lvt_2_1_.contains(lvt_4_2_)) {
            this.bossInfo.removePlayer(lvt_4_2_);
         }
      }

   }

   public int getMaxLevel() {
      return 5;
   }

   public int func_221291_n() {
      return this.badOmenLevel;
   }

   public void increaseLevel(PlayerEntity p_221309_1_) {
      if (p_221309_1_.isPotionActive(Effects.BAD_OMEN)) {
         this.badOmenLevel += p_221309_1_.getActivePotionEffect(Effects.BAD_OMEN).getAmplifier() + 1;
         this.badOmenLevel = MathHelper.clamp(this.badOmenLevel, 0, this.getMaxLevel());
      }

      p_221309_1_.removePotionEffect(Effects.BAD_OMEN);
   }

   public void stop() {
      this.active = false;
      this.bossInfo.removeAllPlayers();
      this.status = Raid.Status.STOPPED;
   }

   public void tick() {
      if (!this.isStopped()) {
         if (this.status == Raid.Status.ONGOING) {
            boolean lvt_1_1_ = this.active;
            this.active = this.world.isBlockLoaded(this.center);
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
               this.stop();
               return;
            }

            if (lvt_1_1_ != this.active) {
               this.bossInfo.setVisible(this.active);
            }

            if (!this.active) {
               return;
            }

            if (!this.world.func_217483_b_(this.center)) {
               this.func_223027_y();
            }

            if (!this.world.func_217483_b_(this.center)) {
               if (this.groupsSpawned > 0) {
                  this.status = Raid.Status.LOSS;
               } else {
                  this.stop();
               }
            }

            ++this.ticksActive;
            if (this.ticksActive >= 48000L) {
               this.stop();
               return;
            }

            int lvt_2_1_ = this.getRaiderCount();
            boolean lvt_3_2_;
            if (lvt_2_1_ == 0 && this.func_221289_z()) {
               if (this.preRaidTicks <= 0) {
                  if (this.preRaidTicks == 0 && this.groupsSpawned > 0) {
                     this.preRaidTicks = 300;
                     this.bossInfo.setName(RAID);
                     return;
                  }
               } else {
                  lvt_3_2_ = this.field_221362_z.isPresent();
                  boolean lvt_4_1_ = !lvt_3_2_ && this.preRaidTicks % 5 == 0;
                  if (lvt_3_2_ && !this.world.getChunkProvider().isChunkLoaded(new ChunkPos((BlockPos)this.field_221362_z.get()))) {
                     lvt_4_1_ = true;
                  }

                  if (lvt_4_1_) {
                     int lvt_5_1_ = 0;
                     if (this.preRaidTicks < 100) {
                        lvt_5_1_ = 1;
                     } else if (this.preRaidTicks < 40) {
                        lvt_5_1_ = 2;
                     }

                     this.field_221362_z = this.func_221313_d(lvt_5_1_);
                  }

                  if (this.preRaidTicks == 300 || this.preRaidTicks % 20 == 0) {
                     this.updateBossInfoVisibility();
                  }

                  --this.preRaidTicks;
                  this.bossInfo.setPercent(MathHelper.clamp((float)(300 - this.preRaidTicks) / 300.0F, 0.0F, 1.0F));
               }
            }

            if (this.ticksActive % 20L == 0L) {
               this.updateBossInfoVisibility();
               this.func_221292_E();
               if (lvt_2_1_ > 0) {
                  if (lvt_2_1_ <= 2) {
                     this.bossInfo.setName(RAID.shallowCopy().appendText(" - ").appendSibling(new TranslationTextComponent("event.minecraft.raid.raiders_remaining", new Object[]{lvt_2_1_})));
                  } else {
                     this.bossInfo.setName(RAID);
                  }
               } else {
                  this.bossInfo.setName(RAID);
               }
            }

            lvt_3_2_ = false;
            int lvt_4_2_ = 0;

            while(this.func_221318_F()) {
               BlockPos lvt_5_2_ = this.field_221362_z.isPresent() ? (BlockPos)this.field_221362_z.get() : this.func_221298_a(lvt_4_2_, 20);
               if (lvt_5_2_ != null) {
                  this.started = true;
                  this.spawnNextWave(lvt_5_2_);
                  if (!lvt_3_2_) {
                     this.playWaveStartSound(lvt_5_2_);
                     lvt_3_2_ = true;
                  }
               } else {
                  ++lvt_4_2_;
               }

               if (lvt_4_2_ > 3) {
                  this.stop();
                  break;
               }
            }

            if (this.func_221301_k() && !this.func_221289_z() && lvt_2_1_ == 0) {
               if (this.postRaidTicks < 40) {
                  ++this.postRaidTicks;
               } else {
                  this.status = Raid.Status.VICTORY;
                  Iterator var12 = this.heroes.iterator();

                  while(var12.hasNext()) {
                     UUID lvt_6_1_ = (UUID)var12.next();
                     Entity lvt_7_1_ = this.world.getEntityByUuid(lvt_6_1_);
                     if (lvt_7_1_ instanceof LivingEntity && !lvt_7_1_.isSpectator()) {
                        LivingEntity lvt_8_1_ = (LivingEntity)lvt_7_1_;
                        lvt_8_1_.addPotionEffect(new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
                        if (lvt_8_1_ instanceof ServerPlayerEntity) {
                           ServerPlayerEntity lvt_9_1_ = (ServerPlayerEntity)lvt_8_1_;
                           lvt_9_1_.addStat(Stats.RAID_WIN);
                           CriteriaTriggers.HERO_OF_THE_VILLAGE.trigger(lvt_9_1_);
                        }
                     }
                  }
               }
            }

            this.markDirty();
         } else if (this.func_221319_a()) {
            ++this.field_221361_y;
            if (this.field_221361_y >= 600) {
               this.stop();
               return;
            }

            if (this.field_221361_y % 20 == 0) {
               this.updateBossInfoVisibility();
               this.bossInfo.setVisible(true);
               if (this.isVictory()) {
                  this.bossInfo.setPercent(0.0F);
                  this.bossInfo.setName(RAID_VICTORY);
               } else {
                  this.bossInfo.setName(RAID_DEFEAT);
               }
            }
         }

      }
   }

   private void func_223027_y() {
      Stream<SectionPos> lvt_1_1_ = SectionPos.getAllInBox(SectionPos.from(this.center), 2);
      ServerWorld var10001 = this.world;
      var10001.getClass();
      lvt_1_1_.filter(var10001::func_222887_a).map(SectionPos::getCenter).min(Comparator.comparingDouble((p_223025_1_) -> {
         return p_223025_1_.distanceSq(this.center);
      })).ifPresent(this::func_223024_c);
   }

   private Optional<BlockPos> func_221313_d(int p_221313_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < 3; ++lvt_2_1_) {
         BlockPos lvt_3_1_ = this.func_221298_a(p_221313_1_, 1);
         if (lvt_3_1_ != null) {
            return Optional.of(lvt_3_1_);
         }
      }

      return Optional.empty();
   }

   private boolean func_221289_z() {
      if (this.func_221328_B()) {
         return !this.func_221314_C();
      } else {
         return !this.func_221288_A();
      }
   }

   private boolean func_221288_A() {
      return this.func_221315_l() == this.numGroups;
   }

   private boolean func_221328_B() {
      return this.badOmenLevel > 1;
   }

   private boolean func_221314_C() {
      return this.func_221315_l() > this.numGroups;
   }

   private boolean func_221305_D() {
      return this.func_221288_A() && this.getRaiderCount() == 0 && this.func_221328_B();
   }

   private void func_221292_E() {
      Iterator<Set<AbstractRaiderEntity>> lvt_1_1_ = this.raiders.values().iterator();
      HashSet lvt_2_1_ = Sets.newHashSet();

      label54:
      while(lvt_1_1_.hasNext()) {
         Set<AbstractRaiderEntity> lvt_3_1_ = (Set)lvt_1_1_.next();
         Iterator var4 = lvt_3_1_.iterator();

         while(true) {
            while(true) {
               if (!var4.hasNext()) {
                  continue label54;
               }

               AbstractRaiderEntity lvt_5_1_ = (AbstractRaiderEntity)var4.next();
               BlockPos lvt_6_1_ = new BlockPos(lvt_5_1_);
               if (!lvt_5_1_.removed && lvt_5_1_.dimension == this.world.getDimension().getType() && this.center.distanceSq(lvt_6_1_) < 12544.0D) {
                  if (lvt_5_1_.ticksExisted > 600) {
                     if (this.world.getEntityByUuid(lvt_5_1_.getUniqueID()) == null) {
                        lvt_2_1_.add(lvt_5_1_);
                     }

                     if (!this.world.func_217483_b_(lvt_6_1_) && lvt_5_1_.getIdleTime() > 2400) {
                        lvt_5_1_.func_213653_b(lvt_5_1_.func_213661_eo() + 1);
                     }

                     if (lvt_5_1_.func_213661_eo() >= 30) {
                        lvt_2_1_.add(lvt_5_1_);
                     }
                  }
               } else {
                  lvt_2_1_.add(lvt_5_1_);
               }
            }
         }
      }

      Iterator var7 = lvt_2_1_.iterator();

      while(var7.hasNext()) {
         AbstractRaiderEntity lvt_4_1_ = (AbstractRaiderEntity)var7.next();
         this.leaveRaid(lvt_4_1_, true);
      }

   }

   private void playWaveStartSound(BlockPos p_221293_1_) {
      float lvt_2_1_ = 13.0F;
      int lvt_3_1_ = true;
      Collection<ServerPlayerEntity> lvt_4_1_ = this.bossInfo.getPlayers();
      Iterator var5 = this.world.getPlayers().iterator();

      while(true) {
         ServerPlayerEntity lvt_6_1_;
         float lvt_9_1_;
         double lvt_10_1_;
         double lvt_12_1_;
         do {
            if (!var5.hasNext()) {
               return;
            }

            lvt_6_1_ = (ServerPlayerEntity)var5.next();
            Vec3d lvt_7_1_ = lvt_6_1_.getPositionVec();
            Vec3d lvt_8_1_ = new Vec3d(p_221293_1_);
            lvt_9_1_ = MathHelper.sqrt((lvt_8_1_.x - lvt_7_1_.x) * (lvt_8_1_.x - lvt_7_1_.x) + (lvt_8_1_.z - lvt_7_1_.z) * (lvt_8_1_.z - lvt_7_1_.z));
            lvt_10_1_ = lvt_7_1_.x + (double)(13.0F / lvt_9_1_) * (lvt_8_1_.x - lvt_7_1_.x);
            lvt_12_1_ = lvt_7_1_.z + (double)(13.0F / lvt_9_1_) * (lvt_8_1_.z - lvt_7_1_.z);
         } while(lvt_9_1_ > 64.0F && !lvt_4_1_.contains(lvt_6_1_));

         lvt_6_1_.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.EVENT_RAID_HORN, SoundCategory.NEUTRAL, lvt_10_1_, lvt_6_1_.func_226278_cu_(), lvt_12_1_, 64.0F, 1.0F));
      }
   }

   private void spawnNextWave(BlockPos p_221294_1_) {
      boolean lvt_2_1_ = false;
      int lvt_3_1_ = this.groupsSpawned + 1;
      this.totalHealth = 0.0F;
      DifficultyInstance lvt_4_1_ = this.world.getDifficultyForLocation(p_221294_1_);
      boolean lvt_5_1_ = this.func_221305_D();
      Raid.WaveMember[] var6 = Raid.WaveMember.VALUES;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Raid.WaveMember lvt_9_1_ = var6[var8];
         int lvt_10_1_ = this.func_221330_a(lvt_9_1_, lvt_3_1_, lvt_5_1_) + this.func_221335_a(lvt_9_1_, this.random, lvt_3_1_, lvt_4_1_, lvt_5_1_);
         int lvt_11_1_ = 0;

         for(int lvt_12_1_ = 0; lvt_12_1_ < lvt_10_1_; ++lvt_12_1_) {
            AbstractRaiderEntity lvt_13_1_ = (AbstractRaiderEntity)lvt_9_1_.type.create(this.world);
            if (!lvt_2_1_ && lvt_13_1_.canBeLeader()) {
               lvt_13_1_.setLeader(true);
               this.setLeader(lvt_3_1_, lvt_13_1_);
               lvt_2_1_ = true;
            }

            this.func_221317_a(lvt_3_1_, lvt_13_1_, p_221294_1_, false);
            if (lvt_9_1_.type == EntityType.RAVAGER) {
               AbstractRaiderEntity lvt_14_1_ = null;
               if (lvt_3_1_ == this.getWaves(Difficulty.NORMAL)) {
                  lvt_14_1_ = (AbstractRaiderEntity)EntityType.PILLAGER.create(this.world);
               } else if (lvt_3_1_ >= this.getWaves(Difficulty.HARD)) {
                  if (lvt_11_1_ == 0) {
                     lvt_14_1_ = (AbstractRaiderEntity)EntityType.EVOKER.create(this.world);
                  } else {
                     lvt_14_1_ = (AbstractRaiderEntity)EntityType.VINDICATOR.create(this.world);
                  }
               }

               ++lvt_11_1_;
               if (lvt_14_1_ != null) {
                  this.func_221317_a(lvt_3_1_, lvt_14_1_, p_221294_1_, false);
                  lvt_14_1_.moveToBlockPosAndAngles(p_221294_1_, 0.0F, 0.0F);
                  lvt_14_1_.startRiding(lvt_13_1_);
               }
            }
         }
      }

      this.field_221362_z = Optional.empty();
      ++this.groupsSpawned;
      this.updateBarPercentage();
      this.markDirty();
   }

   public void func_221317_a(int p_221317_1_, AbstractRaiderEntity p_221317_2_, @Nullable BlockPos p_221317_3_, boolean p_221317_4_) {
      boolean lvt_5_1_ = this.joinRaid(p_221317_1_, p_221317_2_);
      if (lvt_5_1_) {
         p_221317_2_.setRaid(this);
         p_221317_2_.setWave(p_221317_1_);
         p_221317_2_.func_213644_t(true);
         p_221317_2_.func_213653_b(0);
         if (!p_221317_4_ && p_221317_3_ != null) {
            p_221317_2_.setPosition((double)p_221317_3_.getX() + 0.5D, (double)p_221317_3_.getY() + 1.0D, (double)p_221317_3_.getZ() + 0.5D);
            p_221317_2_.onInitialSpawn(this.world, this.world.getDifficultyForLocation(p_221317_3_), SpawnReason.EVENT, (ILivingEntityData)null, (CompoundNBT)null);
            p_221317_2_.func_213660_a(p_221317_1_, false);
            p_221317_2_.onGround = true;
            this.world.addEntity(p_221317_2_);
         }
      }

   }

   public void updateBarPercentage() {
      this.bossInfo.setPercent(MathHelper.clamp(this.getCurrentHealth() / this.totalHealth, 0.0F, 1.0F));
   }

   public float getCurrentHealth() {
      float lvt_1_1_ = 0.0F;
      Iterator var2 = this.raiders.values().iterator();

      while(var2.hasNext()) {
         Set<AbstractRaiderEntity> lvt_3_1_ = (Set)var2.next();

         AbstractRaiderEntity lvt_5_1_;
         for(Iterator var4 = lvt_3_1_.iterator(); var4.hasNext(); lvt_1_1_ += lvt_5_1_.getHealth()) {
            lvt_5_1_ = (AbstractRaiderEntity)var4.next();
         }
      }

      return lvt_1_1_;
   }

   private boolean func_221318_F() {
      return this.preRaidTicks == 0 && (this.groupsSpawned < this.numGroups || this.func_221305_D()) && this.getRaiderCount() == 0;
   }

   public int getRaiderCount() {
      return this.raiders.values().stream().mapToInt(Set::size).sum();
   }

   public void leaveRaid(AbstractRaiderEntity p_221322_1_, boolean p_221322_2_) {
      Set<AbstractRaiderEntity> lvt_3_1_ = (Set)this.raiders.get(p_221322_1_.func_213642_em());
      if (lvt_3_1_ != null) {
         boolean lvt_4_1_ = lvt_3_1_.remove(p_221322_1_);
         if (lvt_4_1_) {
            if (p_221322_2_) {
               this.totalHealth -= p_221322_1_.getHealth();
            }

            p_221322_1_.setRaid((Raid)null);
            this.updateBarPercentage();
            this.markDirty();
         }
      }

   }

   private void markDirty() {
      this.world.getRaids().markDirty();
   }

   public static ItemStack createIllagerBanner() {
      ItemStack lvt_0_1_ = new ItemStack(Items.WHITE_BANNER);
      CompoundNBT lvt_1_1_ = lvt_0_1_.getOrCreateChildTag("BlockEntityTag");
      ListNBT lvt_2_1_ = (new BannerPattern.Builder()).func_222477_a(BannerPattern.RHOMBUS_MIDDLE, DyeColor.CYAN).func_222477_a(BannerPattern.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY).func_222477_a(BannerPattern.STRIPE_CENTER, DyeColor.GRAY).func_222477_a(BannerPattern.BORDER, DyeColor.LIGHT_GRAY).func_222477_a(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK).func_222477_a(BannerPattern.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY).func_222477_a(BannerPattern.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY).func_222477_a(BannerPattern.BORDER, DyeColor.BLACK).func_222476_a();
      lvt_1_1_.put("Patterns", lvt_2_1_);
      lvt_0_1_.setDisplayName((new TranslationTextComponent("block.minecraft.ominous_banner", new Object[0])).applyTextStyle(TextFormatting.GOLD));
      return lvt_0_1_;
   }

   @Nullable
   public AbstractRaiderEntity getLeader(int p_221332_1_) {
      return (AbstractRaiderEntity)this.leaders.get(p_221332_1_);
   }

   @Nullable
   private BlockPos func_221298_a(int p_221298_1_, int p_221298_2_) {
      int lvt_3_1_ = p_221298_1_ == 0 ? 2 : 2 - p_221298_1_;
      BlockPos.Mutable lvt_7_1_ = new BlockPos.Mutable();

      for(int lvt_8_1_ = 0; lvt_8_1_ < p_221298_2_; ++lvt_8_1_) {
         float lvt_9_1_ = this.world.rand.nextFloat() * 6.2831855F;
         int lvt_4_1_ = this.center.getX() + MathHelper.floor(MathHelper.cos(lvt_9_1_) * 32.0F * (float)lvt_3_1_) + this.world.rand.nextInt(5);
         int lvt_6_1_ = this.center.getZ() + MathHelper.floor(MathHelper.sin(lvt_9_1_) * 32.0F * (float)lvt_3_1_) + this.world.rand.nextInt(5);
         int lvt_5_1_ = this.world.getHeight(Heightmap.Type.WORLD_SURFACE, lvt_4_1_, lvt_6_1_);
         lvt_7_1_.setPos(lvt_4_1_, lvt_5_1_, lvt_6_1_);
         if ((!this.world.func_217483_b_(lvt_7_1_) || p_221298_1_ >= 2) && this.world.isAreaLoaded(lvt_7_1_.getX() - 10, lvt_7_1_.getY() - 10, lvt_7_1_.getZ() - 10, lvt_7_1_.getX() + 10, lvt_7_1_.getY() + 10, lvt_7_1_.getZ() + 10) && this.world.getChunkProvider().isChunkLoaded(new ChunkPos(lvt_7_1_)) && (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, this.world, lvt_7_1_, EntityType.RAVAGER) || this.world.getBlockState(lvt_7_1_.down()).getBlock() == Blocks.SNOW && this.world.getBlockState(lvt_7_1_).isAir())) {
            return lvt_7_1_;
         }
      }

      return null;
   }

   private boolean joinRaid(int p_221287_1_, AbstractRaiderEntity p_221287_2_) {
      return this.joinRaid(p_221287_1_, p_221287_2_, true);
   }

   public boolean joinRaid(int p_221300_1_, AbstractRaiderEntity p_221300_2_, boolean p_221300_3_) {
      this.raiders.computeIfAbsent(p_221300_1_, (p_221323_0_) -> {
         return Sets.newHashSet();
      });
      Set<AbstractRaiderEntity> lvt_4_1_ = (Set)this.raiders.get(p_221300_1_);
      AbstractRaiderEntity lvt_5_1_ = null;
      Iterator var6 = lvt_4_1_.iterator();

      while(var6.hasNext()) {
         AbstractRaiderEntity lvt_7_1_ = (AbstractRaiderEntity)var6.next();
         if (lvt_7_1_.getUniqueID().equals(p_221300_2_.getUniqueID())) {
            lvt_5_1_ = lvt_7_1_;
            break;
         }
      }

      if (lvt_5_1_ != null) {
         lvt_4_1_.remove(lvt_5_1_);
         lvt_4_1_.add(p_221300_2_);
      }

      lvt_4_1_.add(p_221300_2_);
      if (p_221300_3_) {
         this.totalHealth += p_221300_2_.getHealth();
      }

      this.updateBarPercentage();
      this.markDirty();
      return true;
   }

   public void setLeader(int p_221324_1_, AbstractRaiderEntity p_221324_2_) {
      this.leaders.put(p_221324_1_, p_221324_2_);
      p_221324_2_.setItemStackToSlot(EquipmentSlotType.HEAD, createIllagerBanner());
      p_221324_2_.setDropChance(EquipmentSlotType.HEAD, 2.0F);
   }

   public void removeLeader(int p_221296_1_) {
      this.leaders.remove(p_221296_1_);
   }

   public BlockPos func_221304_t() {
      return this.center;
   }

   private void func_223024_c(BlockPos p_223024_1_) {
      this.center = p_223024_1_;
   }

   public int getId() {
      return this.id;
   }

   private int func_221330_a(Raid.WaveMember p_221330_1_, int p_221330_2_, boolean p_221330_3_) {
      return p_221330_3_ ? p_221330_1_.waveCounts[this.numGroups] : p_221330_1_.waveCounts[p_221330_2_];
   }

   private int func_221335_a(Raid.WaveMember p_221335_1_, Random p_221335_2_, int p_221335_3_, DifficultyInstance p_221335_4_, boolean p_221335_5_) {
      Difficulty lvt_6_1_ = p_221335_4_.getDifficulty();
      boolean lvt_7_1_ = lvt_6_1_ == Difficulty.EASY;
      boolean lvt_8_1_ = lvt_6_1_ == Difficulty.NORMAL;
      int lvt_9_6_;
      switch(p_221335_1_) {
      case WITCH:
         if (!lvt_7_1_ && p_221335_3_ > 2 && p_221335_3_ != 4) {
            lvt_9_6_ = 1;
            break;
         }

         return 0;
      case PILLAGER:
      case VINDICATOR:
         if (lvt_7_1_) {
            lvt_9_6_ = p_221335_2_.nextInt(2);
         } else if (lvt_8_1_) {
            lvt_9_6_ = 1;
         } else {
            lvt_9_6_ = 2;
         }
         break;
      case RAVAGER:
         lvt_9_6_ = !lvt_7_1_ && p_221335_5_ ? 1 : 0;
         break;
      default:
         return 0;
      }

      return lvt_9_6_ > 0 ? p_221335_2_.nextInt(lvt_9_6_ + 1) : 0;
   }

   public boolean isActive() {
      return this.active;
   }

   public CompoundNBT write(CompoundNBT p_221326_1_) {
      p_221326_1_.putInt("Id", this.id);
      p_221326_1_.putBoolean("Started", this.started);
      p_221326_1_.putBoolean("Active", this.active);
      p_221326_1_.putLong("TicksActive", this.ticksActive);
      p_221326_1_.putInt("BadOmenLevel", this.badOmenLevel);
      p_221326_1_.putInt("GroupsSpawned", this.groupsSpawned);
      p_221326_1_.putInt("PreRaidTicks", this.preRaidTicks);
      p_221326_1_.putInt("PostRaidTicks", this.postRaidTicks);
      p_221326_1_.putFloat("TotalHealth", this.totalHealth);
      p_221326_1_.putInt("NumGroups", this.numGroups);
      p_221326_1_.putString("Status", this.status.func_221277_a());
      p_221326_1_.putInt("CX", this.center.getX());
      p_221326_1_.putInt("CY", this.center.getY());
      p_221326_1_.putInt("CZ", this.center.getZ());
      ListNBT lvt_2_1_ = new ListNBT();
      Iterator var3 = this.heroes.iterator();

      while(var3.hasNext()) {
         UUID lvt_4_1_ = (UUID)var3.next();
         CompoundNBT lvt_5_1_ = new CompoundNBT();
         lvt_5_1_.putUniqueId("UUID", lvt_4_1_);
         lvt_2_1_.add(lvt_5_1_);
      }

      p_221326_1_.put("HeroesOfTheVillage", lvt_2_1_);
      return p_221326_1_;
   }

   public int getWaves(Difficulty p_221306_1_) {
      switch(p_221306_1_) {
      case EASY:
         return 3;
      case NORMAL:
         return 5;
      case HARD:
         return 7;
      default:
         return 0;
      }
   }

   public float func_221308_w() {
      int lvt_1_1_ = this.func_221291_n();
      if (lvt_1_1_ == 2) {
         return 0.1F;
      } else if (lvt_1_1_ == 3) {
         return 0.25F;
      } else if (lvt_1_1_ == 4) {
         return 0.5F;
      } else {
         return lvt_1_1_ == 5 ? 0.75F : 0.0F;
      }
   }

   public void addHero(Entity p_221311_1_) {
      this.heroes.add(p_221311_1_.getUniqueID());
   }

   static {
      RAID_VICTORY = RAID.shallowCopy().appendText(" - ").appendSibling(VICTORY);
      RAID_DEFEAT = RAID.shallowCopy().appendText(" - ").appendSibling(DEFEAT);
   }

   static enum WaveMember {
      VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
      EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
      PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
      WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
      RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

      private static final Raid.WaveMember[] VALUES = values();
      private final EntityType<? extends AbstractRaiderEntity> type;
      private final int[] waveCounts;

      private WaveMember(EntityType<? extends AbstractRaiderEntity> p_i50602_3_, int[] p_i50602_4_) {
         this.type = p_i50602_3_;
         this.waveCounts = p_i50602_4_;
      }
   }

   static enum Status {
      ONGOING,
      VICTORY,
      LOSS,
      STOPPED;

      private static final Raid.Status[] field_221278_e = values();

      private static Raid.Status func_221275_b(String p_221275_0_) {
         Raid.Status[] var1 = field_221278_e;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Raid.Status lvt_4_1_ = var1[var3];
            if (p_221275_0_.equalsIgnoreCase(lvt_4_1_.name())) {
               return lvt_4_1_;
            }
         }

         return ONGOING;
      }

      public String func_221277_a() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
