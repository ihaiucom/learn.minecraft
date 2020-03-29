package net.minecraft.advancements;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancements {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).setPrettyPrinting().create();
   private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> MAP_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>() {
   };
   private final MinecraftServer server;
   private final File progressFile;
   private final Map<Advancement, AdvancementProgress> progress = Maps.newLinkedHashMap();
   private final Set<Advancement> visible = Sets.newLinkedHashSet();
   private final Set<Advancement> visibilityChanged = Sets.newLinkedHashSet();
   private final Set<Advancement> progressChanged = Sets.newLinkedHashSet();
   private ServerPlayerEntity player;
   @Nullable
   private Advancement lastSelectedTab;
   private boolean isFirstPacket = true;

   public PlayerAdvancements(MinecraftServer p_i47422_1_, File p_i47422_2_, ServerPlayerEntity p_i47422_3_) {
      this.server = p_i47422_1_;
      this.progressFile = p_i47422_2_;
      this.player = p_i47422_3_;
      this.load();
   }

   public void setPlayer(ServerPlayerEntity p_192739_1_) {
      this.player = p_192739_1_;
   }

   public void dispose() {
      Iterator var1 = CriteriaTriggers.getAll().iterator();

      while(var1.hasNext()) {
         ICriterionTrigger<?> icriteriontrigger = (ICriterionTrigger)var1.next();
         icriteriontrigger.removeAllListeners(this);
      }

   }

   public void reload() {
      this.dispose();
      this.progress.clear();
      this.visible.clear();
      this.visibilityChanged.clear();
      this.progressChanged.clear();
      this.isFirstPacket = true;
      this.lastSelectedTab = null;
      this.load();
   }

   private void registerListeners() {
      Iterator var1 = this.server.getAdvancementManager().getAllAdvancements().iterator();

      while(var1.hasNext()) {
         Advancement advancement = (Advancement)var1.next();
         this.registerListeners(advancement);
      }

   }

   private void ensureAllVisible() {
      List<Advancement> list = Lists.newArrayList();
      Iterator var2 = this.progress.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<Advancement, AdvancementProgress> entry = (Entry)var2.next();
         if (((AdvancementProgress)entry.getValue()).isDone()) {
            list.add(entry.getKey());
            this.progressChanged.add(entry.getKey());
         }
      }

      var2 = list.iterator();

      while(var2.hasNext()) {
         Advancement advancement = (Advancement)var2.next();
         this.ensureVisibility(advancement);
      }

   }

   private void checkForAutomaticTriggers() {
      Iterator var1 = this.server.getAdvancementManager().getAllAdvancements().iterator();

      while(var1.hasNext()) {
         Advancement advancement = (Advancement)var1.next();
         if (advancement.getCriteria().isEmpty()) {
            this.grantCriterion(advancement, "");
            advancement.getRewards().apply(this.player);
         }
      }

   }

   private void load() {
      if (this.progressFile.isFile()) {
         try {
            JsonReader jsonreader = new JsonReader(new StringReader(Files.toString(this.progressFile, StandardCharsets.UTF_8)));
            Throwable var2 = null;

            try {
               jsonreader.setLenient(false);
               Dynamic<JsonElement> dynamic = new Dynamic(JsonOps.INSTANCE, Streams.parse(jsonreader));
               if (!dynamic.get("DataVersion").asNumber().isPresent()) {
                  dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
               }

               dynamic = this.server.getDataFixer().update(DefaultTypeReferences.ADVANCEMENTS.func_219816_a(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getVersion().getWorldVersion());
               dynamic = dynamic.remove("DataVersion");
               Map<ResourceLocation, AdvancementProgress> map = (Map)GSON.getAdapter(MAP_TOKEN).fromJsonTree((JsonElement)dynamic.getValue());
               if (map == null) {
                  throw new JsonParseException("Found null for advancements");
               }

               Stream<Entry<ResourceLocation, AdvancementProgress>> stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));
               Iterator var6 = ((List)stream.collect(Collectors.toList())).iterator();

               while(var6.hasNext()) {
                  Entry<ResourceLocation, AdvancementProgress> entry = (Entry)var6.next();
                  Advancement advancement = this.server.getAdvancementManager().getAdvancement((ResourceLocation)entry.getKey());
                  if (advancement == null) {
                     LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.progressFile);
                  } else {
                     this.startProgress(advancement, (AdvancementProgress)entry.getValue());
                  }
               }
            } catch (Throwable var18) {
               var2 = var18;
               throw var18;
            } finally {
               if (jsonreader != null) {
                  if (var2 != null) {
                     try {
                        jsonreader.close();
                     } catch (Throwable var17) {
                        var2.addSuppressed(var17);
                     }
                  } else {
                     jsonreader.close();
                  }
               }

            }
         } catch (JsonParseException var20) {
            LOGGER.error("Couldn't parse player advancements in {}", this.progressFile, var20);
         } catch (IOException var21) {
            LOGGER.error("Couldn't access player advancements in {}", this.progressFile, var21);
         }
      }

      this.checkForAutomaticTriggers();
      this.ensureAllVisible();
      this.registerListeners();
   }

   public void save() {
      Map<ResourceLocation, AdvancementProgress> map = Maps.newHashMap();
      Iterator var2 = this.progress.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<Advancement, AdvancementProgress> entry = (Entry)var2.next();
         AdvancementProgress advancementprogress = (AdvancementProgress)entry.getValue();
         if (advancementprogress.hasProgress()) {
            map.put(((Advancement)entry.getKey()).getId(), advancementprogress);
         }
      }

      if (this.progressFile.getParentFile() != null) {
         this.progressFile.getParentFile().mkdirs();
      }

      JsonElement jsonelement = GSON.toJsonTree(map);
      jsonelement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getVersion().getWorldVersion());

      try {
         OutputStream outputstream = new FileOutputStream(this.progressFile);
         Throwable var38 = null;

         try {
            Writer writer = new OutputStreamWriter(outputstream, Charsets.UTF_8.newEncoder());
            Throwable var6 = null;

            try {
               GSON.toJson(jsonelement, writer);
            } catch (Throwable var31) {
               var6 = var31;
               throw var31;
            } finally {
               if (writer != null) {
                  if (var6 != null) {
                     try {
                        writer.close();
                     } catch (Throwable var30) {
                        var6.addSuppressed(var30);
                     }
                  } else {
                     writer.close();
                  }
               }

            }
         } catch (Throwable var33) {
            var38 = var33;
            throw var33;
         } finally {
            if (outputstream != null) {
               if (var38 != null) {
                  try {
                     outputstream.close();
                  } catch (Throwable var29) {
                     var38.addSuppressed(var29);
                  }
               } else {
                  outputstream.close();
               }
            }

         }
      } catch (IOException var35) {
         LOGGER.error("Couldn't save player advancements to {}", this.progressFile, var35);
      }

   }

   public boolean grantCriterion(Advancement p_192750_1_, String p_192750_2_) {
      if (this.player instanceof FakePlayer) {
         return false;
      } else {
         boolean flag = false;
         AdvancementProgress advancementprogress = this.getProgress(p_192750_1_);
         boolean flag1 = advancementprogress.isDone();
         if (advancementprogress.grantCriterion(p_192750_2_)) {
            this.unregisterListeners(p_192750_1_);
            this.progressChanged.add(p_192750_1_);
            flag = true;
            if (!flag1 && advancementprogress.isDone()) {
               p_192750_1_.getRewards().apply(this.player);
               if (p_192750_1_.getDisplay() != null && p_192750_1_.getDisplay().shouldAnnounceToChat() && this.player.world.getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
                  this.server.getPlayerList().sendMessage(new TranslationTextComponent("chat.type.advancement." + p_192750_1_.getDisplay().getFrame().getName(), new Object[]{this.player.getDisplayName(), p_192750_1_.getDisplayText()}));
               }

               ForgeHooks.onAdvancement(this.player, p_192750_1_);
            }
         }

         if (advancementprogress.isDone()) {
            this.ensureVisibility(p_192750_1_);
         }

         return flag;
      }
   }

   public boolean revokeCriterion(Advancement p_192744_1_, String p_192744_2_) {
      boolean flag = false;
      AdvancementProgress advancementprogress = this.getProgress(p_192744_1_);
      if (advancementprogress.revokeCriterion(p_192744_2_)) {
         this.registerListeners(p_192744_1_);
         this.progressChanged.add(p_192744_1_);
         flag = true;
      }

      if (!advancementprogress.hasProgress()) {
         this.ensureVisibility(p_192744_1_);
      }

      return flag;
   }

   private void registerListeners(Advancement p_193764_1_) {
      AdvancementProgress advancementprogress = this.getProgress(p_193764_1_);
      if (!advancementprogress.isDone()) {
         Iterator var3 = p_193764_1_.getCriteria().entrySet().iterator();

         while(var3.hasNext()) {
            Entry<String, Criterion> entry = (Entry)var3.next();
            CriterionProgress criterionprogress = advancementprogress.getCriterionProgress((String)entry.getKey());
            if (criterionprogress != null && !criterionprogress.isObtained()) {
               ICriterionInstance icriterioninstance = ((Criterion)entry.getValue()).getCriterionInstance();
               if (icriterioninstance != null) {
                  ICriterionTrigger<ICriterionInstance> icriteriontrigger = CriteriaTriggers.get(icriterioninstance.getId());
                  if (icriteriontrigger != null) {
                     icriteriontrigger.addListener(this, new ICriterionTrigger.Listener(icriterioninstance, p_193764_1_, (String)entry.getKey()));
                  }
               }
            }
         }
      }

   }

   private void unregisterListeners(Advancement p_193765_1_) {
      AdvancementProgress advancementprogress = this.getProgress(p_193765_1_);
      Iterator var3 = p_193765_1_.getCriteria().entrySet().iterator();

      while(true) {
         Entry entry;
         CriterionProgress criterionprogress;
         do {
            do {
               if (!var3.hasNext()) {
                  return;
               }

               entry = (Entry)var3.next();
               criterionprogress = advancementprogress.getCriterionProgress((String)entry.getKey());
            } while(criterionprogress == null);
         } while(!criterionprogress.isObtained() && !advancementprogress.isDone());

         ICriterionInstance icriterioninstance = ((Criterion)entry.getValue()).getCriterionInstance();
         if (icriterioninstance != null) {
            ICriterionTrigger<ICriterionInstance> icriteriontrigger = CriteriaTriggers.get(icriterioninstance.getId());
            if (icriteriontrigger != null) {
               icriteriontrigger.removeListener(this, new ICriterionTrigger.Listener(icriterioninstance, p_193765_1_, (String)entry.getKey()));
            }
         }
      }
   }

   public void flushDirty(ServerPlayerEntity p_192741_1_) {
      if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
         Map<ResourceLocation, AdvancementProgress> map = Maps.newHashMap();
         Set<Advancement> set = Sets.newLinkedHashSet();
         Set<ResourceLocation> set1 = Sets.newLinkedHashSet();
         Iterator var5 = this.progressChanged.iterator();

         Advancement advancement1;
         while(var5.hasNext()) {
            advancement1 = (Advancement)var5.next();
            if (this.visible.contains(advancement1)) {
               map.put(advancement1.getId(), this.progress.get(advancement1));
            }
         }

         var5 = this.visibilityChanged.iterator();

         while(var5.hasNext()) {
            advancement1 = (Advancement)var5.next();
            if (this.visible.contains(advancement1)) {
               set.add(advancement1);
            } else {
               set1.add(advancement1.getId());
            }
         }

         if (this.isFirstPacket || !map.isEmpty() || !set.isEmpty() || !set1.isEmpty()) {
            p_192741_1_.connection.sendPacket(new SAdvancementInfoPacket(this.isFirstPacket, set, set1, map));
            this.visibilityChanged.clear();
            this.progressChanged.clear();
         }
      }

      this.isFirstPacket = false;
   }

   public void setSelectedTab(@Nullable Advancement p_194220_1_) {
      Advancement advancement = this.lastSelectedTab;
      if (p_194220_1_ != null && p_194220_1_.getParent() == null && p_194220_1_.getDisplay() != null) {
         this.lastSelectedTab = p_194220_1_;
      } else {
         this.lastSelectedTab = null;
      }

      if (advancement != this.lastSelectedTab) {
         this.player.connection.sendPacket(new SSelectAdvancementsTabPacket(this.lastSelectedTab == null ? null : this.lastSelectedTab.getId()));
      }

   }

   public AdvancementProgress getProgress(Advancement p_192747_1_) {
      AdvancementProgress advancementprogress = (AdvancementProgress)this.progress.get(p_192747_1_);
      if (advancementprogress == null) {
         advancementprogress = new AdvancementProgress();
         this.startProgress(p_192747_1_, advancementprogress);
      }

      return advancementprogress;
   }

   private void startProgress(Advancement p_192743_1_, AdvancementProgress p_192743_2_) {
      p_192743_2_.update(p_192743_1_.getCriteria(), p_192743_1_.getRequirements());
      this.progress.put(p_192743_1_, p_192743_2_);
   }

   private void ensureVisibility(Advancement p_192742_1_) {
      boolean flag = this.shouldBeVisible(p_192742_1_);
      boolean flag1 = this.visible.contains(p_192742_1_);
      if (flag && !flag1) {
         this.visible.add(p_192742_1_);
         this.visibilityChanged.add(p_192742_1_);
         if (this.progress.containsKey(p_192742_1_)) {
            this.progressChanged.add(p_192742_1_);
         }
      } else if (!flag && flag1) {
         this.visible.remove(p_192742_1_);
         this.visibilityChanged.add(p_192742_1_);
      }

      if (flag != flag1 && p_192742_1_.getParent() != null) {
         this.ensureVisibility(p_192742_1_.getParent());
      }

      Iterator var4 = p_192742_1_.getChildren().iterator();

      while(var4.hasNext()) {
         Advancement advancement = (Advancement)var4.next();
         this.ensureVisibility(advancement);
      }

   }

   private boolean shouldBeVisible(Advancement p_192738_1_) {
      for(int i = 0; p_192738_1_ != null && i <= 2; ++i) {
         if (i == 0 && this.hasCompletedChildrenOrSelf(p_192738_1_)) {
            return true;
         }

         if (p_192738_1_.getDisplay() == null) {
            return false;
         }

         AdvancementProgress advancementprogress = this.getProgress(p_192738_1_);
         if (advancementprogress.isDone()) {
            return true;
         }

         if (p_192738_1_.getDisplay().isHidden()) {
            return false;
         }

         p_192738_1_ = p_192738_1_.getParent();
      }

      return false;
   }

   private boolean hasCompletedChildrenOrSelf(Advancement p_192746_1_) {
      AdvancementProgress advancementprogress = this.getProgress(p_192746_1_);
      if (advancementprogress.isDone()) {
         return true;
      } else {
         Iterator var3 = p_192746_1_.getChildren().iterator();

         Advancement advancement;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            advancement = (Advancement)var3.next();
         } while(!this.hasCompletedChildrenOrSelf(advancement));

         return true;
      }
   }
}
