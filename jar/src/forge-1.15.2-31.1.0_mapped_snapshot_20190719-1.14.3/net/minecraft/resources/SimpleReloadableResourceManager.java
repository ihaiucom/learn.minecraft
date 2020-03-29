package net.minecraft.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, FallbackResourceManager> namespaceResourceManagers = Maps.newHashMap();
   private final List<IFutureReloadListener> reloadListeners = Lists.newArrayList();
   private final List<IFutureReloadListener> initTaskQueue = Lists.newArrayList();
   private final Set<String> resourceNamespaces = Sets.newLinkedHashSet();
   private final ResourcePackType type;
   private final Thread mainThread;

   public SimpleReloadableResourceManager(ResourcePackType p_i50689_1_, Thread p_i50689_2_) {
      this.type = p_i50689_1_;
      this.mainThread = p_i50689_2_;
   }

   public void addResourcePack(IResourcePack p_199021_1_) {
      FallbackResourceManager fallbackresourcemanager;
      for(Iterator var2 = p_199021_1_.getResourceNamespaces(this.type).iterator(); var2.hasNext(); fallbackresourcemanager.addResourcePack(p_199021_1_)) {
         String s = (String)var2.next();
         this.resourceNamespaces.add(s);
         fallbackresourcemanager = (FallbackResourceManager)this.namespaceResourceManagers.get(s);
         if (fallbackresourcemanager == null) {
            fallbackresourcemanager = new FallbackResourceManager(this.type, s);
            this.namespaceResourceManagers.put(s, fallbackresourcemanager);
         }
      }

   }

   public Set<String> getResourceNamespaces() {
      return this.resourceNamespaces;
   }

   public IResource getResource(ResourceLocation p_199002_1_) throws IOException {
      IResourceManager iresourcemanager = (IResourceManager)this.namespaceResourceManagers.get(p_199002_1_.getNamespace());
      if (iresourcemanager != null) {
         return iresourcemanager.getResource(p_199002_1_);
      } else {
         throw new FileNotFoundException(p_199002_1_.toString());
      }
   }

   public boolean hasResource(ResourceLocation p_219533_1_) {
      IResourceManager iresourcemanager = (IResourceManager)this.namespaceResourceManagers.get(p_219533_1_.getNamespace());
      return iresourcemanager != null ? iresourcemanager.hasResource(p_219533_1_) : false;
   }

   public List<IResource> getAllResources(ResourceLocation p_199004_1_) throws IOException {
      IResourceManager iresourcemanager = (IResourceManager)this.namespaceResourceManagers.get(p_199004_1_.getNamespace());
      if (iresourcemanager != null) {
         return iresourcemanager.getAllResources(p_199004_1_);
      } else {
         throw new FileNotFoundException(p_199004_1_.toString());
      }
   }

   public Collection<ResourceLocation> getAllResourceLocations(String p_199003_1_, Predicate<String> p_199003_2_) {
      Set<ResourceLocation> set = Sets.newHashSet();
      Iterator var4 = this.namespaceResourceManagers.values().iterator();

      while(var4.hasNext()) {
         FallbackResourceManager fallbackresourcemanager = (FallbackResourceManager)var4.next();
         set.addAll(fallbackresourcemanager.getAllResourceLocations(p_199003_1_, p_199003_2_));
      }

      List<ResourceLocation> list = Lists.newArrayList(set);
      Collections.sort(list);
      return list;
   }

   private void clearResourceNamespaces() {
      this.namespaceResourceManagers.clear();
      this.resourceNamespaces.clear();
   }

   public CompletableFuture<Unit> reloadResourcesAndThen(Executor p_219536_1_, Executor p_219536_2_, List<IResourcePack> p_219536_3_, CompletableFuture<Unit> p_219536_4_) {
      IAsyncReloader iasyncreloader = this.reloadResources(p_219536_1_, p_219536_2_, p_219536_4_, p_219536_3_);
      return iasyncreloader.onceDone();
   }

   public void addReloadListener(IFutureReloadListener p_219534_1_) {
      this.reloadListeners.add(p_219534_1_);
      this.initTaskQueue.add(p_219534_1_);
   }

   protected IAsyncReloader initializeAsyncReloader(Executor p_219538_1_, Executor p_219538_2_, List<IFutureReloadListener> p_219538_3_, CompletableFuture<Unit> p_219538_4_) {
      Object iasyncreloader;
      if (LOGGER.isDebugEnabled()) {
         iasyncreloader = new DebugAsyncReloader(this, Lists.newArrayList(p_219538_3_), p_219538_1_, p_219538_2_, p_219538_4_);
      } else {
         iasyncreloader = AsyncReloader.create(this, Lists.newArrayList(p_219538_3_), p_219538_1_, p_219538_2_, p_219538_4_);
      }

      this.initTaskQueue.clear();
      return (IAsyncReloader)iasyncreloader;
   }

   public IAsyncReloader reloadResources(Executor p_219537_1_, Executor p_219537_2_, CompletableFuture<Unit> p_219537_3_, List<IResourcePack> p_219537_4_) {
      this.clearResourceNamespaces();
      LOGGER.info("Reloading ResourceManager: {}", p_219537_4_.stream().map(IResourcePack::getName).collect(Collectors.joining(", ")));
      Iterator var5 = p_219537_4_.iterator();

      while(var5.hasNext()) {
         IResourcePack iresourcepack = (IResourcePack)var5.next();

         try {
            this.addResourcePack(iresourcepack);
         } catch (Exception var8) {
            LOGGER.error("Failed to add resource pack {}", iresourcepack.getName(), var8);
            return new SimpleReloadableResourceManager.FailedPackReloader(new SimpleReloadableResourceManager.FailedPackException(iresourcepack, var8));
         }
      }

      return this.initializeAsyncReloader(p_219537_1_, p_219537_2_, this.reloadListeners, p_219537_3_);
   }

   static class FailedPackReloader implements IAsyncReloader {
      private final SimpleReloadableResourceManager.FailedPackException field_230019_a_;
      private final CompletableFuture<Unit> field_230020_b_;

      public FailedPackReloader(SimpleReloadableResourceManager.FailedPackException p_i229961_1_) {
         this.field_230019_a_ = p_i229961_1_;
         this.field_230020_b_ = new CompletableFuture();
         this.field_230020_b_.completeExceptionally(p_i229961_1_);
      }

      public CompletableFuture<Unit> onceDone() {
         return this.field_230020_b_;
      }

      @OnlyIn(Dist.CLIENT)
      public float estimateExecutionSpeed() {
         return 0.0F;
      }

      @OnlyIn(Dist.CLIENT)
      public boolean asyncPartDone() {
         return false;
      }

      @OnlyIn(Dist.CLIENT)
      public boolean fullyDone() {
         return true;
      }

      @OnlyIn(Dist.CLIENT)
      public void join() {
         throw this.field_230019_a_;
      }
   }

   public static class FailedPackException extends RuntimeException {
      private final IResourcePack field_230021_a_;

      public FailedPackException(IResourcePack p_i229962_1_, Throwable p_i229962_2_) {
         super(p_i229962_1_.getName(), p_i229962_2_);
         this.field_230021_a_ = p_i229962_1_;
      }

      @OnlyIn(Dist.CLIENT)
      public IResourcePack func_230028_a() {
         return this.field_230021_a_;
      }
   }
}
