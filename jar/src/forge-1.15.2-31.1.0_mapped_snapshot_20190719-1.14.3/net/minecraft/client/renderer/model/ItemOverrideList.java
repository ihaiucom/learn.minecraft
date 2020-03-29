package net.minecraft.client.renderer.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemOverrideList {
   public static final ItemOverrideList EMPTY = new ItemOverrideList();
   private final List<ItemOverride> overrides;
   private final List<IBakedModel> overrideBakedModels;

   protected ItemOverrideList() {
      this.overrides = Lists.newArrayList();
      this.overrideBakedModels = Collections.emptyList();
   }

   /** @deprecated */
   @Deprecated
   public ItemOverrideList(ModelBakery p_i50984_1_, BlockModel p_i50984_2_, Function<ResourceLocation, IUnbakedModel> p_i50984_3_, List<ItemOverride> p_i50984_4_) {
      this(p_i50984_1_, p_i50984_2_, p_i50984_3_, p_i50984_1_.getSpriteMap()::func_229151_a_, p_i50984_4_);
   }

   public ItemOverrideList(ModelBakery p_i230089_1_, IUnbakedModel p_i230089_2_, Function<ResourceLocation, IUnbakedModel> p_i230089_3_, Function<Material, TextureAtlasSprite> p_i230089_4_, List<ItemOverride> p_i230089_5_) {
      this.overrides = Lists.newArrayList();
      this.overrideBakedModels = (List)p_i230089_5_.stream().map((p_lambda$new$0_4_) -> {
         IUnbakedModel iunbakedmodel = (IUnbakedModel)p_i230089_3_.apply(p_lambda$new$0_4_.getLocation());
         return Objects.equals(iunbakedmodel, p_i230089_2_) ? null : p_i230089_1_.getBakedModel(p_lambda$new$0_4_.getLocation(), ModelRotation.X0_Y0, p_i230089_4_);
      }).collect(Collectors.toList());
      Collections.reverse(this.overrideBakedModels);

      for(int i = p_i230089_5_.size() - 1; i >= 0; --i) {
         this.overrides.add(p_i230089_5_.get(i));
      }

   }

   @Nullable
   public IBakedModel getModelWithOverrides(IBakedModel p_209581_1_, ItemStack p_209581_2_, @Nullable World p_209581_3_, @Nullable LivingEntity p_209581_4_) {
      if (!this.overrides.isEmpty()) {
         for(int i = 0; i < this.overrides.size(); ++i) {
            ItemOverride itemoverride = (ItemOverride)this.overrides.get(i);
            if (itemoverride.matchesItemStack(p_209581_2_, p_209581_3_, p_209581_4_)) {
               IBakedModel ibakedmodel = (IBakedModel)this.overrideBakedModels.get(i);
               if (ibakedmodel == null) {
                  return p_209581_1_;
               }

               return ibakedmodel;
            }
         }
      }

      return p_209581_1_;
   }

   public ImmutableList<ItemOverride> getOverrides() {
      return ImmutableList.copyOf(this.overrides);
   }
}
