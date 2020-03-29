package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DynamicBucketModel implements IModelGeometry<DynamicBucketModel> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("forge", "dynbucket"), "inventory");
   private static final float NORTH_Z_COVER = 0.4685F;
   private static final float SOUTH_Z_COVER = 0.5315F;
   private static final float NORTH_Z_FLUID = 0.468625F;
   private static final float SOUTH_Z_FLUID = 0.531375F;
   @Nonnull
   private final Fluid fluid;
   private final boolean flipGas;
   private final boolean tint;
   private final boolean coverIsMask;

   public DynamicBucketModel(Fluid fluid, boolean flipGas, boolean tint, boolean coverIsMask) {
      this.fluid = fluid;
      this.flipGas = flipGas;
      this.tint = tint;
      this.coverIsMask = coverIsMask;
   }

   public DynamicBucketModel withFluid(Fluid newFluid) {
      return new DynamicBucketModel(newFluid, this.flipGas, this.tint, this.coverIsMask);
   }

   public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
      Material particleLocation = owner.resolveTexture("particle");
      if (MissingTextureSprite.getLocation().equals(particleLocation.func_229313_b_())) {
         particleLocation = null;
      }

      Material baseLocation = owner.resolveTexture("base");
      if (MissingTextureSprite.getLocation().equals(baseLocation.func_229313_b_())) {
         baseLocation = null;
      }

      Material fluidMaskLocation = owner.resolveTexture("fluid");
      if (MissingTextureSprite.getLocation().equals(fluidMaskLocation.func_229313_b_())) {
         fluidMaskLocation = null;
      }

      Material coverLocation = owner.resolveTexture("cover");
      if (!MissingTextureSprite.getLocation().equals(coverLocation.func_229313_b_())) {
         coverLocation = null;
      }

      IModelTransform transformsFromModel = owner.getCombinedTransform();
      ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transformMap = transformsFromModel != null ? PerspectiveMapWrapper.getTransforms((IModelTransform)(new ModelTransformComposition(transformsFromModel, (IModelTransform)modelTransform))) : PerspectiveMapWrapper.getTransforms((IModelTransform)modelTransform);
      TextureAtlasSprite particleSprite = particleLocation != null ? (TextureAtlasSprite)spriteGetter.apply(particleLocation) : null;
      if (this.flipGas && this.fluid != Fluids.EMPTY && this.fluid.getAttributes().isLighterThanAir()) {
         modelTransform = new ModelTransformComposition((IModelTransform)modelTransform, new SimpleModelTransform(new TransformationMatrix((Vector3f)null, new Quaternion(0.0F, 0.0F, 1.0F, 0.0F), (Vector3f)null, (Quaternion)null)));
      }

      TransformationMatrix transform = ((IModelTransform)modelTransform).func_225615_b_();
      TextureAtlasSprite fluidSprite = this.fluid != Fluids.EMPTY ? (TextureAtlasSprite)spriteGetter.apply(ForgeHooksClient.getBlockMaterial(this.fluid.getAttributes().getStillTexture())) : null;
      if (particleSprite == null) {
         particleSprite = fluidSprite;
      }

      Builder<BakedQuad> builder = ImmutableList.builder();
      if (baseLocation != null) {
         builder.addAll(ItemLayerModel.getQuadsForSprites(ImmutableList.of(baseLocation), transform, spriteGetter));
      }

      TextureAtlasSprite coverSprite;
      if (fluidMaskLocation != null && fluidSprite != null) {
         coverSprite = (TextureAtlasSprite)spriteGetter.apply(fluidMaskLocation);
         if (coverSprite != null) {
            builder.addAll(ItemTextureQuadConverter.convertTexture(transform, coverSprite, fluidSprite, 0.468625F, Direction.NORTH, this.tint ? this.fluid.getAttributes().getColor() : -1, 1));
            builder.addAll(ItemTextureQuadConverter.convertTexture(transform, coverSprite, fluidSprite, 0.531375F, Direction.SOUTH, this.tint ? this.fluid.getAttributes().getColor() : -1, 1));
         }
      }

      if (coverLocation != null && (!this.coverIsMask || baseLocation != null)) {
         coverSprite = (TextureAtlasSprite)spriteGetter.apply(coverLocation);
         if (coverSprite != null) {
            if (this.coverIsMask) {
               TextureAtlasSprite baseSprite = (TextureAtlasSprite)spriteGetter.apply(baseLocation);
               builder.addAll(ItemTextureQuadConverter.convertTexture(transform, coverSprite, baseSprite, 0.4685F, Direction.NORTH, -1, 1));
               builder.addAll(ItemTextureQuadConverter.convertTexture(transform, coverSprite, baseSprite, 0.5315F, Direction.SOUTH, -1, 1));
            } else {
               builder.add(ItemTextureQuadConverter.genQuad(transform, 0.0F, 0.0F, 16.0F, 16.0F, 0.4685F, coverSprite, Direction.NORTH, -1, 2));
               builder.add(ItemTextureQuadConverter.genQuad(transform, 0.0F, 0.0F, 16.0F, 16.0F, 0.5315F, coverSprite, Direction.SOUTH, -1, 2));
               if (particleSprite == null) {
                  particleSprite = coverSprite;
               }
            }
         }
      }

      return new DynamicBucketModel.BakedModel(bakery, owner, this, builder.build(), particleSprite, Maps.immutableEnumMap(transformMap), Maps.newHashMap(), transform.isIdentity(), (IModelTransform)modelTransform, owner.isSideLit());
   }

   public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
      Set<Material> texs = Sets.newHashSet();
      texs.add(owner.resolveTexture("particle"));
      texs.add(owner.resolveTexture("base"));
      texs.add(owner.resolveTexture("fluid"));
      texs.add(owner.resolveTexture("cover"));
      return texs;
   }

   private static final class BakedModel extends BakedItemModel {
      private final IModelConfiguration owner;
      private final DynamicBucketModel parent;
      private final Map<String, IBakedModel> cache;
      private final IModelTransform originalTransform;
      private final boolean isSideLit;

      BakedModel(ModelBakery bakery, IModelConfiguration owner, DynamicBucketModel parent, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms, Map<String, IBakedModel> cache, boolean untransformed, IModelTransform originalTransform, boolean isSideLit) {
         super(quads, particle, transforms, new DynamicBucketModel.ContainedFluidOverrideHandler(bakery), untransformed, isSideLit);
         this.owner = owner;
         this.parent = parent;
         this.cache = cache;
         this.originalTransform = originalTransform;
         this.isSideLit = isSideLit;
      }
   }

   private static final class ContainedFluidOverrideHandler extends ItemOverrideList {
      private final ModelBakery bakery;

      private ContainedFluidOverrideHandler(ModelBakery bakery) {
         this.bakery = bakery;
      }

      public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
         return (IBakedModel)FluidUtil.getFluidContained(stack).map((fluidStack) -> {
            DynamicBucketModel.BakedModel model = (DynamicBucketModel.BakedModel)originalModel;
            Fluid fluid = fluidStack.getFluid();
            String name = fluid.getRegistryName().toString();
            if (!model.cache.containsKey(name)) {
               DynamicBucketModel parent = model.parent.withFluid(fluid);
               IBakedModel bakedModel = parent.bake(model.owner, this.bakery, ModelLoader.defaultTextureGetter(), model.originalTransform, model.getOverrides(), new ResourceLocation("forge:bucket_override"));
               model.cache.put(name, bakedModel);
               return bakedModel;
            } else {
               return (IBakedModel)model.cache.get(name);
            }
         }).orElse(originalModel);
      }

      // $FF: synthetic method
      ContainedFluidOverrideHandler(ModelBakery x0, Object x1) {
         this(x0);
      }
   }

   public static enum Loader implements IModelLoader<DynamicBucketModel> {
      INSTANCE;

      public IResourceType getResourceType() {
         return VanillaResourceType.MODELS;
      }

      public void onResourceManagerReload(IResourceManager resourceManager) {
      }

      public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
      }

      public DynamicBucketModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
         if (!modelContents.has("fluid")) {
            throw new RuntimeException("Bucket model requires 'fluid' value.");
         } else {
            ResourceLocation fluidName = new ResourceLocation(modelContents.get("fluid").getAsString());
            Fluid fluid = (Fluid)ForgeRegistries.FLUIDS.getValue(fluidName);
            boolean flip = false;
            if (modelContents.has("flipGas")) {
               flip = modelContents.get("flipGas").getAsBoolean();
            }

            boolean tint = true;
            if (modelContents.has("applyTint")) {
               tint = modelContents.get("applyTint").getAsBoolean();
            }

            boolean coverIsMask = true;
            if (modelContents.has("coverIsMask")) {
               coverIsMask = modelContents.get("coverIsMask").getAsBoolean();
            }

            return new DynamicBucketModel(fluid, flip, tint, coverIsMask);
         }
      }
   }
}
