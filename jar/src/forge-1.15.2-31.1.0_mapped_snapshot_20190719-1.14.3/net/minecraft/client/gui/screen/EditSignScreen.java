package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditSignScreen extends Screen {
   private final SignTileEntityRenderer.SignModel field_228191_a_ = new SignTileEntityRenderer.SignModel();
   private final SignTileEntity tileSign;
   private int updateCounter;
   private int editLine;
   private TextInputUtil field_214267_d;

   public EditSignScreen(SignTileEntity p_i1097_1_) {
      super(new TranslationTextComponent("sign.edit", new Object[0]));
      this.tileSign = p_i1097_1_;
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, I18n.format("gui.done"), (p_214266_1_) -> {
         this.close();
      }));
      this.tileSign.setEditable(false);
      this.field_214267_d = new TextInputUtil(this.minecraft, () -> {
         return this.tileSign.getText(this.editLine).getString();
      }, (p_214265_1_) -> {
         this.tileSign.setText(this.editLine, new StringTextComponent(p_214265_1_));
      }, 90);
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
      ClientPlayNetHandler lvt_1_1_ = this.minecraft.getConnection();
      if (lvt_1_1_ != null) {
         lvt_1_1_.sendPacket(new CUpdateSignPacket(this.tileSign.getPos(), this.tileSign.getText(0), this.tileSign.getText(1), this.tileSign.getText(2), this.tileSign.getText(3)));
      }

      this.tileSign.setEditable(true);
   }

   public void tick() {
      ++this.updateCounter;
      if (!this.tileSign.getType().isValidBlock(this.tileSign.getBlockState().getBlock())) {
         this.close();
      }

   }

   private void close() {
      this.tileSign.markDirty();
      this.minecraft.displayGuiScreen((Screen)null);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      this.field_214267_d.func_216894_a(p_charTyped_1_);
      return true;
   }

   public void onClose() {
      this.close();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 265) {
         this.editLine = this.editLine - 1 & 3;
         this.field_214267_d.func_216899_b();
         return true;
      } else if (p_keyPressed_1_ != 264 && p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return this.field_214267_d.func_216897_a(p_keyPressed_1_) ? true : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      } else {
         this.editLine = this.editLine + 1 & 3;
         this.field_214267_d.func_216899_b();
         return true;
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      RenderHelper.func_227783_c_();
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
      MatrixStack lvt_4_1_ = new MatrixStack();
      lvt_4_1_.func_227860_a_();
      lvt_4_1_.func_227861_a_((double)(this.width / 2), 0.0D, 50.0D);
      float lvt_5_1_ = 93.75F;
      lvt_4_1_.func_227862_a_(93.75F, -93.75F, 93.75F);
      lvt_4_1_.func_227861_a_(0.0D, -1.3125D, 0.0D);
      BlockState lvt_6_1_ = this.tileSign.getBlockState();
      boolean lvt_7_1_ = lvt_6_1_.getBlock() instanceof StandingSignBlock;
      if (!lvt_7_1_) {
         lvt_4_1_.func_227861_a_(0.0D, -0.3125D, 0.0D);
      }

      boolean lvt_8_1_ = this.updateCounter / 6 % 2 == 0;
      float lvt_9_1_ = 0.6666667F;
      lvt_4_1_.func_227860_a_();
      lvt_4_1_.func_227862_a_(0.6666667F, -0.6666667F, -0.6666667F);
      IRenderTypeBuffer.Impl lvt_10_1_ = this.minecraft.func_228019_au_().func_228487_b_();
      Material lvt_11_1_ = SignTileEntityRenderer.func_228877_a_(lvt_6_1_.getBlock());
      SignTileEntityRenderer.SignModel var10002 = this.field_228191_a_;
      var10002.getClass();
      IVertexBuilder lvt_12_1_ = lvt_11_1_.func_229311_a_(lvt_10_1_, var10002::func_228282_a_);
      this.field_228191_a_.field_78166_a.func_228308_a_(lvt_4_1_, lvt_12_1_, 15728880, OverlayTexture.field_229196_a_);
      if (lvt_7_1_) {
         this.field_228191_a_.field_78165_b.func_228308_a_(lvt_4_1_, lvt_12_1_, 15728880, OverlayTexture.field_229196_a_);
      }

      lvt_4_1_.func_227865_b_();
      float lvt_13_1_ = 0.010416667F;
      lvt_4_1_.func_227861_a_(0.0D, 0.3333333432674408D, 0.046666666865348816D);
      lvt_4_1_.func_227862_a_(0.010416667F, -0.010416667F, 0.010416667F);
      int lvt_14_1_ = this.tileSign.getTextColor().func_218388_g();
      String[] lvt_15_1_ = new String[4];

      for(int lvt_16_1_ = 0; lvt_16_1_ < lvt_15_1_.length; ++lvt_16_1_) {
         lvt_15_1_[lvt_16_1_] = this.tileSign.getRenderText(lvt_16_1_, (p_228192_1_) -> {
            List<ITextComponent> lvt_2_1_ = RenderComponentsUtil.splitText(p_228192_1_, 90, this.minecraft.fontRenderer, false, true);
            return lvt_2_1_.isEmpty() ? "" : ((ITextComponent)lvt_2_1_.get(0)).getFormattedText();
         });
      }

      Matrix4f lvt_16_2_ = lvt_4_1_.func_227866_c_().func_227870_a_();
      int lvt_17_1_ = this.field_214267_d.func_216896_c();
      int lvt_18_1_ = this.field_214267_d.func_216898_d();
      int lvt_19_1_ = this.minecraft.fontRenderer.getBidiFlag() ? -1 : 1;
      int lvt_20_1_ = this.editLine * 10 - this.tileSign.signText.length * 5;

      int lvt_21_2_;
      String lvt_22_2_;
      int lvt_24_2_;
      int lvt_25_2_;
      for(lvt_21_2_ = 0; lvt_21_2_ < lvt_15_1_.length; ++lvt_21_2_) {
         lvt_22_2_ = lvt_15_1_[lvt_21_2_];
         if (lvt_22_2_ != null) {
            float lvt_23_1_ = (float)(-this.minecraft.fontRenderer.getStringWidth(lvt_22_2_) / 2);
            this.minecraft.fontRenderer.func_228079_a_(lvt_22_2_, lvt_23_1_, (float)(lvt_21_2_ * 10 - this.tileSign.signText.length * 5), lvt_14_1_, false, lvt_16_2_, lvt_10_1_, false, 0, 15728880);
            if (lvt_21_2_ == this.editLine && lvt_17_1_ >= 0 && lvt_8_1_) {
               lvt_24_2_ = this.minecraft.fontRenderer.getStringWidth(lvt_22_2_.substring(0, Math.max(Math.min(lvt_17_1_, lvt_22_2_.length()), 0)));
               lvt_25_2_ = (lvt_24_2_ - this.minecraft.fontRenderer.getStringWidth(lvt_22_2_) / 2) * lvt_19_1_;
               if (lvt_17_1_ >= lvt_22_2_.length()) {
                  this.minecraft.fontRenderer.func_228079_a_("_", (float)lvt_25_2_, (float)lvt_20_1_, lvt_14_1_, false, lvt_16_2_, lvt_10_1_, false, 0, 15728880);
               }
            }
         }
      }

      lvt_10_1_.func_228461_a_();

      for(lvt_21_2_ = 0; lvt_21_2_ < lvt_15_1_.length; ++lvt_21_2_) {
         lvt_22_2_ = lvt_15_1_[lvt_21_2_];
         if (lvt_22_2_ != null && lvt_21_2_ == this.editLine && lvt_17_1_ >= 0) {
            int lvt_23_2_ = this.minecraft.fontRenderer.getStringWidth(lvt_22_2_.substring(0, Math.max(Math.min(lvt_17_1_, lvt_22_2_.length()), 0)));
            lvt_24_2_ = (lvt_23_2_ - this.minecraft.fontRenderer.getStringWidth(lvt_22_2_) / 2) * lvt_19_1_;
            if (lvt_8_1_ && lvt_17_1_ < lvt_22_2_.length()) {
               int var34 = lvt_20_1_ - 1;
               int var10003 = lvt_24_2_ + 1;
               this.minecraft.fontRenderer.getClass();
               fill(lvt_16_2_, lvt_24_2_, var34, var10003, lvt_20_1_ + 9, -16777216 | lvt_14_1_);
            }

            if (lvt_18_1_ != lvt_17_1_) {
               lvt_25_2_ = Math.min(lvt_17_1_, lvt_18_1_);
               int lvt_26_1_ = Math.max(lvt_17_1_, lvt_18_1_);
               int lvt_27_1_ = (this.minecraft.fontRenderer.getStringWidth(lvt_22_2_.substring(0, lvt_25_2_)) - this.minecraft.fontRenderer.getStringWidth(lvt_22_2_) / 2) * lvt_19_1_;
               int lvt_28_1_ = (this.minecraft.fontRenderer.getStringWidth(lvt_22_2_.substring(0, lvt_26_1_)) - this.minecraft.fontRenderer.getStringWidth(lvt_22_2_) / 2) * lvt_19_1_;
               int lvt_29_1_ = Math.min(lvt_27_1_, lvt_28_1_);
               int lvt_30_1_ = Math.max(lvt_27_1_, lvt_28_1_);
               Tessellator lvt_31_1_ = Tessellator.getInstance();
               BufferBuilder lvt_32_1_ = lvt_31_1_.getBuffer();
               RenderSystem.disableTexture();
               RenderSystem.enableColorLogicOp();
               RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
               lvt_32_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
               float var35 = (float)lvt_29_1_;
               this.minecraft.fontRenderer.getClass();
               lvt_32_1_.func_227888_a_(lvt_16_2_, var35, (float)(lvt_20_1_ + 9), 0.0F).func_225586_a_(0, 0, 255, 255).endVertex();
               var35 = (float)lvt_30_1_;
               this.minecraft.fontRenderer.getClass();
               lvt_32_1_.func_227888_a_(lvt_16_2_, var35, (float)(lvt_20_1_ + 9), 0.0F).func_225586_a_(0, 0, 255, 255).endVertex();
               lvt_32_1_.func_227888_a_(lvt_16_2_, (float)lvt_30_1_, (float)lvt_20_1_, 0.0F).func_225586_a_(0, 0, 255, 255).endVertex();
               lvt_32_1_.func_227888_a_(lvt_16_2_, (float)lvt_29_1_, (float)lvt_20_1_, 0.0F).func_225586_a_(0, 0, 255, 255).endVertex();
               lvt_32_1_.finishDrawing();
               WorldVertexBufferUploader.draw(lvt_32_1_);
               RenderSystem.disableColorLogicOp();
               RenderSystem.enableTexture();
            }
         }
      }

      lvt_4_1_.func_227865_b_();
      RenderHelper.func_227784_d_();
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
