package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShulkerBoxBlock extends ContainerBlock {
   public static final EnumProperty<Direction> FACING;
   public static final ResourceLocation field_220169_b;
   @Nullable
   private final DyeColor color;

   public ShulkerBoxBlock(@Nullable DyeColor p_i48334_1_, Block.Properties p_i48334_2_) {
      super(p_i48334_2_);
      this.color = p_i48334_1_;
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.UP));
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new ShulkerBoxTileEntity(this.color);
   }

   public boolean func_229869_c_(BlockState p_229869_1_, IBlockReader p_229869_2_, BlockPos p_229869_3_) {
      return true;
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else if (p_225533_4_.isSpectator()) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity tileentity = p_225533_2_.getTileEntity(p_225533_3_);
         if (tileentity instanceof ShulkerBoxTileEntity) {
            Direction direction = (Direction)p_225533_1_.get(FACING);
            ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
            boolean flag;
            if (shulkerboxtileentity.getAnimationStatus() == ShulkerBoxTileEntity.AnimationStatus.CLOSED) {
               AxisAlignedBB axisalignedbb = VoxelShapes.fullCube().getBoundingBox().expand((double)(0.5F * (float)direction.getXOffset()), (double)(0.5F * (float)direction.getYOffset()), (double)(0.5F * (float)direction.getZOffset())).contract((double)direction.getXOffset(), (double)direction.getYOffset(), (double)direction.getZOffset());
               flag = p_225533_2_.func_226664_a_(axisalignedbb.offset(p_225533_3_.offset(direction)));
            } else {
               flag = true;
            }

            if (flag) {
               p_225533_4_.openContainer(shulkerboxtileentity);
               p_225533_4_.addStat(Stats.OPEN_SHULKER_BOX);
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(FACING, p_196258_1_.getFace());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      TileEntity tileentity = p_176208_1_.getTileEntity(p_176208_2_);
      if (tileentity instanceof ShulkerBoxTileEntity) {
         ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
         if (!p_176208_1_.isRemote && p_176208_4_.isCreative() && !shulkerboxtileentity.isEmpty()) {
            ItemStack itemstack = getColoredItemStack(this.getColor());
            CompoundNBT compoundnbt = shulkerboxtileentity.saveToNbt(new CompoundNBT());
            if (!compoundnbt.isEmpty()) {
               itemstack.setTagInfo("BlockEntityTag", compoundnbt);
            }

            if (shulkerboxtileentity.hasCustomName()) {
               itemstack.setDisplayName(shulkerboxtileentity.getCustomName());
            }

            ItemEntity itementity = new ItemEntity(p_176208_1_, (double)p_176208_2_.getX(), (double)p_176208_2_.getY(), (double)p_176208_2_.getZ(), itemstack);
            itementity.setDefaultPickupDelay();
            p_176208_1_.addEntity(itementity);
         } else {
            shulkerboxtileentity.fillWithLoot(p_176208_4_);
         }
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      TileEntity tileentity = (TileEntity)p_220076_2_.get(LootParameters.BLOCK_ENTITY);
      if (tileentity instanceof ShulkerBoxTileEntity) {
         ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
         p_220076_2_ = p_220076_2_.withDynamicDrop(field_220169_b, (p_lambda$getDrops$0_1_, p_lambda$getDrops$0_2_) -> {
            for(int i = 0; i < shulkerboxtileentity.getSizeInventory(); ++i) {
               p_lambda$getDrops$0_2_.accept(shulkerboxtileentity.getStackInSlot(i));
            }

         });
      }

      return super.getDrops(p_220076_1_, p_220076_2_);
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
         if (tileentity instanceof ShulkerBoxTileEntity) {
            ((ShulkerBoxTileEntity)tileentity).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity tileentity = p_196243_2_.getTileEntity(p_196243_3_);
         if (tileentity instanceof ShulkerBoxTileEntity) {
            p_196243_2_.updateComparatorOutputLevel(p_196243_3_, p_196243_1_.getBlock());
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
      super.addInformation(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
      CompoundNBT compoundnbt = p_190948_1_.getChildTag("BlockEntityTag");
      if (compoundnbt != null) {
         if (compoundnbt.contains("LootTable", 8)) {
            p_190948_3_.add(new StringTextComponent("???????"));
         }

         if (compoundnbt.contains("Items", 9)) {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(compoundnbt, nonnulllist);
            int i = 0;
            int j = 0;
            Iterator var9 = nonnulllist.iterator();

            while(var9.hasNext()) {
               ItemStack itemstack = (ItemStack)var9.next();
               if (!itemstack.isEmpty()) {
                  ++j;
                  if (i <= 4) {
                     ++i;
                     ITextComponent itextcomponent = itemstack.getDisplayName().deepCopy();
                     itextcomponent.appendText(" x").appendText(String.valueOf(itemstack.getCount()));
                     p_190948_3_.add(itextcomponent);
                  }
               }
            }

            if (j - i > 0) {
               p_190948_3_.add((new TranslationTextComponent("container.shulkerBox.more", new Object[]{j - i})).applyTextStyle(TextFormatting.ITALIC));
            }
         }
      }

   }

   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      TileEntity tileentity = p_220053_2_.getTileEntity(p_220053_3_);
      return tileentity instanceof ShulkerBoxTileEntity ? VoxelShapes.create(((ShulkerBoxTileEntity)tileentity).getBoundingBox(p_220053_1_)) : VoxelShapes.fullCube();
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstoneFromInventory((IInventory)p_180641_2_.getTileEntity(p_180641_3_));
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      ItemStack itemstack = super.getItem(p_185473_1_, p_185473_2_, p_185473_3_);
      ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)p_185473_1_.getTileEntity(p_185473_2_);
      CompoundNBT compoundnbt = shulkerboxtileentity.saveToNbt(new CompoundNBT());
      if (!compoundnbt.isEmpty()) {
         itemstack.setTagInfo("BlockEntityTag", compoundnbt);
      }

      return itemstack;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static DyeColor getColorFromItem(Item p_190955_0_) {
      return getColorFromBlock(Block.getBlockFromItem(p_190955_0_));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static DyeColor getColorFromBlock(Block p_190954_0_) {
      return p_190954_0_ instanceof ShulkerBoxBlock ? ((ShulkerBoxBlock)p_190954_0_).getColor() : null;
   }

   public static Block getBlockByColor(@Nullable DyeColor p_190952_0_) {
      if (p_190952_0_ == null) {
         return Blocks.SHULKER_BOX;
      } else {
         switch(p_190952_0_) {
         case WHITE:
            return Blocks.WHITE_SHULKER_BOX;
         case ORANGE:
            return Blocks.ORANGE_SHULKER_BOX;
         case MAGENTA:
            return Blocks.MAGENTA_SHULKER_BOX;
         case LIGHT_BLUE:
            return Blocks.LIGHT_BLUE_SHULKER_BOX;
         case YELLOW:
            return Blocks.YELLOW_SHULKER_BOX;
         case LIME:
            return Blocks.LIME_SHULKER_BOX;
         case PINK:
            return Blocks.PINK_SHULKER_BOX;
         case GRAY:
            return Blocks.GRAY_SHULKER_BOX;
         case LIGHT_GRAY:
            return Blocks.LIGHT_GRAY_SHULKER_BOX;
         case CYAN:
            return Blocks.CYAN_SHULKER_BOX;
         case PURPLE:
         default:
            return Blocks.PURPLE_SHULKER_BOX;
         case BLUE:
            return Blocks.BLUE_SHULKER_BOX;
         case BROWN:
            return Blocks.BROWN_SHULKER_BOX;
         case GREEN:
            return Blocks.GREEN_SHULKER_BOX;
         case RED:
            return Blocks.RED_SHULKER_BOX;
         case BLACK:
            return Blocks.BLACK_SHULKER_BOX;
         }
      }
   }

   @Nullable
   public DyeColor getColor() {
      return this.color;
   }

   public static ItemStack getColoredItemStack(@Nullable DyeColor p_190953_0_) {
      return new ItemStack(getBlockByColor(p_190953_0_));
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   static {
      FACING = DirectionalBlock.FACING;
      field_220169_b = new ResourceLocation("contents");
   }
}
