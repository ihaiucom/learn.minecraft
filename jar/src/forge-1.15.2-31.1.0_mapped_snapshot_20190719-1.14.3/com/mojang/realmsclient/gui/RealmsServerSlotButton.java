package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsButtonProxy;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsServerSlotButton extends RealmsButton {
   private final Supplier<RealmsServer> field_223773_a;
   private final Consumer<String> field_223774_b;
   private final RealmsServerSlotButton.IHandler field_223775_c;
   private final int field_223776_d;
   private int field_223777_e;
   @Nullable
   private RealmsServerSlotButton.ServerData field_223778_f;

   public RealmsServerSlotButton(int p_i51780_1_, int p_i51780_2_, int p_i51780_3_, int p_i51780_4_, Supplier<RealmsServer> p_i51780_5_, Consumer<String> p_i51780_6_, int p_i51780_7_, int p_i51780_8_, RealmsServerSlotButton.IHandler p_i51780_9_) {
      super(p_i51780_7_, p_i51780_1_, p_i51780_2_, p_i51780_3_, p_i51780_4_, "");
      this.field_223773_a = p_i51780_5_;
      this.field_223776_d = p_i51780_8_;
      this.field_223774_b = p_i51780_6_;
      this.field_223775_c = p_i51780_9_;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void tick() {
      ++this.field_223777_e;
      RealmsServer lvt_1_1_ = (RealmsServer)this.field_223773_a.get();
      if (lvt_1_1_ != null) {
         RealmsWorldOptions lvt_4_1_ = (RealmsWorldOptions)lvt_1_1_.slots.get(this.field_223776_d);
         boolean lvt_9_1_ = this.field_223776_d == 4;
         boolean lvt_2_2_;
         String lvt_3_2_;
         long lvt_5_2_;
         String lvt_7_2_;
         boolean lvt_8_2_;
         if (lvt_9_1_) {
            lvt_2_2_ = lvt_1_1_.worldType.equals(RealmsServer.ServerType.MINIGAME);
            lvt_3_2_ = "Minigame";
            lvt_5_2_ = (long)lvt_1_1_.minigameId;
            lvt_7_2_ = lvt_1_1_.minigameImage;
            lvt_8_2_ = lvt_1_1_.minigameId == -1;
         } else {
            lvt_2_2_ = lvt_1_1_.activeSlot == this.field_223776_d && !lvt_1_1_.worldType.equals(RealmsServer.ServerType.MINIGAME);
            lvt_3_2_ = lvt_4_1_.getSlotName(this.field_223776_d);
            lvt_5_2_ = lvt_4_1_.templateId;
            lvt_7_2_ = lvt_4_1_.templateImage;
            lvt_8_2_ = lvt_4_1_.empty;
         }

         String lvt_11_1_ = null;
         RealmsServerSlotButton.Action lvt_10_4_;
         if (lvt_2_2_) {
            boolean lvt_12_1_ = lvt_1_1_.state == RealmsServer.Status.OPEN || lvt_1_1_.state == RealmsServer.Status.CLOSED;
            if (!lvt_1_1_.expired && lvt_12_1_) {
               lvt_10_4_ = RealmsServerSlotButton.Action.JOIN;
               lvt_11_1_ = Realms.getLocalizedString("mco.configure.world.slot.tooltip.active");
            } else {
               lvt_10_4_ = RealmsServerSlotButton.Action.NOTHING;
            }
         } else if (lvt_9_1_) {
            if (lvt_1_1_.expired) {
               lvt_10_4_ = RealmsServerSlotButton.Action.NOTHING;
            } else {
               lvt_10_4_ = RealmsServerSlotButton.Action.SWITCH_SLOT;
               lvt_11_1_ = Realms.getLocalizedString("mco.configure.world.slot.tooltip.minigame");
            }
         } else {
            lvt_10_4_ = RealmsServerSlotButton.Action.SWITCH_SLOT;
            lvt_11_1_ = Realms.getLocalizedString("mco.configure.world.slot.tooltip");
         }

         this.field_223778_f = new RealmsServerSlotButton.ServerData(lvt_2_2_, lvt_3_2_, lvt_5_2_, lvt_7_2_, lvt_8_2_, lvt_9_1_, lvt_10_4_, lvt_11_1_);
         String lvt_12_5_;
         if (lvt_10_4_ == RealmsServerSlotButton.Action.NOTHING) {
            lvt_12_5_ = lvt_3_2_;
         } else if (lvt_9_1_) {
            if (lvt_8_2_) {
               lvt_12_5_ = lvt_11_1_;
            } else {
               lvt_12_5_ = lvt_11_1_ + " " + lvt_3_2_ + " " + lvt_1_1_.minigameName;
            }
         } else {
            lvt_12_5_ = lvt_11_1_ + " " + lvt_3_2_;
         }

         this.setMessage(lvt_12_5_);
      }
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      if (this.field_223778_f != null) {
         RealmsButtonProxy lvt_4_1_ = this.getProxy();
         this.func_223772_a(lvt_4_1_.x, lvt_4_1_.y, p_renderButton_1_, p_renderButton_2_, this.field_223778_f.field_225110_a, this.field_223778_f.field_225111_b, this.field_223776_d, this.field_223778_f.field_225112_c, this.field_223778_f.field_225113_d, this.field_223778_f.field_225114_e, this.field_223778_f.field_225115_f, this.field_223778_f.field_225116_g, this.field_223778_f.field_225117_h);
      }
   }

   private void func_223772_a(int p_223772_1_, int p_223772_2_, int p_223772_3_, int p_223772_4_, boolean p_223772_5_, String p_223772_6_, int p_223772_7_, long p_223772_8_, @Nullable String p_223772_10_, boolean p_223772_11_, boolean p_223772_12_, RealmsServerSlotButton.Action p_223772_13_, @Nullable String p_223772_14_) {
      boolean lvt_15_1_ = this.getProxy().isHovered();
      if (this.getProxy().isMouseOver((double)p_223772_3_, (double)p_223772_4_) && p_223772_14_ != null) {
         this.field_223774_b.accept(p_223772_14_);
      }

      if (p_223772_12_) {
         RealmsTextureManager.func_225202_a(String.valueOf(p_223772_8_), p_223772_10_);
      } else if (p_223772_11_) {
         Realms.bind("realms:textures/gui/realms/empty_frame.png");
      } else if (p_223772_10_ != null && p_223772_8_ != -1L) {
         RealmsTextureManager.func_225202_a(String.valueOf(p_223772_8_), p_223772_10_);
      } else if (p_223772_7_ == 1) {
         Realms.bind("textures/gui/title/background/panorama_0.png");
      } else if (p_223772_7_ == 2) {
         Realms.bind("textures/gui/title/background/panorama_2.png");
      } else if (p_223772_7_ == 3) {
         Realms.bind("textures/gui/title/background/panorama_3.png");
      }

      if (p_223772_5_) {
         float lvt_16_1_ = 0.85F + 0.15F * RealmsMth.cos((float)this.field_223777_e * 0.2F);
         RenderSystem.color4f(lvt_16_1_, lvt_16_1_, lvt_16_1_, 1.0F);
      } else {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      RealmsScreen.blit(p_223772_1_ + 3, p_223772_2_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      Realms.bind("realms:textures/gui/realms/slot_frame.png");
      boolean lvt_16_2_ = lvt_15_1_ && p_223772_13_ != RealmsServerSlotButton.Action.NOTHING;
      if (lvt_16_2_) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else if (p_223772_5_) {
         RenderSystem.color4f(0.8F, 0.8F, 0.8F, 1.0F);
      } else {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      RealmsScreen.blit(p_223772_1_, p_223772_2_, 0.0F, 0.0F, 80, 80, 80, 80);
      this.drawCenteredString(p_223772_6_, p_223772_1_ + 40, p_223772_2_ + 66, 16777215);
   }

   public void onPress() {
      if (this.field_223778_f != null) {
         this.field_223775_c.func_224366_a(this.field_223776_d, this.field_223778_f.field_225116_g, this.field_223778_f.field_225115_f, this.field_223778_f.field_225114_e);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerData {
      final boolean field_225110_a;
      final String field_225111_b;
      final long field_225112_c;
      public final String field_225113_d;
      public final boolean field_225114_e;
      final boolean field_225115_f;
      public final RealmsServerSlotButton.Action field_225116_g;
      final String field_225117_h;

      ServerData(boolean p_i51701_1_, String p_i51701_2_, long p_i51701_3_, @Nullable String p_i51701_5_, boolean p_i51701_6_, boolean p_i51701_7_, RealmsServerSlotButton.Action p_i51701_8_, @Nullable String p_i51701_9_) {
         this.field_225110_a = p_i51701_1_;
         this.field_225111_b = p_i51701_2_;
         this.field_225112_c = p_i51701_3_;
         this.field_225113_d = p_i51701_5_;
         this.field_225114_e = p_i51701_6_;
         this.field_225115_f = p_i51701_7_;
         this.field_225116_g = p_i51701_8_;
         this.field_225117_h = p_i51701_9_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Action {
      NOTHING,
      SWITCH_SLOT,
      JOIN;
   }

   @OnlyIn(Dist.CLIENT)
   public interface IHandler {
      void func_224366_a(int var1, RealmsServerSlotButton.Action var2, boolean var3, boolean var4);
   }
}
