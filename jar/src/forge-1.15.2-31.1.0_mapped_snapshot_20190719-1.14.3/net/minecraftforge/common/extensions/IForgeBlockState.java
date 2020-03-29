package net.minecraftforge.common.extensions;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;

public interface IForgeBlockState {
   default BlockState getBlockState() {
      return (BlockState)this;
   }

   default float getSlipperiness(IWorldReader world, BlockPos pos, @Nullable Entity entity) {
      return this.getBlockState().getBlock().getSlipperiness(this.getBlockState(), world, pos, entity);
   }

   default int getLightValue(IBlockReader world, BlockPos pos) {
      return this.getBlockState().getBlock().getLightValue(this.getBlockState(), world, pos);
   }

   default boolean isLadder(IWorldReader world, BlockPos pos, LivingEntity entity) {
      return this.getBlockState().getBlock().isLadder(this.getBlockState(), world, pos, entity);
   }

   default boolean hasTileEntity() {
      return this.getBlockState().getBlock().hasTileEntity(this.getBlockState());
   }

   @Nullable
   default TileEntity createTileEntity(IBlockReader world) {
      return this.getBlockState().getBlock().createTileEntity(this.getBlockState(), world);
   }

   default boolean canHarvestBlock(IBlockReader world, BlockPos pos, PlayerEntity player) {
      return this.getBlockState().getBlock().canHarvestBlock(this.getBlockState(), world, pos, player);
   }

   default boolean removedByPlayer(World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
      return this.getBlockState().getBlock().removedByPlayer(this.getBlockState(), world, pos, player, willHarvest, fluid);
   }

   default boolean isBed(IBlockReader world, BlockPos pos, @Nullable LivingEntity player) {
      return this.getBlockState().getBlock().isBed(this.getBlockState(), world, pos, player);
   }

   default boolean canCreatureSpawn(IWorldReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, EntityType<?> entityType) {
      return this.getBlockState().getBlock().canCreatureSpawn(this.getBlockState(), world, pos, type, entityType);
   }

   default Optional<Vec3d> getBedSpawnPosition(EntityType<?> type, IWorldReader world, BlockPos pos, @Nullable LivingEntity sleeper) {
      return this.getBlockState().getBlock().getBedSpawnPosition(type, this.getBlockState(), world, pos, sleeper);
   }

   default void setBedOccupied(IWorldReader world, BlockPos pos, LivingEntity sleeper, boolean occupied) {
      this.getBlockState().getBlock().setBedOccupied(this.getBlockState(), world, pos, sleeper, occupied);
   }

   default Direction getBedDirection(IWorldReader world, BlockPos pos) {
      return this.getBlockState().getBlock().getBedDirection(this.getBlockState(), world, pos);
   }

   default boolean isBedFoot(IWorldReader world, BlockPos pos) {
      return this.getBlockState().getBlock().isBedFoot(this.getBlockState(), world, pos);
   }

   default void beginLeaveDecay(IWorldReader world, BlockPos pos) {
      this.getBlockState().getBlock().beginLeaveDecay(this.getBlockState(), world, pos);
   }

   default boolean isAir(IBlockReader world, BlockPos pos) {
      return this.getBlockState().getBlock().isAir(this.getBlockState(), world, pos);
   }

   default boolean canBeReplacedByLeaves(IWorldReader world, BlockPos pos) {
      return this.getBlockState().getBlock().canBeReplacedByLeaves(this.getBlockState(), world, pos);
   }

   default boolean canBeReplacedByLogs(IWorldReader world, BlockPos pos) {
      return this.getBlockState().getBlock().canBeReplacedByLogs(this.getBlockState(), world, pos);
   }

   default boolean isReplaceableOreGen(IWorldReader world, BlockPos pos, Predicate<BlockState> target) {
      return this.getBlockState().getBlock().isReplaceableOreGen(this.getBlockState(), world, pos, target);
   }

   default float getExplosionResistance(IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
      return this.getBlockState().getBlock().getExplosionResistance(this.getBlockState(), world, pos, exploder, explosion);
   }

   default boolean canConnectRedstone(IBlockReader world, BlockPos pos, @Nullable Direction side) {
      return this.getBlockState().getBlock().canConnectRedstone(this.getBlockState(), world, pos, side);
   }

