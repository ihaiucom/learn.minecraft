package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MinecartCommandBlockEntity extends AbstractMinecartEntity {
   private static final DataParameter<String> COMMAND;
   private static final DataParameter<ITextComponent> LAST_OUTPUT;
   private final CommandBlockLogic commandBlockLogic = new MinecartCommandBlockEntity.MinecartCommandLogic();
   private int activatorRailCooldown;

   public MinecartCommandBlockEntity(EntityType<? extends MinecartCommandBlockEntity> p_i50123_1_, World p_i50123_2_) {
      super(p_i50123_1_, p_i50123_2_);
   }

   public MinecartCommandBlockEntity(World p_i46755_1_, double p_i46755_2_, double p_i46755_4_, double p_i46755_6_) {
      super(EntityType.COMMAND_BLOCK_MINECART, p_i46755_1_, p_i46755_2_, p_i46755_4_, p_i46755_6_);
   }

   protected void registerData() {
      super.registerData();
      this.getDataManager().register(COMMAND, "");
      this.getDataManager().register(LAST_OUTPUT, new StringTextComponent(""));
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.commandBlockLogic.read(p_70037_1_);
      this.getDataManager().set(COMMAND, this.getCommandBlockLogic().getCommand());
      this.getDataManager().set(LAST_OUTPUT, this.getCommandBlockLogic().getLastOutput());
   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      this.commandBlockLogic.write(p_213281_1_);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.COMMAND_BLOCK;
   }

   public BlockState getDefaultDisplayTile() {
      return Blocks.COMMAND_BLOCK.getDefaultState();
   }

   public CommandBlockLogic getCommandBlockLogic() {
      return this.commandBlockLogic;
   }

   public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      if (p_96095_4_ && this.ticksExisted - this.activatorRailCooldown >= 4) {
         this.getCommandBlockLogic().trigger(this.world);
         this.activatorRailCooldown = this.ticksExisted;
      }

   }

   public boolean processInitialInteract(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      if (super.processInitialInteract(p_184230_1_, p_184230_2_)) {
         return true;
      } else {
         this.commandBlockLogic.tryOpenEditCommandBlock(p_184230_1_);
         return true;
      }
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      super.notifyDataManagerChange(p_184206_1_);
      if (LAST_OUTPUT.equals(p_184206_1_)) {
         try {
            this.commandBlockLogic.setLastOutput((ITextComponent)this.getDataManager().get(LAST_OUTPUT));
         } catch (Throwable var3) {
         }
      } else if (COMMAND.equals(p_184206_1_)) {
         this.commandBlockLogic.setCommand((String)this.getDataManager().get(COMMAND));
      }

   }

   public boolean ignoreItemEntityData() {
      return true;
   }

   static {
      COMMAND = EntityDataManager.createKey(MinecartCommandBlockEntity.class, DataSerializers.STRING);
      LAST_OUTPUT = EntityDataManager.createKey(MinecartCommandBlockEntity.class, DataSerializers.TEXT_COMPONENT);
   }

   public class MinecartCommandLogic extends CommandBlockLogic {
      public ServerWorld getWorld() {
         return (ServerWorld)MinecartCommandBlockEntity.this.world;
      }

      public void updateCommand() {
         MinecartCommandBlockEntity.this.getDataManager().set(MinecartCommandBlockEntity.COMMAND, this.getCommand());
         MinecartCommandBlockEntity.this.getDataManager().set(MinecartCommandBlockEntity.LAST_OUTPUT, this.getLastOutput());
      }

      @OnlyIn(Dist.CLIENT)
      public Vec3d getPositionVector() {
         return MinecartCommandBlockEntity.this.getPositionVec();
      }

      @OnlyIn(Dist.CLIENT)
      public MinecartCommandBlockEntity getMinecart() {
         return MinecartCommandBlockEntity.this;
      }

      public CommandSource getCommandSource() {
         return new CommandSource(this, MinecartCommandBlockEntity.this.getPositionVec(), MinecartCommandBlockEntity.this.getPitchYaw(), this.getWorld(), 2, this.getName().getString(), MinecartCommandBlockEntity.this.getDisplayName(), this.getWorld().getServer(), MinecartCommandBlockEntity.this);
      }
   }
}
