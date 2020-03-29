package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Map;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.PandaHeldItemLayer;
import net.minecraft.client.renderer.entity.model.PandaModel;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaRenderer extends MobRenderer<PandaEntity, PandaModel<PandaEntity>> {
   private static final Map<PandaEntity.Type, ResourceLocation> field_217777_a = (Map)Util.make(Maps.newEnumMap(PandaEntity.Type.class), (p_217776_0_) -> {
      p_217776_0_.put(PandaEntity.Type.NORMAL, new ResourceLocation("textures/entity/panda/panda.png"));
      p_217776_0_.put(PandaEntity.Type.LAZY, new ResourceLocation("textures/entity/panda/lazy_panda.png"));
      p_217776_0_.put(PandaEntity.Type.WORRIED, new ResourceLocation("textures/entity/panda/worried_panda.png"));
      p_217776_0_.put(PandaEntity.Type.PLAYFUL, new ResourceLocation("textures/entity/panda/playful_panda.png"));
      p_217776_0_.put(PandaEntity.Type.BROWN, new ResourceLocation("textures/entity/panda/brown_panda.png"));
      p_217776_0_.put(PandaEntity.Type.WEAK, new ResourceLocation("textures/entity/panda/weak_panda.png"));
      p_217776_0_.put(PandaEntity.Type.AGGRESSIVE, new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
   });

   public PandaRenderer(EntityRendererManager p_i50960_1_) {
      super(p_i50960_1_, new PandaModel(9, 0.0F), 0.9F);
      this.addLayer(new PandaHeldItemLayer(this));
   }

   public ResourceLocation getEntityTexture(PandaEntity p_110775_1_) {
      return (ResourceLocation)field_217777_a.getOrDefault(p_110775_1_.func_213590_ei(), field_217777_a.get(PandaEntity.Type.NORMAL));
   }

   protected void func_225621_a_(PandaEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      super.func_225621_a_(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
      float lvt_8_2_;
      if (p_225621_1_.field_213608_bz > 0) {
         int lvt_6_1_ = p_225621_1_.field_213608_bz;
         int lvt_7_1_ = lvt_6_1_ + 1;
         lvt_8_2_ = 7.0F;
         float lvt_9_1_ = p_225621_1_.isChild() ? 0.3F : 0.8F;
         float lvt_10_4_;
         float lvt_11_4_;
         float lvt_12_4_;
         if (lvt_6_1_ < 8) {
            lvt_11_4_ = (float)(90 * lvt_6_1_) / 7.0F;
            lvt_12_4_ = (float)(90 * lvt_7_1_) / 7.0F;
            lvt_10_4_ = this.func_217775_a(lvt_11_4_, lvt_12_4_, lvt_7_1_, p_225621_5_, 8.0F);
            p_225621_2_.func_227861_a_(0.0D, (double)((lvt_9_1_ + 0.2F) * (lvt_10_4_ / 90.0F)), 0.0D);
            p_225621_2_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-lvt_10_4_));
         } else {
            float lvt_13_3_;
            if (lvt_6_1_ < 16) {
               lvt_11_4_ = ((float)lvt_6_1_ - 8.0F) / 7.0F;
               lvt_12_4_ = 90.0F + 90.0F * lvt_11_4_;
               lvt_13_3_ = 90.0F + 90.0F * ((float)lvt_7_1_ - 8.0F) / 7.0F;
               lvt_10_4_ = this.func_217775_a(lvt_12_4_, lvt_13_3_, lvt_7_1_, p_225621_5_, 16.0F);
               p_225621_2_.func_227861_a_(0.0D, (double)(lvt_9_1_ + 0.2F + (lvt_9_1_ - 0.2F) * (lvt_10_4_ - 90.0F) / 90.0F), 0.0D);
               p_225621_2_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-lvt_10_4_));
            } else if ((float)lvt_6_1_ < 24.0F) {
               lvt_11_4_ = ((float)lvt_6_1_ - 16.0F) / 7.0F;
               lvt_12_4_ = 180.0F + 90.0F * lvt_11_4_;
               lvt_13_3_ = 180.0F + 90.0F * ((float)lvt_7_1_ - 16.0F) / 7.0F;
               lvt_10_4_ = this.func_217775_a(lvt_12_4_, lvt_13_3_, lvt_7_1_, p_225621_5_, 24.0F);
               p_225621_2_.func_227861_a_(0.0D, (double)(lvt_9_1_ + lvt_9_1_ * (270.0F - lvt_10_4_) / 90.0F), 0.0D);
               p_225621_2_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-lvt_10_4_));
            } else if (lvt_6_1_ < 32) {
               lvt_11_4_ = ((float)lvt_6_1_ - 24.0F) / 7.0F;
               lvt_12_4_ = 270.0F + 90.0F * lvt_11_4_;
               lvt_13_3_ = 270.0F + 90.0F * ((float)lvt_7_1_ - 24.0F) / 7.0F;
               lvt_10_4_ = this.func_217775_a(lvt_12_4_, lvt_13_3_, lvt_7_1_, p_225621_5_, 32.0F);
               p_225621_2_.func_227861_a_(0.0D, (double)(lvt_9_1_ * ((360.0F - lvt_10_4_) / 90.0F)), 0.0D);
               p_225621_2_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-lvt_10_4_));
            }
         }
      }

      float lvt_6_2_ = p_225621_1_.func_213561_v(p_225621_5_);
      float lvt_7_3_;
      if (lvt_6_2_ > 0.0F) {
         p_225621_2_.func_227861_a_(0.0D, (double)(0.8F * lvt_6_2_), 0.0D);
         p_225621_2_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(MathHelper.lerp(lvt_6_2_, p_225621_1_.rotationPitch, p_225621_1_.rotationPitch + 90.0F)));
         p_225621_2_.func_227861_a_(0.0D, (double)(-1.0F * lvt_6_2_), 0.0D);
         if (p_225621_1_.func_213566_eo()) {
            lvt_7_3_ = (float)(Math.cos((double)p_225621_1_.ticksExisted * 1.25D) * 3.141592653589793D * 0.05000000074505806D);
            p_225621_2_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_7_3_));
            if (p_225621_1_.isChild()) {
               p_225621_2_.func_227861_a_(0.0D, 0.800000011920929D, 0.550000011920929D);
            }
         }
      }

      lvt_7_3_ = p_225621_1_.func_213583_w(p_225621_5_);
      if (lvt_7_3_ > 0.0F) {
         lvt_8_2_ = p_225621_1_.isChild() ? 0.5F : 1.3F;
         p_225621_2_.func_227861_a_(0.0D, (double)(lvt_8_2_ * lvt_7_3_), 0.0D);
         p_225621_2_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(MathHelper.lerp(lvt_7_3_, p_225621_1_.rotationPitch, p_225621_1_.rotationPitch + 180.0F)));
      }

   }

   private float func_217775_a(float p_217775_1_, float p_217775_2_, int p_217775_3_, float p_217775_4_, float p_217775_5_) {
      return (float)p_217775_3_ < p_217775_5_ ? MathHelper.lerp(p_217775_4_, p_217775_1_, p_217775_2_) : p_217775_1_;
   }
}
