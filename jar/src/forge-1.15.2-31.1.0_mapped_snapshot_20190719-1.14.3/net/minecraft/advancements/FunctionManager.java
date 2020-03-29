package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.SimpleResource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation TICK_TAG_ID = new ResourceLocation("tick");
   private static final ResourceLocation LOAD_TAG_ID = new ResourceLocation("load");
   public static final int PATH_PREFIX_LENGTH = "functions/".length();
   public static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
   private final MinecraftServer server;
   private final Map<ResourceLocation, FunctionObject> functions = Maps.newHashMap();
   private boolean isExecuting;
   private final ArrayDeque<FunctionManager.QueuedCommand> commandQueue = new ArrayDeque();
   private final List<FunctionManager.QueuedCommand> commandChain = Lists.newArrayList();
   private final TagCollection<FunctionObject> tagCollection = new TagCollection(this::get, "tags/functions", true, "function");
   private final List<FunctionObject> tickFunctions = Lists.newArrayList();
   private boolean loadFunctionsRun;

   public FunctionManager(MinecraftServer p_i47920_1_) {
      this.server = p_i47920_1_;
   }

   public Optional<FunctionObject> get(ResourceLocation p_215361_1_) {
      return Optional.ofNullable(this.functions.get(p_215361_1_));
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public int getMaxCommandChainLength() {
      return this.server.getGameRules().getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
   }

   public Map<ResourceLocation, FunctionObject> getFunctions() {
      return this.functions;
   }

   public CommandDispatcher<CommandSource> getCommandDispatcher() {
      return this.server.getCommandManager().getDispatcher();
   }

   public void tick() {
      DebugProfiler var10000 = this.server.getProfiler();
      ResourceLocation var10001 = TICK_TAG_ID;
      var10000.startSection(var10001::toString);
      Iterator var1 = this.tickFunctions.iterator();

      while(var1.hasNext()) {
         FunctionObject lvt_2_1_ = (FunctionObject)var1.next();
         this.execute(lvt_2_1_, this.getCommandSource());
      }

      this.server.getProfiler().endSection();
      if (this.loadFunctionsRun) {
         this.loadFunctionsRun = false;
         Collection<FunctionObject> lvt_1_1_ = this.getTagCollection().getOrCreate(LOAD_TAG_ID).getAllElements();
         var10000 = this.server.getProfiler();
         var10001 = LOAD_TAG_ID;
         var10000.startSection(var10001::toString);
         Iterator var5 = lvt_1_1_.iterator();

         while(var5.hasNext()) {
            FunctionObject lvt_3_1_ = (FunctionObject)var5.next();
            this.execute(lvt_3_1_, this.getCommandSource());
         }

         this.server.getProfiler().endSection();
      }

   }

   public int execute(FunctionObject p_195447_1_, CommandSource p_195447_2_) {
      int lvt_3_1_ = this.getMaxCommandChainLength();
      if (this.isExecuting) {
         if (this.commandQueue.size() + this.commandChain.size() < lvt_3_1_) {
            this.commandChain.add(new FunctionManager.QueuedCommand(this, p_195447_2_, new FunctionObject.FunctionEntry(p_195447_1_)));
         }

         return 0;
      } else {
         int lvt_6_1_;
         try {
            this.isExecuting = true;
            int lvt_4_1_ = 0;
            FunctionObject.IEntry[] lvt_5_1_ = p_195447_1_.getEntries();

            for(lvt_6_1_ = lvt_5_1_.length - 1; lvt_6_1_ >= 0; --lvt_6_1_) {
               this.commandQueue.push(new FunctionManager.QueuedCommand(this, p_195447_2_, lvt_5_1_[lvt_6_1_]));
            }

            while(!this.commandQueue.isEmpty()) {
               try {
                  FunctionManager.QueuedCommand lvt_6_2_ = (FunctionManager.QueuedCommand)this.commandQueue.removeFirst();
                  this.server.getProfiler().startSection(lvt_6_2_::toString);
                  lvt_6_2_.execute(this.commandQueue, lvt_3_1_);
                  if (!this.commandChain.isEmpty()) {
                     List var10000 = Lists.reverse(this.commandChain);
                     ArrayDeque var10001 = this.commandQueue;
                     var10000.forEach(var10001::addFirst);
                     this.commandChain.clear();
                  }
               } finally {
                  this.server.getProfiler().endSection();
               }

               ++lvt_4_1_;
               if (lvt_4_1_ >= lvt_3_1_) {
                  lvt_6_1_ = lvt_4_1_;
                  return lvt_6_1_;
               }
            }

            lvt_6_1_ = lvt_4_1_;
         } finally {
            this.commandQueue.clear();
            this.commandChain.clear();
            this.isExecuting = false;
         }

         return lvt_6_1_;
      }
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.functions.clear();
      this.tickFunctions.clear();
      Collection<ResourceLocation> lvt_2_1_ = p_195410_1_.getAllResourceLocations("functions", (p_215364_0_) -> {
         return p_215364_0_.endsWith(".mcfunction");
      });
      List<CompletableFuture<FunctionObject>> lvt_3_1_ = Lists.newArrayList();
      Iterator var4 = lvt_2_1_.iterator();

      while(var4.hasNext()) {
         ResourceLocation lvt_5_1_ = (ResourceLocation)var4.next();
         String lvt_6_1_ = lvt_5_1_.getPath();
         ResourceLocation lvt_7_1_ = new ResourceLocation(lvt_5_1_.getNamespace(), lvt_6_1_.substring(PATH_PREFIX_LENGTH, lvt_6_1_.length() - PATH_SUFFIX_LENGTH));
         lvt_3_1_.add(CompletableFuture.supplyAsync(() -> {
            return readLines(p_195410_1_, lvt_5_1_);
         }, SimpleResource.RESOURCE_IO_EXECUTOR).thenApplyAsync((p_215365_2_) -> {
            return FunctionObject.create(lvt_7_1_, this, p_215365_2_);
         }, this.server.getBackgroundExecutor()).handle((p_215362_2_, p_215362_3_) -> {
            return this.load(p_215362_2_, p_215362_3_, lvt_5_1_);
         }));
      }

      CompletableFuture.allOf((CompletableFuture[])lvt_3_1_.toArray(new CompletableFuture[0])).join();
      if (!this.functions.isEmpty()) {
         LOGGER.info("Loaded {} custom command functions", this.functions.size());
      }

      this.tagCollection.registerAll((Map)this.tagCollection.reload(p_195410_1_, this.server.getBackgroundExecutor()).join());
      this.tickFunctions.addAll(this.tagCollection.getOrCreate(TICK_TAG_ID).getAllElements());
      this.loadFunctionsRun = true;
   }

   @Nullable
   private FunctionObject load(FunctionObject p_212250_1_, @Nullable Throwable p_212250_2_, ResourceLocation p_212250_3_) {
      if (p_212250_2_ != null) {
         LOGGER.error("Couldn't load function at {}", p_212250_3_, p_212250_2_);
         return null;
      } else {
         synchronized(this.functions) {
            this.functions.put(p_212250_1_.getId(), p_212250_1_);
            return p_212250_1_;
         }
      }
   }

   private static List<String> readLines(IResourceManager p_195449_0_, ResourceLocation p_195449_1_) {
      try {
         IResource lvt_2_1_ = p_195449_0_.getResource(p_195449_1_);
         Throwable var3 = null;

         List var4;
         try {
            var4 = IOUtils.readLines(lvt_2_1_.getInputStream(), StandardCharsets.UTF_8);
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (lvt_2_1_ != null) {
               if (var3 != null) {
                  try {
                     lvt_2_1_.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  lvt_2_1_.close();
               }
            }

         }

         return var4;
      } catch (IOException var16) {
         throw new CompletionException(var16);
      }
   }

   public CommandSource getCommandSource() {
      return this.server.getCommandSource().withPermissionLevel(2).withFeedbackDisabled();
   }

   public CommandSource func_223402_g() {
      return new CommandSource(ICommandSource.field_213139_a_, Vec3d.ZERO, Vec2f.ZERO, (ServerWorld)null, this.server.func_223707_k(), "", new StringTextComponent(""), this.server, (Entity)null);
   }

   public TagCollection<FunctionObject> getTagCollection() {
      return this.tagCollection;
   }

   public static class QueuedCommand {
      private final FunctionManager functionManager;
      private final CommandSource sender;
      private final FunctionObject.IEntry entry;

      public QueuedCommand(FunctionManager p_i48018_1_, CommandSource p_i48018_2_, FunctionObject.IEntry p_i48018_3_) {
         this.functionManager = p_i48018_1_;
         this.sender = p_i48018_2_;
         this.entry = p_i48018_3_;
      }

      public void execute(ArrayDeque<FunctionManager.QueuedCommand> p_194222_1_, int p_194222_2_) {
         try {
            this.entry.execute(this.functionManager, this.sender, p_194222_1_, p_194222_2_);
         } catch (Throwable var4) {
         }

      }

      public String toString() {
         return this.entry.toString();
      }
   }
}
