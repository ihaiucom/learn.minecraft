package com.mojang.realmsclient.util;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextRenderingUtils {
   static List<String> func_225223_a(String p_225223_0_) {
      return Arrays.asList(p_225223_0_.split("\\n"));
   }

   public static List<TextRenderingUtils.Line> func_225224_a(String p_225224_0_, TextRenderingUtils.LineSegment... p_225224_1_) {
      return func_225225_a(p_225224_0_, Arrays.asList(p_225224_1_));
   }

   private static List<TextRenderingUtils.Line> func_225225_a(String p_225225_0_, List<TextRenderingUtils.LineSegment> p_225225_1_) {
      List<String> lvt_2_1_ = func_225223_a(p_225225_0_);
      return func_225222_a(lvt_2_1_, p_225225_1_);
   }

   private static List<TextRenderingUtils.Line> func_225222_a(List<String> p_225222_0_, List<TextRenderingUtils.LineSegment> p_225222_1_) {
      int lvt_2_1_ = 0;
      List<TextRenderingUtils.Line> lvt_3_1_ = Lists.newArrayList();
      Iterator var4 = p_225222_0_.iterator();

      while(var4.hasNext()) {
         String lvt_5_1_ = (String)var4.next();
         List<TextRenderingUtils.LineSegment> lvt_6_1_ = Lists.newArrayList();
         List<String> lvt_7_1_ = func_225226_a(lvt_5_1_, "%link");
         Iterator var8 = lvt_7_1_.iterator();

         while(var8.hasNext()) {
            String lvt_9_1_ = (String)var8.next();
            if (lvt_9_1_.equals("%link")) {
               lvt_6_1_.add(p_225222_1_.get(lvt_2_1_++));
            } else {
               lvt_6_1_.add(TextRenderingUtils.LineSegment.func_225218_a(lvt_9_1_));
            }
         }

         lvt_3_1_.add(new TextRenderingUtils.Line(lvt_6_1_));
      }

      return lvt_3_1_;
   }

   public static List<String> func_225226_a(String p_225226_0_, String p_225226_1_) {
      if (p_225226_1_.isEmpty()) {
         throw new IllegalArgumentException("Delimiter cannot be the empty string");
      } else {
         List<String> lvt_2_1_ = Lists.newArrayList();

         int lvt_3_1_;
         int lvt_4_1_;
         for(lvt_3_1_ = 0; (lvt_4_1_ = p_225226_0_.indexOf(p_225226_1_, lvt_3_1_)) != -1; lvt_3_1_ = lvt_4_1_ + p_225226_1_.length()) {
            if (lvt_4_1_ > lvt_3_1_) {
               lvt_2_1_.add(p_225226_0_.substring(lvt_3_1_, lvt_4_1_));
            }

            lvt_2_1_.add(p_225226_1_);
         }

         if (lvt_3_1_ < p_225226_0_.length()) {
            lvt_2_1_.add(p_225226_0_.substring(lvt_3_1_));
         }

         return lvt_2_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LineSegment {
      final String field_225219_a;
      final String field_225220_b;
      final String field_225221_c;

      private LineSegment(String p_i51642_1_) {
         this.field_225219_a = p_i51642_1_;
         this.field_225220_b = null;
         this.field_225221_c = null;
      }

      private LineSegment(String p_i51643_1_, String p_i51643_2_, String p_i51643_3_) {
         this.field_225219_a = p_i51643_1_;
         this.field_225220_b = p_i51643_2_;
         this.field_225221_c = p_i51643_3_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            TextRenderingUtils.LineSegment lvt_2_1_ = (TextRenderingUtils.LineSegment)p_equals_1_;
            return Objects.equals(this.field_225219_a, lvt_2_1_.field_225219_a) && Objects.equals(this.field_225220_b, lvt_2_1_.field_225220_b) && Objects.equals(this.field_225221_c, lvt_2_1_.field_225221_c);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.field_225219_a, this.field_225220_b, this.field_225221_c});
      }

      public String toString() {
         return "Segment{fullText='" + this.field_225219_a + '\'' + ", linkTitle='" + this.field_225220_b + '\'' + ", linkUrl='" + this.field_225221_c + '\'' + '}';
      }

      public String func_225215_a() {
         return this.func_225217_b() ? this.field_225220_b : this.field_225219_a;
      }

      public boolean func_225217_b() {
         return this.field_225220_b != null;
      }

      public String func_225216_c() {
         if (!this.func_225217_b()) {
            throw new IllegalStateException("Not a link: " + this);
         } else {
            return this.field_225221_c;
         }
      }

      public static TextRenderingUtils.LineSegment func_225214_a(String p_225214_0_, String p_225214_1_) {
         return new TextRenderingUtils.LineSegment((String)null, p_225214_0_, p_225214_1_);
      }

      static TextRenderingUtils.LineSegment func_225218_a(String p_225218_0_) {
         return new TextRenderingUtils.LineSegment(p_225218_0_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Line {
      public final List<TextRenderingUtils.LineSegment> field_225213_a;

      Line(List<TextRenderingUtils.LineSegment> p_i51644_1_) {
         this.field_225213_a = p_i51644_1_;
      }

      public String toString() {
         return "Line{segments=" + this.field_225213_a + '}';
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            TextRenderingUtils.Line lvt_2_1_ = (TextRenderingUtils.Line)p_equals_1_;
            return Objects.equals(this.field_225213_a, lvt_2_1_.field_225213_a);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.field_225213_a});
      }
   }
}
