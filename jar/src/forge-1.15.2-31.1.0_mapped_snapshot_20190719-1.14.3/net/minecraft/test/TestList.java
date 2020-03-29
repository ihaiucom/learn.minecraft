package net.minecraft.test;

import java.util.Iterator;
import java.util.List;

public class TestList {
   private final TestTracker field_229564_a_;
   private final List<TestTickResult> field_229565_b_;
   private long field_229566_c_;

   public void func_229567_a_(long p_229567_1_) {
      try {
         this.func_229569_c_(p_229567_1_);
      } catch (Exception var4) {
      }

   }

   public void func_229568_b_(long p_229568_1_) {
      try {
         this.func_229569_c_(p_229568_1_);
      } catch (Exception var4) {
         this.field_229564_a_.func_229506_a_(var4);
      }

   }

   private void func_229569_c_(long p_229569_1_) {
      Iterator lvt_3_1_ = this.field_229565_b_.iterator();

      while(lvt_3_1_.hasNext()) {
         TestTickResult lvt_4_1_ = (TestTickResult)lvt_3_1_.next();
         lvt_4_1_.field_229486_b_.run();
         lvt_3_1_.remove();
         long lvt_5_1_ = p_229569_1_ - this.field_229566_c_;
         long lvt_7_1_ = this.field_229566_c_;
         this.field_229566_c_ = p_229569_1_;
         if (lvt_4_1_.field_229485_a_ != null && lvt_4_1_.field_229485_a_ != lvt_5_1_) {
            this.field_229564_a_.func_229506_a_(new TestRuntimeException("Succeeded in invalid tick: expected " + (lvt_7_1_ + lvt_4_1_.field_229485_a_) + ", but current tick is " + p_229569_1_));
            break;
         }
      }

   }
}
