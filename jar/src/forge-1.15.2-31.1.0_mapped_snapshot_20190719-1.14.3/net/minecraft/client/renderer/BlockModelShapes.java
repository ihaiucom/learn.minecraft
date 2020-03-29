package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

@OnlyIn(Dist.CLIENT)
public class BlockModelShapes {
   private final Map<BlockState, IBakedModel> bakedModelStore = Maps.newIdentityHashMap();
   private final ModelManager modelManager;

   public BlockModelShapes(ModelManager p_i46245_1_) {
      this.modelManager = p_i46245_1_;
   }

   /** @deprecated */
   @Deprecated
   public TextureAtlasSprite getTexture(BlockState p_178122_1_) {
      return this.getModel(p_178122_1_).getParticleTexture(EmptyModelData.INSTANCE);
   }

   public TextureAtlasSprite getTexture(BlockState p_getTexture_1_, World p_getTexture_2_, BlockPos p_getTexture_3_) {
      IModelData data = ModelDataManager.getModelData(p_getTexture_2_, p_getTexture_3_);
      return this.getModel(p_getTexture_1_).getParticleTexture((IModelData)(data == null ? EmptyModelData.INSTANCE : data));
   }

   public IBakedModel getModel(BlockState p_178125_1_) {
      IBakedModel ibakedmodel = (IBakedModel)this.bakedModelStore.get(p_178125_1_);
      if (ibakedmodel == null) {
         ibakedmodel = this.modelManager.getMissingModel();
      }

      return ibakedmodel;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void reloadModels() {
      this.bakedModelStore.clear();
      Iterator var1 = Registry.BLOCK.iterator();

      while(var1.hasNext()) {
         Block block = (Block)var1.next();
         block.getStateContainer().getValidStates().forEach((p_lambda$reloadModels$0_1_) -> {
            IBakedModel ibakedmodel = (IBakedModel)this.bakedModelStore.put(p_lambda$reloadModels$0_1_, this.modelManager.getModel(getModelLocation(p_lambda$reloadModels$0_1_)));
         });
      }

   }

   public static ModelResourceLocation getModelLocation(BlockState p_209554_0_) {
      return getModelLocation(Registry.BLOCK.getKey(p_209554_0_.getBlock()), p_209554_0_);
   }

   public static ModelResourceLocation getModelLocation(ResourceLocation p_209553_0_, BlockState p_209553_1_) {
      return new ModelResourceLocation(p_209553_0_, getPropertyMapString(p_209553_1_.getValues()));
   }

   public static String getPropertyMapString(Map<IProperty<?>, Comparable<?>> p_209552_0_) {
      StringBuilder stringbuilder = new StringBuilder();
      Iterator var2 = p_209552_0_.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<IProperty<?>, Comparable<?>> entry = (Entry)var2.next();
         if (stringbuilder.length() != 0) {
            stringbuilder.append(',');
         }

         IProperty<?> iproperty = (IProperty)entry.getKey();
         stringbuilder.append(iproperty.getName());
         stringbuilder.append('=');
         stringbuilder.append(getPropertyValueString(iproperty, (Comparable)entry.getValue()));
      }

      return stringbuilder.toString();
   }

   private static <T extends Comparable<T>> String getPropertyValueString(IProperty<T> p_209555_0_, Comparable<?> p_209555_1_) {
      return p_209555_0_.getName(p_209555_1_);
   }
}
