package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

public abstract class ModelFile {
   protected ResourceLocation location;

   protected ModelFile(ResourceLocation location) {
      this.location = location;
   }

   protected abstract boolean exists();

   public ResourceLocation getLocation() {
      this.assertExistence();
      return this.location;
   }

   public void assertExistence() {
      Preconditions.checkState(this.exists(), "Model at %s does not exist", this.location);
   }

   public ResourceLocation getUncheckedLocation() {
      return this.location;
   }

   public static class ExistingModelFile extends ModelFile {
      private final ExistingFileHelper existingHelper;

      public ExistingModelFile(ResourceLocation location, ExistingFileHelper existingHelper) {
         super(location);
         this.existingHelper = existingHelper;
      }

      protected boolean exists() {
         return this.getUncheckedLocation().getPath().contains(".") ? this.existingHelper.exists(this.getUncheckedLocation(), ResourcePackType.CLIENT_RESOURCES, "", "models") : this.existingHelper.exists(this.getUncheckedLocation(), ResourcePackType.CLIENT_RESOURCES, ".json", "models");
      }
   }

   public static class UncheckedModelFile extends ModelFile {
      public UncheckedModelFile(String location) {
         this(new ResourceLocation(location));
      }

      public UncheckedModelFile(ResourceLocation location) {
         super(location);
      }

      protected boolean exists() {
         return true;
      }
   }
}
