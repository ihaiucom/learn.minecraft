package net.minecraft.world.gen.feature.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final Logger field_214825_d = LogManager.getLogger();
   protected Template template;
   protected PlacementSettings placeSettings;
   protected BlockPos templatePosition;

   public TemplateStructurePiece(IStructurePieceType p_i51338_1_, int p_i51338_2_) {
      super(p_i51338_1_, p_i51338_2_);
   }

   public TemplateStructurePiece(IStructurePieceType p_i51339_1_, CompoundNBT p_i51339_2_) {
      super(p_i51339_1_, p_i51339_2_);
      this.templatePosition = new BlockPos(p_i51339_2_.getInt("TPX"), p_i51339_2_.getInt("TPY"), p_i51339_2_.getInt("TPZ"));
   }

   protected void setup(Template p_186173_1_, BlockPos p_186173_2_, PlacementSettings p_186173_3_) {
      this.template = p_186173_1_;
      this.setCoordBaseMode(Direction.NORTH);
      this.templatePosition = p_186173_2_;
      this.placeSettings = p_186173_3_;
      this.boundingBox = p_186173_1_.func_215388_b(p_186173_3_, p_186173_2_);
   }

   protected void readAdditional(CompoundNBT p_143011_1_) {
      p_143011_1_.putInt("TPX", this.templatePosition.getX());
      p_143011_1_.putInt("TPY", this.templatePosition.getY());
      p_143011_1_.putInt("TPZ", this.templatePosition.getZ());
   }

   public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
      this.placeSettings.setBoundingBox(p_225577_4_);
      this.boundingBox = this.template.func_215388_b(this.placeSettings, this.templatePosition);
      if (this.template.addBlocksToWorld(p_225577_1_, this.templatePosition, this.placeSettings, 2)) {
         List<Template.BlockInfo> lvt_6_1_ = this.template.func_215381_a(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK);
         Iterator var7 = lvt_6_1_.iterator();

         while(var7.hasNext()) {
            Template.BlockInfo lvt_8_1_ = (Template.BlockInfo)var7.next();
            if (lvt_8_1_.nbt != null) {
               StructureMode lvt_9_1_ = StructureMode.valueOf(lvt_8_1_.nbt.getString("mode"));
               if (lvt_9_1_ == StructureMode.DATA) {
                  this.handleDataMarker(lvt_8_1_.nbt.getString("metadata"), lvt_8_1_.pos, p_225577_1_, p_225577_3_, p_225577_4_);
               }
            }
         }

         List<Template.BlockInfo> lvt_7_1_ = this.template.func_215381_a(this.templatePosition, this.placeSettings, Blocks.field_226904_lY_);
         Iterator var16 = lvt_7_1_.iterator();

         while(var16.hasNext()) {
            Template.BlockInfo lvt_9_2_ = (Template.BlockInfo)var16.next();
            if (lvt_9_2_.nbt != null) {
               String lvt_10_1_ = lvt_9_2_.nbt.getString("final_state");
               BlockStateParser lvt_11_1_ = new BlockStateParser(new StringReader(lvt_10_1_), false);
               BlockState lvt_12_1_ = Blocks.AIR.getDefaultState();

               try {
                  lvt_11_1_.parse(true);
                  BlockState lvt_13_1_ = lvt_11_1_.getState();
                  if (lvt_13_1_ != null) {
                     lvt_12_1_ = lvt_13_1_;
                  } else {
                     field_214825_d.error("Error while parsing blockstate {} in jigsaw block @ {}", lvt_10_1_, lvt_9_2_.pos);
                  }
               } catch (CommandSyntaxException var14) {
                  field_214825_d.error("Error while parsing blockstate {} in jigsaw block @ {}", lvt_10_1_, lvt_9_2_.pos);
               }

               p_225577_1_.setBlockState(lvt_9_2_.pos, lvt_12_1_, 3);
            }
         }
      }

      return true;
   }

   protected abstract void handleDataMarker(String var1, BlockPos var2, IWorld var3, Random var4, MutableBoundingBox var5);

   public void offset(int p_181138_1_, int p_181138_2_, int p_181138_3_) {
      super.offset(p_181138_1_, p_181138_2_, p_181138_3_);
      this.templatePosition = this.templatePosition.add(p_181138_1_, p_181138_2_, p_181138_3_);
   }

   public Rotation getRotation() {
      return this.placeSettings.getRotation();
   }
}
