package net.minecraft.client.gui.screen;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedScreen extends Screen {
   private final ITextComponent message;
   private List<String> multilineMessage;
   private final Screen field_146307_h;
   private int textHeight;

   public DisconnectedScreen(Screen p_i45020_1_, String p_i45020_2_, ITextComponent p_i45020_3_) {
      super(new TranslationTextComponent(p_i45020_2_, new Object[0]));
      this.field_146307_h = p_i45020_1_;
      this.message = p_i45020_3_;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.multilineMessage = this.font.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
      int var10001 = this.multilineMessage.size();
      this.font.getClass();
      this.textHeight = var10001 * 9;
      int var10003 = this.width / 2 - 100;
      int var10004 = this.height / 2 + this.textHeight / 2;
      this.font.getClass();
      this.addButton(new Button(var10003, Math.min(var10004 + 9, this.height - 30), 200, 20, I18n.format("gui.toMenu"), (p_213033_1_) -> {
         this.minecraft.displayGuiScreen(this.field_146307_h);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      FontRenderer var10001 = this.font;
      String var10002 = this.title.getFormattedText();
      int var10003 = this.width / 2;
      int var10004 = this.height / 2 - this.textHeight / 2;
      this.font.getClass();
      this.drawCenteredString(var10001, var10002, var10003, var10004 - 9 * 2, 11184810);
      int lvt_4_1_ = this.height / 2 - this.textHeight / 2;
      if (this.multilineMessage != null) {
         for(Iterator var5 = this.multilineMessage.iterator(); var5.hasNext(); lvt_4_1_ += 9) {
            String lvt_6_1_ = (String)var5.next();
            this.drawCenteredString(this.font, lvt_6_1_, this.width / 2, lvt_4_1_, 16777215);
            this.font.getClass();
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
