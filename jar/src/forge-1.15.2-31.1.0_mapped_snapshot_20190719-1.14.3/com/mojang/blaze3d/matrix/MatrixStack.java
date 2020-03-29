package com.mojang.blaze3d.matrix;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MatrixStack {
   private final Deque<MatrixStack.Entry> field_227859_a_ = (Deque)Util.make(Queues.newArrayDeque(), (p_227864_0_) -> {
      Matrix4f lvt_1_1_ = new Matrix4f();
      lvt_1_1_.func_226591_a_();
      Matrix3f lvt_2_1_ = new Matrix3f();
      lvt_2_1_.func_226119_c_();
      p_227864_0_.add(new MatrixStack.Entry(lvt_1_1_, lvt_2_1_));
   });

   public void func_227861_a_(double p_227861_1_, double p_227861_3_, double p_227861_5_) {
      MatrixStack.Entry lvt_7_1_ = (MatrixStack.Entry)this.field_227859_a_.getLast();
      lvt_7_1_.field_227868_a_.func_226595_a_(Matrix4f.func_226599_b_((float)p_227861_1_, (float)p_227861_3_, (float)p_227861_5_));
   }

   public void func_227862_a_(float p_227862_1_, float p_227862_2_, float p_227862_3_) {
      MatrixStack.Entry lvt_4_1_ = (MatrixStack.Entry)this.field_227859_a_.getLast();
      lvt_4_1_.field_227868_a_.func_226595_a_(Matrix4f.func_226593_a_(p_227862_1_, p_227862_2_, p_227862_3_));
      if (p_227862_1_ == p_227862_2_ && p_227862_2_ == p_227862_3_) {
         if (p_227862_1_ > 0.0F) {
            return;
         }

         lvt_4_1_.field_227869_b_.func_226111_a_(-1.0F);
      }

      float lvt_5_1_ = 1.0F / p_227862_1_;
      float lvt_6_1_ = 1.0F / p_227862_2_;
      float lvt_7_1_ = 1.0F / p_227862_3_;
      float lvt_8_1_ = MathHelper.func_226166_j_(lvt_5_1_ * lvt_6_1_ * lvt_7_1_);
      lvt_4_1_.field_227869_b_.func_226118_b_(Matrix3f.func_226117_b_(lvt_8_1_ * lvt_5_1_, lvt_8_1_ * lvt_6_1_, lvt_8_1_ * lvt_7_1_));
   }

   public void func_227863_a_(Quaternion p_227863_1_) {
      MatrixStack.Entry lvt_2_1_ = (MatrixStack.Entry)this.field_227859_a_.getLast();
      lvt_2_1_.field_227868_a_.func_226596_a_(p_227863_1_);
      lvt_2_1_.field_227869_b_.func_226115_a_(p_227863_1_);
   }

   public void func_227860_a_() {
      MatrixStack.Entry lvt_1_1_ = (MatrixStack.Entry)this.field_227859_a_.getLast();
      this.field_227859_a_.addLast(new MatrixStack.Entry(lvt_1_1_.field_227868_a_.func_226601_d_(), lvt_1_1_.field_227869_b_.func_226121_d_()));
   }

   public void func_227865_b_() {
      this.field_227859_a_.removeLast();
   }

   public MatrixStack.Entry func_227866_c_() {
      return (MatrixStack.Entry)this.field_227859_a_.getLast();
   }

   public boolean func_227867_d_() {
      return this.field_227859_a_.size() == 1;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Entry {
      private final Matrix4f field_227868_a_;
      private final Matrix3f field_227869_b_;

      private Entry(Matrix4f p_i225909_1_, Matrix3f p_i225909_2_) {
         this.field_227868_a_ = p_i225909_1_;
         this.field_227869_b_ = p_i225909_2_;
      }

      public Matrix4f func_227870_a_() {
         return this.field_227868_a_;
      }

      public Matrix3f func_227872_b_() {
         return this.field_227869_b_;
      }

      // $FF: synthetic method
      Entry(Matrix4f p_i225910_1_, Matrix3f p_i225910_2_, Object p_i225910_3_) {
         this(p_i225910_1_, p_i225910_2_);
      }
   }
}
