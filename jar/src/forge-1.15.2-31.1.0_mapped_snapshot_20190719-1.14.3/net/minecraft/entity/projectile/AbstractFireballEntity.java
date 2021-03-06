package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public abstract class AbstractFireballEntity extends DamagingProjectileEntity implements IRendersAsItem {
   private static final DataParameter<ItemStack> field_213899_f;

   public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> p_i50166_1_, World p_i50166_2_) {
      super(p_i50166_1_, p_i50166_2_);
   }

   public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> p_i50167_1_, double p_i50167_2_, double p_i50167_4_, double p_i50167_6_, double p_i50167_8_, double p_i50167_10_, double p_i50167_12_, World p_i50167_14_) {
      super(p_i50167_1_, p_i50167_2_, p_i50167_4_, p_i50167_6_, p_i50167_8_, p_i50167_10_, p_i50167_12_, p_i50167_14_);
   }

   public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> p_i50168_1_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_, World p_i50168_9_) {
      super(p_i50168_1_, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
   }

   public void func_213898_b(ItemStack p_213898_1_) {
      if (p_213898_1_.getItem() != Items.FIRE_CHARGE || p_213898_1_.hasTag()) {
         this.getDataManager().set(field_213899_f, Util.make(p_213898_1_.copy(), (p_213897_0_) -> {
            p_213897_0_.setCount(1);
         }));
      }

   }

   protected ItemStack func_213896_l() {
      return (ItemStack)this.getDataManager().get(field_213899_f);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      ItemStack lvt_1_1_ = this.func_213896_l();
      return lvt_1_1_.isEmpty() ? new ItemStack(Items.FIRE_CHARGE) : lvt_1_1_;
   }

   protected void registerData() {
      this.getDataManager().register(field_213899_f, ItemStack.EMPTY);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      ItemStack lvt_2_1_ = this.func_213896_l();
      if (!lvt_2_1_.isEmpty()) {
         p_213281_1_.put("Item", lvt_2_1_.write(new CompoundNBT()));
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      ItemStack lvt_2_1_ = ItemStack.read(p_70037_1_.getCompound("Item"));
      this.func_213898_b(lvt_2_1_);
   }

   static {
      field_213899_f = EntityDataManager.createKey(AbstractFireballEntity.class, DataSerializers.ITEMSTACK);
   }
}
