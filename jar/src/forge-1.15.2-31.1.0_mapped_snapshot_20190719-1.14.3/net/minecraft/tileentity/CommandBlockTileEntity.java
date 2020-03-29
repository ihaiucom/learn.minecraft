package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CommandBlockTileEntity extends TileEntity {
   private boolean powered;
   private boolean auto;
   private boolean conditionMet;
   private boolean sendToClient;
   private final CommandBlockLogic commandBlockLogic = new CommandBlockLogic() {
      public void setCommand(String p_145752_1_) {
         super.setCommand(p_145752_1_);
         CommandBlockTileEntity.this.markDirty();
      }

      public ServerWorld getWorld() {
         return (ServerWorld)CommandBlockTileEntity.this.world;
      }

      public void updateCommand() {
         BlockState lvt_1_1_ = CommandBlockTileEntity.this.world.getBlockState(CommandBlockTileEntity.this.pos);
         this.getWorld().notifyBlockUpdate(CommandBlockTileEntity.this.pos, lvt_1_1_, lvt_1_1_, 3);
      }

      @OnlyIn(Dist.CLIENT)
      public Vec3d getPositionVector() {
         return new Vec3d((double)CommandBlockTileEntity.this.pos.getX() + 0.5D, (double)CommandBlockTileEntity.this.pos.getY() + 0.5D, (double)CommandBlockTileEntity.this.pos.getZ() + 0.5D);
      }

      public CommandSource getCommandSource() {
         return new CommandSource(this, new Vec3d((double)CommandBlockTileEntity.this.pos.getX() + 0.5D, (double)CommandBlockTileEntity.this.pos.getY() + 0.5D, (double)CommandBlockTileEntity.this.pos.getZ() + 0.5D), Vec2f.ZERO, this.getWorld(), 2, this.getName().getString(), this.getName(), this.getWorld().getServer(), (Entity)null);
      }
   };

   public CommandBlockTileEntity() {
      super(TileEntityType.COMMAND_BLOCK);
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      this.commandBlockLogic.write(p_189515_1_);
      p_189515_1_.putBoolean("powered", this.isPowered());
      p_189515_1_.putBoolean("conditionMet", this.isConditionMet());
      p_189515_1_.putBoolean("auto", this.isAuto());
      return p_189515_1_;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.commandBlockLogic.read(p_145839_1_);
      this.powered = p_145839_1_.getBoolean("powered");
      this.conditionMet = p_145839_1_.getBoolean("conditionMet");
      this.setAuto(p_145839_1_.getBoolean("auto"));
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      if (this.isSendToClient()) {
         this.setSendToClient(false);
         CompoundNBT lvt_1_1_ = this.write(new CompoundNBT());
         return new SUpdateTileEntityPacket(this.pos, 2, lvt_1_1_);
      } else {
         return null;
      }
   }

   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public CommandBlockLogic getCommandBlockLogic() {
      return this.commandBlockLogic;
   }

   public void setPowered(boolean p_184250_1_) {
      this.powered = p_184250_1_;
   }

   public boolean isPowered() {
      return this.powered;
   }

   public boolean isAuto() {
      return this.auto;
   }

   public void setAuto(boolean p_184253_1_) {
      boolean lvt_2_1_ = this.auto;
      this.auto = p_184253_1_;
      if (!lvt_2_1_ && p_184253_1_ && !this.powered && this.world != null && this.getMode() != CommandBlockTileEntity.Mode.SEQUENCE) {
         this.func_226988_y_();
      }

   }

   public void func_226987_h_() {
      CommandBlockTileEntity.Mode lvt_1_1_ = this.getMode();
      if (lvt_1_1_ == CommandBlockTileEntity.Mode.AUTO && (this.powered || this.auto) && this.world != null) {
         this.func_226988_y_();
      }

   }

   private void func_226988_y_() {
      Block lvt_1_1_ = this.getBlockState().getBlock();
      if (lvt_1_1_ instanceof CommandBlockBlock) {
         this.setConditionMet();
         this.world.getPendingBlockTicks().scheduleTick(this.pos, lvt_1_1_, lvt_1_1_.tickRate(this.world));
      }

   }

   public boolean isConditionMet() {
      return this.conditionMet;
   }

   public boolean setConditionMet() {
      this.conditionMet = true;
      if (this.isConditional()) {
         BlockPos lvt_1_1_ = this.pos.offset(((Direction)this.world.getBlockState(this.pos).get(CommandBlockBlock.FACING)).getOpposite());
         if (this.world.getBlockState(lvt_1_1_).getBlock() instanceof CommandBlockBlock) {
            TileEntity lvt_2_1_ = this.world.getTileEntity(lvt_1_1_);
            this.conditionMet = lvt_2_1_ instanceof CommandBlockTileEntity && ((CommandBlockTileEntity)lvt_2_1_).getCommandBlockLogic().getSuccessCount() > 0;
         } else {
            this.conditionMet = false;
         }
      }

      return this.conditionMet;
   }

   public boolean isSendToClient() {
      return this.sendToClient;
   }

   public void setSendToClient(boolean p_184252_1_) {
      this.sendToClient = p_184252_1_;
   }

   public CommandBlockTileEntity.Mode getMode() {
      Block lvt_1_1_ = this.getBlockState().getBlock();
      if (lvt_1_1_ == Blocks.COMMAND_BLOCK) {
         return CommandBlockTileEntity.Mode.REDSTONE;
      } else if (lvt_1_1_ == Blocks.REPEATING_COMMAND_BLOCK) {
         return CommandBlockTileEntity.Mode.AUTO;
      } else {
         return lvt_1_1_ == Blocks.CHAIN_COMMAND_BLOCK ? CommandBlockTileEntity.Mode.SEQUENCE : CommandBlockTileEntity.Mode.REDSTONE;
      }
   }

   public boolean isConditional() {
      BlockState lvt_1_1_ = this.world.getBlockState(this.getPos());
      return lvt_1_1_.getBlock() instanceof CommandBlockBlock ? (Boolean)lvt_1_1_.get(CommandBlockBlock.CONDITIONAL) : false;
   }

   public void validate() {
      this.updateContainingBlockInfo();
      super.validate();
   }

   public static enum Mode {
      SEQUENCE,
      AUTO,
      REDSTONE;
   }
}
