package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.advancements.AdventureAdvancements;
import net.minecraft.data.advancements.EndAdvancements;
import net.minecraft.data.advancements.HusbandryAdvancements;
import net.minecraft.data.advancements.NetherAdvancements;
import net.minecraft.data.advancements.StoryAdvancements;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementProvider implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;
   private final List<Consumer<Consumer<Advancement>>> advancements = ImmutableList.of(new EndAdvancements(), new HusbandryAdvancements(), new AdventureAdvancements(), new NetherAdvancements(), new StoryAdvancements());

   public AdvancementProvider(DataGenerator p_i48869_1_) {
      this.generator = p_i48869_1_;
   }

   public void act(DirectoryCache p_200398_1_) throws IOException {
      Path lvt_2_1_ = this.generator.getOutputFolder();
      Set<ResourceLocation> lvt_3_1_ = Sets.newHashSet();
      Consumer<Advancement> lvt_4_1_ = (p_204017_3_) -> {
         if (!lvt_3_1_.add(p_204017_3_.getId())) {
            throw new IllegalStateException("Duplicate advancement " + p_204017_3_.getId());
         } else {
            Path lvt_4_1_ = getPath(lvt_2_1_, p_204017_3_);

            try {
               IDataProvider.save(GSON, p_200398_1_, p_204017_3_.copy().serialize(), lvt_4_1_);
            } catch (IOException var6) {
               LOGGER.error("Couldn't save advancement {}", lvt_4_1_, var6);
            }

         }
      };
      Iterator var5 = this.advancements.iterator();

      while(var5.hasNext()) {
         Consumer<Consumer<Advancement>> lvt_6_1_ = (Consumer)var5.next();
         lvt_6_1_.accept(lvt_4_1_);
      }

   }

   private static Path getPath(Path p_218428_0_, Advancement p_218428_1_) {
      return p_218428_0_.resolve("data/" + p_218428_1_.getId().getNamespace() + "/advancements/" + p_218428_1_.getId().getPath() + ".json");
   }

   public String getName() {
      return "Advancements";
   }
}
