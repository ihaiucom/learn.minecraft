package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmScreen extends Screen {
   private final ITextComponent messageLine2;
   private final List<String> listLines;
   protected String confirmButtonText;
   protected String cancelButtonText;
   private int ticksUntilEnable;
   protected final BooleanConsumer field_213003_c;

   public ConfirmScreen(BooleanConsumer p_i51119_1_, ITextComponent p_i51119_2_, ITextComponent p_i51119_3_) {
      this(p_i51119_1_, p_i51119_2_, p_i51119_3_, I18n.format("gui.yes"), I18n.format("gui.no"));
   }

   public ConfirmScreen(BooleanConsumer p_i51120_1_, ITextComponent p_i51120_2_, ITextComponent p_i51120_3_, String p_i51120_4_, String p_i51120_5_) {
      super(p_i51120_2_);
      this.listLines = Lists.newArrayList();
      this.field_213003_c = p_i51120_1_;
      this.messageLine2 = p_i51120_3_;
      this.confirmButtonText = p_i51120_4_;
      this.cancelButtonText = p_i51120_5_;
   }

   public String getNarrationMessage() {
      return super.getNarrationMessage() + ". " + this.messageLine2.getString();
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.confirmButtonText, (p_213002_1_) -> {
         this.field_213003_c.accept(true);
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.cancelButtonText, (p_213001_1_) -> {
         this.field_213003_c.accept(false);
      }));
      this.listLines.clear();
      this.listLines.addAll(this.font.listFormattedStringToWidth(this.messageLine2.getFormattedText(), this.width - 50));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 70, 16777215);
      int lvt_4_1_ = 90;

      for(Iterator var5 = this.listLines.iterator(); var5.hasNext(); lvt_4_1_ += 9) {
         String lvt_6_1_ = (String)var5.next();
         this.drawCenteredString(this.font, lvt_6_1_, this.width / 2, lvt_4_1_, 16777215);
         this.font.getClass();
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void setButtonDelay(int p_146350_1_) {
      this.ticksUntilEnable = p_146350_1_;

      Widget lvt_3_1_;
      for(Iterator var2 = this.buttons.iterator(); var2.hasNext(); lvt_3_1_.active = false) {
         lvt_3_1_ = (Widget)var2.next();
      }

   }

   public void tick() {
      super.tick();
      Widget lvt_2_1_;
      if (--this.ticksUntilEnable == 0) {
         for(Iterator var1 = this.buttons.iterator(); var1.hasNext(); lvt_2_1_.active = true) {
            lvt_2_1_ = (Widget)var1.next();
         }
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.field_213003_c.accept(false);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }
}
