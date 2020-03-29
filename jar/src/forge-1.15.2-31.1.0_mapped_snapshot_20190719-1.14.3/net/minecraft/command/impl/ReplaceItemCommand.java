package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.SlotArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class ReplaceItemCommand {
   public static final SimpleCommandExceptionType BLOCK_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.replaceitem.block.failed", new Object[0]));
   public static final DynamicCommandExceptionType INAPPLICABLE_SLOT_EXCEPTION = new DynamicCommandExceptionType((p_211409_0_) -> {
      return new TranslationTextComponent("commands.replaceitem.slot.inapplicable", new Object[]{p_211409_0_});
   });
   public static final Dynamic2CommandExceptionType ENTITY_FAILED_EXCEPTION = new Dynamic2CommandExceptionType((p_211411_0_, p_211411_1_) -> {
      return new TranslationTextComponent("commands.replaceitem.entity.failed", new Object[]{p_211411_0_, p_211411_1_});
   });

   public static void register(CommandDispatcher<CommandSource> p_198602_0_) {
      p_198602_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replaceitem").requires((p_198607_0_) -> {
         return p_198607_0_.hasPermissionLevel(2);
      })).then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands.argument("slot", SlotArgument.slot()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes((p_198601_0_) -> {
         return replaceItemBlock((CommandSource)p_198601_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198601_0_, "pos"), SlotArgument.getSlot(p_198601_0_, "slot"), ItemArgument.getItem(p_198601_0_, "item").createStack(1, false));
      })).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((p_198605_0_) -> {
         return replaceItemBlock((CommandSource)p_198605_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198605_0_, "pos"), SlotArgument.getSlot(p_198605_0_, "slot"), ItemArgument.getItem(p_198605_0_, "item").createStack(IntegerArgumentType.getInteger(p_198605_0_, "count"), true));
      }))))))).then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("slot", SlotArgument.slot()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes((p_198600_0_) -> {
         return replaceItemEntities((CommandSource)p_198600_0_.getSource(), EntityArgument.getEntities(p_198600_0_, "targets"), SlotArgument.getSlot(p_198600_0_, "slot"), ItemArgument.getItem(p_198600_0_, "item").createStack(1, false));
      })).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((p_198606_0_) -> {
         return replaceItemEntities((CommandSource)p_198606_0_.getSource(), EntityArgument.getEntities(p_198606_0_, "targets"), SlotArgument.getSlot(p_198606_0_, "slot"), ItemArgument.getItem(p_198606_0_, "item").createStack(IntegerArgumentType.getInteger(p_198606_0_, "count"), true));
      })))))));
   }

   private static int replaceItemBlock(CommandSource p_198603_0_, BlockPos p_198603_1_, int p_198603_2_, ItemStack p_198603_3_) throws CommandSyntaxException {
      TileEntity lvt_4_1_ = p_198603_0_.getWorld().getTileEntity(p_198603_1_);
      if (!(lvt_4_1_ instanceof IInventory)) {
         throw BLOCK_FAILED_EXCEPTION.create();
      } else {
         IInventory lvt_5_1_ = (IInventory)lvt_4_1_;
         if (p_198603_2_ >= 0 && p_198603_2_ < lvt_5_1_.getSizeInventory()) {
            lvt_5_1_.setInventorySlotContents(p_198603_2_, p_198603_3_);
            p_198603_0_.sendFeedback(new TranslationTextComponent("commands.replaceitem.block.success", new Object[]{p_198603_1_.getX(), p_198603_1_.getY(), p_198603_1_.getZ(), p_198603_3_.getTextComponent()}), true);
            return 1;
         } else {
            throw INAPPLICABLE_SLOT_EXCEPTION.create(p_198603_2_);
         }
      }
   }

   private static int replaceItemEntities(CommandSource p_198604_0_, Collection<? extends Entity> p_198604_1_, int p_198604_2_, ItemStack p_198604_3_) throws CommandSyntaxException {
      List<Entity> lvt_4_1_ = Lists.newArrayListWithCapacity(p_198604_1_.size());
      Iterator var5 = p_198604_1_.iterator();

      while(var5.hasNext()) {
         Entity lvt_6_1_ = (Entity)var5.next();
         if (lvt_6_1_ instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)lvt_6_1_).container.detectAndSendChanges();
         }

         if (lvt_6_1_.replaceItemInInventory(p_198604_2_, p_198604_3_.copy())) {
            lvt_4_1_.add(lvt_6_1_);
            if (lvt_6_1_ instanceof ServerPlayerEntity) {
               ((ServerPlayerEntity)lvt_6_1_).container.detectAndSendChanges();
            }
         }
      }

      if (lvt_4_1_.isEmpty()) {
         throw ENTITY_FAILED_EXCEPTION.create(p_198604_3_.getTextComponent(), p_198604_2_);
      } else {
         if (lvt_4_1_.size() == 1) {
            p_198604_0_.sendFeedback(new TranslationTextComponent("commands.replaceitem.entity.success.single", new Object[]{((Entity)lvt_4_1_.iterator().next()).getDisplayName(), p_198604_3_.getTextComponent()}), true);
         } else {
            p_198604_0_.sendFeedback(new TranslationTextComponent("commands.replaceitem.entity.success.multiple", new Object[]{lvt_4_1_.size(), p_198604_3_.getTextComponent()}), true);
         }

         return lvt_4_1_.size();
      }
   }
}
