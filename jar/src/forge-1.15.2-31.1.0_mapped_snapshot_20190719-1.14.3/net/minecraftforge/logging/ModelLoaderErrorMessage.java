package net.minecraftforge.logging;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.message.SimpleMessage;

public class ModelLoaderErrorMessage extends SimpleMessage {
   private final ModelResourceLocation resourceLocation;
   private final Exception exception;
   private static Multimap<ModelResourceLocation, BlockState> reverseBlockMap = HashMultimap.create();
   private static Multimap<ModelResourceLocation, String> reverseItemMap = HashMultimap.create();

   private static void buildLookups() {
      if (reverseBlockMap.isEmpty()) {
         ForgeRegistries.BLOCKS.getValues().stream().flatMap((block) -> {
            return block.getStateContainer().getValidStates().stream();
         }).forEach((state) -> {
            reverseBlockMap.put(BlockModelShapes.getModelLocation(state), state);
         });
         ForgeRegistries.ITEMS.forEach((item) -> {
            ModelResourceLocation memory = ModelLoader.getInventoryVariant(ForgeRegistries.ITEMS.getKey(item).toString());
            reverseItemMap.put(memory, item.getRegistryName().toString());
         });
      }
   }

   public ModelLoaderErrorMessage(ModelResourceLocation resourceLocation, Exception exception) {
      buildLookups();
      this.resourceLocation = resourceLocation;
      this.exception = exception;
   }

   private void stuffs() {
      String domain = this.resourceLocation.getNamespace();
      String errorMsg = "Exception loading model for variant " + this.resourceLocation;
      Collection<BlockState> blocks = reverseBlockMap.get(this.resourceLocation);
      if (!blocks.isEmpty()) {
         if (blocks.size() == 1) {
            errorMsg = errorMsg + " for blockstate \"" + blocks.iterator().next() + "\"";
         } else {
            errorMsg = errorMsg + " for blockstates [\"" + Joiner.on("\", \"").join(blocks) + "\"]";
         }
      }

      Collection<String> items = reverseItemMap.get(this.resourceLocation);
      if (!items.isEmpty()) {
         if (!blocks.isEmpty()) {
            errorMsg = errorMsg + " and";
         }

         if (items.size() == 1) {
            errorMsg = errorMsg + " for item \"" + (String)items.iterator().next() + "\"";
         } else {
            errorMsg = errorMsg + " for items [\"" + Joiner.on("\", \"").join(items) + "\"]";
         }
      }

      if (this.exception instanceof ModelLoader.ItemLoadingException) {
         ModelLoader.ItemLoadingException var5 = (ModelLoader.ItemLoadingException)this.exception;
      }

   }

   public void formatTo(StringBuilder buffer) {
   }
}
