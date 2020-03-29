package net.minecraft.util.palette;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.StemGrownBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction8;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final UpgradeData EMPTY = new UpgradeData();
   private static final Direction8[] field_208832_b = Direction8.values();
   private final EnumSet<Direction8> field_196995_b;
   private final int[][] field_196996_c;
   private static final Map<Block, UpgradeData.IBlockFixer> field_196997_d = new IdentityHashMap();
   private static final Set<UpgradeData.IBlockFixer> FIXERS = Sets.newHashSet();

   private UpgradeData() {
      this.field_196995_b = EnumSet.noneOf(Direction8.class);
      this.field_196996_c = new int[16][];
   }

   public UpgradeData(CompoundNBT p_i47714_1_) {
      this();
      if (p_i47714_1_.contains("Indices", 10)) {
         CompoundNBT lvt_2_1_ = p_i47714_1_.getCompound("Indices");

         for(int lvt_3_1_ = 0; lvt_3_1_ < this.field_196996_c.length; ++lvt_3_1_) {
            String lvt_4_1_ = String.valueOf(lvt_3_1_);
            if (lvt_2_1_.contains(lvt_4_1_, 11)) {
               this.field_196996_c[lvt_3_1_] = lvt_2_1_.getIntArray(lvt_4_1_);
            }
         }
      }

      int lvt_2_2_ = p_i47714_1_.getInt("Sides");
      Direction8[] var8 = Direction8.values();
      int var9 = var8.length;

      for(int var5 = 0; var5 < var9; ++var5) {
         Direction8 lvt_6_1_ = var8[var5];
         if ((lvt_2_2_ & 1 << lvt_6_1_.ordinal()) != 0) {
            this.field_196995_b.add(lvt_6_1_);
         }
      }

   }

   public void postProcessChunk(Chunk p_196990_1_) {
      this.func_196989_a(p_196990_1_);
      Direction8[] var2 = field_208832_b;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction8 lvt_5_1_ = var2[var4];
         func_196991_a(p_196990_1_, lvt_5_1_);
      }

      World lvt_2_1_ = p_196990_1_.getWorld();
      FIXERS.forEach((p_208829_1_) -> {
         p_208829_1_.func_208826_a(lvt_2_1_);
      });
   }

   private static void func_196991_a(Chunk p_196991_0_, Direction8 p_196991_1_) {
      World lvt_2_1_ = p_196991_0_.getWorld();
      if (p_196991_0_.getUpgradeData().field_196995_b.remove(p_196991_1_)) {
         Set<Direction> lvt_3_1_ = p_196991_1_.getDirections();
         int lvt_4_1_ = false;
         int lvt_5_1_ = true;
         boolean lvt_6_1_ = lvt_3_1_.contains(Direction.EAST);
         boolean lvt_7_1_ = lvt_3_1_.contains(Direction.WEST);
         boolean lvt_8_1_ = lvt_3_1_.contains(Direction.SOUTH);
         boolean lvt_9_1_ = lvt_3_1_.contains(Direction.NORTH);
         boolean lvt_10_1_ = lvt_3_1_.size() == 1;
         ChunkPos lvt_11_1_ = p_196991_0_.getPos();
         int lvt_12_1_ = lvt_11_1_.getXStart() + (lvt_10_1_ && (lvt_9_1_ || lvt_8_1_) ? 1 : (lvt_7_1_ ? 0 : 15));
         int lvt_13_1_ = lvt_11_1_.getXStart() + (!lvt_10_1_ || !lvt_9_1_ && !lvt_8_1_ ? (lvt_7_1_ ? 0 : 15) : 14);
         int lvt_14_1_ = lvt_11_1_.getZStart() + (!lvt_10_1_ || !lvt_6_1_ && !lvt_7_1_ ? (lvt_9_1_ ? 0 : 15) : 1);
         int lvt_15_1_ = lvt_11_1_.getZStart() + (!lvt_10_1_ || !lvt_6_1_ && !lvt_7_1_ ? (lvt_9_1_ ? 0 : 15) : 14);
         Direction[] lvt_16_1_ = Direction.values();
         BlockPos.Mutable lvt_17_1_ = new BlockPos.Mutable();
         Iterator var18 = BlockPos.getAllInBoxMutable(lvt_12_1_, 0, lvt_14_1_, lvt_13_1_, lvt_2_1_.getHeight() - 1, lvt_15_1_).iterator();

         while(var18.hasNext()) {
            BlockPos lvt_19_1_ = (BlockPos)var18.next();
            BlockState lvt_20_1_ = lvt_2_1_.getBlockState(lvt_19_1_);
            BlockState lvt_21_1_ = lvt_20_1_;
            Direction[] var22 = lvt_16_1_;
            int var23 = lvt_16_1_.length;

            for(int var24 = 0; var24 < var23; ++var24) {
               Direction lvt_25_1_ = var22[var24];
               lvt_17_1_.setPos((Vec3i)lvt_19_1_).move(lvt_25_1_);
               lvt_21_1_ = func_196987_a(lvt_21_1_, lvt_25_1_, lvt_2_1_, lvt_19_1_, lvt_17_1_);
            }

            Block.replaceBlock(lvt_20_1_, lvt_21_1_, lvt_2_1_, lvt_19_1_, 18);
         }

      }
   }

   private static BlockState func_196987_a(BlockState p_196987_0_, Direction p_196987_1_, IWorld p_196987_2_, BlockPos p_196987_3_, BlockPos p_196987_4_) {
      return ((UpgradeData.IBlockFixer)field_196997_d.getOrDefault(p_196987_0_.getBlock(), UpgradeData.BlockFixers.DEFAULT)).func_196982_a(p_196987_0_, p_196987_1_, p_196987_2_.getBlockState(p_196987_4_), p_196987_2_, p_196987_3_, p_196987_4_);
   }

   private void func_196989_a(Chunk p_196989_1_) {
      BlockPos.PooledMutable lvt_2_1_ = BlockPos.PooledMutable.retain();
      Throwable var3 = null;

      try {
         BlockPos.PooledMutable lvt_4_1_ = BlockPos.PooledMutable.retain();
         Throwable var5 = null;

         try {
            ChunkPos lvt_6_1_ = p_196989_1_.getPos();
            IWorld lvt_7_1_ = p_196989_1_.getWorld();

            int lvt_8_1_;
            for(lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_) {
               ChunkSection lvt_9_1_ = p_196989_1_.getSections()[lvt_8_1_];
               int[] lvt_10_1_ = this.field_196996_c[lvt_8_1_];
               this.field_196996_c[lvt_8_1_] = null;
               if (lvt_9_1_ != null && lvt_10_1_ != null && lvt_10_1_.length > 0) {
                  Direction[] lvt_11_1_ = Direction.values();
                  PalettedContainer<BlockState> lvt_12_1_ = lvt_9_1_.getData();
                  int[] var13 = lvt_10_1_;
                  int var14 = lvt_10_1_.length;

                  for(int var15 = 0; var15 < var14; ++var15) {
                     int lvt_16_1_ = var13[var15];
                     int lvt_17_1_ = lvt_16_1_ & 15;
                     int lvt_18_1_ = lvt_16_1_ >> 8 & 15;
                     int lvt_19_1_ = lvt_16_1_ >> 4 & 15;
                     lvt_2_1_.setPos(lvt_6_1_.getXStart() + lvt_17_1_, (lvt_8_1_ << 4) + lvt_18_1_, lvt_6_1_.getZStart() + lvt_19_1_);
                     BlockState lvt_20_1_ = (BlockState)lvt_12_1_.get(lvt_16_1_);
                     BlockState lvt_21_1_ = lvt_20_1_;
                     Direction[] var22 = lvt_11_1_;
                     int var23 = lvt_11_1_.length;

                     for(int var24 = 0; var24 < var23; ++var24) {
                        Direction lvt_25_1_ = var22[var24];
                        lvt_4_1_.setPos((Vec3i)lvt_2_1_).move(lvt_25_1_);
                        if (lvt_2_1_.getX() >> 4 == lvt_6_1_.x && lvt_2_1_.getZ() >> 4 == lvt_6_1_.z) {
                           lvt_21_1_ = func_196987_a(lvt_21_1_, lvt_25_1_, lvt_7_1_, lvt_2_1_, lvt_4_1_);
                        }
                     }

                     Block.replaceBlock(lvt_20_1_, lvt_21_1_, lvt_7_1_, lvt_2_1_, 18);
                  }
               }
            }

            for(lvt_8_1_ = 0; lvt_8_1_ < this.field_196996_c.length; ++lvt_8_1_) {
               if (this.field_196996_c[lvt_8_1_] != null) {
                  LOGGER.warn("Discarding update data for section {} for chunk ({} {})", lvt_8_1_, lvt_6_1_.x, lvt_6_1_.z);
               }

               this.field_196996_c[lvt_8_1_] = null;
            }

         } catch (Throwable var47) {
            var5 = var47;
            throw var47;
         } finally {
            if (lvt_4_1_ != null) {
               if (var5 != null) {
                  try {
                     lvt_4_1_.close();
                  } catch (Throwable var46) {
                     var5.addSuppressed(var46);
                  }
               } else {
                  lvt_4_1_.close();
               }
            }

         }
      } catch (Throwable var49) {
         var3 = var49;
         throw var49;
      } finally {
         if (lvt_2_1_ != null) {
            if (var3 != null) {
               try {
                  lvt_2_1_.close();
               } catch (Throwable var45) {
                  var3.addSuppressed(var45);
               }
            } else {
               lvt_2_1_.close();
            }
         }

      }
   }

   public boolean isEmpty() {
      int[][] var1 = this.field_196996_c;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int[] lvt_4_1_ = var1[var3];
         if (lvt_4_1_ != null) {
            return false;
         }
      }

      return this.field_196995_b.isEmpty();
   }

   public CompoundNBT write() {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      CompoundNBT lvt_2_1_ = new CompoundNBT();

      int lvt_3_2_;
      for(lvt_3_2_ = 0; lvt_3_2_ < this.field_196996_c.length; ++lvt_3_2_) {
         String lvt_4_1_ = String.valueOf(lvt_3_2_);
         if (this.field_196996_c[lvt_3_2_] != null && this.field_196996_c[lvt_3_2_].length != 0) {
            lvt_2_1_.putIntArray(lvt_4_1_, this.field_196996_c[lvt_3_2_]);
         }
      }

      if (!lvt_2_1_.isEmpty()) {
         lvt_1_1_.put("Indices", lvt_2_1_);
      }

      lvt_3_2_ = 0;

      Direction8 lvt_5_1_;
      for(Iterator var6 = this.field_196995_b.iterator(); var6.hasNext(); lvt_3_2_ |= 1 << lvt_5_1_.ordinal()) {
         lvt_5_1_ = (Direction8)var6.next();
      }

      lvt_1_1_.putByte("Sides", (byte)lvt_3_2_);
      return lvt_1_1_;
   }

   static enum BlockFixers implements UpgradeData.IBlockFixer {
      BLACKLIST(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN}) {
         public BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            return p_196982_1_;
         }
      },
      DEFAULT(new Block[0]) {
         public BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            return p_196982_1_.updatePostPlacement(p_196982_2_, p_196982_4_.getBlockState(p_196982_6_), p_196982_4_, p_196982_5_, p_196982_6_);
         }
      },
      CHEST(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}) {
         public BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            if (p_196982_3_.getBlock() == p_196982_1_.getBlock() && p_196982_2_.getAxis().isHorizontal() && p_196982_1_.get(ChestBlock.TYPE) == ChestType.SINGLE && p_196982_3_.get(ChestBlock.TYPE) == ChestType.SINGLE) {
               Direction lvt_7_1_ = (Direction)p_196982_1_.get(ChestBlock.FACING);
               if (p_196982_2_.getAxis() != lvt_7_1_.getAxis() && lvt_7_1_ == p_196982_3_.get(ChestBlock.FACING)) {
                  ChestType lvt_8_1_ = p_196982_2_ == lvt_7_1_.rotateY() ? ChestType.LEFT : ChestType.RIGHT;
                  p_196982_4_.setBlockState(p_196982_6_, (BlockState)p_196982_3_.with(ChestBlock.TYPE, lvt_8_1_.opposite()), 18);
                  if (lvt_7_1_ == Direction.NORTH || lvt_7_1_ == Direction.EAST) {
                     TileEntity lvt_9_1_ = p_196982_4_.getTileEntity(p_196982_5_);
                     TileEntity lvt_10_1_ = p_196982_4_.getTileEntity(p_196982_6_);
                     if (lvt_9_1_ instanceof ChestTileEntity && lvt_10_1_ instanceof ChestTileEntity) {
                        ChestTileEntity.swapContents((ChestTileEntity)lvt_9_1_, (ChestTileEntity)lvt_10_1_);
                     }
                  }

                  return (BlockState)p_196982_1_.with(ChestBlock.TYPE, lvt_8_1_);
               }
            }

            return p_196982_1_;
         }
      },
      LEAVES(true, new Block[]{Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES}) {
         private final ThreadLocal<List<ObjectSet<BlockPos>>> field_208828_g = ThreadLocal.withInitial(() -> {
            return Lists.newArrayListWithCapacity(7);
         });

         public BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            BlockState lvt_7_1_ = p_196982_1_.updatePostPlacement(p_196982_2_, p_196982_4_.getBlockState(p_196982_6_), p_196982_4_, p_196982_5_, p_196982_6_);
            if (p_196982_1_ != lvt_7_1_) {
               int lvt_8_1_ = (Integer)lvt_7_1_.get(BlockStateProperties.DISTANCE_1_7);
               List<ObjectSet<BlockPos>> lvt_9_1_ = (List)this.field_208828_g.get();
               if (lvt_9_1_.isEmpty()) {
                  for(int lvt_10_1_ = 0; lvt_10_1_ < 7; ++lvt_10_1_) {
                     lvt_9_1_.add(new ObjectOpenHashSet());
                  }
               }

               ((ObjectSet)lvt_9_1_.get(lvt_8_1_)).add(p_196982_5_.toImmutable());
            }

            return p_196982_1_;
         }

         public void func_208826_a(IWorld p_208826_1_) {
            BlockPos.Mutable lvt_2_1_ = new BlockPos.Mutable();
            List<ObjectSet<BlockPos>> lvt_3_1_ = (List)this.field_208828_g.get();

            label44:
            for(int lvt_4_1_ = 2; lvt_4_1_ < lvt_3_1_.size(); ++lvt_4_1_) {
               int lvt_5_1_ = lvt_4_1_ - 1;
               ObjectSet<BlockPos> lvt_6_1_ = (ObjectSet)lvt_3_1_.get(lvt_5_1_);
               ObjectSet<BlockPos> lvt_7_1_ = (ObjectSet)lvt_3_1_.get(lvt_4_1_);
               ObjectIterator var8 = lvt_6_1_.iterator();

               while(true) {
                  BlockPos lvt_9_1_;
                  BlockState lvt_10_1_;
                  do {
                     do {
                        if (!var8.hasNext()) {
                           continue label44;
                        }

                        lvt_9_1_ = (BlockPos)var8.next();
                        lvt_10_1_ = p_208826_1_.getBlockState(lvt_9_1_);
                     } while((Integer)lvt_10_1_.get(BlockStateProperties.DISTANCE_1_7) < lvt_5_1_);

                     p_208826_1_.setBlockState(lvt_9_1_, (BlockState)lvt_10_1_.with(BlockStateProperties.DISTANCE_1_7, lvt_5_1_), 18);
                  } while(lvt_4_1_ == 7);

                  Direction[] var11 = field_208827_f;
                  int var12 = var11.length;

                  for(int var13 = 0; var13 < var12; ++var13) {
                     Direction lvt_14_1_ = var11[var13];
                     lvt_2_1_.setPos((Vec3i)lvt_9_1_).move(lvt_14_1_);
                     BlockState lvt_15_1_ = p_208826_1_.getBlockState(lvt_2_1_);
                     if (lvt_15_1_.has(BlockStateProperties.DISTANCE_1_7) && (Integer)lvt_10_1_.get(BlockStateProperties.DISTANCE_1_7) > lvt_4_1_) {
                        lvt_7_1_.add(lvt_2_1_.toImmutable());
                     }
                  }
               }
            }

            lvt_3_1_.clear();
         }
      },
      STEM_BLOCK(new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM}) {
         public BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            if ((Integer)p_196982_1_.get(StemBlock.AGE) == 7) {
               StemGrownBlock lvt_7_1_ = ((StemBlock)p_196982_1_.getBlock()).getCrop();
               if (p_196982_3_.getBlock() == lvt_7_1_) {
                  return (BlockState)lvt_7_1_.getAttachedStem().getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, p_196982_2_);
               }
            }

            return p_196982_1_;
         }
      };

      public static final Direction[] field_208827_f = Direction.values();

      private BlockFixers(Block... p_i47847_3_) {
         this(false, p_i47847_3_);
      }

      private BlockFixers(boolean p_i49366_3_, Block... p_i49366_4_) {
         Block[] var5 = p_i49366_4_;
         int var6 = p_i49366_4_.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Block lvt_8_1_ = var5[var7];
            UpgradeData.field_196997_d.put(lvt_8_1_, this);
         }

         if (p_i49366_3_) {
            UpgradeData.FIXERS.add(this);
         }

      }

      // $FF: synthetic method
      BlockFixers(Block[] p_i47848_3_, Object p_i47848_4_) {
         this(p_i47848_3_);
      }

      // $FF: synthetic method
      BlockFixers(boolean p_i49367_3_, Block[] p_i49367_4_, Object p_i49367_5_) {
         this(p_i49367_3_, p_i49367_4_);
      }
   }

   public interface IBlockFixer {
      BlockState func_196982_a(BlockState var1, Direction var2, BlockState var3, IWorld var4, BlockPos var5, BlockPos var6);

      default void func_208826_a(IWorld p_208826_1_) {
      }
   }
}
