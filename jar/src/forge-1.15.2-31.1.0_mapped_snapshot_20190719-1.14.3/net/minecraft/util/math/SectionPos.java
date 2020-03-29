package net.minecraft.util.math;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;

public class SectionPos extends Vec3i {
   private SectionPos(int p_i50794_1_, int p_i50794_2_, int p_i50794_3_) {
      super(p_i50794_1_, p_i50794_2_, p_i50794_3_);
   }

   public static SectionPos of(int p_218154_0_, int p_218154_1_, int p_218154_2_) {
      return new SectionPos(p_218154_0_, p_218154_1_, p_218154_2_);
   }

   public static SectionPos from(BlockPos p_218167_0_) {
      return new SectionPos(toChunk(p_218167_0_.getX()), toChunk(p_218167_0_.getY()), toChunk(p_218167_0_.getZ()));
   }

   public static SectionPos from(ChunkPos p_218156_0_, int p_218156_1_) {
      return new SectionPos(p_218156_0_.x, p_218156_1_, p_218156_0_.z);
   }

   public static SectionPos from(Entity p_218157_0_) {
      return new SectionPos(toChunk(MathHelper.floor(p_218157_0_.func_226277_ct_())), toChunk(MathHelper.floor(p_218157_0_.func_226278_cu_())), toChunk(MathHelper.floor(p_218157_0_.func_226281_cx_())));
   }

   public static SectionPos from(long p_218170_0_) {
      return new SectionPos(extractX(p_218170_0_), extractY(p_218170_0_), extractZ(p_218170_0_));
   }

   public static long withOffset(long p_218172_0_, Direction p_218172_2_) {
      return withOffset(p_218172_0_, p_218172_2_.getXOffset(), p_218172_2_.getYOffset(), p_218172_2_.getZOffset());
   }

   public static long withOffset(long p_218174_0_, int p_218174_2_, int p_218174_3_, int p_218174_4_) {
      return asLong(extractX(p_218174_0_) + p_218174_2_, extractY(p_218174_0_) + p_218174_3_, extractZ(p_218174_0_) + p_218174_4_);
   }

   public static int toChunk(int p_218159_0_) {
      return p_218159_0_ >> 4;
   }

   public static int mask(int p_218171_0_) {
      return p_218171_0_ & 15;
   }

   public static short toRelativeOffset(BlockPos p_218150_0_) {
      int lvt_1_1_ = mask(p_218150_0_.getX());
      int lvt_2_1_ = mask(p_218150_0_.getY());
      int lvt_3_1_ = mask(p_218150_0_.getZ());
      return (short)(lvt_1_1_ << 8 | lvt_3_1_ << 4 | lvt_2_1_);
   }

   public static int toWorld(int p_218142_0_) {
      return p_218142_0_ << 4;
   }

   public static int extractX(long p_218173_0_) {
      return (int)(p_218173_0_ << 0 >> 42);
   }

   public static int extractY(long p_218144_0_) {
      return (int)(p_218144_0_ << 44 >> 44);
   }

   public static int extractZ(long p_218153_0_) {
      return (int)(p_218153_0_ << 22 >> 42);
   }

   public int getSectionX() {
      return this.getX();
   }

   public int getSectionY() {
      return this.getY();
   }

   public int getSectionZ() {
      return this.getZ();
   }

   public int getWorldStartX() {
      return this.getSectionX() << 4;
   }

   public int getWorldStartY() {
      return this.getSectionY() << 4;
   }

   public int getWorldStartZ() {
      return this.getSectionZ() << 4;
   }

   public int getWorldEndX() {
      return (this.getSectionX() << 4) + 15;
   }

   public int getWorldEndY() {
      return (this.getSectionY() << 4) + 15;
   }

   public int getWorldEndZ() {
      return (this.getSectionZ() << 4) + 15;
   }

