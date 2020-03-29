package net.minecraft.world.lighting;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldLightManager implements ILightListener {
   @Nullable
   private final LightEngine<?, ?> blockLight;
   @Nullable
   private final LightEngine<?, ?> skyLight;

   public WorldLightManager(IChunkLightProvider p_i51290_1_, boolean p_i51290_2_, boolean p_i51290_3_) {
      this.blockLight = p_i51290_2_ ? new BlockLightEngine(p_i51290_1_) : null;
      this.skyLight = p_i51290_3_ ? new SkyLightEngine(p_i51290_1_) : null;
   }

   public void checkBlock(BlockPos p_215568_1_) {
      if (this.blockLight != null) {
         this.blockLight.checkLight(p_215568_1_);
      }

      if (this.skyLight != null) {
         this.skyLight.checkLight(p_215568_1_);
      }

   }

   public void func_215573_a(BlockPos p_215573_1_, int p_215573_2_) {
      if (this.blockLight != null) {
         this.blockLight.func_215623_a(p_215573_1_, p_215573_2_);
      }

   }

   public boolean func_215570_a() {
      if (this.skyLight != null && this.skyLight.func_215619_a()) {
         return true;
      } else {
         return this.blockLight != null && this.blockLight.func_215619_a();
      }
   }

   public int tick(int p_215575_1_, boolean p_215575_2_, boolean p_215575_3_) {
      if (this.blockLight != null && this.skyLight != null) {
         int lvt_4_1_ = p_215575_1_ / 2;
         int lvt_5_1_ = this.blockLight.tick(lvt_4_1_, p_215575_2_, p_215575_3_);
         int lvt_6_1_ = p_215575_1_ - lvt_4_1_ + lvt_5_1_;
         int lvt_7_1_ = this.skyLight.tick(lvt_6_1_, p_215575_2_, p_215575_3_);
         return lvt_5_1_ == 0 && lvt_7_1_ > 0 ? this.blockLight.tick(lvt_7_1_, p_215575_2_, p_215575_3_) : lvt_7_1_;
      } else if (this.blockLight != null) {
         return this.blockLight.tick(p_215575_1_, p_215575_2_, p_215575_3_);
      } else {
         return this.skyLight != null ? this.skyLight.tick(p_215575_1_, p_215575_2_, p_215575_3_) : p_215575_1_;
      }
   }

   public void updateSectionStatus(SectionPos p_215566_1_, boolean p_215566_2_) {
      if (this.blockLight != null) {
         this.blockLight.updateSectionStatus(p_215566_1_, p_215566_2_);
      }

      if (this.skyLight != null) {
         this.skyLight.updateSectionStatus(p_215566_1_, p_215566_2_);
      }

   }

   public void func_215571_a(ChunkPos p_215571_1_, boolean p_215571_2_) {
      if (this.blockLight != null) {
         this.blockLight.func_215620_a(p_215571_1_, p_215571_2_);
      }

      if (this.skyLight != null) {
         this.skyLight.func_215620_a(p_215571_1_, p_215571_2_);
      }

   }

   public IWorldLightListener getLightEngine(LightType p_215569_1_) {
      if (p_215569_1_ == LightType.BLOCK) {
         return (IWorldLightListener)(this.blockLight == null ? IWorldLightListener.Dummy.INSTANCE : this.blockLight);
      } else {
         return (IWorldLightListener)(this.skyLight == null ? IWorldLightListener.Dummy.INSTANCE : this.skyLight);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public String func_215572_a(LightType p_215572_1_, SectionPos p_215572_2_) {
      if (p_215572_1_ == LightType.BLOCK) {
         if (this.blockLight != null) {
            return this.blockLight.getDebugString(p_215572_2_.asLong());
         }
      } else if (this.skyLight != null) {
         return this.skyLight.getDebugString(p_215572_2_.asLong());
      }

      return "n/a";
   }

   public void setData(LightType p_215574_1_, SectionPos p_215574_2_, @Nullable NibbleArray p_215574_3_) {
      if (p_215574_1_ == LightType.BLOCK) {
         if (this.blockLight != null) {
            this.blockLight.setData(p_215574_2_.asLong(), p_215574_3_);
         }
      } else if (this.skyLight != null) {
         this.skyLight.setData(p_215574_2_.asLong(), p_215574_3_);
      }

   }

   public void retainData(ChunkPos p_223115_1_, boolean p_223115_2_) {
      if (this.blockLight != null) {
         this.blockLight.retainChunkData(p_223115_1_, p_223115_2_);
      }

      if (this.skyLight != null) {
         this.skyLight.retainChunkData(p_223115_1_, p_223115_2_);
      }

   }

   public int func_227470_b_(BlockPos p_227470_1_, int p_227470_2_) {
      int lvt_3_1_ = this.skyLight == null ? 0 : this.skyLight.getLightFor(p_227470_1_) - p_227470_2_;
      int lvt_4_1_ = this.blockLight == null ? 0 : this.blockLight.getLightFor(p_227470_1_);
      return Math.max(lvt_4_1_, lvt_3_1_);
   }
}
