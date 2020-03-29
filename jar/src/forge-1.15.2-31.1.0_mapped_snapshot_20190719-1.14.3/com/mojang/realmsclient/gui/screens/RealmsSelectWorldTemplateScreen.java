package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
   private static final Logger field_224515_a = LogManager.getLogger();
   private final RealmsScreenWithCallback<WorldTemplate> field_224516_b;
   private RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList field_224517_c;
   private int field_224518_d;
   private String field_224519_e;
   private RealmsButton field_224520_f;
   private RealmsButton field_224521_g;
   private RealmsButton field_224522_h;
   private String field_224523_i;
   private String field_224524_j;
   private final RealmsServer.ServerType field_224525_k;
   private int field_224526_l;
   private String field_224527_m;
   private String field_224528_n;
   private boolean field_224529_o;
   private boolean field_224530_p;
   private List<TextRenderingUtils.Line> field_224531_q;

   public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback<WorldTemplate> p_i51752_1_, RealmsServer.ServerType p_i51752_2_) {
      this(p_i51752_1_, p_i51752_2_, (WorldTemplatePaginatedList)null);
   }

   public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback<WorldTemplate> p_i51753_1_, RealmsServer.ServerType p_i51753_2_, @Nullable WorldTemplatePaginatedList p_i51753_3_) {
      this.field_224518_d = -1;
      this.field_224516_b = p_i51753_1_;
      this.field_224525_k = p_i51753_2_;
      if (p_i51753_3_ == null) {
         this.field_224517_c = new RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList();
         this.func_224497_a(new WorldTemplatePaginatedList(10));
      } else {
         this.field_224517_c = new RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList(Lists.newArrayList(p_i51753_3_.templates));
         this.func_224497_a(p_i51753_3_);
      }

      this.field_224519_e = getLocalizedString("mco.template.title");
   }

   public void func_224483_a(String p_224483_1_) {
      this.field_224519_e = p_224483_1_;
   }

   public void func_224492_b(String p_224492_1_) {
      this.field_224527_m = p_224492_1_;
      this.field_224529_o = true;
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.field_224530_p && this.field_224528_n != null) {
         RealmsUtil.func_225190_c("https://beta.minecraft.net/realms/adventure-maps-in-1-9");
         return true;
      } else {
         return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.field_224517_c = new RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList(this.field_224517_c.func_223879_b());
      this.buttonsAdd(this.field_224521_g = new RealmsButton(2, this.width() / 2 - 206, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.trailer")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen.this.func_224496_i();
         }
      });
      this.buttonsAdd(this.field_224520_f = new RealmsButton(1, this.width() / 2 - 100, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.select")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen.this.func_224500_h();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 6, this.height() - 32, 100, 20, getLocalizedString(this.field_224525_k == RealmsServer.ServerType.MINIGAME ? "gui.cancel" : "gui.back")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen.this.func_224484_g();
         }
      });
      this.field_224522_h = new RealmsButton(3, this.width() / 2 + 112, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.publisher")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen.this.func_224511_j();
         }
      };
      this.buttonsAdd(this.field_224522_h);
      this.field_224520_f.active(false);
      this.field_224521_g.setVisible(false);
      this.field_224522_h.setVisible(false);
      this.addWidget(this.field_224517_c);
      this.focusOn(this.field_224517_c);
      Realms.narrateNow((Iterable)Stream.of(this.field_224519_e, this.field_224527_m).filter(Objects::nonNull).collect(Collectors.toList()));
   }

   private void func_224514_b() {
      this.field_224522_h.setVisible(this.func_224510_d());
      this.field_224521_g.setVisible(this.func_224512_f());
      this.field_224520_f.active(this.func_224495_c());
   }

   private boolean func_224495_c() {
      return this.field_224518_d != -1;
   }

   private boolean func_224510_d() {
      return this.field_224518_d != -1 && !this.func_224487_e().link.isEmpty();
   }

   private WorldTemplate func_224487_e() {
      return this.field_224517_c.func_223877_a(this.field_224518_d);
   }

   private boolean func_224512_f() {
      return this.field_224518_d != -1 && !this.func_224487_e().trailer.isEmpty();
   }

   public void tick() {
      super.tick();
      --this.field_224526_l;
      if (this.field_224526_l < 0) {
         this.field_224526_l = 0;
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      switch(p_keyPressed_1_) {
      case 256:
         this.func_224484_g();
         return true;
      default:
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_224484_g() {
      this.field_224516_b.func_223627_a_((Object)null);
      Realms.setScreen(this.field_224516_b);
   }

   private void func_224500_h() {
      if (this.field_224518_d >= 0 && this.field_224518_d < this.field_224517_c.getItemCount()) {
         WorldTemplate lvt_1_1_ = this.func_224487_e();
         this.field_224516_b.func_223627_a_(lvt_1_1_);
      }

   }

   private void func_224496_i() {
      if (this.field_224518_d >= 0 && this.field_224518_d < this.field_224517_c.getItemCount()) {
         WorldTemplate lvt_1_1_ = this.func_224487_e();
         if (!"".equals(lvt_1_1_.trailer)) {
            RealmsUtil.func_225190_c(lvt_1_1_.trailer);
         }
      }

   }

   private void func_224511_j() {
      if (this.field_224518_d >= 0 && this.field_224518_d < this.field_224517_c.getItemCount()) {
         WorldTemplate lvt_1_1_ = this.func_224487_e();
         if (!"".equals(lvt_1_1_.link)) {
            RealmsUtil.func_225190_c(lvt_1_1_.link);
         }
      }

   }

   private void func_224497_a(final WorldTemplatePaginatedList p_224497_1_) {
      (new Thread("realms-template-fetcher") {
         public void run() {
            WorldTemplatePaginatedList lvt_1_1_ = p_224497_1_;

            Either lvt_3_1_;
            for(RealmsClient lvt_2_1_ = RealmsClient.func_224911_a(); lvt_1_1_ != null; lvt_1_1_ = (WorldTemplatePaginatedList)Realms.execute(() -> {
               if (lvt_3_1_.right().isPresent()) {
                  RealmsSelectWorldTemplateScreen.field_224515_a.error("Couldn't fetch templates: {}", lvt_3_1_.right().get());
                  if (RealmsSelectWorldTemplateScreen.this.field_224517_c.func_223878_a()) {
                     RealmsSelectWorldTemplateScreen.this.field_224531_q = TextRenderingUtils.func_225224_a(RealmsScreen.getLocalizedString("mco.template.select.failure"));
                  }

                  return null;
               } else {
                  assert lvt_3_1_.left().isPresent();

                  WorldTemplatePaginatedList lvt_2_1_ = (WorldTemplatePaginatedList)lvt_3_1_.left().get();
                  Iterator var3 = lvt_2_1_.templates.iterator();

                  while(var3.hasNext()) {
                     WorldTemplate lvt_4_1_ = (WorldTemplate)var3.next();
                     RealmsSelectWorldTemplateScreen.this.field_224517_c.func_223876_a(lvt_4_1_);
                  }

                  if (lvt_2_1_.templates.isEmpty()) {
                     if (RealmsSelectWorldTemplateScreen.this.field_224517_c.func_223878_a()) {
                        String lvt_3_1_x = RealmsScreen.getLocalizedString("mco.template.select.none", "%link");
                        TextRenderingUtils.LineSegment lvt_4_2_ = TextRenderingUtils.LineSegment.func_225214_a(RealmsScreen.getLocalizedString("mco.template.select.none.linkTitle"), "https://minecraft.net/realms/content-creator/");
                        RealmsSelectWorldTemplateScreen.this.field_224531_q = TextRenderingUtils.func_225224_a(lvt_3_1_x, lvt_4_2_);
                     }

                     return null;
                  } else {
                     return lvt_2_1_;
                  }
               }
            }).join()) {
               lvt_3_1_ = RealmsSelectWorldTemplateScreen.this.func_224509_a(lvt_1_1_, lvt_2_1_);
            }

         }
      }).start();
   }

   private Either<WorldTemplatePaginatedList, String> func_224509_a(WorldTemplatePaginatedList p_224509_1_, RealmsClient p_224509_2_) {
      try {
         return Either.left(p_224509_2_.func_224930_a(p_224509_1_.page + 1, p_224509_1_.size, this.field_224525_k));
      } catch (RealmsServiceException var4) {
         return Either.right(var4.getMessage());
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.field_224523_i = null;
      this.field_224524_j = null;
      this.field_224530_p = false;
      this.renderBackground();
      this.field_224517_c.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.field_224531_q != null) {
         this.func_224506_a(p_render_1_, p_render_2_, this.field_224531_q);
      }

      this.drawCenteredString(this.field_224519_e, this.width() / 2, 13, 16777215);
      if (this.field_224529_o) {
         String[] lvt_4_1_ = this.field_224527_m.split("\\\\n");

         int lvt_5_2_;
         int lvt_7_2_;
         for(lvt_5_2_ = 0; lvt_5_2_ < lvt_4_1_.length; ++lvt_5_2_) {
            int lvt_6_1_ = this.fontWidth(lvt_4_1_[lvt_5_2_]);
            lvt_7_2_ = this.width() / 2 - lvt_6_1_ / 2;
            int lvt_8_1_ = RealmsConstants.func_225109_a(-1 + lvt_5_2_);
            if (p_render_1_ >= lvt_7_2_ && p_render_1_ <= lvt_7_2_ + lvt_6_1_ && p_render_2_ >= lvt_8_1_ && p_render_2_ <= lvt_8_1_ + this.fontLineHeight()) {
               this.field_224530_p = true;
            }
         }

         for(lvt_5_2_ = 0; lvt_5_2_ < lvt_4_1_.length; ++lvt_5_2_) {
            String lvt_6_2_ = lvt_4_1_[lvt_5_2_];
            lvt_7_2_ = 10526880;
            if (this.field_224528_n != null) {
               if (this.field_224530_p) {
                  lvt_7_2_ = 7107012;
                  lvt_6_2_ = "§n" + lvt_6_2_;
               } else {
                  lvt_7_2_ = 3368635;
               }
            }

            this.drawCenteredString(lvt_6_2_, this.width() / 2, RealmsConstants.func_225109_a(-1 + lvt_5_2_), lvt_7_2_);
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.field_224523_i != null) {
         this.func_224488_a(this.field_224523_i, p_render_1_, p_render_2_);
      }

   }

   private void func_224506_a(int p_224506_1_, int p_224506_2_, List<TextRenderingUtils.Line> p_224506_3_) {
      for(int lvt_4_1_ = 0; lvt_4_1_ < p_224506_3_.size(); ++lvt_4_1_) {
         TextRenderingUtils.Line lvt_5_1_ = (TextRenderingUtils.Line)p_224506_3_.get(lvt_4_1_);
         int lvt_6_1_ = RealmsConstants.func_225109_a(4 + lvt_4_1_);
         int lvt_7_1_ = lvt_5_1_.field_225213_a.stream().mapToInt((p_224504_1_) -> {
            return this.fontWidth(p_224504_1_.func_225215_a());
         }).sum();
         int lvt_8_1_ = this.width() / 2 - lvt_7_1_ / 2;

         int lvt_12_1_;
         for(Iterator var9 = lvt_5_1_.field_225213_a.iterator(); var9.hasNext(); lvt_8_1_ = lvt_12_1_) {
            TextRenderingUtils.LineSegment lvt_10_1_ = (TextRenderingUtils.LineSegment)var9.next();
            int lvt_11_1_ = lvt_10_1_.func_225217_b() ? 3368635 : 16777215;
            lvt_12_1_ = this.draw(lvt_10_1_.func_225215_a(), lvt_8_1_, lvt_6_1_, lvt_11_1_, true);
            if (lvt_10_1_.func_225217_b() && p_224506_1_ > lvt_8_1_ && p_224506_1_ < lvt_12_1_ && p_224506_2_ > lvt_6_1_ - 3 && p_224506_2_ < lvt_6_1_ + 8) {
               this.field_224523_i = lvt_10_1_.func_225216_c();
               this.field_224524_j = lvt_10_1_.func_225216_c();
            }
         }
      }

   }

   protected void func_224488_a(String p_224488_1_, int p_224488_2_, int p_224488_3_) {
      if (p_224488_1_ != null) {
         int lvt_4_1_ = p_224488_2_ + 12;
         int lvt_5_1_ = p_224488_3_ - 12;
         int lvt_6_1_ = this.fontWidth(p_224488_1_);
         this.fillGradient(lvt_4_1_ - 3, lvt_5_1_ - 3, lvt_4_1_ + lvt_6_1_ + 3, lvt_5_1_ + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(p_224488_1_, lvt_4_1_, lvt_5_1_, 16777215);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class WorldTemplateSelectionEntry extends RealmListEntry {
      final WorldTemplate field_223756_a;

      public WorldTemplateSelectionEntry(WorldTemplate p_i51724_2_) {
         this.field_223756_a = p_i51724_2_;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223753_a(this.field_223756_a, p_render_3_, p_render_2_, p_render_6_, p_render_7_);
      }

      private void func_223753_a(WorldTemplate p_223753_1_, int p_223753_2_, int p_223753_3_, int p_223753_4_, int p_223753_5_) {
         int lvt_6_1_ = p_223753_2_ + 45 + 20;
         RealmsSelectWorldTemplateScreen.this.drawString(p_223753_1_.name, lvt_6_1_, p_223753_3_ + 2, 16777215);
         RealmsSelectWorldTemplateScreen.this.drawString(p_223753_1_.author, lvt_6_1_, p_223753_3_ + 15, 8421504);
         RealmsSelectWorldTemplateScreen.this.drawString(p_223753_1_.version, lvt_6_1_ + 227 - RealmsSelectWorldTemplateScreen.this.fontWidth(p_223753_1_.version), p_223753_3_ + 1, 8421504);
         if (!"".equals(p_223753_1_.link) || !"".equals(p_223753_1_.trailer) || !"".equals(p_223753_1_.recommendedPlayers)) {
            this.func_223755_a(lvt_6_1_ - 1, p_223753_3_ + 25, p_223753_4_, p_223753_5_, p_223753_1_.link, p_223753_1_.trailer, p_223753_1_.recommendedPlayers);
         }

         this.func_223754_a(p_223753_2_, p_223753_3_ + 1, p_223753_4_, p_223753_5_, p_223753_1_);
      }

      private void func_223754_a(int p_223754_1_, int p_223754_2_, int p_223754_3_, int p_223754_4_, WorldTemplate p_223754_5_) {
         RealmsTextureManager.func_225202_a(p_223754_5_.id, p_223754_5_.image);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RealmsScreen.blit(p_223754_1_ + 1, p_223754_2_ + 1, 0.0F, 0.0F, 38, 38, 38, 38);
         RealmsScreen.bind("realms:textures/gui/realms/slot_frame.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RealmsScreen.blit(p_223754_1_, p_223754_2_, 0.0F, 0.0F, 40, 40, 40, 40);
      }

      private void func_223755_a(int p_223755_1_, int p_223755_2_, int p_223755_3_, int p_223755_4_, String p_223755_5_, String p_223755_6_, String p_223755_7_) {
         if (!"".equals(p_223755_7_)) {
            RealmsSelectWorldTemplateScreen.this.drawString(p_223755_7_, p_223755_1_, p_223755_2_ + 4, 8421504);
         }

         int lvt_8_1_ = "".equals(p_223755_7_) ? 0 : RealmsSelectWorldTemplateScreen.this.fontWidth(p_223755_7_) + 2;
         boolean lvt_9_1_ = false;
         boolean lvt_10_1_ = false;
         if (p_223755_3_ >= p_223755_1_ + lvt_8_1_ && p_223755_3_ <= p_223755_1_ + lvt_8_1_ + 32 && p_223755_4_ >= p_223755_2_ && p_223755_4_ <= p_223755_2_ + 15 && p_223755_4_ < RealmsSelectWorldTemplateScreen.this.height() - 15 && p_223755_4_ > 32) {
            if (p_223755_3_ <= p_223755_1_ + 15 + lvt_8_1_ && p_223755_3_ > lvt_8_1_) {
               if ("".equals(p_223755_5_)) {
                  lvt_10_1_ = true;
               } else {
                  lvt_9_1_ = true;
               }
            } else if (!"".equals(p_223755_5_)) {
               lvt_10_1_ = true;
            }
         }

         if (!"".equals(p_223755_5_)) {
            RealmsScreen.bind("realms:textures/gui/realms/link_icons.png");
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(p_223755_1_ + lvt_8_1_, p_223755_2_, lvt_9_1_ ? 15.0F : 0.0F, 0.0F, 15, 15, 30, 15);
            RenderSystem.popMatrix();
         }

         if (!"".equals(p_223755_6_)) {
            RealmsScreen.bind("realms:textures/gui/realms/trailer_icons.png");
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(p_223755_1_ + lvt_8_1_ + ("".equals(p_223755_5_) ? 0 : 17), p_223755_2_, lvt_10_1_ ? 15.0F : 0.0F, 0.0F, 15, 15, 30, 15);
            RenderSystem.popMatrix();
         }

         if (lvt_9_1_ && !"".equals(p_223755_5_)) {
            RealmsSelectWorldTemplateScreen.this.field_224523_i = RealmsScreen.getLocalizedString("mco.template.info.tooltip");
            RealmsSelectWorldTemplateScreen.this.field_224524_j = p_223755_5_;
         } else if (lvt_10_1_ && !"".equals(p_223755_6_)) {
            RealmsSelectWorldTemplateScreen.this.field_224523_i = RealmsScreen.getLocalizedString("mco.template.trailer.tooltip");
            RealmsSelectWorldTemplateScreen.this.field_224524_j = p_223755_6_;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class WorldTemplateSelectionList extends RealmsObjectSelectionList<RealmsSelectWorldTemplateScreen.WorldTemplateSelectionEntry> {
      public WorldTemplateSelectionList() {
         this(Collections.emptyList());
      }

      public WorldTemplateSelectionList(Iterable<WorldTemplate> p_i51726_2_) {
         super(RealmsSelectWorldTemplateScreen.this.width(), RealmsSelectWorldTemplateScreen.this.height(), RealmsSelectWorldTemplateScreen.this.field_224529_o ? RealmsConstants.func_225109_a(1) : 32, RealmsSelectWorldTemplateScreen.this.height() - 40, 46);
         p_i51726_2_.forEach(this::func_223876_a);
      }

      public void func_223876_a(WorldTemplate p_223876_1_) {
         this.addEntry(RealmsSelectWorldTemplateScreen.this.new WorldTemplateSelectionEntry(p_223876_1_));
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (p_mouseClicked_5_ == 0 && p_mouseClicked_3_ >= (double)this.y0() && p_mouseClicked_3_ <= (double)this.y1()) {
            int lvt_6_1_ = this.width() / 2 - 150;
            if (RealmsSelectWorldTemplateScreen.this.field_224524_j != null) {
               RealmsUtil.func_225190_c(RealmsSelectWorldTemplateScreen.this.field_224524_j);
            }

            int lvt_7_1_ = (int)Math.floor(p_mouseClicked_3_ - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
            int lvt_8_1_ = lvt_7_1_ / this.itemHeight();
            if (p_mouseClicked_1_ >= (double)lvt_6_1_ && p_mouseClicked_1_ < (double)this.getScrollbarPosition() && lvt_8_1_ >= 0 && lvt_7_1_ >= 0 && lvt_8_1_ < this.getItemCount()) {
               this.selectItem(lvt_8_1_);
               this.itemClicked(lvt_7_1_, lvt_8_1_, p_mouseClicked_1_, p_mouseClicked_3_, this.width());
               if (lvt_8_1_ >= RealmsSelectWorldTemplateScreen.this.field_224517_c.getItemCount()) {
                  return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
               }

               RealmsSelectWorldTemplateScreen.this.field_224518_d = lvt_8_1_;
               RealmsSelectWorldTemplateScreen.this.func_224514_b();
               RealmsSelectWorldTemplateScreen.this.field_224526_l = RealmsSelectWorldTemplateScreen.this.field_224526_l + 7;
               if (RealmsSelectWorldTemplateScreen.this.field_224526_l >= 10) {
                  RealmsSelectWorldTemplateScreen.this.func_224500_h();
               }

               return true;
            }
         }

         return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }

      public void selectItem(int p_selectItem_1_) {
         RealmsSelectWorldTemplateScreen.this.field_224518_d = p_selectItem_1_;
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            WorldTemplate lvt_2_1_ = RealmsSelectWorldTemplateScreen.this.field_224517_c.func_223877_a(p_selectItem_1_);
            String lvt_3_1_ = RealmsScreen.getLocalizedString("narrator.select.list.position", p_selectItem_1_ + 1, RealmsSelectWorldTemplateScreen.this.field_224517_c.getItemCount());
            String lvt_4_1_ = RealmsScreen.getLocalizedString("mco.template.select.narrate.version", lvt_2_1_.version);
            String lvt_5_1_ = RealmsScreen.getLocalizedString("mco.template.select.narrate.authors", lvt_2_1_.author);
            String lvt_6_1_ = Realms.joinNarrations(Arrays.asList(lvt_2_1_.name, lvt_5_1_, lvt_2_1_.recommendedPlayers, lvt_4_1_, lvt_3_1_));
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", lvt_6_1_));
         }

         RealmsSelectWorldTemplateScreen.this.func_224514_b();
      }

      public void itemClicked(int p_itemClicked_1_, int p_itemClicked_2_, double p_itemClicked_3_, double p_itemClicked_5_, int p_itemClicked_7_) {
         if (p_itemClicked_2_ < RealmsSelectWorldTemplateScreen.this.field_224517_c.getItemCount()) {
            ;
         }
      }

      public int getMaxPosition() {
         return this.getItemCount() * 46;
      }

      public int getRowWidth() {
         return 300;
      }

      public void renderBackground() {
         RealmsSelectWorldTemplateScreen.this.renderBackground();
      }

      public boolean isFocused() {
         return RealmsSelectWorldTemplateScreen.this.isFocused(this);
      }

      public boolean func_223878_a() {
         return this.getItemCount() == 0;
      }

      public WorldTemplate func_223877_a(int p_223877_1_) {
         return ((RealmsSelectWorldTemplateScreen.WorldTemplateSelectionEntry)this.children().get(p_223877_1_)).field_223756_a;
      }

      public List<WorldTemplate> func_223879_b() {
         return (List)this.children().stream().map((p_223875_0_) -> {
            return p_223875_0_.field_223756_a;
         }).collect(Collectors.toList());
      }
   }
}
