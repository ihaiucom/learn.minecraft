package net.minecraftforge.client.model.generators;

import net.minecraft.data.DataGenerator;

public abstract class BlockModelProvider extends ModelProvider<BlockModelBuilder> {
   public BlockModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
      super(generator, modid, "block", BlockModelBuilder::new, existingFileHelper);
   }
}
