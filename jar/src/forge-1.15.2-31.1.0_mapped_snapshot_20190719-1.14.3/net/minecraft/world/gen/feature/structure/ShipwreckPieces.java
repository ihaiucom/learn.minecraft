package net.minecraft.world.gen.feature.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class ShipwreckPieces {
   private static final BlockPos STRUCTURE_OFFSET = new BlockPos(4, 0, 15);
   private static final ResourceLocation[] field_204761_a = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};
   private static final ResourceLocation[] field_204762_b = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/upsidedown_full"), new ResourceLocation("shipwreck/upsidedown_fronthalf"), new ResourceLocation("shipwreck/upsidedown_backhalf"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/upsidedown_full_degraded"), new ResourceLocation("shipwreck/upsidedown_fronthalf_degraded"), new ResourceLocation("shipwreck/upsidedown_backhalf_degraded"), new ResourceLocation("shipwreck/sideways_full_degraded"), new ResourceLocation("shipwreck/sideways_fronthalf_degraded"), new ResourceLocation("shipwreck/sideways_backhalf_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};

   public static void func_204760_a(TemplateManager p_204760_0_, BlockPos p_204760_1_, Rotation p_204760_2_, List<StructurePiece> p_204760_3_, Random p_204760_4_, ShipwreckConfig p_204760_5_) {
      ResourceLocation lvt_6_1_ = p_204760_5_.isBeached ? field_204761_a[p_204760_4_.nextInt(field_204761_a.length)] : field_204762_b[p_204760_4_.nextInt(field_204762_b.length)];
      p_204760_3_.add(new ShipwreckPieces.Piece(p_204760_0_, lvt_6_1_, p_204760_1_, p_204760_2_, p_204760_5_.isBeached));
   }

   public static class Piece extends TemplateStructurePiece {
      private final Rotation rotation;
      private final ResourceLocation field_204756_e;
      private final boolean isBeached;

      public Piece(TemplateManager p_i48904_1_, ResourceLocation p_i48904_2_, BlockPos p_i48904_3_, Rotation p_i48904_4_, boolean p_i48904_5_) {
         super(IStructurePieceType.SHIPWRECK, 0);
         this.templatePosition = p_i48904_3_;
         this.rotation = p_i48904_4_;
         this.field_204756_e = p_i48904_2_;
         this.isBeached = p_i48904_5_;
         this.func_204754_a(p_i48904_1_);
      }

      public Piece(TemplateManager p_i50445_1_, CompoundNBT p_i50445_2_) {
         super(IStructurePieceType.SHIPWRECK, p_i50445_2_);
         this.field_204756_e = new ResourceLocation(p_i50445_2_.getString("Template"));
         this.isBeached = p_i50445_2_.getBoolean("isBeached");
         this.rotation = Rotation.valueOf(p_i50445_2_.getString("Rot"));
         this.func_204754_a(p_i50445_1_);
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putString("Template", this.field_204756_e.toString());
         p_143011_1_.putBoolean("isBeached", this.isBeached);
         p_143011_1_.putString("Rot", this.rotation.name());
      }

      private void func_204754_a(TemplateManager p_204754_1_) {
         Template lvt_2_1_ = p_204754_1_.getTemplateDefaulted(this.field_204756_e);
         PlacementSettings lvt_3_1_ = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setCenterOffset(ShipwreckPieces.STRUCTURE_OFFSET).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
         this.setup(lvt_2_1_, this.templatePosition, lvt_3_1_);
      }

      protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
         if ("map_chest".equals(p_186175_1_)) {
            LockableLootTileEntity.setLootTable(p_186175_3_, p_186175_4_, p_186175_2_.down(), LootTables.CHESTS_SHIPWRECK_MAP);
         } else if ("treasure_chest".equals(p_186175_1_)) {
            LockableLootTileEntity.setLootTable(p_186175_3_, p_186175_4_, p_186175_2_.down(), LootTables.CHESTS_SHIPWRECK_TREASURE);
         } else if ("supply_chest".equals(p_186175_1_)) {
            LockableLootTileEntity.setLootTable(p_186175_3_, p_186175_4_, p_186175_2_.down(), LootTables.CHESTS_SHIPWRECK_SUPPLY);
         }

      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         int lvt_6_1_ = 256;
         int lvt_7_1_ = 0;
         BlockPos lvt_8_1_ = this.template.getSize();
         Heightmap.Type lvt_9_1_ = this.isBeached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
         int lvt_10_1_ = lvt_8_1_.getX() * lvt_8_1_.getZ();
         if (lvt_10_1_ == 0) {
            lvt_7_1_ = p_225577_1_.getHeight(lvt_9_1_, this.templatePosition.getX(), this.templatePosition.getZ());
         } else {
            BlockPos lvt_11_1_ = this.templatePosition.add(lvt_8_1_.getX() - 1, 0, lvt_8_1_.getZ() - 1);

            int lvt_14_1_;
            for(Iterator var12 = BlockPos.getAllInBoxMutable(this.templatePosition, lvt_11_1_).iterator(); var12.hasNext(); lvt_6_1_ = Math.min(lvt_6_1_, lvt_14_1_)) {
               BlockPos lvt_13_1_ = (BlockPos)var12.next();
               lvt_14_1_ = p_225577_1_.getHeight(lvt_9_1_, lvt_13_1_.getX(), lvt_13_1_.getZ());
               lvt_7_1_ += lvt_14_1_;
            }

            lvt_7_1_ /= lvt_10_1_;
         }

         int lvt_11_2_ = this.isBeached ? lvt_6_1_ - lvt_8_1_.getY() / 2 - p_225577_3_.nextInt(3) : lvt_7_1_;
         this.templatePosition = new BlockPos(this.templatePosition.getX(), lvt_11_2_, this.templatePosition.getZ());
         return super.func_225577_a_(p_225577_1_, p_225577_2_, p_225577_3_, p_225577_4_, p_225577_5_);
      }
   }
}
