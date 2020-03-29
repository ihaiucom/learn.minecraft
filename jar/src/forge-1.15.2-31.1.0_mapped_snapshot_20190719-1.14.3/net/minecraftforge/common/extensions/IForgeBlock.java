package net.minecraftforge.common.extensions;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.Effects;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;

public interface IForgeBlock {
   default Block getBlock() {
      return (Block)this;
   }

   float getSlipperiness(BlockState var1, IWorldReader var2, BlockPos var3, @Nullable Entity var4);

   default int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
      return state.getLightValue();
   }

   default boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
      return false;
   }

   default boolean isBurning(BlockState state, IBlockReader world, BlockPos pos) {
      return this == Blocks.FIRE || this == Blocks.LAVA;
   }

   default boolean hasTileEntity(BlockState state) {
      return this instanceof ITileEntityProvider;
   }

   @Nullable
   default TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return this.getBlock() instanceof ITileEntityProvider ? ((ITileEntityProvider)this.getBlock()).createNewTileEntity(world) : null;
   }

   default boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
      return ForgeHooks.canHarvestBlock(state, player, world, pos);
   }

   default boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
      this.getBlock().onBlockHarvested(world, pos, state, player);
      return world.removeBlock(pos, false);
   }

   default boolean isBed(BlockState state, IBlockReader world, BlockPos pos, @Nullable Entity player) {
      return this.getBlock() instanceof BedBlock;
   }

   default boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
      return state.canEntitySpawn(world, pos, entityType);
   }

   default Optional<Vec3d> getBedSpawnPosition(EntityType<?> entityType, BlockState state, IWorldReader world, BlockPos pos, @Nullable LivingEntity sleeper) {
      return world instanceof World ? BedBlock.func_220172_a(entityType, world, pos, 0) : Optional.empty();
   }

   default void setBedOccupied(BlockState state, IWorldReader world, BlockPos pos, LivingEntity sleeper, boolean occupied) {
      if (world instanceof IWorldWriter) {
         ((IWorldWriter)world).setBlockState(pos, (BlockState)state.with(BedBlock.OCCUPIED, occupied), 4);
      }

   }

   default Direction getBedDirection(BlockState state, IWorldReader world, BlockPos pos) {
      return (Direction)state.get(HorizontalBlock.HORIZONTAL_FACING);
   }

   default boolean isBedFoot(BlockState state, IWorldReader world, BlockPos pos) {
      return state.get(BedBlock.PART) == BedPart.FOOT;
   }

   default void beginLeaveDecay(BlockState state, IWorldReader world, BlockPos pos) {
   }

   default boolean isAir(BlockState state, IBlockReader world, BlockPos pos) {
      return state.getMaterial() == Material.AIR;
   }

   default boolean canBeReplacedByLeaves(BlockState state, IWorldReader world, BlockPos pos) {
      return this.isAir(state, world, pos) || state.isIn(BlockTags.LEAVES);
   }

   default boolean canBeReplacedByLogs(BlockState state, IWorldReader world, BlockPos pos) {
      return this.isAir(state, world, pos) || state.isIn(BlockTags.LEAVES) || this == Blocks.GRASS_BLOCK || state.isIn(Tags.Blocks.DIRT) || this.getBlock().isIn(BlockTags.LOGS) || this.getBlock().isIn(BlockTags.SAPLINGS) || this == Blocks.VINE;
   }

   default boolean isReplaceableOreGen(BlockState state, IWorldReader world, BlockPos pos, Predicate<BlockState> target) {
      return target.test(state);
   }

   default float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
      return this.getBlock().getExplosionResistance();
   }

   default boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
      return state.canProvidePower() && side != null;
   }

   default ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
      return this.getBlock().getItem(world, pos, state);
   }

   default boolean isFoliage(BlockState state, IWorldReader world, BlockPos pos) {
      return false;
   }

   default boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
      return false;
   }

   default boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   default boolean addHitEffects(BlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   default boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
      return false;
   }

   boolean canSustainPlant(BlockState var1, IBlockReader var2, BlockPos var3, Direction var4, IPlantable var5);

   default void onPlantGrow(BlockState state, IWorld world, BlockPos pos, BlockPos source) {
      if (state.isIn(Tags.Blocks.DIRT)) {
         world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
      }

   }

   default boolean isFertile(BlockState state, IBlockReader world, BlockPos pos) {
      if (this.getBlock() == Blocks.FARMLAND) {
         return (Integer)state.get(FarmlandBlock.MOISTURE) > 0;
      } else {
         return false;
      }
   }

   default boolean isBeaconBase(BlockState state, IWorldReader world, BlockPos pos, BlockPos beacon) {
      return Tags.Blocks.SUPPORTS_BEACON.contains(state.getBlock());
   }

   default boolean isPortalFrame(BlockState state, IWorldReader world, BlockPos pos) {
      return state.getBlock() == Blocks.OBSIDIAN;
   }

   default int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
      return 0;
   }

   default BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
      return state.rotate(direction);
   }

   @Nullable
   default Direction[] getValidRotations(BlockState state, IBlockReader world, BlockPos pos) {
      Iterator var4 = state.getProperties().iterator();

      IProperty prop;
      do {
         do {
            if (!var4.hasNext()) {
               return null;
            }

            prop = (IProperty)var4.next();
         } while(!prop.getName().equals("facing") && !prop.getName().equals("rotation"));
      } while(prop.getValueClass() != Direction.class);

      Collection<Direction> values = prop.getAllowedValues();
      return (Direction[])values.toArray(new Direction[values.size()]);
   }

   default float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
      return this.getBlock() == Blocks.BOOKSHELF ? 1.0F : 0.0F;
   }

   default boolean recolorBlock(BlockState state, IWorld world, BlockPos pos, Direction facing, DyeColor color) {
      Iterator var6 = state.getProperties().iterator();

      while(var6.hasNext()) {
         IProperty<?> prop = (IProperty)var6.next();
         if (prop.getName().equals("color") && prop.getValueClass() == DyeColor.class) {
            DyeColor current = (DyeColor)state.get(prop);
            if (current != color && prop.getAllowedValues().contains(color)) {
               world.setBlockState(pos, (BlockState)state.with(prop, color), 3);
               return true;
            }
         }
      }

      return false;
   }

   default void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
   }

   default void observedNeighborChange(BlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos) {
   }

   default boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
      return state.isNormalCube(world, pos);
   }

   default boolean getWeakChanges(BlockState state, IWorldReader world, BlockPos pos) {
      return false;
   }

   ToolType getHarvestTool(BlockState var1);

   int getHarvestLevel(BlockState var1);

   default boolean isToolEffective(BlockState state, ToolType tool) {
      if (tool != ToolType.PICKAXE || this.getBlock() != Blocks.REDSTONE_ORE && this.getBlock() != Blocks.REDSTONE_LAMP && this.getBlock() != Blocks.OBSIDIAN) {
         return tool == this.getHarvestTool(state);
      } else {
         return false;
      }
   }

   default BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
      return state;
   }

   default SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
      return this.getBlock().getSoundType(state);
   }

   @Nullable
   default float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
      return this.getBlock() instanceof IBeaconBeamColorProvider ? ((IBeaconBeamColorProvider)this.getBlock()).getColor().getColorComponentValues() : null;
   }

   @OnlyIn(Dist.CLIENT)
   default Vec3d getFogColor(BlockState state, IWorldReader world, BlockPos pos, Entity entity, Vec3d originalColor, float partialTicks) {
      if (state.getMaterial() == Material.WATER) {
         float f12 = 0.0F;
         if (entity instanceof LivingEntity) {
            LivingEntity ent = (LivingEntity)entity;
            f12 = (float)EnchantmentHelper.getRespirationModifier(ent) * 0.2F;
            if (ent.isPotionActive(Effects.WATER_BREATHING)) {
               f12 = f12 * 0.3F + 0.6F;
            }
         }

         return new Vec3d((double)(0.02F + f12), (double)(0.02F + f12), (double)(0.2F + f12));
      } else {
         return state.getMaterial() == Material.LAVA ? new Vec3d(0.6000000238418579D, 0.10000000149011612D, 0.0D) : originalColor;
      }
   }

   default BlockState getStateAtViewpoint(BlockState state, IBlockReader world, BlockPos pos, Vec3d viewpoint) {
      return state;
   }

   default BlockState getStateForPlacement(BlockState state, Direction facing, BlockState state2, IWorld world, BlockPos pos1, BlockPos pos2, Hand hand) {
      return this.getBlock().updatePostPlacement(state, facing, state2, world, pos1, pos2);
   }

   default boolean canBeConnectedTo(BlockState state, IBlockReader world, BlockPos pos, Direction facing) {
      return false;
   }

   @Nullable
   default PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
      return state.isBurning(world, pos) ? PathNodeType.DANGER_FIRE : null;
   }

   default boolean isSlimeBlock(BlockState state) {
      return state.getBlock() == Blocks.SLIME_BLOCK;
   }

   default boolean isStickyBlock(BlockState state) {
      return state.getBlock() == Blocks.SLIME_BLOCK || state.getBlock() == Blocks.field_226907_mc_;
   }

   default boolean canStickTo(BlockState state, BlockState other) {
      if (state.getBlock() == Blocks.field_226907_mc_ && other.getBlock() == Blocks.SLIME_BLOCK) {
         return false;
      } else if (state.getBlock() == Blocks.SLIME_BLOCK && other.getBlock() == Blocks.field_226907_mc_) {
         return false;
      } else {
         return state.isStickyBlock() || other.isStickyBlock();
      }
   }

   default int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
      return ((FireBlock)Blocks.FIRE).func_220274_q(state);
   }

   default boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
      return state.getFlammability(world, pos, face) > 0;
   }

   default void catchFire(BlockState state, World world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
   }

   default int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
      return ((FireBlock)Blocks.FIRE).func_220275_r(state);
   }

   default boolean isFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
      if (side != Direction.UP) {
         return false;
      } else if (this.getBlock() != Blocks.NETHERRACK && this.getBlock() != Blocks.MAGMA_BLOCK) {
         return world instanceof IWorldReader && ((IWorldReader)world).getDimension() instanceof EndDimension && this.getBlock() == Blocks.BEDROCK;
      } else {
         return true;
      }
   }

   default boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
      if (entity instanceof EnderDragonEntity) {
         return !BlockTags.DRAGON_IMMUNE.contains(this.getBlock());
      } else if (!(entity instanceof WitherEntity) && !(entity instanceof WitherSkullEntity)) {
         return true;
      } else {
         return state.isAir(world, pos) || WitherEntity.canDestroyBlock(state);
      }
   }

   @Nullable
   default RayTraceResult getRayTraceResult(BlockState state, World world, BlockPos pos, Vec3d start, Vec3d end, RayTraceResult original) {
      return original;
   }

   default boolean canDropFromExplosion(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
      return state.getBlock().canDropFromExplosion(explosion);
   }

   Set<ResourceLocation> getTags();

   default void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
      this.getBlock().onExplosionDestroy(world, pos, explosion);
   }

   default boolean collisionExtendsVertically(BlockState state, IBlockReader world, BlockPos pos, Entity collidingEntity) {
      return this.getBlock().isIn(BlockTags.FENCES) || this.getBlock().isIn(BlockTags.WALLS) || this.getBlock() instanceof FenceGateBlock;
   }
}
