package net.minecraftforge.fml.event.lifecycle;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.fml.ModContainer;

public class GatherDataEvent extends ModLifecycleEvent {
   private final DataGenerator dataGenerator;
   private final GatherDataEvent.DataGeneratorConfig config;
   private final ExistingFileHelper existingFileHelper;

   public GatherDataEvent(ModContainer modContainer, DataGenerator dataGenerator, GatherDataEvent.DataGeneratorConfig dataGeneratorConfig, ExistingFileHelper existingFileHelper) {
      super(modContainer);
      this.dataGenerator = dataGenerator;
      this.config = dataGeneratorConfig;
      this.existingFileHelper = existingFileHelper;
   }

   public DataGenerator getGenerator() {
      return this.dataGenerator;
   }

   public ExistingFileHelper getExistingFileHelper() {
      return this.existingFileHelper;
   }

   public boolean includeServer() {
      return this.config.server;
   }

   public boolean includeClient() {
      return this.config.client;
   }

   public boolean includeDev() {
      return this.config.dev;
   }

   public boolean includeReports() {
      return this.config.reports;
   }

   public boolean validate() {
      return this.config.validate;
   }

   public static class DataGeneratorConfig {
      private final Set<String> mods;
      private final Path path;
      private final Collection<Path> inputs;
      private final boolean server;
      private final boolean client;
      private final boolean dev;
      private final boolean reports;
      private final boolean validate;
      private List<DataGenerator> generators = new ArrayList();

      public DataGeneratorConfig(Set<String> mods, Path path, Collection<Path> inputs, boolean server, boolean client, boolean dev, boolean reports, boolean validate) {
         this.mods = mods;
         this.path = path;
         this.inputs = inputs;
         this.server = server;
         this.client = client;
         this.dev = dev;
         this.reports = reports;
         this.validate = validate;
      }

      public Set<String> getMods() {
         return this.mods;
      }

      public DataGenerator makeGenerator(Function<Path, Path> pathEnhancer, boolean shouldExecute) {
         DataGenerator generator = new DataGenerator((Path)pathEnhancer.apply(this.path), this.inputs);
         if (shouldExecute) {
            this.generators.add(generator);
         }

         return generator;
      }

      public void runAll() {
         this.generators.forEach(LamdbaExceptionUtils.rethrowConsumer(DataGenerator::run));
      }
   }
}
