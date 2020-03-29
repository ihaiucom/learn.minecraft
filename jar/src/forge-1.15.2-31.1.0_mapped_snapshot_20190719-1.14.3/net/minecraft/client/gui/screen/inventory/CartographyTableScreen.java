package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.CartographyContainer;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CartographyTableScreen extends ContainerScreen<CartographyContainer> {
   private static final ResourceLocation field_214109_k = new ResourceLocation("textures/gui/container/cartography_table.png");

   public CartographyTableScreen(CartographyContainer p_i51096_1_, PlayerInventory p_i51096_2_, ITextComponent p_i51096_3_) {
      super(p_i51096_1_, p_i51096_2_, p_i51096_3_);
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
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(field_214109_k);
      int lvt_4_1_ = this.guiLeft;
      int lvt_5_1_ = this.guiTop;
      this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
      Item lvt_6_1_ = ((CartographyContainer)this.container).getSlot(1).getStack().getItem();
      boolean lvt_7_1_ = lvt_6_1_ == Items.MAP;
      boolean lvt_8_1_ = lvt_6_1_ == Items.PAPER;
      boolean lvt_9_1_ = lvt_6_1_ == Items.GLASS_PANE;
      ItemStack lvt_10_1_ = ((CartographyContainer)this.container).getSlot(0).getStack();
      boolean lvt_12_1_ = false;
      MapData lvt_11_1_;
      if (lvt_10_1_.getItem() == Items.FILLED_MAP) {
         lvt_11_1_ = FilledMapItem.func_219994_a(lvt_10_1_, this.minecraft.world);
         if (lvt_11_1_ != null) {
            if (lvt_11_1_.locked) {
               lvt_12_1_ = true;
               if (lvt_8_1_ || lvt_9_1_) {
                  this.blit(lvt_4_1_ + 35, lvt_5_1_ + 31, this.xSize + 50, 132, 28, 21);
               }
            }

            if (lvt_8_1_ && lvt_11_1_.scale >= 4) {
               lvt_12_1_ = true;
               this.blit(lvt_4_1_ + 35, lvt_5_1_ + 31, this.xSize + 50, 132, 28, 21);
            }
         }
      } else {
         lvt_11_1_ = null;
      }

      this.func_214107_a(lvt_11_1_, lvt_7_1_, lvt_8_1_, lvt_9_1_, lvt_12_1_);
   }

   private void func_214107_a(@Nullable MapData p_214107_1_, boolean p_214107_2_, boolean p_214107_3_, boolean p_214107_4_, boolean p_214107_5_) {
      int lvt_6_1_ = this.guiLeft;
      int lvt_7_1_ = this.guiTop;
      if (p_214107_3_ && !p_214107_5_) {
         this.blit(lvt_6_1_ + 67, lvt_7_1_ + 13, this.xSize, 66, 66, 66);
         this.func_214108_a(p_214107_1_, lvt_6_1_ + 85, lvt_7_1_ + 31, 0.226F);
      } else if (p_214107_2_) {
         this.blit(lvt_6_1_ + 67 + 16, lvt_7_1_ + 13, this.xSize, 132, 50, 66);
         this.func_214108_a(p_214107_1_, lvt_6_1_ + 86, lvt_7_1_ + 16, 0.34F);
         this.minecraft.getTextureManager().bindTexture(field_214109_k);
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 1.0F);
         this.blit(lvt_6_1_ + 67, lvt_7_1_ + 13 + 16, this.xSize, 132, 50, 66);
         this.func_214108_a(p_214107_1_, lvt_6_1_ + 70, lvt_7_1_ + 32, 0.34F);
         RenderSystem.popMatrix();
      } else if (p_214107_4_) {
         this.blit(lvt_6_1_ + 67, lvt_7_1_ + 13, this.xSize, 0, 66, 66);
         this.func_214108_a(p_214107_1_, lvt_6_1_ + 71, lvt_7_1_ + 17, 0.45F);
         this.minecraft.getTextureManager().bindTexture(field_214109_k);
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 1.0F);
         this.blit(lvt_6_1_ + 66, lvt_7_1_ + 12, 0, this.ySize, 66, 66);
         RenderSystem.popMatrix();
      } else {
         this.blit(lvt_6_1_ + 67, lvt_7_1_ + 13, this.xSize, 0, 66, 66);
         this.func_214108_a(p_214107_1_, lvt_6_1_ + 71, lvt_7_1_ + 17, 0.45F);
      }

   }

   private void func_214108_a(@Nullable MapData p_214108_1_, int p_214108_2_, int p_214108_3_, float p_214108_4_) {
      if (p_214108_1_ != null) {
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)p_214108_2_, (float)p_214108_3_, 1.0F);
         RenderSystem.scalef(p_214108_4_, p_214108_4_, 1.0F);
         IRenderTypeBuffer.Impl lvt_5_1_ = IRenderTypeBuffer.func_228455_a_(Tessellator.getInstance().getBuffer());
         this.minecraft.gameRenderer.getMapItemRenderer().func_228086_a_(new MatrixStack(), lvt_5_1_, p_214108_1_, true, 15728880);
         lvt_5_1_.func_228461_a_();
         RenderSystem.popMatrix();
      }

   }
}
