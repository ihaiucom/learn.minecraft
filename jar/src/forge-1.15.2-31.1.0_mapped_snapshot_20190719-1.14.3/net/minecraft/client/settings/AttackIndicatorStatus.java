package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum AttackIndicatorStatus {
   OFF(0, "options.off"),
   CROSSHAIR(1, "options.attack.crosshair"),
   HOTBAR(2, "options.attack.hotbar");

   private static final AttackIndicatorStatus[] BY_ID = (AttackIndicatorStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(AttackIndicatorStatus::func_216751_a)).toArray((p_216750_0_) -> {
      return new AttackIndicatorStatus[p_216750_0_];
   });
   private final int id;
   private final String field_216754_f;

   private AttackIndicatorStatus(int p_i51168_3_, String p_i51168_4_) {
      this.id = p_i51168_3_;
      this.field_216754_f = p_i51168_4_;
   }

   public int func_216751_a() {
      return this.id;
   }

   public String func_216748_b() {
      return this.field_216754_f;
   }

   public static AttackIndicatorStatus byId(int p_216749_0_) {
      return BY_ID[MathHelper.normalizeAngle(p_216749_0_, BY_ID.length)];
   }
}
