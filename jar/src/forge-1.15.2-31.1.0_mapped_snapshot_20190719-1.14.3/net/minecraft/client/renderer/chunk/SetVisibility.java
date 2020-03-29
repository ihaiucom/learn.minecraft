package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SetVisibility {
   private static final int COUNT_FACES = Direction.values().length;
   private final BitSet bitSet;

   public SetVisibility() {
      this.bitSet = new BitSet(COUNT_FACES * COUNT_FACES);
   }

   public void setManyVisible(Set<Direction> p_178620_1_) {
      Iterator var2 = p_178620_1_.iterator();

      while(var2.hasNext()) {
         Direction lvt_3_1_ = (Direction)var2.next();
         Iterator var4 = p_178620_1_.iterator();

         while(var4.hasNext()) {
            Direction lvt_5_1_ = (Direction)var4.next();
            this.setVisible(lvt_3_1_, lvt_5_1_, true);
         }
      }

   }

   public void setVisible(Direction p_178619_1_, Direction p_178619_2_, boolean p_178619_3_) {
      this.bitSet.set(p_178619_1_.ordinal() + p_178619_2_.ordinal() * COUNT_FACES, p_178619_3_);
      this.bitSet.set(p_178619_2_.ordinal() + p_178619_1_.ordinal() * COUNT_FACES, p_178619_3_);
   }

   public void setAllVisible(boolean p_178618_1_) {
      this.bitSet.set(0, this.bitSet.size(), p_178618_1_);
   }

   public boolean isVisible(Direction p_178621_1_, Direction p_178621_2_) {
      return this.bitSet.get(p_178621_1_.ordinal() + p_178621_2_.ordinal() * COUNT_FACES);
   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder();
      lvt_1_1_.append(' ');
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      int var4;
      Direction lvt_5_2_;
      for(var4 = 0; var4 < var3; ++var4) {
         lvt_5_2_ = var2[var4];
         lvt_1_1_.append(' ').append(lvt_5_2_.toString().toUpperCase().charAt(0));
      }

      lvt_1_1_.append('\n');
      var2 = Direction.values();
      var3 = var2.length;

      for(var4 = 0; var4 < var3; ++var4) {
         lvt_5_2_ = var2[var4];
         lvt_1_1_.append(lvt_5_2_.toString().toUpperCase().charAt(0));
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction lvt_9_1_ = var6[var8];
            if (lvt_5_2_ == lvt_9_1_) {
               lvt_1_1_.append("  ");
            } else {
               boolean lvt_10_1_ = this.isVisible(lvt_5_2_, lvt_9_1_);
               lvt_1_1_.append(' ').append((char)(lvt_10_1_ ? 'Y' : 'n'));
            }
         }

         lvt_1_1_.append('\n');
      }

      return lvt_1_1_.toString();
   }
}
