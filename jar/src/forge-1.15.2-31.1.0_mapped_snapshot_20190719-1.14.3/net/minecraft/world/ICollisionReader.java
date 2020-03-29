package net.minecraft.world;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.CubeCoordinateIterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.border.WorldBorder;

public interface ICollisionReader extends IBlockReader {
   WorldBorder getWorldBorder();

   @Nullable
   IBlockReader func_225522_c_(int var1, int var2);

   default boolean checkNoEntityCollision(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      return true;
   }

   default boolean func_226663_a_(BlockState p_226663_1_, BlockPos p_226663_2_, ISelectionContext p_226663_3_) {
      VoxelShape lvt_4_1_ = p_226663_1_.getCollisionShape(this, p_226663_2_, p_226663_3_);
      return lvt_4_1_.isEmpty() || this.checkNoEntityCollision((Entity)null, lvt_4_1_.withOffset((double)p_226663_2_.getX(), (double)p_226663_2_.getY(), (double)p_226663_2_.getZ()));
   }

   default boolean func_226668_i_(Entity p_226668_1_) {
      return this.checkNoEntityCollision(p_226668_1_, VoxelShapes.create(p_226668_1_.getBoundingBox()));
   }

   default boolean func_226664_a_(AxisAlignedBB p_226664_1_) {
      return this.func_226662_a_((Entity)null, p_226664_1_, Collections.emptySet());
   }

   default boolean func_226669_j_(Entity p_226669_1_) {
      return this.func_226662_a_(p_226669_1_, p_226669_1_.getBoundingBox(), Collections.emptySet());
   }

   default boolean func_226665_a__(Entity p_226665_1_, AxisAlignedBB p_226665_2_) {
      return this.func_226662_a_(p_226665_1_, p_226665_2_, Collections.emptySet());
   }

   default boolean func_226662_a_(@Nullable Entity p_226662_1_, AxisAlignedBB p_226662_2_, Set<Entity> p_226662_3_) {
      return this.func_226667_c_(p_226662_1_, p_226662_2_, p_226662_3_).allMatch(VoxelShape::isEmpty);
   }

   default Stream<VoxelShape> getEmptyCollisionShapes(@Nullable Entity p_223439_1_, AxisAlignedBB p_223439_2_, Set<Entity> p_223439_3_) {
      return Stream.empty();
   }

   default Stream<VoxelShape> func_226667_c_(@Nullable Entity p_226667_1_, AxisAlignedBB p_226667_2_, Set<Entity> p_226667_3_) {
      return Streams.concat(new Stream[]{this.func_226666_b_(p_226667_1_, p_226667_2_), this.getEmptyCollisionShapes(p_226667_1_, p_226667_2_, p_226667_3_)});
   }

   default Stream<VoxelShape> func_226666_b_(@Nullable final Entity p_226666_1_, AxisAlignedBB p_226666_2_) {
      int lvt_3_1_ = MathHelper.floor(p_226666_2_.minX - 1.0E-7D) - 1;
      int lvt_4_1_ = MathHelper.floor(p_226666_2_.maxX + 1.0E-7D) + 1;
      int lvt_5_1_ = MathHelper.floor(p_226666_2_.minY - 1.0E-7D) - 1;
      int lvt_6_1_ = MathHelper.floor(p_226666_2_.maxY + 1.0E-7D) + 1;
      int lvt_7_1_ = MathHelper.floor(p_226666_2_.minZ - 1.0E-7D) - 1;
      int lvt_8_1_ = MathHelper.floor(p_226666_2_.maxZ + 1.0E-7D) + 1;
      final ISelectionContext lvt_9_1_ = p_226666_1_ == null ? ISelectionContext.dummy() : ISelectionContext.forEntity(p_226666_1_);
      final CubeCoordinateIterator lvt_10_1_ = new CubeCoordinateIterator(lvt_3_1_, lvt_5_1_, lvt_7_1_, lvt_4_1_, lvt_6_1_, lvt_8_1_);
      final BlockPos.Mutable lvt_11_1_ = new BlockPos.Mutable();
      final VoxelShape lvt_12_1_ = VoxelShapes.create(p_226666_2_);
      return StreamSupport.stream(new AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280) {
         boolean field_226670_a_ = p_226666_1_ == null;

         public boolean tryAdvance(Consumer<? super VoxelShape> p_tryAdvance_1_) {
            if (!this.field_226670_a_) {
               this.field_226670_a_ = true;
               VoxelShape lvt_2_1_ = ICollisionReader.this.getWorldBorder().getShape();
               boolean lvt_3_1_ = VoxelShapes.compare(lvt_2_1_, VoxelShapes.create(p_226666_1_.getBoundingBox().shrink(1.0E-7D)), IBooleanFunction.AND);
               boolean lvt_4_1_ = VoxelShapes.compare(lvt_2_1_, VoxelShapes.create(p_226666_1_.getBoundingBox().grow(1.0E-7D)), IBooleanFunction.AND);
               if (!lvt_3_1_ && lvt_4_1_) {
                  p_tryAdvance_1_.accept(lvt_2_1_);
                  return true;
               }
            }

            VoxelShape lvt_11_1_x;
            do {
               int lvt_5_1_;
               BlockState lvt_9_1_x;
               int lvt_2_2_;
               int lvt_3_2_;
               int lvt_4_2_;
               do {
                  do {
                     IBlockReader lvt_8_1_;
                     do {
                        do {
                           if (!lvt_10_1_.hasNext()) {
                              return false;
                           }

                           lvt_2_2_ = lvt_10_1_.getX();
                           lvt_3_2_ = lvt_10_1_.getY();
                           lvt_4_2_ = lvt_10_1_.getZ();
                           lvt_5_1_ = lvt_10_1_.func_223473_e();
                        } while(lvt_5_1_ == 3);

                        int lvt_6_1_ = lvt_2_2_ >> 4;
                        int lvt_7_1_ = lvt_4_2_ >> 4;
                        lvt_8_1_ = ICollisionReader.this.func_225522_c_(lvt_6_1_, lvt_7_1_);
                     } while(lvt_8_1_ == null);

                     lvt_11_1_.setPos(lvt_2_2_, lvt_3_2_, lvt_4_2_);
                     lvt_9_1_x = lvt_8_1_.getBlockState(lvt_11_1_);
                  } while(lvt_5_1_ == 1 && !lvt_9_1_x.func_215704_f());
               } while(lvt_5_1_ == 2 && lvt_9_1_x.getBlock() != Blocks.MOVING_PISTON);

               VoxelShape lvt_10_1_x = lvt_9_1_x.getCollisionShape(ICollisionReader.this, lvt_11_1_, lvt_9_1_);
               lvt_11_1_x = lvt_10_1_x.withOffset((double)lvt_2_2_, (double)lvt_3_2_, (double)lvt_4_2_);
            } while(!VoxelShapes.compare(lvt_12_1_, lvt_11_1_x, IBooleanFunction.AND));

            p_tryAdvance_1_.accept(lvt_11_1_x);
            return true;
         }
      }, false);
   }
}
