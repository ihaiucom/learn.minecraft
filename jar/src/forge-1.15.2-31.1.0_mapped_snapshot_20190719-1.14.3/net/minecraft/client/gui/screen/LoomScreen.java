package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.LoomContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoomScreen extends ContainerScreen<LoomContainer> {
   private static final ResourceLocation field_214113_k = new ResourceLocation("textures/gui/container/loom.png");
   private static final int field_214114_l;
   private final ModelRenderer field_228188_m_;
   @Nullable
   private List<Pair<BannerPattern, DyeColor>> field_230155_n_;
   private ItemStack field_214119_q;
   private ItemStack field_214120_r;
   private ItemStack field_214121_s;
   private boolean field_214123_u;
   private boolean field_214124_v;
   private boolean field_214125_w;
   private float field_214126_x;
   private boolean field_214127_y;
   private int field_214128_z;

   public LoomScreen(LoomContainer p_i51081_1_, PlayerInventory p_i51081_2_, ITextComponent p_i51081_3_) {
      super(p_i51081_1_, p_i51081_2_, p_i51081_3_);
      this.field_214119_q = ItemStack.EMPTY;
      this.field_214120_r = ItemStack.EMPTY;
      this.field_214121_s = ItemStack.EMPTY;
      this.field_214128_z = 1;
      this.field_228188_m_ = BannerTileEntityRenderer.func_228836_a_();
      p_i51081_1_.func_217020_a(this::func_214111_b);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.font.drawString(this.title.getFormattedText(), 8.0F, 4.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      this.renderBackground();
      this.minecraft.getTextureManager().bindTexture(field_214113_k);
      int lvt_4_1_ = this.guiLeft;
      int lvt_5_1_ = this.guiTop;
      this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
      Slot lvt_6_1_ = ((LoomContainer)this.container).func_217024_f();
      Slot lvt_7_1_ = ((LoomContainer)this.container).func_217022_g();
      Slot lvt_8_1_ = ((LoomContainer)this.container).func_217025_h();
      Slot lvt_9_1_ = ((LoomContainer)this.container).func_217026_i();
      if (!lvt_6_1_.getHasStack()) {
         this.blit(lvt_4_1_ + lvt_6_1_.xPos, lvt_5_1_ + lvt_6_1_.yPos, this.xSize, 0, 16, 16);
      }

      if (!lvt_7_1_.getHasStack()) {
         this.blit(lvt_4_1_ + lvt_7_1_.xPos, lvt_5_1_ + lvt_7_1_.yPos, this.xSize + 16, 0, 16, 16);
      }

      if (!lvt_8_1_.getHasStack()) {
         this.blit(lvt_4_1_ + lvt_8_1_.xPos, lvt_5_1_ + lvt_8_1_.yPos, this.xSize + 32, 0, 16, 16);
      }

      int lvt_10_1_ = (int)(41.0F * this.field_214126_x);
      this.blit(lvt_4_1_ + 119, lvt_5_1_ + 13 + lvt_10_1_, 232 + (this.field_214123_u ? 0 : 12), 0, 12, 15);
      RenderHelper.func_227783_c_();
      if (this.field_230155_n_ != null && !this.field_214125_w) {
         IRenderTypeBuffer.Impl lvt_11_1_ = this.minecraft.func_228019_au_().func_228487_b_();
         MatrixStack lvt_12_1_ = new MatrixStack();
         lvt_12_1_.func_227861_a_((double)(lvt_4_1_ + 139), (double)(lvt_5_1_ + 52), 0.0D);
         lvt_12_1_.func_227862_a_(24.0F, -24.0F, 1.0F);
         lvt_12_1_.func_227861_a_(0.5D, 0.5D, 0.5D);
         float lvt_13_1_ = 0.6666667F;
         lvt_12_1_.func_227862_a_(0.6666667F, -0.6666667F, -0.6666667F);
         this.field_228188_m_.rotateAngleX = 0.0F;
         this.field_228188_m_.rotationPointY = -32.0F;
         BannerTileEntityRenderer.func_230180_a_(lvt_12_1_, lvt_11_1_, 15728880, OverlayTexture.field_229196_a_, this.field_228188_m_, ModelBakery.field_229315_f_, true, this.field_230155_n_);
         lvt_11_1_.func_228461_a_();
      } else if (this.field_214125_w) {
         this.blit(lvt_4_1_ + lvt_9_1_.xPos - 2, lvt_5_1_ + lvt_9_1_.yPos - 2, this.xSize, 17, 17, 16);
      }

      int lvt_11_2_;
      int lvt_12_2_;
      int lvt_13_2_;
      if (this.field_214123_u) {
         lvt_11_2_ = lvt_4_1_ + 60;
         lvt_12_2_ = lvt_5_1_ + 13;
         lvt_13_2_ = this.field_214128_z + 16;

         for(int lvt_14_1_ = this.field_214128_z; lvt_14_1_ < lvt_13_2_ && lvt_14_1_ < BannerPattern.field_222480_O - 5; ++lvt_14_1_) {
            int lvt_15_1_ = lvt_14_1_ - this.field_214128_z;
            int lvt_16_1_ = lvt_11_2_ + lvt_15_1_ % 4 * 14;
            int lvt_17_1_ = lvt_12_2_ + lvt_15_1_ / 4 * 14;
            this.minecraft.getTextureManager().bindTexture(field_214113_k);
            int lvt_18_1_ = this.ySize;
            if (lvt_14_1_ == ((LoomContainer)this.container).func_217023_e()) {
               lvt_18_1_ += 14;
            } else if (p_146976_2_ >= lvt_16_1_ && p_146976_3_ >= lvt_17_1_ && p_146976_2_ < lvt_16_1_ + 14 && p_146976_3_ < lvt_17_1_ + 14) {
               lvt_18_1_ += 28;
            }

            this.blit(lvt_16_1_, lvt_17_1_, 0, lvt_18_1_, 14, 14);
            this.func_228190_b_(lvt_14_1_, lvt_16_1_, lvt_17_1_);
         }
      } else if (this.field_214124_v) {
         lvt_11_2_ = lvt_4_1_ + 60;
         lvt_12_2_ = lvt_5_1_ + 13;
         this.minecraft.getTextureManager().bindTexture(field_214113_k);
         this.blit(lvt_11_2_, lvt_12_2_, 0, this.ySize, 14, 14);
         lvt_13_2_ = ((LoomContainer)this.container).func_217023_e();
         this.func_228190_b_(lvt_13_2_, lvt_11_2_, lvt_12_2_);
      }

      RenderHelper.func_227784_d_();
   }

   private void func_228190_b_(int p_228190_1_, int p_228190_2_, int p_228190_3_) {
      ItemStack lvt_4_1_ = new ItemStack(Items.GRAY_BANNER);
      CompoundNBT lvt_5_1_ = lvt_4_1_.getOrCreateChildTag("BlockEntityTag");
      ListNBT lvt_6_1_ = (new BannerPattern.Builder()).func_222477_a(BannerPattern.BASE, DyeColor.GRAY).func_222477_a(BannerPattern.values()[p_228190_1_], DyeColor.WHITE).func_222476_a();
      lvt_5_1_.put("Patterns", lvt_6_1_);
      MatrixStack lvt_7_1_ = new MatrixStack();
      lvt_7_1_.func_227860_a_();
      lvt_7_1_.func_227861_a_((double)((float)p_228190_2_ + 0.5F), (double)(p_228190_3_ + 16), 0.0D);
      lvt_7_1_.func_227862_a_(6.0F, -6.0F, 1.0F);
      lvt_7_1_.func_227861_a_(0.5D, 0.5D, 0.0D);
      lvt_7_1_.func_227861_a_(0.5D, 0.5D, 0.5D);
      float lvt_8_1_ = 0.6666667F;
      lvt_7_1_.func_227862_a_(0.6666667F, -0.6666667F, -0.6666667F);
      IRenderTypeBuffer.Impl lvt_9_1_ = this.minecraft.func_228019_au_().func_228487_b_();
      this.field_228188_m_.rotateAngleX = 0.0F;
      this.field_228188_m_.rotationPointY = -32.0F;
      List<Pair<BannerPattern, DyeColor>> lvt_10_1_ = BannerTileEntity.func_230138_a_(DyeColor.GRAY, BannerTileEntity.func_230139_a_(lvt_4_1_));
      BannerTileEntityRenderer.func_230180_a_(lvt_7_1_, lvt_9_1_, 15728880, OverlayTexture.field_229196_a_, this.field_228188_m_, ModelBakery.field_229315_f_, true, lvt_10_1_);
      lvt_7_1_.func_227865_b_();
      lvt_9_1_.func_228461_a_();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.field_214127_y = false;
      if (this.field_214123_u) {
         int lvt_6_1_ = this.guiLeft + 60;
         int lvt_7_1_ = this.guiTop + 13;
         int lvt_8_1_ = this.field_214128_z + 16;

         for(int lvt_9_1_ = this.field_214128_z; lvt_9_1_ < lvt_8_1_; ++lvt_9_1_) {
            int lvt_10_1_ = lvt_9_1_ - this.field_214128_z;
            double lvt_11_1_ = p_mouseClicked_1_ - (double)(lvt_6_1_ + lvt_10_1_ % 4 * 14);
            double lvt_13_1_ = p_mouseClicked_3_ - (double)(lvt_7_1_ + lvt_10_1_ / 4 * 14);
            if (lvt_11_1_ >= 0.0D && lvt_13_1_ >= 0.0D && lvt_11_1_ < 14.0D && lvt_13_1_ < 14.0D && ((LoomContainer)this.container).enchantItem(this.minecraft.player, lvt_9_1_)) {
               Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
               this.minecraft.playerController.sendEnchantPacket(((LoomContainer)this.container).windowId, lvt_9_1_);
               return true;
            }
         }

         lvt_6_1_ = this.guiLeft + 119;
         lvt_7_1_ = this.guiTop + 9;
         if (p_mouseClicked_1_ >= (double)lvt_6_1_ && p_mouseClicked_1_ < (double)(lvt_6_1_ + 12) && p_mouseClicked_3_ >= (double)lvt_7_1_ && p_mouseClicked_3_ < (double)(lvt_7_1_ + 56)) {
            this.field_214127_y = true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (this.field_214127_y && this.field_214123_u) {
         int lvt_10_1_ = this.guiTop + 13;
         int lvt_11_1_ = lvt_10_1_ + 56;
         this.field_214126_x = ((float)p_mouseDragged_3_ - (float)lvt_10_1_ - 7.5F) / ((float)(lvt_11_1_ - lvt_10_1_) - 15.0F);
         this.field_214126_x = MathHelper.clamp(this.field_214126_x, 0.0F, 1.0F);
         int lvt_12_1_ = field_214114_l - 4;
         int lvt_13_1_ = (int)((double)(this.field_214126_x * (float)lvt_12_1_) + 0.5D);
         if (lvt_13_1_ < 0) {
            lvt_13_1_ = 0;
         }

         this.field_214128_z = 1 + lvt_13_1_ * 4;
         return true;
      } else {
         return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (this.field_214123_u) {
         int lvt_7_1_ = field_214114_l - 4;
         this.field_214126_x = (float)((double)this.field_214126_x - p_mouseScrolled_5_ / (double)lvt_7_1_);
         this.field_214126_x = MathHelper.clamp(this.field_214126_x, 0.0F, 1.0F);
         this.field_214128_z = 1 + (int)((double)(this.field_214126_x * (float)lvt_7_1_) + 0.5D) * 4;
      }

      return true;
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      return p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
   }

   private void func_214111_b() {
      ItemStack lvt_1_1_ = ((LoomContainer)this.container).func_217026_i().getStack();
      if (lvt_1_1_.isEmpty()) {
         this.field_230155_n_ = null;
      } else {
         this.field_230155_n_ = BannerTileEntity.func_230138_a_(((BannerItem)lvt_1_1_.getItem()).getColor(), BannerTileEntity.func_230139_a_(lvt_1_1_));
      }

      ItemStack lvt_2_1_ = ((LoomContainer)this.container).func_217024_f().getStack();
      ItemStack lvt_3_1_ = ((LoomContainer)this.container).func_217022_g().getStack();
      ItemStack lvt_4_1_ = ((LoomContainer)this.container).func_217025_h().getStack();
      CompoundNBT lvt_5_1_ = lvt_2_1_.getOrCreateChildTag("BlockEntityTag");
      this.field_214125_w = lvt_5_1_.contains("Patterns", 9) && !lvt_2_1_.isEmpty() && lvt_5_1_.getList("Patterns", 10).size() >= 6;
      if (this.field_214125_w) {
         this.field_230155_n_ = null;
      }

      if (!ItemStack.areItemStacksEqual(lvt_2_1_, this.field_214119_q) || !ItemStack.areItemStacksEqual(lvt_3_1_, this.field_214120_r) || !ItemStack.areItemStacksEqual(lvt_4_1_, this.field_214121_s)) {
         this.field_214123_u = !lvt_2_1_.isEmpty() && !lvt_3_1_.isEmpty() && lvt_4_1_.isEmpty() && !this.field_214125_w;
         this.field_214124_v = !this.field_214125_w && !lvt_4_1_.isEmpty() && !lvt_2_1_.isEmpty() && !lvt_3_1_.isEmpty();
      }

      this.field_214119_q = lvt_2_1_.copy();
      this.field_214120_r = lvt_3_1_.copy();
      this.field_214121_s = lvt_4_1_.copy();
   }

   static {
      field_214114_l = (BannerPattern.field_222480_O - 5 - 1 + 4 - 1) / 4;
   }
}
