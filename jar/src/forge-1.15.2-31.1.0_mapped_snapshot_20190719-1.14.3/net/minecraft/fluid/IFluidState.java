package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeFluidState;

public interface IFluidState extends IStateHolder<IFluidState>, IForgeFluidState {
   Fluid getFluid();

   default boolean isSource() {
      return this.getFluid().isSource(this);
   }

   default boolean isEmpty() {
      return this.getFluid().isEmpty();
   }

   default float func_215679_a(IBlockReader p_215679_1_, BlockPos p_215679_2_) {
      return this.getFluid().func_215662_a(this, p_215679_1_, p_215679_2_);
   }

   default float func_223408_f() {
      return this.getFluid().func_223407_a(this);
   }

   default int getLevel() {
      return this.getFluid().getLevel(this);
   }

   @OnlyIn(Dist.CLIENT)
   default boolean shouldRenderSides(IBlockReader p_205586_1_, BlockPos p_205586_2_) {
      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            BlockPos blockpos = p_205586_2_.add(i, 0, j);
            IFluidState ifluidstate = p_205586_1_.getFluidState(blockpos);
            if (!ifluidstate.getFluid().isEquivalentTo(this.getFluid()) && !p_205586_1_.getBlockState(blockpos).isOpaqueCube(p_205586_1_, blockpos)) {
               return true;
            }
         }
      }

      return false;
   }

   default void tick(World p_206880_1_, BlockPos p_206880_2_) {
      this.getFluid().tick(p_206880_1_, p_206880_2_, this);
   }

   @OnlyIn(Dist.CLIENT)
   default void animateTick(World p_206881_1_, BlockPos p_206881_2_, Random p_206881_3_) {
      this.getFluid().animateTick(p_206881_1_, p_206881_2_, this, p_206881_3_);
   }

   default boolean ticksRandomly() {
      return this.getFluid().ticksRandomly();
   }

   default void randomTick(World p_206891_1_, BlockPos p_206891_2_, Random p_206891_3_) {
      this.getFluid().randomTick(p_206891_1_, p_206891_2_, this, p_206891_3_);
   }

   default Vec3d getFlow(IBlockReader p_215673_1_, BlockPos p_215673_2_) {
      return this.getFluid().func_215663_a(p_215673_1_, p_215673_2_, this);
   }

   default BlockState getBlockState() {
      return this.getFluid().getBlockState(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   default IParticleData getDripParticleData() {
      return this.getFluid().getDripParticleData();
   }

   default boolean isTagged(Tag<Fluid> p_206884_1_) {
      return this.getFluid().isIn(p_206884_1_);
   }

   /** @deprecated */
   @Deprecated
   default float getExplosionResistance() {
      return this.getFluid().getExplosionResistance();
   }

   default boolean func_215677_a(IBlockReader p_215677_1_, BlockPos p_215677_2_, Fluid p_215677_3_, Direction p_215677_4_) {
      return this.getFluid().func_215665_a(this, p_215677_1_, p_215677_2_, p_215677_3_, p_215677_4_);
   }

   static <T> Dynamic<T> serialize(DynamicOps<T> p_215680_0_, IFluidState p_215680_1_) {
      ImmutableMap<IProperty<?>, Comparable<?>> immutablemap = p_215680_1_.getValues();
      Object t;
      if (immutablemap.isEmpty()) {
         t = p_215680_0_.createMap(ImmutableMap.of(p_215680_0_.createString("Name"), p_215680_0_.createString(Registry.FLUID.getKey(p_215680_1_.getFluid()).toString())));
      } else {
         t = p_215680_0_.createMap(ImmutableMap.of(p_215680_0_.createString("Name"), p_215680_0_.createString(Registry.FLUID.getKey(p_215680_1_.getFluid()).toString()), p_215680_0_.createString("Properties"), p_215680_0_.createMap((Map)immutablemap.entrySet().stream().map((p_lambda$serialize$0_1_) -> {
            return Pair.of(p_215680_0_.createString(((IProperty)p_lambda$serialize$0_1_.getKey()).getName()), p_215680_0_.createString(IStateHolder.func_215670_b((IProperty)p_lambda$serialize$0_1_.getKey(), (Comparable)p_lambda$serialize$0_1_.getValue())));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
      }

      return new Dynamic(p_215680_0_, t);
   }

   static <T> IFluidState deserialize(Dynamic<T> p_215681_0_) {
      DefaultedRegistry var10000 = Registry.FLUID;
      Optional var10003 = p_215681_0_.getElement("Name");
      DynamicOps var10004 = p_215681_0_.getOps();
      var10004.getClass();
      Fluid fluid = (Fluid)var10000.getOrDefault(new ResourceLocation((String)var10003.flatMap(var10004::getStringValue).orElse("minecraft:empty")));
      Map<String, String> map = p_215681_0_.get("Properties").asMap((p_lambda$deserialize$1_0_) -> {
         return p_lambda$deserialize$1_0_.asString("");
      }, (p_lambda$deserialize$2_0_) -> {
         return p_lambda$deserialize$2_0_.asString("");
      });
      IFluidState ifluidstate = fluid.getDefaultState();
      StateContainer<Fluid, IFluidState> statecontainer = fluid.getStateContainer();
      Iterator var5 = map.entrySet().iterator();

      while(var5.hasNext()) {
         Entry<String, String> entry = (Entry)var5.next();
         String s = (String)entry.getKey();
         IProperty<?> iproperty = statecontainer.getProperty(s);
         if (iproperty != null) {
            ifluidstate = (IFluidState)IStateHolder.func_215671_a(ifluidstate, iproperty, s, p_215681_0_.toString(), (String)entry.getValue());
         }
      }

      return ifluidstate;
   }

   default VoxelShape getShape(IBlockReader p_215676_1_, BlockPos p_215676_2_) {
      return this.getFluid().func_215664_b(this, p_215676_1_, p_215676_2_);
   }
}