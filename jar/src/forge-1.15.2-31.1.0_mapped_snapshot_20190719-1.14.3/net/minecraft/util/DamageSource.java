package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;

public class DamageSource {
   public static final DamageSource IN_FIRE = (new DamageSource("inFire")).setFireDamage();
   public static final DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
   public static final DamageSource ON_FIRE = (new DamageSource("onFire")).setDamageBypassesArmor().setFireDamage();
   public static final DamageSource LAVA = (new DamageSource("lava")).setFireDamage();
   public static final DamageSource HOT_FLOOR = (new DamageSource("hotFloor")).setFireDamage();
   public static final DamageSource IN_WALL = (new DamageSource("inWall")).setDamageBypassesArmor();
   public static final DamageSource CRAMMING = (new DamageSource("cramming")).setDamageBypassesArmor();
   public static final DamageSource DROWN = (new DamageSource("drown")).setDamageBypassesArmor();
   public static final DamageSource STARVE = (new DamageSource("starve")).setDamageBypassesArmor().setDamageIsAbsolute();
   public static final DamageSource CACTUS = new DamageSource("cactus");
   public static final DamageSource FALL = (new DamageSource("fall")).setDamageBypassesArmor();
   public static final DamageSource FLY_INTO_WALL = (new DamageSource("flyIntoWall")).setDamageBypassesArmor();
   public static final DamageSource OUT_OF_WORLD = (new DamageSource("outOfWorld")).setDamageBypassesArmor().setDamageAllowedInCreativeMode();
   public static final DamageSource GENERIC = (new DamageSource("generic")).setDamageBypassesArmor();
   public static final DamageSource MAGIC = (new DamageSource("magic")).setDamageBypassesArmor().setMagicDamage();
   public static final DamageSource WITHER = (new DamageSource("wither")).setDamageBypassesArmor();
   public static final DamageSource ANVIL = new DamageSource("anvil");
   public static final DamageSource FALLING_BLOCK = new DamageSource("fallingBlock");
   public static final DamageSource DRAGON_BREATH = (new DamageSource("dragonBreath")).setDamageBypassesArmor();
   public static final DamageSource FIREWORKS = (new DamageSource("fireworks")).setExplosion();
   public static final DamageSource DRYOUT = new DamageSource("dryout");
   public static final DamageSource SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
   private boolean isUnblockable;
   private boolean isDamageAllowedInCreativeMode;
   private boolean damageIsAbsolute;
   private float hungerDamage = 0.1F;
   private boolean fireDamage;
   private boolean projectile;
   private boolean difficultyScaled;
   private boolean magicDamage;
   private boolean explosion;
   public final String damageType;

   public static DamageSource func_226252_a_(LivingEntity p_226252_0_) {
      return new EntityDamageSource("sting", p_226252_0_);
   }

   public static DamageSource causeMobDamage(LivingEntity p_76358_0_) {
      return new EntityDamageSource("mob", p_76358_0_);
   }

   public static DamageSource causeIndirectDamage(Entity p_188403_0_, LivingEntity p_188403_1_) {
      return new IndirectEntityDamageSource("mob", p_188403_0_, p_188403_1_);
   }

   public static DamageSource causePlayerDamage(PlayerEntity p_76365_0_) {
      return new EntityDamageSource("player", p_76365_0_);
   }

   public static DamageSource causeArrowDamage(AbstractArrowEntity p_76353_0_, @Nullable Entity p_76353_1_) {
      return (new IndirectEntityDamageSource("arrow", p_76353_0_, p_76353_1_)).setProjectile();
   }

   public static DamageSource causeTridentDamage(Entity p_203096_0_, @Nullable Entity p_203096_1_) {
      return (new IndirectEntityDamageSource("trident", p_203096_0_, p_203096_1_)).setProjectile();
   }

   public static DamageSource causeFireballDamage(DamagingProjectileEntity p_76362_0_, @Nullable Entity p_76362_1_) {
      return p_76362_1_ == null ? (new IndirectEntityDamageSource("onFire", p_76362_0_, p_76362_0_)).setFireDamage().setProjectile() : (new IndirectEntityDamageSource("fireball", p_76362_0_, p_76362_1_)).setFireDamage().setProjectile();
   }

