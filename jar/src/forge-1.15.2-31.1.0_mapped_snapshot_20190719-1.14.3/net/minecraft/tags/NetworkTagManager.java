package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;

public class NetworkTagManager implements IFutureReloadListener {
   private final NetworkTagCollection<Block> blocks;
   private final NetworkTagCollection<Item> items;
   private final NetworkTagCollection<Fluid> fluids;
   private final NetworkTagCollection<EntityType<?>> entityTypes;

   public NetworkTagManager() {
      this.blocks = new NetworkTagCollection(Registry.BLOCK, "tags/blocks", "block");
      this.items = new NetworkTagCollection(Registry.ITEM, "tags/items", "item");
      this.fluids = new NetworkTagCollection(Registry.FLUID, "tags/fluids", "fluid");
      this.entityTypes = new NetworkTagCollection(Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");
   }

   public NetworkTagCollection<Block> getBlocks() {
      return this.blocks;
   }

   public NetworkTagCollection<Item> getItems() {
      return this.items;
   }

   public NetworkTagCollection<Fluid> getFluids() {
      return this.fluids;
   }

   public NetworkTagCollection<EntityType<?>> getEntityTypes() {
      return this.entityTypes;
   }

   public void write(PacketBuffer p_199716_1_) {
      this.blocks.write(p_199716_1_);
      this.items.write(p_199716_1_);
      this.fluids.write(p_199716_1_);
      this.entityTypes.write(p_199716_1_);
   }

   public static NetworkTagManager read(PacketBuffer p_199714_0_) {
      NetworkTagManager networktagmanager = new NetworkTagManager();
      networktagmanager.getBlocks().read(p_199714_0_);
      networktagmanager.getItems().read(p_199714_0_);
      networktagmanager.getFluids().read(p_199714_0_);
      networktagmanager.getEntityTypes().read(p_199714_0_);
      return networktagmanager;
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Block>>> completablefuture = this.blocks.reload(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Item>>> completablefuture1 = this.items.reload(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Fluid>>> completablefuture2 = this.fluids.reload(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<EntityType<?>>>> completablefuture3 = this.entityTypes.reload(p_215226_2_, p_215226_5_);
      CompletableFuture var10000 = completablefuture.thenCombine(completablefuture1, Pair::of).thenCombine(completablefuture2.thenCombine(completablefuture3, Pair::of), (p_lambda$reload$0_0_, p_lambda$reload$0_1_) -> {
         return new NetworkTagManager.ReloadResults((Map)p_lambda$reload$0_0_.getFirst(), (Map)p_lambda$reload$0_0_.getSecond(), (Map)p_lambda$reload$0_1_.getFirst(), (Map)p_lambda$reload$0_1_.getSecond());
      });
      p_215226_1_.getClass();
      return var10000.thenCompose(p_215226_1_::markCompleteAwaitingOthers).thenAcceptAsync((p_lambda$reload$1_1_) -> {
         this.blocks.registerAll(p_lambda$reload$1_1_.blocks);
         this.items.registerAll(p_lambda$reload$1_1_.items);
         this.fluids.registerAll(p_lambda$reload$1_1_.fluids);
         this.entityTypes.registerAll(p_lambda$reload$1_1_.entityTypes);
         BlockTags.setCollection(this.blocks);
         ItemTags.setCollection(this.items);
         FluidTags.setCollection(this.fluids);
         EntityTypeTags.setCollection(this.entityTypes);
         MinecraftForge.EVENT_BUS.post(new TagsUpdatedEvent(this));
      }, p_215226_6_);
   }

   public static class ReloadResults {
      final Map<ResourceLocation, Tag.Builder<Block>> blocks;
      final Map<ResourceLocation, Tag.Builder<Item>> items;
      final Map<ResourceLocation, Tag.Builder<Fluid>> fluids;
      final Map<ResourceLocation, Tag.Builder<EntityType<?>>> entityTypes;

      public ReloadResults(Map<ResourceLocation, Tag.Builder<Block>> p_i50480_1_, Map<ResourceLocation, Tag.Builder<Item>> p_i50480_2_, Map<ResourceLocation, Tag.Builder<Fluid>> p_i50480_3_, Map<ResourceLocation, Tag.Builder<EntityType<?>>> p_i50480_4_) {
         this.blocks = p_i50480_1_;
         this.items = p_i50480_2_;
         this.fluids = p_i50480_3_;
         this.entityTypes = p_i50480_4_;
      }
   }
}
