package net.minecraftforge.common.util;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.Stat;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class FakePlayer extends ServerPlayerEntity {
   public FakePlayer(ServerWorld world, GameProfile name) {
      super(world.getServer(), world, name, new PlayerInteractionManager(world));
   }

   public Vec3d getPositionVector() {
      return new Vec3d(0.0D, 0.0D, 0.0D);
   }

   public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
   }

   public void sendMessage(ITextComponent component) {
   }

   public void addStat(Stat par1StatBase, int par2) {
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return true;
   }

   public boolean canAttackPlayer(PlayerEntity player) {
      return false;
   }

   public void onDeath(DamageSource source) {
   }

   public void tick() {
   }

   public void handleClientSettings(CClientSettingsPacket pkt) {
   }

   @Nullable
   public MinecraftServer getServer() {
      return ServerLifecycleHooks.getCurrentServer();
   }
}
