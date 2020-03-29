package net.minecraft.entity.boss.dragon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;

public class EnderDragonPartEntity extends Entity {
   public final EnderDragonEntity dragon;
   public final String field_213853_c;
   private final EntitySize field_213854_d;

   public EnderDragonPartEntity(EnderDragonEntity p_i50232_1_, String p_i50232_2_, float p_i50232_3_, float p_i50232_4_) {
      super(p_i50232_1_.getType(), p_i50232_1_.world);
      this.field_213854_d = EntitySize.flexible(p_i50232_3_, p_i50232_4_);
      this.recalculateSize();
      this.dragon = p_i50232_1_;
      this.field_213853_c = p_i50232_2_;
   }

   protected void registerData() {
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return this.isInvulnerableTo(p_70097_1_) ? false : this.dragon.func_213403_a(this, p_70097_1_, p_70097_2_);
   }

   public boolean isEntityEqual(Entity p_70028_1_) {
      return this == p_70028_1_ || this.dragon == p_70028_1_;
   }

   public IPacket<?> createSpawnPacket() {
      throw new UnsupportedOperationException();
   }

   public EntitySize getSize(Pose p_213305_1_) {
      return this.field_213854_d;
   }
}
