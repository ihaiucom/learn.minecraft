package net.minecraft.item;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ItemGroup {
   public static ItemGroup[] GROUPS = new ItemGroup[12];
   public static final ItemGroup BUILDING_BLOCKS = (new ItemGroup(0, "buildingBlocks") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.BRICKS);
      }
   }).setTabPath("building_blocks");
   public static final ItemGroup DECORATIONS = new ItemGroup(1, "decorations") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.PEONY);
      }
   };
   public static final ItemGroup REDSTONE = new ItemGroup(2, "redstone") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.REDSTONE);
      }
   };
   public static final ItemGroup TRANSPORTATION = new ItemGroup(3, "transportation") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.POWERED_RAIL);
      }
   };
   public static final ItemGroup MISC = new ItemGroup(6, "misc") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.LAVA_BUCKET);
      }
   };
   public static final ItemGroup SEARCH = (new ItemGroup(5, "search") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.COMPASS);
      }
   }).setBackgroundImageName("item_search.png");
   public static final ItemGroup FOOD = new ItemGroup(7, "food") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.APPLE);
      }
   };
   public static final ItemGroup TOOLS;
   public static final ItemGroup COMBAT;
   public static final ItemGroup BREWING;
   public static final ItemGroup MATERIALS;
   public static final ItemGroup HOTBAR;
   public static final ItemGroup INVENTORY;
   private final int index;
   private final String tabLabel;
   private String tabPath;
   private String backgroundTexture;
   private boolean hasScrollbar;
   private boolean drawTitle;
   private EnchantmentType[] enchantmentTypes;
   private ItemStack icon;
   private static final ResourceLocation CREATIVE_INVENTORY_TABS;

   public ItemGroup(String p_i230074_1_) {
      this(-1, p_i230074_1_);
   }

   public ItemGroup(int p_i1853_1_, String p_i1853_2_) {
      this.backgroundTexture = "items.png";
      this.hasScrollbar = true;
      this.drawTitle = true;
      this.enchantmentTypes = new EnchantmentType[0];
      this.tabLabel = p_i1853_2_;
      this.icon = ItemStack.EMPTY;
      this.index = addGroupSafe(p_i1853_1_, this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getIndex() {
      return this.index;
   }

   @OnlyIn(Dist.CLIENT)
   public String getTabLabel() {
      return this.tabLabel;
   }

   public String getPath() {
      return this.tabPath == null ? this.tabLabel : this.tabPath;
   }

   @OnlyIn(Dist.CLIENT)
   public String getTranslationKey() {
      return "itemGroup." + this.getTabLabel();
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getIcon() {
      if (this.icon.isEmpty()) {
         this.icon = this.createIcon();
      }

      return this.icon;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract ItemStack createIcon();

   @OnlyIn(Dist.CLIENT)
   public String getBackgroundImageName() {
      return this.backgroundTexture;
   }

   public ItemGroup setBackgroundImageName(String p_78025_1_) {
      this.backgroundTexture = p_78025_1_;
      return this;
   }

   public ItemGroup setTabPath(String p_199783_1_) {
      this.tabPath = p_199783_1_;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean drawInForegroundOfTab() {
      return this.drawTitle;
   }

   public ItemGroup setNoTitle() {
      this.drawTitle = false;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasScrollbar() {
      return this.hasScrollbar;
   }

   public ItemGroup setNoScrollbar() {
      this.hasScrollbar = false;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public int getColumn() {
      return this.index > 11 ? (this.index - 12) % 10 % 5 : this.index % 6;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOnTopRow() {
      if (this.index > 11) {
         return (this.index - 12) % 10 < 5;
      } else {
         return this.index < 6;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAlignedRight() {
      return this.getColumn() == 5;
   }

   public EnchantmentType[] getRelevantEnchantmentTypes() {
      return this.enchantmentTypes;
   }

   public ItemGroup setRelevantEnchantmentTypes(EnchantmentType... p_111229_1_) {
      this.enchantmentTypes = p_111229_1_;
      return this;
   }

   public boolean hasRelevantEnchantmentType(@Nullable EnchantmentType p_111226_1_) {
      if (p_111226_1_ != null) {
         EnchantmentType[] var2 = this.enchantmentTypes;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnchantmentType enchantmenttype = var2[var4];
            if (enchantmenttype == p_111226_1_) {
               return true;
            }
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public void fill(NonNullList<ItemStack> p_78018_1_) {
      Iterator var2 = Registry.ITEM.iterator();

      while(var2.hasNext()) {
         Item item = (Item)var2.next();
         item.fillItemGroup(this, p_78018_1_);
      }

   }

   public int getTabPage() {
      return this.index < 12 ? 0 : (this.index - 12) / 10 + 1;
   }

   public boolean hasSearchBar() {
      return this.index == SEARCH.index;
   }

   public int getSearchbarWidth() {
      return 89;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getBackgroundImage() {
      return new ResourceLocation("textures/gui/container/creative_inventory/tab_" + this.getBackgroundImageName());
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getTabsImage() {
      return CREATIVE_INVENTORY_TABS;
   }

   public int getLabelColor() {
      return 4210752;
   }

   public int getSlotColor() {
      return -2130706433;
   }

   public static synchronized int getGroupCountSafe() {
      return GROUPS.length;
   }

   private static synchronized int addGroupSafe(int p_addGroupSafe_0_, ItemGroup p_addGroupSafe_1_) {
      if (p_addGroupSafe_0_ == -1) {
         p_addGroupSafe_0_ = GROUPS.length;
      }

      if (p_addGroupSafe_0_ >= GROUPS.length) {
         ItemGroup[] tmp = new ItemGroup[p_addGroupSafe_0_ + 1];
         System.arraycopy(GROUPS, 0, tmp, 0, GROUPS.length);
         GROUPS = tmp;
      }

      GROUPS[p_addGroupSafe_0_] = p_addGroupSafe_1_;
      return p_addGroupSafe_0_;
   }

   static {
      TOOLS = (new ItemGroup(8, "tools") {
         @OnlyIn(Dist.CLIENT)
         public ItemStack createIcon() {
            return new ItemStack(Items.IRON_AXE);
         }
      }).setRelevantEnchantmentTypes(new EnchantmentType[]{EnchantmentType.ALL, EnchantmentType.DIGGER, EnchantmentType.FISHING_ROD, EnchantmentType.BREAKABLE});
      COMBAT = (new ItemGroup(9, "combat") {
         @OnlyIn(Dist.CLIENT)
         public ItemStack createIcon() {
            return new ItemStack(Items.GOLDEN_SWORD);
         }
      }).setRelevantEnchantmentTypes(new EnchantmentType[]{EnchantmentType.ALL, EnchantmentType.ARMOR, EnchantmentType.ARMOR_FEET, EnchantmentType.ARMOR_HEAD, EnchantmentType.ARMOR_LEGS, EnchantmentType.ARMOR_CHEST, EnchantmentType.BOW, EnchantmentType.WEAPON, EnchantmentType.WEARABLE, EnchantmentType.BREAKABLE, EnchantmentType.TRIDENT, EnchantmentType.CROSSBOW});
      BREWING = new ItemGroup(10, "brewing") {
         @OnlyIn(Dist.CLIENT)
         public ItemStack createIcon() {
            return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
         }
      };
      MATERIALS = MISC;
      HOTBAR = new ItemGroup(4, "hotbar") {
         @OnlyIn(Dist.CLIENT)
         public ItemStack createIcon() {
            return new ItemStack(Blocks.BOOKSHELF);
         }

         @OnlyIn(Dist.CLIENT)
         public void fill(NonNullList<ItemStack> p_78018_1_) {
            throw new RuntimeException("Implement exception client-side.");
         }

         @OnlyIn(Dist.CLIENT)
         public boolean isAlignedRight() {
            return true;
         }
      };
      INVENTORY = (new ItemGroup(11, "inventory") {
         @OnlyIn(Dist.CLIENT)
         public ItemStack createIcon() {
            return new ItemStack(Blocks.CHEST);
         }
      }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
      CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   }
}
