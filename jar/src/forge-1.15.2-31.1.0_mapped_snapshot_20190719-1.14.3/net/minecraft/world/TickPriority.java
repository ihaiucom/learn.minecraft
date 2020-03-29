package net.minecraft.world;

public enum TickPriority {
   EXTREMELY_HIGH(-3),
   VERY_HIGH(-2),
   HIGH(-1),
   NORMAL(0),
   LOW(1),
   VERY_LOW(2),
   EXTREMELY_LOW(3);

   private final int priority;

   private TickPriority(int p_i48976_3_) {
      this.priority = p_i48976_3_;
   }

   public static TickPriority getPriority(int p_205397_0_) {
      TickPriority[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         TickPriority lvt_4_1_ = var1[var3];
         if (lvt_4_1_.priority == p_205397_0_) {
            return lvt_4_1_;
         }
      }

      if (p_205397_0_ < EXTREMELY_HIGH.priority) {
         return EXTREMELY_HIGH;
      } else {
         return EXTREMELY_LOW;
      }
   }

   public int getPriority() {
      return this.priority;
   }
}
