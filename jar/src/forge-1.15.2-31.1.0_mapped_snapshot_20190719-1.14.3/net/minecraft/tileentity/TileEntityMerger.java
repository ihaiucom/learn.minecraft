package net.minecraft.tileentity;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TileEntityMerger {
   public static <S extends TileEntity> TileEntityMerger.ICallbackWrapper<S> func_226924_a_(TileEntityType<S> p_226924_0_, Function<BlockState, TileEntityMerger.Type> p_226924_1_, Function<BlockState, Direction> p_226924_2_, DirectionProperty p_226924_3_, BlockState p_226924_4_, IWorld p_226924_5_, BlockPos p_226924_6_, BiPredicate<IWorld, BlockPos> p_226924_7_) {
      S lvt_8_1_ = p_226924_0_.func_226986_a_(p_226924_5_, p_226924_6_);
      if (lvt_8_1_ == null) {
         return TileEntityMerger.ICallback::func_225537_b_;
      } else if (p_226924_7_.test(p_226924_5_, p_226924_6_)) {
         return TileEntityMerger.ICallback::func_225537_b_;
      } else {
         TileEntityMerger.Type lvt_9_1_ = (TileEntityMerger.Type)p_226924_1_.apply(p_226924_4_);
         boolean lvt_10_1_ = lvt_9_1_ == TileEntityMerger.Type.SINGLE;
         boolean lvt_11_1_ = lvt_9_1_ == TileEntityMerger.Type.FIRST;
         if (lvt_10_1_) {
            return new TileEntityMerger.ICallbackWrapper.Single(lvt_8_1_);
         } else {
            BlockPos lvt_12_1_ = p_226924_6_.offset((Direction)p_226924_2_.apply(p_226924_4_));
            BlockState lvt_13_1_ = p_226924_5_.getBlockState(lvt_12_1_);
            if (lvt_13_1_.getBlock() == p_226924_4_.getBlock()) {
               TileEntityMerger.Type lvt_14_1_ = (TileEntityMerger.Type)p_226924_1_.apply(lvt_13_1_);
               if (lvt_14_1_ != TileEntityMerger.Type.SINGLE && lvt_9_1_ != lvt_14_1_ && lvt_13_1_.get(p_226924_3_) == p_226924_4_.get(p_226924_3_)) {
                  if (p_226924_7_.test(p_226924_5_, lvt_12_1_)) {
                     return TileEntityMerger.ICallback::func_225537_b_;
                  }

                  S lvt_15_1_ = p_226924_0_.func_226986_a_(p_226924_5_, lvt_12_1_);
                  if (lvt_15_1_ != null) {
                     S lvt_16_1_ = lvt_11_1_ ? lvt_8_1_ : lvt_15_1_;
                     S lvt_17_1_ = lvt_11_1_ ? lvt_15_1_ : lvt_8_1_;
                     return new TileEntityMerger.ICallbackWrapper.Double(lvt_16_1_, lvt_17_1_);
                  }
               }
            }

            return new TileEntityMerger.ICallbackWrapper.Single(lvt_8_1_);
         }
      }
   }

   public interface ICallbackWrapper<S> {
      <T> T apply(TileEntityMerger.ICallback<? super S, T> var1);

      public static final class Single<S> implements TileEntityMerger.ICallbackWrapper<S> {
         private final S field_226927_a_;

         public Single(S p_i225761_1_) {
            this.field_226927_a_ = p_i225761_1_;
         }

         public <T> T apply(TileEntityMerger.ICallback<? super S, T> p_apply_1_) {
            return p_apply_1_.func_225538_a_(this.field_226927_a_);
         }
      }

      public static final class Double<S> implements TileEntityMerger.ICallbackWrapper<S> {
         private final S field_226925_a_;
         private final S field_226926_b_;

         public Double(S p_i225760_1_, S p_i225760_2_) {
            this.field_226925_a_ = p_i225760_1_;
            this.field_226926_b_ = p_i225760_2_;
         }

         public <T> T apply(TileEntityMerger.ICallback<? super S, T> p_apply_1_) {
            return p_apply_1_.func_225539_a_(this.field_226925_a_, this.field_226926_b_);
         }
      }
   }

   public interface ICallback<S, T> {
      T func_225539_a_(S var1, S var2);

      T func_225538_a_(S var1);

      T func_225537_b_();
   }

   public static enum Type {
      SINGLE,
      FIRST,
      SECOND;
   }
}