   public static DamageSource causeThrownDamage(Entity p_76356_0_, @Nullable Entity p_76356_1_) {
      return (new IndirectEntityDamageSource("thrown", p_76356_0_, p_76356_1_)).setProjectile();
   }

   public static DamageSource causeIndirectMagicDamage(Entity p_76354_0_, @Nullable Entity p_76354_1_) {
      return (new IndirectEntityDamageSource("indirectMagic", p_76354_0_, p_76354_1_)).setDamageBypassesArmor().setMagicDamage();
   }

   public static DamageSource causeThornsDamage(Entity p_92087_0_) {
      return (new EntityDamageSource("thorns", p_92087_0_)).setIsThornsDamage().setMagicDamage();
   }

   public static DamageSource causeExplosionDamage(@Nullable Explosion p_94539_0_) {
      return p_94539_0_ != null && p_94539_0_.getExplosivePlacedBy() != null ? (new EntityDamageSource("explosion.player", p_94539_0_.getExplosivePlacedBy())).setDifficultyScaled().setExplosion() : (new DamageSource("explosion")).setDifficultyScaled().setExplosion();
   }

   public static DamageSource causeExplosionDamage(@Nullable LivingEntity p_188405_0_) {
      return p_188405_0_ != null ? (new EntityDamageSource("explosion.player", p_188405_0_)).setDifficultyScaled().setExplosion() : (new DamageSource("explosion")).setDifficultyScaled().setExplosion();
   }

   public static DamageSource netherBedExplosion() {
      return new NetherBedDamageSource();
   }

   public boolean isProjectile() {
      return this.projectile;
   }

   public DamageSource setProjectile() {
      this.projectile = true;
      return this;
   }

   public boolean isExplosion() {
      return this.explosion;
   }

   public DamageSource setExplosion() {
      this.explosion = true;
      return this;
   }

   public boolean isUnblockable() {
      return this.isUnblockable;
   }

   public float getHungerDamage() {
      return this.hungerDamage;
   }

   public boolean canHarmInCreative() {
      return this.isDamageAllowedInCreativeMode;
   }

   public boolean isDamageAbsolute() {
      return this.damageIsAbsolute;
   }

   public DamageSource(String p_i1566_1_) {
      this.damageType = p_i1566_1_;
   }

   @Nullable
   public Entity getImmediateSource() {
      return this.getTrueSource();
   }

   @Nullable
   public Entity getTrueSource() {
      return null;
   }

   public DamageSource setDamageBypassesArmor() {
      this.isUnblockable = true;
      this.hungerDamage = 0.0F;
      return this;
   }

   public DamageSource setDamageAllowedInCreativeMode() {
      this.isDamageAllowedInCreativeMode = true;
      return this;
   }

   public DamageSource setDamageIsAbsolute() {
      this.damageIsAbsolute = true;
      this.hungerDamage = 0.0F;
      return this;
   }

   public DamageSource setFireDamage() {
      this.fireDamage = true;
      return this;
   }

   public ITextComponent getDeathMessage(LivingEntity p_151519_1_) {
      LivingEntity lvt_2_1_ = p_151519_1_.getAttackingEntity();
      String lvt_3_1_ = "death.attack." + this.damageType;
      String lvt_4_1_ = lvt_3_1_ + ".player";
      return lvt_2_1_ != null ? new TranslationTextComponent(lvt_4_1_, new Object[]{p_151519_1_.getDisplayName(), lvt_2_1_.getDisplayName()}) : new TranslationTextComponent(lvt_3_1_, new Object[]{p_151519_1_.getDisplayName()});
   }

   public boolean isFireDamage() {
      return this.fireDamage;
   }

   public String getDamageType() {
      return this.damageType;
   }

   public DamageSource setDifficultyScaled() {
      this.difficultyScaled = true;
      return this;
   }

   public boolean isDifficultyScaled() {
      return this.difficultyScaled;
   }

   public boolean isMagicDamage() {
      return this.magicDamage;
   }

   public DamageSource setMagicDamage() {
      this.magicDamage = true;
      return this;
   }

   public boolean isCreativePlayer() {
      Entity lvt_1_1_ = this.getTrueSource();
      return lvt_1_1_ instanceof PlayerEntity && ((PlayerEntity)lvt_1_1_).abilities.isCreativeMode;
   }

   @Nullable
   public Vec3d getDamageLocation() {
      return null;
   }
}
