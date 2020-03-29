package net.minecraft.client.gui.overlay;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerTabOverlayGui extends AbstractGui {
   private static final Ordering<NetworkPlayerInfo> ENTRY_ORDERING = Ordering.from(new PlayerTabOverlayGui.PlayerComparator());
   private final Minecraft mc;
   private final IngameGui guiIngame;
   private ITextComponent footer;
   private ITextComponent header;
   private long lastTimeOpened;
   private boolean visible;

   public PlayerTabOverlayGui(Minecraft p_i45529_1_, IngameGui p_i45529_2_) {
      this.mc = p_i45529_1_;
      this.guiIngame = p_i45529_2_;
   }

   public ITextComponent getDisplayName(NetworkPlayerInfo p_200262_1_) {
      return p_200262_1_.getDisplayName() != null ? p_200262_1_.getDisplayName() : ScorePlayerTeam.formatMemberName(p_200262_1_.getPlayerTeam(), new StringTextComponent(p_200262_1_.getGameProfile().getName()));
   }

   public void setVisible(boolean p_175246_1_) {
      if (p_175246_1_ && !this.visible) {
         this.lastTimeOpened = Util.milliTime();
      }

      this.visible = p_175246_1_;
   }

   public void render(int p_175249_1_, Scoreboard p_175249_2_, @Nullable ScoreObjective p_175249_3_) {
      ClientPlayNetHandler lvt_4_1_ = this.mc.player.connection;
      List<NetworkPlayerInfo> lvt_5_1_ = ENTRY_ORDERING.sortedCopy(lvt_4_1_.getPlayerInfoMap());
      int lvt_6_1_ = 0;
      int lvt_7_1_ = 0;
      Iterator var8 = lvt_5_1_.iterator();

      int lvt_10_2_;
      while(var8.hasNext()) {
         NetworkPlayerInfo lvt_9_1_ = (NetworkPlayerInfo)var8.next();
         lvt_10_2_ = this.mc.fontRenderer.getStringWidth(this.getDisplayName(lvt_9_1_).getFormattedText());
         lvt_6_1_ = Math.max(lvt_6_1_, lvt_10_2_);
         if (p_175249_3_ != null && p_175249_3_.getRenderType() != ScoreCriteria.RenderType.HEARTS) {
            lvt_10_2_ = this.mc.fontRenderer.getStringWidth(" " + p_175249_2_.getOrCreateScore(lvt_9_1_.getGameProfile().getName(), p_175249_3_).getScorePoints());
            lvt_7_1_ = Math.max(lvt_7_1_, lvt_10_2_);
         }
      }

      lvt_5_1_ = lvt_5_1_.subList(0, Math.min(lvt_5_1_.size(), 80));
      int lvt_8_1_ = lvt_5_1_.size();
      int lvt_9_2_ = lvt_8_1_;

      for(lvt_10_2_ = 1; lvt_9_2_ > 20; lvt_9_2_ = (lvt_8_1_ + lvt_10_2_ - 1) / lvt_10_2_) {
         ++lvt_10_2_;
      }

      boolean lvt_11_1_ = this.mc.isIntegratedServerRunning() || this.mc.getConnection().getNetworkManager().isEncrypted();
      int lvt_12_3_;
      if (p_175249_3_ != null) {
         if (p_175249_3_.getRenderType() == ScoreCriteria.RenderType.HEARTS) {
            lvt_12_3_ = 90;
         } else {
            lvt_12_3_ = lvt_7_1_;
         }
      } else {
         lvt_12_3_ = 0;
      }

      int lvt_13_1_ = Math.min(lvt_10_2_ * ((lvt_11_1_ ? 9 : 0) + lvt_6_1_ + lvt_12_3_ + 13), p_175249_1_ - 50) / lvt_10_2_;
      int lvt_14_1_ = p_175249_1_ / 2 - (lvt_13_1_ * lvt_10_2_ + (lvt_10_2_ - 1) * 5) / 2;
      int lvt_15_1_ = 10;
      int lvt_16_1_ = lvt_13_1_ * lvt_10_2_ + (lvt_10_2_ - 1) * 5;
      List<String> lvt_17_1_ = null;
      if (this.header != null) {
         lvt_17_1_ = this.mc.fontRenderer.listFormattedStringToWidth(this.header.getFormattedText(), p_175249_1_ - 50);

         String lvt_19_1_;
         for(Iterator var18 = lvt_17_1_.iterator(); var18.hasNext(); lvt_16_1_ = Math.max(lvt_16_1_, this.mc.fontRenderer.getStringWidth(lvt_19_1_))) {
            lvt_19_1_ = (String)var18.next();
         }
      }

      List<String> lvt_18_1_ = null;
      String lvt_20_2_;
      Iterator var36;
      if (this.footer != null) {
         lvt_18_1_ = this.mc.fontRenderer.listFormattedStringToWidth(this.footer.getFormattedText(), p_175249_1_ - 50);

         for(var36 = lvt_18_1_.iterator(); var36.hasNext(); lvt_16_1_ = Math.max(lvt_16_1_, this.mc.fontRenderer.getStringWidth(lvt_20_2_))) {
            lvt_20_2_ = (String)var36.next();
         }
      }

      int var10000;
      int var10001;
      int var10002;
      int var10004;
      int lvt_21_2_;
      if (lvt_17_1_ != null) {
         var10000 = p_175249_1_ / 2 - lvt_16_1_ / 2 - 1;
         var10001 = lvt_15_1_ - 1;
         var10002 = p_175249_1_ / 2 + lvt_16_1_ / 2 + 1;
         var10004 = lvt_17_1_.size();
         this.mc.fontRenderer.getClass();
         fill(var10000, var10001, var10002, lvt_15_1_ + var10004 * 9, Integer.MIN_VALUE);

         for(var36 = lvt_17_1_.iterator(); var36.hasNext(); lvt_15_1_ += 9) {
            lvt_20_2_ = (String)var36.next();
            lvt_21_2_ = this.mc.fontRenderer.getStringWidth(lvt_20_2_);
            this.mc.fontRenderer.drawStringWithShadow(lvt_20_2_, (float)(p_175249_1_ / 2 - lvt_21_2_ / 2), (float)lvt_15_1_, -1);
            this.mc.fontRenderer.getClass();
         }

         ++lvt_15_1_;
      }

      fill(p_175249_1_ / 2 - lvt_16_1_ / 2 - 1, lvt_15_1_ - 1, p_175249_1_ / 2 + lvt_16_1_ / 2 + 1, lvt_15_1_ + lvt_9_2_ * 9, Integer.MIN_VALUE);
      int lvt_19_2_ = this.mc.gameSettings.func_216839_a(553648127);

      int lvt_22_2_;
      for(int lvt_20_3_ = 0; lvt_20_3_ < lvt_8_1_; ++lvt_20_3_) {
         lvt_21_2_ = lvt_20_3_ / lvt_9_2_;
         lvt_22_2_ = lvt_20_3_ % lvt_9_2_;
         int lvt_23_1_ = lvt_14_1_ + lvt_21_2_ * lvt_13_1_ + lvt_21_2_ * 5;
         int lvt_24_1_ = lvt_15_1_ + lvt_22_2_ * 9;
         fill(lvt_23_1_, lvt_24_1_, lvt_23_1_ + lvt_13_1_, lvt_24_1_ + 8, lvt_19_2_);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableAlphaTest();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         if (lvt_20_3_ < lvt_5_1_.size()) {
            NetworkPlayerInfo lvt_25_1_ = (NetworkPlayerInfo)lvt_5_1_.get(lvt_20_3_);
            GameProfile lvt_26_1_ = lvt_25_1_.getGameProfile();
            int lvt_29_2_;
            if (lvt_11_1_) {
               PlayerEntity lvt_27_1_ = this.mc.world.getPlayerByUuid(lvt_26_1_.getId());
               boolean lvt_28_1_ = lvt_27_1_ != null && lvt_27_1_.isWearing(PlayerModelPart.CAPE) && ("Dinnerbone".equals(lvt_26_1_.getName()) || "Grumm".equals(lvt_26_1_.getName()));
               this.mc.getTextureManager().bindTexture(lvt_25_1_.getLocationSkin());
               lvt_29_2_ = 8 + (lvt_28_1_ ? 8 : 0);
               int lvt_30_1_ = 8 * (lvt_28_1_ ? -1 : 1);
               AbstractGui.blit(lvt_23_1_, lvt_24_1_, 8, 8, 8.0F, (float)lvt_29_2_, 8, lvt_30_1_, 64, 64);
               if (lvt_27_1_ != null && lvt_27_1_.isWearing(PlayerModelPart.HAT)) {
                  int lvt_31_1_ = 8 + (lvt_28_1_ ? 8 : 0);
                  int lvt_32_1_ = 8 * (lvt_28_1_ ? -1 : 1);
                  AbstractGui.blit(lvt_23_1_, lvt_24_1_, 8, 8, 40.0F, (float)lvt_31_1_, 8, lvt_32_1_, 64, 64);
               }

               lvt_23_1_ += 9;
            }

            String lvt_27_2_ = this.getDisplayName(lvt_25_1_).getFormattedText();
            if (lvt_25_1_.getGameType() == GameType.SPECTATOR) {
               this.mc.fontRenderer.drawStringWithShadow(TextFormatting.ITALIC + lvt_27_2_, (float)lvt_23_1_, (float)lvt_24_1_, -1862270977);
            } else {
               this.mc.fontRenderer.drawStringWithShadow(lvt_27_2_, (float)lvt_23_1_, (float)lvt_24_1_, -1);
            }

            if (p_175249_3_ != null && lvt_25_1_.getGameType() != GameType.SPECTATOR) {
               int lvt_28_2_ = lvt_23_1_ + lvt_6_1_ + 1;
               lvt_29_2_ = lvt_28_2_ + lvt_12_3_;
               if (lvt_29_2_ - lvt_28_2_ > 5) {
                  this.drawScoreboardValues(p_175249_3_, lvt_24_1_, lvt_26_1_.getName(), lvt_28_2_, lvt_29_2_, lvt_25_1_);
               }
            }

            this.drawPing(lvt_13_1_, lvt_23_1_ - (lvt_11_1_ ? 9 : 0), lvt_24_1_, lvt_25_1_);
         }
      }

      if (lvt_18_1_ != null) {
         lvt_15_1_ += lvt_9_2_ * 9 + 1;
         var10000 = p_175249_1_ / 2 - lvt_16_1_ / 2 - 1;
         var10001 = lvt_15_1_ - 1;
         var10002 = p_175249_1_ / 2 + lvt_16_1_ / 2 + 1;
         var10004 = lvt_18_1_.size();
         this.mc.fontRenderer.getClass();
         fill(var10000, var10001, var10002, lvt_15_1_ + var10004 * 9, Integer.MIN_VALUE);

         for(Iterator var39 = lvt_18_1_.iterator(); var39.hasNext(); lvt_15_1_ += 9) {
            String lvt_21_3_ = (String)var39.next();
            lvt_22_2_ = this.mc.fontRenderer.getStringWidth(lvt_21_3_);
            this.mc.fontRenderer.drawStringWithShadow(lvt_21_3_, (float)(p_175249_1_ / 2 - lvt_22_2_ / 2), (float)lvt_15_1_, -1);
            this.mc.fontRenderer.getClass();
         }
      }

   }

   protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo p_175245_4_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
      int lvt_5_1_ = false;
      byte lvt_6_6_;
      if (p_175245_4_.getResponseTime() < 0) {
         lvt_6_6_ = 5;
      } else if (p_175245_4_.getResponseTime() < 150) {
         lvt_6_6_ = 0;
      } else if (p_175245_4_.getResponseTime() < 300) {
         lvt_6_6_ = 1;
      } else if (p_175245_4_.getResponseTime() < 600) {
         lvt_6_6_ = 2;
      } else if (p_175245_4_.getResponseTime() < 1000) {
         lvt_6_6_ = 3;
      } else {
         lvt_6_6_ = 4;
      }

      this.setBlitOffset(this.getBlitOffset() + 100);
      this.blit(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0, 176 + lvt_6_6_ * 8, 10, 8);
      this.setBlitOffset(this.getBlitOffset() - 100);
   }

   private void drawScoreboardValues(ScoreObjective p_175247_1_, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo p_175247_6_) {
      int lvt_7_1_ = p_175247_1_.getScoreboard().getOrCreateScore(p_175247_3_, p_175247_1_).getScorePoints();
      if (p_175247_1_.getRenderType() == ScoreCriteria.RenderType.HEARTS) {
         this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
         long lvt_8_1_ = Util.milliTime();
         if (this.lastTimeOpened == p_175247_6_.getRenderVisibilityId()) {
            if (lvt_7_1_ < p_175247_6_.getLastHealth()) {
               p_175247_6_.setLastHealthTime(lvt_8_1_);
               p_175247_6_.setHealthBlinkTime((long)(this.guiIngame.getTicks() + 20));
            } else if (lvt_7_1_ > p_175247_6_.getLastHealth()) {
               p_175247_6_.setLastHealthTime(lvt_8_1_);
               p_175247_6_.setHealthBlinkTime((long)(this.guiIngame.getTicks() + 10));
            }
         }

         if (lvt_8_1_ - p_175247_6_.getLastHealthTime() > 1000L || this.lastTimeOpened != p_175247_6_.getRenderVisibilityId()) {
            p_175247_6_.setLastHealth(lvt_7_1_);
            p_175247_6_.setDisplayHealth(lvt_7_1_);
            p_175247_6_.setLastHealthTime(lvt_8_1_);
         }

         p_175247_6_.setRenderVisibilityId(this.lastTimeOpened);
         p_175247_6_.setLastHealth(lvt_7_1_);
         int lvt_10_1_ = MathHelper.ceil((float)Math.max(lvt_7_1_, p_175247_6_.getDisplayHealth()) / 2.0F);
         int lvt_11_1_ = Math.max(MathHelper.ceil((float)(lvt_7_1_ / 2)), Math.max(MathHelper.ceil((float)(p_175247_6_.getDisplayHealth() / 2)), 10));
         boolean lvt_12_1_ = p_175247_6_.getHealthBlinkTime() > (long)this.guiIngame.getTicks() && (p_175247_6_.getHealthBlinkTime() - (long)this.guiIngame.getTicks()) / 3L % 2L == 1L;
         if (lvt_10_1_ > 0) {
            int lvt_13_1_ = MathHelper.floor(Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / (float)lvt_11_1_, 9.0F));
            if (lvt_13_1_ > 3) {
               int lvt_14_2_;
               for(lvt_14_2_ = lvt_10_1_; lvt_14_2_ < lvt_11_1_; ++lvt_14_2_) {
                  this.blit(p_175247_4_ + lvt_14_2_ * lvt_13_1_, p_175247_2_, lvt_12_1_ ? 25 : 16, 0, 9, 9);
               }

               for(lvt_14_2_ = 0; lvt_14_2_ < lvt_10_1_; ++lvt_14_2_) {
                  this.blit(p_175247_4_ + lvt_14_2_ * lvt_13_1_, p_175247_2_, lvt_12_1_ ? 25 : 16, 0, 9, 9);
                  if (lvt_12_1_) {
                     if (lvt_14_2_ * 2 + 1 < p_175247_6_.getDisplayHealth()) {
                        this.blit(p_175247_4_ + lvt_14_2_ * lvt_13_1_, p_175247_2_, 70, 0, 9, 9);
                     }

                     if (lvt_14_2_ * 2 + 1 == p_175247_6_.getDisplayHealth()) {
                        this.blit(p_175247_4_ + lvt_14_2_ * lvt_13_1_, p_175247_2_, 79, 0, 9, 9);
                     }
                  }

                  if (lvt_14_2_ * 2 + 1 < lvt_7_1_) {
                     this.blit(p_175247_4_ + lvt_14_2_ * lvt_13_1_, p_175247_2_, lvt_14_2_ >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (lvt_14_2_ * 2 + 1 == lvt_7_1_) {
                     this.blit(p_175247_4_ + lvt_14_2_ * lvt_13_1_, p_175247_2_, lvt_14_2_ >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float lvt_14_3_ = MathHelper.clamp((float)lvt_7_1_ / 20.0F, 0.0F, 1.0F);
               int lvt_15_1_ = (int)((1.0F - lvt_14_3_) * 255.0F) << 16 | (int)(lvt_14_3_ * 255.0F) << 8;
               String lvt_16_1_ = "" + (float)lvt_7_1_ / 2.0F;
               if (p_175247_5_ - this.mc.fontRenderer.getStringWidth(lvt_16_1_ + "hp") >= p_175247_4_) {
                  lvt_16_1_ = lvt_16_1_ + "hp";
               }

               this.mc.fontRenderer.drawStringWithShadow(lvt_16_1_, (float)((p_175247_5_ + p_175247_4_) / 2 - this.mc.fontRenderer.getStringWidth(lvt_16_1_) / 2), (float)p_175247_2_, lvt_15_1_);
            }
         }
      } else {
         String lvt_8_2_ = TextFormatting.YELLOW + "" + lvt_7_1_;
         this.mc.fontRenderer.drawStringWithShadow(lvt_8_2_, (float)(p_175247_5_ - this.mc.fontRenderer.getStringWidth(lvt_8_2_)), (float)p_175247_2_, 16777215);
      }

   }

   public void setFooter(@Nullable ITextComponent p_175248_1_) {
      this.footer = p_175248_1_;
   }

   public void setHeader(@Nullable ITextComponent p_175244_1_) {
      this.header = p_175244_1_;
   }

   public void resetFooterHeader() {
      this.header = null;
      this.footer = null;
   }

   @OnlyIn(Dist.CLIENT)
   static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
      private PlayerComparator() {
      }

      public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
         ScorePlayerTeam lvt_3_1_ = p_compare_1_.getPlayerTeam();
         ScorePlayerTeam lvt_4_1_ = p_compare_2_.getPlayerTeam();
         return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != GameType.SPECTATOR, p_compare_2_.getGameType() != GameType.SPECTATOR).compare(lvt_3_1_ != null ? lvt_3_1_.getName() : "", lvt_4_1_ != null ? lvt_4_1_.getName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName(), String::compareToIgnoreCase).result();
      }

      // $FF: synthetic method
      public int compare(Object p_compare_1_, Object p_compare_2_) {
         return this.compare((NetworkPlayerInfo)p_compare_1_, (NetworkPlayerInfo)p_compare_2_);
      }

      // $FF: synthetic method
      PlayerComparator(Object p_i45528_1_) {
         this();
      }
   }
}