   public static long worldToSection(long p_218162_0_) {
      return asLong(toChunk(BlockPos.unpackX(p_218162_0_)), toChunk(BlockPos.unpackY(p_218162_0_)), toChunk(BlockPos.unpackZ(p_218162_0_)));
   }

   public static long toSectionColumnPos(long p_218169_0_) {
      return p_218169_0_ & -1048576L;
   }

   public BlockPos asBlockPos() {
      return new BlockPos(toWorld(this.getSectionX()), toWorld(this.getSectionY()), toWorld(this.getSectionZ()));
   }

   public BlockPos getCenter() {
      int lvt_1_1_ = true;
      return this.asBlockPos().add(8, 8, 8);
   }

   public ChunkPos asChunkPos() {
      return new ChunkPos(this.getSectionX(), this.getSectionZ());
   }

   public static long asLong(int p_218166_0_, int p_218166_1_, int p_218166_2_) {
      long lvt_3_1_ = 0L;
      lvt_3_1_ |= ((long)p_218166_0_ & 4194303L) << 42;
      lvt_3_1_ |= ((long)p_218166_1_ & 1048575L) << 0;
      lvt_3_1_ |= ((long)p_218166_2_ & 4194303L) << 20;
      return lvt_3_1_;
   }

   public long asLong() {
      return asLong(this.getSectionX(), this.getSectionY(), this.getSectionZ());
   }

   public Stream<BlockPos> allBlocksWithin() {
      return BlockPos.getAllInBox(this.getWorldStartX(), this.getWorldStartY(), this.getWorldStartZ(), this.getWorldEndX(), this.getWorldEndY(), this.getWorldEndZ());
   }

   public static Stream<SectionPos> getAllInBox(SectionPos p_218158_0_, int p_218158_1_) {
      int lvt_2_1_ = p_218158_0_.getSectionX();
      int lvt_3_1_ = p_218158_0_.getSectionY();
      int lvt_4_1_ = p_218158_0_.getSectionZ();
      return getAllInBox(lvt_2_1_ - p_218158_1_, lvt_3_1_ - p_218158_1_, lvt_4_1_ - p_218158_1_, lvt_2_1_ + p_218158_1_, lvt_3_1_ + p_218158_1_, lvt_4_1_ + p_218158_1_);
   }

   public static Stream<SectionPos> func_229421_b_(ChunkPos p_229421_0_, int p_229421_1_) {
      int lvt_2_1_ = p_229421_0_.x;
      int lvt_3_1_ = p_229421_0_.z;
      return getAllInBox(lvt_2_1_ - p_229421_1_, 0, lvt_3_1_ - p_229421_1_, lvt_2_1_ + p_229421_1_, 15, lvt_3_1_ + p_229421_1_);
   }

   public static Stream<SectionPos> getAllInBox(final int p_218168_0_, final int p_218168_1_, final int p_218168_2_, final int p_218168_3_, final int p_218168_4_, final int p_218168_5_) {
      return StreamSupport.stream(new AbstractSpliterator<SectionPos>((long)((p_218168_3_ - p_218168_0_ + 1) * (p_218168_4_ - p_218168_1_ + 1) * (p_218168_5_ - p_218168_2_ + 1)), 64) {
         final CubeCoordinateIterator field_218394_a = new CubeCoordinateIterator(p_218168_0_, p_218168_1_, p_218168_2_, p_218168_3_, p_218168_4_, p_218168_5_);

         public boolean tryAdvance(Consumer<? super SectionPos> p_tryAdvance_1_) {
            if (this.field_218394_a.hasNext()) {
               p_tryAdvance_1_.accept(new SectionPos(this.field_218394_a.getX(), this.field_218394_a.getY(), this.field_218394_a.getZ()));
               return true;
            } else {
               return false;
            }
         }
      }, false);
   }

   // $FF: synthetic method
   SectionPos(int p_i50795_1_, int p_i50795_2_, int p_i50795_3_, Object p_i50795_4_) {
      this(p_i50795_1_, p_i50795_2_, p_i50795_3_);
   }
}
