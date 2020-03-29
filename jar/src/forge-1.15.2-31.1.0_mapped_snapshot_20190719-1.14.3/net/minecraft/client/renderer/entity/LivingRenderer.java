package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class LivingRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements IEntityRenderer<T, M> {
   private static final Logger LOGGER = LogManager.getLogger();
   protected M entityModel;
   protected final List<LayerRenderer<T, M>> layerRenderers = Lists.newArrayList();

   public LivingRenderer(EntityRendererManager p_i50965_1_, M p_i50965_2_, float p_i50965_3_) {
      super(p_i50965_1_);
      this.entityModel = p_i50965_2_;
      this.shadowSize = p_i50965_3_;
   }

   public final boolean addLayer(LayerRenderer<T, M> p_177094_1_) {
      return this.layerRenderers.add(p_177094_1_);
   }

   public M getEntityModel() {
      return this.entityModel;
   }

   public void func_225623_a_(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      if (!MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(p_225623_1_, this, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_))) {
         p_225623_4_.func_227860_a_();
         this.entityModel.swingProgress = this.getSwingProgress(p_225623_1_, p_225623_3_);
         boolean shouldSit = p_225623_1_.isPassenger() && p_225623_1_.getRidingEntity() != null && p_225623_1_.getRidingEntity().shouldRiderSit();
         this.entityModel.isSitting = shouldSit;
         this.entityModel.isChild = p_225623_1_.isChild();
         float f = MathHelper.func_219805_h(p_225623_3_, p_225623_1_.prevRenderYawOffset, p_225623_1_.renderYawOffset);
         float f1 = MathHelper.func_219805_h(p_225623_3_, p_225623_1_.prevRotationYawHead, p_225623_1_.rotationYawHead);
         float f2 = f1 - f;
         float f7;
         if (shouldSit && p_225623_1_.getRidingEntity() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)p_225623_1_.getRidingEntity();
            f = MathHelper.func_219805_h(p_225623_3_, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
            f2 = f1 - f;
            f7 = MathHelper.wrapDegrees(f2);
            if (f7 < -85.0F) {
               f7 = -85.0F;
            }

            if (f7 >= 85.0F) {
               f7 = 85.0F;
            }

            f = f1 - f7;
            if (f7 * f7 > 2500.0F) {
               f += f7 * 0.2F;
            }

            f2 = f1 - f;
         }

         float f6 = MathHelper.lerp(p_225623_3_, p_225623_1_.prevRotationPitch, p_225623_1_.rotationPitch);
         float f8;
         if (p_225623_1_.getPose() == Pose.SLEEPING) {
            Direction direction = p_225623_1_.getBedDirection();
            if (direction != null) {
               f8 = p_225623_1_.getEyeHeight(Pose.STANDING) - 0.1F;
               p_225623_4_.func_227861_a_((double)((float)(-direction.getXOffset()) * f8), 0.0D, (double)((float)(-direction.getZOffset()) * f8));
            }
         }

         f7 = this.handleRotationFloat(p_225623_1_, p_225623_3_);
         this.func_225621_a_(p_225623_1_, p_225623_4_, f7, f, p_225623_3_);
         p_225623_4_.func_227862_a_(-1.0F, -1.0F, 1.0F);
         this.func_225620_a_(p_225623_1_, p_225623_4_, p_225623_3_);
         p_225623_4_.func_227861_a_(0.0D, -1.5010000467300415D, 0.0D);
         f8 = 0.0F;
         float f5 = 0.0F;
         if (!shouldSit && p_225623_1_.isAlive()) {
            f8 = MathHelper.lerp(p_225623_3_, p_225623_1_.prevLimbSwingAmount, p_225623_1_.limbSwingAmount);
            f5 = p_225623_1_.limbSwing - p_225623_1_.limbSwingAmount * (1.0F - p_225623_3_);
            if (p_225623_1_.isChild()) {
               f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
               f8 = 1.0F;
            }
         }

         this.entityModel.setLivingAnimations(p_225623_1_, f5, f8, p_225623_3_);
         this.entityModel.func_225597_a_(p_225623_1_, f5, f8, f7, f2, f6);
         boolean flag = this.func_225622_a_(p_225623_1_);
         boolean flag1 = !flag && !p_225623_1_.isInvisibleToPlayer(Minecraft.getInstance().player);
         RenderType rendertype = this.func_230042_a_(p_225623_1_, flag, flag1);
         if (rendertype != null) {
            IVertexBuilder ivertexbuilder = p_225623_5_.getBuffer(rendertype);
            int i = func_229117_c_(p_225623_1_, this.func_225625_b_(p_225623_1_, p_225623_3_));
            this.entityModel.func_225598_a_(p_225623_4_, ivertexbuilder, p_225623_6_, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
         }

         if (!p_225623_1_.isSpectator()) {
            Iterator var22 = this.layerRenderers.iterator();

            while(var22.hasNext()) {
               LayerRenderer<T, M> layerrenderer = (LayerRenderer)var22.next();
               layerrenderer.func_225628_a_(p_225623_4_, p_225623_5_, p_225623_6_, p_225623_1_, f5, f8, p_225623_3_, f7, f2, f6);
            }
         }

         p_225623_4_.func_227865_b_();
         super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
         MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(p_225623_1_, this, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_));
      }
   }

   @Nullable
   protected RenderType func_230042_a_(T p_230042_1_, boolean p_230042_2_, boolean p_230042_3_) {
      ResourceLocation resourcelocation = this.getEntityTexture(p_230042_1_);
      if (p_230042_3_) {
         return RenderType.func_228644_e_(resourcelocation);
      } else if (p_230042_2_) {
         return this.entityModel.func_228282_a_(resourcelocation);
      } else {
         return p_230042_1_.func_225510_bt_() ? RenderType.func_228654_j_(resourcelocation) : null;
      }
   }

   public static int func_229117_c_(LivingEntity p_229117_0_, float p_229117_1_) {
      return OverlayTexture.func_229201_a_(OverlayTexture.func_229199_a_(p_229117_1_), OverlayTexture.func_229202_a_(p_229117_0_.hurtTime > 0 || p_229117_0_.deathTime > 0));
   }

   protected boolean func_225622_a_(T p_225622_1_) {
      return !p_225622_1_.isInvisible();
   }

   private static float func_217765_a(Direction p_217765_0_) {
      switch(p_217765_0_) {
      case SOUTH:
         return 90.0F;
      case WEST:
         return 0.0F;
      case NORTH:
         return 270.0F;
      case EAST:
         return 180.0F;
      default:
         return 0.0F;
      }
   }

   protected void func_225621_a_(T p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      Pose pose = p_225621_1_.getPose();
      if (pose != Pose.SLEEPING) {
         p_225621_2_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F - p_225621_4_));
      }

      if (p_225621_1_.deathTime > 0) {
         float f = ((float)p_225621_1_.deathTime + p_225621_5_ - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         p_225621_2_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(f * this.getDeathMaxRotation(p_225621_1_)));
      } else if (p_225621_1_.isSpinAttacking()) {
         p_225621_2_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-90.0F - p_225621_1_.rotationPitch));
         p_225621_2_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(((float)p_225621_1_.ticksExisted + p_225621_5_) * -75.0F));
      } else if (pose == Pose.SLEEPING) {
         Direction direction = p_225621_1_.getBedDirection();
         float f1 = direction != null ? func_217765_a(direction) : p_225621_4_;
         p_225621_2_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(f1));
         p_225621_2_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(this.getDeathMaxRotation(p_225621_1_)));
         p_225621_2_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(270.0F));
      } else if (p_225621_1_.hasCustomName() || p_225621_1_ instanceof PlayerEntity) {
         String s = TextFormatting.getTextWithoutFormattingCodes(p_225621_1_.getName().getString());
         if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(p_225621_1_ instanceof PlayerEntity) || ((PlayerEntity)p_225621_1_).isWearing(PlayerModelPart.CAPE))) {
            p_225621_2_.func_227861_a_(0.0D, (double)(p_225621_1_.getHeight() + 0.1F), 0.0D);
            p_225621_2_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
         }
      }

   }

   protected float getSwingProgress(T p_77040_1_, float p_77040_2_) {
      return p_77040_1_.getSwingProgress(p_77040_2_);
   }

   protected float handleRotationFloat(T p_77044_1_, float p_77044_2_) {
      return (float)p_77044_1_.ticksExisted + p_77044_2_;
   }

   protected float getDeathMaxRotation(T p_77037_1_) {
      return 90.0F;
   }

   protected float func_225625_b_(T p_225625_1_, float p_225625_2_) {
      return 0.0F;
   }

   protected void func_225620_a_(T p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
   }

   protected boolean canRenderName(T p_177070_1_) {
      double d0 = this.renderManager.func_229099_b_(p_177070_1_);
      float f = p_177070_1_.func_226273_bm_() ? 32.0F : 64.0F;
      if (d0 >= (double)(f * f)) {
         return false;
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         ClientPlayerEntity clientplayerentity = minecraft.player;
         boolean flag = !p_177070_1_.isInvisibleToPlayer(clientplayerentity);
         if (p_177070_1_ != clientplayerentity) {
            Team team = p_177070_1_.getTeam();
            Team team1 = clientplayerentity.getTeam();
            if (team != null) {
               Team.Visible team$visible = team.getNameTagVisibility();
               switch(team$visible) {
               case ALWAYS:
                  return flag;
               case NEVER:
                  return false;
               case HIDE_FOR_OTHER_TEAMS:
                  return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
               case HIDE_FOR_OWN_TEAM:
                  return team1 == null ? flag : !team.isSameTeam(team1) && flag;
               default:
                  return true;
               }
            }
         }

         return Minecraft.isGuiEnabled() && p_177070_1_ != minecraft.getRenderViewEntity() && flag && !p_177070_1_.isBeingRidden();
      }
   }
}
