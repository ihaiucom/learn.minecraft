package net.minecraft.world;

import java.util.UUID;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public abstract class BossInfo {
   private final UUID uniqueId;
   protected ITextComponent name;
   protected float percent;
   protected BossInfo.Color color;
   protected BossInfo.Overlay overlay;
   protected boolean darkenSky;
   protected boolean playEndBossMusic;
   protected boolean createFog;

   public BossInfo(UUID p_i46824_1_, ITextComponent p_i46824_2_, BossInfo.Color p_i46824_3_, BossInfo.Overlay p_i46824_4_) {
      this.uniqueId = p_i46824_1_;
      this.name = p_i46824_2_;
      this.color = p_i46824_3_;
      this.overlay = p_i46824_4_;
      this.percent = 1.0F;
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public ITextComponent getName() {
      return this.name;
   }

   public void setName(ITextComponent p_186739_1_) {
      this.name = p_186739_1_;
   }

   public float getPercent() {
      return this.percent;
   }

   public void setPercent(float p_186735_1_) {
      this.percent = p_186735_1_;
   }

   public BossInfo.Color getColor() {
      return this.color;
   }

   public void setColor(BossInfo.Color p_186745_1_) {
      this.color = p_186745_1_;
   }

   public BossInfo.Overlay getOverlay() {
      return this.overlay;
   }

   public void setOverlay(BossInfo.Overlay p_186746_1_) {
      this.overlay = p_186746_1_;
   }

   public boolean shouldDarkenSky() {
      return this.darkenSky;
   }

   public BossInfo setDarkenSky(boolean p_186741_1_) {
      this.darkenSky = p_186741_1_;
      return this;
   }

   public boolean shouldPlayEndBossMusic() {
      return this.playEndBossMusic;
   }

   public BossInfo setPlayEndBossMusic(boolean p_186742_1_) {
      this.playEndBossMusic = p_186742_1_;
      return this;
   }

   public BossInfo setCreateFog(boolean p_186743_1_) {
      this.createFog = p_186743_1_;
      return this;
   }

   public boolean shouldCreateFog() {
      return this.createFog;
   }

   public static enum Overlay {
      PROGRESS("progress"),
      NOTCHED_6("notched_6"),
      NOTCHED_10("notched_10"),
      NOTCHED_12("notched_12"),
      NOTCHED_20("notched_20");

      private final String name;

      private Overlay(String p_i48621_3_) {
         this.name = p_i48621_3_;
      }

      public String getName() {
         return this.name;
      }

      public static BossInfo.Overlay byName(String p_201485_0_) {
         BossInfo.Overlay[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BossInfo.Overlay lvt_4_1_ = var1[var3];
            if (lvt_4_1_.name.equals(p_201485_0_)) {
               return lvt_4_1_;
            }
         }

         return PROGRESS;
      }
   }

   public static enum Color {
      PINK("pink", TextFormatting.RED),
      BLUE("blue", TextFormatting.BLUE),
      RED("red", TextFormatting.DARK_RED),
      GREEN("green", TextFormatting.GREEN),
      YELLOW("yellow", TextFormatting.YELLOW),
      PURPLE("purple", TextFormatting.DARK_BLUE),
      WHITE("white", TextFormatting.WHITE);

      private final String name;
      private final TextFormatting formatting;

      private Color(String p_i48622_3_, TextFormatting p_i48622_4_) {
         this.name = p_i48622_3_;
         this.formatting = p_i48622_4_;
      }

      public TextFormatting getFormatting() {
         return this.formatting;
      }

      public String getName() {
         return this.name;
      }

      public static BossInfo.Color byName(String p_201481_0_) {
         BossInfo.Color[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BossInfo.Color lvt_4_1_ = var1[var3];
            if (lvt_4_1_.name.equals(p_201481_0_)) {
               return lvt_4_1_;
            }
         }

         return WHITE;
      }
   }
}