   default ItemStack getPickBlock(RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
      return this.getBlockState().getBlock().getPickBlock(this.getBlockState(), target, world, pos, player);
   }

   default boolean isFoliage(IWorldReader world, BlockPos pos) {
      return this.getBlockState().getBlock().isFoliage(this.getBlockState(), world, pos);
   }

   default boolean addLandingEffects(ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
      return this.getBlockState().getBlock().addLandingEffects(this.getBlockState(), worldserver, pos, state2, entity, numberOfParticles);
   }

   default boolean addRunningEffects(World world, BlockPos pos, Entity entity) {
      return this.getBlockState().getBlock().addRunningEffects(this.getBlockState(), world, pos, entity);
   }

   @OnlyIn(Dist.CLIENT)
   default boolean addHitEffects(World world, RayTraceResult target, ParticleManager manager) {
      return this.getBlockState().getBlock().addHitEffects(this.getBlockState(), world, target, manager);
   }

   @OnlyIn(Dist.CLIENT)
   default boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
      return this.getBlockState().getBlock().addDestroyEffects(this.getBlockState(), world, pos, manager);
   }

   default boolean canSustainPlant(IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
      return this.getBlockState().getBlock().canSustainPlant(this.getBlockState(), world, pos, facing, plantable);
   }

   default void onPlantGrow(IWorld world, BlockPos pos, BlockPos source) {
      this.getBlockState().getBlock().onPlantGrow(this.getBlockState(), world, pos, source);
   }

   default boolean isFertile(IBlockReader world, BlockPos pos) {
      return this.getBlockState().getBlock().isFertile(this.getBlockState(), world, pos);
   }

   default boolean isBeaconBase(IWorldReader world, BlockPos pos, BlockPos beacon) {
      return this.getBlockState().getBlock().isBeaconBase(this.getBlockState(), world, pos, beacon);
   }

   default boolean isPortalFrame(IWorldReader world, BlockPos pos) {
      return this.getBlockState().getBlock().isPortalFrame(this.getBlockState(), world, pos);
   }

   default int getExpDrop(IWorldReader world, BlockPos pos, int fortune, int silktouch) {
      return this.getBlockState().getBlock().getExpDrop(this.getBlockState(), world, pos, fortune, silktouch);
   }

   default BlockState rotate(IWorld world, BlockPos pos, Rotation direction) {
      return this.getBlockState().getBlock().rotate(this.getBlockState(), world, pos, direction);
   }

   default float getEnchantPowerBonus(IWorldReader world, BlockPos pos) {
      return this.getBlockState().getBlock().getEnchantPowerBonus(this.getBlockState(), world, pos);
   }

   default boolean recolorBlock(IWorld world, BlockPos pos, Direction facing, DyeColor color) {
      return this.getBlockState().getBlock().recolorBlock(this.getBlockState(), world, pos, facing, color);
   }

   default void onNeighborChange(IWorldReader world, BlockPos pos, BlockPos neighbor) {
      this.getBlockState().getBlock().onNeighborChange(this.getBlockState(), world, pos, neighbor);
   }

   default void observedNeighborChange(World world, BlockPos pos, Block changed, BlockPos changedPos) {
      this.getBlockState().getBlock().observedNeighborChange(this.getBlockState(), world, pos, changed, changedPos);
   }

   default boolean shouldCheckWeakPower(IWorldReader world, BlockPos pos, Direction side) {
      return this.getBlockState().getBlock().shouldCheckWeakPower(this.getBlockState(), world, pos, side);
   }

   default boolean getWeakChanges(IWorldReader world, BlockPos pos) {
      return this.getBlockState().getBlock().getWeakChanges(this.getBlockState(), world, pos);
   }

   default ToolType getHarvestTool() {
      return this.getBlockState().getBlock().getHarvestTool(this.getBlockState());
   }

   default int getHarvestLevel() {
      return this.getBlockState().getBlock().getHarvestLevel(this.getBlockState());
   }

   default boolean isToolEffective(ToolType tool) {
      return this.getBlockState().getBlock().isToolEffective(this.getBlockState(), tool);
   }

   default SoundType getSoundType(IWorldReader world, BlockPos pos, @Nullable Entity entity) {
      return this.getBlockState().getBlock().getSoundType(this.getBlockState(), world, pos, entity);
   }

   @Nullable
   default float[] getBeaconColorMultiplier(IWorldReader world, BlockPos pos, BlockPos beacon) {
      return this.getBlockState().getBlock().getBeaconColorMultiplier(this.getBlockState(), world, pos, beacon);
   }

   @OnlyIn(Dist.CLIENT)
   default Vec3d getFogColor(IWorldReader world, BlockPos pos, Entity entity, Vec3d originalColor, float partialTicks) {
      return this.getBlockState().getBlock().getFogColor(this.getBlockState(), world, pos, entity, originalColor, partialTicks);
   }

   default BlockState getStateAtViewpoint(IBlockReader world, BlockPos pos, Vec3d viewpoint) {
      return this.getBlockState().getBlock().getStateAtViewpoint(this.getBlockState(), world, pos, viewpoint);
   }

   default BlockState getStateForPlacement(Direction facing, BlockState state2, IWorld world, BlockPos pos1, BlockPos pos2, Hand hand) {
      return this.getBlockState().getBlock().getStateForPlacement(this.getBlockState(), facing, state2, world, pos1, pos2, hand);
   }

   default boolean canBeConnectedTo(IBlockReader world, BlockPos pos, Direction facing) {
      return this.getBlockState().getBlock().canBeConnectedTo(this.getBlockState(), world, pos, facing);
   }

   default boolean isSlimeBlock() {
      return this.getBlockState().getBlock().isSlimeBlock(this.getBlockState());
   }

   default boolean isStickyBlock() {
      return this.getBlockState().getBlock().isStickyBlock(this.getBlockState());
   }

   default boolean canStickTo(BlockState other) {
      return this.getBlockState().getBlock().canStickTo(this.getBlockState(), other);
   }

   default int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
      return this.getBlockState().getBlock().getFlammability(this.getBlockState(), world, pos, face);
   }

   default boolean isFlammable(IBlockReader world, BlockPos pos, Direction face) {
      return this.getBlockState().getBlock().isFlammable(this.getBlockState(), world, pos, face);
   }

   default void catchFire(World world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
      this.getBlockState().getBlock().catchFire(this.getBlockState(), world, pos, face, igniter);
   }

   default int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
      return this.getBlockState().getBlock().getFireSpreadSpeed(this.getBlockState(), world, pos, face);
   }

   default boolean isFireSource(IBlockReader world, BlockPos pos, Direction side) {
      return this.getBlockState().getBlock().isFireSource(this.getBlockState(), world, pos, side);
   }

   default boolean canEntityDestroy(IBlockReader world, BlockPos pos, Entity entity) {
      return this.getBlockState().getBlock().canEntityDestroy(this.getBlockState(), world, pos, entity);
   }

   @Nullable
   default Direction[] getValidRotations(IBlockReader world, BlockPos pos) {
      return this.getBlockState().getBlock().getValidRotations(this.getBlockState(), world, pos);
   }

   default boolean isBurning(IBlockReader world, BlockPos pos) {
      return this.getBlockState().getBlock().isBurning(this.getBlockState(), world, pos);
   }

   @Nullable
   default PathNodeType getAiPathNodeType(IBlockReader world, BlockPos pos) {
      return this.getAiPathNodeType(world, pos, (MobEntity)null);
   }

   @Nullable
   default PathNodeType getAiPathNodeType(IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
      return this.getBlockState().getBlock().getAiPathNodeType(this.getBlockState(), world, pos, entity);
   }

   default boolean canDropFromExplosion(IBlockReader world, BlockPos pos, Explosion explosion) {
      return this.getBlockState().getBlock().canDropFromExplosion(this.getBlockState(), world, pos, explosion);
   }

   default void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
      this.getBlockState().getBlock().onBlockExploded(this.getBlockState(), world, pos, explosion);
   }

   default boolean collisionExtendsVertically(IBlockReader world, BlockPos pos, Entity collidingEntity) {
      return this.getBlockState().getBlock().collisionExtendsVertically(this.getBlockState(), world, pos, collidingEntity);
   }
}
