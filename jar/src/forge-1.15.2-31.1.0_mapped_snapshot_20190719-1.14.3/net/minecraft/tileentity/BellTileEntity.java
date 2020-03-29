package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BellTileEntity extends TileEntity implements ITickableTileEntity {
   private long ringTime;
   public int field_213943_a;
   public boolean field_213944_b;
   public Direction field_213945_c;
   private List<LivingEntity> entitiesAtRing;
   private boolean field_213948_i;
   private int field_213949_j;

   public BellTileEntity() {
      super(TileEntityType.BELL);
   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.func_213941_c();
         this.field_213949_j = 0;
         this.field_213945_c = Direction.byIndex(p_145842_2_);
         this.field_213943_a = 0;
         this.field_213944_b = true;
         return true;
      } else {
         return super.receiveClientEvent(p_145842_1_, p_145842_2_);
      }
   }

   public void tick() {
      if (this.field_213944_b) {
         ++this.field_213943_a;
      }

      if (this.field_213943_a >= 50) {
         this.field_213944_b = false;
         this.field_213943_a = 0;
      }

      if (this.field_213943_a >= 5 && this.field_213949_j == 0 && this.hasRaidersNearby()) {
         this.field_213948_i = true;
         this.func_222833_c();
      }

      if (this.field_213948_i) {
         if (this.field_213949_j < 40) {
            ++this.field_213949_j;
         } else {
            this.func_222828_b(this.world);
            this.func_222826_c(this.world);
            this.field_213948_i = false;
         }
      }

   }

   private void func_222833_c() {
      this.world.playSound((PlayerEntity)null, this.getPos(), SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   public void func_213939_a(Direction p_213939_1_) {
      BlockPos lvt_2_1_ = this.getPos();
      this.field_213945_c = p_213939_1_;
      if (this.field_213944_b) {
         this.field_213943_a = 0;
      } else {
         this.field_213944_b = true;
      }

      this.world.addBlockEvent(lvt_2_1_, this.getBlockState().getBlock(), 1, p_213939_1_.getIndex());
   }

   private void func_213941_c() {
      BlockPos lvt_1_1_ = this.getPos();
      if (this.world.getGameTime() > this.ringTime + 60L || this.entitiesAtRing == null) {
         this.ringTime = this.world.getGameTime();
         AxisAlignedBB lvt_2_1_ = (new AxisAlignedBB(lvt_1_1_)).grow(48.0D);
         this.entitiesAtRing = this.world.getEntitiesWithinAABB(LivingEntity.class, lvt_2_1_);
      }

      if (!this.world.isRemote) {
         Iterator var4 = this.entitiesAtRing.iterator();

         while(var4.hasNext()) {
            LivingEntity lvt_3_1_ = (LivingEntity)var4.next();
            if (lvt_3_1_.isAlive() && !lvt_3_1_.removed && lvt_1_1_.withinDistance(lvt_3_1_.getPositionVec(), 32.0D)) {
               lvt_3_1_.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, (Object)this.world.getGameTime());
            }
         }
      }

   }

   private boolean hasRaidersNearby() {
      BlockPos lvt_1_1_ = this.getPos();
      Iterator var2 = this.entitiesAtRing.iterator();

      LivingEntity lvt_3_1_;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         lvt_3_1_ = (LivingEntity)var2.next();
      } while(!lvt_3_1_.isAlive() || lvt_3_1_.removed || !lvt_1_1_.withinDistance(lvt_3_1_.getPositionVec(), 32.0D) || !lvt_3_1_.getType().isContained(EntityTypeTags.RAIDERS));

      return true;
   }

   private void func_222828_b(World p_222828_1_) {
      if (!p_222828_1_.isRemote) {
         this.entitiesAtRing.stream().filter(this::isNearbyRaider).forEach(this::glow);
      }
   }

   private void func_222826_c(World p_222826_1_) {
      if (p_222826_1_.isRemote) {
         BlockPos lvt_2_1_ = this.getPos();
         AtomicInteger lvt_3_1_ = new AtomicInteger(16700985);
         int lvt_4_1_ = (int)this.entitiesAtRing.stream().filter((p_222829_1_) -> {
            return lvt_2_1_.withinDistance(p_222829_1_.getPositionVec(), 48.0D);
         }).count();
         this.entitiesAtRing.stream().filter(this::isNearbyRaider).forEach((p_222831_4_) -> {
            float lvt_5_1_ = 1.0F;
            float lvt_6_1_ = MathHelper.sqrt((p_222831_4_.func_226277_ct_() - (double)lvt_2_1_.getX()) * (p_222831_4_.func_226277_ct_() - (double)lvt_2_1_.getX()) + (p_222831_4_.func_226281_cx_() - (double)lvt_2_1_.getZ()) * (p_222831_4_.func_226281_cx_() - (double)lvt_2_1_.getZ()));
            double lvt_7_1_ = (double)((float)lvt_2_1_.getX() + 0.5F) + (double)(1.0F / lvt_6_1_) * (p_222831_4_.func_226277_ct_() - (double)lvt_2_1_.getX());
            double lvt_9_1_ = (double)((float)lvt_2_1_.getZ() + 0.5F) + (double)(1.0F / lvt_6_1_) * (p_222831_4_.func_226281_cx_() - (double)lvt_2_1_.getZ());
            int lvt_11_1_ = MathHelper.clamp((lvt_4_1_ - 21) / -2, 3, 15);

            for(int lvt_12_1_ = 0; lvt_12_1_ < lvt_11_1_; ++lvt_12_1_) {
               lvt_3_1_.addAndGet(5);
               double lvt_13_1_ = (double)(lvt_3_1_.get() >> 16 & 255) / 255.0D;
               double lvt_15_1_ = (double)(lvt_3_1_.get() >> 8 & 255) / 255.0D;
               double lvt_17_1_ = (double)(lvt_3_1_.get() & 255) / 255.0D;
               p_222826_1_.addParticle(ParticleTypes.ENTITY_EFFECT, lvt_7_1_, (double)((float)lvt_2_1_.getY() + 0.5F), lvt_9_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_);
            }

         });
      }
   }

   private boolean isNearbyRaider(LivingEntity p_222832_1_) {
      return p_222832_1_.isAlive() && !p_222832_1_.removed && this.getPos().withinDistance(p_222832_1_.getPositionVec(), 48.0D) && p_222832_1_.getType().isContained(EntityTypeTags.RAIDERS);
   }

   private void glow(LivingEntity p_222827_1_) {
      p_222827_1_.addPotionEffect(new EffectInstance(Effects.GLOWING, 60));
   }
}
