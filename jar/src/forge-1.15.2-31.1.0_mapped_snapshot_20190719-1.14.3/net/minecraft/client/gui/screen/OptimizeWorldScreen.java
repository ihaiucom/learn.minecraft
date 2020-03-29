package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Iterator;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptimizeWorldScreen extends Screen {
   private static final Object2IntMap<DimensionType> PROGRESS_BAR_COLORS = (Object2IntMap)Util.make(new Object2IntOpenCustomHashMap(Util.identityHashStrategy()), (p_212346_0_) -> {
      p_212346_0_.put(DimensionType.OVERWORLD, -13408734);
      p_212346_0_.put(DimensionType.THE_NETHER, -10075085);
      p_212346_0_.put(DimensionType.THE_END, -8943531);
      p_212346_0_.defaultReturnValue(-2236963);
   });
   private final BooleanConsumer field_214332_b;
   private final WorldOptimizer optimizer;

   public OptimizeWorldScreen(BooleanConsumer p_i51072_1_, String p_i51072_2_, SaveFormat p_i51072_3_, boolean p_i51072_4_) {
      super(new TranslationTextComponent("optimizeWorld.title", new Object[]{p_i51072_3_.getWorldInfo(p_i51072_2_).getWorldName()}));
      this.field_214332_b = p_i51072_1_;
      this.optimizer = new WorldOptimizer(p_i51072_2_, p_i51072_3_, p_i51072_3_.getWorldInfo(p_i51072_2_), p_i51072_4_);
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 150, 200, 20, I18n.format("gui.cancel"), (p_214331_1_) -> {
         this.optimizer.cancel();
         this.field_214332_b.accept(false);
      }));
   }

   public void tick() {
      if (this.optimizer.isFinished()) {
         this.field_214332_b.accept(true);
      }

   }

   public void removed() {
      this.optimizer.cancel();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
      int lvt_4_1_ = this.width / 2 - 150;
      int lvt_5_1_ = this.width / 2 + 150;
      int lvt_6_1_ = this.height / 4 + 100;
      int lvt_7_1_ = lvt_6_1_ + 10;
      FontRenderer var10001 = this.font;
      String var10002 = this.optimizer.getStatusText().getFormattedText();
      int var10003 = this.width / 2;
      this.font.getClass();
      this.drawCenteredString(var10001, var10002, var10003, lvt_6_1_ - 9 - 2, 10526880);
      if (this.optimizer.getTotalChunks() > 0) {
         fill(lvt_4_1_ - 1, lvt_6_1_ - 1, lvt_5_1_ + 1, lvt_7_1_ + 1, -16777216);
         this.drawString(this.font, I18n.format("optimizeWorld.info.converted", this.optimizer.getConverted()), lvt_4_1_, 40, 10526880);
         var10001 = this.font;
         var10002 = I18n.format("optimizeWorld.info.skipped", this.optimizer.getSkipped());
         this.font.getClass();
         this.drawString(var10001, var10002, lvt_4_1_, 40 + 9 + 3, 10526880);
         var10001 = this.font;
         var10002 = I18n.format("optimizeWorld.info.total", this.optimizer.getTotalChunks());
         this.font.getClass();
         this.drawString(var10001, var10002, lvt_4_1_, 40 + (9 + 3) * 2, 10526880);
         int lvt_8_1_ = 0;

         int lvt_11_1_;
         for(Iterator var9 = DimensionType.getAll().iterator(); var9.hasNext(); lvt_8_1_ += lvt_11_1_) {
            DimensionType lvt_10_1_ = (DimensionType)var9.next();
            lvt_11_1_ = MathHelper.floor(this.optimizer.getProgress(lvt_10_1_) * (float)(lvt_5_1_ - lvt_4_1_));
            fill(lvt_4_1_ + lvt_8_1_, lvt_6_1_, lvt_4_1_ + lvt_8_1_ + lvt_11_1_, lvt_7_1_, PROGRESS_BAR_COLORS.getInt(lvt_10_1_));
         }

         int lvt_9_1_ = this.optimizer.getConverted() + this.optimizer.getSkipped();
         var10001 = this.font;
         var10002 = lvt_9_1_ + " / " + this.optimizer.getTotalChunks();
         var10003 = this.width / 2;
         this.font.getClass();
         this.drawCenteredString(var10001, var10002, var10003, lvt_6_1_ + 2 * 9 + 2, 10526880);
         var10001 = this.font;
         var10002 = MathHelper.floor(this.optimizer.getTotalProgress() * 100.0F) + "%";
         var10003 = this.width / 2;
         int var10004 = lvt_6_1_ + (lvt_7_1_ - lvt_6_1_) / 2;
         this.font.getClass();
         this.drawCenteredString(var10001, var10002, var10003, var10004 - 9 / 2, 10526880);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
