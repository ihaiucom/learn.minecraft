package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SlotArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;

public class LootCommand {
   public static final SuggestionProvider<CommandSource> field_218904_a = (p_218873_0_, p_218873_1_) -> {
      LootTableManager lvt_2_1_ = ((CommandSource)p_218873_0_.getSource()).getServer().getLootTableManager();
      return ISuggestionProvider.suggestIterable(lvt_2_1_.getLootTableKeys(), p_218873_1_);
   };
   private static final DynamicCommandExceptionType field_218905_b = new DynamicCommandExceptionType((p_218896_0_) -> {
      return new TranslationTextComponent("commands.drop.no_held_items", new Object[]{p_218896_0_});
   });
   private static final DynamicCommandExceptionType field_218906_c = new DynamicCommandExceptionType((p_218889_0_) -> {
      return new TranslationTextComponent("commands.drop.no_loot_table", new Object[]{p_218889_0_});
   });

   public static void register(CommandDispatcher<CommandSource> p_218886_0_) {
      p_218886_0_.register((LiteralArgumentBuilder)func_218868_a(Commands.literal("loot").requires((p_218903_0_) -> {
         return p_218903_0_.hasPermissionLevel(2);
      }), (p_218880_0_, p_218880_1_) -> {
         return p_218880_0_.then(Commands.literal("fish").then(Commands.argument("loot_table", ResourceLocationArgument.resourceLocation()).suggests(field_218904_a).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_218899_1_) -> {
            return func_218876_a(p_218899_1_, ResourceLocationArgument.getResourceLocation(p_218899_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218899_1_, "pos"), ItemStack.EMPTY, p_218880_1_);
         })).then(Commands.argument("tool", ItemArgument.item()).executes((p_218874_1_) -> {
            return func_218876_a(p_218874_1_, ResourceLocationArgument.getResourceLocation(p_218874_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218874_1_, "pos"), ItemArgument.getItem(p_218874_1_, "tool").createStack(1, false), p_218880_1_);
         }))).then(Commands.literal("mainhand").executes((p_218892_1_) -> {
            return func_218876_a(p_218892_1_, ResourceLocationArgument.getResourceLocation(p_218892_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218892_1_, "pos"), func_218872_a((CommandSource)p_218892_1_.getSource(), EquipmentSlotType.MAINHAND), p_218880_1_);
         }))).then(Commands.literal("offhand").executes((p_218898_1_) -> {
            return func_218876_a(p_218898_1_, ResourceLocationArgument.getResourceLocation(p_218898_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218898_1_, "pos"), func_218872_a((CommandSource)p_218898_1_.getSource(), EquipmentSlotType.OFFHAND), p_218880_1_);
         }))))).then(Commands.literal("loot").then(Commands.argument("loot_table", ResourceLocationArgument.resourceLocation()).suggests(field_218904_a).executes((p_218861_1_) -> {
            return func_218887_a(p_218861_1_, ResourceLocationArgument.getResourceLocation(p_218861_1_, "loot_table"), p_218880_1_);
         }))).then(Commands.literal("kill").then(Commands.argument("target", EntityArgument.entity()).executes((p_218891_1_) -> {
            return func_218869_a(p_218891_1_, EntityArgument.getEntity(p_218891_1_, "target"), p_218880_1_);
         }))).then(Commands.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_218897_1_) -> {
            return func_218879_a(p_218897_1_, BlockPosArgument.getLoadedBlockPos(p_218897_1_, "pos"), ItemStack.EMPTY, p_218880_1_);
         })).then(Commands.argument("tool", ItemArgument.item()).executes((p_218878_1_) -> {
            return func_218879_a(p_218878_1_, BlockPosArgument.getLoadedBlockPos(p_218878_1_, "pos"), ItemArgument.getItem(p_218878_1_, "tool").createStack(1, false), p_218880_1_);
         }))).then(Commands.literal("mainhand").executes((p_218895_1_) -> {
            return func_218879_a(p_218895_1_, BlockPosArgument.getLoadedBlockPos(p_218895_1_, "pos"), func_218872_a((CommandSource)p_218895_1_.getSource(), EquipmentSlotType.MAINHAND), p_218880_1_);
         }))).then(Commands.literal("offhand").executes((p_218888_1_) -> {
            return func_218879_a(p_218888_1_, BlockPosArgument.getLoadedBlockPos(p_218888_1_, "pos"), func_218872_a((CommandSource)p_218888_1_.getSource(), EquipmentSlotType.OFFHAND), p_218880_1_);
         }))));
      }));
   }

   private static <T extends ArgumentBuilder<CommandSource, T>> T func_218868_a(T p_218868_0_, LootCommand.ISourceArgumentBuilder p_218868_1_) {
      return p_218868_0_.then(((LiteralArgumentBuilder)Commands.literal("replace").then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).then(p_218868_1_.construct(Commands.argument("slot", SlotArgument.slot()), (p_218866_0_, p_218866_1_, p_218866_2_) -> {
         return func_218865_a(EntityArgument.getEntities(p_218866_0_, "entities"), SlotArgument.getSlot(p_218866_0_, "slot"), p_218866_1_.size(), p_218866_1_, p_218866_2_);
      }).then(p_218868_1_.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (p_218884_0_, p_218884_1_, p_218884_2_) -> {
         return func_218865_a(EntityArgument.getEntities(p_218884_0_, "entities"), SlotArgument.getSlot(p_218884_0_, "slot"), IntegerArgumentType.getInteger(p_218884_0_, "count"), p_218884_1_, p_218884_2_);
      })))))).then(Commands.literal("block").then(Commands.argument("targetPos", BlockPosArgument.blockPos()).then(p_218868_1_.construct(Commands.argument("slot", SlotArgument.slot()), (p_218864_0_, p_218864_1_, p_218864_2_) -> {
         return func_218894_a((CommandSource)p_218864_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_218864_0_, "targetPos"), SlotArgument.getSlot(p_218864_0_, "slot"), p_218864_1_.size(), p_218864_1_, p_218864_2_);
      }).then(p_218868_1_.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (p_218870_0_, p_218870_1_, p_218870_2_) -> {
         return func_218894_a((CommandSource)p_218870_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_218870_0_, "targetPos"), IntegerArgumentType.getInteger(p_218870_0_, "slot"), IntegerArgumentType.getInteger(p_218870_0_, "count"), p_218870_1_, p_218870_2_);
      })))))).then(Commands.literal("insert").then(p_218868_1_.construct(Commands.argument("targetPos", BlockPosArgument.blockPos()), (p_218885_0_, p_218885_1_, p_218885_2_) -> {
         return func_218900_a((CommandSource)p_218885_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_218885_0_, "targetPos"), p_218885_1_, p_218885_2_);
      }))).then(Commands.literal("give").then(p_218868_1_.construct(Commands.argument("players", EntityArgument.players()), (p_218867_0_, p_218867_1_, p_218867_2_) -> {
         return func_218859_a(EntityArgument.getPlayers(p_218867_0_, "players"), p_218867_1_, p_218867_2_);
      }))).then(Commands.literal("spawn").then(p_218868_1_.construct(Commands.argument("targetPos", Vec3Argument.vec3()), (p_218877_0_, p_218877_1_, p_218877_2_) -> {
         return func_218881_a((CommandSource)p_218877_0_.getSource(), Vec3Argument.getVec3(p_218877_0_, "targetPos"), p_218877_1_, p_218877_2_);
      })));
   }

   private static IInventory func_218862_a(CommandSource p_218862_0_, BlockPos p_218862_1_) throws CommandSyntaxException {
      TileEntity lvt_2_1_ = p_218862_0_.getWorld().getTileEntity(p_218862_1_);
      if (!(lvt_2_1_ instanceof IInventory)) {
         throw ReplaceItemCommand.BLOCK_FAILED_EXCEPTION.create();
      } else {
         return (IInventory)lvt_2_1_;
      }
   }

   private static int func_218900_a(CommandSource p_218900_0_, BlockPos p_218900_1_, List<ItemStack> p_218900_2_, LootCommand.ISuccessListener p_218900_3_) throws CommandSyntaxException {
      IInventory lvt_4_1_ = func_218862_a(p_218900_0_, p_218900_1_);
      List<ItemStack> lvt_5_1_ = Lists.newArrayListWithCapacity(p_218900_2_.size());
      Iterator var6 = p_218900_2_.iterator();

      while(var6.hasNext()) {
         ItemStack lvt_7_1_ = (ItemStack)var6.next();
         if (func_218890_a(lvt_4_1_, lvt_7_1_.copy())) {
            lvt_4_1_.markDirty();
            lvt_5_1_.add(lvt_7_1_);
         }
      }

      p_218900_3_.accept(lvt_5_1_);
      return lvt_5_1_.size();
   }

   private static boolean func_218890_a(IInventory p_218890_0_, ItemStack p_218890_1_) {
      boolean lvt_2_1_ = false;

      for(int lvt_3_1_ = 0; lvt_3_1_ < p_218890_0_.getSizeInventory() && !p_218890_1_.isEmpty(); ++lvt_3_1_) {
         ItemStack lvt_4_1_ = p_218890_0_.getStackInSlot(lvt_3_1_);
         if (p_218890_0_.isItemValidForSlot(lvt_3_1_, p_218890_1_)) {
            if (lvt_4_1_.isEmpty()) {
               p_218890_0_.setInventorySlotContents(lvt_3_1_, p_218890_1_);
               lvt_2_1_ = true;
               break;
            }

            if (func_218883_a(lvt_4_1_, p_218890_1_)) {
               int lvt_5_1_ = p_218890_1_.getMaxStackSize() - lvt_4_1_.getCount();
               int lvt_6_1_ = Math.min(p_218890_1_.getCount(), lvt_5_1_);
               p_218890_1_.shrink(lvt_6_1_);
               lvt_4_1_.grow(lvt_6_1_);
               lvt_2_1_ = true;
            }
         }
      }

      return lvt_2_1_;
   }

   private static int func_218894_a(CommandSource p_218894_0_, BlockPos p_218894_1_, int p_218894_2_, int p_218894_3_, List<ItemStack> p_218894_4_, LootCommand.ISuccessListener p_218894_5_) throws CommandSyntaxException {
      IInventory lvt_6_1_ = func_218862_a(p_218894_0_, p_218894_1_);
      int lvt_7_1_ = lvt_6_1_.getSizeInventory();
      if (p_218894_2_ >= 0 && p_218894_2_ < lvt_7_1_) {
         List<ItemStack> lvt_8_1_ = Lists.newArrayListWithCapacity(p_218894_4_.size());

         for(int lvt_9_1_ = 0; lvt_9_1_ < p_218894_3_; ++lvt_9_1_) {
            int lvt_10_1_ = p_218894_2_ + lvt_9_1_;
            ItemStack lvt_11_1_ = lvt_9_1_ < p_218894_4_.size() ? (ItemStack)p_218894_4_.get(lvt_9_1_) : ItemStack.EMPTY;
            if (lvt_6_1_.isItemValidForSlot(lvt_10_1_, lvt_11_1_)) {
               lvt_6_1_.setInventorySlotContents(lvt_10_1_, lvt_11_1_);
               lvt_8_1_.add(lvt_11_1_);
            }
         }

         p_218894_5_.accept(lvt_8_1_);
         return lvt_8_1_.size();
      } else {
         throw ReplaceItemCommand.INAPPLICABLE_SLOT_EXCEPTION.create(p_218894_2_);
      }
   }

   private static boolean func_218883_a(ItemStack p_218883_0_, ItemStack p_218883_1_) {
      return p_218883_0_.getItem() == p_218883_1_.getItem() && p_218883_0_.getDamage() == p_218883_1_.getDamage() && p_218883_0_.getCount() <= p_218883_0_.getMaxStackSize() && Objects.equals(p_218883_0_.getTag(), p_218883_1_.getTag());
   }

   private static int func_218859_a(Collection<ServerPlayerEntity> p_218859_0_, List<ItemStack> p_218859_1_, LootCommand.ISuccessListener p_218859_2_) throws CommandSyntaxException {
      List<ItemStack> lvt_3_1_ = Lists.newArrayListWithCapacity(p_218859_1_.size());
      Iterator var4 = p_218859_1_.iterator();

      while(var4.hasNext()) {
         ItemStack lvt_5_1_ = (ItemStack)var4.next();
         Iterator var6 = p_218859_0_.iterator();

         while(var6.hasNext()) {
            ServerPlayerEntity lvt_7_1_ = (ServerPlayerEntity)var6.next();
            if (lvt_7_1_.inventory.addItemStackToInventory(lvt_5_1_.copy())) {
               lvt_3_1_.add(lvt_5_1_);
            }
         }
      }

      p_218859_2_.accept(lvt_3_1_);
      return lvt_3_1_.size();
   }

   private static void func_218901_a(Entity p_218901_0_, List<ItemStack> p_218901_1_, int p_218901_2_, int p_218901_3_, List<ItemStack> p_218901_4_) {
      for(int lvt_5_1_ = 0; lvt_5_1_ < p_218901_3_; ++lvt_5_1_) {
         ItemStack lvt_6_1_ = lvt_5_1_ < p_218901_1_.size() ? (ItemStack)p_218901_1_.get(lvt_5_1_) : ItemStack.EMPTY;
         if (p_218901_0_.replaceItemInInventory(p_218901_2_ + lvt_5_1_, lvt_6_1_.copy())) {
            p_218901_4_.add(lvt_6_1_);
         }
      }

   }

   private static int func_218865_a(Collection<? extends Entity> p_218865_0_, int p_218865_1_, int p_218865_2_, List<ItemStack> p_218865_3_, LootCommand.ISuccessListener p_218865_4_) throws CommandSyntaxException {
      List<ItemStack> lvt_5_1_ = Lists.newArrayListWithCapacity(p_218865_3_.size());
      Iterator var6 = p_218865_0_.iterator();

      while(var6.hasNext()) {
         Entity lvt_7_1_ = (Entity)var6.next();
         if (lvt_7_1_ instanceof ServerPlayerEntity) {
            ServerPlayerEntity lvt_8_1_ = (ServerPlayerEntity)lvt_7_1_;
            lvt_8_1_.container.detectAndSendChanges();
            func_218901_a(lvt_7_1_, p_218865_3_, p_218865_1_, p_218865_2_, lvt_5_1_);
            lvt_8_1_.container.detectAndSendChanges();
         } else {
            func_218901_a(lvt_7_1_, p_218865_3_, p_218865_1_, p_218865_2_, lvt_5_1_);
         }
      }

      p_218865_4_.accept(lvt_5_1_);
      return lvt_5_1_.size();
   }

   private static int func_218881_a(CommandSource p_218881_0_, Vec3d p_218881_1_, List<ItemStack> p_218881_2_, LootCommand.ISuccessListener p_218881_3_) throws CommandSyntaxException {
      ServerWorld lvt_4_1_ = p_218881_0_.getWorld();
      p_218881_2_.forEach((p_218882_2_) -> {
         ItemEntity lvt_3_1_ = new ItemEntity(lvt_4_1_, p_218881_1_.x, p_218881_1_.y, p_218881_1_.z, p_218882_2_.copy());
         lvt_3_1_.setDefaultPickupDelay();
         lvt_4_1_.addEntity(lvt_3_1_);
      });
      p_218881_3_.accept(p_218881_2_);
      return p_218881_2_.size();
   }

   private static void func_218875_a(CommandSource p_218875_0_, List<ItemStack> p_218875_1_) {
      if (p_218875_1_.size() == 1) {
         ItemStack lvt_2_1_ = (ItemStack)p_218875_1_.get(0);
         p_218875_0_.sendFeedback(new TranslationTextComponent("commands.drop.success.single", new Object[]{lvt_2_1_.getCount(), lvt_2_1_.getTextComponent()}), false);
      } else {
         p_218875_0_.sendFeedback(new TranslationTextComponent("commands.drop.success.multiple", new Object[]{p_218875_1_.size()}), false);
      }

   }

   private static void func_218860_a(CommandSource p_218860_0_, List<ItemStack> p_218860_1_, ResourceLocation p_218860_2_) {
      if (p_218860_1_.size() == 1) {
         ItemStack lvt_3_1_ = (ItemStack)p_218860_1_.get(0);
         p_218860_0_.sendFeedback(new TranslationTextComponent("commands.drop.success.single_with_table", new Object[]{lvt_3_1_.getCount(), lvt_3_1_.getTextComponent(), p_218860_2_}), false);
      } else {
         p_218860_0_.sendFeedback(new TranslationTextComponent("commands.drop.success.multiple_with_table", new Object[]{p_218860_1_.size(), p_218860_2_}), false);
      }

   }

   private static ItemStack func_218872_a(CommandSource p_218872_0_, EquipmentSlotType p_218872_1_) throws CommandSyntaxException {
      Entity lvt_2_1_ = p_218872_0_.assertIsEntity();
      if (lvt_2_1_ instanceof LivingEntity) {
         return ((LivingEntity)lvt_2_1_).getItemStackFromSlot(p_218872_1_);
      } else {
         throw field_218905_b.create(lvt_2_1_.getDisplayName());
      }
   }

   private static int func_218879_a(CommandContext<CommandSource> p_218879_0_, BlockPos p_218879_1_, ItemStack p_218879_2_, LootCommand.ITargetHandler p_218879_3_) throws CommandSyntaxException {
      CommandSource lvt_4_1_ = (CommandSource)p_218879_0_.getSource();
      ServerWorld lvt_5_1_ = lvt_4_1_.getWorld();
      BlockState lvt_6_1_ = lvt_5_1_.getBlockState(p_218879_1_);
      TileEntity lvt_7_1_ = lvt_5_1_.getTileEntity(p_218879_1_);
      LootContext.Builder lvt_8_1_ = (new LootContext.Builder(lvt_5_1_)).withParameter(LootParameters.POSITION, p_218879_1_).withParameter(LootParameters.BLOCK_STATE, lvt_6_1_).withNullableParameter(LootParameters.BLOCK_ENTITY, lvt_7_1_).withNullableParameter(LootParameters.THIS_ENTITY, lvt_4_1_.getEntity()).withParameter(LootParameters.TOOL, p_218879_2_);
      List<ItemStack> lvt_9_1_ = lvt_6_1_.getDrops(lvt_8_1_);
      return p_218879_3_.accept(p_218879_0_, lvt_9_1_, (p_218893_2_) -> {
         func_218860_a(lvt_4_1_, p_218893_2_, lvt_6_1_.getBlock().getLootTable());
      });
   }

   private static int func_218869_a(CommandContext<CommandSource> p_218869_0_, Entity p_218869_1_, LootCommand.ITargetHandler p_218869_2_) throws CommandSyntaxException {
      if (!(p_218869_1_ instanceof LivingEntity)) {
         throw field_218906_c.create(p_218869_1_.getDisplayName());
      } else {
         ResourceLocation lvt_3_1_ = ((LivingEntity)p_218869_1_).func_213346_cF();
         CommandSource lvt_4_1_ = (CommandSource)p_218869_0_.getSource();
         LootContext.Builder lvt_5_1_ = new LootContext.Builder(lvt_4_1_.getWorld());
         Entity lvt_6_1_ = lvt_4_1_.getEntity();
         if (lvt_6_1_ instanceof PlayerEntity) {
            lvt_5_1_.withParameter(LootParameters.LAST_DAMAGE_PLAYER, (PlayerEntity)lvt_6_1_);
         }

         lvt_5_1_.withParameter(LootParameters.DAMAGE_SOURCE, DamageSource.MAGIC);
         lvt_5_1_.withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, lvt_6_1_);
         lvt_5_1_.withNullableParameter(LootParameters.KILLER_ENTITY, lvt_6_1_);
         lvt_5_1_.withParameter(LootParameters.THIS_ENTITY, p_218869_1_);
         lvt_5_1_.withParameter(LootParameters.POSITION, new BlockPos(lvt_4_1_.getPos()));
         LootTable lvt_7_1_ = lvt_4_1_.getServer().getLootTableManager().getLootTableFromLocation(lvt_3_1_);
         List<ItemStack> lvt_8_1_ = lvt_7_1_.generate(lvt_5_1_.build(LootParameterSets.ENTITY));
         return p_218869_2_.accept(p_218869_0_, lvt_8_1_, (p_218863_2_) -> {
            func_218860_a(lvt_4_1_, p_218863_2_, lvt_3_1_);
         });
      }
   }

   private static int func_218887_a(CommandContext<CommandSource> p_218887_0_, ResourceLocation p_218887_1_, LootCommand.ITargetHandler p_218887_2_) throws CommandSyntaxException {
      CommandSource lvt_3_1_ = (CommandSource)p_218887_0_.getSource();
      LootContext.Builder lvt_4_1_ = (new LootContext.Builder(lvt_3_1_.getWorld())).withNullableParameter(LootParameters.THIS_ENTITY, lvt_3_1_.getEntity()).withParameter(LootParameters.POSITION, new BlockPos(lvt_3_1_.getPos()));
      return func_218871_a(p_218887_0_, p_218887_1_, lvt_4_1_.build(LootParameterSets.CHEST), p_218887_2_);
   }

   private static int func_218876_a(CommandContext<CommandSource> p_218876_0_, ResourceLocation p_218876_1_, BlockPos p_218876_2_, ItemStack p_218876_3_, LootCommand.ITargetHandler p_218876_4_) throws CommandSyntaxException {
      CommandSource lvt_5_1_ = (CommandSource)p_218876_0_.getSource();
      LootContext lvt_6_1_ = (new LootContext.Builder(lvt_5_1_.getWorld())).withParameter(LootParameters.POSITION, p_218876_2_).withParameter(LootParameters.TOOL, p_218876_3_).build(LootParameterSets.FISHING);
      return func_218871_a(p_218876_0_, p_218876_1_, lvt_6_1_, p_218876_4_);
   }

   private static int func_218871_a(CommandContext<CommandSource> p_218871_0_, ResourceLocation p_218871_1_, LootContext p_218871_2_, LootCommand.ITargetHandler p_218871_3_) throws CommandSyntaxException {
      CommandSource lvt_4_1_ = (CommandSource)p_218871_0_.getSource();
      LootTable lvt_5_1_ = lvt_4_1_.getServer().getLootTableManager().getLootTableFromLocation(p_218871_1_);
      List<ItemStack> lvt_6_1_ = lvt_5_1_.generate(p_218871_2_);
      return p_218871_3_.accept(p_218871_0_, lvt_6_1_, (p_218902_1_) -> {
         func_218875_a(lvt_4_1_, p_218902_1_);
      });
   }

   @FunctionalInterface
   interface ISourceArgumentBuilder {
      ArgumentBuilder<CommandSource, ?> construct(ArgumentBuilder<CommandSource, ?> var1, LootCommand.ITargetHandler var2);
   }

   @FunctionalInterface
   interface ITargetHandler {
      int accept(CommandContext<CommandSource> var1, List<ItemStack> var2, LootCommand.ISuccessListener var3) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface ISuccessListener {
      void accept(List<ItemStack> var1) throws CommandSyntaxException;
   }
}
