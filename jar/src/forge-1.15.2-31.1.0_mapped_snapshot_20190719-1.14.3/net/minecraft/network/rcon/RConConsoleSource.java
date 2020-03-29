package net.minecraft.network.rcon;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class RConConsoleSource implements ICommandSource {
   private final StringBuffer buffer = new StringBuffer();
   private final MinecraftServer server;

   public RConConsoleSource(MinecraftServer p_i46835_1_) {
      this.server = p_i46835_1_;
   }

   public void resetLog() {
      this.buffer.setLength(0);
   }

   public String getLogContents() {
      return this.buffer.toString();
   }

   public CommandSource getCommandSource() {
      ServerWorld lvt_1_1_ = this.server.getWorld(DimensionType.OVERWORLD);
      return new CommandSource(this, new Vec3d(lvt_1_1_.getSpawnPoint()), Vec2f.ZERO, lvt_1_1_, 4, "Recon", new StringTextComponent("Rcon"), this.server, (Entity)null);
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      this.buffer.append(p_145747_1_.getString());
   }

   public boolean shouldReceiveFeedback() {
      return true;
   }

   public boolean shouldReceiveErrors() {
      return true;
   }

   public boolean allowLogging() {
      return this.server.allowLoggingRcon();
   }
}
