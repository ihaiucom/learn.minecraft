package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
public abstract class ProjectileItemEntity extends ThrowableEntity implements IRendersAsItem {
   private static final DataParameter<ItemStack> ITEMSTACK_DATA;

   public ProjectileItemEntity(EntityType<? extends ProjectileItemEntity> p_i50155_1_, World p_i50155_2_) {
      super(p_i50155_1_, p_i50155_2_);
   }

   public ProjectileItemEntity(EntityType<? extends ProjectileItemEntity> p_i50156_1_, double p_i50156_2_, double p_i50156_4_, double p_i50156_6_, World p_i50156_8_) {
      super(p_i50156_1_, p_i50156_2_, p_i50156_4_, p_i50156_6_, p_i50156_8_);
   }

   public ProjectileItemEntity(EntityType<? extends ProjectileItemEntity> p_i50157_1_, LivingEntity p_i50157_2_, World p_i50157_3_) {
      super(p_i50157_1_, p_i50157_2_, p_i50157_3_);
   }

   public void func_213884_b(ItemStack p_213884_1_) {
      if (p_213884_1_.getItem() != this.func_213885_i() || p_213884_1_.hasTag()) {
         this.getDataManager().set(ITEMSTACK_DATA, Util.make(p_213884_1_.copy(), (p_213883_0_) -> {
            p_213883_0_.setCount(1);
         }));
      }

   }

   protected abstract Item func_213885_i();

   protected ItemStack func_213882_k() {
      return (ItemStack)this.getDataManager().get(ITEMSTACK_DATA);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      ItemStack lvt_1_1_ = this.func_213882_k();
      return lvt_1_1_.isEmpty() ? new ItemStack(this.func_213885_i()) : lvt_1_1_;
   }

   protected void registerData() {
      this.getDataManager().register(ITEMSTACK_DATA, ItemStack.EMPTY);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      ItemStack lvt_2_1_ = this.func_213882_k();
      if (!lvt_2_1_.isEmpty()) {
         p_213281_1_.put("Item", lvt_2_1_.write(new CompoundNBT()));
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      ItemStack lvt_2_1_ = ItemStack.read(p_70037_1_.getCompound("Item"));
      this.func_213884_b(lvt_2_1_);
   }

   static {
      ITEMSTACK_DATA = EntityDataManager.createKey(ProjectileItemEntity.class, DataSerializers.ITEMSTACK);
   }
}
