package net.minecraft.inventory;

public enum EquipmentSlotType {
   MAINHAND(EquipmentSlotType.Group.HAND, 0, 0, "mainhand"),
   OFFHAND(EquipmentSlotType.Group.HAND, 1, 5, "offhand"),
   FEET(EquipmentSlotType.Group.ARMOR, 0, 1, "feet"),
   LEGS(EquipmentSlotType.Group.ARMOR, 1, 2, "legs"),
   CHEST(EquipmentSlotType.Group.ARMOR, 2, 3, "chest"),
   HEAD(EquipmentSlotType.Group.ARMOR, 3, 4, "head");

   private final EquipmentSlotType.Group slotType;
   private final int index;
   private final int slotIndex;
   private final String name;

   private EquipmentSlotType(EquipmentSlotType.Group p_i46808_3_, int p_i46808_4_, int p_i46808_5_, String p_i46808_6_) {
      this.slotType = p_i46808_3_;
      this.index = p_i46808_4_;
      this.slotIndex = p_i46808_5_;
      this.name = p_i46808_6_;
   }

   public EquipmentSlotType.Group getSlotType() {
      return this.slotType;
   }

   public int getIndex() {
      return this.index;
   }

   public int getSlotIndex() {
      return this.slotIndex;
   }

   public String getName() {
      return this.name;
   }

   public static EquipmentSlotType fromString(String p_188451_0_) {
      EquipmentSlotType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EquipmentSlotType lvt_4_1_ = var1[var3];
         if (lvt_4_1_.getName().equals(p_188451_0_)) {
            return lvt_4_1_;
         }
      }

      throw new IllegalArgumentException("Invalid slot '" + p_188451_0_ + "'");
   }

   public static EquipmentSlotType func_220318_a(EquipmentSlotType.Group p_220318_0_, int p_220318_1_) {
      EquipmentSlotType[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EquipmentSlotType lvt_5_1_ = var2[var4];
         if (lvt_5_1_.getSlotType() == p_220318_0_ && lvt_5_1_.getIndex() == p_220318_1_) {
            return lvt_5_1_;
         }
      }

      throw new IllegalArgumentException("Invalid slot '" + p_220318_0_ + "': " + p_220318_1_);
   }

   public static enum Group {
      HAND,
      ARMOR;
   }
}
