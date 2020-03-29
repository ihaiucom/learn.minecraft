package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class EndSpikeFeatureConfig implements IFeatureConfig {
   private final boolean crystalInvulnerable;
   private final List<EndSpikeFeature.EndSpike> spikes;
   @Nullable
   private final BlockPos crystalBeamTarget;

   public EndSpikeFeatureConfig(boolean p_i51433_1_, List<EndSpikeFeature.EndSpike> p_i51433_2_, @Nullable BlockPos p_i51433_3_) {
      this.crystalInvulnerable = p_i51433_1_;
      this.spikes = p_i51433_2_;
      this.crystalBeamTarget = p_i51433_3_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      Dynamic var10000 = new Dynamic;
      Object var10004 = p_214634_1_.createString("crystalInvulnerable");
      Object var10005 = p_214634_1_.createBoolean(this.crystalInvulnerable);
      Object var10006 = p_214634_1_.createString("spikes");
      Object var10007 = p_214634_1_.createList(this.spikes.stream().map((p_214670_1_) -> {
         return p_214670_1_.func_214749_a(p_214634_1_).getValue();
      }));
      Object var10008 = p_214634_1_.createString("crystalBeamTarget");
      Object var10009;
      if (this.crystalBeamTarget == null) {
         var10009 = p_214634_1_.createList(Stream.empty());
      } else {
         IntStream var10010 = IntStream.of(new int[]{this.crystalBeamTarget.getX(), this.crystalBeamTarget.getY(), this.crystalBeamTarget.getZ()});
         p_214634_1_.getClass();
         var10009 = p_214634_1_.createList(var10010.mapToObj(p_214634_1_::createInt));
      }

      var10000.<init>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(var10004, var10005, var10006, var10007, var10008, var10009)));
      return var10000;
   }

   public static <T> EndSpikeFeatureConfig deserialize(Dynamic<T> p_214673_0_) {
      List<EndSpikeFeature.EndSpike> lvt_1_1_ = p_214673_0_.get("spikes").asList(EndSpikeFeature.EndSpike::func_214747_a);
      List<Integer> lvt_2_1_ = p_214673_0_.get("crystalBeamTarget").asList((p_214672_0_) -> {
         return p_214672_0_.asInt(0);
      });
      BlockPos lvt_3_2_;
      if (lvt_2_1_.size() == 3) {
         lvt_3_2_ = new BlockPos((Integer)lvt_2_1_.get(0), (Integer)lvt_2_1_.get(1), (Integer)lvt_2_1_.get(2));
      } else {
         lvt_3_2_ = null;
      }

      return new EndSpikeFeatureConfig(p_214673_0_.get("crystalInvulnerable").asBoolean(false), lvt_1_1_, lvt_3_2_);
   }

   public boolean func_214669_a() {
      return this.crystalInvulnerable;
   }

   public List<EndSpikeFeature.EndSpike> func_214671_b() {
      return this.spikes;
   }

   @Nullable
   public BlockPos func_214668_c() {
      return this.crystalBeamTarget;
   }
}
