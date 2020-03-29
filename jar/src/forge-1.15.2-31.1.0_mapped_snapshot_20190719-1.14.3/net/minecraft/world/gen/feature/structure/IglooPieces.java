package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class IglooPieces {
   private static final ResourceLocation field_202592_e = new ResourceLocation("igloo/top");
   private static final ResourceLocation field_202593_f = new ResourceLocation("igloo/middle");
   private static final ResourceLocation field_202594_g = new ResourceLocation("igloo/bottom");
   private static final Map<ResourceLocation, BlockPos> field_207621_d;
   private static final Map<ResourceLocation, BlockPos> field_207622_e;

   public static void func_207617_a(TemplateManager p_207617_0_, BlockPos p_207617_1_, Rotation p_207617_2_, List<StructurePiece> p_207617_3_, Random p_207617_4_, NoFeatureConfig p_207617_5_) {
      if (p_207617_4_.nextDouble() < 0.5D) {
         int lvt_6_1_ = p_207617_4_.nextInt(8) + 4;
         p_207617_3_.add(new IglooPieces.Piece(p_207617_0_, field_202594_g, p_207617_1_, p_207617_2_, lvt_6_1_ * 3));

         for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_ - 1; ++lvt_7_1_) {
            p_207617_3_.add(new IglooPieces.Piece(p_207617_0_, field_202593_f, p_207617_1_, p_207617_2_, lvt_7_1_ * 3));
         }
      }

      p_207617_3_.add(new IglooPieces.Piece(p_207617_0_, field_202592_e, p_207617_1_, p_207617_2_, 0));
   }

   static {
      field_207621_d = ImmutableMap.of(field_202592_e, new BlockPos(3, 5, 5), field_202593_f, new BlockPos(1, 3, 1), field_202594_g, new BlockPos(3, 6, 7));
      field_207622_e = ImmutableMap.of(field_202592_e, BlockPos.ZERO, field_202593_f, new BlockPos(2, -3, 4), field_202594_g, new BlockPos(0, -3, -2));
   }

   public static class Piece extends TemplateStructurePiece {
      private final ResourceLocation field_207615_d;
      private final Rotation field_207616_e;

      public Piece(TemplateManager p_i49313_1_, ResourceLocation p_i49313_2_, BlockPos p_i49313_3_, Rotation p_i49313_4_, int p_i49313_5_) {
         super(IStructurePieceType.IGLU, 0);
         this.field_207615_d = p_i49313_2_;
         BlockPos lvt_6_1_ = (BlockPos)IglooPieces.field_207622_e.get(p_i49313_2_);
         this.templatePosition = p_i49313_3_.add(lvt_6_1_.getX(), lvt_6_1_.getY() - p_i49313_5_, lvt_6_1_.getZ());
         this.field_207616_e = p_i49313_4_;
         this.func_207614_a(p_i49313_1_);
      }

      public Piece(TemplateManager p_i50566_1_, CompoundNBT p_i50566_2_) {
         super(IStructurePieceType.IGLU, p_i50566_2_);
         this.field_207615_d = new ResourceLocation(p_i50566_2_.getString("Template"));
         this.field_207616_e = Rotation.valueOf(p_i50566_2_.getString("Rot"));
         this.func_207614_a(p_i50566_1_);
      }

      private void func_207614_a(TemplateManager p_207614_1_) {
         Template lvt_2_1_ = p_207614_1_.getTemplateDefaulted(this.field_207615_d);
         PlacementSettings lvt_3_1_ = (new PlacementSettings()).setRotation(this.field_207616_e).setMirror(Mirror.NONE).setCenterOffset((BlockPos)IglooPieces.field_207621_d.get(this.field_207615_d)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
         this.setup(lvt_2_1_, this.templatePosition, lvt_3_1_);
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putString("Template", this.field_207615_d.toString());
         p_143011_1_.putString("Rot", this.field_207616_e.name());
      }

      protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
         if ("chest".equals(p_186175_1_)) {
            p_186175_3_.setBlockState(p_186175_2_, Blocks.AIR.getDefaultState(), 3);
            TileEntity lvt_6_1_ = p_186175_3_.getTileEntity(p_186175_2_.down());
            if (lvt_6_1_ instanceof ChestTileEntity) {
               ((ChestTileEntity)lvt_6_1_).setLootTable(LootTables.CHESTS_IGLOO_CHEST, p_186175_4_.nextLong());
            }

         }
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         PlacementSettings lvt_6_1_ = (new PlacementSettings()).setRotation(this.field_207616_e).setMirror(Mirror.NONE).setCenterOffset((BlockPos)IglooPieces.field_207621_d.get(this.field_207615_d)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
         BlockPos lvt_7_1_ = (BlockPos)IglooPieces.field_207622_e.get(this.field_207615_d);
         BlockPos lvt_8_1_ = this.templatePosition.add(Template.transformedBlockPos(lvt_6_1_, new BlockPos(3 - lvt_7_1_.getX(), 0, 0 - lvt_7_1_.getZ())));
         int lvt_9_1_ = p_225577_1_.getHeight(Heightmap.Type.WORLD_SURFACE_WG, lvt_8_1_.getX(), lvt_8_1_.getZ());
         BlockPos lvt_10_1_ = this.templatePosition;
         this.templatePosition = this.templatePosition.add(0, lvt_9_1_ - 90 - 1, 0);
         boolean lvt_11_1_ = super.func_225577_a_(p_225577_1_, p_225577_2_, p_225577_3_, p_225577_4_, p_225577_5_);
         if (this.field_207615_d.equals(IglooPieces.field_202592_e)) {
            BlockPos lvt_12_1_ = this.templatePosition.add(Template.transformedBlockPos(lvt_6_1_, new BlockPos(3, 0, 5)));
            BlockState lvt_13_1_ = p_225577_1_.getBlockState(lvt_12_1_.down());
            if (!lvt_13_1_.isAir() && lvt_13_1_.getBlock() != Blocks.LADDER) {
               p_225577_1_.setBlockState(lvt_12_1_, Blocks.SNOW_BLOCK.getDefaultState(), 3);
            }
         }

         this.templatePosition = lvt_10_1_;
         return lvt_11_1_;
      }
   }
}
