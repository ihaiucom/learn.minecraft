package net.minecraft.block;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class SoundType {
   public static final SoundType WOOD;
   public static final SoundType GROUND;
   public static final SoundType PLANT;
   public static final SoundType STONE;
   public static final SoundType METAL;
   public static final SoundType GLASS;
   public static final SoundType CLOTH;
   public static final SoundType SAND;
   public static final SoundType SNOW;
   public static final SoundType LADDER;
   public static final SoundType ANVIL;
   public static final SoundType SLIME;
   public static final SoundType field_226947_m_;
   public static final SoundType WET_GRASS;
   public static final SoundType CORAL;
   public static final SoundType BAMBOO;
   public static final SoundType BAMBOO_SAPLING;
   public static final SoundType SCAFFOLDING;
   public static final SoundType SWEET_BERRY_BUSH;
   public static final SoundType CROP;
   public static final SoundType STEM;
   public static final SoundType NETHER_WART;
   public static final SoundType LANTERN;
   public final float volume;
   public final float pitch;
   private final SoundEvent breakSound;
   private final SoundEvent stepSound;
   private final SoundEvent placeSound;
   private final SoundEvent hitSound;
   private final SoundEvent fallSound;

   public SoundType(float p_i46679_1_, float p_i46679_2_, SoundEvent p_i46679_3_, SoundEvent p_i46679_4_, SoundEvent p_i46679_5_, SoundEvent p_i46679_6_, SoundEvent p_i46679_7_) {
      this.volume = p_i46679_1_;
      this.pitch = p_i46679_2_;
      this.breakSound = p_i46679_3_;
      this.stepSound = p_i46679_4_;
      this.placeSound = p_i46679_5_;
      this.hitSound = p_i46679_6_;
      this.fallSound = p_i46679_7_;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public SoundEvent getBreakSound() {
      return this.breakSound;
   }

   public SoundEvent getStepSound() {
      return this.stepSound;
   }

   public SoundEvent getPlaceSound() {
      return this.placeSound;
   }

   public SoundEvent getHitSound() {
      return this.hitSound;
   }

   public SoundEvent getFallSound() {
      return this.fallSound;
   }

   static {
      WOOD = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WOOD_BREAK, SoundEvents.BLOCK_WOOD_STEP, SoundEvents.BLOCK_WOOD_PLACE, SoundEvents.BLOCK_WOOD_HIT, SoundEvents.BLOCK_WOOD_FALL);
      GROUND = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_GRAVEL_BREAK, SoundEvents.BLOCK_GRAVEL_STEP, SoundEvents.BLOCK_GRAVEL_PLACE, SoundEvents.BLOCK_GRAVEL_HIT, SoundEvents.BLOCK_GRAVEL_FALL);
      PLANT = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_GRASS_BREAK, SoundEvents.BLOCK_GRASS_STEP, SoundEvents.BLOCK_GRASS_PLACE, SoundEvents.BLOCK_GRASS_HIT, SoundEvents.BLOCK_GRASS_FALL);
      STONE = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_STONE_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL);
      METAL = new SoundType(1.0F, 1.5F, SoundEvents.BLOCK_METAL_BREAK, SoundEvents.BLOCK_METAL_STEP, SoundEvents.BLOCK_METAL_PLACE, SoundEvents.BLOCK_METAL_HIT, SoundEvents.BLOCK_METAL_FALL);
      GLASS = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_GLASS_BREAK, SoundEvents.BLOCK_GLASS_STEP, SoundEvents.BLOCK_GLASS_PLACE, SoundEvents.BLOCK_GLASS_HIT, SoundEvents.BLOCK_GLASS_FALL);
      CLOTH = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WOOL_BREAK, SoundEvents.BLOCK_WOOL_STEP, SoundEvents.BLOCK_WOOL_PLACE, SoundEvents.BLOCK_WOOL_HIT, SoundEvents.BLOCK_WOOL_FALL);
      SAND = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SAND_BREAK, SoundEvents.BLOCK_SAND_STEP, SoundEvents.BLOCK_SAND_PLACE, SoundEvents.BLOCK_SAND_HIT, SoundEvents.BLOCK_SAND_FALL);
      SNOW = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SNOW_BREAK, SoundEvents.BLOCK_SNOW_STEP, SoundEvents.BLOCK_SNOW_PLACE, SoundEvents.BLOCK_SNOW_HIT, SoundEvents.BLOCK_SNOW_FALL);
      LADDER = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_LADDER_BREAK, SoundEvents.BLOCK_LADDER_STEP, SoundEvents.BLOCK_LADDER_PLACE, SoundEvents.BLOCK_LADDER_HIT, SoundEvents.BLOCK_LADDER_FALL);
      ANVIL = new SoundType(0.3F, 1.0F, SoundEvents.BLOCK_ANVIL_BREAK, SoundEvents.BLOCK_ANVIL_STEP, SoundEvents.BLOCK_ANVIL_PLACE, SoundEvents.BLOCK_ANVIL_HIT, SoundEvents.BLOCK_ANVIL_FALL);
      SLIME = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SLIME_BLOCK_BREAK, SoundEvents.BLOCK_SLIME_BLOCK_STEP, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundEvents.BLOCK_SLIME_BLOCK_HIT, SoundEvents.BLOCK_SLIME_BLOCK_FALL);
      field_226947_m_ = new SoundType(1.0F, 1.0F, SoundEvents.field_226135_eP_, SoundEvents.field_226140_eU_, SoundEvents.field_226138_eS_, SoundEvents.field_226137_eR_, SoundEvents.field_226136_eQ_);
      WET_GRASS = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WET_GRASS_BREAK, SoundEvents.BLOCK_WET_GRASS_STEP, SoundEvents.BLOCK_WET_GRASS_PLACE, SoundEvents.BLOCK_WET_GRASS_HIT, SoundEvents.BLOCK_WET_GRASS_FALL);
      CORAL = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_CORAL_BLOCK_BREAK, SoundEvents.BLOCK_CORAL_BLOCK_STEP, SoundEvents.BLOCK_CORAL_BLOCK_PLACE, SoundEvents.BLOCK_CORAL_BLOCK_HIT, SoundEvents.BLOCK_CORAL_BLOCK_FALL);
      BAMBOO = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_BAMBOO_BREAK, SoundEvents.BLOCK_BAMBOO_STEP, SoundEvents.BLOCK_BAMBOO_PLACE, SoundEvents.BLOCK_BAMBOO_HIT, SoundEvents.BLOCK_BAMBOO_FALL);
      BAMBOO_SAPLING = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_BAMBOO_SAPLING_BREAK, SoundEvents.BLOCK_BAMBOO_STEP, SoundEvents.BLOCK_BAMBOO_SAPLING_PLACE, SoundEvents.BLOCK_BAMBOO_SAPLING_HIT, SoundEvents.BLOCK_BAMBOO_FALL);
      SCAFFOLDING = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SCAFFOLDING_BREAK, SoundEvents.BLOCK_SCAFFOLDING_STEP, SoundEvents.BLOCK_SCAFFOLDING_PLACE, SoundEvents.BLOCK_SCAFFOLDING_HIT, SoundEvents.BLOCK_SCAFFOLDING_FALL);
      SWEET_BERRY_BUSH = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SWEET_BERRY_BUSH_BREAK, SoundEvents.BLOCK_GRASS_STEP, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PLACE, SoundEvents.BLOCK_GRASS_HIT, SoundEvents.BLOCK_GRASS_FALL);
      CROP = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_CROP_BREAK, SoundEvents.BLOCK_GRASS_STEP, SoundEvents.ITEM_CROP_PLANT, SoundEvents.BLOCK_GRASS_HIT, SoundEvents.BLOCK_GRASS_FALL);
      STEM = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WOOD_BREAK, SoundEvents.BLOCK_WOOD_STEP, SoundEvents.ITEM_CROP_PLANT, SoundEvents.BLOCK_WOOD_HIT, SoundEvents.BLOCK_WOOD_FALL);
      NETHER_WART = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_NETHER_WART_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.ITEM_NETHER_WART_PLANT, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL);
      LANTERN = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_LANTERN_BREAK, SoundEvents.BLOCK_LANTERN_STEP, SoundEvents.BLOCK_LANTERN_PLACE, SoundEvents.BLOCK_LANTERN_HIT, SoundEvents.BLOCK_LANTERN_FALL);
   }
}
