package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.entity.model.HorseArmorChestsModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestedHorseRenderer<T extends AbstractChestedHorseEntity> extends AbstractHorseRenderer<T, HorseArmorChestsModel<T>> {
   private static final Map<EntityType<?>, ResourceLocation> field_195635_a;

   public ChestedHorseRenderer(EntityRendererManager p_i48144_1_, float p_i48144_2_) {
      super(p_i48144_1_, new HorseArmorChestsModel(0.0F), p_i48144_2_);
   }

   public ResourceLocation getEntityTexture(T p_110775_1_) {
      return (ResourceLocation)field_195635_a.get(p_110775_1_.getType());
   }

   static {
      field_195635_a = Maps.newHashMap(ImmutableMap.of(EntityType.DONKEY, new ResourceLocation("textures/entity/horse/donkey.png"), EntityType.MULE, new ResourceLocation("textures/entity/horse/mule.png")));
   }
}
