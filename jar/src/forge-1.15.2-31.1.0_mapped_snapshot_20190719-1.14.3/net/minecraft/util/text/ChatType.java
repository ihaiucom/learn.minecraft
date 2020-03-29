package net.minecraft.util.text;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ChatType {
   CHAT((byte)0, false),
   SYSTEM((byte)1, true),
   GAME_INFO((byte)2, true);

   private final byte id;
   private final boolean field_218691_e;

   private ChatType(byte p_i50783_3_, boolean p_i50783_4_) {
      this.id = p_i50783_3_;
      this.field_218691_e = p_i50783_4_;
   }

   public byte getId() {
      return this.id;
   }

   public static ChatType byId(byte p_192582_0_) {
      ChatType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ChatType lvt_4_1_ = var1[var3];
         if (p_192582_0_ == lvt_4_1_.id) {
            return lvt_4_1_;
         }
      }

      return CHAT;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_218690_b() {
      return this.field_218691_e;
   }
}
