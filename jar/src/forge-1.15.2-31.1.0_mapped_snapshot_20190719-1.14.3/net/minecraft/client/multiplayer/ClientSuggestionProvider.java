package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSuggestionProvider implements ISuggestionProvider {
   private final ClientPlayNetHandler connection;
   private final Minecraft mc;
   private int currentTransaction = -1;
   private CompletableFuture<Suggestions> future;

   public ClientSuggestionProvider(ClientPlayNetHandler p_i49558_1_, Minecraft p_i49558_2_) {
      this.connection = p_i49558_1_;
      this.mc = p_i49558_2_;
   }

   public Collection<String> getPlayerNames() {
      List<String> lvt_1_1_ = Lists.newArrayList();
      Iterator var2 = this.connection.getPlayerInfoMap().iterator();

      while(var2.hasNext()) {
         NetworkPlayerInfo lvt_3_1_ = (NetworkPlayerInfo)var2.next();
         lvt_1_1_.add(lvt_3_1_.getGameProfile().getName());
      }

      return lvt_1_1_;
   }

   public Collection<String> getTargetedEntity() {
      return (Collection)(this.mc.objectMouseOver != null && this.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY ? Collections.singleton(((EntityRayTraceResult)this.mc.objectMouseOver).getEntity().getCachedUniqueIdString()) : Collections.emptyList());
   }

   public Collection<String> getTeamNames() {
      return this.connection.getWorld().getScoreboard().getTeamNames();
   }

   public Collection<ResourceLocation> getSoundResourceLocations() {
      return this.mc.getSoundHandler().getAvailableSounds();
   }

   public Stream<ResourceLocation> getRecipeResourceLocations() {
      return this.connection.getRecipeManager().getKeys();
   }

   public boolean hasPermissionLevel(int p_197034_1_) {
      ClientPlayerEntity lvt_2_1_ = this.mc.player;
      return lvt_2_1_ != null ? lvt_2_1_.hasPermissionLevel(p_197034_1_) : p_197034_1_ == 0;
   }

   public CompletableFuture<Suggestions> getSuggestionsFromServer(CommandContext<ISuggestionProvider> p_197009_1_, SuggestionsBuilder p_197009_2_) {
      if (this.future != null) {
         this.future.cancel(false);
      }

      this.future = new CompletableFuture();
      int lvt_3_1_ = ++this.currentTransaction;
      this.connection.sendPacket(new CTabCompletePacket(lvt_3_1_, p_197009_1_.getInput()));
      return this.future;
   }

   private static String formatDouble(double p_209001_0_) {
      return String.format(Locale.ROOT, "%.2f", p_209001_0_);
   }

   private static String formatInt(int p_209002_0_) {
      return Integer.toString(p_209002_0_);
   }

   public Collection<ISuggestionProvider.Coordinates> func_217294_q() {
      RayTraceResult lvt_1_1_ = this.mc.objectMouseOver;
      if (lvt_1_1_ != null && lvt_1_1_.getType() == RayTraceResult.Type.BLOCK) {
         BlockPos lvt_2_1_ = ((BlockRayTraceResult)lvt_1_1_).getPos();
         return Collections.singleton(new ISuggestionProvider.Coordinates(formatInt(lvt_2_1_.getX()), formatInt(lvt_2_1_.getY()), formatInt(lvt_2_1_.getZ())));
      } else {
         return ISuggestionProvider.super.func_217294_q();
      }
   }

   public Collection<ISuggestionProvider.Coordinates> func_217293_r() {
      RayTraceResult lvt_1_1_ = this.mc.objectMouseOver;
      if (lvt_1_1_ != null && lvt_1_1_.getType() == RayTraceResult.Type.BLOCK) {
         Vec3d lvt_2_1_ = lvt_1_1_.getHitVec();
         return Collections.singleton(new ISuggestionProvider.Coordinates(formatDouble(lvt_2_1_.x), formatDouble(lvt_2_1_.y), formatDouble(lvt_2_1_.z)));
      } else {
         return ISuggestionProvider.super.func_217293_r();
      }
   }

   public void handleResponse(int p_197015_1_, Suggestions p_197015_2_) {
      if (p_197015_1_ == this.currentTransaction) {
         this.future.complete(p_197015_2_);
         this.future = null;
         this.currentTransaction = -1;
      }

   }
}
