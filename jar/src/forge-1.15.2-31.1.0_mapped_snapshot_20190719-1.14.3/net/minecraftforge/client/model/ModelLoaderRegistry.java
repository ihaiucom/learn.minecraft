package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.TransformationHelper;

public class ModelLoaderRegistry {
   public static final String WHITE_TEXTURE = "forge:white";
   private static final Map<ResourceLocation, IModelLoader<?>> loaders = Maps.newHashMap();
   private static volatile boolean registryFrozen = false;
   private static final Pattern FILESYSTEM_PATH_TO_RESLOC = Pattern.compile("(?:.*[\\\\/]assets[\\\\/](?<namespace>[a-z_-]+)[\\\\/]textures[\\\\/])?(?<path>[a-z_\\\\/-]+)\\.png");

   public static void init() {
      registerLoader(new ResourceLocation("minecraft", "elements"), ModelLoaderRegistry.VanillaProxy.Loader.INSTANCE);
      registerLoader(new ResourceLocation("forge", "obj"), OBJLoader.INSTANCE);
      registerLoader(new ResourceLocation("forge", "bucket"), DynamicBucketModel.Loader.INSTANCE);
      registerLoader(new ResourceLocation("forge", "composite"), CompositeModel.Loader.INSTANCE);
      registerLoader(new ResourceLocation("forge", "multi-layer"), MultiLayerModel.Loader.INSTANCE);
   }

   public static void initComplete() {
      registryFrozen = true;
   }

   public static void registerLoader(ResourceLocation id, IModelLoader<?> loader) {
      if (registryFrozen) {
         throw new IllegalStateException("Can not register model loaders after models have started loading. Please use FMLClientSetupEvent or ModelRegistryEvent to register your loaders.");
      } else {
         synchronized(loaders) {
            loaders.put(id, loader);
            ((IReloadableResourceManager)Minecraft.getInstance().getResourceManager()).addReloadListener(loader);
         }
      }
   }

   public static IModelGeometry<?> getModel(ResourceLocation loaderId, JsonDeserializationContext deserializationContext, JsonObject data) {
      try {
         if (!loaders.containsKey(loaderId)) {
            throw new IllegalStateException(String.format("Model loader '%s' not found. Registered loaders: %s", loaderId, loaders.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(", "))));
         } else {
            IModelLoader<?> loader = (IModelLoader)loaders.get(loaderId);
            return loader.read(deserializationContext, data);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         throw var4;
      }
   }

   @Nullable
   public static IModelGeometry<?> deserializeGeometry(JsonDeserializationContext deserializationContext, JsonObject object) {
      if (!object.has("loader")) {
         return null;
      } else {
         ResourceLocation loader = new ResourceLocation(JSONUtils.getString(object, "loader"));
         return getModel(loader, deserializationContext, object);
      }
   }

   public static Material resolveTexture(@Nullable String tex, IModelConfiguration owner) {
      if (tex == null) {
         return blockMaterial("forge:white");
      } else if (tex.startsWith("#")) {
         return owner.resolveTexture(tex);
      } else {
         Matcher match = FILESYSTEM_PATH_TO_RESLOC.matcher(tex);
         if (match.matches()) {
            String namespace = match.group("namespace");
            String path = match.group("path").replace("\\", "/");
            return namespace != null ? blockMaterial(new ResourceLocation(namespace, path)) : blockMaterial(path);
         } else {
            return blockMaterial(tex);
         }
      }
   }

