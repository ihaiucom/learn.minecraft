package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.common.IPlantable;

public class BaseTreeFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider field_227368_m_;
   public final BlockStateProvider field_227369_n_;
   public final List<TreeDecorator> field_227370_o_;
   public final int field_227371_p_;
   public transient boolean field_227372_q_;
   protected IPlantable sapling;

   protected BaseTreeFeatureConfig(BlockStateProvider p_i225842_1_, BlockStateProvider p_i225842_2_, List<TreeDecorator> p_i225842_3_, int p_i225842_4_) {
      this.sapling = (IPlantable)Blocks.OAK_SAPLING;
      this.field_227368_m_ = p_i225842_1_;
      this.field_227369_n_ = p_i225842_2_;
      this.field_227370_o_ = p_i225842_3_;
      this.field_227371_p_ = p_i225842_4_;
   }

   public void func_227373_a_() {
      this.field_227372_q_ = true;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      com.google.common.collect.ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_214634_1_.createString("trunk_provider"), this.field_227368_m_.serialize(p_214634_1_)).put(p_214634_1_.createString("leaves_provider"), this.field_227369_n_.serialize(p_214634_1_)).put(p_214634_1_.createString("decorators"), p_214634_1_.createList(this.field_227370_o_.stream().map((p_lambda$serialize$0_1_) -> {
         return p_lambda$serialize$0_1_.serialize(p_214634_1_);
      }))).put(p_214634_1_.createString("base_height"), p_214634_1_.createInt(this.field_227371_p_));
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(builder.build()));
   }

   protected BaseTreeFeatureConfig setSapling(IPlantable p_setSapling_1_) {
      this.sapling = p_setSapling_1_;
      return this;
   }

   public IPlantable getSapling() {
      return this.sapling;
   }

   public static <T> BaseTreeFeatureConfig func_227376_b_(Dynamic<T> p_227376_0_) {
      BlockStateProviderType<?> blockstateprovidertype = (BlockStateProviderType)Registry.field_229387_t_.getOrDefault(new ResourceLocation((String)p_227376_0_.get("trunk_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockStateProviderType<?> blockstateprovidertype1 = (BlockStateProviderType)Registry.field_229387_t_.getOrDefault(new ResourceLocation((String)p_227376_0_.get("leaves_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BaseTreeFeatureConfig(blockstateprovidertype.func_227399_a_(p_227376_0_.get("trunk_provider").orElseEmptyMap()), blockstateprovidertype1.func_227399_a_(p_227376_0_.get("leaves_provider").orElseEmptyMap()), p_227376_0_.get("decorators").asList((p_lambda$func_227376_b_$1_0_) -> {
         return ((TreeDecoratorType)Registry.field_229390_w_.getOrDefault(new ResourceLocation((String)p_lambda$func_227376_b_$1_0_.get("type").asString().orElseThrow(RuntimeException::new)))).func_227431_a_(p_lambda$func_227376_b_$1_0_);
      }), p_227376_0_.get("base_height").asInt(0));
   }

   public static <T> BaseTreeFeatureConfig deserializeJungle(Dynamic<T> p_deserializeJungle_0_) {
      return func_227376_b_(p_deserializeJungle_0_).setSapling((IPlantable)Blocks.JUNGLE_SAPLING);
   }

   public static class Builder {
      public final BlockStateProvider field_227377_a_;
      public final BlockStateProvider field_227378_b_;
      private List<TreeDecorator> field_227379_c_ = Lists.newArrayList();
      private int field_227380_d_ = 0;
      protected IPlantable sapling;

      public Builder(BlockStateProvider p_i225843_1_, BlockStateProvider p_i225843_2_) {
         this.sapling = (IPlantable)Blocks.OAK_SAPLING;
         this.field_227377_a_ = p_i225843_1_;
         this.field_227378_b_ = p_i225843_2_;
      }

      public BaseTreeFeatureConfig.Builder func_225569_d_(int p_225569_1_) {
         this.field_227380_d_ = p_225569_1_;
         return this;
      }

      public BaseTreeFeatureConfig.Builder setSapling(IPlantable p_setSapling_1_) {
         this.sapling = p_setSapling_1_;
         return this;
      }

      public BaseTreeFeatureConfig func_225568_b_() {
         return (new BaseTreeFeatureConfig(this.field_227377_a_, this.field_227378_b_, this.field_227379_c_, this.field_227380_d_)).setSapling(this.sapling);
      }
   }
}
