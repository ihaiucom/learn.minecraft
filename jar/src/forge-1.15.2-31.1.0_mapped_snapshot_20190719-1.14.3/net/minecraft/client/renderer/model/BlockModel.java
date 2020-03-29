package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.BlockModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BlockModel implements IUnbakedModel {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final FaceBakery field_217647_g = new FaceBakery();
   @VisibleForTesting
   static final Gson SERIALIZER = (new GsonBuilder()).registerTypeAdapter(BlockModel.class, new BlockModel.Deserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer()).create();
   private final List<BlockPart> elements;
   @Nullable
   private final BlockModel.GuiLight field_230174_i_;
   public final boolean ambientOcclusion;
   private final ItemCameraTransforms cameraTransforms;
   private final List<ItemOverride> overrides;
   public String name = "";
   @VisibleForTesting
   public final Map<String, Either<Material, String>> textures;
   @Nullable
   public BlockModel parent;
   @Nullable
   protected ResourceLocation parentLocation;
   public final BlockModelConfiguration customData = new BlockModelConfiguration(this);

   public static BlockModel deserialize(Reader p_178307_0_) {
      return (BlockModel)JSONUtils.fromJson(ModelLoaderRegistry.ExpandedBlockModelDeserializer.INSTANCE, p_178307_0_, BlockModel.class);
   }

   public static BlockModel deserialize(String p_178294_0_) {
      return deserialize((Reader)(new StringReader(p_178294_0_)));
   }

   public BlockModel(@Nullable ResourceLocation p_i230056_1_, List<BlockPart> p_i230056_2_, Map<String, Either<Material, String>> p_i230056_3_, boolean p_i230056_4_, @Nullable BlockModel.GuiLight p_i230056_5_, ItemCameraTransforms p_i230056_6_, List<ItemOverride> p_i230056_7_) {
      this.elements = p_i230056_2_;
      this.ambientOcclusion = p_i230056_4_;
      this.field_230174_i_ = p_i230056_5_;
      this.textures = p_i230056_3_;
      this.parentLocation = p_i230056_1_;
      this.cameraTransforms = p_i230056_6_;
      this.overrides = p_i230056_7_;
   }

   /** @deprecated */
   @Deprecated
   public List<BlockPart> getElements() {
      if (this.customData.hasCustomGeometry()) {
         return Collections.emptyList();
      } else {
         return this.elements.isEmpty() && this.parent != null ? this.parent.getElements() : this.elements;
      }
   }

   @Nullable
   public ResourceLocation getParentLocation() {
      return this.parentLocation;
   }

   public boolean isAmbientOcclusion() {
      return this.parent != null ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
   }

   public BlockModel.GuiLight func_230176_c_() {
      if (this.field_230174_i_ != null) {
         return this.field_230174_i_;
      } else {
         return this.parent != null ? this.parent.func_230176_c_() : BlockModel.GuiLight.SIDE;
      }
   }

   public List<ItemOverride> getOverrides() {
      return this.overrides;
   }

   private ItemOverrideList func_217646_a(ModelBakery p_217646_1_, BlockModel p_217646_2_) {
      return this.overrides.isEmpty() ? ItemOverrideList.EMPTY : new ItemOverrideList(p_217646_1_, p_217646_2_, p_217646_1_::getUnbakedModel, this.overrides);
   }

   public ItemOverrideList getOverrides(ModelBakery p_getOverrides_1_, BlockModel p_getOverrides_2_, Function<Material, TextureAtlasSprite> p_getOverrides_3_) {
      return this.overrides.isEmpty() ? ItemOverrideList.EMPTY : new ItemOverrideList(p_getOverrides_1_, p_getOverrides_2_, p_getOverrides_1_::getUnbakedModel, p_getOverrides_3_, this.overrides);
   }

   public Collection<ResourceLocation> getDependencies() {
      Set<ResourceLocation> set = Sets.newHashSet();
      Iterator var2 = this.overrides.iterator();

      while(var2.hasNext()) {
         ItemOverride itemoverride = (ItemOverride)var2.next();
         set.add(itemoverride.getLocation());
      }

      if (this.parentLocation != null) {
         set.add(this.parentLocation);
      }

      return set;
   }

   public Collection<Material> func_225614_a_(Function<ResourceLocation, IUnbakedModel> p_225614_1_, Set<Pair<String, String>> p_225614_2_) {
      Set<IUnbakedModel> set = Sets.newLinkedHashSet();

      for(BlockModel blockmodel = this; blockmodel.parentLocation != null && blockmodel.parent == null; blockmodel = blockmodel.parent) {
         set.add(blockmodel);
         IUnbakedModel iunbakedmodel = (IUnbakedModel)p_225614_1_.apply(blockmodel.parentLocation);
         if (iunbakedmodel == null) {
            LOGGER.warn("No parent '{}' while loading model '{}'", this.parentLocation, blockmodel);
         }

         if (set.contains(iunbakedmodel)) {
            LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", blockmodel, set.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentLocation);
            iunbakedmodel = null;
         }

         if (iunbakedmodel == null) {
            blockmodel.parentLocation = ModelBakery.MODEL_MISSING;
            iunbakedmodel = (IUnbakedModel)p_225614_1_.apply(blockmodel.parentLocation);
         }

         if (!(iunbakedmodel instanceof BlockModel)) {
            throw new IllegalStateException("BlockModel parent has to be a block model.");
         }

         blockmodel.parent = (BlockModel)iunbakedmodel;
      }

      Set<Material> set1 = Sets.newHashSet(new Material[]{this.func_228816_c_("particle")});
      if (this.customData.hasCustomGeometry()) {
         set1.addAll(this.customData.getTextureDependencies(p_225614_1_, p_225614_2_));
      } else {
         Iterator var11 = this.getElements().iterator();

         while(var11.hasNext()) {
            BlockPart blockpart = (BlockPart)var11.next();

            Material material;
            for(Iterator var7 = blockpart.mapFaces.values().iterator(); var7.hasNext(); set1.add(material)) {
               BlockPartFace blockpartface = (BlockPartFace)var7.next();
               material = this.func_228816_c_(blockpartface.texture);
               if (Objects.equals(material.func_229313_b_(), MissingTextureSprite.getLocation())) {
                  p_225614_2_.add(Pair.of(blockpartface.texture, this.name));
               }
            }
         }
      }

      this.overrides.forEach((p_lambda$func_225614_a_$0_4_) -> {
         IUnbakedModel iunbakedmodel1 = (IUnbakedModel)p_225614_1_.apply(p_lambda$func_225614_a_$0_4_.getLocation());
         if (!Objects.equals(iunbakedmodel1, this)) {
            set1.addAll(iunbakedmodel1.func_225614_a_(p_225614_1_, p_225614_2_));
         }

      });
      if (this.getRootModel() == ModelBakery.MODEL_GENERATED) {
         ItemModelGenerator.LAYERS.forEach((p_lambda$func_225614_a_$1_2_) -> {
            set1.add(this.func_228816_c_(p_lambda$func_225614_a_$1_2_));
         });
      }

      return set1;
   }

   /** @deprecated */
   @Deprecated
   public IBakedModel func_225613_a_(ModelBakery p_225613_1_, Function<Material, TextureAtlasSprite> p_225613_2_, IModelTransform p_225613_3_, ResourceLocation p_225613_4_) {
      return this.func_228813_a_(p_225613_1_, this, p_225613_2_, p_225613_3_, p_225613_4_, true);
   }

   public IBakedModel func_228813_a_(ModelBakery p_228813_1_, BlockModel p_228813_2_, Function<Material, TextureAtlasSprite> p_228813_3_, IModelTransform p_228813_4_, ResourceLocation p_228813_5_, boolean p_228813_6_) {
      return ModelLoaderRegistry.bakeHelper(this, p_228813_1_, p_228813_2_, p_228813_3_, p_228813_4_, p_228813_5_, p_228813_6_);
   }

   /** @deprecated */
   @Deprecated
   public IBakedModel bakeVanilla(ModelBakery p_bakeVanilla_1_, BlockModel p_bakeVanilla_2_, Function<Material, TextureAtlasSprite> p_bakeVanilla_3_, IModelTransform p_bakeVanilla_4_, ResourceLocation p_bakeVanilla_5_, boolean p_bakeVanilla_6_) {
      TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)p_bakeVanilla_3_.apply(this.func_228816_c_("particle"));
      if (this.getRootModel() == ModelBakery.MODEL_ENTITY) {
         return new BuiltInModel(this.getAllTransforms(), this.func_217646_a(p_bakeVanilla_1_, p_bakeVanilla_2_), textureatlassprite, this.func_230176_c_().func_230178_a_());
      } else {
         SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(this, this.func_217646_a(p_bakeVanilla_1_, p_bakeVanilla_2_), p_bakeVanilla_6_)).setTexture(textureatlassprite);
         Iterator var9 = this.getElements().iterator();

         while(var9.hasNext()) {
            BlockPart blockpart = (BlockPart)var9.next();
            Iterator var11 = blockpart.mapFaces.keySet().iterator();

            while(var11.hasNext()) {
               Direction direction = (Direction)var11.next();
               BlockPartFace blockpartface = (BlockPartFace)blockpart.mapFaces.get(direction);
               TextureAtlasSprite textureatlassprite1 = (TextureAtlasSprite)p_bakeVanilla_3_.apply(this.func_228816_c_(blockpartface.texture));
               if (blockpartface.cullFace == null) {
                  simplebakedmodel$builder.addGeneralQuad(func_228812_a_(blockpart, blockpartface, textureatlassprite1, direction, p_bakeVanilla_4_, p_bakeVanilla_5_));
               } else {
                  simplebakedmodel$builder.addFaceQuad(Direction.func_229385_a_(p_bakeVanilla_4_.func_225615_b_().func_227988_c_(), blockpartface.cullFace), func_228812_a_(blockpart, blockpartface, textureatlassprite1, direction, p_bakeVanilla_4_, p_bakeVanilla_5_));
               }
            }
         }

         return simplebakedmodel$builder.build();
      }
   }

   private static BakedQuad func_228812_a_(BlockPart p_228812_0_, BlockPartFace p_228812_1_, TextureAtlasSprite p_228812_2_, Direction p_228812_3_, IModelTransform p_228812_4_, ResourceLocation p_228812_5_) {
      return field_217647_g.func_228824_a_(p_228812_0_.positionFrom, p_228812_0_.positionTo, p_228812_1_, p_228812_2_, p_228812_3_, p_228812_4_, p_228812_0_.partRotation, p_228812_0_.shade, p_228812_5_);
   }

   public static BakedQuad makeBakedQuad(BlockPart p_makeBakedQuad_0_, BlockPartFace p_makeBakedQuad_1_, TextureAtlasSprite p_makeBakedQuad_2_, Direction p_makeBakedQuad_3_, IModelTransform p_makeBakedQuad_4_, ResourceLocation p_makeBakedQuad_5_) {
      return func_228812_a_(p_makeBakedQuad_0_, p_makeBakedQuad_1_, p_makeBakedQuad_2_, p_makeBakedQuad_3_, p_makeBakedQuad_4_, p_makeBakedQuad_5_);
   }

   public boolean isTexturePresent(String p_178300_1_) {
      return !MissingTextureSprite.getLocation().equals(this.func_228816_c_(p_178300_1_).func_229313_b_());
   }

   public Material func_228816_c_(String p_228816_1_) {
      if (startsWithHash(p_228816_1_)) {
         p_228816_1_ = p_228816_1_.substring(1);
      }

      ArrayList list = Lists.newArrayList();

      while(true) {
         Either<Material, String> either = this.func_228818_e_(p_228816_1_);
         Optional<Material> optional = either.left();
         if (optional.isPresent()) {
            return (Material)optional.get();
         }

         p_228816_1_ = (String)either.right().get();
         if (list.contains(p_228816_1_)) {
            LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", Joiner.on("->").join(list), p_228816_1_, this.name);
            return new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, MissingTextureSprite.getLocation());
         }

         list.add(p_228816_1_);
      }
   }

   private Either<Material, String> func_228818_e_(String p_228818_1_) {
      for(BlockModel blockmodel = this; blockmodel != null; blockmodel = blockmodel.parent) {
         Either<Material, String> either = (Either)blockmodel.textures.get(p_228818_1_);
         if (either != null) {
            return either;
         }
      }

      return Either.left(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, MissingTextureSprite.getLocation()));
   }

   private static boolean startsWithHash(String p_178304_0_) {
      return p_178304_0_.charAt(0) == '#';
   }

   public BlockModel getRootModel() {
      return this.parent == null ? this : this.parent.getRootModel();
   }

   public ItemCameraTransforms getAllTransforms() {
      ItemTransformVec3f itemtransformvec3f = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
      ItemTransformVec3f itemtransformvec3f1 = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
      ItemTransformVec3f itemtransformvec3f2 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
      ItemTransformVec3f itemtransformvec3f3 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
      ItemTransformVec3f itemtransformvec3f4 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
      ItemTransformVec3f itemtransformvec3f5 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
      ItemTransformVec3f itemtransformvec3f6 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
      ItemTransformVec3f itemtransformvec3f7 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
      return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7);
   }

   private ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType p_181681_1_) {
      return this.parent != null && !this.cameraTransforms.hasCustomTransform(p_181681_1_) ? this.parent.getTransform(p_181681_1_) : this.cameraTransforms.getTransform(p_181681_1_);
   }

   public String toString() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum GuiLight {
      FRONT("front"),
      SIDE("side");

      private final String field_230177_c_;

      private GuiLight(String p_i230057_3_) {
         this.field_230177_c_ = p_i230057_3_;
      }

      public static BlockModel.GuiLight func_230179_a_(String p_230179_0_) {
         BlockModel.GuiLight[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BlockModel.GuiLight blockmodel$guilight = var1[var3];
            if (blockmodel$guilight.field_230177_c_.equals(p_230179_0_)) {
               return blockmodel$guilight;
            }
         }

         throw new IllegalArgumentException("Invalid gui light: " + p_230179_0_);
      }

      public boolean func_230178_a_() {
         return this == SIDE;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockModel> {
      public BlockModel deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         List<BlockPart> list = this.getModelElements(p_deserialize_3_, jsonobject);
         String s = this.getParent(jsonobject);
         Map<String, Either<Material, String>> map = this.getTextures(jsonobject);
         boolean flag = this.getAmbientOcclusionEnabled(jsonobject);
         ItemCameraTransforms itemcameratransforms = ItemCameraTransforms.DEFAULT;
         if (jsonobject.has("display")) {
            JsonObject jsonobject1 = JSONUtils.getJsonObject(jsonobject, "display");
            itemcameratransforms = (ItemCameraTransforms)p_deserialize_3_.deserialize(jsonobject1, ItemCameraTransforms.class);
         }

         List<ItemOverride> list1 = this.getItemOverrides(p_deserialize_3_, jsonobject);
         BlockModel.GuiLight blockmodel$guilight = null;
         if (jsonobject.has("gui_light")) {
            blockmodel$guilight = BlockModel.GuiLight.func_230179_a_(JSONUtils.getString(jsonobject, "gui_light"));
         }

         ResourceLocation resourcelocation = s.isEmpty() ? null : new ResourceLocation(s);
         return new BlockModel(resourcelocation, list, map, flag, blockmodel$guilight, itemcameratransforms, list1);
      }

      protected List<ItemOverride> getItemOverrides(JsonDeserializationContext p_187964_1_, JsonObject p_187964_2_) {
         List<ItemOverride> list = Lists.newArrayList();
         if (p_187964_2_.has("overrides")) {
            Iterator var4 = JSONUtils.getJsonArray(p_187964_2_, "overrides").iterator();

            while(var4.hasNext()) {
               JsonElement jsonelement = (JsonElement)var4.next();
               list.add(p_187964_1_.deserialize(jsonelement, ItemOverride.class));
            }
         }

         return list;
      }

      private Map<String, Either<Material, String>> getTextures(JsonObject p_178329_1_) {
         ResourceLocation resourcelocation = AtlasTexture.LOCATION_BLOCKS_TEXTURE;
         Map<String, Either<Material, String>> map = Maps.newHashMap();
         if (p_178329_1_.has("textures")) {
            JsonObject jsonobject = JSONUtils.getJsonObject(p_178329_1_, "textures");
            Iterator var5 = jsonobject.entrySet().iterator();

            while(var5.hasNext()) {
               Entry<String, JsonElement> entry = (Entry)var5.next();
               map.put(entry.getKey(), func_228819_a_(resourcelocation, ((JsonElement)entry.getValue()).getAsString()));
            }
         }

         return map;
      }

      private static Either<Material, String> func_228819_a_(ResourceLocation p_228819_0_, String p_228819_1_) {
         if (BlockModel.startsWithHash(p_228819_1_)) {
            return Either.right(p_228819_1_.substring(1));
         } else {
            ResourceLocation resourcelocation = ResourceLocation.tryCreate(p_228819_1_);
            if (resourcelocation == null) {
               throw new JsonParseException(p_228819_1_ + " is not valid resource location");
            } else {
               return Either.left(new Material(p_228819_0_, resourcelocation));
            }
         }
      }

      private String getParent(JsonObject p_178326_1_) {
         return JSONUtils.getString(p_178326_1_, "parent", "");
      }

      protected boolean getAmbientOcclusionEnabled(JsonObject p_178328_1_) {
         return JSONUtils.getBoolean(p_178328_1_, "ambientocclusion", true);
      }

      protected List<BlockPart> getModelElements(JsonDeserializationContext p_178325_1_, JsonObject p_178325_2_) {
         List<BlockPart> list = Lists.newArrayList();
         if (p_178325_2_.has("elements")) {
            Iterator var4 = JSONUtils.getJsonArray(p_178325_2_, "elements").iterator();

            while(var4.hasNext()) {
               JsonElement jsonelement = (JsonElement)var4.next();
               list.add(p_178325_1_.deserialize(jsonelement, BlockPart.class));
            }
         }

         return list;
      }
   }
}
