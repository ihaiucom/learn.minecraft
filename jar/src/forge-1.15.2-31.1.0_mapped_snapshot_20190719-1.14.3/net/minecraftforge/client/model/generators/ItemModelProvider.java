package net.minecraftforge.client.model.generators;

import net.minecraft.data.DataGenerator;

public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {
   public ItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
      super(generator, modid, "item", ItemModelBuilder::new, existingFileHelper);
   }
}
