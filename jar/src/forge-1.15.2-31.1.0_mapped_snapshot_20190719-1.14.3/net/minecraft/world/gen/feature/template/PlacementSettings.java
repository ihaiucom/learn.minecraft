package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;

public class PlacementSettings {
   private Mirror mirror;
   private Rotation rotation;
   private BlockPos centerOffset;
   private boolean ignoreEntities;
   @Nullable
   private ChunkPos chunk;
   @Nullable
   private MutableBoundingBox boundingBox;
   private boolean field_204765_h;
   @Nullable
   private Random random;
   @Nullable
   private int field_204767_m;
   private final List<StructureProcessor> processors;
   private boolean field_215225_l;

   public PlacementSettings() {
      this.mirror = Mirror.NONE;
      this.rotation = Rotation.NONE;
      this.centerOffset = BlockPos.ZERO;
      this.field_204765_h = true;
      this.processors = Lists.newArrayList();
   }

   public PlacementSettings copy() {
      PlacementSettings lvt_1_1_ = new PlacementSettings();
      lvt_1_1_.mirror = this.mirror;
      lvt_1_1_.rotation = this.rotation;
      lvt_1_1_.centerOffset = this.centerOffset;
      lvt_1_1_.ignoreEntities = this.ignoreEntities;
      lvt_1_1_.chunk = this.chunk;
      lvt_1_1_.boundingBox = this.boundingBox;
      lvt_1_1_.field_204765_h = this.field_204765_h;
      lvt_1_1_.random = this.random;
      lvt_1_1_.field_204767_m = this.field_204767_m;
      lvt_1_1_.processors.addAll(this.processors);
      lvt_1_1_.field_215225_l = this.field_215225_l;
      return lvt_1_1_;
   }

   public PlacementSettings setMirror(Mirror p_186214_1_) {
      this.mirror = p_186214_1_;
      return this;
   }

   public PlacementSettings setRotation(Rotation p_186220_1_) {
      this.rotation = p_186220_1_;
      return this;
   }

   public PlacementSettings setCenterOffset(BlockPos p_207665_1_) {
      this.centerOffset = p_207665_1_;
      return this;
   }

   public PlacementSettings setIgnoreEntities(boolean p_186222_1_) {
      this.ignoreEntities = p_186222_1_;
      return this;
   }

   public PlacementSettings setChunk(ChunkPos p_186218_1_) {
      this.chunk = p_186218_1_;
      return this;
   }

   public PlacementSettings setBoundingBox(MutableBoundingBox p_186223_1_) {
      this.boundingBox = p_186223_1_;
      return this;
   }

   public PlacementSettings setRandom(@Nullable Random p_189950_1_) {
      this.random = p_189950_1_;
      return this;
   }

   public PlacementSettings func_215223_c(boolean p_215223_1_) {
      this.field_215225_l = p_215223_1_;
      return this;
   }

   public PlacementSettings func_215219_b() {
      this.processors.clear();
      return this;
   }

   public PlacementSettings addProcessor(StructureProcessor p_215222_1_) {
      this.processors.add(p_215222_1_);
      return this;
   }

   public PlacementSettings func_215220_b(StructureProcessor p_215220_1_) {
      this.processors.remove(p_215220_1_);
      return this;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public BlockPos func_207664_d() {
      return this.centerOffset;
   }

   public Random getRandom(@Nullable BlockPos p_189947_1_) {
      if (this.random != null) {
         return this.random;
      } else {
         return p_189947_1_ == null ? new Random(Util.milliTime()) : new Random(MathHelper.getPositionRandom(p_189947_1_));
      }
   }

   public boolean getIgnoreEntities() {
      return this.ignoreEntities;
   }

   @Nullable
   public MutableBoundingBox getBoundingBox() {
      if (this.boundingBox == null && this.chunk != null) {
         this.setBoundingBoxFromChunk();
      }

      return this.boundingBox;
   }

   public boolean func_215218_i() {
      return this.field_215225_l;
   }

   public List<StructureProcessor> getProcessors() {
      return this.processors;
   }

   void setBoundingBoxFromChunk() {
      if (this.chunk != null) {
         this.boundingBox = this.getBoundingBoxFromChunk(this.chunk);
      }

   }

   public boolean func_204763_l() {
      return this.field_204765_h;
   }

   public List<Template.BlockInfo> func_227459_a_(List<List<Template.BlockInfo>> p_227459_1_, @Nullable BlockPos p_227459_2_) {
      int lvt_3_1_ = p_227459_1_.size();
      return lvt_3_1_ > 0 ? (List)p_227459_1_.get(this.getRandom(p_227459_2_).nextInt(lvt_3_1_)) : Collections.emptyList();
   }

   @Nullable
   private MutableBoundingBox getBoundingBoxFromChunk(@Nullable ChunkPos p_186216_1_) {
      if (p_186216_1_ == null) {
         return this.boundingBox;
      } else {
         int lvt_2_1_ = p_186216_1_.x * 16;
         int lvt_3_1_ = p_186216_1_.z * 16;
         return new MutableBoundingBox(lvt_2_1_, 0, lvt_3_1_, lvt_2_1_ + 16 - 1, 255, lvt_3_1_ + 16 - 1);
      }
   }
}
