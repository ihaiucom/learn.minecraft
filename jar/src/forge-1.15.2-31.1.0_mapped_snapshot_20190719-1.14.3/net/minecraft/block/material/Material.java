package net.minecraft.block.material;

public final class Material {
   public static final Material AIR;
   public static final Material STRUCTURE_VOID;
   public static final Material PORTAL;
   public static final Material CARPET;
   public static final Material PLANTS;
   public static final Material OCEAN_PLANT;
   public static final Material TALL_PLANTS;
   public static final Material SEA_GRASS;
   public static final Material WATER;
   public static final Material BUBBLE_COLUMN;
   public static final Material LAVA;
   public static final Material SNOW;
   public static final Material FIRE;
   public static final Material MISCELLANEOUS;
   public static final Material WEB;
   public static final Material REDSTONE_LIGHT;
   public static final Material CLAY;
   public static final Material EARTH;
   public static final Material ORGANIC;
   public static final Material PACKED_ICE;
   public static final Material SAND;
   public static final Material SPONGE;
   public static final Material SHULKER;
   public static final Material WOOD;
   public static final Material BAMBOO_SAPLING;
   public static final Material BAMBOO;
   public static final Material WOOL;
   public static final Material TNT;
   public static final Material LEAVES;
   public static final Material GLASS;
   public static final Material ICE;
   public static final Material CACTUS;
   public static final Material ROCK;
   public static final Material IRON;
   public static final Material SNOW_BLOCK;
   public static final Material ANVIL;
   public static final Material BARRIER;
   public static final Material PISTON;
   public static final Material CORAL;
   public static final Material GOURD;
   public static final Material DRAGON_EGG;
   public static final Material CAKE;
   private final MaterialColor color;
   private final PushReaction pushReaction;
   private final boolean blocksMovement;
   private final boolean flammable;
   private final boolean requiresNoTool;
   private final boolean isLiquid;
   private final boolean isOpaque;
   private final boolean replaceable;
   private final boolean isSolid;

   public Material(MaterialColor p_i48243_1_, boolean p_i48243_2_, boolean p_i48243_3_, boolean p_i48243_4_, boolean p_i48243_5_, boolean p_i48243_6_, boolean p_i48243_7_, boolean p_i48243_8_, PushReaction p_i48243_9_) {
      this.color = p_i48243_1_;
      this.isLiquid = p_i48243_2_;
      this.isSolid = p_i48243_3_;
      this.blocksMovement = p_i48243_4_;
      this.isOpaque = p_i48243_5_;
      this.requiresNoTool = p_i48243_6_;
      this.flammable = p_i48243_7_;
      this.replaceable = p_i48243_8_;
      this.pushReaction = p_i48243_9_;
   }

   public boolean isLiquid() {
      return this.isLiquid;
   }

   public boolean isSolid() {
      return this.isSolid;
   }

   public boolean blocksMovement() {
      return this.blocksMovement;
   }

   public boolean isFlammable() {
      return this.flammable;
   }

   public boolean isReplaceable() {
      return this.replaceable;
   }

   public boolean isOpaque() {
      return this.isOpaque;
   }

   public boolean isToolNotRequired() {
      return this.requiresNoTool;
   }

   public PushReaction getPushReaction() {
      return this.pushReaction;
   }

   public MaterialColor getColor() {
      return this.color;
   }

