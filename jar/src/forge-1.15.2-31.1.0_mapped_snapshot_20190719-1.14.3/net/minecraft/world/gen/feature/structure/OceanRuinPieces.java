package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class OceanRuinPieces {
   private static final ResourceLocation[] field_204058_G = new ResourceLocation[]{new ResourceLocation("underwater_ruin/warm_1"), new ResourceLocation("underwater_ruin/warm_2"), new ResourceLocation("underwater_ruin/warm_3"), new ResourceLocation("underwater_ruin/warm_4"), new ResourceLocation("underwater_ruin/warm_5"), new ResourceLocation("underwater_ruin/warm_6"), new ResourceLocation("underwater_ruin/warm_7"), new ResourceLocation("underwater_ruin/warm_8")};
   private static final ResourceLocation[] field_204059_H = new ResourceLocation[]{new ResourceLocation("underwater_ruin/brick_1"), new ResourceLocation("underwater_ruin/brick_2"), new ResourceLocation("underwater_ruin/brick_3"), new ResourceLocation("underwater_ruin/brick_4"), new ResourceLocation("underwater_ruin/brick_5"), new ResourceLocation("underwater_ruin/brick_6"), new ResourceLocation("underwater_ruin/brick_7"), new ResourceLocation("underwater_ruin/brick_8")};
   private static final ResourceLocation[] field_204053_B = new ResourceLocation[]{new ResourceLocation("underwater_ruin/cracked_1"), new ResourceLocation("underwater_ruin/cracked_2"), new ResourceLocation("underwater_ruin/cracked_3"), new ResourceLocation("underwater_ruin/cracked_4"), new ResourceLocation("underwater_ruin/cracked_5"), new ResourceLocation("underwater_ruin/cracked_6"), new ResourceLocation("underwater_ruin/cracked_7"), new ResourceLocation("underwater_ruin/cracked_8")};
   private static final ResourceLocation[] field_204061_J = new ResourceLocation[]{new ResourceLocation("underwater_ruin/mossy_1"), new ResourceLocation("underwater_ruin/mossy_2"), new ResourceLocation("underwater_ruin/mossy_3"), new ResourceLocation("underwater_ruin/mossy_4"), new ResourceLocation("underwater_ruin/mossy_5"), new ResourceLocation("underwater_ruin/mossy_6"), new ResourceLocation("underwater_ruin/mossy_7"), new ResourceLocation("underwater_ruin/mossy_8")};
   private static final ResourceLocation[] field_204062_K = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_brick_1"), new ResourceLocation("underwater_ruin/big_brick_2"), new ResourceLocation("underwater_ruin/big_brick_3"), new ResourceLocation("underwater_ruin/big_brick_8")};
   private static final ResourceLocation[] field_204066_O = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_mossy_1"), new ResourceLocation("underwater_ruin/big_mossy_2"), new ResourceLocation("underwater_ruin/big_mossy_3"), new ResourceLocation("underwater_ruin/big_mossy_8")};
   private static final ResourceLocation[] field_204070_S = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_cracked_1"), new ResourceLocation("underwater_ruin/big_cracked_2"), new ResourceLocation("underwater_ruin/big_cracked_3"), new ResourceLocation("underwater_ruin/big_cracked_8")};
   private static final ResourceLocation[] field_204049_ab = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_warm_4"), new ResourceLocation("underwater_ruin/big_warm_5"), new ResourceLocation("underwater_ruin/big_warm_6"), new ResourceLocation("underwater_ruin/big_warm_7")};

   private static ResourceLocation func_204042_a(Random p_204042_0_) {
      return field_204058_G[p_204042_0_.nextInt(field_204058_G.length)];
   }

   private static ResourceLocation func_204043_b(Random p_204043_0_) {
      return field_204049_ab[p_204043_0_.nextInt(field_204049_ab.length)];
   }

   public static void func_204041_a(TemplateManager p_204041_0_, BlockPos p_204041_1_, Rotation p_204041_2_, List<StructurePiece> p_204041_3_, Random p_204041_4_, OceanRuinConfig p_204041_5_) {
      boolean lvt_6_1_ = p_204041_4_.nextFloat() <= p_204041_5_.largeProbability;
      float lvt_7_1_ = lvt_6_1_ ? 0.9F : 0.8F;
      func_204045_a(p_204041_0_, p_204041_1_, p_204041_2_, p_204041_3_, p_204041_4_, p_204041_5_, lvt_6_1_, lvt_7_1_);
      if (lvt_6_1_ && p_204041_4_.nextFloat() <= p_204041_5_.clusterProbability) {
         func_204047_a(p_204041_0_, p_204041_4_, p_204041_2_, p_204041_1_, p_204041_5_, p_204041_3_);
      }

   }

   private static void func_204047_a(TemplateManager p_204047_0_, Random p_204047_1_, Rotation p_204047_2_, BlockPos p_204047_3_, OceanRuinConfig p_204047_4_, List<StructurePiece> p_204047_5_) {
      int lvt_6_1_ = p_204047_3_.getX();
      int lvt_7_1_ = p_204047_3_.getZ();
      BlockPos lvt_8_1_ = Template.getTransformedPos(new BlockPos(15, 0, 15), Mirror.NONE, p_204047_2_, BlockPos.ZERO).add(lvt_6_1_, 0, lvt_7_1_);
      MutableBoundingBox lvt_9_1_ = MutableBoundingBox.createProper(lvt_6_1_, 0, lvt_7_1_, lvt_8_1_.getX(), 0, lvt_8_1_.getZ());
      BlockPos lvt_10_1_ = new BlockPos(Math.min(lvt_6_1_, lvt_8_1_.getX()), 0, Math.min(lvt_7_1_, lvt_8_1_.getZ()));
      List<BlockPos> lvt_11_1_ = func_204044_a(p_204047_1_, lvt_10_1_.getX(), lvt_10_1_.getZ());
      int lvt_12_1_ = MathHelper.nextInt(p_204047_1_, 4, 8);

      for(int lvt_13_1_ = 0; lvt_13_1_ < lvt_12_1_; ++lvt_13_1_) {
         if (!lvt_11_1_.isEmpty()) {
            int lvt_14_1_ = p_204047_1_.nextInt(lvt_11_1_.size());
            BlockPos lvt_15_1_ = (BlockPos)lvt_11_1_.remove(lvt_14_1_);
            int lvt_16_1_ = lvt_15_1_.getX();
            int lvt_17_1_ = lvt_15_1_.getZ();
            Rotation lvt_18_1_ = Rotation.values()[p_204047_1_.nextInt(Rotation.values().length)];
            BlockPos lvt_19_1_ = Template.getTransformedPos(new BlockPos(5, 0, 6), Mirror.NONE, lvt_18_1_, BlockPos.ZERO).add(lvt_16_1_, 0, lvt_17_1_);
            MutableBoundingBox lvt_20_1_ = MutableBoundingBox.createProper(lvt_16_1_, 0, lvt_17_1_, lvt_19_1_.getX(), 0, lvt_19_1_.getZ());
            if (!lvt_20_1_.intersectsWith(lvt_9_1_)) {
               func_204045_a(p_204047_0_, lvt_15_1_, lvt_18_1_, p_204047_5_, p_204047_1_, p_204047_4_, false, 0.8F);
            }
         }
      }

   }

   private static List<BlockPos> func_204044_a(Random p_204044_0_, int p_204044_1_, int p_204044_2_) {
      List<BlockPos> lvt_3_1_ = Lists.newArrayList();
      lvt_3_1_.add(new BlockPos(p_204044_1_ - 16 + MathHelper.nextInt(p_204044_0_, 1, 8), 90, p_204044_2_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7)));
      lvt_3_1_.add(new BlockPos(p_204044_1_ - 16 + MathHelper.nextInt(p_204044_0_, 1, 8), 90, p_204044_2_ + MathHelper.nextInt(p_204044_0_, 1, 7)));
      lvt_3_1_.add(new BlockPos(p_204044_1_ - 16 + MathHelper.nextInt(p_204044_0_, 1, 8), 90, p_204044_2_ - 16 + MathHelper.nextInt(p_204044_0_, 4, 8)));
      lvt_3_1_.add(new BlockPos(p_204044_1_ + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7)));
      lvt_3_1_.add(new BlockPos(p_204044_1_ + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ - 16 + MathHelper.nextInt(p_204044_0_, 4, 6)));
      lvt_3_1_.add(new BlockPos(p_204044_1_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ + 16 + MathHelper.nextInt(p_204044_0_, 3, 8)));
      lvt_3_1_.add(new BlockPos(p_204044_1_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ + MathHelper.nextInt(p_204044_0_, 1, 7)));
      lvt_3_1_.add(new BlockPos(p_204044_1_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ - 16 + MathHelper.nextInt(p_204044_0_, 4, 8)));
      return lvt_3_1_;
   }

   private static void func_204045_a(TemplateManager p_204045_0_, BlockPos p_204045_1_, Rotation p_204045_2_, List<StructurePiece> p_204045_3_, Random p_204045_4_, OceanRuinConfig p_204045_5_, boolean p_204045_6_, float p_204045_7_) {
      if (p_204045_5_.field_204031_a == OceanRuinStructure.Type.WARM) {
         ResourceLocation lvt_8_1_ = p_204045_6_ ? func_204043_b(p_204045_4_) : func_204042_a(p_204045_4_);
         p_204045_3_.add(new OceanRuinPieces.Piece(p_204045_0_, lvt_8_1_, p_204045_1_, p_204045_2_, p_204045_7_, p_204045_5_.field_204031_a, p_204045_6_));
      } else if (p_204045_5_.field_204031_a == OceanRuinStructure.Type.COLD) {
         ResourceLocation[] lvt_8_2_ = p_204045_6_ ? field_204062_K : field_204059_H;
         ResourceLocation[] lvt_9_1_ = p_204045_6_ ? field_204070_S : field_204053_B;
         ResourceLocation[] lvt_10_1_ = p_204045_6_ ? field_204066_O : field_204061_J;
         int lvt_11_1_ = p_204045_4_.nextInt(lvt_8_2_.length);
         p_204045_3_.add(new OceanRuinPieces.Piece(p_204045_0_, lvt_8_2_[lvt_11_1_], p_204045_1_, p_204045_2_, p_204045_7_, p_204045_5_.field_204031_a, p_204045_6_));
         p_204045_3_.add(new OceanRuinPieces.Piece(p_204045_0_, lvt_9_1_[lvt_11_1_], p_204045_1_, p_204045_2_, 0.7F, p_204045_5_.field_204031_a, p_204045_6_));
         p_204045_3_.add(new OceanRuinPieces.Piece(p_204045_0_, lvt_10_1_[lvt_11_1_], p_204045_1_, p_204045_2_, 0.5F, p_204045_5_.field_204031_a, p_204045_6_));
      }

   }

   public static class Piece extends TemplateStructurePiece {
      private final OceanRuinStructure.Type biomeType;
      private final float integrity;
      private final ResourceLocation field_204038_f;
      private final Rotation rotation;
      private final boolean isLarge;

      public Piece(TemplateManager p_i48868_1_, ResourceLocation p_i48868_2_, BlockPos p_i48868_3_, Rotation p_i48868_4_, float p_i48868_5_, OceanRuinStructure.Type p_i48868_6_, boolean p_i48868_7_) {
         super(IStructurePieceType.ORP, 0);
         this.field_204038_f = p_i48868_2_;
         this.templatePosition = p_i48868_3_;
         this.rotation = p_i48868_4_;
         this.integrity = p_i48868_5_;
         this.biomeType = p_i48868_6_;
         this.isLarge = p_i48868_7_;
         this.func_204034_a(p_i48868_1_);
      }

      public Piece(TemplateManager p_i50592_1_, CompoundNBT p_i50592_2_) {
         super(IStructurePieceType.ORP, p_i50592_2_);
         this.field_204038_f = new ResourceLocation(p_i50592_2_.getString("Template"));
         this.rotation = Rotation.valueOf(p_i50592_2_.getString("Rot"));
         this.integrity = p_i50592_2_.getFloat("Integrity");
         this.biomeType = OceanRuinStructure.Type.valueOf(p_i50592_2_.getString("BiomeType"));
         this.isLarge = p_i50592_2_.getBoolean("IsLarge");
         this.func_204034_a(p_i50592_1_);
      }

      private void func_204034_a(TemplateManager p_204034_1_) {
         Template lvt_2_1_ = p_204034_1_.getTemplateDefaulted(this.field_204038_f);
         PlacementSettings lvt_3_1_ = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
         this.setup(lvt_2_1_, this.templatePosition, lvt_3_1_);
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putString("Template", this.field_204038_f.toString());
         p_143011_1_.putString("Rot", this.rotation.name());
         p_143011_1_.putFloat("Integrity", this.integrity);
         p_143011_1_.putString("BiomeType", this.biomeType.toString());
         p_143011_1_.putBoolean("IsLarge", this.isLarge);
      }

      protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
         if ("chest".equals(p_186175_1_)) {
            p_186175_3_.setBlockState(p_186175_2_, (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, p_186175_3_.getFluidState(p_186175_2_).isTagged(FluidTags.WATER)), 2);
            TileEntity lvt_6_1_ = p_186175_3_.getTileEntity(p_186175_2_);
            if (lvt_6_1_ instanceof ChestTileEntity) {
               ((ChestTileEntity)lvt_6_1_).setLootTable(this.isLarge ? LootTables.CHESTS_UNDERWATER_RUIN_BIG : LootTables.CHESTS_UNDERWATER_RUIN_SMALL, p_186175_4_.nextLong());
            }
         } else if ("drowned".equals(p_186175_1_)) {
            DrownedEntity lvt_6_2_ = (DrownedEntity)EntityType.DROWNED.create(p_186175_3_.getWorld());
            lvt_6_2_.enablePersistence();
            lvt_6_2_.moveToBlockPosAndAngles(p_186175_2_, 0.0F, 0.0F);
            lvt_6_2_.onInitialSpawn(p_186175_3_, p_186175_3_.getDifficultyForLocation(p_186175_2_), SpawnReason.STRUCTURE, (ILivingEntityData)null, (CompoundNBT)null);
            p_186175_3_.addEntity(lvt_6_2_);
            if (p_186175_2_.getY() > p_186175_3_.getSeaLevel()) {
               p_186175_3_.setBlockState(p_186175_2_, Blocks.AIR.getDefaultState(), 2);
            } else {
               p_186175_3_.setBlockState(p_186175_2_, Blocks.WATER.getDefaultState(), 2);
            }
         }

      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.placeSettings.func_215219_b().addProcessor(new IntegrityProcessor(this.integrity)).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
         int lvt_6_1_ = p_225577_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
         this.templatePosition = new BlockPos(this.templatePosition.getX(), lvt_6_1_, this.templatePosition.getZ());
         BlockPos lvt_7_1_ = Template.getTransformedPos(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.rotation, BlockPos.ZERO).add(this.templatePosition);
         this.templatePosition = new BlockPos(this.templatePosition.getX(), this.func_204035_a(this.templatePosition, p_225577_1_, lvt_7_1_), this.templatePosition.getZ());
         return super.func_225577_a_(p_225577_1_, p_225577_2_, p_225577_3_, p_225577_4_, p_225577_5_);
      }

      private int func_204035_a(BlockPos p_204035_1_, IBlockReader p_204035_2_, BlockPos p_204035_3_) {
         int lvt_4_1_ = p_204035_1_.getY();
         int lvt_5_1_ = 512;
         int lvt_6_1_ = lvt_4_1_ - 1;
         int lvt_7_1_ = 0;
         Iterator var8 = BlockPos.getAllInBoxMutable(p_204035_1_, p_204035_3_).iterator();

         while(var8.hasNext()) {
            BlockPos lvt_9_1_ = (BlockPos)var8.next();
            int lvt_10_1_ = lvt_9_1_.getX();
            int lvt_11_1_ = lvt_9_1_.getZ();
            int lvt_12_1_ = p_204035_1_.getY() - 1;
            BlockPos.Mutable lvt_13_1_ = new BlockPos.Mutable(lvt_10_1_, lvt_12_1_, lvt_11_1_);
            BlockState lvt_14_1_ = p_204035_2_.getBlockState(lvt_13_1_);

            for(IFluidState lvt_15_1_ = p_204035_2_.getFluidState(lvt_13_1_); (lvt_14_1_.isAir() || lvt_15_1_.isTagged(FluidTags.WATER) || lvt_14_1_.getBlock().isIn(BlockTags.ICE)) && lvt_12_1_ > 1; lvt_15_1_ = p_204035_2_.getFluidState(lvt_13_1_)) {
               --lvt_12_1_;
               lvt_13_1_.setPos(lvt_10_1_, lvt_12_1_, lvt_11_1_);
               lvt_14_1_ = p_204035_2_.getBlockState(lvt_13_1_);
            }

            lvt_5_1_ = Math.min(lvt_5_1_, lvt_12_1_);
            if (lvt_12_1_ < lvt_6_1_ - 2) {
               ++lvt_7_1_;
            }
         }

         int lvt_8_1_ = Math.abs(p_204035_1_.getX() - p_204035_3_.getX());
         if (lvt_6_1_ - lvt_5_1_ > 2 && lvt_7_1_ > lvt_8_1_ - 2) {
            lvt_4_1_ = lvt_5_1_ + 1;
         }

         return lvt_4_1_;
      }
   }
}
