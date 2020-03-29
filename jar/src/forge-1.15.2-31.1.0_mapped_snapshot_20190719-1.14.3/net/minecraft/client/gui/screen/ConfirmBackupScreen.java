package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmBackupScreen extends Screen {
   private final Screen parentScreen;
   protected final ConfirmBackupScreen.ICallback callback;
   private final ITextComponent message;
   private final boolean field_212994_d;
   private final List<String> wrappedMessage = Lists.newArrayList();
   private final String field_212995_f;
   private final String confirmText;
   private final String skipBackupText;
   private final String cancelText;
   private CheckboxButton field_212996_j;

   public ConfirmBackupScreen(Screen p_i51122_1_, ConfirmBackupScreen.ICallback p_i51122_2_, ITextComponent p_i51122_3_, ITextComponent p_i51122_4_, boolean p_i51122_5_) {
      super(p_i51122_3_);
      this.parentScreen = p_i51122_1_;
      this.callback = p_i51122_2_;
      this.message = p_i51122_4_;
      this.field_212994_d = p_i51122_5_;
      this.field_212995_f = I18n.format("selectWorld.backupEraseCache");
      this.confirmText = I18n.format("selectWorld.backupJoinConfirmButton");
      this.skipBackupText = I18n.format("selectWorld.backupJoinSkipButton");
      this.cancelText = I18n.format("gui.cancel");
   }

   protected void init() {
      super.init();
      this.wrappedMessage.clear();
      this.wrappedMessage.addAll(this.font.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50));
      int var10000 = this.wrappedMessage.size() + 1;
      this.font.getClass();
      int lvt_1_1_ = var10000 * 9;
      this.addButton(new Button(this.width / 2 - 155, 100 + lvt_1_1_, 150, 20, this.confirmText, (p_212993_1_) -> {
         this.callback.proceed(true, this.field_212996_j.func_212942_a());
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, 100 + lvt_1_1_, 150, 20, this.skipBackupText, (p_212992_1_) -> {
         this.callback.proceed(false, this.field_212996_j.func_212942_a());
      }));
      this.addButton(new Button(this.width / 2 - 155 + 80, 124 + lvt_1_1_, 150, 20, this.cancelText, (p_212991_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
      this.field_212996_j = new CheckboxButton(this.width / 2 - 155 + 80, 76 + lvt_1_1_, 150, 20, this.field_212995_f, false);
      if (this.field_212994_d) {
         this.addButton(this.field_212996_j);
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 50, 16777215);
      int lvt_4_1_ = 70;

      for(Iterator var5 = this.wrappedMessage.iterator(); var5.hasNext(); lvt_4_1_ += 9) {
         String lvt_6_1_ = (String)var5.next();
         this.drawCenteredString(this.font, lvt_6_1_, this.width / 2, lvt_4_1_, 16777215);
         this.font.getClass();
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.minecraft.displayGuiScreen(this.parentScreen);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface ICallback {
      void proceed(boolean var1, boolean var2);
   }
}
