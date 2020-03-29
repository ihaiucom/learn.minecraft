package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class ItemModelMesher {
   private final Int2ObjectMap<ModelResourceLocation> modelLocations = new Int2ObjectOpenHashMap(256);
   private final Int2ObjectMap<IBakedModel> itemModels = new Int2ObjectOpenHashMap(256);
   private final ModelManager modelManager;

   public ItemModelMesher(ModelManager p_i46250_1_) {
      this.modelManager = p_i46250_1_;
   }

   public TextureAtlasSprite getParticleIcon(IItemProvider p_199934_1_) {
      return this.getParticleIcon(new ItemStack(p_199934_1_));
   }

   public TextureAtlasSprite getParticleIcon(ItemStack p_199309_1_) {
      IBakedModel ibakedmodel = this.getItemModel(p_199309_1_);
      return ibakedmodel == this.modelManager.getMissingModel() && p_199309_1_.getItem() instanceof BlockItem ? this.modelManager.getBlockModelShapes().getTexture(((BlockItem)p_199309_1_.getItem()).getBlock().getDefaultState()) : ibakedmodel.getOverrides().getModelWithOverrides(ibakedmodel, p_199309_1_, (World)null, (LivingEntity)null).getParticleTexture(EmptyModelData.INSTANCE);
   }

   public IBakedModel getItemModel(ItemStack p_178089_1_) {
      IBakedModel ibakedmodel = this.getItemModel(p_178089_1_.getItem());
      return ibakedmodel == null ? this.modelManager.getMissingModel() : ibakedmodel;
   }

   @Nullable
   public IBakedModel getItemModel(Item p_199312_1_) {
      return (IBakedModel)this.itemModels.get(getIndex(p_199312_1_));
   }

   private static int getIndex(Item p_199310_0_) {
      return Item.getIdFromItem(p_199310_0_);
   }

   public void register(Item p_199311_1_, ModelResourceLocation p_199311_2_) {
      this.modelLocations.put(getIndex(p_199311_1_), p_199311_2_);
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.itemModels.clear();
      ObjectIterator var1 = this.modelLocations.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<Integer, ModelResourceLocation> entry = (Entry)var1.next();
         this.itemModels.put((Integer)entry.getKey(), this.modelManager.getModel((ModelResourceLocation)entry.getValue()));
      }

   }
}
