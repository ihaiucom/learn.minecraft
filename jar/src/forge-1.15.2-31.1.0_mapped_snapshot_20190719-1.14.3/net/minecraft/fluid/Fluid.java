package net.minecraft.fluid;

import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.extensions.IForgeFluid;
import net.minecraftforge.common.util.ReverseTagWrapper;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class Fluid extends ForgeRegistryEntry<Fluid> implements IForgeFluid {
   public static final ObjectIntIdentityMap<IFluidState> STATE_REGISTRY = new ObjectIntIdentityMap();
   protected final StateContainer<Fluid, IFluidState> stateContainer;
   private IFluidState defaultState;
   private final ReverseTagWrapper<Fluid> reverseTags = new ReverseTagWrapper(this, FluidTags::getGeneration, FluidTags::func_226157_a_);
   private FluidAttributes forgeFluidAttributes;

   protected Fluid() {
      StateContainer.Builder<Fluid, IFluidState> builder = new StateContainer.Builder(this);
      this.fillStateContainer(builder);
      this.stateContainer = builder.create(FluidState::new);
      this.setDefaultState((IFluidState)this.stateContainer.getBaseState());
   }

   protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> p_207184_1_) {
   }

   public StateContainer<Fluid, IFluidState> getStateContainer() {
      return this.stateContainer;
   }

   protected final void setDefaultState(IFluidState p_207183_1_) {
      this.defaultState = p_207183_1_;
   }

   public final IFluidState getDefaultState() {
      return this.defaultState;
   }

   public abstract Item getFilledBucket();

   @OnlyIn(Dist.CLIENT)
   protected void animateTick(World p_204522_1_, BlockPos p_204522_2_, IFluidState p_204522_3_, Random p_204522_4_) {
   }

   protected void tick(World p_207191_1_, BlockPos p_207191_2_, IFluidState p_207191_3_) {
   }

   protected void randomTick(World p_207186_1_, BlockPos p_207186_2_, IFluidState p_207186_3_, Random p_207186_4_) {
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   protected IParticleData getDripParticleData() {
      return null;
   }

   protected abstract boolean func_215665_a(IFluidState var1, IBlockReader var2, BlockPos var3, Fluid var4, Direction var5);

   protected abstract Vec3d func_215663_a(IBlockReader var1, BlockPos var2, IFluidState var3);

   public abstract int getTickRate(IWorldReader var1);

   protected boolean ticksRandomly() {
      return false;
   }

   protected boolean isEmpty() {
      return false;
   }

   protected abstract float getExplosionResistance();

   public abstract float func_215662_a(IFluidState var1, IBlockReader var2, BlockPos var3);

   public abstract float func_223407_a(IFluidState var1);

   protected abstract BlockState getBlockState(IFluidState var1);

   public abstract boolean isSource(IFluidState var1);

   public abstract int getLevel(IFluidState var1);

   public boolean isEquivalentTo(Fluid p_207187_1_) {
      return p_207187_1_ == this;
   }

   public boolean isIn(Tag<Fluid> p_207185_1_) {
      return p_207185_1_.contains(this);
   }

   public abstract VoxelShape func_215664_b(IFluidState var1, IBlockReader var2, BlockPos var3);

   public Set<ResourceLocation> getTags() {
      return this.reverseTags.getTagNames();
   }

   protected FluidAttributes createAttributes() {
      return ForgeHooks.createVanillaFluidAttributes(this);
   }

   public final FluidAttributes getAttributes() {
      if (this.forgeFluidAttributes == null) {
         this.forgeFluidAttributes = this.createAttributes();
      }

      return this.forgeFluidAttributes;
   }
}
