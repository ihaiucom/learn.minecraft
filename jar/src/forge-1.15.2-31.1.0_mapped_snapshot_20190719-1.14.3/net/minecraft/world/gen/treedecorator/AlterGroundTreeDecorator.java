package net.minecraft.world.gen.treedecorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class AlterGroundTreeDecorator extends TreeDecorator {
   private final BlockStateProvider field_227410_b_;

   public AlterGroundTreeDecorator(BlockStateProvider p_i225864_1_) {
      super(TreeDecoratorType.field_227429_e_);
      this.field_227410_b_ = p_i225864_1_;
   }

   public <T> AlterGroundTreeDecorator(Dynamic<T> p_i225865_1_) {
      this(((BlockStateProviderType)Registry.field_229387_t_.getOrDefault(new ResourceLocation((String)p_i225865_1_.get("provider").get("type").asString().orElseThrow(RuntimeException::new)))).func_227399_a_(p_i225865_1_.get("provider").orElseEmptyMap()));
   }

   public void func_225576_a_(IWorld p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
      int lvt_7_1_ = ((BlockPos)p_225576_3_.get(0)).getY();
      p_225576_3_.stream().filter((p_227411_1_) -> {
         return p_227411_1_.getY() == lvt_7_1_;
      }).forEach((p_227412_3_) -> {
         this.func_227413_a_(p_225576_1_, p_225576_2_, p_227412_3_.west().north());
         this.func_227413_a_(p_225576_1_, p_225576_2_, p_227412_3_.east(2).north());
         this.func_227413_a_(p_225576_1_, p_225576_2_, p_227412_3_.west().south(2));
         this.func_227413_a_(p_225576_1_, p_225576_2_, p_227412_3_.east(2).south(2));

         for(int lvt_4_1_ = 0; lvt_4_1_ < 5; ++lvt_4_1_) {
            int lvt_5_1_ = p_225576_2_.nextInt(64);
            int lvt_6_1_ = lvt_5_1_ % 8;
            int lvt_7_1_ = lvt_5_1_ / 8;
            if (lvt_6_1_ == 0 || lvt_6_1_ == 7 || lvt_7_1_ == 0 || lvt_7_1_ == 7) {
               this.func_227413_a_(p_225576_1_, p_225576_2_, p_227412_3_.add(-3 + lvt_6_1_, 0, -3 + lvt_7_1_));
            }
         }

      });
   }

   private void func_227413_a_(IWorldGenerationReader p_227413_1_, Random p_227413_2_, BlockPos p_227413_3_) {
      for(int lvt_4_1_ = -2; lvt_4_1_ <= 2; ++lvt_4_1_) {
         for(int lvt_5_1_ = -2; lvt_5_1_ <= 2; ++lvt_5_1_) {
            if (Math.abs(lvt_4_1_) != 2 || Math.abs(lvt_5_1_) != 2) {
               this.func_227414_b_(p_227413_1_, p_227413_2_, p_227413_3_.add(lvt_4_1_, 0, lvt_5_1_));
            }
         }
      }

   }

   private void func_227414_b_(IWorldGenerationReader p_227414_1_, Random p_227414_2_, BlockPos p_227414_3_) {
      for(int lvt_4_1_ = 2; lvt_4_1_ >= -3; --lvt_4_1_) {
         BlockPos lvt_5_1_ = p_227414_3_.up(lvt_4_1_);
         if (AbstractTreeFeature.isDirtOrGrassBlock(p_227414_1_, lvt_5_1_)) {
            p_227414_1_.setBlockState(lvt_5_1_, this.field_227410_b_.func_225574_a_(p_227414_2_, p_227414_3_), 19);
            break;
         }

         if (!AbstractTreeFeature.isAir(p_227414_1_, lvt_5_1_) && lvt_4_1_ < 0) {
            break;
         }
      }

   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return (new Dynamic(p_218175_1_, p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.field_229390_w_.getKey(this.field_227422_a_).toString()), p_218175_1_.createString("provider"), this.field_227410_b_.serialize(p_218175_1_))))).getValue();
   }
}
