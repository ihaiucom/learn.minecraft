package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReadBookScreen extends Screen {
   public static final ReadBookScreen.IBookInfo field_214166_a = new ReadBookScreen.IBookInfo() {
      public int func_216918_a() {
         return 0;
      }

      public ITextComponent func_216915_a(int p_216915_1_) {
         return new StringTextComponent("");
      }
   };
   public static final ResourceLocation field_214167_b = new ResourceLocation("textures/gui/book.png");
   private ReadBookScreen.IBookInfo field_214168_c;
   private int field_214169_d;
   private List<ITextComponent> field_214170_e;
   private int field_214171_f;
   private ChangePageButton field_214172_g;
   private ChangePageButton field_214173_h;
   private final boolean field_214174_i;

   public ReadBookScreen(ReadBookScreen.IBookInfo p_i51098_1_) {
      this(p_i51098_1_, true);
   }

   public ReadBookScreen() {
      this(field_214166_a, false);
   }

   private ReadBookScreen(ReadBookScreen.IBookInfo p_i51099_1_, boolean p_i51099_2_) {
      super(NarratorChatListener.field_216868_a);
      this.field_214170_e = Collections.emptyList();
      this.field_214171_f = -1;
      this.field_214168_c = p_i51099_1_;
      this.field_214174_i = p_i51099_2_;
   }

   public void func_214155_a(ReadBookScreen.IBookInfo p_214155_1_) {
      this.field_214168_c = p_214155_1_;
      this.field_214169_d = MathHelper.clamp(this.field_214169_d, 0, p_214155_1_.func_216918_a());
      this.func_214151_f();
      this.field_214171_f = -1;
   }

   public boolean func_214160_a(int p_214160_1_) {
      int lvt_2_1_ = MathHelper.clamp(p_214160_1_, 0, this.field_214168_c.func_216918_a() - 1);
      if (lvt_2_1_ != this.field_214169_d) {
         this.field_214169_d = lvt_2_1_;
         this.func_214151_f();
         this.field_214171_f = -1;
         return true;
      } else {
         return false;
      }
   }

   protected boolean func_214153_b(int p_214153_1_) {
      return this.func_214160_a(p_214153_1_);
   }

   protected void init() {
      this.func_214162_b();
      this.func_214164_c();
   }

   protected void func_214162_b() {
      this.addButton(new Button(this.width / 2 - 100, 196, 200, 20, I18n.format("gui.done"), (p_214161_1_) -> {
         this.minecraft.displayGuiScreen((Screen)null);
      }));
   }

   protected void func_214164_c() {
      int lvt_1_1_ = (this.width - 192) / 2;
      int lvt_2_1_ = true;
      this.field_214172_g = (ChangePageButton)this.addButton(new ChangePageButton(lvt_1_1_ + 116, 159, true, (p_214159_1_) -> {
         this.func_214163_e();
      }, this.field_214174_i));
      this.field_214173_h = (ChangePageButton)this.addButton(new ChangePageButton(lvt_1_1_ + 43, 159, false, (p_214158_1_) -> {
         this.func_214165_d();
      }, this.field_214174_i));
      this.func_214151_f();
   }

   private int func_214152_a() {
      return this.field_214168_c.func_216918_a();
   }

   protected void func_214165_d() {
      if (this.field_214169_d > 0) {
         --this.field_214169_d;
      }

      this.func_214151_f();
   }

   protected void func_214163_e() {
      if (this.field_214169_d < this.func_214152_a() - 1) {
         ++this.field_214169_d;
      }

      this.func_214151_f();
   }

   private void func_214151_f() {
      this.field_214172_g.visible = this.field_214169_d < this.func_214152_a() - 1;
      this.field_214173_h.visible = this.field_214169_d > 0;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else {
         switch(p_keyPressed_1_) {
         case 266:
            this.field_214173_h.onPress();
            return true;
         case 267:
            this.field_214172_g.onPress();
            return true;
         default:
            return false;
         }
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(field_214167_b);
      int lvt_4_1_ = (this.width - 192) / 2;
      int lvt_5_1_ = true;
      this.blit(lvt_4_1_, 2, 0, 0, 192, 192);
      String lvt_6_1_ = I18n.format("book.pageIndicator", this.field_214169_d + 1, Math.max(this.func_214152_a(), 1));
      if (this.field_214171_f != this.field_214169_d) {
         ITextComponent lvt_7_1_ = this.field_214168_c.func_216916_b(this.field_214169_d);
         this.field_214170_e = RenderComponentsUtil.splitText(lvt_7_1_, 114, this.font, true, true);
      }

      this.field_214171_f = this.field_214169_d;
      int lvt_7_2_ = this.func_214156_a(lvt_6_1_);
      this.font.drawString(lvt_6_1_, (float)(lvt_4_1_ - lvt_7_2_ + 192 - 44), 18.0F, 0);
      this.font.getClass();
      int lvt_8_1_ = Math.min(128 / 9, this.field_214170_e.size());

      for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_8_1_; ++lvt_9_1_) {
         ITextComponent lvt_10_1_ = (ITextComponent)this.field_214170_e.get(lvt_9_1_);
         FontRenderer var10000 = this.font;
         String var10001 = lvt_10_1_.getFormattedText();
         float var10002 = (float)(lvt_4_1_ + 36);
         this.font.getClass();
         var10000.drawString(var10001, var10002, (float)(32 + lvt_9_1_ * 9), 0);
      }

      ITextComponent lvt_9_2_ = this.func_214154_c((double)p_render_1_, (double)p_render_2_);
      if (lvt_9_2_ != null) {
         this.renderComponentHoverEffect(lvt_9_2_, p_render_1_, p_render_2_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private int func_214156_a(String p_214156_1_) {
      return this.font.getStringWidth(this.font.getBidiFlag() ? this.font.bidiReorder(p_214156_1_) : p_214156_1_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         ITextComponent lvt_6_1_ = this.func_214154_c(p_mouseClicked_1_, p_mouseClicked_3_);
         if (lvt_6_1_ != null && this.handleComponentClicked(lvt_6_1_)) {
            return true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean handleComponentClicked(ITextComponent p_handleComponentClicked_1_) {
      ClickEvent lvt_2_1_ = p_handleComponentClicked_1_.getStyle().getClickEvent();
      if (lvt_2_1_ == null) {
         return false;
      } else if (lvt_2_1_.getAction() == ClickEvent.Action.CHANGE_PAGE) {
         String lvt_3_1_ = lvt_2_1_.getValue();

         try {
            int lvt_4_1_ = Integer.parseInt(lvt_3_1_) - 1;
            return this.func_214153_b(lvt_4_1_);
         } catch (Exception var5) {
            return false;
         }
      } else {
         boolean lvt_3_2_ = super.handleComponentClicked(p_handleComponentClicked_1_);
         if (lvt_3_2_ && lvt_2_1_.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.minecraft.displayGuiScreen((Screen)null);
         }

         return lvt_3_2_;
      }
   }

   @Nullable
   public ITextComponent func_214154_c(double p_214154_1_, double p_214154_3_) {
      if (this.field_214170_e == null) {
         return null;
      } else {
         int lvt_5_1_ = MathHelper.floor(p_214154_1_ - (double)((this.width - 192) / 2) - 36.0D);
         int lvt_6_1_ = MathHelper.floor(p_214154_3_ - 2.0D - 30.0D);
         if (lvt_5_1_ >= 0 && lvt_6_1_ >= 0) {
            this.font.getClass();
            int lvt_7_1_ = Math.min(128 / 9, this.field_214170_e.size());
            if (lvt_5_1_ <= 114) {
               this.minecraft.fontRenderer.getClass();
               if (lvt_6_1_ < 9 * lvt_7_1_ + lvt_7_1_) {
                  this.minecraft.fontRenderer.getClass();
                  int lvt_8_1_ = lvt_6_1_ / 9;
                  if (lvt_8_1_ >= 0 && lvt_8_1_ < this.field_214170_e.size()) {
                     ITextComponent lvt_9_1_ = (ITextComponent)this.field_214170_e.get(lvt_8_1_);
                     int lvt_10_1_ = 0;
                     Iterator var11 = lvt_9_1_.iterator();

                     while(var11.hasNext()) {
                        ITextComponent lvt_12_1_ = (ITextComponent)var11.next();
                        if (lvt_12_1_ instanceof StringTextComponent) {
                           lvt_10_1_ += this.minecraft.fontRenderer.getStringWidth(lvt_12_1_.getFormattedText());
                           if (lvt_10_1_ > lvt_5_1_) {
                              return lvt_12_1_;
                           }
                        }
                     }
                  }

                  return null;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public static List<String> func_214157_a(CompoundNBT p_214157_0_) {
      ListNBT lvt_1_1_ = p_214157_0_.getList("pages", 8).copy();
      Builder<String> lvt_2_1_ = ImmutableList.builder();

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_1_1_.size(); ++lvt_3_1_) {
         lvt_2_1_.add(lvt_1_1_.getString(lvt_3_1_));
      }

      return lvt_2_1_.build();
   }

   @OnlyIn(Dist.CLIENT)
   public static class UnwrittenBookInfo implements ReadBookScreen.IBookInfo {
      private final List<String> field_216920_a;

      public UnwrittenBookInfo(ItemStack p_i50617_1_) {
         this.field_216920_a = func_216919_b(p_i50617_1_);
      }

      private static List<String> func_216919_b(ItemStack p_216919_0_) {
         CompoundNBT lvt_1_1_ = p_216919_0_.getTag();
         return (List)(lvt_1_1_ != null ? ReadBookScreen.func_214157_a(lvt_1_1_) : ImmutableList.of());
      }

      public int func_216918_a() {
         return this.field_216920_a.size();
      }

      public ITextComponent func_216915_a(int p_216915_1_) {
         return new StringTextComponent((String)this.field_216920_a.get(p_216915_1_));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WrittenBookInfo implements ReadBookScreen.IBookInfo {
      private final List<String> field_216922_a;

      public WrittenBookInfo(ItemStack p_i50616_1_) {
         this.field_216922_a = func_216921_b(p_i50616_1_);
      }

      private static List<String> func_216921_b(ItemStack p_216921_0_) {
         CompoundNBT lvt_1_1_ = p_216921_0_.getTag();
         return (List)(lvt_1_1_ != null && WrittenBookItem.validBookTagContents(lvt_1_1_) ? ReadBookScreen.func_214157_a(lvt_1_1_) : ImmutableList.of((new TranslationTextComponent("book.invalid.tag", new Object[0])).applyTextStyle(TextFormatting.DARK_RED).getFormattedText()));
      }

      public int func_216918_a() {
         return this.field_216922_a.size();
      }

      public ITextComponent func_216915_a(int p_216915_1_) {
         String lvt_2_1_ = (String)this.field_216922_a.get(p_216915_1_);

         try {
            ITextComponent lvt_3_1_ = ITextComponent.Serializer.fromJson(lvt_2_1_);
            if (lvt_3_1_ != null) {
               return lvt_3_1_;
            }
         } catch (Exception var4) {
         }

         return new StringTextComponent(lvt_2_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface IBookInfo {
      int func_216918_a();

      ITextComponent func_216915_a(int var1);

      default ITextComponent func_216916_b(int p_216916_1_) {
         return (ITextComponent)(p_216916_1_ >= 0 && p_216916_1_ < this.func_216918_a() ? this.func_216915_a(p_216916_1_) : new StringTextComponent(""));
      }

      static ReadBookScreen.IBookInfo func_216917_a(ItemStack p_216917_0_) {
         Item lvt_1_1_ = p_216917_0_.getItem();
         if (lvt_1_1_ == Items.WRITTEN_BOOK) {
            return new ReadBookScreen.WrittenBookInfo(p_216917_0_);
         } else {
            return (ReadBookScreen.IBookInfo)(lvt_1_1_ == Items.WRITABLE_BOOK ? new ReadBookScreen.UnwrittenBookInfo(p_216917_0_) : ReadBookScreen.field_214166_a);
         }
      }
   }
}
