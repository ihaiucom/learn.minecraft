package net.minecraft.client.gui.widget.button;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Button extends AbstractButton {
   protected final Button.IPressable onPress;

   public Button(int p_i51141_1_, int p_i51141_2_, int p_i51141_3_, int p_i51141_4_, String p_i51141_5_, Button.IPressable p_i51141_6_) {
      super(p_i51141_1_, p_i51141_2_, p_i51141_3_, p_i51141_4_, p_i51141_5_);
      this.onPress = p_i51141_6_;
   }

   public void onPress() {
      this.onPress.onPress(this);
   }

   @OnlyIn(Dist.CLIENT)
   public interface IPressable {
      void onPress(Button var1);
   }
}
