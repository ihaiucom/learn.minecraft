package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NeighborsUpdateDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Long, Map<BlockPos, Integer>> lastUpdate = Maps.newTreeMap(Ordering.natural().reverse());

   NeighborsUpdateDebugRenderer(Minecraft p_i47365_1_) {
      this.minecraft = p_i47365_1_;
   }

   public void addUpdate(long p_191553_1_, BlockPos p_191553_3_) {
      Map<BlockPos, Integer> lvt_4_1_ = (Map)this.lastUpdate.get(p_191553_1_);
      if (lvt_4_1_ == null) {
         lvt_4_1_ = Maps.newHashMap();
         this.lastUpdate.put(p_191553_1_, lvt_4_1_);
      }

      Integer lvt_5_1_ = (Integer)((Map)lvt_4_1_).get(p_191553_3_);
      if (lvt_5_1_ == null) {
         lvt_5_1_ = 0;
      }

      ((Map)lvt_4_1_).put(p_191553_3_, lvt_5_1_ + 1);
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      long lvt_9_1_ = this.minecraft.world.getGameTime();
      int lvt_11_1_ = true;
      double lvt_12_1_ = 0.0025D;
      Set<BlockPos> lvt_14_1_ = Sets.newHashSet();
      Map<BlockPos, Integer> lvt_15_1_ = Maps.newHashMap();
      IVertexBuilder lvt_16_1_ = p_225619_2_.getBuffer(RenderType.func_228659_m_());
      Iterator lvt_17_1_ = this.lastUpdate.entrySet().iterator();

      while(true) {
         Entry lvt_18_2_;
         while(lvt_17_1_.hasNext()) {
            lvt_18_2_ = (Entry)lvt_17_1_.next();
            Long lvt_19_1_ = (Long)lvt_18_2_.getKey();
            Map<BlockPos, Integer> lvt_20_1_ = (Map)lvt_18_2_.getValue();
            long lvt_21_1_ = lvt_9_1_ - lvt_19_1_;
            if (lvt_21_1_ > 200L) {
               lvt_17_1_.remove();
            } else {
               Iterator var23 = lvt_20_1_.entrySet().iterator();

               while(var23.hasNext()) {
                  Entry<BlockPos, Integer> lvt_24_1_ = (Entry)var23.next();
                  BlockPos lvt_25_1_ = (BlockPos)lvt_24_1_.getKey();
                  Integer lvt_26_1_ = (Integer)lvt_24_1_.getValue();
                  if (lvt_14_1_.add(lvt_25_1_)) {
                     AxisAlignedBB lvt_27_1_ = (new AxisAlignedBB(BlockPos.ZERO)).grow(0.002D).shrink(0.0025D * (double)lvt_21_1_).offset((double)lvt_25_1_.getX(), (double)lvt_25_1_.getY(), (double)lvt_25_1_.getZ()).offset(-p_225619_3_, -p_225619_5_, -p_225619_7_);
                     WorldRenderer.func_228432_a_(lvt_16_1_, lvt_27_1_.minX, lvt_27_1_.minY, lvt_27_1_.minZ, lvt_27_1_.maxX, lvt_27_1_.maxY, lvt_27_1_.maxZ, 1.0F, 1.0F, 1.0F, 1.0F);
                     lvt_15_1_.put(lvt_25_1_, lvt_26_1_);
                  }
               }
            }
         }

         lvt_17_1_ = lvt_15_1_.entrySet().iterator();

         while(lvt_17_1_.hasNext()) {
            lvt_18_2_ = (Entry)lvt_17_1_.next();
            BlockPos lvt_19_2_ = (BlockPos)lvt_18_2_.getKey();
            Integer lvt_20_2_ = (Integer)lvt_18_2_.getValue();
            DebugRenderer.func_217731_a(String.valueOf(lvt_20_2_), lvt_19_2_.getX(), lvt_19_2_.getY(), lvt_19_2_.getZ(), -1);
         }

         return;
      }
   }
}
