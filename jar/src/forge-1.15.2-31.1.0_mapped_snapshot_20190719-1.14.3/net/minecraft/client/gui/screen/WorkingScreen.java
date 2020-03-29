package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorkingScreen extends Screen implements IProgressUpdate {
   private String title = "";
   private String stage = "";
   private int progress;
   private boolean doneWorking;

   public WorkingScreen() {
      super(NarratorChatListener.field_216868_a);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void displaySavingString(ITextComponent p_200210_1_) {
      this.resetProgressAndMessage(p_200210_1_);
   }

   public void resetProgressAndMessage(ITextComponent p_200211_1_) {
      this.title = p_200211_1_.getFormattedText();
      this.displayLoadingString(new TranslationTextComponent("progress.working", new Object[0]));
   }

   public void displayLoadingString(ITextComponent p_200209_1_) {
      this.stage = p_200209_1_.getFormattedText();
      this.setLoadingProgress(0);
   }

   public void setLoadingProgress(int p_73718_1_) {
      this.progress = p_73718_1_;
   }

   public void setDoneWorking() {
      this.doneWorking = true;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.doneWorking) {
         if (!this.minecraft.isConnectedToRealms()) {
            this.minecraft.displayGuiScreen((Screen)null);
         }

      } else {
         this.renderBackground();
         this.drawCenteredString(this.font, this.title, this.width / 2, 70, 16777215);
         if (!Objects.equals(this.stage, "") && this.progress != 0) {
            this.drawCenteredString(this.font, this.stage + " " + this.progress + "%", this.width / 2, 90, 16777215);
         }

         super.render(p_render_1_, p_render_2_, p_render_3_);
      }
   }
}
