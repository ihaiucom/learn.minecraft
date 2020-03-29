package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType DATA_BLOCK_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.block.invalid", new Object[0]));
   public static final Function<String, DataCommand.IDataProvider> DATA_PROVIDER = (p_218923_0_) -> {
      return new DataCommand.IDataProvider() {
         public IDataAccessor createAccessor(CommandContext<CommandSource> p_198919_1_) throws CommandSyntaxException {
            BlockPos lvt_2_1_ = BlockPosArgument.getLoadedBlockPos(p_198919_1_, p_218923_0_ + "Pos");
            TileEntity lvt_3_1_ = ((CommandSource)p_198919_1_.getSource()).getWorld().getTileEntity(lvt_2_1_);
            if (lvt_3_1_ == null) {
               throw BlockDataAccessor.DATA_BLOCK_INVALID_EXCEPTION.create();
            } else {
               return new BlockDataAccessor(lvt_3_1_, lvt_2_1_);
            }
         }

         public ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> p_198920_1_, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> p_198920_2_) {
            return p_198920_1_.then(Commands.literal("block").then((ArgumentBuilder)p_198920_2_.apply(Commands.argument(p_218923_0_ + "Pos", BlockPosArgument.blockPos()))));
         }
      };
   };
   private final TileEntity tileEntity;
   private final BlockPos pos;

   public BlockDataAccessor(TileEntity p_i47918_1_, BlockPos p_i47918_2_) {
      this.tileEntity = p_i47918_1_;
      this.pos = p_i47918_2_;
   }

   public void mergeData(CompoundNBT p_198925_1_) {
      p_198925_1_.putInt("x", this.pos.getX());
      p_198925_1_.putInt("y", this.pos.getY());
      p_198925_1_.putInt("z", this.pos.getZ());
      this.tileEntity.read(p_198925_1_);
      this.tileEntity.markDirty();
      BlockState lvt_2_1_ = this.tileEntity.getWorld().getBlockState(this.pos);
      this.tileEntity.getWorld().notifyBlockUpdate(this.pos, lvt_2_1_, lvt_2_1_, 3);
   }

   public CompoundNBT getData() {
      return this.tileEntity.write(new CompoundNBT());
   }

   public ITextComponent getModifiedMessage() {
      return new TranslationTextComponent("commands.data.block.modified", new Object[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()});
   }

   public ITextComponent getQueryMessage(INBT p_198924_1_) {
      return new TranslationTextComponent("commands.data.block.query", new Object[]{this.pos.getX(), this.pos.getY(), this.pos.getZ(), p_198924_1_.toFormattedComponent()});
   }

   public ITextComponent getGetMessage(NBTPathArgument.NBTPath p_198922_1_, double p_198922_2_, int p_198922_4_) {
      return new TranslationTextComponent("commands.data.block.get", new Object[]{p_198922_1_, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", p_198922_2_), p_198922_4_});
   }
}
