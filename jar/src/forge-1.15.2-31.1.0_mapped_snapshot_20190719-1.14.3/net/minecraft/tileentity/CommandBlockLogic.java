package net.minecraft.tileentity;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class CommandBlockLogic implements ICommandSource {
   private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private static final ITextComponent field_226655_c_ = new StringTextComponent("@");
   private long lastExecution = -1L;
   private boolean updateLastExecution = true;
   private int successCount;
   private boolean trackOutput = true;
   private ITextComponent lastOutput;
   private String commandStored = "";
   private ITextComponent customName;

   public CommandBlockLogic() {
      this.customName = field_226655_c_;
   }

   public int getSuccessCount() {
      return this.successCount;
   }

   public void setSuccessCount(int p_184167_1_) {
      this.successCount = p_184167_1_;
   }

   public ITextComponent getLastOutput() {
      return (ITextComponent)(this.lastOutput == null ? new StringTextComponent("") : this.lastOutput);
   }

   public CompoundNBT write(CompoundNBT p_189510_1_) {
      p_189510_1_.putString("Command", this.commandStored);
      p_189510_1_.putInt("SuccessCount", this.successCount);
      p_189510_1_.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
      p_189510_1_.putBoolean("TrackOutput", this.trackOutput);
      if (this.lastOutput != null && this.trackOutput) {
         p_189510_1_.putString("LastOutput", ITextComponent.Serializer.toJson(this.lastOutput));
      }

      p_189510_1_.putBoolean("UpdateLastExecution", this.updateLastExecution);
      if (this.updateLastExecution && this.lastExecution > 0L) {
         p_189510_1_.putLong("LastExecution", this.lastExecution);
      }

      return p_189510_1_;
   }

   public void read(CompoundNBT p_145759_1_) {
      this.commandStored = p_145759_1_.getString("Command");
      this.successCount = p_145759_1_.getInt("SuccessCount");
      if (p_145759_1_.contains("CustomName", 8)) {
         this.setName(ITextComponent.Serializer.fromJson(p_145759_1_.getString("CustomName")));
      }

      if (p_145759_1_.contains("TrackOutput", 1)) {
         this.trackOutput = p_145759_1_.getBoolean("TrackOutput");
      }

      if (p_145759_1_.contains("LastOutput", 8) && this.trackOutput) {
         try {
            this.lastOutput = ITextComponent.Serializer.fromJson(p_145759_1_.getString("LastOutput"));
         } catch (Throwable var3) {
            this.lastOutput = new StringTextComponent(var3.getMessage());
         }
      } else {
         this.lastOutput = null;
      }

      if (p_145759_1_.contains("UpdateLastExecution")) {
         this.updateLastExecution = p_145759_1_.getBoolean("UpdateLastExecution");
      }

      if (this.updateLastExecution && p_145759_1_.contains("LastExecution")) {
         this.lastExecution = p_145759_1_.getLong("LastExecution");
      } else {
         this.lastExecution = -1L;
      }

   }

   public void setCommand(String p_145752_1_) {
      this.commandStored = p_145752_1_;
      this.successCount = 0;
   }

   public String getCommand() {
      return this.commandStored;
   }

   public boolean trigger(World p_145755_1_) {
      if (!p_145755_1_.isRemote && p_145755_1_.getGameTime() != this.lastExecution) {
         if ("Searge".equalsIgnoreCase(this.commandStored)) {
            this.lastOutput = new StringTextComponent("#itzlipofutzli");
            this.successCount = 1;
            return true;
         } else {
            this.successCount = 0;
            MinecraftServer lvt_2_1_ = this.getWorld().getServer();
            if (lvt_2_1_ != null && lvt_2_1_.isAnvilFileSet() && lvt_2_1_.isCommandBlockEnabled() && !StringUtils.isNullOrEmpty(this.commandStored)) {
               try {
                  this.lastOutput = null;
                  CommandSource lvt_3_1_ = this.getCommandSource().withResultConsumer((p_209527_1_, p_209527_2_, p_209527_3_) -> {
                     if (p_209527_2_) {
                        ++this.successCount;
                     }

                  });
                  lvt_2_1_.getCommandManager().handleCommand(lvt_3_1_, this.commandStored);
               } catch (Throwable var6) {
                  CrashReport lvt_4_1_ = CrashReport.makeCrashReport(var6, "Executing command block");
                  CrashReportCategory lvt_5_1_ = lvt_4_1_.makeCategory("Command to be executed");
                  lvt_5_1_.addDetail("Command", this::getCommand);
                  lvt_5_1_.addDetail("Name", () -> {
                     return this.getName().getString();
                  });
                  throw new ReportedException(lvt_4_1_);
               }
            }

            if (this.updateLastExecution) {
               this.lastExecution = p_145755_1_.getGameTime();
            } else {
               this.lastExecution = -1L;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public ITextComponent getName() {
      return this.customName;
   }

   public void setName(@Nullable ITextComponent p_207405_1_) {
      if (p_207405_1_ != null) {
         this.customName = p_207405_1_;
      } else {
         this.customName = field_226655_c_;
      }

   }

   public void sendMessage(ITextComponent p_145747_1_) {
      if (this.trackOutput) {
         this.lastOutput = (new StringTextComponent("[" + TIMESTAMP_FORMAT.format(new Date()) + "] ")).appendSibling(p_145747_1_);
         this.updateCommand();
      }

   }

   public abstract ServerWorld getWorld();

   public abstract void updateCommand();

   public void setLastOutput(@Nullable ITextComponent p_145750_1_) {
      this.lastOutput = p_145750_1_;
   }

   public void setTrackOutput(boolean p_175573_1_) {
      this.trackOutput = p_175573_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldTrackOutput() {
      return this.trackOutput;
   }

   public boolean tryOpenEditCommandBlock(PlayerEntity p_175574_1_) {
      if (!p_175574_1_.canUseCommandBlock()) {
         return false;
      } else {
         if (p_175574_1_.getEntityWorld().isRemote) {
            p_175574_1_.openMinecartCommandBlock(this);
         }

         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract Vec3d getPositionVector();

   public abstract CommandSource getCommandSource();

   public boolean shouldReceiveFeedback() {
      return this.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK) && this.trackOutput;
   }

   public boolean shouldReceiveErrors() {
      return this.trackOutput;
   }

   public boolean allowLogging() {
      return this.getWorld().getGameRules().getBoolean(GameRules.COMMAND_BLOCK_OUTPUT);
   }
}
