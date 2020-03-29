package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class BlockState extends StateHolder<Block, BlockState> implements IStateHolder<BlockState>, IForgeBlockState {
   @Nullable
   private BlockState.Cache cache;
   private final int lightLevel;
   private final boolean field_215709_e;

   public BlockState(Block p_i49958_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_i49958_2_) {
      super(p_i49958_1_, p_i49958_2_);
      this.lightLevel = p_i49958_1_.getLightValue(this);
      this.field_215709_e = p_i49958_1_.func_220074_n(this);
   }

   public void func_215692_c() {
      if (!this.getBlock().isVariableOpacity()) {
         this.cache = new BlockState.Cache(this);
      }

   }

   public Block getBlock() {
      return (Block)this.object;
   }

   public Material getMaterial() {
      return this.getBlock().getMaterial(this);
   }

   public boolean canEntitySpawn(IBlockReader p_215688_1_, BlockPos p_215688_2_, EntityType<?> p_215688_3_) {
      return this.getBlock().canEntitySpawn(this, p_215688_1_, p_215688_2_, p_215688_3_);
   }

   public boolean propagatesSkylightDown(IBlockReader p_200131_1_, BlockPos p_200131_2_) {
      return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().propagatesSkylightDown(this, p_200131_1_, p_200131_2_);
   }

   public int getOpacity(IBlockReader p_200016_1_, BlockPos p_200016_2_) {
      return this.cache != null ? this.cache.opacity : this.getBlock().getOpacity(this, p_200016_1_, p_200016_2_);
   }

   public VoxelShape func_215702_a(IBlockReader p_215702_1_, BlockPos p_215702_2_, Direction p_215702_3_) {
      return this.cache != null && this.cache.renderShapes != null ? this.cache.renderShapes[p_215702_3_.ordinal()] : VoxelShapes.func_216387_a(this.getRenderShape(p_215702_1_, p_215702_2_), p_215702_3_);
   }

   public boolean func_215704_f() {
      return this.cache == null || this.cache.isCollisionShapeLargerThanFullBlock;
   }

   public boolean func_215691_g() {
      return this.field_215709_e;
   }

   public int getLightValue() {
      return this.lightLevel;
   }

   /** @deprecated */
   @Deprecated
   public boolean isAir() {
      return this.getBlock().isAir(this);
   }

   /** @deprecated */
   @Deprecated
   public MaterialColor getMaterialColor(IBlockReader p_185909_1_, BlockPos p_185909_2_) {
      return this.getBlock().getMaterialColor(this, p_185909_1_, p_185909_2_);
   }

   public BlockState rotate(Rotation p_185907_1_) {
      return this.getBlock().rotate(this, p_185907_1_);
   }

   public BlockState mirror(Mirror p_185902_1_) {
      return this.getBlock().mirror(this, p_185902_1_);
   }

   public BlockRenderType getRenderType() {
      return this.getBlock().getRenderType(this);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_227035_k_() {
      return this.getBlock().func_225543_m_(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_215703_d(IBlockReader p_215703_1_, BlockPos p_215703_2_) {
      return this.getBlock().func_220080_a(this, p_215703_1_, p_215703_2_);
   }

   public boolean isNormalCube(IBlockReader p_215686_1_, BlockPos p_215686_2_) {
      return this.getBlock().isNormalCube(this, p_215686_1_, p_215686_2_);
   }

   public boolean canProvidePower() {
      return this.getBlock().canProvidePower(this);
   }

   public int getWeakPower(IBlockReader p_185911_1_, BlockPos p_185911_2_, Direction p_185911_3_) {
      return this.getBlock().getWeakPower(this, p_185911_1_, p_185911_2_, p_185911_3_);
   }

   public boolean hasComparatorInputOverride() {
      return this.getBlock().hasComparatorInputOverride(this);
   }

   public int getComparatorInputOverride(World p_185888_1_, BlockPos p_185888_2_) {
      return this.getBlock().getComparatorInputOverride(this, p_185888_1_, p_185888_2_);
   }

   public float getBlockHardness(IBlockReader p_185887_1_, BlockPos p_185887_2_) {
      return this.getBlock().getBlockHardness(this, p_185887_1_, p_185887_2_);
   }

   public float getPlayerRelativeBlockHardness(PlayerEntity p_185903_1_, IBlockReader p_185903_2_, BlockPos p_185903_3_) {
      return this.getBlock().getPlayerRelativeBlockHardness(this, p_185903_1_, p_185903_2_, p_185903_3_);
   }

   public int getStrongPower(IBlockReader p_185893_1_, BlockPos p_185893_2_, Direction p_185893_3_) {
      return this.getBlock().getStrongPower(this, p_185893_1_, p_185893_2_, p_185893_3_);
   }

   public PushReaction getPushReaction() {
      return this.getBlock().getPushReaction(this);
   }

   public boolean isOpaqueCube(IBlockReader p_200015_1_, BlockPos p_200015_2_) {
      return this.cache != null ? this.cache.opaqueCube : this.getBlock().isOpaqueCube(this, p_200015_1_, p_200015_2_);
   }

   public boolean isSolid() {
      return this.cache != null ? this.cache.solid : this.getBlock().isSolid(this);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(BlockState p_200017_1_, Direction p_200017_2_) {
      return this.getBlock().isSideInvisible(this, p_200017_1_, p_200017_2_);
   }

   public VoxelShape getShape(IBlockReader p_196954_1_, BlockPos p_196954_2_) {
      return this.getShape(p_196954_1_, p_196954_2_, ISelectionContext.dummy());
   }

   public VoxelShape getShape(IBlockReader p_215700_1_, BlockPos p_215700_2_, ISelectionContext p_215700_3_) {
      return this.getBlock().getShape(this, p_215700_1_, p_215700_2_, p_215700_3_);
   }

   public VoxelShape getCollisionShape(IBlockReader p_196952_1_, BlockPos p_196952_2_) {
      return this.cache != null ? this.cache.field_230026_g : this.getCollisionShape(p_196952_1_, p_196952_2_, ISelectionContext.dummy());
   }

   public VoxelShape getCollisionShape(IBlockReader p_215685_1_, BlockPos p_215685_2_, ISelectionContext p_215685_3_) {
      return this.getBlock().getCollisionShape(this, p_215685_1_, p_215685_2_, p_215685_3_);
   }

   public VoxelShape getRenderShape(IBlockReader p_196951_1_, BlockPos p_196951_2_) {
      return this.getBlock().getRenderShape(this, p_196951_1_, p_196951_2_);
   }

   public VoxelShape getRaytraceShape(IBlockReader p_199611_1_, BlockPos p_199611_2_) {
      return this.getBlock().getRaytraceShape(this, p_199611_1_, p_199611_2_);
   }

   public final boolean func_215682_a(IBlockReader p_215682_1_, BlockPos p_215682_2_, Entity p_215682_3_) {
      return Block.doesSideFillSquare(this.getCollisionShape(p_215682_1_, p_215682_2_, ISelectionContext.forEntity(p_215682_3_)), Direction.UP);
   }

   public Vec3d getOffset(IBlockReader p_191059_1_, BlockPos p_191059_2_) {
      return this.getBlock().getOffset(this, p_191059_1_, p_191059_2_);
   }

   public boolean onBlockEventReceived(World p_189547_1_, BlockPos p_189547_2_, int p_189547_3_, int p_189547_4_) {
      return this.getBlock().eventReceived(this, p_189547_1_, p_189547_2_, p_189547_3_, p_189547_4_);
   }

   public void neighborChanged(World p_215697_1_, BlockPos p_215697_2_, Block p_215697_3_, BlockPos p_215697_4_, boolean p_215697_5_) {
      this.getBlock().neighborChanged(this, p_215697_1_, p_215697_2_, p_215697_3_, p_215697_4_, p_215697_5_);
   }

   public void updateNeighbors(IWorld p_196946_1_, BlockPos p_196946_2_, int p_196946_3_) {
      this.getBlock().updateNeighbors(this, p_196946_1_, p_196946_2_, p_196946_3_);
   }

   public void updateDiagonalNeighbors(IWorld p_196948_1_, BlockPos p_196948_2_, int p_196948_3_) {
      this.getBlock().updateDiagonalNeighbors(this, p_196948_1_, p_196948_2_, p_196948_3_);
   }

   public void onBlockAdded(World p_215705_1_, BlockPos p_215705_2_, BlockState p_215705_3_, boolean p_215705_4_) {
      this.getBlock().onBlockAdded(this, p_215705_1_, p_215705_2_, p_215705_3_, p_215705_4_);
   }

   public void onReplaced(World p_196947_1_, BlockPos p_196947_2_, BlockState p_196947_3_, boolean p_196947_4_) {
      this.getBlock().onReplaced(this, p_196947_1_, p_196947_2_, p_196947_3_, p_196947_4_);
   }

   public void func_227033_a_(ServerWorld p_227033_1_, BlockPos p_227033_2_, Random p_227033_3_) {
      this.getBlock().func_225534_a_(this, p_227033_1_, p_227033_2_, p_227033_3_);
   }

   public void func_227034_b_(ServerWorld p_227034_1_, BlockPos p_227034_2_, Random p_227034_3_) {
      this.getBlock().func_225542_b_(this, p_227034_1_, p_227034_2_, p_227034_3_);
   }

   public void onEntityCollision(World p_196950_1_, BlockPos p_196950_2_, Entity p_196950_3_) {
      this.getBlock().onEntityCollision(this, p_196950_1_, p_196950_2_, p_196950_3_);
   }

   public void spawnAdditionalDrops(World p_215706_1_, BlockPos p_215706_2_, ItemStack p_215706_3_) {
      this.getBlock().spawnAdditionalDrops(this, p_215706_1_, p_215706_2_, p_215706_3_);
   }

   public List<ItemStack> getDrops(LootContext.Builder p_215693_1_) {
      return this.getBlock().getDrops(this, p_215693_1_);
   }

   public ActionResultType func_227031_a_(World p_227031_1_, PlayerEntity p_227031_2_, Hand p_227031_3_, BlockRayTraceResult p_227031_4_) {
      return this.getBlock().func_225533_a_(this, p_227031_1_, p_227031_4_.getPos(), p_227031_2_, p_227031_3_, p_227031_4_);
   }

   public void onBlockClicked(World p_196942_1_, BlockPos p_196942_2_, PlayerEntity p_196942_3_) {
      this.getBlock().onBlockClicked(this, p_196942_1_, p_196942_2_, p_196942_3_);
   }

   public boolean func_229980_m_(IBlockReader p_229980_1_, BlockPos p_229980_2_) {
      return this.getBlock().func_229869_c_(this, p_229980_1_, p_229980_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean causesSuffocation(IBlockReader p_215696_1_, BlockPos p_215696_2_) {
      return this.getBlock().func_229870_f_(this, p_215696_1_, p_215696_2_);
   }

   public BlockState updatePostPlacement(Direction p_196956_1_, BlockState p_196956_2_, IWorld p_196956_3_, BlockPos p_196956_4_, BlockPos p_196956_5_) {
      return this.getBlock().updatePostPlacement(this, p_196956_1_, p_196956_2_, p_196956_3_, p_196956_4_, p_196956_5_);
   }

   public boolean allowsMovement(IBlockReader p_196957_1_, BlockPos p_196957_2_, PathType p_196957_3_) {
      return this.getBlock().allowsMovement(this, p_196957_1_, p_196957_2_, p_196957_3_);
   }

   public boolean isReplaceable(BlockItemUseContext p_196953_1_) {
      return this.getBlock().isReplaceable(this, p_196953_1_);
   }

   public boolean func_227032_a_(Fluid p_227032_1_) {
      return this.getBlock().func_225541_a_(this, p_227032_1_);
   }

   public boolean isValidPosition(IWorldReader p_196955_1_, BlockPos p_196955_2_) {
      return this.getBlock().isValidPosition(this, p_196955_1_, p_196955_2_);
   }

   public boolean blockNeedsPostProcessing(IBlockReader p_202065_1_, BlockPos p_202065_2_) {
      return this.getBlock().needsPostProcessing(this, p_202065_1_, p_202065_2_);
   }

   @Nullable
   public INamedContainerProvider getContainer(World p_215699_1_, BlockPos p_215699_2_) {
      return this.getBlock().getContainer(this, p_215699_1_, p_215699_2_);
   }

   public boolean isIn(Tag<Block> p_203425_1_) {
      return this.getBlock().isIn(p_203425_1_);
   }

   public IFluidState getFluidState() {
      return this.getBlock().getFluidState(this);
   }

   public boolean ticksRandomly() {
      return this.getBlock().ticksRandomly(this);
   }

   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(BlockPos p_209533_1_) {
      return this.getBlock().getPositionRandom(this, p_209533_1_);
   }

   public SoundType getSoundType() {
      return this.getBlock().getSoundType(this);
   }

   public void onProjectileCollision(World p_215690_1_, BlockState p_215690_2_, BlockRayTraceResult p_215690_3_, Entity p_215690_4_) {
      this.getBlock().onProjectileCollision(p_215690_1_, p_215690_2_, p_215690_3_, p_215690_4_);
   }

   public boolean func_224755_d(IBlockReader p_224755_1_, BlockPos p_224755_2_, Direction p_224755_3_) {
      return this.cache != null ? this.cache.field_225493_i[p_224755_3_.ordinal()] : Block.hasSolidSide(this, p_224755_1_, p_224755_2_, p_224755_3_);
   }

   public boolean func_224756_o(IBlockReader p_224756_1_, BlockPos p_224756_2_) {
      return this.cache != null ? this.cache.field_225494_j : Block.isOpaque(this.getCollisionShape(p_224756_1_, p_224756_2_));
   }

   public static <T> Dynamic<T> serialize(DynamicOps<T> p_215689_0_, BlockState p_215689_1_) {
      ImmutableMap<IProperty<?>, Comparable<?>> immutablemap = p_215689_1_.getValues();
      Object t;
      if (immutablemap.isEmpty()) {
         t = p_215689_0_.createMap(ImmutableMap.of(p_215689_0_.createString("Name"), p_215689_0_.createString(Registry.BLOCK.getKey(p_215689_1_.getBlock()).toString())));
      } else {
         t = p_215689_0_.createMap(ImmutableMap.of(p_215689_0_.createString("Name"), p_215689_0_.createString(Registry.BLOCK.getKey(p_215689_1_.getBlock()).toString()), p_215689_0_.createString("Properties"), p_215689_0_.createMap((Map)immutablemap.entrySet().stream().map((p_lambda$serialize$0_1_) -> {
            return Pair.of(p_215689_0_.createString(((IProperty)p_lambda$serialize$0_1_.getKey()).getName()), p_215689_0_.createString(IStateHolder.func_215670_b((IProperty)p_lambda$serialize$0_1_.getKey(), (Comparable)p_lambda$serialize$0_1_.getValue())));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
      }

      return new Dynamic(p_215689_0_, t);
   }

   public static <T> BlockState deserialize(Dynamic<T> p_215698_0_) {
      DefaultedRegistry var10000 = Registry.BLOCK;
      Optional var10003 = p_215698_0_.getElement("Name");
      DynamicOps var10004 = p_215698_0_.getOps();
      var10004.getClass();
      Block block = (Block)var10000.getOrDefault(new ResourceLocation((String)var10003.flatMap(var10004::getStringValue).orElse("minecraft:air")));
      Map<String, String> map = p_215698_0_.get("Properties").asMap((p_lambda$deserialize$1_0_) -> {
         return p_lambda$deserialize$1_0_.asString("");
      }, (p_lambda$deserialize$2_0_) -> {
         return p_lambda$deserialize$2_0_.asString("");
      });
      BlockState blockstate = block.getDefaultState();
      StateContainer<Block, BlockState> statecontainer = block.getStateContainer();
      Iterator var5 = map.entrySet().iterator();

      while(var5.hasNext()) {
         Entry<String, String> entry = (Entry)var5.next();
         String s = (String)entry.getKey();
         IProperty<?> iproperty = statecontainer.getProperty(s);
         if (iproperty != null) {
            blockstate = (BlockState)IStateHolder.func_215671_a(blockstate, iproperty, s, p_215698_0_.toString(), (String)entry.getValue());
         }
      }

      return blockstate;
   }

   static final class Cache {
      private static final Direction[] DIRECTIONS = Direction.values();
      private final boolean solid;
      private final boolean opaqueCube;
      private final boolean propagatesSkylightDown;
      private final int opacity;
      private final VoxelShape[] renderShapes;
      private final VoxelShape field_230026_g;
      private final boolean isCollisionShapeLargerThanFullBlock;
      private final boolean[] field_225493_i;
      private final boolean field_225494_j;

      private Cache(BlockState p_i50627_1_) {
         Block block = p_i50627_1_.getBlock();
         this.solid = block.isSolid(p_i50627_1_);
         this.opaqueCube = block.isOpaqueCube(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
         this.propagatesSkylightDown = block.propagatesSkylightDown(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
         this.opacity = block.getOpacity(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
         int var5;
         if (!p_i50627_1_.isSolid()) {
            this.renderShapes = null;
         } else {
            this.renderShapes = new VoxelShape[DIRECTIONS.length];
            VoxelShape voxelshape = block.getRenderShape(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
            Direction[] var4 = DIRECTIONS;
            var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Direction direction = var4[var6];
               this.renderShapes[direction.ordinal()] = VoxelShapes.func_216387_a(voxelshape, direction);
            }
         }

         this.field_230026_g = block.getCollisionShape(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO, ISelectionContext.dummy());
         this.isCollisionShapeLargerThanFullBlock = Arrays.stream(Direction.Axis.values()).anyMatch((p_lambda$new$0_1_) -> {
            return this.field_230026_g.getStart(p_lambda$new$0_1_) < 0.0D || this.field_230026_g.getEnd(p_lambda$new$0_1_) > 1.0D;
         });
         this.field_225493_i = new boolean[6];
         Direction[] var8 = DIRECTIONS;
         int var9 = var8.length;

         for(var5 = 0; var5 < var9; ++var5) {
            Direction direction1 = var8[var5];
            this.field_225493_i[direction1.ordinal()] = Block.hasSolidSide(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO, direction1);
         }

         this.field_225494_j = Block.isOpaque(p_i50627_1_.getCollisionShape(EmptyBlockReader.INSTANCE, BlockPos.ZERO));
      }

      // $FF: synthetic method
      Cache(BlockState p_i50628_1_, Object p_i50628_2_) {
         this(p_i50628_1_);
      }
   }
}
