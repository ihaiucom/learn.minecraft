package net.minecraft.world.gen.treedecorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class BeehiveTreeDecorator extends TreeDecorator {
   private final float field_227415_b_;

   public BeehiveTreeDecorator(float p_i225866_1_) {
      super(TreeDecoratorType.field_227428_d_);
      this.field_227415_b_ = p_i225866_1_;
   }

   public <T> BeehiveTreeDecorator(Dynamic<T> p_i225867_1_) {
      this(p_i225867_1_.get("probability").asFloat(0.0F));
   }

   public void func_225576_a_(IWorld p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
      if (p_225576_2_.nextFloat() < this.field_227415_b_) {
         Direction lvt_7_1_ = BeehiveBlock.field_226871_a_[p_225576_2_.nextInt(BeehiveBlock.field_226871_a_.length)];
         int lvt_8_1_ = !p_225576_4_.isEmpty() ? Math.max(((BlockPos)p_225576_4_.get(0)).getY() - 1, ((BlockPos)p_225576_3_.get(0)).getY()) : Math.min(((BlockPos)p_225576_3_.get(0)).getY() + 1 + p_225576_2_.nextInt(3), ((BlockPos)p_225576_3_.get(p_225576_3_.size() - 1)).getY());
         List<BlockPos> lvt_9_1_ = (List)p_225576_3_.stream().filter((p_227416_1_) -> {
            return p_227416_1_.getY() == lvt_8_1_;
         }).collect(Collectors.toList());
         if (!lvt_9_1_.isEmpty()) {
            BlockPos lvt_10_1_ = (BlockPos)lvt_9_1_.get(p_225576_2_.nextInt(lvt_9_1_.size()));
            BlockPos lvt_11_1_ = lvt_10_1_.offset(lvt_7_1_);
            if (AbstractTreeFeature.isAir(p_225576_1_, lvt_11_1_) && AbstractTreeFeature.isAir(p_225576_1_, lvt_11_1_.offset(Direction.SOUTH))) {
               BlockState lvt_12_1_ = (BlockState)Blocks.field_226905_ma_.getDefaultState().with(BeehiveBlock.field_226872_b_, Direction.SOUTH);
               this.func_227423_a_(p_225576_1_, lvt_11_1_, lvt_12_1_, p_225576_5_, p_225576_6_);
               TileEntity lvt_13_1_ = p_225576_1_.getTileEntity(lvt_11_1_);
               if (lvt_13_1_ instanceof BeehiveTileEntity) {
                  BeehiveTileEntity lvt_14_1_ = (BeehiveTileEntity)lvt_13_1_;
                  int lvt_15_1_ = 2 + p_225576_2_.nextInt(2);

                  for(int lvt_16_1_ = 0; lvt_16_1_ < lvt_15_1_; ++lvt_16_1_) {
                     BeeEntity lvt_17_1_ = new BeeEntity(EntityType.field_226289_e_, p_225576_1_.getWorld());
                     lvt_14_1_.func_226962_a_(lvt_17_1_, false, p_225576_2_.nextInt(599));
                  }
               }

            }
         }
      }
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return (new Dynamic(p_218175_1_, p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.field_229390_w_.getKey(this.field_227422_a_).toString()), p_218175_1_.createString("probability"), p_218175_1_.createFloat(this.field_227415_b_))))).getValue();
   }
}
