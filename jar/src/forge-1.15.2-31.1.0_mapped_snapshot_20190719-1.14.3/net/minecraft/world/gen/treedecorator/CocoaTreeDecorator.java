package net.minecraft.world.gen.treedecorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class CocoaTreeDecorator extends TreeDecorator {
   private final float field_227417_b_;

   public CocoaTreeDecorator(float p_i225868_1_) {
      super(TreeDecoratorType.field_227427_c_);
      this.field_227417_b_ = p_i225868_1_;
   }

   public <T> CocoaTreeDecorator(Dynamic<T> p_i225869_1_) {
      this(p_i225869_1_.get("probability").asFloat(0.0F));
   }

   public void func_225576_a_(IWorld p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
      if (p_225576_2_.nextFloat() < this.field_227417_b_) {
         int lvt_7_1_ = ((BlockPos)p_225576_3_.get(0)).getY();
         p_225576_3_.stream().filter((p_227418_1_) -> {
            return p_227418_1_.getY() - lvt_7_1_ <= 2;
         }).forEach((p_227419_5_) -> {
            Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

            while(var6.hasNext()) {
               Direction lvt_7_1_ = (Direction)var6.next();
               if (p_225576_2_.nextFloat() <= 0.25F) {
                  Direction lvt_8_1_ = lvt_7_1_.getOpposite();
                  BlockPos lvt_9_1_ = p_227419_5_.add(lvt_8_1_.getXOffset(), 0, lvt_8_1_.getZOffset());
                  if (AbstractTreeFeature.isAir(p_225576_1_, lvt_9_1_)) {
                     BlockState lvt_10_1_ = (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, p_225576_2_.nextInt(3))).with(CocoaBlock.HORIZONTAL_FACING, lvt_7_1_);
                     this.func_227423_a_(p_225576_1_, lvt_9_1_, lvt_10_1_, p_225576_5_, p_225576_6_);
                  }
               }
            }

         });
      }
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return (new Dynamic(p_218175_1_, p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.field_229390_w_.getKey(this.field_227422_a_).toString()), p_218175_1_.createString("probability"), p_218175_1_.createFloat(this.field_227417_b_))))).getValue();
   }
}
