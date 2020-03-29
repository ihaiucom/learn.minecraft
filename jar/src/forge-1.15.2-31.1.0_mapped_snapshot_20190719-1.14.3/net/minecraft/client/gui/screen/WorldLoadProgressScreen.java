package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldLoadProgressScreen extends Screen {
   private final TrackingChunkStatusListener field_213040_a;
   private long field_213041_b = -1L;
   private static final Object2IntMap<ChunkStatus> COLORS = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (p_213039_0_) -> {
      p_213039_0_.defaultReturnValue(0);
      p_213039_0_.put(ChunkStatus.EMPTY, 5526612);
      p_213039_0_.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
      p_213039_0_.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
      p_213039_0_.put(ChunkStatus.BIOMES, 8434258);
      p_213039_0_.put(ChunkStatus.NOISE, 13750737);
      p_213039_0_.put(ChunkStatus.SURFACE, 7497737);
      p_213039_0_.put(ChunkStatus.CARVERS, 7169628);
      p_213039_0_.put(ChunkStatus.LIQUID_CARVERS, 3159410);
      p_213039_0_.put(ChunkStatus.FEATURES, 2213376);
      p_213039_0_.put(ChunkStatus.LIGHT, 13421772);
      p_213039_0_.put(ChunkStatus.SPAWN, 15884384);
      p_213039_0_.put(ChunkStatus.HEIGHTMAPS, 15658734);
      p_213039_0_.put(ChunkStatus.FULL, 16777215);
   });

   public WorldLoadProgressScreen(TrackingChunkStatusListener p_i51113_1_) {
      super(NarratorChatListener.field_216868_a);
      this.field_213040_a = p_i51113_1_;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void removed() {
      NarratorChatListener.INSTANCE.func_216864_a(I18n.format("narrator.loading.done"));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      String lvt_4_1_ = MathHelper.clamp(this.field_213040_a.getPercentDone(), 0, 100) + "%";
      long lvt_5_1_ = Util.milliTime();
      if (lvt_5_1_ - this.field_213041_b > 2000L) {
         this.field_213041_b = lvt_5_1_;
         NarratorChatListener.INSTANCE.func_216864_a((new TranslationTextComponent("narrator.loading", new Object[]{lvt_4_1_})).getString());
      }

      int lvt_7_1_ = this.width / 2;
      int lvt_8_1_ = this.height / 2;
      int lvt_9_1_ = true;
      func_213038_a(this.field_213040_a, lvt_7_1_, lvt_8_1_ + 30, 2, 0);
      FontRenderer var10001 = this.font;
      this.font.getClass();
      this.drawCenteredString(var10001, lvt_4_1_, lvt_7_1_, lvt_8_1_ - 9 / 2 - 30, 16777215);
   }

   public static void func_213038_a(TrackingChunkStatusListener p_213038_0_, int p_213038_1_, int p_213038_2_, int p_213038_3_, int p_213038_4_) {
      int lvt_5_1_ = p_213038_3_ + p_213038_4_;
      int lvt_6_1_ = p_213038_0_.getDiameter();
      int lvt_7_1_ = lvt_6_1_ * lvt_5_1_ - p_213038_4_;
      int lvt_8_1_ = p_213038_0_.func_219523_d();
      int lvt_9_1_ = lvt_8_1_ * lvt_5_1_ - p_213038_4_;
      int lvt_10_1_ = p_213038_1_ - lvt_9_1_ / 2;
      int lvt_11_1_ = p_213038_2_ - lvt_9_1_ / 2;
      int lvt_12_1_ = lvt_7_1_ / 2 + 1;
      int lvt_13_1_ = -16772609;
      if (p_213038_4_ != 0) {
         fill(p_213038_1_ - lvt_12_1_, p_213038_2_ - lvt_12_1_, p_213038_1_ - lvt_12_1_ + 1, p_213038_2_ + lvt_12_1_, -16772609);
         fill(p_213038_1_ + lvt_12_1_ - 1, p_213038_2_ - lvt_12_1_, p_213038_1_ + lvt_12_1_, p_213038_2_ + lvt_12_1_, -16772609);
         fill(p_213038_1_ - lvt_12_1_, p_213038_2_ - lvt_12_1_, p_213038_1_ + lvt_12_1_, p_213038_2_ - lvt_12_1_ + 1, -16772609);
         fill(p_213038_1_ - lvt_12_1_, p_213038_2_ + lvt_12_1_ - 1, p_213038_1_ + lvt_12_1_, p_213038_2_ + lvt_12_1_, -16772609);
      }

      for(int lvt_14_1_ = 0; lvt_14_1_ < lvt_8_1_; ++lvt_14_1_) {
         for(int lvt_15_1_ = 0; lvt_15_1_ < lvt_8_1_; ++lvt_15_1_) {
            ChunkStatus lvt_16_1_ = p_213038_0_.func_219525_a(lvt_14_1_, lvt_15_1_);
            int lvt_17_1_ = lvt_10_1_ + lvt_14_1_ * lvt_5_1_;
            int lvt_18_1_ = lvt_11_1_ + lvt_15_1_ * lvt_5_1_;
            fill(lvt_17_1_, lvt_18_1_, lvt_17_1_ + p_213038_3_, lvt_18_1_ + p_213038_3_, COLORS.getInt(lvt_16_1_) | -16777216);
         }
      }

   }
}
