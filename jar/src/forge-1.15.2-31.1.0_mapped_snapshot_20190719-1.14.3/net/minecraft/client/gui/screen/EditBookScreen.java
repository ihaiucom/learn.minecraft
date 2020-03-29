package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditBookScreen extends Screen {
   private final PlayerEntity editingPlayer;
   private final ItemStack book;
   private boolean field_214234_c;
   private boolean field_214235_d;
   private int field_214236_e;
   private int field_214237_f;
   private final List<String> field_214238_g = Lists.newArrayList();
   private String field_214239_h = "";
   private int field_214240_i;
   private int field_214241_j;
   private long field_214242_k;
   private int field_214243_l = -1;
   private ChangePageButton field_214244_m;
   private ChangePageButton field_214245_n;
   private Button field_214246_o;
   private Button field_214247_p;
   private Button field_214248_q;
   private Button field_214249_r;
   private final Hand hand;

   public EditBookScreen(PlayerEntity p_i51100_1_, ItemStack p_i51100_2_, Hand p_i51100_3_) {
      super(NarratorChatListener.field_216868_a);
      this.editingPlayer = p_i51100_1_;
      this.book = p_i51100_2_;
      this.hand = p_i51100_3_;
      CompoundNBT lvt_4_1_ = p_i51100_2_.getTag();
      if (lvt_4_1_ != null) {
         ListNBT lvt_5_1_ = lvt_4_1_.getList("pages", 8).copy();

         for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.size(); ++lvt_6_1_) {
            this.field_214238_g.add(lvt_5_1_.getString(lvt_6_1_));
         }
      }

      if (this.field_214238_g.isEmpty()) {
         this.field_214238_g.add("");
      }

   }

   private int func_214199_a() {
      return this.field_214238_g.size();
   }

   public void tick() {
      super.tick();
      ++this.field_214236_e;
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.field_214247_p = (Button)this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.format("book.signButton"), (p_214201_1_) -> {
         this.field_214235_d = true;
         this.func_214229_d();
      }));
      this.field_214246_o = (Button)this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.format("gui.done"), (p_214204_1_) -> {
         this.minecraft.displayGuiScreen((Screen)null);
         this.sendBookToServer(false);
      }));
      this.field_214248_q = (Button)this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.format("book.finalizeButton"), (p_214195_1_) -> {
         if (this.field_214235_d) {
            this.sendBookToServer(true);
            this.minecraft.displayGuiScreen((Screen)null);
         }

      }));
      this.field_214249_r = (Button)this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.format("gui.cancel"), (p_214212_1_) -> {
         if (this.field_214235_d) {
            this.field_214235_d = false;
         }

         this.func_214229_d();
      }));
      int lvt_1_1_ = (this.width - 192) / 2;
      int lvt_2_1_ = true;
      this.field_214244_m = (ChangePageButton)this.addButton(new ChangePageButton(lvt_1_1_ + 116, 159, true, (p_214208_1_) -> {
         this.func_214214_c();
      }, true));
      this.field_214245_n = (ChangePageButton)this.addButton(new ChangePageButton(lvt_1_1_ + 43, 159, false, (p_214205_1_) -> {
         this.func_214228_b();
      }, true));
      this.func_214229_d();
   }

   private String func_214219_a(String p_214219_1_) {
      StringBuilder lvt_2_1_ = new StringBuilder();
      char[] var3 = p_214219_1_.toCharArray();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         char lvt_6_1_ = var3[var5];
         if (lvt_6_1_ != 167 && lvt_6_1_ != 127) {
            lvt_2_1_.append(lvt_6_1_);
         }
      }

      return lvt_2_1_.toString();
   }

   private void func_214228_b() {
      if (this.field_214237_f > 0) {
         --this.field_214237_f;
         this.field_214240_i = 0;
         this.field_214241_j = this.field_214240_i;
      }

      this.func_214229_d();
   }

   private void func_214214_c() {
      if (this.field_214237_f < this.func_214199_a() - 1) {
         ++this.field_214237_f;
         this.field_214240_i = 0;
         this.field_214241_j = this.field_214240_i;
      } else {
         this.func_214215_f();
         if (this.field_214237_f < this.func_214199_a() - 1) {
            ++this.field_214237_f;
         }

         this.field_214240_i = 0;
         this.field_214241_j = this.field_214240_i;
      }

      this.func_214229_d();
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   private void func_214229_d() {
      this.field_214245_n.visible = !this.field_214235_d && this.field_214237_f > 0;
      this.field_214244_m.visible = !this.field_214235_d;
      this.field_214246_o.visible = !this.field_214235_d;
      this.field_214247_p.visible = !this.field_214235_d;
      this.field_214249_r.visible = this.field_214235_d;
      this.field_214248_q.visible = this.field_214235_d;
      this.field_214248_q.active = !this.field_214239_h.trim().isEmpty();
   }

   private void func_214213_e() {
      ListIterator lvt_1_1_ = this.field_214238_g.listIterator(this.field_214238_g.size());

      while(lvt_1_1_.hasPrevious() && ((String)lvt_1_1_.previous()).isEmpty()) {
         lvt_1_1_.remove();
      }

   }

   private void sendBookToServer(boolean p_214198_1_) {
      if (this.field_214234_c) {
         this.func_214213_e();
         ListNBT lvt_2_1_ = new ListNBT();
         this.field_214238_g.stream().map(StringNBT::func_229705_a_).forEach(lvt_2_1_::add);
         if (!this.field_214238_g.isEmpty()) {
            this.book.setTagInfo("pages", lvt_2_1_);
         }

         if (p_214198_1_) {
            this.book.setTagInfo("author", StringNBT.func_229705_a_(this.editingPlayer.getGameProfile().getName()));
            this.book.setTagInfo("title", StringNBT.func_229705_a_(this.field_214239_h.trim()));
         }

         this.minecraft.getConnection().sendPacket(new CEditBookPacket(this.book, p_214198_1_, this.hand));
      }
   }

   private void func_214215_f() {
      if (this.func_214199_a() < 100) {
         this.field_214238_g.add("");
         this.field_214234_c = true;
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else {
         return this.field_214235_d ? this.func_214196_c(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : this.func_214230_b(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (super.charTyped(p_charTyped_1_, p_charTyped_2_)) {
         return true;
      } else if (this.field_214235_d) {
         if (this.field_214239_h.length() < 16 && SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
            this.field_214239_h = this.field_214239_h + Character.toString(p_charTyped_1_);
            this.func_214229_d();
            this.field_214234_c = true;
            return true;
         } else {
            return false;
         }
      } else if (SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
         this.func_214202_k(Character.toString(p_charTyped_1_));
         return true;
      } else {
         return false;
      }
   }

   private boolean func_214230_b(int p_214230_1_, int p_214230_2_, int p_214230_3_) {
      String lvt_4_1_ = this.func_214193_h();
      if (Screen.isSelectAll(p_214230_1_)) {
         this.field_214241_j = 0;
         this.field_214240_i = lvt_4_1_.length();
         return true;
      } else if (Screen.isCopy(p_214230_1_)) {
         this.minecraft.keyboardListener.setClipboardString(this.func_214231_i());
         return true;
      } else if (Screen.isPaste(p_214230_1_)) {
         this.func_214202_k(this.func_214219_a(TextFormatting.getTextWithoutFormattingCodes(this.minecraft.keyboardListener.getClipboardString().replaceAll("\\r", ""))));
         this.field_214241_j = this.field_214240_i;
         return true;
      } else if (Screen.isCut(p_214230_1_)) {
         this.minecraft.keyboardListener.setClipboardString(this.func_214231_i());
         this.func_214192_g();
         return true;
      } else {
         switch(p_214230_1_) {
         case 257:
         case 335:
            this.func_214202_k("\n");
            return true;
         case 259:
            this.func_214207_b(lvt_4_1_);
            return true;
         case 261:
            this.func_214221_c(lvt_4_1_);
            return true;
         case 262:
            this.func_214218_e(lvt_4_1_);
            return true;
         case 263:
            this.func_214200_d(lvt_4_1_);
            return true;
         case 264:
            this.func_214209_g(lvt_4_1_);
            return true;
         case 265:
            this.func_214197_f(lvt_4_1_);
            return true;
         case 266:
            this.field_214245_n.onPress();
            return true;
         case 267:
            this.field_214244_m.onPress();
            return true;
         case 268:
            this.func_214220_h(lvt_4_1_);
            return true;
         case 269:
            this.func_214211_i(lvt_4_1_);
            return true;
         default:
            return false;
         }
      }
   }

   private void func_214207_b(String p_214207_1_) {
      if (!p_214207_1_.isEmpty()) {
         if (this.field_214241_j != this.field_214240_i) {
            this.func_214192_g();
         } else if (this.field_214240_i > 0) {
            String lvt_2_1_ = (new StringBuilder(p_214207_1_)).deleteCharAt(Math.max(0, this.field_214240_i - 1)).toString();
            this.func_214217_j(lvt_2_1_);
            this.field_214240_i = Math.max(0, this.field_214240_i - 1);
            this.field_214241_j = this.field_214240_i;
         }
      }

   }

   private void func_214221_c(String p_214221_1_) {
      if (!p_214221_1_.isEmpty()) {
         if (this.field_214241_j != this.field_214240_i) {
            this.func_214192_g();
         } else if (this.field_214240_i < p_214221_1_.length()) {
            String lvt_2_1_ = (new StringBuilder(p_214221_1_)).deleteCharAt(Math.max(0, this.field_214240_i)).toString();
            this.func_214217_j(lvt_2_1_);
         }
      }

   }

   private void func_214200_d(String p_214200_1_) {
      int lvt_2_1_ = this.font.getBidiFlag() ? 1 : -1;
      if (Screen.hasControlDown()) {
         this.field_214240_i = this.font.func_216863_a(p_214200_1_, lvt_2_1_, this.field_214240_i, true);
      } else {
         this.field_214240_i = Math.max(0, this.field_214240_i + lvt_2_1_);
      }

      if (!Screen.hasShiftDown()) {
         this.field_214241_j = this.field_214240_i;
      }

   }

   private void func_214218_e(String p_214218_1_) {
      int lvt_2_1_ = this.font.getBidiFlag() ? -1 : 1;
      if (Screen.hasControlDown()) {
         this.field_214240_i = this.font.func_216863_a(p_214218_1_, lvt_2_1_, this.field_214240_i, true);
      } else {
         this.field_214240_i = Math.min(p_214218_1_.length(), this.field_214240_i + lvt_2_1_);
      }

      if (!Screen.hasShiftDown()) {
         this.field_214241_j = this.field_214240_i;
      }

   }

   private void func_214197_f(String p_214197_1_) {
      if (!p_214197_1_.isEmpty()) {
         EditBookScreen.Point lvt_2_1_ = this.func_214194_c(p_214197_1_, this.field_214240_i);
         if (lvt_2_1_.field_216929_c == 0) {
            this.field_214240_i = 0;
            if (!Screen.hasShiftDown()) {
               this.field_214241_j = this.field_214240_i;
            }
         } else {
            int var10005 = lvt_2_1_.field_216928_b + this.func_214206_a(p_214197_1_, this.field_214240_i) / 3;
            int var10006 = lvt_2_1_.field_216929_c;
            this.font.getClass();
            int lvt_3_1_ = this.func_214203_a(p_214197_1_, new EditBookScreen.Point(var10005, var10006 - 9));
            if (lvt_3_1_ >= 0) {
               this.field_214240_i = lvt_3_1_;
               if (!Screen.hasShiftDown()) {
                  this.field_214241_j = this.field_214240_i;
               }
            }
         }
      }

   }

   private void func_214209_g(String p_214209_1_) {
      if (!p_214209_1_.isEmpty()) {
         EditBookScreen.Point lvt_2_1_ = this.func_214194_c(p_214209_1_, this.field_214240_i);
         int lvt_3_1_ = this.font.getWordWrappedHeight(p_214209_1_ + "" + TextFormatting.BLACK + "_", 114);
         int var10000 = lvt_2_1_.field_216929_c;
         this.font.getClass();
         if (var10000 + 9 == lvt_3_1_) {
            this.field_214240_i = p_214209_1_.length();
            if (!Screen.hasShiftDown()) {
               this.field_214241_j = this.field_214240_i;
            }
         } else {
            int var10005 = lvt_2_1_.field_216928_b + this.func_214206_a(p_214209_1_, this.field_214240_i) / 3;
            int var10006 = lvt_2_1_.field_216929_c;
            this.font.getClass();
            int lvt_4_1_ = this.func_214203_a(p_214209_1_, new EditBookScreen.Point(var10005, var10006 + 9));
            if (lvt_4_1_ >= 0) {
               this.field_214240_i = lvt_4_1_;
               if (!Screen.hasShiftDown()) {
                  this.field_214241_j = this.field_214240_i;
               }
            }
         }
      }

   }

   private void func_214220_h(String p_214220_1_) {
      this.field_214240_i = this.func_214203_a(p_214220_1_, new EditBookScreen.Point(0, this.func_214194_c(p_214220_1_, this.field_214240_i).field_216929_c));
      if (!Screen.hasShiftDown()) {
         this.field_214241_j = this.field_214240_i;
      }

   }

   private void func_214211_i(String p_214211_1_) {
      this.field_214240_i = this.func_214203_a(p_214211_1_, new EditBookScreen.Point(113, this.func_214194_c(p_214211_1_, this.field_214240_i).field_216929_c));
      if (!Screen.hasShiftDown()) {
         this.field_214241_j = this.field_214240_i;
      }

   }

   private void func_214192_g() {
      if (this.field_214241_j != this.field_214240_i) {
         String lvt_1_1_ = this.func_214193_h();
         int lvt_2_1_ = Math.min(this.field_214240_i, this.field_214241_j);
         int lvt_3_1_ = Math.max(this.field_214240_i, this.field_214241_j);
         String lvt_4_1_ = lvt_1_1_.substring(0, lvt_2_1_) + lvt_1_1_.substring(lvt_3_1_);
         this.field_214240_i = lvt_2_1_;
         this.field_214241_j = this.field_214240_i;
         this.func_214217_j(lvt_4_1_);
      }
   }

   private int func_214206_a(String p_214206_1_, int p_214206_2_) {
      return (int)this.font.getCharWidth(p_214206_1_.charAt(MathHelper.clamp(p_214206_2_, 0, p_214206_1_.length() - 1)));
   }

   private boolean func_214196_c(int p_214196_1_, int p_214196_2_, int p_214196_3_) {
      switch(p_214196_1_) {
      case 257:
      case 335:
         if (!this.field_214239_h.isEmpty()) {
            this.sendBookToServer(true);
            this.minecraft.displayGuiScreen((Screen)null);
         }

         return true;
      case 259:
         if (!this.field_214239_h.isEmpty()) {
            this.field_214239_h = this.field_214239_h.substring(0, this.field_214239_h.length() - 1);
            this.func_214229_d();
         }

         return true;
      default:
         return false;
      }
   }

   private String func_214193_h() {
      return this.field_214237_f >= 0 && this.field_214237_f < this.field_214238_g.size() ? (String)this.field_214238_g.get(this.field_214237_f) : "";
   }

   private void func_214217_j(String p_214217_1_) {
      if (this.field_214237_f >= 0 && this.field_214237_f < this.field_214238_g.size()) {
         this.field_214238_g.set(this.field_214237_f, p_214217_1_);
         this.field_214234_c = true;
      }

   }

   private void func_214202_k(String p_214202_1_) {
      if (this.field_214241_j != this.field_214240_i) {
         this.func_214192_g();
      }

      String lvt_2_1_ = this.func_214193_h();
      this.field_214240_i = MathHelper.clamp(this.field_214240_i, 0, lvt_2_1_.length());
      String lvt_3_1_ = (new StringBuilder(lvt_2_1_)).insert(this.field_214240_i, p_214202_1_).toString();
      int lvt_4_1_ = this.font.getWordWrappedHeight(lvt_3_1_ + "" + TextFormatting.BLACK + "_", 114);
      if (lvt_4_1_ <= 128 && lvt_3_1_.length() < 1024) {
         this.func_214217_j(lvt_3_1_);
         this.field_214241_j = this.field_214240_i = Math.min(this.func_214193_h().length(), this.field_214240_i + p_214202_1_.length());
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.setFocused((IGuiEventListener)null);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(ReadBookScreen.field_214167_b);
      int lvt_4_1_ = (this.width - 192) / 2;
      int lvt_5_1_ = true;
      this.blit(lvt_4_1_, 2, 0, 0, 192, 192);
      String lvt_6_1_;
      String lvt_7_2_;
      int lvt_8_1_;
      if (this.field_214235_d) {
         lvt_6_1_ = this.field_214239_h;
         if (this.field_214236_e / 6 % 2 == 0) {
            lvt_6_1_ = lvt_6_1_ + "" + TextFormatting.BLACK + "_";
         } else {
            lvt_6_1_ = lvt_6_1_ + "" + TextFormatting.GRAY + "_";
         }

         lvt_7_2_ = I18n.format("book.editTitle");
         lvt_8_1_ = this.func_214225_l(lvt_7_2_);
         this.font.drawString(lvt_7_2_, (float)(lvt_4_1_ + 36 + (114 - lvt_8_1_) / 2), 34.0F, 0);
         int lvt_9_1_ = this.func_214225_l(lvt_6_1_);
         this.font.drawString(lvt_6_1_, (float)(lvt_4_1_ + 36 + (114 - lvt_9_1_) / 2), 50.0F, 0);
         String lvt_10_1_ = I18n.format("book.byAuthor", this.editingPlayer.getName().getString());
         int lvt_11_1_ = this.func_214225_l(lvt_10_1_);
         this.font.drawString(TextFormatting.DARK_GRAY + lvt_10_1_, (float)(lvt_4_1_ + 36 + (114 - lvt_11_1_) / 2), 60.0F, 0);
         String lvt_12_1_ = I18n.format("book.finalizeWarning");
         this.font.drawSplitString(lvt_12_1_, lvt_4_1_ + 36, 82, 114, 0);
      } else {
         lvt_6_1_ = I18n.format("book.pageIndicator", this.field_214237_f + 1, this.func_214199_a());
         lvt_7_2_ = this.func_214193_h();
         lvt_8_1_ = this.func_214225_l(lvt_6_1_);
         this.font.drawString(lvt_6_1_, (float)(lvt_4_1_ - lvt_8_1_ + 192 - 44), 18.0F, 0);
         this.font.drawSplitString(lvt_7_2_, lvt_4_1_ + 36, 32, 114, 0);
         this.func_214222_m(lvt_7_2_);
         if (this.field_214236_e / 6 % 2 == 0) {
            EditBookScreen.Point lvt_9_2_ = this.func_214194_c(lvt_7_2_, this.field_214240_i);
            if (this.font.getBidiFlag()) {
               this.func_214227_a(lvt_9_2_);
               lvt_9_2_.field_216928_b = lvt_9_2_.field_216928_b - 4;
            }

            this.func_214224_c(lvt_9_2_);
            if (this.field_214240_i < lvt_7_2_.length()) {
               int var10000 = lvt_9_2_.field_216928_b;
               int var10001 = lvt_9_2_.field_216929_c - 1;
               int var10002 = lvt_9_2_.field_216928_b + 1;
               int var10003 = lvt_9_2_.field_216929_c;
               this.font.getClass();
               AbstractGui.fill(var10000, var10001, var10002, var10003 + 9, -16777216);
            } else {
               this.font.drawString("_", (float)lvt_9_2_.field_216928_b, (float)lvt_9_2_.field_216929_c, 0);
            }
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private int func_214225_l(String p_214225_1_) {
      return this.font.getStringWidth(this.font.getBidiFlag() ? this.font.bidiReorder(p_214225_1_) : p_214225_1_);
   }

   private int func_214216_b(String p_214216_1_, int p_214216_2_) {
      return this.font.sizeStringToWidth(p_214216_1_, p_214216_2_);
   }

   private String func_214231_i() {
      String lvt_1_1_ = this.func_214193_h();
      int lvt_2_1_ = Math.min(this.field_214240_i, this.field_214241_j);
      int lvt_3_1_ = Math.max(this.field_214240_i, this.field_214241_j);
      return lvt_1_1_.substring(lvt_2_1_, lvt_3_1_);
   }

   private void func_214222_m(String p_214222_1_) {
      if (this.field_214241_j != this.field_214240_i) {
         int lvt_2_1_ = Math.min(this.field_214240_i, this.field_214241_j);
         int lvt_3_1_ = Math.max(this.field_214240_i, this.field_214241_j);
         String lvt_4_1_ = p_214222_1_.substring(lvt_2_1_, lvt_3_1_);
         int lvt_5_1_ = this.font.func_216863_a(p_214222_1_, 1, lvt_3_1_, true);
         String lvt_6_1_ = p_214222_1_.substring(lvt_2_1_, lvt_5_1_);
         EditBookScreen.Point lvt_7_1_ = this.func_214194_c(p_214222_1_, lvt_2_1_);
         int var10003 = lvt_7_1_.field_216928_b;
         int var10004 = lvt_7_1_.field_216929_c;
         this.font.getClass();
         EditBookScreen.Point lvt_8_1_ = new EditBookScreen.Point(var10003, var10004 + 9);

         while(!lvt_4_1_.isEmpty()) {
            int lvt_9_1_ = this.func_214216_b(lvt_6_1_, 114 - lvt_7_1_.field_216928_b);
            if (lvt_4_1_.length() <= lvt_9_1_) {
               lvt_8_1_.field_216928_b = lvt_7_1_.field_216928_b + this.func_214225_l(lvt_4_1_);
               this.func_214223_a(lvt_7_1_, lvt_8_1_);
               break;
            }

            lvt_9_1_ = Math.min(lvt_9_1_, lvt_4_1_.length() - 1);
            String lvt_10_1_ = lvt_4_1_.substring(0, lvt_9_1_);
            char lvt_11_1_ = lvt_4_1_.charAt(lvt_9_1_);
            boolean lvt_12_1_ = lvt_11_1_ == ' ' || lvt_11_1_ == '\n';
            lvt_4_1_ = TextFormatting.getFormatString(lvt_10_1_) + lvt_4_1_.substring(lvt_9_1_ + (lvt_12_1_ ? 1 : 0));
            lvt_6_1_ = TextFormatting.getFormatString(lvt_10_1_) + lvt_6_1_.substring(lvt_9_1_ + (lvt_12_1_ ? 1 : 0));
            lvt_8_1_.field_216928_b = lvt_7_1_.field_216928_b + this.func_214225_l(lvt_10_1_ + " ");
            this.func_214223_a(lvt_7_1_, lvt_8_1_);
            lvt_7_1_.field_216928_b = 0;
            int var10001 = lvt_7_1_.field_216929_c;
            this.font.getClass();
            lvt_7_1_.field_216929_c = var10001 + 9;
            var10001 = lvt_8_1_.field_216929_c;
            this.font.getClass();
            lvt_8_1_.field_216929_c = var10001 + 9;
         }

      }
   }

   private void func_214223_a(EditBookScreen.Point p_214223_1_, EditBookScreen.Point p_214223_2_) {
      EditBookScreen.Point lvt_3_1_ = new EditBookScreen.Point(p_214223_1_.field_216928_b, p_214223_1_.field_216929_c);
      EditBookScreen.Point lvt_4_1_ = new EditBookScreen.Point(p_214223_2_.field_216928_b, p_214223_2_.field_216929_c);
      if (this.font.getBidiFlag()) {
         this.func_214227_a(lvt_3_1_);
         this.func_214227_a(lvt_4_1_);
         int lvt_5_1_ = lvt_4_1_.field_216928_b;
         lvt_4_1_.field_216928_b = lvt_3_1_.field_216928_b;
         lvt_3_1_.field_216928_b = lvt_5_1_;
      }

      this.func_214224_c(lvt_3_1_);
      this.func_214224_c(lvt_4_1_);
      Tessellator lvt_5_2_ = Tessellator.getInstance();
      BufferBuilder lvt_6_1_ = lvt_5_2_.getBuffer();
      RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      RenderSystem.disableTexture();
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      lvt_6_1_.begin(7, DefaultVertexFormats.POSITION);
      lvt_6_1_.func_225582_a_((double)lvt_3_1_.field_216928_b, (double)lvt_4_1_.field_216929_c, 0.0D).endVertex();
      lvt_6_1_.func_225582_a_((double)lvt_4_1_.field_216928_b, (double)lvt_4_1_.field_216929_c, 0.0D).endVertex();
      lvt_6_1_.func_225582_a_((double)lvt_4_1_.field_216928_b, (double)lvt_3_1_.field_216929_c, 0.0D).endVertex();
      lvt_6_1_.func_225582_a_((double)lvt_3_1_.field_216928_b, (double)lvt_3_1_.field_216929_c, 0.0D).endVertex();
      lvt_5_2_.draw();
      RenderSystem.disableColorLogicOp();
      RenderSystem.enableTexture();
   }

   private EditBookScreen.Point func_214194_c(String p_214194_1_, int p_214194_2_) {
      EditBookScreen.Point lvt_3_1_ = new EditBookScreen.Point();
      int lvt_4_1_ = 0;
      int lvt_5_1_ = 0;

      for(String lvt_6_1_ = p_214194_1_; !lvt_6_1_.isEmpty(); lvt_5_1_ = lvt_4_1_) {
         int lvt_7_1_ = this.func_214216_b(lvt_6_1_, 114);
         String lvt_8_2_;
         if (lvt_6_1_.length() <= lvt_7_1_) {
            lvt_8_2_ = lvt_6_1_.substring(0, Math.min(Math.max(p_214194_2_ - lvt_5_1_, 0), lvt_6_1_.length()));
            lvt_3_1_.field_216928_b = lvt_3_1_.field_216928_b + this.func_214225_l(lvt_8_2_);
            break;
         }

         lvt_8_2_ = lvt_6_1_.substring(0, lvt_7_1_);
         char lvt_9_1_ = lvt_6_1_.charAt(lvt_7_1_);
         boolean lvt_10_1_ = lvt_9_1_ == ' ' || lvt_9_1_ == '\n';
         lvt_6_1_ = TextFormatting.getFormatString(lvt_8_2_) + lvt_6_1_.substring(lvt_7_1_ + (lvt_10_1_ ? 1 : 0));
         lvt_4_1_ += lvt_8_2_.length() + (lvt_10_1_ ? 1 : 0);
         if (lvt_4_1_ - 1 >= p_214194_2_) {
            String lvt_11_1_ = lvt_8_2_.substring(0, Math.min(Math.max(p_214194_2_ - lvt_5_1_, 0), lvt_8_2_.length()));
            lvt_3_1_.field_216928_b = lvt_3_1_.field_216928_b + this.func_214225_l(lvt_11_1_);
            break;
         }

         int var10001 = lvt_3_1_.field_216929_c;
         this.font.getClass();
         lvt_3_1_.field_216929_c = var10001 + 9;
      }

      return lvt_3_1_;
   }

   private void func_214227_a(EditBookScreen.Point p_214227_1_) {
      if (this.font.getBidiFlag()) {
         p_214227_1_.field_216928_b = 114 - p_214227_1_.field_216928_b;
      }

   }

   private void func_214210_b(EditBookScreen.Point p_214210_1_) {
      p_214210_1_.field_216928_b = p_214210_1_.field_216928_b - (this.width - 192) / 2 - 36;
      p_214210_1_.field_216929_c = p_214210_1_.field_216929_c - 32;
   }

   private void func_214224_c(EditBookScreen.Point p_214224_1_) {
      p_214224_1_.field_216928_b = p_214224_1_.field_216928_b + (this.width - 192) / 2 + 36;
      p_214224_1_.field_216929_c = p_214224_1_.field_216929_c + 32;
   }

   private int func_214226_d(String p_214226_1_, int p_214226_2_) {
      if (p_214226_2_ < 0) {
         return 0;
      } else {
         float lvt_4_1_ = 0.0F;
         boolean lvt_5_1_ = false;
         String lvt_6_1_ = p_214226_1_ + " ";

         for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_.length(); ++lvt_7_1_) {
            char lvt_8_1_ = lvt_6_1_.charAt(lvt_7_1_);
            float lvt_9_1_ = this.font.getCharWidth(lvt_8_1_);
            if (lvt_8_1_ == 167 && lvt_7_1_ < lvt_6_1_.length() - 1) {
               ++lvt_7_1_;
               lvt_8_1_ = lvt_6_1_.charAt(lvt_7_1_);
               if (lvt_8_1_ != 'l' && lvt_8_1_ != 'L') {
                  if (lvt_8_1_ == 'r' || lvt_8_1_ == 'R') {
                     lvt_5_1_ = false;
                  }
               } else {
                  lvt_5_1_ = true;
               }

               lvt_9_1_ = 0.0F;
            }

            float lvt_3_1_ = lvt_4_1_;
            lvt_4_1_ += lvt_9_1_;
            if (lvt_5_1_ && lvt_9_1_ > 0.0F) {
               ++lvt_4_1_;
            }

            if ((float)p_214226_2_ >= lvt_3_1_ && (float)p_214226_2_ < lvt_4_1_) {
               return lvt_7_1_;
            }
         }

         if ((float)p_214226_2_ >= lvt_4_1_) {
            return lvt_6_1_.length() - 1;
         } else {
            return -1;
         }
      }
   }

   private int func_214203_a(String p_214203_1_, EditBookScreen.Point p_214203_2_) {
      this.font.getClass();
      int lvt_3_1_ = 16 * 9;
      if (p_214203_2_.field_216929_c > lvt_3_1_) {
         return -1;
      } else {
         int lvt_4_1_ = Integer.MIN_VALUE;
         this.font.getClass();
         int lvt_5_1_ = 9;
         int lvt_6_1_ = 0;

         for(String lvt_7_1_ = p_214203_1_; !lvt_7_1_.isEmpty() && lvt_4_1_ < lvt_3_1_; lvt_5_1_ += 9) {
            int lvt_8_1_ = this.func_214216_b(lvt_7_1_, 114);
            if (lvt_8_1_ < lvt_7_1_.length()) {
               String lvt_9_1_ = lvt_7_1_.substring(0, lvt_8_1_);
               if (p_214203_2_.field_216929_c >= lvt_4_1_ && p_214203_2_.field_216929_c < lvt_5_1_) {
                  int lvt_10_1_ = this.func_214226_d(lvt_9_1_, p_214203_2_.field_216928_b);
                  return lvt_10_1_ < 0 ? -1 : lvt_6_1_ + lvt_10_1_;
               }

               char lvt_10_2_ = lvt_7_1_.charAt(lvt_8_1_);
               boolean lvt_11_1_ = lvt_10_2_ == ' ' || lvt_10_2_ == '\n';
               lvt_7_1_ = TextFormatting.getFormatString(lvt_9_1_) + lvt_7_1_.substring(lvt_8_1_ + (lvt_11_1_ ? 1 : 0));
               lvt_6_1_ += lvt_9_1_.length() + (lvt_11_1_ ? 1 : 0);
            } else if (p_214203_2_.field_216929_c >= lvt_4_1_ && p_214203_2_.field_216929_c < lvt_5_1_) {
               int lvt_9_2_ = this.func_214226_d(lvt_7_1_, p_214203_2_.field_216928_b);
               return lvt_9_2_ < 0 ? -1 : lvt_6_1_ + lvt_9_2_;
            }

            lvt_4_1_ = lvt_5_1_;
            this.font.getClass();
         }

         return p_214203_1_.length();
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         long lvt_6_1_ = Util.milliTime();
         String lvt_8_1_ = this.func_214193_h();
         if (!lvt_8_1_.isEmpty()) {
            EditBookScreen.Point lvt_9_1_ = new EditBookScreen.Point((int)p_mouseClicked_1_, (int)p_mouseClicked_3_);
            this.func_214210_b(lvt_9_1_);
            this.func_214227_a(lvt_9_1_);
            int lvt_10_1_ = this.func_214203_a(lvt_8_1_, lvt_9_1_);
            if (lvt_10_1_ >= 0) {
               if (lvt_10_1_ == this.field_214243_l && lvt_6_1_ - this.field_214242_k < 250L) {
                  if (this.field_214241_j == this.field_214240_i) {
                     this.field_214241_j = this.font.func_216863_a(lvt_8_1_, -1, lvt_10_1_, false);
                     this.field_214240_i = this.font.func_216863_a(lvt_8_1_, 1, lvt_10_1_, false);
                  } else {
                     this.field_214241_j = 0;
                     this.field_214240_i = this.func_214193_h().length();
                  }
               } else {
                  this.field_214240_i = lvt_10_1_;
                  if (!Screen.hasShiftDown()) {
                     this.field_214241_j = this.field_214240_i;
                  }
               }
            }

            this.field_214243_l = lvt_10_1_;
         }

         this.field_214242_k = lvt_6_1_;
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (p_mouseDragged_5_ == 0 && this.field_214237_f >= 0 && this.field_214237_f < this.field_214238_g.size()) {
         String lvt_10_1_ = (String)this.field_214238_g.get(this.field_214237_f);
         EditBookScreen.Point lvt_11_1_ = new EditBookScreen.Point((int)p_mouseDragged_1_, (int)p_mouseDragged_3_);
         this.func_214210_b(lvt_11_1_);
         this.func_214227_a(lvt_11_1_);
         int lvt_12_1_ = this.func_214203_a(lvt_10_1_, lvt_11_1_);
         if (lvt_12_1_ >= 0) {
            this.field_214240_i = lvt_12_1_;
         }
      }

      return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }

   @OnlyIn(Dist.CLIENT)
   class Point {
      private int field_216928_b;
      private int field_216929_c;

      Point() {
      }

      Point(int p_i50636_2_, int p_i50636_3_) {
         this.field_216928_b = p_i50636_2_;
         this.field_216929_c = p_i50636_3_;
      }
   }
}
