package net.minecraft.world.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class RaidManager extends WorldSavedData {
   private final Map<Integer, Raid> byId = Maps.newHashMap();
   private final ServerWorld world;
   private int nextAvailableId;
   private int tick;

   public RaidManager(ServerWorld p_i50142_1_) {
      super(func_215172_a(p_i50142_1_.dimension));
      this.world = p_i50142_1_;
      this.nextAvailableId = 1;
      this.markDirty();
   }

   public Raid func_215167_a(int p_215167_1_) {
      return (Raid)this.byId.get(p_215167_1_);
   }

   public void tick() {
      ++this.tick;
      Iterator lvt_1_1_ = this.byId.values().iterator();

      while(lvt_1_1_.hasNext()) {
         Raid lvt_2_1_ = (Raid)lvt_1_1_.next();
         if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
            lvt_2_1_.stop();
         }

         if (lvt_2_1_.isStopped()) {
            lvt_1_1_.remove();
            this.markDirty();
         } else {
            lvt_2_1_.tick();
         }
      }

      if (this.tick % 200 == 0) {
         this.markDirty();
      }

      DebugPacketSender.sendRaids(this.world, this.byId.values());
   }

   public static boolean func_215165_a(AbstractRaiderEntity p_215165_0_, Raid p_215165_1_) {
      if (p_215165_0_ != null && p_215165_1_ != null && p_215165_1_.getWorld() != null) {
         return p_215165_0_.isAlive() && p_215165_0_.func_213658_ej() && p_215165_0_.getIdleTime() <= 2400 && p_215165_0_.world.getDimension().getType() == p_215165_1_.getWorld().getDimension().getType();
      } else {
         return false;
      }
   }

   @Nullable
   public Raid badOmenTick(ServerPlayerEntity p_215170_1_) {
      if (p_215170_1_.isSpectator()) {
         return null;
      } else if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
         return null;
      } else {
         DimensionType lvt_2_1_ = p_215170_1_.world.getDimension().getType();
         if (lvt_2_1_ == DimensionType.THE_NETHER) {
            return null;
         } else {
            BlockPos lvt_3_1_ = new BlockPos(p_215170_1_);
            List<PointOfInterest> lvt_5_1_ = (List)this.world.func_217443_B().func_219146_b(PointOfInterestType.field_221053_a, lvt_3_1_, 64, PointOfInterestManager.Status.IS_OCCUPIED).collect(Collectors.toList());
            int lvt_6_1_ = 0;
            Vec3d lvt_7_1_ = Vec3d.ZERO;

            for(Iterator var8 = lvt_5_1_.iterator(); var8.hasNext(); ++lvt_6_1_) {
               PointOfInterest lvt_9_1_ = (PointOfInterest)var8.next();
               BlockPos lvt_10_1_ = lvt_9_1_.getPos();
               lvt_7_1_ = lvt_7_1_.add((double)lvt_10_1_.getX(), (double)lvt_10_1_.getY(), (double)lvt_10_1_.getZ());
            }

            BlockPos lvt_4_2_;
            if (lvt_6_1_ > 0) {
               lvt_7_1_ = lvt_7_1_.scale(1.0D / (double)lvt_6_1_);
               lvt_4_2_ = new BlockPos(lvt_7_1_);
            } else {
               lvt_4_2_ = lvt_3_1_;
            }

            Raid lvt_8_1_ = this.findOrCreateRaid(p_215170_1_.getServerWorld(), lvt_4_2_);
            boolean lvt_9_2_ = false;
            if (!lvt_8_1_.func_221301_k()) {
               if (!this.byId.containsKey(lvt_8_1_.getId())) {
                  this.byId.put(lvt_8_1_.getId(), lvt_8_1_);
               }

               lvt_9_2_ = true;
            } else if (lvt_8_1_.func_221291_n() < lvt_8_1_.getMaxLevel()) {
               lvt_9_2_ = true;
            } else {
               p_215170_1_.removePotionEffect(Effects.BAD_OMEN);
               p_215170_1_.connection.sendPacket(new SEntityStatusPacket(p_215170_1_, (byte)43));
            }

            if (lvt_9_2_) {
               lvt_8_1_.increaseLevel(p_215170_1_);
               p_215170_1_.connection.sendPacket(new SEntityStatusPacket(p_215170_1_, (byte)43));
               if (!lvt_8_1_.func_221297_c()) {
                  p_215170_1_.addStat(Stats.RAID_TRIGGER);
                  CriteriaTriggers.VOLUNTARY_EXILE.trigger(p_215170_1_);
               }
            }

            this.markDirty();
            return lvt_8_1_;
         }
      }
   }

   private Raid findOrCreateRaid(ServerWorld p_215168_1_, BlockPos p_215168_2_) {
      Raid lvt_3_1_ = p_215168_1_.findRaid(p_215168_2_);
      return lvt_3_1_ != null ? lvt_3_1_ : new Raid(this.incrementNextId(), p_215168_1_, p_215168_2_);
   }

   public void read(CompoundNBT p_76184_1_) {
      this.nextAvailableId = p_76184_1_.getInt("NextAvailableID");
      this.tick = p_76184_1_.getInt("Tick");
      ListNBT lvt_2_1_ = p_76184_1_.getList("Raids", 10);

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
         CompoundNBT lvt_4_1_ = lvt_2_1_.getCompound(lvt_3_1_);
         Raid lvt_5_1_ = new Raid(this.world, lvt_4_1_);
         this.byId.put(lvt_5_1_.getId(), lvt_5_1_);
      }

   }

   public CompoundNBT write(CompoundNBT p_189551_1_) {
      p_189551_1_.putInt("NextAvailableID", this.nextAvailableId);
      p_189551_1_.putInt("Tick", this.tick);
      ListNBT lvt_2_1_ = new ListNBT();
      Iterator var3 = this.byId.values().iterator();

      while(var3.hasNext()) {
         Raid lvt_4_1_ = (Raid)var3.next();
         CompoundNBT lvt_5_1_ = new CompoundNBT();
         lvt_4_1_.write(lvt_5_1_);
         lvt_2_1_.add(lvt_5_1_);
      }

      p_189551_1_.put("Raids", lvt_2_1_);
      return p_189551_1_;
   }

   public static String func_215172_a(Dimension p_215172_0_) {
      return "raids" + p_215172_0_.getType().getSuffix();
   }

   private int incrementNextId() {
      return ++this.nextAvailableId;
   }

   @Nullable
   public Raid findRaid(BlockPos p_215174_1_, int p_215174_2_) {
      Raid lvt_3_1_ = null;
      double lvt_4_1_ = (double)p_215174_2_;
      Iterator var6 = this.byId.values().iterator();

      while(var6.hasNext()) {
         Raid lvt_7_1_ = (Raid)var6.next();
         double lvt_8_1_ = lvt_7_1_.func_221304_t().distanceSq(p_215174_1_);
         if (lvt_7_1_.isActive() && lvt_8_1_ < lvt_4_1_) {
            lvt_3_1_ = lvt_7_1_;
            lvt_4_1_ = lvt_8_1_;
         }
      }

      return lvt_3_1_;
   }
}
