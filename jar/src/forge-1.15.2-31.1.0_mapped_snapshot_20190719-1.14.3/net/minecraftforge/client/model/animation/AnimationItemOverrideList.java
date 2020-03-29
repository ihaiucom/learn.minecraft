package net.minecraftforge.client.model.animation;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.common.model.animation.CapabilityAnimation;

public final class AnimationItemOverrideList extends ItemOverrideList {
   private final ModelBakery bakery;
   private final IUnbakedModel model;
   private final ResourceLocation modelLoc;
   private final IModelTransform state;
   private final Function<Material, TextureAtlasSprite> bakedTextureGetter;

   public AnimationItemOverrideList(ModelBakery bakery, IUnbakedModel model, ResourceLocation modelLoc, IModelTransform state, Function<Material, TextureAtlasSprite> bakedTextureGetter, ItemOverrideList overrides) {
      this(bakery, model, modelLoc, state, bakedTextureGetter, (List)overrides.getOverrides().reverse());
   }

   public AnimationItemOverrideList(ModelBakery bakery, IUnbakedModel model, ResourceLocation modelLoc, IModelTransform state, Function<Material, TextureAtlasSprite> bakedTextureGetter, List<ItemOverride> overrides) {
      super(bakery, model, ModelLoader.defaultModelGetter(), bakedTextureGetter, overrides);
      this.bakery = bakery;
      this.model = model;
      this.modelLoc = modelLoc;
      this.state = state;
      this.bakedTextureGetter = bakedTextureGetter;
   }

   public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
      return (IBakedModel)stack.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, (Direction)null).map((asm) -> {
         if (world == null && entity != null) {
            World var3 = entity.world;
         }

         if (world == null) {
            ClientWorld var4 = Minecraft.getInstance().world;
         }

         return (IModelTransform)asm.apply(Animation.getWorldTime(world, Animation.getPartialTickTime())).getLeft();
      }).map((state) -> {
         return this.model.func_225613_a_(this.bakery, this.bakedTextureGetter, new ModelTransformComposition(state, this.state), this.modelLoc);
      }).orElseGet(() -> {
         return super.getModelWithOverrides(originalModel, stack, world, entity);
      });
   }
}
