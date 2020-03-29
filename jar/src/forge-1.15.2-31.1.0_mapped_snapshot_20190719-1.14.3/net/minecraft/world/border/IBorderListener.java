package net.minecraft.world.border;

public interface IBorderListener {
   void onSizeChanged(WorldBorder var1, double var2);

   void onTransitionStarted(WorldBorder var1, double var2, double var4, long var6);

   void onCenterChanged(WorldBorder var1, double var2, double var4);

   void onWarningTimeChanged(WorldBorder var1, int var2);

   void onWarningDistanceChanged(WorldBorder var1, int var2);

   void onDamageAmountChanged(WorldBorder var1, double var2);

   void onDamageBufferChanged(WorldBorder var1, double var2);

   public static class Impl implements IBorderListener {
      private final WorldBorder field_219590_a;

      public Impl(WorldBorder p_i50549_1_) {
         this.field_219590_a = p_i50549_1_;
      }

      public void onSizeChanged(WorldBorder p_177694_1_, double p_177694_2_) {
         this.field_219590_a.setTransition(p_177694_2_);
      }

      public void onTransitionStarted(WorldBorder p_177692_1_, double p_177692_2_, double p_177692_4_, long p_177692_6_) {
         this.field_219590_a.setTransition(p_177692_2_, p_177692_4_, p_177692_6_);
      }

      public void onCenterChanged(WorldBorder p_177693_1_, double p_177693_2_, double p_177693_4_) {
         this.field_219590_a.setCenter(p_177693_2_, p_177693_4_);
      }

      public void onWarningTimeChanged(WorldBorder p_177691_1_, int p_177691_2_) {
         this.field_219590_a.setWarningTime(p_177691_2_);
      }

      public void onWarningDistanceChanged(WorldBorder p_177690_1_, int p_177690_2_) {
         this.field_219590_a.setWarningDistance(p_177690_2_);
      }

      public void onDamageAmountChanged(WorldBorder p_177696_1_, double p_177696_2_) {
         this.field_219590_a.setDamagePerBlock(p_177696_2_);
      }

      public void onDamageBufferChanged(WorldBorder p_177695_1_, double p_177695_2_) {
         this.field_219590_a.setDamageBuffer(p_177695_2_);
      }
   }
}
