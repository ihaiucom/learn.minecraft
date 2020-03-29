package net.minecraft.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.common.util.ReverseTagWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block extends ForgeRegistryEntry<Block> implements IItemProvider, IForgeBlock {
   protected static final Logger LOGGER = LogManager.getLogger();
   /** @deprecated */
   @Deprecated
   public static final ObjectIntIdentityMap<BlockState> BLOCK_STATE_IDS = GameData.getBlockStateIDMap();
   private static final Direction[] UPDATE_ORDER;
   private static final LoadingCache<VoxelShape, Boolean> OPAQUE_CACHE;
   private static final VoxelShape field_220083_b;
   private static final VoxelShape field_220084_c;
   protected final int lightValue;
   protected final float blockHardness;
   protected final float blockResistance;
   protected final boolean ticksRandomly;
   protected final SoundType soundType;
   protected final Material material;
   protected final MaterialColor materialColor;
   private final float slipperiness;
   private final float field_226886_f_;
   private final float field_226887_g_;
   protected final StateContainer<Block, BlockState> stateContainer;
   private BlockState defaultState;
   protected final boolean blocksMovement;
   private final boolean variableOpacity;
   private final boolean field_226888_j_;
   @Nullable
   private ResourceLocation lootTable;
   @Nullable
   private String translationKey;
   @Nullable
   private Item item;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> SHOULD_SIDE_RENDER_CACHE;
   protected Random RANDOM = new Random();
   private ToolType harvestTool;
   private int harvestLevel;
   private final ReverseTagWrapper<Block> reverseTags = new ReverseTagWrapper(this, BlockTags::getGeneration, BlockTags::getCollection);
   private final Supplier<ResourceLocation> lootTableSupplier;

   public static int getStateId(@Nullable BlockState p_196246_0_) {
      if (p_196246_0_ == null) {
         return 0;
      } else {
         int i = BLOCK_STATE_IDS.get(p_196246_0_);
         return i == -1 ? 0 : i;
      }
   }

   public static BlockState getStateById(int p_196257_0_) {
      BlockState blockstate = (BlockState)BLOCK_STATE_IDS.getByValue(p_196257_0_);
      return blockstate == null ? Blocks.AIR.getDefaultState() : blockstate;
   }

   public static Block getBlockFromItem(@Nullable Item p_149634_0_) {
      return p_149634_0_ instanceof BlockItem ? ((BlockItem)p_149634_0_).getBlock() : Blocks.AIR;
   }

   public static BlockState nudgeEntitiesWithNewState(BlockState p_199601_0_, BlockState p_199601_1_, World p_199601_2_, BlockPos p_199601_3_) {
      VoxelShape voxelshape = VoxelShapes.combine(p_199601_0_.getCollisionShape(p_199601_2_, p_199601_3_), p_199601_1_.getCollisionShape(p_199601_2_, p_199601_3_), IBooleanFunction.ONLY_SECOND).withOffset((double)p_199601_3_.getX(), (double)p_199601_3_.getY(), (double)p_199601_3_.getZ());
      Iterator var5 = p_199601_2_.getEntitiesWithinAABBExcludingEntity((Entity)null, voxelshape.getBoundingBox()).iterator();

      while(var5.hasNext()) {
         Entity entity = (Entity)var5.next();
         double d0 = VoxelShapes.getAllowedOffset(Direction.Axis.Y, entity.getBoundingBox().offset(0.0D, 1.0D, 0.0D), Stream.of(voxelshape), -1.0D);
         entity.setPositionAndUpdate(entity.func_226277_ct_(), entity.func_226278_cu_() + 1.0D + d0, entity.func_226281_cx_());
      }

      return p_199601_1_;
   }

   public static VoxelShape makeCuboidShape(double p_208617_0_, double p_208617_2_, double p_208617_4_, double p_208617_6_, double p_208617_8_, double p_208617_10_) {
      return VoxelShapes.create(p_208617_0_ / 16.0D, p_208617_2_ / 16.0D, p_208617_4_ / 16.0D, p_208617_6_ / 16.0D, p_208617_8_ / 16.0D, p_208617_10_ / 16.0D);
   }

   /** @deprecated */
   @Deprecated
   public boolean canEntitySpawn(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return p_220067_1_.func_224755_d(p_220067_2_, p_220067_3_, Direction.UP) && p_220067_1_.getLightValue(p_220067_2_, p_220067_3_) < 14;
   }

   /** @deprecated */
   @Deprecated
   public boolean isAir(BlockState p_196261_1_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public int getLightValue(BlockState p_149750_1_) {
      return this.lightValue;
   }

   /** @deprecated */
   @Deprecated
   public Material getMaterial(BlockState p_149688_1_) {
      return this.material;
   }

   /** @deprecated */
   @Deprecated
   public MaterialColor getMaterialColor(BlockState p_180659_1_, IBlockReader p_180659_2_, BlockPos p_180659_3_) {
      return this.materialColor;
   }

   /** @deprecated */
   @Deprecated
   public void updateNeighbors(BlockState p_196242_1_, IWorld p_196242_2_, BlockPos p_196242_3_, int p_196242_4_) {
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var6 = null;

      try {
         Direction[] var7 = UPDATE_ORDER;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Direction direction = var7[var9];
            blockpos$pooledmutable.setPos((Vec3i)p_196242_3_).move(direction);
            BlockState blockstate = p_196242_2_.getBlockState(blockpos$pooledmutable);
            BlockState blockstate1 = blockstate.updatePostPlacement(direction.getOpposite(), p_196242_1_, p_196242_2_, blockpos$pooledmutable, p_196242_3_);
            replaceBlock(blockstate, blockstate1, p_196242_2_, blockpos$pooledmutable, p_196242_4_);
         }
      } catch (Throwable var20) {
         var6 = var20;
         throw var20;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var6 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var19) {
                  var6.addSuppressed(var19);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

   }

   public boolean isIn(Tag<Block> p_203417_1_) {
      return p_203417_1_.contains(this);
   }

   public static BlockState getValidBlockForPosition(BlockState p_199770_0_, IWorld p_199770_1_, BlockPos p_199770_2_) {
      BlockState blockstate = p_199770_0_;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      Direction[] var5 = UPDATE_ORDER;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction direction = var5[var7];
         blockpos$mutable.setPos((Vec3i)p_199770_2_).move(direction);
         blockstate = blockstate.updatePostPlacement(direction, p_199770_1_.getBlockState(blockpos$mutable), p_199770_1_, p_199770_2_, blockpos$mutable);
      }

      return blockstate;
   }

   public static void replaceBlock(BlockState p_196263_0_, BlockState p_196263_1_, IWorld p_196263_2_, BlockPos p_196263_3_, int p_196263_4_) {
      if (p_196263_1_ != p_196263_0_) {
         if (p_196263_1_.isAir()) {
            if (!p_196263_2_.isRemote()) {
               p_196263_2_.destroyBlock(p_196263_3_, (p_196263_4_ & 32) == 0);
            }
         } else {
            p_196263_2_.setBlockState(p_196263_3_, p_196263_1_, p_196263_4_ & -33);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void updateDiagonalNeighbors(BlockState p_196248_1_, IWorld p_196248_2_, BlockPos p_196248_3_, int p_196248_4_) {
   }

   /** @deprecated */
   @Deprecated
   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_1_;
   }

   /** @deprecated */
   @Deprecated
   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_;
   }

   /** @deprecated */
   @Deprecated
   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_;
   }

   public Block(Block.Properties p_i48440_1_) {
      StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder(this);
      this.fillStateContainer(builder);
      this.material = p_i48440_1_.material;
      this.materialColor = p_i48440_1_.mapColor;
      this.blocksMovement = p_i48440_1_.blocksMovement;
      this.soundType = p_i48440_1_.soundType;
      this.lightValue = p_i48440_1_.lightValue;
      this.blockResistance = p_i48440_1_.resistance;
      this.blockHardness = p_i48440_1_.hardness;
      this.ticksRandomly = p_i48440_1_.ticksRandomly;
      this.harvestLevel = p_i48440_1_.harvestLevel;
      this.harvestTool = p_i48440_1_.harvestTool;
      ResourceLocation lootTableCache = p_i48440_1_.lootTable;
      this.lootTableSupplier = lootTableCache != null ? () -> {
         return lootTableCache;
      } : (p_i48440_1_.lootTableSupplier != null ? p_i48440_1_.lootTableSupplier : () -> {
         return new ResourceLocation(this.getRegistryName().getNamespace(), "blocks/" + this.getRegistryName().getPath());
      });
      this.slipperiness = p_i48440_1_.slipperiness;
      this.field_226886_f_ = p_i48440_1_.field_226893_j_;
      this.field_226887_g_ = p_i48440_1_.field_226894_k_;
      this.variableOpacity = p_i48440_1_.variableOpacity;
      this.lootTable = p_i48440_1_.lootTable;
      this.field_226888_j_ = p_i48440_1_.field_226895_m_;
      this.stateContainer = builder.create(BlockState::new);
      this.setDefaultState((BlockState)this.stateContainer.getBaseState());
   }

   public static boolean cannotAttach(Block p_220073_0_) {
      return p_220073_0_ instanceof LeavesBlock || p_220073_0_ == Blocks.BARRIER || p_220073_0_ == Blocks.CARVED_PUMPKIN || p_220073_0_ == Blocks.JACK_O_LANTERN || p_220073_0_ == Blocks.MELON || p_220073_0_ == Blocks.PUMPKIN || p_220073_0_.isIn(BlockTags.field_226150_J_);
   }

   /** @deprecated */
   @Deprecated
   public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
      return p_220081_1_.getMaterial().isOpaque() && p_220081_1_.func_224756_o(p_220081_2_, p_220081_3_) && !p_220081_1_.canProvidePower();
   }

   /** @deprecated */
   @Deprecated
   public boolean func_229869_c_(BlockState p_229869_1_, IBlockReader p_229869_2_, BlockPos p_229869_3_) {
      return this.material.blocksMovement() && p_229869_1_.func_224756_o(p_229869_2_, p_229869_3_);
   }

   /** @deprecated */
   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public boolean func_229870_f_(BlockState p_229870_1_, IBlockReader p_229870_2_, BlockPos p_229870_3_) {
      return p_229870_1_.func_229980_m_(p_229870_2_, p_229870_3_);
   }

   /** @deprecated */
   @Deprecated
   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return !p_196266_1_.func_224756_o(p_196266_2_, p_196266_3_);
      case WATER:
         return p_196266_2_.getFluidState(p_196266_3_).isTagged(FluidTags.WATER);
      case AIR:
         return !p_196266_1_.func_224756_o(p_196266_2_, p_196266_3_);
      default:
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   /** @deprecated */
   @Deprecated
   public boolean isReplaceable(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      return p_196253_1_.getMaterial().isReplaceable() && (p_196253_2_.getItem().isEmpty() || p_196253_2_.getItem().getItem() != this.asItem());
   }

   /** @deprecated */
   @Deprecated
   public boolean func_225541_a_(BlockState p_225541_1_, Fluid p_225541_2_) {
      return this.material.isReplaceable() || !this.material.isSolid();
   }

   /** @deprecated */
   @Deprecated
   public float getBlockHardness(BlockState p_176195_1_, IBlockReader p_176195_2_, BlockPos p_176195_3_) {
      return this.blockHardness;
   }

   public boolean ticksRandomly(BlockState p_149653_1_) {
      return this.ticksRandomly;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasTileEntity() {
      return this.hasTileEntity(this.getDefaultState());
   }

   /** @deprecated */
   @Deprecated
   public boolean needsPostProcessing(BlockState p_201783_1_, IBlockReader p_201783_2_, BlockPos p_201783_3_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public boolean func_225543_m_(BlockState p_225543_1_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean shouldSideBeRendered(BlockState p_176225_0_, IBlockReader p_176225_1_, BlockPos p_176225_2_, Direction p_176225_3_) {
      BlockPos blockpos = p_176225_2_.offset(p_176225_3_);
      BlockState blockstate = p_176225_1_.getBlockState(blockpos);
      if (p_176225_0_.isSideInvisible(blockstate, p_176225_3_)) {
         return false;
      } else if (blockstate.isSolid()) {
         Block.RenderSideCacheKey block$rendersidecachekey = new Block.RenderSideCacheKey(p_176225_0_, blockstate, p_176225_3_);
         Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = (Object2ByteLinkedOpenHashMap)SHOULD_SIDE_RENDER_CACHE.get();
         byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);
         if (b0 != 127) {
            return b0 != 0;
         } else {
            VoxelShape voxelshape = p_176225_0_.func_215702_a(p_176225_1_, p_176225_2_, p_176225_3_);
            VoxelShape voxelshape1 = blockstate.func_215702_a(p_176225_1_, blockpos, p_176225_3_.getOpposite());
            boolean flag = VoxelShapes.compare(voxelshape, voxelshape1, IBooleanFunction.ONLY_FIRST);
            if (object2bytelinkedopenhashmap.size() == 2048) {
               object2bytelinkedopenhashmap.removeLastByte();
            }

            object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte)(flag ? 1 : 0));
            return flag;
         }
      } else {
         return true;
      }
   }

   /** @deprecated */
   @Deprecated
   public final boolean isSolid(BlockState p_200124_1_) {
      return this.field_226888_j_;
   }

   /** @deprecated */
   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.fullCube();
   }

   /** @deprecated */
   @Deprecated
   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return this.blocksMovement ? p_220071_1_.getShape(p_220071_2_, p_220071_3_) : VoxelShapes.empty();
   }

   /** @deprecated */
   @Deprecated
   public VoxelShape getRenderShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      return p_196247_1_.getShape(p_196247_2_, p_196247_3_);
   }

   /** @deprecated */
   @Deprecated
   public VoxelShape getRaytraceShape(BlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
      return VoxelShapes.empty();
   }

   public static boolean func_220064_c(IBlockReader p_220064_0_, BlockPos p_220064_1_) {
      BlockState blockstate = p_220064_0_.getBlockState(p_220064_1_);
      return !blockstate.isIn(BlockTags.LEAVES) && !VoxelShapes.compare(blockstate.getCollisionShape(p_220064_0_, p_220064_1_).project(Direction.UP), field_220083_b, IBooleanFunction.ONLY_SECOND);
   }

   public static boolean func_220055_a(IWorldReader p_220055_0_, BlockPos p_220055_1_, Direction p_220055_2_) {
      BlockState blockstate = p_220055_0_.getBlockState(p_220055_1_);
      return !blockstate.isIn(BlockTags.LEAVES) && !VoxelShapes.compare(blockstate.getCollisionShape(p_220055_0_, p_220055_1_).project(p_220055_2_), field_220084_c, IBooleanFunction.ONLY_SECOND);
   }

   public static boolean hasSolidSide(BlockState p_220056_0_, IBlockReader p_220056_1_, BlockPos p_220056_2_, Direction p_220056_3_) {
      return !p_220056_0_.isIn(BlockTags.LEAVES) && doesSideFillSquare(p_220056_0_.getCollisionShape(p_220056_1_, p_220056_2_), p_220056_3_);
   }

   public static boolean doesSideFillSquare(VoxelShape p_208061_0_, Direction p_208061_1_) {
      VoxelShape voxelshape = p_208061_0_.project(p_208061_1_);
      return isOpaque(voxelshape);
   }

   public static boolean isOpaque(VoxelShape p_208062_0_) {
      return (Boolean)OPAQUE_CACHE.getUnchecked(p_208062_0_);
   }

   /** @deprecated */
   @Deprecated
   public final boolean isOpaqueCube(BlockState p_200012_1_, IBlockReader p_200012_2_, BlockPos p_200012_3_) {
      return p_200012_1_.isSolid() ? isOpaque(p_200012_1_.getRenderShape(p_200012_2_, p_200012_3_)) : false;
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return !isOpaque(p_200123_1_.getShape(p_200123_2_, p_200123_3_)) && p_200123_1_.getFluidState().isEmpty();
   }

   /** @deprecated */
   @Deprecated
   public int getOpacity(BlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      if (p_200011_1_.isOpaqueCube(p_200011_2_, p_200011_3_)) {
         return p_200011_2_.getMaxLightLevel();
      } else {
         return p_200011_1_.propagatesSkylightDown(p_200011_2_, p_200011_3_) ? 0 : 1;
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean func_220074_n(BlockState p_220074_1_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public void func_225542_b_(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      this.func_225534_a_(p_225542_1_, p_225542_2_, p_225542_3_, p_225542_4_);
   }

   /** @deprecated */
   @Deprecated
   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
   }

   public void onPlayerDestroy(IWorld p_176206_1_, BlockPos p_176206_2_, BlockState p_176206_3_) {
   }

   /** @deprecated */
   @Deprecated
   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      DebugPacketSender.func_218806_a(p_220069_2_, p_220069_3_);
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 10;
   }

   /** @deprecated */
   @Nullable
   @Deprecated
   public INamedContainerProvider getContainer(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
   }

   /** @deprecated */
   @Deprecated
   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.hasTileEntity() && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         p_196243_2_.removeTileEntity(p_196243_3_);
      }

   }

   /** @deprecated */
   @Deprecated
   public float getPlayerRelativeBlockHardness(BlockState p_180647_1_, PlayerEntity p_180647_2_, IBlockReader p_180647_3_, BlockPos p_180647_4_) {
      float f = p_180647_1_.getBlockHardness(p_180647_3_, p_180647_4_);
      if (f == -1.0F) {
         return 0.0F;
      } else {
         int i = ForgeHooks.canHarvestBlock(p_180647_1_, p_180647_2_, p_180647_3_, p_180647_4_) ? 30 : 100;
         return p_180647_2_.getDigSpeed(p_180647_1_, p_180647_4_) / f / (float)i;
      }
   }

   /** @deprecated */
   @Deprecated
   public void spawnAdditionalDrops(BlockState p_220062_1_, World p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
   }

   public ResourceLocation getLootTable() {
      if (this.lootTable == null) {
         this.lootTable = (ResourceLocation)this.lootTableSupplier.get();
      }

      return this.lootTable;
   }

   /** @deprecated */
   @Deprecated
   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      ResourceLocation resourcelocation = this.getLootTable();
      if (resourcelocation == LootTables.EMPTY) {
         return Collections.emptyList();
      } else {
         LootContext lootcontext = p_220076_2_.withParameter(LootParameters.BLOCK_STATE, p_220076_1_).build(LootParameterSets.BLOCK);
         ServerWorld serverworld = lootcontext.getWorld();
         LootTable loottable = serverworld.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);
         return loottable.generate(lootcontext);
      }
   }

   public static List<ItemStack> getDrops(BlockState p_220070_0_, ServerWorld p_220070_1_, BlockPos p_220070_2_, @Nullable TileEntity p_220070_3_) {
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(p_220070_1_)).withRandom(p_220070_1_.rand).withParameter(LootParameters.POSITION, p_220070_2_).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, p_220070_3_);
      return p_220070_0_.getDrops(lootcontext$builder);
   }

   public static List<ItemStack> getDrops(BlockState p_220077_0_, ServerWorld p_220077_1_, BlockPos p_220077_2_, @Nullable TileEntity p_220077_3_, @Nullable Entity p_220077_4_, ItemStack p_220077_5_) {
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(p_220077_1_)).withRandom(p_220077_1_.rand).withParameter(LootParameters.POSITION, p_220077_2_).withParameter(LootParameters.TOOL, p_220077_5_).withNullableParameter(LootParameters.THIS_ENTITY, p_220077_4_).withNullableParameter(LootParameters.BLOCK_ENTITY, p_220077_3_);
      return p_220077_0_.getDrops(lootcontext$builder);
   }

   public static void spawnDrops(BlockState p_220075_0_, World p_220075_1_, BlockPos p_220075_2_) {
      if (p_220075_1_ instanceof ServerWorld) {
         getDrops(p_220075_0_, (ServerWorld)p_220075_1_, p_220075_2_, (TileEntity)null).forEach((p_lambda$spawnDrops$3_2_) -> {
            spawnAsEntity(p_220075_1_, p_220075_2_, p_lambda$spawnDrops$3_2_);
         });
      }

      p_220075_0_.spawnAdditionalDrops(p_220075_1_, p_220075_2_, ItemStack.EMPTY);
   }

   public static void spawnDrops(BlockState p_220059_0_, World p_220059_1_, BlockPos p_220059_2_, @Nullable TileEntity p_220059_3_) {
      if (p_220059_1_ instanceof ServerWorld) {
         getDrops(p_220059_0_, (ServerWorld)p_220059_1_, p_220059_2_, p_220059_3_).forEach((p_lambda$spawnDrops$4_2_) -> {
            spawnAsEntity(p_220059_1_, p_220059_2_, p_lambda$spawnDrops$4_2_);
         });
      }

      p_220059_0_.spawnAdditionalDrops(p_220059_1_, p_220059_2_, ItemStack.EMPTY);
   }

   public static void spawnDrops(BlockState p_220054_0_, World p_220054_1_, BlockPos p_220054_2_, @Nullable TileEntity p_220054_3_, Entity p_220054_4_, ItemStack p_220054_5_) {
      if (p_220054_1_ instanceof ServerWorld) {
         getDrops(p_220054_0_, (ServerWorld)p_220054_1_, p_220054_2_, p_220054_3_, p_220054_4_, p_220054_5_).forEach((p_lambda$spawnDrops$5_2_) -> {
            spawnAsEntity(p_220054_1_, p_220054_2_, p_lambda$spawnDrops$5_2_);
         });
      }

      p_220054_0_.spawnAdditionalDrops(p_220054_1_, p_220054_2_, p_220054_5_);
   }

   public static void spawnAsEntity(World p_180635_0_, BlockPos p_180635_1_, ItemStack p_180635_2_) {
      if (!p_180635_0_.isRemote && !p_180635_2_.isEmpty() && p_180635_0_.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && !p_180635_0_.restoringBlockSnapshots) {
         float f = 0.5F;
         double d0 = (double)(p_180635_0_.rand.nextFloat() * 0.5F) + 0.25D;
         double d1 = (double)(p_180635_0_.rand.nextFloat() * 0.5F) + 0.25D;
         double d2 = (double)(p_180635_0_.rand.nextFloat() * 0.5F) + 0.25D;
         ItemEntity itementity = new ItemEntity(p_180635_0_, (double)p_180635_1_.getX() + d0, (double)p_180635_1_.getY() + d1, (double)p_180635_1_.getZ() + d2, p_180635_2_);
         itementity.setDefaultPickupDelay();
         p_180635_0_.addEntity(itementity);
      }

   }

   public void dropXpOnBlockBreak(World p_180637_1_, BlockPos p_180637_2_, int p_180637_3_) {
      if (!p_180637_1_.isRemote && p_180637_1_.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && !p_180637_1_.restoringBlockSnapshots) {
         while(p_180637_3_ > 0) {
            int i = ExperienceOrbEntity.getXPSplit(p_180637_3_);
            p_180637_3_ -= i;
            p_180637_1_.addEntity(new ExperienceOrbEntity(p_180637_1_, (double)p_180637_2_.getX() + 0.5D, (double)p_180637_2_.getY() + 0.5D, (double)p_180637_2_.getZ() + 0.5D, i));
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public float getExplosionResistance() {
      return this.blockResistance;
   }

   public void onExplosionDestroy(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_) {
   }

   /** @deprecated */
   @Deprecated
   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      return ActionResultType.PASS;
   }

   public void onEntityWalk(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState();
   }

   /** @deprecated */
   @Deprecated
   public void onBlockClicked(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
   }

   /** @deprecated */
   @Deprecated
   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return 0;
   }

   /** @deprecated */
   @Deprecated
   public boolean canProvidePower(BlockState p_149744_1_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
   }

   /** @deprecated */
   @Deprecated
   public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return 0;
   }

   public void harvestBlock(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      p_180657_2_.addStat(Stats.BLOCK_MINED.get(this));
      p_180657_2_.addExhaustion(0.005F);
      spawnDrops(p_180657_4_, p_180657_1_, p_180657_3_, p_180657_5_, p_180657_2_, p_180657_6_);
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
   }

   public boolean canSpawnInBlock() {
      return !this.material.isSolid() && !this.material.isLiquid();
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getNameTextComponent() {
      return new TranslationTextComponent(this.getTranslationKey(), new Object[0]);
   }

   public String getTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("block", Registry.BLOCK.getKey(this));
      }

      return this.translationKey;
   }

   /** @deprecated */
   @Deprecated
   public boolean eventReceived(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return this.material.getPushReaction();
   }

   /** @deprecated */
   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public float func_220080_a(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
      return p_220080_1_.func_224756_o(p_220080_2_, p_220080_3_) ? 0.2F : 1.0F;
   }

   public void onFallenUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      p_180658_3_.func_225503_b_(p_180658_4_, 1.0F);
   }

   public void onLanded(IBlockReader p_176216_1_, Entity p_176216_2_) {
      p_176216_2_.setMotion(p_176216_2_.getMotion().mul(1.0D, 0.0D, 1.0D));
   }

   /** @deprecated */
   @Deprecated
   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(this);
   }

   public void fillItemGroup(ItemGroup p_149666_1_, NonNullList<ItemStack> p_149666_2_) {
      p_149666_2_.add(new ItemStack(this));
   }

   /** @deprecated */
   @Deprecated
   public IFluidState getFluidState(BlockState p_204507_1_) {
      return Fluids.EMPTY.getDefaultState();
   }

   /** @deprecated */
   @Deprecated
   public float getSlipperiness() {
      return this.slipperiness;
   }

   public float func_226891_m_() {
      return this.field_226886_f_;
   }

   public float func_226892_n_() {
      return this.field_226887_g_;
   }

   /** @deprecated */
   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(BlockState p_209900_1_, BlockPos p_209900_2_) {
      return MathHelper.getPositionRandom(p_209900_2_);
   }

   public void onProjectileCollision(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, Entity p_220066_4_) {
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      p_176208_1_.playEvent(p_176208_4_, 2001, p_176208_2_, getStateId(p_176208_3_));
   }

   public void fillWithRain(World p_176224_1_, BlockPos p_176224_2_) {
   }

   /** @deprecated */
   @Deprecated
   public boolean canDropFromExplosion(Explosion p_149659_1_) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return 0;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
   }

   public StateContainer<Block, BlockState> getStateContainer() {
      return this.stateContainer;
   }

   protected final void setDefaultState(BlockState p_180632_1_) {
      this.defaultState = p_180632_1_;
   }

   public final BlockState getDefaultState() {
      return this.defaultState;
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.NONE;
   }

   /** @deprecated */
   @Deprecated
   public Vec3d getOffset(BlockState p_190949_1_, IBlockReader p_190949_2_, BlockPos p_190949_3_) {
      Block.OffsetType block$offsettype = this.getOffsetType();
      if (block$offsettype == Block.OffsetType.NONE) {
         return Vec3d.ZERO;
      } else {
         long i = MathHelper.getCoordinateRandom(p_190949_3_.getX(), 0, p_190949_3_.getZ());
         return new Vec3d(((double)((float)(i & 15L) / 15.0F) - 0.5D) * 0.5D, block$offsettype == Block.OffsetType.XYZ ? ((double)((float)(i >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D : 0.0D, ((double)((float)(i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
      }
   }

   /** @deprecated */
   @Deprecated
   public SoundType getSoundType(BlockState p_220072_1_) {
      return this.soundType;
   }

   public Item asItem() {
      if (this.item == null) {
         this.item = Item.getItemFromBlock(this);
      }

      return (Item)this.item.delegate.get();
   }

   public boolean isVariableOpacity() {
      return this.variableOpacity;
   }

   public String toString() {
      return "Block{" + this.getRegistryName() + "}";
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
   }

   public float getSlipperiness(BlockState p_getSlipperiness_1_, IWorldReader p_getSlipperiness_2_, BlockPos p_getSlipperiness_3_, @Nullable Entity p_getSlipperiness_4_) {
      return this.slipperiness;
   }

   @Nullable
   public ToolType getHarvestTool(BlockState p_getHarvestTool_1_) {
      return this.harvestTool;
   }

   public int getHarvestLevel(BlockState p_getHarvestLevel_1_) {
      return this.harvestLevel;
   }

   public boolean canSustainPlant(BlockState p_canSustainPlant_1_, IBlockReader p_canSustainPlant_2_, BlockPos p_canSustainPlant_3_, Direction p_canSustainPlant_4_, IPlantable p_canSustainPlant_5_) {
      BlockState plant = p_canSustainPlant_5_.getPlant(p_canSustainPlant_2_, p_canSustainPlant_3_.offset(p_canSustainPlant_4_));
      PlantType type = p_canSustainPlant_5_.getPlantType(p_canSustainPlant_2_, p_canSustainPlant_3_.offset(p_canSustainPlant_4_));
      if (plant.getBlock() == Blocks.CACTUS) {
         return this.getBlock() == Blocks.CACTUS || this.getBlock() == Blocks.SAND || this.getBlock() == Blocks.RED_SAND;
      } else if (plant.getBlock() == Blocks.SUGAR_CANE && this == Blocks.SUGAR_CANE) {
         return true;
      } else if (p_canSustainPlant_5_ instanceof BushBlock && ((BushBlock)p_canSustainPlant_5_).isValidGround(p_canSustainPlant_1_, p_canSustainPlant_2_, p_canSustainPlant_3_)) {
         return true;
      } else {
         switch(type) {
         case Desert:
            return this.getBlock() == Blocks.SAND || this.getBlock() == Blocks.TERRACOTTA || this.getBlock() instanceof GlazedTerracottaBlock;
         case Nether:
            return this.getBlock() == Blocks.SOUL_SAND;
         case Crop:
            return this.getBlock() == Blocks.FARMLAND;
         case Cave:
            return hasSolidSide(p_canSustainPlant_1_, p_canSustainPlant_2_, p_canSustainPlant_3_, Direction.UP);
         case Plains:
            return this.getBlock() == Blocks.GRASS_BLOCK || Tags.Blocks.DIRT.contains(this) || this.getBlock() == Blocks.FARMLAND;
         case Water:
            return p_canSustainPlant_1_.getMaterial() == Material.WATER;
         case Beach:
            boolean isBeach = this.getBlock() == Blocks.GRASS_BLOCK || Tags.Blocks.DIRT.contains(this) || this.getBlock() == Blocks.SAND;
            boolean hasWater = p_canSustainPlant_2_.getBlockState(p_canSustainPlant_3_.east()).getMaterial() == Material.WATER || p_canSustainPlant_2_.getBlockState(p_canSustainPlant_3_.west()).getMaterial() == Material.WATER || p_canSustainPlant_2_.getBlockState(p_canSustainPlant_3_.north()).getMaterial() == Material.WATER || p_canSustainPlant_2_.getBlockState(p_canSustainPlant_3_.south()).getMaterial() == Material.WATER;
            return isBeach && hasWater;
         default:
            return false;
         }
      }
   }

   public final Set<ResourceLocation> getTags() {
      return this.reverseTags.getTagNames();
   }

   static {
      UPDATE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
      OPAQUE_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, Boolean>() {
         public Boolean load(VoxelShape p_load_1_) {
            return !VoxelShapes.compare(VoxelShapes.fullCube(), p_load_1_, IBooleanFunction.NOT_SAME);
         }
      });
      field_220083_b = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
      field_220084_c = makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D);
      SHOULD_SIDE_RENDER_CACHE = ThreadLocal.withInitial(() -> {
         Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(2048, 0.25F) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
         return object2bytelinkedopenhashmap;
      });
      ForgeHooks.setBlockToolSetter((p_lambda$static$6_0_, p_lambda$static$6_1_, p_lambda$static$6_2_) -> {
         p_lambda$static$6_0_.harvestTool = p_lambda$static$6_1_;
         p_lambda$static$6_0_.harvestLevel = p_lambda$static$6_2_;
      });
   }

   public static final class RenderSideCacheKey {
      private final BlockState state;
      private final BlockState adjacentState;
      private final Direction side;

      public RenderSideCacheKey(BlockState p_i49791_1_, BlockState p_i49791_2_, Direction p_i49791_3_) {
         this.state = p_i49791_1_;
         this.adjacentState = p_i49791_2_;
         this.side = p_i49791_3_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof Block.RenderSideCacheKey)) {
            return false;
         } else {
            Block.RenderSideCacheKey block$rendersidecachekey = (Block.RenderSideCacheKey)p_equals_1_;
            return this.state == block$rendersidecachekey.state && this.adjacentState == block$rendersidecachekey.adjacentState && this.side == block$rendersidecachekey.side;
         }
      }

      public int hashCode() {
         int i = this.state.hashCode();
         i = 31 * i + this.adjacentState.hashCode();
         i = 31 * i + this.side.hashCode();
         return i;
      }
   }

   public static class Properties {
      private Material material;
      private MaterialColor mapColor;
      private boolean blocksMovement = true;
      private SoundType soundType;
      private int lightValue;
      private float resistance;
      private float hardness;
      private boolean ticksRandomly;
      private float slipperiness;
      private float field_226893_j_;
      private float field_226894_k_;
      private ResourceLocation lootTable;
      private boolean field_226895_m_;
      private boolean variableOpacity;
      private int harvestLevel;
      private ToolType harvestTool;
      private Supplier<ResourceLocation> lootTableSupplier;

      private Properties(Material p_i48616_1_, MaterialColor p_i48616_2_) {
         this.soundType = SoundType.STONE;
         this.slipperiness = 0.6F;
         this.field_226893_j_ = 1.0F;
         this.field_226894_k_ = 1.0F;
         this.field_226895_m_ = true;
         this.harvestLevel = -1;
         this.material = p_i48616_1_;
         this.mapColor = p_i48616_2_;
      }

      public static Block.Properties create(Material p_200945_0_) {
         return create(p_200945_0_, p_200945_0_.getColor());
      }

      public static Block.Properties create(Material p_200952_0_, DyeColor p_200952_1_) {
         return create(p_200952_0_, p_200952_1_.getMapColor());
      }

      public static Block.Properties create(Material p_200949_0_, MaterialColor p_200949_1_) {
         return new Block.Properties(p_200949_0_, p_200949_1_);
      }

      public static Block.Properties from(Block p_200950_0_) {
         Block.Properties block$properties = new Block.Properties(p_200950_0_.material, p_200950_0_.materialColor);
         block$properties.material = p_200950_0_.material;
         block$properties.hardness = p_200950_0_.blockHardness;
         block$properties.resistance = p_200950_0_.blockResistance;
         block$properties.blocksMovement = p_200950_0_.blocksMovement;
         block$properties.ticksRandomly = p_200950_0_.ticksRandomly;
         block$properties.lightValue = p_200950_0_.lightValue;
         block$properties.mapColor = p_200950_0_.materialColor;
         block$properties.soundType = p_200950_0_.soundType;
         block$properties.slipperiness = p_200950_0_.getSlipperiness();
         block$properties.field_226893_j_ = p_200950_0_.func_226891_m_();
         block$properties.variableOpacity = p_200950_0_.variableOpacity;
         block$properties.field_226895_m_ = p_200950_0_.field_226888_j_;
         block$properties.harvestLevel = p_200950_0_.harvestLevel;
         block$properties.harvestTool = p_200950_0_.harvestTool;
         return block$properties;
      }

      public Block.Properties doesNotBlockMovement() {
         this.blocksMovement = false;
         this.field_226895_m_ = false;
         return this;
      }

      public Block.Properties func_226896_b_() {
         this.field_226895_m_ = false;
         return this;
      }

      public Block.Properties slipperiness(float p_200941_1_) {
         this.slipperiness = p_200941_1_;
         return this;
      }

      public Block.Properties func_226897_b_(float p_226897_1_) {
         this.field_226893_j_ = p_226897_1_;
         return this;
      }

      public Block.Properties func_226898_c_(float p_226898_1_) {
         this.field_226894_k_ = p_226898_1_;
         return this;
      }

      public Block.Properties sound(SoundType p_200947_1_) {
         this.soundType = p_200947_1_;
         return this;
      }

      public Block.Properties lightValue(int p_200951_1_) {
         this.lightValue = p_200951_1_;
         return this;
      }

      public Block.Properties hardnessAndResistance(float p_200948_1_, float p_200948_2_) {
         this.hardness = p_200948_1_;
         this.resistance = Math.max(0.0F, p_200948_2_);
         return this;
      }

      protected Block.Properties zeroHardnessAndResistance() {
         return this.hardnessAndResistance(0.0F);
      }

      public Block.Properties hardnessAndResistance(float p_200943_1_) {
         this.hardnessAndResistance(p_200943_1_, p_200943_1_);
         return this;
      }

      public Block.Properties tickRandomly() {
         this.ticksRandomly = true;
         return this;
      }

      public Block.Properties variableOpacity() {
         this.variableOpacity = true;
         return this;
      }

      public Block.Properties harvestLevel(int p_harvestLevel_1_) {
         this.harvestLevel = p_harvestLevel_1_;
         return this;
      }

      public Block.Properties harvestTool(ToolType p_harvestTool_1_) {
         this.harvestTool = p_harvestTool_1_;
         return this;
      }

      public Block.Properties noDrops() {
         this.lootTable = LootTables.EMPTY;
         return this;
      }

      public Block.Properties lootFrom(Block p_222379_1_) {
         this.lootTableSupplier = () -> {
            return ((Block)p_222379_1_.delegate.get()).getLootTable();
         };
         return this;
      }
   }

   public static enum OffsetType {
      NONE,
      XZ,
      XYZ;
   }
}
