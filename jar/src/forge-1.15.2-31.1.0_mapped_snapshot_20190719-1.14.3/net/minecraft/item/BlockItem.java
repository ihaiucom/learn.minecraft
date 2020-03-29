package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockItem extends Item {
   /** @deprecated */
   @Deprecated
   private final Block block;

   public BlockItem(Block p_i48527_1_, Item.Properties p_i48527_2_) {
      super(p_i48527_2_);
      this.block = p_i48527_1_;
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      ActionResultType actionresulttype = this.tryPlace(new BlockItemUseContext(p_195939_1_));
      return actionresulttype != ActionResultType.SUCCESS && this.isFood() ? this.onItemRightClick(p_195939_1_.world, p_195939_1_.player, p_195939_1_.hand).getType() : actionresulttype;
   }

   public ActionResultType tryPlace(BlockItemUseContext p_195942_1_) {
      if (!p_195942_1_.canPlace()) {
         return ActionResultType.FAIL;
      } else {
         BlockItemUseContext blockitemusecontext = this.getBlockItemUseContext(p_195942_1_);
         if (blockitemusecontext == null) {
            return ActionResultType.FAIL;
         } else {
            BlockState blockstate = this.getStateForPlacement(blockitemusecontext);
            if (blockstate == null) {
               return ActionResultType.FAIL;
            } else if (!this.placeBlock(blockitemusecontext, blockstate)) {
               return ActionResultType.FAIL;
            } else {
               BlockPos blockpos = blockitemusecontext.getPos();
               World world = blockitemusecontext.getWorld();
               PlayerEntity playerentity = blockitemusecontext.getPlayer();
               ItemStack itemstack = blockitemusecontext.getItem();
               BlockState blockstate1 = world.getBlockState(blockpos);
               Block block = blockstate1.getBlock();
               if (block == blockstate.getBlock()) {
                  blockstate1 = this.func_219985_a(blockpos, world, itemstack, blockstate1);
                  this.onBlockPlaced(blockpos, world, playerentity, itemstack, blockstate1);
                  block.onBlockPlacedBy(world, blockpos, blockstate1, playerentity, itemstack);
                  if (playerentity instanceof ServerPlayerEntity) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerentity, blockpos, itemstack);
                  }
               }

               SoundType soundtype = blockstate1.getSoundType(world, blockpos, p_195942_1_.getPlayer());
               world.playSound(playerentity, blockpos, this.getPlaceSound(blockstate1, world, blockpos, p_195942_1_.getPlayer()), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
               itemstack.shrink(1);
               return ActionResultType.SUCCESS;
            }
         }
      }
   }

   /** @deprecated */
   @Deprecated
   protected SoundEvent getPlaceSound(BlockState p_219983_1_) {
      return p_219983_1_.getSoundType().getPlaceSound();
   }

   protected SoundEvent getPlaceSound(BlockState p_getPlaceSound_1_, World p_getPlaceSound_2_, BlockPos p_getPlaceSound_3_, PlayerEntity p_getPlaceSound_4_) {
      return p_getPlaceSound_1_.getSoundType(p_getPlaceSound_2_, p_getPlaceSound_3_, p_getPlaceSound_4_).getPlaceSound();
   }

   @Nullable
   public BlockItemUseContext getBlockItemUseContext(BlockItemUseContext p_219984_1_) {
      return p_219984_1_;
   }

   protected boolean onBlockPlaced(BlockPos p_195943_1_, World p_195943_2_, @Nullable PlayerEntity p_195943_3_, ItemStack p_195943_4_, BlockState p_195943_5_) {
      return setTileEntityNBT(p_195943_2_, p_195943_3_, p_195943_1_, p_195943_4_);
   }

   @Nullable
   protected BlockState getStateForPlacement(BlockItemUseContext p_195945_1_) {
      BlockState blockstate = this.getBlock().getStateForPlacement(p_195945_1_);
      return blockstate != null && this.canPlace(p_195945_1_, blockstate) ? blockstate : null;
   }

   private BlockState func_219985_a(BlockPos p_219985_1_, World p_219985_2_, ItemStack p_219985_3_, BlockState p_219985_4_) {
      BlockState blockstate = p_219985_4_;
      CompoundNBT compoundnbt = p_219985_3_.getTag();
      if (compoundnbt != null) {
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("BlockStateTag");
         StateContainer<Block, BlockState> statecontainer = p_219985_4_.getBlock().getStateContainer();
         Iterator var9 = compoundnbt1.keySet().iterator();

         while(var9.hasNext()) {
            String s = (String)var9.next();
            IProperty<?> iproperty = statecontainer.getProperty(s);
            if (iproperty != null) {
               String s1 = compoundnbt1.get(s).getString();
               blockstate = func_219988_a(blockstate, iproperty, s1);
            }
         }
      }

      if (blockstate != p_219985_4_) {
         p_219985_2_.setBlockState(p_219985_1_, blockstate, 2);
      }

      return blockstate;
   }

   private static <T extends Comparable<T>> BlockState func_219988_a(BlockState p_219988_0_, IProperty<T> p_219988_1_, String p_219988_2_) {
      return (BlockState)p_219988_1_.parseValue(p_219988_2_).map((p_lambda$func_219988_a$0_2_) -> {
         return (BlockState)p_219988_0_.with(p_219988_1_, p_lambda$func_219988_a$0_2_);
      }).orElse(p_219988_0_);
   }

   protected boolean canPlace(BlockItemUseContext p_195944_1_, BlockState p_195944_2_) {
      PlayerEntity playerentity = p_195944_1_.getPlayer();
      ISelectionContext iselectioncontext = playerentity == null ? ISelectionContext.dummy() : ISelectionContext.forEntity(playerentity);
      return (!this.func_219987_d() || p_195944_2_.isValidPosition(p_195944_1_.getWorld(), p_195944_1_.getPos())) && p_195944_1_.getWorld().func_226663_a_(p_195944_2_, p_195944_1_.getPos(), iselectioncontext);
   }

   protected boolean func_219987_d() {
      return true;
   }

   protected boolean placeBlock(BlockItemUseContext p_195941_1_, BlockState p_195941_2_) {
      return p_195941_1_.getWorld().setBlockState(p_195941_1_.getPos(), p_195941_2_, 11);
   }

   public static boolean setTileEntityNBT(World p_179224_0_, @Nullable PlayerEntity p_179224_1_, BlockPos p_179224_2_, ItemStack p_179224_3_) {
      MinecraftServer minecraftserver = p_179224_0_.getServer();
      if (minecraftserver == null) {
         return false;
      } else {
         CompoundNBT compoundnbt = p_179224_3_.getChildTag("BlockEntityTag");
         if (compoundnbt != null) {
            TileEntity tileentity = p_179224_0_.getTileEntity(p_179224_2_);
            if (tileentity != null) {
               if (!p_179224_0_.isRemote && tileentity.onlyOpsCanSetNbt() && (p_179224_1_ == null || !p_179224_1_.canUseCommandBlock())) {
                  return false;
               }

               CompoundNBT compoundnbt1 = tileentity.write(new CompoundNBT());
               CompoundNBT compoundnbt2 = compoundnbt1.copy();
               compoundnbt1.merge(compoundnbt);
               compoundnbt1.putInt("x", p_179224_2_.getX());
               compoundnbt1.putInt("y", p_179224_2_.getY());
               compoundnbt1.putInt("z", p_179224_2_.getZ());
               if (!compoundnbt1.equals(compoundnbt2)) {
                  tileentity.read(compoundnbt1);
                  tileentity.markDirty();
                  return true;
               }
            }
         }

         return false;
      }
   }

   public String getTranslationKey() {
      return this.getBlock().getTranslationKey();
   }

   public void fillItemGroup(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.isInGroup(p_150895_1_)) {
         this.getBlock().fillItemGroup(p_150895_1_, p_150895_2_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      super.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
      this.getBlock().addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
   }

   public Block getBlock() {
      return this.getBlockRaw() == null ? null : (Block)this.getBlockRaw().delegate.get();
   }

   private Block getBlockRaw() {
      return this.block;
   }

   public void addToBlockToItemMap(Map<Block, Item> p_195946_1_, Item p_195946_2_) {
      p_195946_1_.put(this.getBlock(), p_195946_2_);
   }

   public void removeFromBlockToItemMap(Map<Block, Item> p_removeFromBlockToItemMap_1_, Item p_removeFromBlockToItemMap_2_) {
      p_removeFromBlockToItemMap_1_.remove(this.getBlock());
   }
}
