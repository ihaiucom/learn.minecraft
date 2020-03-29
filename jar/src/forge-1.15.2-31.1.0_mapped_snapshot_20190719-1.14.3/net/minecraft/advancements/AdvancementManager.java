package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementManager extends JsonReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.Builder.class, (p_lambda$static$0_0_, p_lambda$static$0_1_, p_lambda$static$0_2_) -> {
      JsonObject jsonobject = JSONUtils.getJsonObject(p_lambda$static$0_0_, "advancement");
      return Advancement.Builder.deserialize(jsonobject, p_lambda$static$0_2_);
   }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
   private AdvancementList field_223388_c = new AdvancementList();

   public AdvancementManager() {
      super(GSON, "advancements");
   }

   protected void apply(Map<ResourceLocation, JsonObject> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      Map<ResourceLocation, Advancement.Builder> map = Maps.newHashMap();
      p_212853_1_.forEach((p_lambda$apply$1_1_, p_lambda$apply$1_2_) -> {
         try {
            Advancement.Builder advancement$builder = ConditionalAdvancement.read(GSON, p_lambda$apply$1_1_, p_lambda$apply$1_2_);
            if (advancement$builder == null) {
               LOGGER.info("Skipping loading advancement {} as it's conditions were not met", p_lambda$apply$1_1_);
               return;
            }

            map.put(p_lambda$apply$1_1_, advancement$builder);
         } catch (JsonParseException | IllegalArgumentException var4) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", p_lambda$apply$1_1_, var4.getMessage());
         }

      });
      AdvancementList advancementlist = new AdvancementList();
      advancementlist.loadAdvancements(map);
      Iterator var6 = advancementlist.getRoots().iterator();

      while(var6.hasNext()) {
         Advancement advancement = (Advancement)var6.next();
         if (advancement.getDisplay() != null) {
            AdvancementTreeNode.layout(advancement);
         }
      }

      this.field_223388_c = advancementlist;
   }

   @Nullable
   public Advancement getAdvancement(ResourceLocation p_192778_1_) {
      return this.field_223388_c.getAdvancement(p_192778_1_);
   }

   public Collection<Advancement> getAllAdvancements() {
      return this.field_223388_c.getAll();
   }
}