   public static Material blockMaterial(String location) {
      return new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(location));
   }

   public static Material blockMaterial(ResourceLocation location) {
      return new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, location);
   }

   @Nullable
   public static IModelTransform deserializeModelTransforms(JsonDeserializationContext deserializationContext, JsonObject modelData) {
      return !modelData.has("transform") ? null : (IModelTransform)deserializeTransform(deserializationContext, modelData.get("transform")).orElse((Object)null);
   }

   public static Optional<IModelTransform> deserializeTransform(JsonDeserializationContext context, JsonElement transformData) {
      if (!transformData.isJsonObject()) {
         try {
            TransformationMatrix base = (TransformationMatrix)context.deserialize(transformData, TransformationMatrix.class);
            return Optional.of(new SimpleModelTransform(ImmutableMap.of(), base.blockCenterToCorner()));
         } catch (JsonParseException var7) {
            throw new JsonParseException("transform: expected a string, object or valid base transformation, got: " + transformData);
         }
      } else {
         JsonObject transform = transformData.getAsJsonObject();
         EnumMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms = Maps.newEnumMap(ItemCameraTransforms.TransformType.class);
         deserializeTRSR(context, transforms, transform, "thirdperson", ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
         deserializeTRSR(context, transforms, transform, "thirdperson_righthand", ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
         deserializeTRSR(context, transforms, transform, "thirdperson_lefthand", ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
         deserializeTRSR(context, transforms, transform, "firstperson", ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
         deserializeTRSR(context, transforms, transform, "firstperson_righthand", ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
         deserializeTRSR(context, transforms, transform, "firstperson_lefthand", ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
         deserializeTRSR(context, transforms, transform, "head", ItemCameraTransforms.TransformType.HEAD);
         deserializeTRSR(context, transforms, transform, "gui", ItemCameraTransforms.TransformType.GUI);
         deserializeTRSR(context, transforms, transform, "ground", ItemCameraTransforms.TransformType.GROUND);
         deserializeTRSR(context, transforms, transform, "fixed", ItemCameraTransforms.TransformType.FIXED);
         int k = transform.entrySet().size();
         if (transform.has("matrix")) {
            --k;
         }

         if (transform.has("translation")) {
            --k;
         }

         if (transform.has("rotation")) {
            --k;
         }

         if (transform.has("scale")) {
            --k;
         }

         if (transform.has("post-rotation")) {
            --k;
         }

         if (k > 0) {
            throw new JsonParseException("transform: allowed keys: 'thirdperson', 'firstperson', 'gui', 'head', 'matrix', 'translation', 'rotation', 'scale', 'post-rotation'");
         } else {
            TransformationMatrix base = TransformationMatrix.func_227983_a_();
            if (!transform.entrySet().isEmpty()) {
               base = (TransformationMatrix)context.deserialize(transform, TransformationMatrix.class);
               base = base.blockCenterToCorner();
            }

            IModelTransform state = new SimpleModelTransform(Maps.immutableEnumMap(transforms), base);
            return Optional.of(state);
         }
      }
   }

   private static void deserializeTRSR(JsonDeserializationContext context, EnumMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms, JsonObject transform, String name, ItemCameraTransforms.TransformType itemCameraTransform) {
      if (transform.has(name)) {
         TransformationMatrix t = (TransformationMatrix)context.deserialize(transform.remove(name), TransformationMatrix.class);
         transforms.put(itemCameraTransform, t.blockCenterToCorner());
      }

   }

   public static IBakedModel bakeHelper(BlockModel blockModel, ModelBakery modelBakery, BlockModel otherModel, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation, boolean guiLight3d) {
      IModelGeometry<?> customModel = blockModel.customData.getCustomGeometry();
      IModelTransform customModelState = blockModel.customData.getCustomModelState();
      if (customModelState != null) {
         modelTransform = new ModelTransformComposition(customModelState, (IModelTransform)modelTransform, ((IModelTransform)modelTransform).isUvLock());
      }

      Object model;
      if (customModel != null) {
         model = customModel.bake(blockModel.customData, modelBakery, spriteGetter, (IModelTransform)modelTransform, blockModel.getOverrides(modelBakery, otherModel, spriteGetter), modelLocation);
      } else {
         model = blockModel.bakeVanilla(modelBakery, otherModel, spriteGetter, (IModelTransform)modelTransform, modelLocation, guiLight3d);
      }

      if (customModelState != null && !((IBakedModel)model).doesHandlePerspectives()) {
         model = new PerspectiveMapWrapper((IBakedModel)model, customModelState);
      }

      return (IBakedModel)model;
   }

   public static class ExpandedBlockModelDeserializer extends BlockModel.Deserializer {
      public static final Gson INSTANCE = (new GsonBuilder()).registerTypeAdapter(BlockModel.class, new ModelLoaderRegistry.ExpandedBlockModelDeserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer()).registerTypeAdapter(TransformationMatrix.class, new TransformationHelper.Deserializer()).create();

      public BlockModel deserialize(JsonElement element, Type targetType, JsonDeserializationContext deserializationContext) throws JsonParseException {
         BlockModel model = super.deserialize(element, targetType, deserializationContext);
         JsonObject jsonobject = element.getAsJsonObject();
         IModelGeometry<?> geometry = ModelLoaderRegistry.deserializeGeometry(deserializationContext, jsonobject);
         List<BlockPart> elements = model.getElements();
         if (geometry != null) {
            elements.clear();
            model.customData.setCustomGeometry(geometry);
         }

         IModelTransform modelState = ModelLoaderRegistry.deserializeModelTransforms(deserializationContext, jsonobject);
         if (modelState != null) {
            model.customData.setCustomModelState(modelState);
         }

         if (jsonobject.has("visibility")) {
            JsonObject visibility = JSONUtils.getJsonObject(jsonobject, "visibility");
            Iterator var10 = visibility.entrySet().iterator();

            while(var10.hasNext()) {
               Entry<String, JsonElement> part = (Entry)var10.next();
               model.customData.visibilityData.setVisibilityState((String)part.getKey(), ((JsonElement)part.getValue()).getAsBoolean());
            }
         }

         return model;
      }
   }

   public static class VanillaProxy implements ISimpleModelGeometry<ModelLoaderRegistry.VanillaProxy> {
      private final List<BlockPart> elements;

      public VanillaProxy(List<BlockPart> list) {
         this.elements = list;
      }

      public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
         Iterator var7 = this.elements.iterator();

         while(var7.hasNext()) {
            BlockPart blockpart = (BlockPart)var7.next();
            Iterator var9 = blockpart.mapFaces.keySet().iterator();

            while(var9.hasNext()) {
               Direction direction = (Direction)var9.next();
               BlockPartFace blockpartface = (BlockPartFace)blockpart.mapFaces.get(direction);
               TextureAtlasSprite textureatlassprite1 = (TextureAtlasSprite)spriteGetter.apply(owner.resolveTexture(blockpartface.texture));
               if (blockpartface.cullFace == null) {
                  modelBuilder.addGeneralQuad(BlockModel.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, direction, modelTransform, modelLocation));
               } else {
                  modelBuilder.addFaceQuad(modelTransform.func_225615_b_().rotateTransform(blockpartface.cullFace), BlockModel.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, direction, modelTransform, modelLocation));
               }
            }
         }

      }

      public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
         Set<Material> textures = Sets.newHashSet();
         Iterator var5 = this.elements.iterator();

         while(var5.hasNext()) {
            BlockPart part = (BlockPart)var5.next();

            Material texture;
            for(Iterator var7 = part.mapFaces.values().iterator(); var7.hasNext(); textures.add(texture)) {
               BlockPartFace face = (BlockPartFace)var7.next();
               texture = owner.resolveTexture(face.texture);
               if (Objects.equals(texture, MissingTextureSprite.getLocation().toString())) {
                  missingTextureErrors.add(Pair.of(face.texture, owner.getModelName()));
               }
            }
         }

         return textures;
      }

      public static class Loader implements IModelLoader<ModelLoaderRegistry.VanillaProxy> {
         public static final ModelLoaderRegistry.VanillaProxy.Loader INSTANCE = new ModelLoaderRegistry.VanillaProxy.Loader();

         private Loader() {
         }

         public void onResourceManagerReload(IResourceManager resourceManager) {
         }

         public ModelLoaderRegistry.VanillaProxy read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            List<BlockPart> list = this.getModelElements(deserializationContext, modelContents);
            return new ModelLoaderRegistry.VanillaProxy(list);
         }

         private List<BlockPart> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
            List<BlockPart> list = Lists.newArrayList();
            if (object.has("elements")) {
               Iterator var4 = JSONUtils.getJsonArray(object, "elements").iterator();

               while(var4.hasNext()) {
                  JsonElement jsonelement = (JsonElement)var4.next();
                  list.add(deserializationContext.deserialize(jsonelement, BlockPart.class));
               }
            }

            return list;
         }
      }
   }
}
