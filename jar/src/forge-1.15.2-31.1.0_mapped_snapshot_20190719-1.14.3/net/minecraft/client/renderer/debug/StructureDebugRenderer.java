package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StructureDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<DimensionType, Map<String, MutableBoundingBox>> mainBoxes = Maps.newIdentityHashMap();
   private final Map<DimensionType, Map<String, MutableBoundingBox>> subBoxes = Maps.newIdentityHashMap();
   private final Map<DimensionType, Map<String, Boolean>> subBoxFlags = Maps.newIdentityHashMap();

   public StructureDebugRenderer(Minecraft p_i48764_1_) {
      this.minecraft = p_i48764_1_;
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      ActiveRenderInfo lvt_9_1_ = this.minecraft.gameRenderer.getActiveRenderInfo();
      IWorld lvt_10_1_ = this.minecraft.world;
      DimensionType lvt_11_1_ = lvt_10_1_.getDimension().getType();
      BlockPos lvt_12_1_ = new BlockPos(lvt_9_1_.getProjectedView().x, 0.0D, lvt_9_1_.getProjectedView().z);
      IVertexBuilder lvt_13_1_ = p_225619_2_.getBuffer(RenderType.func_228659_m_());
      Iterator var14;
      if (this.mainBoxes.containsKey(lvt_11_1_)) {
         var14 = ((Map)this.mainBoxes.get(lvt_11_1_)).values().iterator();

         while(var14.hasNext()) {
            MutableBoundingBox lvt_15_1_ = (MutableBoundingBox)var14.next();
            if (lvt_12_1_.withinDistance(lvt_15_1_.func_215126_f(), 500.0D)) {
               WorldRenderer.func_228432_a_(lvt_13_1_, (double)lvt_15_1_.minX - p_225619_3_, (double)lvt_15_1_.minY - p_225619_5_, (double)lvt_15_1_.minZ - p_225619_7_, (double)(lvt_15_1_.maxX + 1) - p_225619_3_, (double)(lvt_15_1_.maxY + 1) - p_225619_5_, (double)(lvt_15_1_.maxZ + 1) - p_225619_7_, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      if (this.subBoxes.containsKey(lvt_11_1_)) {
         var14 = ((Map)this.subBoxes.get(lvt_11_1_)).entrySet().iterator();

         while(var14.hasNext()) {
            Entry<String, MutableBoundingBox> lvt_15_2_ = (Entry)var14.next();
            String lvt_16_1_ = (String)lvt_15_2_.getKey();
            MutableBoundingBox lvt_17_1_ = (MutableBoundingBox)lvt_15_2_.getValue();
            Boolean lvt_18_1_ = (Boolean)((Map)this.subBoxFlags.get(lvt_11_1_)).get(lvt_16_1_);
            if (lvt_12_1_.withinDistance(lvt_17_1_.func_215126_f(), 500.0D)) {
               if (lvt_18_1_) {
                  WorldRenderer.func_228432_a_(lvt_13_1_, (double)lvt_17_1_.minX - p_225619_3_, (double)lvt_17_1_.minY - p_225619_5_, (double)lvt_17_1_.minZ - p_225619_7_, (double)(lvt_17_1_.maxX + 1) - p_225619_3_, (double)(lvt_17_1_.maxY + 1) - p_225619_5_, (double)(lvt_17_1_.maxZ + 1) - p_225619_7_, 0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  WorldRenderer.func_228432_a_(lvt_13_1_, (double)lvt_17_1_.minX - p_225619_3_, (double)lvt_17_1_.minY - p_225619_5_, (double)lvt_17_1_.minZ - p_225619_7_, (double)(lvt_17_1_.maxX + 1) - p_225619_3_, (double)(lvt_17_1_.maxY + 1) - p_225619_5_, (double)(lvt_17_1_.maxZ + 1) - p_225619_7_, 0.0F, 0.0F, 1.0F, 1.0F);
               }
            }
         }
      }

   }

   public void func_223454_a(MutableBoundingBox p_223454_1_, List<MutableBoundingBox> p_223454_2_, List<Boolean> p_223454_3_, DimensionType p_223454_4_) {
      if (!this.mainBoxes.containsKey(p_223454_4_)) {
         this.mainBoxes.put(p_223454_4_, Maps.newHashMap());
      }

      if (!this.subBoxes.containsKey(p_223454_4_)) {
         this.subBoxes.put(p_223454_4_, Maps.newHashMap());
         this.subBoxFlags.put(p_223454_4_, Maps.newHashMap());
      }

      ((Map)this.mainBoxes.get(p_223454_4_)).put(p_223454_1_.toString(), p_223454_1_);

      for(int lvt_5_1_ = 0; lvt_5_1_ < p_223454_2_.size(); ++lvt_5_1_) {
         MutableBoundingBox lvt_6_1_ = (MutableBoundingBox)p_223454_2_.get(lvt_5_1_);
         Boolean lvt_7_1_ = (Boolean)p_223454_3_.get(lvt_5_1_);
         ((Map)this.subBoxes.get(p_223454_4_)).put(lvt_6_1_.toString(), lvt_6_1_);
         ((Map)this.subBoxFlags.get(p_223454_4_)).put(lvt_6_1_.toString(), lvt_7_1_);
      }

   }

   public void func_217675_a() {
      this.mainBoxes.clear();
      this.subBoxes.clear();
      this.subBoxFlags.clear();
   }
}
