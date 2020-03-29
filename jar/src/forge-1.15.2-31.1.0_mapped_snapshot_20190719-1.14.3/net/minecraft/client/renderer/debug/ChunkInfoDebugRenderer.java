package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkInfoDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft client;
   private double field_217679_b = Double.MIN_VALUE;
   private final int field_217680_c = 12;
   @Nullable
   private ChunkInfoDebugRenderer.Entry field_217681_d;

   public ChunkInfoDebugRenderer(Minecraft p_i50978_1_) {
      this.client = p_i50978_1_;
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      double lvt_9_1_ = (double)Util.nanoTime();
      if (lvt_9_1_ - this.field_217679_b > 3.0E9D) {
         this.field_217679_b = lvt_9_1_;
         IntegratedServer lvt_11_1_ = this.client.getIntegratedServer();
         if (lvt_11_1_ != null) {
            this.field_217681_d = new ChunkInfoDebugRenderer.Entry(lvt_11_1_, p_225619_3_, p_225619_7_);
         } else {
            this.field_217681_d = null;
         }
      }

      if (this.field_217681_d != null) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.lineWidth(2.0F);
         RenderSystem.disableTexture();
         RenderSystem.depthMask(false);
         Map<ChunkPos, String> lvt_11_2_ = (Map)this.field_217681_d.field_217722_c.getNow((Object)null);
         double lvt_12_1_ = this.client.gameRenderer.getActiveRenderInfo().getProjectedView().y * 0.85D;
         Iterator var14 = this.field_217681_d.field_217721_b.entrySet().iterator();

         while(var14.hasNext()) {
            java.util.Map.Entry<ChunkPos, String> lvt_15_1_ = (java.util.Map.Entry)var14.next();
            ChunkPos lvt_16_1_ = (ChunkPos)lvt_15_1_.getKey();
            String lvt_17_1_ = (String)lvt_15_1_.getValue();
            if (lvt_11_2_ != null) {
               lvt_17_1_ = lvt_17_1_ + (String)lvt_11_2_.get(lvt_16_1_);
            }

            String[] lvt_18_1_ = lvt_17_1_.split("\n");
            int lvt_19_1_ = 0;
            String[] var20 = lvt_18_1_;
            int var21 = lvt_18_1_.length;

            for(int var22 = 0; var22 < var21; ++var22) {
               String lvt_23_1_ = var20[var22];
               DebugRenderer.func_217729_a(lvt_23_1_, (double)((lvt_16_1_.x << 4) + 8), lvt_12_1_ + (double)lvt_19_1_, (double)((lvt_16_1_.z << 4) + 8), -1, 0.15F);
               lvt_19_1_ -= 2;
            }
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableTexture();
         RenderSystem.disableBlend();
      }

   }

   @OnlyIn(Dist.CLIENT)
   final class Entry {
      private final Map<ChunkPos, String> field_217721_b;
      private final CompletableFuture<Map<ChunkPos, String>> field_217722_c;

      private Entry(IntegratedServer p_i226030_2_, double p_i226030_3_, double p_i226030_5_) {
         ClientWorld lvt_7_1_ = ChunkInfoDebugRenderer.this.client.world;
         DimensionType lvt_8_1_ = ChunkInfoDebugRenderer.this.client.world.dimension.getType();
         ServerWorld lvt_9_2_;
         if (p_i226030_2_.getWorld(lvt_8_1_) != null) {
            lvt_9_2_ = p_i226030_2_.getWorld(lvt_8_1_);
         } else {
            lvt_9_2_ = null;
         }

         int lvt_10_1_ = (int)p_i226030_3_ >> 4;
         int lvt_11_1_ = (int)p_i226030_5_ >> 4;
         Builder<ChunkPos, String> lvt_12_1_ = ImmutableMap.builder();
         ClientChunkProvider lvt_13_1_ = lvt_7_1_.getChunkProvider();

         for(int lvt_14_1_ = lvt_10_1_ - 12; lvt_14_1_ <= lvt_10_1_ + 12; ++lvt_14_1_) {
            for(int lvt_15_1_ = lvt_11_1_ - 12; lvt_15_1_ <= lvt_11_1_ + 12; ++lvt_15_1_) {
               ChunkPos lvt_16_1_ = new ChunkPos(lvt_14_1_, lvt_15_1_);
               String lvt_17_1_ = "";
               Chunk lvt_18_1_ = lvt_13_1_.getChunk(lvt_14_1_, lvt_15_1_, false);
               lvt_17_1_ = lvt_17_1_ + "Client: ";
               if (lvt_18_1_ == null) {
                  lvt_17_1_ = lvt_17_1_ + "0n/a\n";
               } else {
                  lvt_17_1_ = lvt_17_1_ + (lvt_18_1_.isEmpty() ? " E" : "");
                  lvt_17_1_ = lvt_17_1_ + "\n";
               }

               lvt_12_1_.put(lvt_16_1_, lvt_17_1_);
            }
         }

         this.field_217721_b = lvt_12_1_.build();
         this.field_217722_c = p_i226030_2_.supplyAsync(() -> {
            Builder<ChunkPos, String> lvt_4_1_ = ImmutableMap.builder();
            ServerChunkProvider lvt_5_1_ = lvt_9_2_.getChunkProvider();

            for(int lvt_6_1_ = lvt_10_1_ - 12; lvt_6_1_ <= lvt_10_1_ + 12; ++lvt_6_1_) {
               for(int lvt_7_1_ = lvt_11_1_ - 12; lvt_7_1_ <= lvt_11_1_ + 12; ++lvt_7_1_) {
                  ChunkPos lvt_8_1_ = new ChunkPos(lvt_6_1_, lvt_7_1_);
                  lvt_4_1_.put(lvt_8_1_, "Server: " + lvt_5_1_.func_217208_a(lvt_8_1_));
               }
            }

            return lvt_4_1_.build();
         });
      }

      // $FF: synthetic method
      Entry(IntegratedServer p_i226031_2_, double p_i226031_3_, double p_i226031_5_, Object p_i226031_7_) {
         this(p_i226031_2_, p_i226031_3_, p_i226031_5_);
      }
   }
}
