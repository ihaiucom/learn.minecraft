package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class EndCityPieces {
   private static final PlacementSettings OVERWRITE;
   private static final PlacementSettings INSERT;
   private static final EndCityPieces.IGenerator HOUSE_TOWER_GENERATOR;
   private static final List<Tuple<Rotation, BlockPos>> TOWER_BRIDGES;
   private static final EndCityPieces.IGenerator TOWER_GENERATOR;
   private static final EndCityPieces.IGenerator TOWER_BRIDGE_GENERATOR;
   private static final List<Tuple<Rotation, BlockPos>> FAT_TOWER_BRIDGES;
   private static final EndCityPieces.IGenerator FAT_TOWER_GENERATOR;

   private static EndCityPieces.CityTemplate addPiece(TemplateManager p_191090_0_, EndCityPieces.CityTemplate p_191090_1_, BlockPos p_191090_2_, String p_191090_3_, Rotation p_191090_4_, boolean p_191090_5_) {
      EndCityPieces.CityTemplate lvt_6_1_ = new EndCityPieces.CityTemplate(p_191090_0_, p_191090_3_, p_191090_1_.templatePosition, p_191090_4_, p_191090_5_);
      BlockPos lvt_7_1_ = p_191090_1_.template.calculateConnectedPos(p_191090_1_.placeSettings, p_191090_2_, lvt_6_1_.placeSettings, BlockPos.ZERO);
      lvt_6_1_.offset(lvt_7_1_.getX(), lvt_7_1_.getY(), lvt_7_1_.getZ());
      return lvt_6_1_;
   }

   public static void startHouseTower(TemplateManager p_191087_0_, BlockPos p_191087_1_, Rotation p_191087_2_, List<StructurePiece> p_191087_3_, Random p_191087_4_) {
      FAT_TOWER_GENERATOR.init();
      HOUSE_TOWER_GENERATOR.init();
      TOWER_BRIDGE_GENERATOR.init();
      TOWER_GENERATOR.init();
      EndCityPieces.CityTemplate lvt_5_1_ = addHelper(p_191087_3_, new EndCityPieces.CityTemplate(p_191087_0_, "base_floor", p_191087_1_, p_191087_2_, true));
      lvt_5_1_ = addHelper(p_191087_3_, addPiece(p_191087_0_, lvt_5_1_, new BlockPos(-1, 0, -1), "second_floor_1", p_191087_2_, false));
      lvt_5_1_ = addHelper(p_191087_3_, addPiece(p_191087_0_, lvt_5_1_, new BlockPos(-1, 4, -1), "third_floor_1", p_191087_2_, false));
      lvt_5_1_ = addHelper(p_191087_3_, addPiece(p_191087_0_, lvt_5_1_, new BlockPos(-1, 8, -1), "third_roof", p_191087_2_, true));
      recursiveChildren(p_191087_0_, TOWER_GENERATOR, 1, lvt_5_1_, (BlockPos)null, p_191087_3_, p_191087_4_);
   }

   private static EndCityPieces.CityTemplate addHelper(List<StructurePiece> p_189935_0_, EndCityPieces.CityTemplate p_189935_1_) {
      p_189935_0_.add(p_189935_1_);
      return p_189935_1_;
   }

   private static boolean recursiveChildren(TemplateManager p_191088_0_, EndCityPieces.IGenerator p_191088_1_, int p_191088_2_, EndCityPieces.CityTemplate p_191088_3_, BlockPos p_191088_4_, List<StructurePiece> p_191088_5_, Random p_191088_6_) {
      if (p_191088_2_ > 8) {
         return false;
      } else {
         List<StructurePiece> lvt_7_1_ = Lists.newArrayList();
         if (p_191088_1_.generate(p_191088_0_, p_191088_2_, p_191088_3_, p_191088_4_, lvt_7_1_, p_191088_6_)) {
            boolean lvt_8_1_ = false;
            int lvt_9_1_ = p_191088_6_.nextInt();
            Iterator var10 = lvt_7_1_.iterator();

            while(var10.hasNext()) {
               StructurePiece lvt_11_1_ = (StructurePiece)var10.next();
               lvt_11_1_.componentType = lvt_9_1_;
               StructurePiece lvt_12_1_ = StructurePiece.findIntersecting(p_191088_5_, lvt_11_1_.getBoundingBox());
               if (lvt_12_1_ != null && lvt_12_1_.componentType != p_191088_3_.componentType) {
                  lvt_8_1_ = true;
                  break;
               }
            }

            if (!lvt_8_1_) {
               p_191088_5_.addAll(lvt_7_1_);
               return true;
            }
         }

         return false;
      }
   }

   static {
      OVERWRITE = (new PlacementSettings()).setIgnoreEntities(true).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
      INSERT = (new PlacementSettings()).setIgnoreEntities(true).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
      HOUSE_TOWER_GENERATOR = new EndCityPieces.IGenerator() {
         public void init() {
         }

         public boolean generate(TemplateManager p_191086_1_, int p_191086_2_, EndCityPieces.CityTemplate p_191086_3_, BlockPos p_191086_4_, List<StructurePiece> p_191086_5_, Random p_191086_6_) {
            if (p_191086_2_ > 8) {
               return false;
            } else {
               Rotation lvt_7_1_ = p_191086_3_.placeSettings.getRotation();
               EndCityPieces.CityTemplate lvt_8_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, p_191086_3_, p_191086_4_, "base_floor", lvt_7_1_, true));
               int lvt_9_1_ = p_191086_6_.nextInt(3);
               if (lvt_9_1_ == 0) {
                  EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_8_1_, new BlockPos(-1, 4, -1), "base_roof", lvt_7_1_, true));
               } else if (lvt_9_1_ == 1) {
                  lvt_8_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_8_1_, new BlockPos(-1, 0, -1), "second_floor_2", lvt_7_1_, false));
                  lvt_8_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_8_1_, new BlockPos(-1, 8, -1), "second_roof", lvt_7_1_, false));
                  EndCityPieces.recursiveChildren(p_191086_1_, EndCityPieces.TOWER_GENERATOR, p_191086_2_ + 1, lvt_8_1_, (BlockPos)null, p_191086_5_, p_191086_6_);
               } else if (lvt_9_1_ == 2) {
                  lvt_8_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_8_1_, new BlockPos(-1, 0, -1), "second_floor_2", lvt_7_1_, false));
                  lvt_8_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_8_1_, new BlockPos(-1, 4, -1), "third_floor_2", lvt_7_1_, false));
                  lvt_8_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_8_1_, new BlockPos(-1, 8, -1), "third_roof", lvt_7_1_, true));
                  EndCityPieces.recursiveChildren(p_191086_1_, EndCityPieces.TOWER_GENERATOR, p_191086_2_ + 1, lvt_8_1_, (BlockPos)null, p_191086_5_, p_191086_6_);
               }

               return true;
            }
         }
      };
      TOWER_BRIDGES = Lists.newArrayList(new Tuple[]{new Tuple(Rotation.NONE, new BlockPos(1, -1, 0)), new Tuple(Rotation.CLOCKWISE_90, new BlockPos(6, -1, 1)), new Tuple(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)), new Tuple(Rotation.CLOCKWISE_180, new BlockPos(5, -1, 6))});
      TOWER_GENERATOR = new EndCityPieces.IGenerator() {
         public void init() {
         }

         public boolean generate(TemplateManager p_191086_1_, int p_191086_2_, EndCityPieces.CityTemplate p_191086_3_, BlockPos p_191086_4_, List<StructurePiece> p_191086_5_, Random p_191086_6_) {
            Rotation lvt_7_1_ = p_191086_3_.placeSettings.getRotation();
            EndCityPieces.CityTemplate lvt_8_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, p_191086_3_, new BlockPos(3 + p_191086_6_.nextInt(2), -3, 3 + p_191086_6_.nextInt(2)), "tower_base", lvt_7_1_, true));
            lvt_8_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_8_1_, new BlockPos(0, 7, 0), "tower_piece", lvt_7_1_, true));
            EndCityPieces.CityTemplate lvt_9_1_ = p_191086_6_.nextInt(3) == 0 ? lvt_8_1_ : null;
            int lvt_10_1_ = 1 + p_191086_6_.nextInt(3);

            for(int lvt_11_1_ = 0; lvt_11_1_ < lvt_10_1_; ++lvt_11_1_) {
               lvt_8_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_8_1_, new BlockPos(0, 4, 0), "tower_piece", lvt_7_1_, true));
               if (lvt_11_1_ < lvt_10_1_ - 1 && p_191086_6_.nextBoolean()) {
                  lvt_9_1_ = lvt_8_1_;
               }
            }

            if (lvt_9_1_ != null) {
               Iterator var14 = EndCityPieces.TOWER_BRIDGES.iterator();

               while(var14.hasNext()) {
                  Tuple<Rotation, BlockPos> lvt_12_1_ = (Tuple)var14.next();
                  if (p_191086_6_.nextBoolean()) {
                     EndCityPieces.CityTemplate lvt_13_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_9_1_, (BlockPos)lvt_12_1_.getB(), "bridge_end", lvt_7_1_.add((Rotation)lvt_12_1_.getA()), true));
                     EndCityPieces.recursiveChildren(p_191086_1_, EndCityPieces.TOWER_BRIDGE_GENERATOR, p_191086_2_ + 1, lvt_13_1_, (BlockPos)null, p_191086_5_, p_191086_6_);
                  }
               }

               EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_8_1_, new BlockPos(-1, 4, -1), "tower_top", lvt_7_1_, true));
            } else {
               if (p_191086_2_ != 7) {
                  return EndCityPieces.recursiveChildren(p_191086_1_, EndCityPieces.FAT_TOWER_GENERATOR, p_191086_2_ + 1, lvt_8_1_, (BlockPos)null, p_191086_5_, p_191086_6_);
               }

               EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_8_1_, new BlockPos(-1, 4, -1), "tower_top", lvt_7_1_, true));
            }

            return true;
         }
      };
      TOWER_BRIDGE_GENERATOR = new EndCityPieces.IGenerator() {
         public boolean shipCreated;

         public void init() {
            this.shipCreated = false;
         }

         public boolean generate(TemplateManager p_191086_1_, int p_191086_2_, EndCityPieces.CityTemplate p_191086_3_, BlockPos p_191086_4_, List<StructurePiece> p_191086_5_, Random p_191086_6_) {
            Rotation lvt_7_1_ = p_191086_3_.placeSettings.getRotation();
            int lvt_8_1_ = p_191086_6_.nextInt(4) + 1;
            EndCityPieces.CityTemplate lvt_9_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, p_191086_3_, new BlockPos(0, 0, -4), "bridge_piece", lvt_7_1_, true));
            lvt_9_1_.componentType = -1;
            int lvt_10_1_ = 0;

            for(int lvt_11_1_ = 0; lvt_11_1_ < lvt_8_1_; ++lvt_11_1_) {
               if (p_191086_6_.nextBoolean()) {
                  lvt_9_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_9_1_, new BlockPos(0, lvt_10_1_, -4), "bridge_piece", lvt_7_1_, true));
                  lvt_10_1_ = 0;
               } else {
                  if (p_191086_6_.nextBoolean()) {
                     lvt_9_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_9_1_, new BlockPos(0, lvt_10_1_, -4), "bridge_steep_stairs", lvt_7_1_, true));
                  } else {
                     lvt_9_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_9_1_, new BlockPos(0, lvt_10_1_, -8), "bridge_gentle_stairs", lvt_7_1_, true));
                  }

                  lvt_10_1_ = 4;
               }
            }

            if (!this.shipCreated && p_191086_6_.nextInt(10 - p_191086_2_) == 0) {
               EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_9_1_, new BlockPos(-8 + p_191086_6_.nextInt(8), lvt_10_1_, -70 + p_191086_6_.nextInt(10)), "ship", lvt_7_1_, true));
               this.shipCreated = true;
            } else if (!EndCityPieces.recursiveChildren(p_191086_1_, EndCityPieces.HOUSE_TOWER_GENERATOR, p_191086_2_ + 1, lvt_9_1_, new BlockPos(-3, lvt_10_1_ + 1, -11), p_191086_5_, p_191086_6_)) {
               return false;
            }

            lvt_9_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_9_1_, new BlockPos(4, lvt_10_1_, 0), "bridge_end", lvt_7_1_.add(Rotation.CLOCKWISE_180), true));
            lvt_9_1_.componentType = -1;
            return true;
         }
      };
      FAT_TOWER_BRIDGES = Lists.newArrayList(new Tuple[]{new Tuple(Rotation.NONE, new BlockPos(4, -1, 0)), new Tuple(Rotation.CLOCKWISE_90, new BlockPos(12, -1, 4)), new Tuple(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)), new Tuple(Rotation.CLOCKWISE_180, new BlockPos(8, -1, 12))});
      FAT_TOWER_GENERATOR = new EndCityPieces.IGenerator() {
         public void init() {
         }

         public boolean generate(TemplateManager p_191086_1_, int p_191086_2_, EndCityPieces.CityTemplate p_191086_3_, BlockPos p_191086_4_, List<StructurePiece> p_191086_5_, Random p_191086_6_) {
            Rotation lvt_8_1_ = p_191086_3_.placeSettings.getRotation();
            EndCityPieces.CityTemplate lvt_7_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, p_191086_3_, new BlockPos(-3, 4, -3), "fat_tower_base", lvt_8_1_, true));
            lvt_7_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_7_1_, new BlockPos(0, 4, 0), "fat_tower_middle", lvt_8_1_, true));

            for(int lvt_9_1_ = 0; lvt_9_1_ < 2 && p_191086_6_.nextInt(3) != 0; ++lvt_9_1_) {
               lvt_7_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_7_1_, new BlockPos(0, 8, 0), "fat_tower_middle", lvt_8_1_, true));
               Iterator var10 = EndCityPieces.FAT_TOWER_BRIDGES.iterator();

               while(var10.hasNext()) {
                  Tuple<Rotation, BlockPos> lvt_11_1_ = (Tuple)var10.next();
                  if (p_191086_6_.nextBoolean()) {
                     EndCityPieces.CityTemplate lvt_12_1_ = EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_7_1_, (BlockPos)lvt_11_1_.getB(), "bridge_end", lvt_8_1_.add((Rotation)lvt_11_1_.getA()), true));
                     EndCityPieces.recursiveChildren(p_191086_1_, EndCityPieces.TOWER_BRIDGE_GENERATOR, p_191086_2_ + 1, lvt_12_1_, (BlockPos)null, p_191086_5_, p_191086_6_);
                  }
               }
            }

            EndCityPieces.addHelper(p_191086_5_, EndCityPieces.addPiece(p_191086_1_, lvt_7_1_, new BlockPos(-2, 8, -2), "fat_tower_top", lvt_8_1_, true));
            return true;
         }
      };
   }

   interface IGenerator {
      void init();

      boolean generate(TemplateManager var1, int var2, EndCityPieces.CityTemplate var3, BlockPos var4, List<StructurePiece> var5, Random var6);
   }

   public static class CityTemplate extends TemplateStructurePiece {
      private final String pieceName;
      private final Rotation rotation;
      private final boolean overwrite;

      public CityTemplate(TemplateManager p_i47214_1_, String p_i47214_2_, BlockPos p_i47214_3_, Rotation p_i47214_4_, boolean p_i47214_5_) {
         super(IStructurePieceType.ECP, 0);
         this.pieceName = p_i47214_2_;
         this.templatePosition = p_i47214_3_;
         this.rotation = p_i47214_4_;
         this.overwrite = p_i47214_5_;
         this.loadTemplate(p_i47214_1_);
      }

      public CityTemplate(TemplateManager p_i50598_1_, CompoundNBT p_i50598_2_) {
         super(IStructurePieceType.ECP, p_i50598_2_);
         this.pieceName = p_i50598_2_.getString("Template");
         this.rotation = Rotation.valueOf(p_i50598_2_.getString("Rot"));
         this.overwrite = p_i50598_2_.getBoolean("OW");
         this.loadTemplate(p_i50598_1_);
      }

      private void loadTemplate(TemplateManager p_191085_1_) {
         Template lvt_2_1_ = p_191085_1_.getTemplateDefaulted(new ResourceLocation("end_city/" + this.pieceName));
         PlacementSettings lvt_3_1_ = (this.overwrite ? EndCityPieces.OVERWRITE : EndCityPieces.INSERT).copy().setRotation(this.rotation);
         this.setup(lvt_2_1_, this.templatePosition, lvt_3_1_);
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putString("Template", this.pieceName);
         p_143011_1_.putString("Rot", this.rotation.name());
         p_143011_1_.putBoolean("OW", this.overwrite);
      }

      protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
         if (p_186175_1_.startsWith("Chest")) {
            BlockPos lvt_6_1_ = p_186175_2_.down();
            if (p_186175_5_.isVecInside(lvt_6_1_)) {
               LockableLootTileEntity.setLootTable(p_186175_3_, p_186175_4_, lvt_6_1_, LootTables.CHESTS_END_CITY_TREASURE);
            }
         } else if (p_186175_1_.startsWith("Sentry")) {
            ShulkerEntity lvt_6_2_ = (ShulkerEntity)EntityType.SHULKER.create(p_186175_3_.getWorld());
            lvt_6_2_.setPosition((double)p_186175_2_.getX() + 0.5D, (double)p_186175_2_.getY() + 0.5D, (double)p_186175_2_.getZ() + 0.5D);
            lvt_6_2_.setAttachmentPos(p_186175_2_);
            p_186175_3_.addEntity(lvt_6_2_);
         } else if (p_186175_1_.startsWith("Elytra")) {
            ItemFrameEntity lvt_6_3_ = new ItemFrameEntity(p_186175_3_.getWorld(), p_186175_2_, this.rotation.rotate(Direction.SOUTH));
            lvt_6_3_.setDisplayedItemWithUpdate(new ItemStack(Items.ELYTRA), false);
            p_186175_3_.addEntity(lvt_6_3_);
         }

      }
   }
}
