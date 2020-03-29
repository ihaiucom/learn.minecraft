package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SStatisticsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatisticsManager extends StatisticsManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   private final File statsFile;
   private final Set<Stat<?>> dirty = Sets.newHashSet();
   private int lastStatRequest = -300;

   public ServerStatisticsManager(MinecraftServer p_i45306_1_, File p_i45306_2_) {
      this.server = p_i45306_1_;
      this.statsFile = p_i45306_2_;
      if (p_i45306_2_.isFile()) {
         try {
            this.parseLocal(p_i45306_1_.getDataFixer(), FileUtils.readFileToString(p_i45306_2_));
         } catch (IOException var4) {
            LOGGER.error("Couldn't read statistics file {}", p_i45306_2_, var4);
         } catch (JsonParseException var5) {
            LOGGER.error("Couldn't parse statistics file {}", p_i45306_2_, var5);
         }
      }

   }

   public void saveStatFile() {
      try {
         FileUtils.writeStringToFile(this.statsFile, this.func_199061_b());
      } catch (IOException var2) {
         LOGGER.error("Couldn't save stats", var2);
      }

   }

   public void setValue(PlayerEntity p_150873_1_, Stat<?> p_150873_2_, int p_150873_3_) {
      super.setValue(p_150873_1_, p_150873_2_, p_150873_3_);
      this.dirty.add(p_150873_2_);
   }

   private Set<Stat<?>> getDirty() {
      Set<Stat<?>> lvt_1_1_ = Sets.newHashSet(this.dirty);
      this.dirty.clear();
      return lvt_1_1_;
   }

   public void parseLocal(DataFixer p_199062_1_, String p_199062_2_) {
      try {
         JsonReader lvt_3_1_ = new JsonReader(new StringReader(p_199062_2_));
         Throwable var4 = null;

         try {
            lvt_3_1_.setLenient(false);
            JsonElement lvt_5_1_ = Streams.parse(lvt_3_1_);
            if (!lvt_5_1_.isJsonNull()) {
               CompoundNBT lvt_6_1_ = func_199065_a(lvt_5_1_.getAsJsonObject());
               if (!lvt_6_1_.contains("DataVersion", 99)) {
                  lvt_6_1_.putInt("DataVersion", 1343);
               }

               lvt_6_1_ = NBTUtil.update(p_199062_1_, DefaultTypeReferences.STATS, lvt_6_1_, lvt_6_1_.getInt("DataVersion"));
               if (lvt_6_1_.contains("stats", 10)) {
                  CompoundNBT lvt_7_1_ = lvt_6_1_.getCompound("stats");
                  Iterator var8 = lvt_7_1_.keySet().iterator();

                  while(var8.hasNext()) {
                     String lvt_9_1_ = (String)var8.next();
                     if (lvt_7_1_.contains(lvt_9_1_, 10)) {
                        Util.acceptOrElse(Registry.STATS.getValue(new ResourceLocation(lvt_9_1_)), (p_219731_3_) -> {
                           CompoundNBT lvt_4_1_ = lvt_7_1_.getCompound(lvt_9_1_);
                           Iterator var5 = lvt_4_1_.keySet().iterator();

                           while(var5.hasNext()) {
                              String lvt_6_1_ = (String)var5.next();
                              if (lvt_4_1_.contains(lvt_6_1_, 99)) {
                                 Util.acceptOrElse(this.func_219728_a(p_219731_3_, lvt_6_1_), (p_219730_3_) -> {
                                    this.statsData.put(p_219730_3_, lvt_4_1_.getInt(lvt_6_1_));
                                 }, () -> {
                                    LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.statsFile, lvt_6_1_);
                                 });
                              } else {
                                 LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.statsFile, lvt_4_1_.get(lvt_6_1_), lvt_6_1_);
                              }
                           }

                        }, () -> {
                           LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.statsFile, lvt_9_1_);
                        });
                     }
                  }
               }

               return;
            }

            LOGGER.error("Unable to parse Stat data from {}", this.statsFile);
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if (lvt_3_1_ != null) {
               if (var4 != null) {
                  try {
                     lvt_3_1_.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  lvt_3_1_.close();
               }
            }

         }

      } catch (IOException | JsonParseException var21) {
         LOGGER.error("Unable to parse Stat data from {}", this.statsFile, var21);
      }
   }

   private <T> Optional<Stat<T>> func_219728_a(StatType<T> p_219728_1_, String p_219728_2_) {
      Optional var10000 = Optional.ofNullable(ResourceLocation.tryCreate(p_219728_2_));
      Registry var10001 = p_219728_1_.getRegistry();
      var10001.getClass();
      var10000 = var10000.flatMap(var10001::getValue);
      p_219728_1_.getClass();
      return var10000.map(p_219728_1_::get);
   }

   private static CompoundNBT func_199065_a(JsonObject p_199065_0_) {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      Iterator var2 = p_199065_0_.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, JsonElement> lvt_3_1_ = (Entry)var2.next();
         JsonElement lvt_4_1_ = (JsonElement)lvt_3_1_.getValue();
         if (lvt_4_1_.isJsonObject()) {
            lvt_1_1_.put((String)lvt_3_1_.getKey(), func_199065_a(lvt_4_1_.getAsJsonObject()));
         } else if (lvt_4_1_.isJsonPrimitive()) {
            JsonPrimitive lvt_5_1_ = lvt_4_1_.getAsJsonPrimitive();
            if (lvt_5_1_.isNumber()) {
               lvt_1_1_.putInt((String)lvt_3_1_.getKey(), lvt_5_1_.getAsInt());
            }
         }
      }

      return lvt_1_1_;
   }

   protected String func_199061_b() {
      Map<StatType<?>, JsonObject> lvt_1_1_ = Maps.newHashMap();
      ObjectIterator var2 = this.statsData.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Stat<?>> lvt_3_1_ = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry)var2.next();
         Stat<?> lvt_4_1_ = (Stat)lvt_3_1_.getKey();
         ((JsonObject)lvt_1_1_.computeIfAbsent(lvt_4_1_.getType(), (p_199064_0_) -> {
            return new JsonObject();
         })).addProperty(func_199066_b(lvt_4_1_).toString(), lvt_3_1_.getIntValue());
      }

      JsonObject lvt_2_1_ = new JsonObject();
      Iterator var6 = lvt_1_1_.entrySet().iterator();

      while(var6.hasNext()) {
         Entry<StatType<?>, JsonObject> lvt_4_2_ = (Entry)var6.next();
         lvt_2_1_.add(Registry.STATS.getKey(lvt_4_2_.getKey()).toString(), (JsonElement)lvt_4_2_.getValue());
      }

      JsonObject lvt_3_2_ = new JsonObject();
      lvt_3_2_.add("stats", lvt_2_1_);
      lvt_3_2_.addProperty("DataVersion", SharedConstants.getVersion().getWorldVersion());
      return lvt_3_2_.toString();
   }

   private static <T> ResourceLocation func_199066_b(Stat<T> p_199066_0_) {
      return p_199066_0_.getType().getRegistry().getKey(p_199066_0_.getValue());
   }

   public void markAllDirty() {
      this.dirty.addAll(this.statsData.keySet());
   }

   public void sendStats(ServerPlayerEntity p_150876_1_) {
      int lvt_2_1_ = this.server.getTickCounter();
      Object2IntMap<Stat<?>> lvt_3_1_ = new Object2IntOpenHashMap();
      if (lvt_2_1_ - this.lastStatRequest > 300) {
         this.lastStatRequest = lvt_2_1_;
         Iterator var4 = this.getDirty().iterator();

         while(var4.hasNext()) {
            Stat<?> lvt_5_1_ = (Stat)var4.next();
            lvt_3_1_.put(lvt_5_1_, this.getValue(lvt_5_1_));
         }
      }

      p_150876_1_.connection.sendPacket(new SStatisticsPacket(lvt_3_1_));
   }
}
