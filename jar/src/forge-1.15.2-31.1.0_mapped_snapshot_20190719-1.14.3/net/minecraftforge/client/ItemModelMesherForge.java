package net.minecraftforge.client;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IRegistryDelegate;

public class ItemModelMesherForge extends ItemModelMesher {
   final Map<IRegistryDelegate<Item>, ModelResourceLocation> locations = Maps.newHashMap();
   final Map<IRegistryDelegate<Item>, IBakedModel> models = Maps.newHashMap();

   public ItemModelMesherForge(ModelManager manager) {
      super(manager);
   }

   @Nullable
   public IBakedModel getItemModel(Item item) {
      return (IBakedModel)this.models.get(item.delegate);
   }

   public void register(Item item, ModelResourceLocation location) {
      IRegistryDelegate<Item> key = item.delegate;
      this.locations.put(key, location);
      this.models.put(key, this.getModelManager().getModel(location));
   }

   public void rebuildCache() {
      ModelManager manager = this.getModelManager();
      Iterator var2 = this.locations.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<IRegistryDelegate<Item>, ModelResourceLocation> e = (Entry)var2.next();
         this.models.put(e.getKey(), manager.getModel((ModelResourceLocation)e.getValue()));
      }

   }

   public ModelResourceLocation getLocation(@Nonnull ItemStack stack) {
      ModelResourceLocation location = (ModelResourceLocation)this.locations.get(stack.getItem().delegate);
      if (location == null) {
         location = ModelBakery.MODEL_MISSING;
      }

      return location;
   }
}
