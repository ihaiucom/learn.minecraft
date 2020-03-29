package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public class BipedArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends ArmorLayer<T, M, A> {
   public BipedArmorLayer(IEntityRenderer<T, M> p_i50936_1_, A p_i50936_2_, A p_i50936_3_) {
      super(p_i50936_1_, p_i50936_2_, p_i50936_3_);
   }

   protected void setModelSlotVisible(A p_188359_1_, EquipmentSlotType p_188359_2_) {
      this.setModelVisible(p_188359_1_);
      switch(p_188359_2_) {
      case HEAD:
         p_188359_1_.bipedHead.showModel = true;
         p_188359_1_.bipedHeadwear.showModel = true;
         break;
      case CHEST:
         p_188359_1_.bipedBody.showModel = true;
         p_188359_1_.bipedRightArm.showModel = true;
         p_188359_1_.bipedLeftArm.showModel = true;
         break;
      case LEGS:
         p_188359_1_.bipedBody.showModel = true;
         p_188359_1_.bipedRightLeg.showModel = true;
         p_188359_1_.bipedLeftLeg.showModel = true;
         break;
      case FEET:
         p_188359_1_.bipedRightLeg.showModel = true;
         p_188359_1_.bipedLeftLeg.showModel = true;
      }

   }

   protected void setModelVisible(A p_177194_1_) {
      p_177194_1_.setVisible(false);
   }

   protected A getArmorModelHook(T p_getArmorModelHook_1_, ItemStack p_getArmorModelHook_2_, EquipmentSlotType p_getArmorModelHook_3_, A p_getArmorModelHook_4_) {
      return ForgeHooksClient.getArmorModel(p_getArmorModelHook_1_, p_getArmorModelHook_2_, p_getArmorModelHook_3_, p_getArmorModelHook_4_);
   }
}
