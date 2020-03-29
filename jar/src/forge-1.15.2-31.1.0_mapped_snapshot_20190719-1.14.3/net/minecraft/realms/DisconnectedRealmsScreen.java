package net.minecraft.realms;

import java.util.Iterator;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedRealmsScreen extends RealmsScreen {
   private final String title;
   private final ITextComponent reason;
   private List<String> lines;
   private final RealmsScreen parent;
   private int textHeight;

   public DisconnectedRealmsScreen(RealmsScreen p_i45742_1_, String p_i45742_2_, ITextComponent p_i45742_3_) {
      this.parent = p_i45742_1_;
      this.title = getLocalizedString(p_i45742_2_);
      this.reason = p_i45742_3_;
   }

   public void init() {
      Realms.setConnectedToRealms(false);
      Realms.clearResourcePack();
      Realms.narrateNow(this.title + ": " + this.reason.getString());
      this.lines = this.fontSplit(this.reason.getFormattedText(), this.width() - 50);
      this.textHeight = this.lines.size() * this.fontLineHeight();
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() / 2 + this.textHeight / 2 + this.fontLineHeight(), getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(DisconnectedRealmsScreen.this.parent);
         }
      });
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.parent);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - this.textHeight / 2 - this.fontLineHeight() * 2, 11184810);
      int lvt_4_1_ = this.height() / 2 - this.textHeight / 2;
      if (this.lines != null) {
         for(Iterator var5 = this.lines.iterator(); var5.hasNext(); lvt_4_1_ += this.fontLineHeight()) {
            String lvt_6_1_ = (String)var5.next();
            this.drawCenteredString(lvt_6_1_, this.width() / 2, lvt_4_1_, 16777215);
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
