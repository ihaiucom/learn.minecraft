package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.block.JigsawBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JigsawManager {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final JigsawPatternRegistry field_214891_a = new JigsawPatternRegistry();

   public static void func_214889_a(ResourceLocation p_214889_0_, int p_214889_1_, JigsawManager.IPieceFactory p_214889_2_, ChunkGenerator<?> p_214889_3_, TemplateManager p_214889_4_, BlockPos p_214889_5_, List<StructurePiece> p_214889_6_, Random p_214889_7_) {
      Structures.init();
      new JigsawManager.Assembler(p_214889_0_, p_214889_1_, p_214889_2_, p_214889_3_, p_214889_4_, p_214889_5_, p_214889_6_, p_214889_7_);
   }

   static {
      field_214891_a.register(JigsawPattern.EMPTY);
   }

   public interface IPieceFactory {
      AbstractVillagePiece create(TemplateManager var1, JigsawPiece var2, BlockPos var3, int var4, Rotation var5, MutableBoundingBox var6);
   }

   static final class Assembler {
      private final int field_214882_a;
      private final JigsawManager.IPieceFactory field_214883_b;
      private final ChunkGenerator<?> field_214884_c;
      private final TemplateManager field_214885_d;
      private final List<StructurePiece> field_214886_e;
      private final Random field_214887_f;
      private final Deque<JigsawManager.Entry> field_214888_g = Queues.newArrayDeque();

      public Assembler(ResourceLocation p_i50691_1_, int p_i50691_2_, JigsawManager.IPieceFactory p_i50691_3_, ChunkGenerator<?> p_i50691_4_, TemplateManager p_i50691_5_, BlockPos p_i50691_6_, List<StructurePiece> p_i50691_7_, Random p_i50691_8_) {
         this.field_214882_a = p_i50691_2_;
         this.field_214883_b = p_i50691_3_;
         this.field_214884_c = p_i50691_4_;
         this.field_214885_d = p_i50691_5_;
         this.field_214886_e = p_i50691_7_;
         this.field_214887_f = p_i50691_8_;
         Rotation lvt_9_1_ = Rotation.func_222466_a(p_i50691_8_);
         JigsawPattern lvt_10_1_ = JigsawManager.field_214891_a.get(p_i50691_1_);
         JigsawPiece lvt_11_1_ = lvt_10_1_.func_214944_a(p_i50691_8_);
         AbstractVillagePiece lvt_12_1_ = p_i50691_3_.create(p_i50691_5_, lvt_11_1_, p_i50691_6_, lvt_11_1_.func_214850_d(), lvt_9_1_, lvt_11_1_.func_214852_a(p_i50691_5_, p_i50691_6_, lvt_9_1_));
         MutableBoundingBox lvt_13_1_ = lvt_12_1_.getBoundingBox();
         int lvt_14_1_ = (lvt_13_1_.maxX + lvt_13_1_.minX) / 2;
         int lvt_15_1_ = (lvt_13_1_.maxZ + lvt_13_1_.minZ) / 2;
         int lvt_16_1_ = p_i50691_4_.func_222532_b(lvt_14_1_, lvt_15_1_, Heightmap.Type.WORLD_SURFACE_WG);
         lvt_12_1_.offset(0, lvt_16_1_ - (lvt_13_1_.minY + lvt_12_1_.getGroundLevelDelta()), 0);
         p_i50691_7_.add(lvt_12_1_);
         if (p_i50691_2_ > 0) {
            int lvt_17_1_ = true;
            AxisAlignedBB lvt_18_1_ = new AxisAlignedBB((double)(lvt_14_1_ - 80), (double)(lvt_16_1_ - 80), (double)(lvt_15_1_ - 80), (double)(lvt_14_1_ + 80 + 1), (double)(lvt_16_1_ + 80 + 1), (double)(lvt_15_1_ + 80 + 1));
            this.field_214888_g.addLast(new JigsawManager.Entry(lvt_12_1_, new AtomicReference(VoxelShapes.combineAndSimplify(VoxelShapes.create(lvt_18_1_), VoxelShapes.create(AxisAlignedBB.func_216363_a(lvt_13_1_)), IBooleanFunction.ONLY_FIRST)), lvt_16_1_ + 80, 0));

            while(!this.field_214888_g.isEmpty()) {
               JigsawManager.Entry lvt_19_1_ = (JigsawManager.Entry)this.field_214888_g.removeFirst();
               this.func_214881_a(lvt_19_1_.field_214876_a, lvt_19_1_.field_214877_b, lvt_19_1_.field_214878_c, lvt_19_1_.field_214879_d);
            }

         }
      }

      private void func_214881_a(AbstractVillagePiece p_214881_1_, AtomicReference<VoxelShape> p_214881_2_, int p_214881_3_, int p_214881_4_) {
         JigsawPiece lvt_5_1_ = p_214881_1_.func_214826_b();
         BlockPos lvt_6_1_ = p_214881_1_.func_214828_c();
         Rotation lvt_7_1_ = p_214881_1_.getRotation();
         JigsawPattern.PlacementBehaviour lvt_8_1_ = lvt_5_1_.getPlacementBehaviour();
         boolean lvt_9_1_ = lvt_8_1_ == JigsawPattern.PlacementBehaviour.RIGID;
         AtomicReference<VoxelShape> lvt_10_1_ = new AtomicReference();
         MutableBoundingBox lvt_11_1_ = p_214881_1_.getBoundingBox();
         int lvt_12_1_ = lvt_11_1_.minY;
         Iterator var13 = lvt_5_1_.func_214849_a(this.field_214885_d, lvt_6_1_, lvt_7_1_, this.field_214887_f).iterator();

         while(true) {
            while(true) {
               label90:
               while(var13.hasNext()) {
                  Template.BlockInfo lvt_14_1_ = (Template.BlockInfo)var13.next();
                  Direction lvt_15_1_ = (Direction)lvt_14_1_.state.get(JigsawBlock.FACING);
                  BlockPos lvt_16_1_ = lvt_14_1_.pos;
                  BlockPos lvt_17_1_ = lvt_16_1_.offset(lvt_15_1_);
                  int lvt_18_1_ = lvt_16_1_.getY() - lvt_12_1_;
                  int lvt_19_1_ = -1;
                  JigsawPattern lvt_20_1_ = JigsawManager.field_214891_a.get(new ResourceLocation(lvt_14_1_.nbt.getString("target_pool")));
                  JigsawPattern lvt_21_1_ = JigsawManager.field_214891_a.get(lvt_20_1_.func_214948_a());
                  if (lvt_20_1_ != JigsawPattern.INVALID && (lvt_20_1_.func_214946_c() != 0 || lvt_20_1_ == JigsawPattern.EMPTY)) {
                     boolean lvt_24_1_ = lvt_11_1_.isVecInside(lvt_17_1_);
                     AtomicReference lvt_22_2_;
                     int lvt_23_2_;
                     if (lvt_24_1_) {
                        lvt_22_2_ = lvt_10_1_;
                        lvt_23_2_ = lvt_12_1_;
                        if (lvt_10_1_.get() == null) {
                           lvt_10_1_.set(VoxelShapes.create(AxisAlignedBB.func_216363_a(lvt_11_1_)));
                        }
                     } else {
                        lvt_22_2_ = p_214881_2_;
                        lvt_23_2_ = p_214881_3_;
                     }

                     List<JigsawPiece> lvt_25_1_ = Lists.newArrayList();
                     if (p_214881_4_ != this.field_214882_a) {
                        lvt_25_1_.addAll(lvt_20_1_.func_214943_b(this.field_214887_f));
                     }

                     lvt_25_1_.addAll(lvt_21_1_.func_214943_b(this.field_214887_f));
                     Iterator var26 = lvt_25_1_.iterator();

                     while(var26.hasNext()) {
                        JigsawPiece lvt_27_1_ = (JigsawPiece)var26.next();
                        if (lvt_27_1_ == EmptyJigsawPiece.INSTANCE) {
                           break;
                        }

                        Iterator var28 = Rotation.func_222467_b(this.field_214887_f).iterator();

                        label117:
                        while(var28.hasNext()) {
                           Rotation lvt_29_1_ = (Rotation)var28.next();
                           List<Template.BlockInfo> lvt_30_1_ = lvt_27_1_.func_214849_a(this.field_214885_d, BlockPos.ZERO, lvt_29_1_, this.field_214887_f);
                           MutableBoundingBox lvt_31_1_ = lvt_27_1_.func_214852_a(this.field_214885_d, BlockPos.ZERO, lvt_29_1_);
                           int lvt_32_2_;
                           if (lvt_31_1_.getYSize() > 16) {
                              lvt_32_2_ = 0;
                           } else {
                              lvt_32_2_ = lvt_30_1_.stream().mapToInt((p_214880_2_) -> {
                                 if (!lvt_31_1_.isVecInside(p_214880_2_.pos.offset((Direction)p_214880_2_.state.get(JigsawBlock.FACING)))) {
                                    return 0;
                                 } else {
                                    ResourceLocation lvt_3_1_ = new ResourceLocation(p_214880_2_.nbt.getString("target_pool"));
                                    JigsawPattern lvt_4_1_ = JigsawManager.field_214891_a.get(lvt_3_1_);
                                    JigsawPattern lvt_5_1_ = JigsawManager.field_214891_a.get(lvt_4_1_.func_214948_a());
                                    return Math.max(lvt_4_1_.func_214945_a(this.field_214885_d), lvt_5_1_.func_214945_a(this.field_214885_d));
                                 }
                              }).max().orElse(0);
                           }

                           Iterator var33 = lvt_30_1_.iterator();

                           JigsawPattern.PlacementBehaviour lvt_39_1_;
                           boolean lvt_40_1_;
                           int lvt_41_1_;
                           int lvt_42_1_;
                           int lvt_43_2_;
                           MutableBoundingBox lvt_45_1_;
                           BlockPos lvt_46_1_;
                           int lvt_47_2_;
                           do {
                              Template.BlockInfo lvt_34_1_;
                              do {
                                 if (!var33.hasNext()) {
                                    continue label117;
                                 }

                                 lvt_34_1_ = (Template.BlockInfo)var33.next();
                              } while(!JigsawBlock.func_220171_a(lvt_14_1_, lvt_34_1_));

                              BlockPos lvt_35_1_ = lvt_34_1_.pos;
                              BlockPos lvt_36_1_ = new BlockPos(lvt_17_1_.getX() - lvt_35_1_.getX(), lvt_17_1_.getY() - lvt_35_1_.getY(), lvt_17_1_.getZ() - lvt_35_1_.getZ());
                              MutableBoundingBox lvt_37_1_ = lvt_27_1_.func_214852_a(this.field_214885_d, lvt_36_1_, lvt_29_1_);
                              int lvt_38_1_ = lvt_37_1_.minY;
                              lvt_39_1_ = lvt_27_1_.getPlacementBehaviour();
                              lvt_40_1_ = lvt_39_1_ == JigsawPattern.PlacementBehaviour.RIGID;
                              lvt_41_1_ = lvt_35_1_.getY();
                              lvt_42_1_ = lvt_18_1_ - lvt_41_1_ + ((Direction)lvt_14_1_.state.get(JigsawBlock.FACING)).getYOffset();
                              if (lvt_9_1_ && lvt_40_1_) {
                                 lvt_43_2_ = lvt_12_1_ + lvt_42_1_;
                              } else {
                                 if (lvt_19_1_ == -1) {
                                    lvt_19_1_ = this.field_214884_c.func_222532_b(lvt_16_1_.getX(), lvt_16_1_.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                 }

                                 lvt_43_2_ = lvt_19_1_ - lvt_41_1_;
                              }

                              int lvt_44_1_ = lvt_43_2_ - lvt_38_1_;
                              lvt_45_1_ = lvt_37_1_.func_215127_b(0, lvt_44_1_, 0);
                              lvt_46_1_ = lvt_36_1_.add(0, lvt_44_1_, 0);
                              if (lvt_32_2_ > 0) {
                                 lvt_47_2_ = Math.max(lvt_32_2_ + 1, lvt_45_1_.maxY - lvt_45_1_.minY);
                                 lvt_45_1_.maxY = lvt_45_1_.minY + lvt_47_2_;
                              }
                           } while(VoxelShapes.compare((VoxelShape)lvt_22_2_.get(), VoxelShapes.create(AxisAlignedBB.func_216363_a(lvt_45_1_).shrink(0.25D)), IBooleanFunction.ONLY_SECOND));

                           lvt_22_2_.set(VoxelShapes.combine((VoxelShape)lvt_22_2_.get(), VoxelShapes.create(AxisAlignedBB.func_216363_a(lvt_45_1_)), IBooleanFunction.ONLY_FIRST));
                           lvt_47_2_ = p_214881_1_.getGroundLevelDelta();
                           int lvt_48_2_;
                           if (lvt_40_1_) {
                              lvt_48_2_ = lvt_47_2_ - lvt_42_1_;
                           } else {
                              lvt_48_2_ = lvt_27_1_.func_214850_d();
                           }

                           AbstractVillagePiece lvt_49_1_ = this.field_214883_b.create(this.field_214885_d, lvt_27_1_, lvt_46_1_, lvt_48_2_, lvt_29_1_, lvt_45_1_);
                           int lvt_50_3_;
                           if (lvt_9_1_) {
                              lvt_50_3_ = lvt_12_1_ + lvt_18_1_;
                           } else if (lvt_40_1_) {
                              lvt_50_3_ = lvt_43_2_ + lvt_41_1_;
                           } else {
                              if (lvt_19_1_ == -1) {
                                 lvt_19_1_ = this.field_214884_c.func_222532_b(lvt_16_1_.getX(), lvt_16_1_.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                              }

                              lvt_50_3_ = lvt_19_1_ + lvt_42_1_ / 2;
                           }

                           p_214881_1_.addJunction(new JigsawJunction(lvt_17_1_.getX(), lvt_50_3_ - lvt_18_1_ + lvt_47_2_, lvt_17_1_.getZ(), lvt_42_1_, lvt_39_1_));
                           lvt_49_1_.addJunction(new JigsawJunction(lvt_16_1_.getX(), lvt_50_3_ - lvt_41_1_ + lvt_48_2_, lvt_16_1_.getZ(), -lvt_42_1_, lvt_8_1_));
                           this.field_214886_e.add(lvt_49_1_);
                           if (p_214881_4_ + 1 <= this.field_214882_a) {
                              this.field_214888_g.addLast(new JigsawManager.Entry(lvt_49_1_, lvt_22_2_, lvt_23_2_, p_214881_4_ + 1));
                           }
                           continue label90;
                        }
                     }
                  } else {
                     JigsawManager.LOGGER.warn("Empty or none existent pool: {}", lvt_14_1_.nbt.getString("target_pool"));
                  }
               }

               return;
            }
         }
      }
   }

   static final class Entry {
      private final AbstractVillagePiece field_214876_a;
      private final AtomicReference<VoxelShape> field_214877_b;
      private final int field_214878_c;
      private final int field_214879_d;

      private Entry(AbstractVillagePiece p_i50692_1_, AtomicReference<VoxelShape> p_i50692_2_, int p_i50692_3_, int p_i50692_4_) {
         this.field_214876_a = p_i50692_1_;
         this.field_214877_b = p_i50692_2_;
         this.field_214878_c = p_i50692_3_;
         this.field_214879_d = p_i50692_4_;
      }

      // $FF: synthetic method
      Entry(AbstractVillagePiece p_i50693_1_, AtomicReference p_i50693_2_, int p_i50693_3_, int p_i50693_4_, Object p_i50693_5_) {
         this(p_i50693_1_, p_i50693_2_, p_i50693_3_, p_i50693_4_);
      }
   }
}