   static {
      AIR = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque().notSolid().replaceable().build();
      STRUCTURE_VOID = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque().notSolid().replaceable().build();
      PORTAL = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque().notSolid().pushBlocks().build();
      CARPET = (new Material.Builder(MaterialColor.WOOL)).doesNotBlockMovement().notOpaque().notSolid().flammable().build();
      PLANTS = (new Material.Builder(MaterialColor.FOLIAGE)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().build();
      OCEAN_PLANT = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().build();
      TALL_PLANTS = (new Material.Builder(MaterialColor.FOLIAGE)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().flammable().build();
      SEA_GRASS = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().build();
      WATER = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().liquid().build();
      BUBBLE_COLUMN = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().liquid().build();
      LAVA = (new Material.Builder(MaterialColor.TNT)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().liquid().build();
      SNOW = (new Material.Builder(MaterialColor.SNOW)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().requiresTool().build();
      FIRE = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().build();
      MISCELLANEOUS = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().build();
      WEB = (new Material.Builder(MaterialColor.WOOL)).doesNotBlockMovement().notOpaque().pushDestroys().requiresTool().build();
      REDSTONE_LIGHT = (new Material.Builder(MaterialColor.AIR)).build();
      CLAY = (new Material.Builder(MaterialColor.CLAY)).build();
      EARTH = (new Material.Builder(MaterialColor.DIRT)).build();
      ORGANIC = (new Material.Builder(MaterialColor.GRASS)).build();
      PACKED_ICE = (new Material.Builder(MaterialColor.ICE)).build();
      SAND = (new Material.Builder(MaterialColor.SAND)).build();
      SPONGE = (new Material.Builder(MaterialColor.YELLOW)).build();
      SHULKER = (new Material.Builder(MaterialColor.PURPLE)).build();
      WOOD = (new Material.Builder(MaterialColor.WOOD)).flammable().build();
      BAMBOO_SAPLING = (new Material.Builder(MaterialColor.WOOD)).flammable().pushDestroys().doesNotBlockMovement().build();
      BAMBOO = (new Material.Builder(MaterialColor.WOOD)).flammable().pushDestroys().build();
      WOOL = (new Material.Builder(MaterialColor.WOOL)).flammable().build();
      TNT = (new Material.Builder(MaterialColor.TNT)).flammable().notOpaque().build();
      LEAVES = (new Material.Builder(MaterialColor.FOLIAGE)).flammable().notOpaque().pushDestroys().build();
      GLASS = (new Material.Builder(MaterialColor.AIR)).notOpaque().build();
      ICE = (new Material.Builder(MaterialColor.ICE)).notOpaque().build();
      CACTUS = (new Material.Builder(MaterialColor.FOLIAGE)).notOpaque().pushDestroys().build();
      ROCK = (new Material.Builder(MaterialColor.STONE)).requiresTool().build();
      IRON = (new Material.Builder(MaterialColor.IRON)).requiresTool().build();
      SNOW_BLOCK = (new Material.Builder(MaterialColor.SNOW)).requiresTool().build();
      ANVIL = (new Material.Builder(MaterialColor.IRON)).requiresTool().pushBlocks().build();
      BARRIER = (new Material.Builder(MaterialColor.AIR)).requiresTool().pushBlocks().build();
      PISTON = (new Material.Builder(MaterialColor.STONE)).pushBlocks().build();
      CORAL = (new Material.Builder(MaterialColor.FOLIAGE)).pushDestroys().build();
      GOURD = (new Material.Builder(MaterialColor.FOLIAGE)).pushDestroys().build();
      DRAGON_EGG = (new Material.Builder(MaterialColor.FOLIAGE)).pushDestroys().build();
      CAKE = (new Material.Builder(MaterialColor.AIR)).pushDestroys().build();
   }

   public static class Builder {
      private PushReaction pushReaction;
      private boolean blocksMovement;
      private boolean canBurn;
      private boolean requiresNoTool;
      private boolean isLiquid;
      private boolean isReplaceable;
      private boolean isSolid;
      private final MaterialColor color;
      private boolean isOpaque;

      public Builder(MaterialColor p_i48270_1_) {
         this.pushReaction = PushReaction.NORMAL;
         this.blocksMovement = true;
         this.requiresNoTool = true;
         this.isSolid = true;
         this.isOpaque = true;
         this.color = p_i48270_1_;
      }

      public Material.Builder liquid() {
         this.isLiquid = true;
         return this;
      }

      public Material.Builder notSolid() {
         this.isSolid = false;
         return this;
      }

      public Material.Builder doesNotBlockMovement() {
         this.blocksMovement = false;
         return this;
      }

      private Material.Builder notOpaque() {
         this.isOpaque = false;
         return this;
      }

      protected Material.Builder requiresTool() {
         this.requiresNoTool = false;
         return this;
      }

      protected Material.Builder flammable() {
         this.canBurn = true;
         return this;
      }

      public Material.Builder replaceable() {
         this.isReplaceable = true;
         return this;
      }

      protected Material.Builder pushDestroys() {
         this.pushReaction = PushReaction.DESTROY;
         return this;
      }

      protected Material.Builder pushBlocks() {
         this.pushReaction = PushReaction.BLOCK;
         return this;
      }

      public Material build() {
         return new Material(this.color, this.isLiquid, this.isSolid, this.blocksMovement, this.isOpaque, this.requiresNoTool, this.canBurn, this.isReplaceable, this.pushReaction);
      }
   }
}
