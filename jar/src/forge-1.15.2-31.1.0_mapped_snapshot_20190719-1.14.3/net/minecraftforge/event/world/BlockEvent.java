package net.minecraftforge.event.world;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;

public class BlockEvent extends Event {
   private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockEvent", "false"));
   private final IWorld world;
   private final BlockPos pos;
   private final BlockState state;

   public BlockEvent(IWorld world, BlockPos pos, BlockState state) {
      this.pos = pos;
      this.world = world;
      this.state = state;
   }

   public IWorld getWorld() {
      return this.world;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getState() {
      return this.state;
   }

   @Cancelable
   public static class PortalSpawnEvent extends BlockEvent {
      private final NetherPortalBlock.Size size;

      public PortalSpawnEvent(IWorld world, BlockPos pos, BlockState state, NetherPortalBlock.Size size) {
         super(world, pos, state);
         this.size = size;
      }

      public NetherPortalBlock.Size getPortalSize() {
         return this.size;
      }
   }

   @Cancelable
   public static class FarmlandTrampleEvent extends BlockEvent {
      private final Entity entity;
      private final float fallDistance;

      public FarmlandTrampleEvent(World world, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
         super(world, pos, state);
         this.entity = entity;
         this.fallDistance = fallDistance;
      }

      public Entity getEntity() {
         return this.entity;
      }

      public float getFallDistance() {
         return this.fallDistance;
      }
   }

   public static class CropGrowEvent extends BlockEvent {
      public CropGrowEvent(World world, BlockPos pos, BlockState state) {
         super(world, pos, state);
      }

      public static class Post extends BlockEvent.CropGrowEvent {
         private final BlockState originalState;

         public Post(World world, BlockPos pos, BlockState original, BlockState state) {
            super(world, pos, state);
            this.originalState = original;
         }

         public BlockState getOriginalState() {
            return this.originalState;
         }
      }

      @HasResult
      public static class Pre extends BlockEvent.CropGrowEvent {
         public Pre(World world, BlockPos pos, BlockState state) {
            super(world, pos, state);
         }
      }
   }

   @Cancelable
   public static class FluidPlaceBlockEvent extends BlockEvent {
      private final BlockPos liquidPos;
      private BlockState newState;
      private BlockState origState;

      public FluidPlaceBlockEvent(IWorld world, BlockPos pos, BlockPos liquidPos, BlockState state) {
         super(world, pos, state);
         this.liquidPos = liquidPos;
         this.newState = state;
         this.origState = world.getBlockState(pos);
      }

      public BlockPos getLiquidPos() {
         return this.liquidPos;
      }

      public BlockState getNewState() {
         return this.newState;
      }

      public void setNewState(BlockState state) {
         this.newState = state;
      }

      public BlockState getOriginalState() {
         return this.origState;
      }
   }

   @HasResult
   public static class CreateFluidSourceEvent extends BlockEvent {
      public CreateFluidSourceEvent(World world, BlockPos pos, BlockState state) {
         super(world, pos, state);
      }
   }

   @Cancelable
   public static class NeighborNotifyEvent extends BlockEvent {
      private final EnumSet<Direction> notifiedSides;
      private final boolean forceRedstoneUpdate;

      public NeighborNotifyEvent(World world, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
         super(world, pos, state);
         this.notifiedSides = notifiedSides;
         this.forceRedstoneUpdate = forceRedstoneUpdate;
      }

      public EnumSet<Direction> getNotifiedSides() {
         return this.notifiedSides;
      }

      public boolean getForceRedstoneUpdate() {
         return this.forceRedstoneUpdate;
      }
   }

   @Cancelable
   public static class EntityMultiPlaceEvent extends BlockEvent.EntityPlaceEvent {
      private final List<BlockSnapshot> blockSnapshots;

      public EntityMultiPlaceEvent(@Nonnull List<BlockSnapshot> blockSnapshots, @Nonnull BlockState placedAgainst, @Nullable Entity entity) {
         super((BlockSnapshot)blockSnapshots.get(0), placedAgainst, entity);
         this.blockSnapshots = ImmutableList.copyOf(blockSnapshots);
         if (BlockEvent.DEBUG) {
            System.out.printf("Created EntityMultiPlaceEvent - [PlacedAgainst: %s ][Entity: %s ]\n", placedAgainst, entity);
         }

      }

      public List<BlockSnapshot> getReplacedBlockSnapshots() {
         return this.blockSnapshots;
      }
   }

   @Cancelable
   public static class EntityPlaceEvent extends BlockEvent {
      private final Entity entity;
      private final BlockSnapshot blockSnapshot;
      private final BlockState placedBlock;
      private final BlockState placedAgainst;

      public EntityPlaceEvent(@Nonnull BlockSnapshot blockSnapshot, @Nonnull BlockState placedAgainst, @Nullable Entity entity) {
         super(blockSnapshot.getWorld(), blockSnapshot.getPos(), !(entity instanceof PlayerEntity) ? blockSnapshot.getReplacedBlock() : blockSnapshot.getCurrentBlock());
         this.entity = entity;
         this.blockSnapshot = blockSnapshot;
         this.placedBlock = !(entity instanceof PlayerEntity) ? blockSnapshot.getReplacedBlock() : blockSnapshot.getCurrentBlock();
         this.placedAgainst = placedAgainst;
         if (BlockEvent.DEBUG) {
            System.out.printf("Created EntityPlaceEvent - [PlacedBlock: %s ][PlacedAgainst: %s ][Entity: %s ]\n", this.getPlacedBlock(), placedAgainst, entity);
         }

      }

      @Nullable
      public Entity getEntity() {
         return this.entity;
      }

      public BlockSnapshot getBlockSnapshot() {
         return this.blockSnapshot;
      }

      public BlockState getPlacedBlock() {
         return this.placedBlock;
      }

      public BlockState getPlacedAgainst() {
         return this.placedAgainst;
      }
   }

   @Cancelable
   public static class BreakEvent extends BlockEvent {
      private final PlayerEntity player;
      private int exp;

      public BreakEvent(World world, BlockPos pos, BlockState state, PlayerEntity player) {
         super(world, pos, state);
         this.player = player;
         if (state != null && ForgeHooks.canHarvestBlock(state, player, world, pos)) {
            int bonusLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand());
            int silklevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand());
            this.exp = state.getExpDrop(world, pos, bonusLevel, silklevel);
         } else {
            this.exp = 0;
         }

      }

