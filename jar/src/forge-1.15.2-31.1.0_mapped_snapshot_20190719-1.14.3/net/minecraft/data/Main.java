package net.minecraft.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraftforge.fml.ModLoader;

public class Main {
   public static void main(String[] p_main_0_) throws IOException {
      OptionParser optionparser = new OptionParser();
      OptionSpec<Void> optionspec = optionparser.accepts("help", "Show the help menu").forHelp();
      OptionSpec<Void> optionspec1 = optionparser.accepts("server", "Include server generators");
      OptionSpec<Void> optionspec2 = optionparser.accepts("client", "Include client generators");
      OptionSpec<Void> optionspec3 = optionparser.accepts("dev", "Include development tools");
      OptionSpec<Void> optionspec4 = optionparser.accepts("reports", "Include data reports");
      OptionSpec<Void> optionspec5 = optionparser.accepts("validate", "Validate inputs");
      OptionSpec<Void> optionspec6 = optionparser.accepts("all", "Include all generators");
      OptionSpec<String> optionspec7 = optionparser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", new String[0]);
      OptionSpec<String> optionspec8 = optionparser.accepts("input", "Input folder").withRequiredArg();
      OptionSpec<String> existing = optionparser.accepts("existing", "Existing resource packs that generated resources can reference").withRequiredArg();
      OptionSpec<File> gameDir = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]).required();
      OptionSpec<String> mod = optionparser.accepts("mod", "A modid to dump").withRequiredArg().withValuesSeparatedBy(",");
      OptionSet optionset = optionparser.parse(p_main_0_);
      if (!optionset.has(optionspec) && optionset.hasOptions() && (optionset.specs().size() != 1 || !optionset.has(gameDir))) {
         Path path = Paths.get((String)optionspec7.value(optionset));
         boolean flag = optionset.has(optionspec6);
         boolean flag1 = flag || optionset.has(optionspec2);
         boolean flag2 = flag || optionset.has(optionspec1);
         boolean flag3 = flag || optionset.has(optionspec3);
         boolean flag4 = flag || optionset.has(optionspec4);
         boolean flag5 = flag || optionset.has(optionspec5);
         Collection<Path> inputs = (Collection)optionset.valuesOf(optionspec8).stream().map((p_lambda$main$0_0_) -> {
            return Paths.get(p_lambda$main$0_0_);
         }).collect(Collectors.toList());
         Collection<Path> existingPacks = (Collection)optionset.valuesOf(existing).stream().map((p_lambda$main$1_0_) -> {
            return Paths.get(p_lambda$main$1_0_);
         }).collect(Collectors.toList());
         Set<String> mods = new HashSet(optionset.valuesOf(mod));
         ModLoader.get().runDataGenerator(mods, path, inputs, existingPacks, flag2, flag1, flag3, flag4, flag5);
         if (mods.contains("minecraft") || mods.isEmpty()) {
            makeGenerator(mods.isEmpty() ? path : path.resolve("minecraft"), inputs, flag1, flag2, flag3, flag4, flag5).run();
         }
      } else {
         optionparser.printHelpOn(System.out);
      }

   }

   public static DataGenerator makeGenerator(Path p_200264_0_, Collection<Path> p_200264_1_, boolean p_200264_2_, boolean p_200264_3_, boolean p_200264_4_, boolean p_200264_5_, boolean p_200264_6_) {
      DataGenerator datagenerator = new DataGenerator(p_200264_0_, p_200264_1_);
      if (p_200264_2_ || p_200264_3_) {
         datagenerator.addProvider((new SNBTToNBTConverter(datagenerator)).func_225369_a(new StructureUpdater()));
      }

      if (p_200264_3_) {
         datagenerator.addProvider(new FluidTagsProvider(datagenerator));
         datagenerator.addProvider(new BlockTagsProvider(datagenerator));
         datagenerator.addProvider(new ItemTagsProvider(datagenerator));
         datagenerator.addProvider(new EntityTypeTagsProvider(datagenerator));
         datagenerator.addProvider(new RecipeProvider(datagenerator));
         datagenerator.addProvider(new AdvancementProvider(datagenerator));
         datagenerator.addProvider(new LootTableProvider(datagenerator));
      }

      if (p_200264_4_) {
         datagenerator.addProvider(new NBTToSNBTConverter(datagenerator));
      }

      if (p_200264_5_) {
         datagenerator.addProvider(new BlockListReport(datagenerator));
         datagenerator.addProvider(new RegistryDumpReport(datagenerator));
         datagenerator.addProvider(new CommandsReport(datagenerator));
      }

      return datagenerator;
   }
}