      public PlayerEntity getPlayer() {
         return this.player;
      }

      public int getExpToDrop() {
         return this.isCanceled() ? 0 : this.exp;
      }

      public void setExpToDrop(int exp) {
         this.exp = exp;
      }
   }

   public static class HarvestDropsEvent extends BlockEvent {
      private final int fortuneLevel;
      private final NonNullList<ItemStack> drops;
      private final boolean isSilkTouching;
      private float dropChance;
      private final PlayerEntity harvester;

      public HarvestDropsEvent(World world, BlockPos pos, BlockState state, int fortuneLevel, float dropChance, NonNullList<ItemStack> drops, PlayerEntity harvester, boolean isSilkTouching) {
         super(world, pos, state);
         this.fortuneLevel = fortuneLevel;
         this.setDropChance(dropChance);
         this.drops = drops;
         this.isSilkTouching = isSilkTouching;
         this.harvester = harvester;
      }

      public int getFortuneLevel() {
         return this.fortuneLevel;
      }

      public List<ItemStack> getDrops() {
         return this.drops;
      }

      public boolean isSilkTouching() {
         return this.isSilkTouching;
      }

      public float getDropChance() {
         return this.dropChance;
      }

      public void setDropChance(float dropChance) {
         this.dropChance = dropChance;
      }

      public PlayerEntity getHarvester() {
         return this.harvester;
      }
   }
}
