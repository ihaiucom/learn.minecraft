package net.minecraft.client.gui.screen.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.HotbarSnapshot;
import net.minecraft.client.util.IMutableSearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class CreativeScreen extends DisplayEffectsScreen<CreativeScreen.CreativeContainer> {
   private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static final Inventory TMP_INVENTORY = new Inventory(45);
   private static int selectedTabIndex;
   private float currentScroll;
   private boolean isScrolling;
   private TextFieldWidget searchField;
   @Nullable
   private List<Slot> originalSlots;
   @Nullable
   private Slot destroyItemSlot;
   private CreativeCraftingListener listener;
   private boolean field_195377_F;
   private boolean field_199506_G;
   private final Map<ResourceLocation, Tag<Item>> tagSearchResults = Maps.newTreeMap();
   private static int tabPage;
   private int maxPages = 0;

   public CreativeScreen(PlayerEntity p_i1088_1_) {
      super(new CreativeScreen.CreativeContainer(p_i1088_1_), p_i1088_1_.inventory, new StringTextComponent(""));
      p_i1088_1_.openContainer = this.container;
      this.passEvents = true;
      this.ySize = 136;
      this.xSize = 195;
   }

   public void tick() {
      if (!this.minecraft.playerController.isInCreativeMode()) {
         this.minecraft.displayGuiScreen(new InventoryScreen(this.minecraft.player));
      } else if (this.searchField != null) {
         this.searchField.tick();
      }

   }

   protected void handleMouseClick(@Nullable Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
      if (this.hasTmpInventory(p_184098_1_)) {
         this.searchField.setCursorPositionEnd();
         this.searchField.setSelectionPos(0);
      }

      boolean flag = p_184098_4_ == ClickType.QUICK_MOVE;
      p_184098_4_ = p_184098_2_ == -999 && p_184098_4_ == ClickType.PICKUP ? ClickType.THROW : p_184098_4_;
      ItemStack itemstack5;
      PlayerInventory playerinventory;
      if (p_184098_1_ == null && selectedTabIndex != ItemGroup.INVENTORY.getIndex() && p_184098_4_ != ClickType.QUICK_CRAFT) {
         playerinventory = this.minecraft.player.inventory;
         if (!playerinventory.getItemStack().isEmpty() && this.field_199506_G) {
            if (p_184098_3_ == 0) {
               this.minecraft.player.dropItem(playerinventory.getItemStack(), true);
               this.minecraft.playerController.sendPacketDropItem(playerinventory.getItemStack());
               playerinventory.setItemStack(ItemStack.EMPTY);
            }

            if (p_184098_3_ == 1) {
               itemstack5 = playerinventory.getItemStack().split(1);
               this.minecraft.player.dropItem(itemstack5, true);
               this.minecraft.playerController.sendPacketDropItem(itemstack5);
            }
         }
      } else {
         if (p_184098_1_ != null && !p_184098_1_.canTakeStack(this.minecraft.player)) {
            return;
         }

         if (p_184098_1_ == this.destroyItemSlot && flag) {
            for(int j = 0; j < this.minecraft.player.container.getInventory().size(); ++j) {
               this.minecraft.playerController.sendSlotPacket(ItemStack.EMPTY, j);
            }
         } else {
            ItemStack itemstack3;
            if (selectedTabIndex == ItemGroup.INVENTORY.getIndex()) {
               if (p_184098_1_ == this.destroyItemSlot) {
                  this.minecraft.player.inventory.setItemStack(ItemStack.EMPTY);
               } else if (p_184098_4_ == ClickType.THROW && p_184098_1_ != null && p_184098_1_.getHasStack()) {
                  itemstack3 = p_184098_1_.decrStackSize(p_184098_3_ == 0 ? 1 : p_184098_1_.getStack().getMaxStackSize());
                  itemstack5 = p_184098_1_.getStack();
                  this.minecraft.player.dropItem(itemstack3, true);
                  this.minecraft.playerController.sendPacketDropItem(itemstack3);
                  this.minecraft.playerController.sendSlotPacket(itemstack5, ((CreativeScreen.CreativeSlot)p_184098_1_).slot.slotNumber);
               } else if (p_184098_4_ == ClickType.THROW && !this.minecraft.player.inventory.getItemStack().isEmpty()) {
                  this.minecraft.player.dropItem(this.minecraft.player.inventory.getItemStack(), true);
                  this.minecraft.playerController.sendPacketDropItem(this.minecraft.player.inventory.getItemStack());
                  this.minecraft.player.inventory.setItemStack(ItemStack.EMPTY);
               } else {
                  this.minecraft.player.container.slotClick(p_184098_1_ == null ? p_184098_2_ : ((CreativeScreen.CreativeSlot)p_184098_1_).slot.slotNumber, p_184098_3_, p_184098_4_, this.minecraft.player);
                  this.minecraft.player.container.detectAndSendChanges();
               }
            } else {
               ItemStack itemstack2;
               if (p_184098_4_ != ClickType.QUICK_CRAFT && p_184098_1_.inventory == TMP_INVENTORY) {
                  playerinventory = this.minecraft.player.inventory;
                  itemstack5 = playerinventory.getItemStack();
                  ItemStack itemstack7 = p_184098_1_.getStack();
                  if (p_184098_4_ == ClickType.SWAP) {
                     if (!itemstack7.isEmpty() && p_184098_3_ >= 0 && p_184098_3_ < 9) {
                        itemstack2 = itemstack7.copy();
                        itemstack2.setCount(itemstack2.getMaxStackSize());
                        this.minecraft.player.inventory.setInventorySlotContents(p_184098_3_, itemstack2);
                        this.minecraft.player.container.detectAndSendChanges();
                     }

                     return;
                  }

                  if (p_184098_4_ == ClickType.CLONE) {
                     if (playerinventory.getItemStack().isEmpty() && p_184098_1_.getHasStack()) {
                        itemstack2 = p_184098_1_.getStack().copy();
                        itemstack2.setCount(itemstack2.getMaxStackSize());
                        playerinventory.setItemStack(itemstack2);
                     }

                     return;
                  }

                  if (p_184098_4_ == ClickType.THROW) {
                     if (!itemstack7.isEmpty()) {
                        itemstack2 = itemstack7.copy();
                        itemstack2.setCount(p_184098_3_ == 0 ? 1 : itemstack2.getMaxStackSize());
                        this.minecraft.player.dropItem(itemstack2, true);
                        this.minecraft.playerController.sendPacketDropItem(itemstack2);
                     }

                     return;
                  }

                  if (!itemstack5.isEmpty() && !itemstack7.isEmpty() && itemstack5.isItemEqual(itemstack7) && ItemStack.areItemStackTagsEqual(itemstack5, itemstack7)) {
                     if (p_184098_3_ == 0) {
                        if (flag) {
                           itemstack5.setCount(itemstack5.getMaxStackSize());
                        } else if (itemstack5.getCount() < itemstack5.getMaxStackSize()) {
                           itemstack5.grow(1);
                        }
                     } else {
                        itemstack5.shrink(1);
                     }
                  } else if (!itemstack7.isEmpty() && itemstack5.isEmpty()) {
                     playerinventory.setItemStack(itemstack7.copy());
                     itemstack5 = playerinventory.getItemStack();
                     if (flag) {
                        itemstack5.setCount(itemstack5.getMaxStackSize());
                     }
                  } else if (p_184098_3_ == 0) {
                     playerinventory.setItemStack(ItemStack.EMPTY);
                  } else {
                     playerinventory.getItemStack().shrink(1);
                  }
               } else if (this.container != null) {
                  itemstack3 = p_184098_1_ == null ? ItemStack.EMPTY : ((CreativeScreen.CreativeContainer)this.container).getSlot(p_184098_1_.slotNumber).getStack();
                  ((CreativeScreen.CreativeContainer)this.container).slotClick(p_184098_1_ == null ? p_184098_2_ : p_184098_1_.slotNumber, p_184098_3_, p_184098_4_, this.minecraft.player);
                  if (Container.getDragEvent(p_184098_3_) == 2) {
                     for(int k = 0; k < 9; ++k) {
                        this.minecraft.playerController.sendSlotPacket(((CreativeScreen.CreativeContainer)this.container).getSlot(45 + k).getStack(), 36 + k);
                     }
                  } else if (p_184098_1_ != null) {
                     itemstack5 = ((CreativeScreen.CreativeContainer)this.container).getSlot(p_184098_1_.slotNumber).getStack();
                     this.minecraft.playerController.sendSlotPacket(itemstack5, p_184098_1_.slotNumber - ((CreativeScreen.CreativeContainer)this.container).inventorySlots.size() + 9 + 36);
                     int i = 45 + p_184098_3_;
                     if (p_184098_4_ == ClickType.SWAP) {
                        this.minecraft.playerController.sendSlotPacket(itemstack3, i - ((CreativeScreen.CreativeContainer)this.container).inventorySlots.size() + 9 + 36);
                     } else if (p_184098_4_ == ClickType.THROW && !itemstack3.isEmpty()) {
                        itemstack2 = itemstack3.copy();
                        itemstack2.setCount(p_184098_3_ == 0 ? 1 : itemstack2.getMaxStackSize());
                        this.minecraft.player.dropItem(itemstack2, true);
                        this.minecraft.playerController.sendPacketDropItem(itemstack2);
                     }

                     this.minecraft.player.container.detectAndSendChanges();
                  }
               }
            }
         }
      }

   }

   private boolean hasTmpInventory(@Nullable Slot p_208018_1_) {
      return p_208018_1_ != null && p_208018_1_.inventory == TMP_INVENTORY;
   }

   protected void updateActivePotionEffects() {
      int i = this.guiLeft;
      super.updateActivePotionEffects();
      if (this.searchField != null && this.guiLeft != i) {
         this.searchField.setX(this.guiLeft + 82);
      }

   }

   protected void init() {
      if (this.minecraft.playerController.isInCreativeMode()) {
         super.init();
         this.minecraft.keyboardListener.enableRepeatEvents(true);
         int tabCount = ItemGroup.GROUPS.length;
         if (tabCount > 12) {
            this.addButton(new Button(this.guiLeft, this.guiTop - 50, 20, 20, "<", (p_lambda$init$0_0_) -> {
               tabPage = Math.max(tabPage - 1, 0);
            }));
            this.addButton(new Button(this.guiLeft + this.xSize - 20, this.guiTop - 50, 20, 20, ">", (p_lambda$init$1_1_) -> {
               tabPage = Math.min(tabPage + 1, this.maxPages);
            }));
            this.maxPages = (int)Math.ceil((double)(tabCount - 12) / 10.0D);
         }

         this.searchField = new TextFieldWidget(this.font, this.guiLeft + 82, this.guiTop + 6, 80, 9, I18n.format("itemGroup.search"));
         this.searchField.setMaxStringLength(50);
         this.searchField.setEnableBackgroundDrawing(false);
         this.searchField.setVisible(false);
         this.searchField.setTextColor(16777215);
         this.children.add(this.searchField);
         int i = selectedTabIndex;
         selectedTabIndex = -1;
         this.setCurrentCreativeTab(ItemGroup.GROUPS[i]);
         this.minecraft.player.container.removeListener(this.listener);
         this.listener = new CreativeCraftingListener(this.minecraft);
         this.minecraft.player.container.addListener(this.listener);
      } else {
         this.minecraft.displayGuiScreen(new InventoryScreen(this.minecraft.player));
      }

   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.searchField.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.searchField.setText(s);
      if (!this.searchField.getText().isEmpty()) {
         this.updateCreativeSearch();
      }

   }

   public void removed() {
      super.removed();
      if (this.minecraft.player != null && this.minecraft.player.inventory != null) {
         this.minecraft.player.container.removeListener(this.listener);
      }

      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (this.field_195377_F) {
         return false;
      } else if (!ItemGroup.GROUPS[selectedTabIndex].hasSearchBar()) {
         return false;
      } else {
         String s = this.searchField.getText();
         if (this.searchField.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            if (!Objects.equals(s, this.searchField.getText())) {
               this.updateCreativeSearch();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      this.field_195377_F = false;
      if (!ItemGroup.GROUPS[selectedTabIndex].hasSearchBar()) {
         if (this.minecraft.gameSettings.keyBindChat.matchesKey(p_keyPressed_1_, p_keyPressed_2_)) {
            this.field_195377_F = true;
            this.setCurrentCreativeTab(ItemGroup.SEARCH);
            return true;
         } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         }
      } else {
         boolean flag = !this.hasTmpInventory(this.hoveredSlot) || this.hoveredSlot != null && this.hoveredSlot.getHasStack();
         if (flag && this.func_195363_d(p_keyPressed_1_, p_keyPressed_2_)) {
            this.field_195377_F = true;
            return true;
         } else {
            String s = this.searchField.getText();
            if (this.searchField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
               if (!Objects.equals(s, this.searchField.getText())) {
                  this.updateCreativeSearch();
               }

               return true;
            } else {
               return this.searchField.isFocused() && this.searchField.getVisible() && p_keyPressed_1_ != 256 ? true : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
            }
         }
      }
   }

   public boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
      this.field_195377_F = false;
      return super.keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
   }

   private void updateCreativeSearch() {
      ((CreativeScreen.CreativeContainer)this.container).itemList.clear();
      this.tagSearchResults.clear();
      ItemGroup tab = ItemGroup.GROUPS[selectedTabIndex];
      String search;
      Iterator itr;
      if (tab.hasSearchBar() && tab != ItemGroup.SEARCH) {
         tab.fill(((CreativeScreen.CreativeContainer)this.container).itemList);
         if (!this.searchField.getText().isEmpty()) {
            search = this.searchField.getText().toLowerCase(Locale.ROOT);
            itr = ((CreativeScreen.CreativeContainer)this.container).itemList.iterator();

            while(itr.hasNext()) {
               ItemStack stack = (ItemStack)itr.next();
               boolean matches = false;
               Iterator var6 = stack.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).iterator();

               while(var6.hasNext()) {
                  ITextComponent line = (ITextComponent)var6.next();
                  if (TextFormatting.getTextWithoutFormattingCodes(line.getString()).toLowerCase(Locale.ROOT).contains(search)) {
                     matches = true;
                     break;
                  }
               }

               if (!matches) {
                  itr.remove();
               }
            }
         }

         this.currentScroll = 0.0F;
         ((CreativeScreen.CreativeContainer)this.container).scrollTo(0.0F);
      } else {
         search = this.searchField.getText();
         if (search.isEmpty()) {
            itr = Registry.ITEM.iterator();

            while(itr.hasNext()) {
               Item item = (Item)itr.next();
               item.fillItemGroup(ItemGroup.SEARCH, ((CreativeScreen.CreativeContainer)this.container).itemList);
            }
         } else {
            IMutableSearchTree isearchtree;
            if (search.startsWith("#")) {
               search = search.substring(1);
               isearchtree = this.minecraft.func_213253_a(SearchTreeManager.field_215360_b);
               this.searchTags(search);
            } else {
               isearchtree = this.minecraft.func_213253_a(SearchTreeManager.field_215359_a);
            }

            ((CreativeScreen.CreativeContainer)this.container).itemList.addAll(isearchtree.search(search.toLowerCase(Locale.ROOT)));
         }

         this.currentScroll = 0.0F;
         ((CreativeScreen.CreativeContainer)this.container).scrollTo(0.0F);
      }
   }

   private void searchTags(String p_214080_1_) {
      int i = p_214080_1_.indexOf(58);
      Predicate predicate;
      if (i == -1) {
         predicate = (p_lambda$searchTags$2_1_) -> {
            return p_lambda$searchTags$2_1_.getPath().contains(p_214080_1_);
         };
      } else {
         String s = p_214080_1_.substring(0, i).trim();
         String s1 = p_214080_1_.substring(i + 1).trim();
         predicate = (p_lambda$searchTags$3_2_) -> {
            return p_lambda$searchTags$3_2_.getNamespace().contains(s) && p_lambda$searchTags$3_2_.getPath().contains(s1);
         };
      }

      TagCollection<Item> tagcollection = ItemTags.getCollection();
      tagcollection.getRegisteredTags().stream().filter(predicate).forEach((p_lambda$searchTags$4_2_) -> {
         Tag var10000 = (Tag)this.tagSearchResults.put(p_lambda$searchTags$4_2_, tagcollection.get(p_lambda$searchTags$4_2_));
      });
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      ItemGroup itemgroup = ItemGroup.GROUPS[selectedTabIndex];
      if (itemgroup != null && itemgroup.drawInForegroundOfTab()) {
         RenderSystem.disableBlend();
         this.font.drawString(I18n.format(itemgroup.getTranslationKey()), 8.0F, 6.0F, itemgroup.getLabelColor());
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         double d0 = p_mouseClicked_1_ - (double)this.guiLeft;
         double d1 = p_mouseClicked_3_ - (double)this.guiTop;
         ItemGroup[] var10 = ItemGroup.GROUPS;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            ItemGroup itemgroup = var10[var12];
            if (itemgroup != null && this.isMouseOverGroup(itemgroup, d0, d1)) {
               return true;
            }
         }

         if (selectedTabIndex != ItemGroup.INVENTORY.getIndex() && this.func_195376_a(p_mouseClicked_1_, p_mouseClicked_3_)) {
            this.isScrolling = this.needsScrollBars();
            return true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (p_mouseReleased_5_ == 0) {
         double d0 = p_mouseReleased_1_ - (double)this.guiLeft;
         double d1 = p_mouseReleased_3_ - (double)this.guiTop;
         this.isScrolling = false;
         ItemGroup[] var10 = ItemGroup.GROUPS;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            ItemGroup itemgroup = var10[var12];
            if (itemgroup != null && this.isMouseOverGroup(itemgroup, d0, d1)) {
               this.setCurrentCreativeTab(itemgroup);
               return true;
            }
         }
      }

      return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   private boolean needsScrollBars() {
      if (ItemGroup.GROUPS[selectedTabIndex] == null) {
         return false;
      } else {
         return selectedTabIndex != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[selectedTabIndex].hasScrollbar() && ((CreativeScreen.CreativeContainer)this.container).canScroll();
      }
   }

   private void setCurrentCreativeTab(ItemGroup p_147050_1_) {
      if (p_147050_1_ != null) {
         int i = selectedTabIndex;
         selectedTabIndex = p_147050_1_.getIndex();
         this.slotColor = p_147050_1_.getSlotColor();
         this.dragSplittingSlots.clear();
         ((CreativeScreen.CreativeContainer)this.container).itemList.clear();
         int l;
         int j1;
         if (p_147050_1_ == ItemGroup.HOTBAR) {
            CreativeSettings creativesettings = this.minecraft.getCreativeSettings();

            for(l = 0; l < 9; ++l) {
               HotbarSnapshot hotbarsnapshot = creativesettings.getHotbarSnapshot(l);
               if (hotbarsnapshot.isEmpty()) {
                  for(j1 = 0; j1 < 9; ++j1) {
                     if (j1 == l) {
                        ItemStack itemstack = new ItemStack(Items.PAPER);
                        itemstack.getOrCreateChildTag("CustomCreativeLock");
                        String s = this.minecraft.gameSettings.keyBindsHotbar[l].getLocalizedName();
                        String s1 = this.minecraft.gameSettings.keyBindSaveToolbar.getLocalizedName();
                        itemstack.setDisplayName(new TranslationTextComponent("inventory.hotbarInfo", new Object[]{s1, s}));
                        ((CreativeScreen.CreativeContainer)this.container).itemList.add(itemstack);
                     } else {
                        ((CreativeScreen.CreativeContainer)this.container).itemList.add(ItemStack.EMPTY);
                     }
                  }
               } else {
                  ((CreativeScreen.CreativeContainer)this.container).itemList.addAll(hotbarsnapshot);
               }
            }
         } else if (p_147050_1_ != ItemGroup.SEARCH) {
            p_147050_1_.fill(((CreativeScreen.CreativeContainer)this.container).itemList);
         }

         if (p_147050_1_ == ItemGroup.INVENTORY) {
            Container container = this.minecraft.player.container;
            if (this.originalSlots == null) {
               this.originalSlots = ImmutableList.copyOf(((CreativeScreen.CreativeContainer)this.container).inventorySlots);
            }

            ((CreativeScreen.CreativeContainer)this.container).inventorySlots.clear();

            for(l = 0; l < container.inventorySlots.size(); ++l) {
               int i1;
               int k1;
               int i2;
               int k2;
               if (l >= 5 && l < 9) {
                  k1 = l - 5;
                  i2 = k1 / 2;
                  k2 = k1 % 2;
                  i1 = 54 + i2 * 54;
                  j1 = 6 + k2 * 27;
               } else if (l >= 0 && l < 5) {
                  i1 = -2000;
                  j1 = -2000;
               } else if (l == 45) {
                  i1 = 35;
                  j1 = 20;
               } else {
                  k1 = l - 9;
                  i2 = k1 % 9;
                  k2 = k1 / 9;
                  i1 = 9 + i2 * 18;
                  if (l >= 36) {
                     j1 = 112;
                  } else {
                     j1 = 54 + k2 * 18;
                  }
               }

               Slot slot = new CreativeScreen.CreativeSlot((Slot)container.inventorySlots.get(l), l, i1, j1);
               ((CreativeScreen.CreativeContainer)this.container).inventorySlots.add(slot);
            }

            this.destroyItemSlot = new Slot(TMP_INVENTORY, 0, 173, 112);
            ((CreativeScreen.CreativeContainer)this.container).inventorySlots.add(this.destroyItemSlot);
         } else if (i == ItemGroup.INVENTORY.getIndex()) {
            ((CreativeScreen.CreativeContainer)this.container).inventorySlots.clear();
            ((CreativeScreen.CreativeContainer)this.container).inventorySlots.addAll(this.originalSlots);
            this.originalSlots = null;
         }

         if (this.searchField != null) {
            if (p_147050_1_.hasSearchBar()) {
               this.searchField.setVisible(true);
               this.searchField.setCanLoseFocus(false);
               this.searchField.setFocused2(true);
               if (i != p_147050_1_.getIndex()) {
                  this.searchField.setText("");
               }

               this.searchField.setWidth(p_147050_1_.getSearchbarWidth());
               this.searchField.x = this.guiLeft + 171 - this.searchField.getWidth();
               this.updateCreativeSearch();
            } else {
               this.searchField.setVisible(false);
               this.searchField.setCanLoseFocus(true);
               this.searchField.setFocused2(false);
               this.searchField.setText("");
            }
         }

         this.currentScroll = 0.0F;
         ((CreativeScreen.CreativeContainer)this.container).scrollTo(0.0F);
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (!this.needsScrollBars()) {
         return false;
      } else {
         int i = (((CreativeScreen.CreativeContainer)this.container).itemList.size() + 9 - 1) / 9 - 5;
         this.currentScroll = (float)((double)this.currentScroll - p_mouseScrolled_5_ / (double)i);
         this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
         ((CreativeScreen.CreativeContainer)this.container).scrollTo(this.currentScroll);
         return true;
      }
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
      this.field_199506_G = flag && !this.isMouseOverGroup(ItemGroup.GROUPS[selectedTabIndex], p_195361_1_, p_195361_3_);
      return this.field_199506_G;
   }

   protected boolean func_195376_a(double p_195376_1_, double p_195376_3_) {
      int i = this.guiLeft;
      int j = this.guiTop;
      int k = i + 175;
      int l = j + 18;
      int i1 = k + 14;
      int j1 = l + 112;
      return p_195376_1_ >= (double)k && p_195376_3_ >= (double)l && p_195376_1_ < (double)i1 && p_195376_3_ < (double)j1;
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (this.isScrolling) {
         int i = this.guiTop + 18;
         int j = i + 112;
         this.currentScroll = ((float)p_mouseDragged_3_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
         ((CreativeScreen.CreativeContainer)this.container).scrollTo(this.currentScroll);
         return true;
      } else {
         return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      int start = tabPage * 10;
      int end = Math.min(ItemGroup.GROUPS.length, (tabPage + 1) * 10 + 2);
      if (tabPage != 0) {
         start += 2;
      }

      boolean rendered = false;

      for(int x = start; x < end; ++x) {
         ItemGroup itemgroup = ItemGroup.GROUPS[x];
         if (itemgroup != null && this.renderCreativeInventoryHoveringText(itemgroup, p_render_1_, p_render_2_)) {
            rendered = true;
            break;
         }
      }

      if (!rendered && !this.renderCreativeInventoryHoveringText(ItemGroup.SEARCH, p_render_1_, p_render_2_)) {
         this.renderCreativeInventoryHoveringText(ItemGroup.INVENTORY, p_render_1_, p_render_2_);
      }

      if (this.destroyItemSlot != null && selectedTabIndex == ItemGroup.INVENTORY.getIndex() && this.isPointInRegion(this.destroyItemSlot.xPos, this.destroyItemSlot.yPos, 16, 16, (double)p_render_1_, (double)p_render_2_)) {
         this.renderTooltip(I18n.format("inventory.binSlot"), p_render_1_, p_render_2_);
      }

      if (this.maxPages != 0) {
         String page = String.format("%d / %d", tabPage + 1, this.maxPages + 1);
         RenderSystem.disableLighting();
         this.setBlitOffset(300);
         this.itemRenderer.zLevel = 300.0F;
         this.font.drawString(page, (float)(this.guiLeft + this.xSize / 2 - this.font.getStringWidth(page) / 2), (float)(this.guiTop - 44), -1);
         this.setBlitOffset(0);
         this.itemRenderer.zLevel = 0.0F;
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   protected void renderTooltip(ItemStack p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      if (selectedTabIndex == ItemGroup.SEARCH.getIndex()) {
         List<ITextComponent> list = p_renderTooltip_1_.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
         List<String> list1 = Lists.newArrayListWithCapacity(list.size());
         Iterator var6 = list.iterator();

         while(var6.hasNext()) {
            ITextComponent itextcomponent = (ITextComponent)var6.next();
            list1.add(itextcomponent.getFormattedText());
         }

         Item item = p_renderTooltip_1_.getItem();
         ItemGroup itemgroup1 = item.getGroup();
         if (itemgroup1 == null && item == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(p_renderTooltip_1_);
            if (map.size() == 1) {
               Enchantment enchantment = (Enchantment)map.keySet().iterator().next();
               ItemGroup[] var10 = ItemGroup.GROUPS;
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  ItemGroup itemgroup = var10[var12];
                  if (itemgroup.hasRelevantEnchantmentType(enchantment.type)) {
                     itemgroup1 = itemgroup;
                     break;
                  }
               }
            }
         }

         this.tagSearchResults.forEach((p_lambda$renderTooltip$5_2_, p_lambda$renderTooltip$5_3_) -> {
            if (p_lambda$renderTooltip$5_3_.contains(item)) {
               list1.add(1, "" + TextFormatting.BOLD + TextFormatting.DARK_PURPLE + "#" + p_lambda$renderTooltip$5_2_);
            }

         });
         if (itemgroup1 != null) {
            list1.add(1, "" + TextFormatting.BOLD + TextFormatting.BLUE + I18n.format(itemgroup1.getTranslationKey()));
         }

         for(int i = 0; i < list1.size(); ++i) {
            if (i == 0) {
               list1.set(i, p_renderTooltip_1_.getRarity().color + (String)list1.get(i));
            } else {
               list1.set(i, TextFormatting.GRAY + (String)list1.get(i));
            }
         }

         FontRenderer font = p_renderTooltip_1_.getItem().getFontRenderer(p_renderTooltip_1_);
         GuiUtils.preItemToolTip(p_renderTooltip_1_);
         this.renderTooltip(list1, p_renderTooltip_2_, p_renderTooltip_3_, font == null ? this.font : font);
         GuiUtils.postItemToolTip();
      } else {
         super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      ItemGroup itemgroup = ItemGroup.GROUPS[selectedTabIndex];
      int start = tabPage * 10;
      int end = Math.min(ItemGroup.GROUPS.length, (tabPage + 1) * 10 + 2);
      if (tabPage != 0) {
         start += 2;
      }

      int i;
      for(i = start; i < end; ++i) {
         ItemGroup itemgroup1 = ItemGroup.GROUPS[i];
         if (itemgroup1 != null && itemgroup1.getIndex() != selectedTabIndex) {
            this.minecraft.getTextureManager().bindTexture(itemgroup1.getTabsImage());
            this.drawTab(itemgroup1);
         }
      }

      if (tabPage != 0) {
         if (itemgroup != ItemGroup.SEARCH) {
            this.minecraft.getTextureManager().bindTexture(ItemGroup.SEARCH.getTabsImage());
            this.drawTab(ItemGroup.SEARCH);
         }

         if (itemgroup != ItemGroup.INVENTORY) {
            this.minecraft.getTextureManager().bindTexture(ItemGroup.INVENTORY.getTabsImage());
            this.drawTab(ItemGroup.INVENTORY);
         }
      }

      this.minecraft.getTextureManager().bindTexture(itemgroup.getBackgroundImage());
      this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      this.searchField.render(p_146976_2_, p_146976_3_, p_146976_1_);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      i = this.guiLeft + 175;
      int j = this.guiTop + 18;
      int k = j + 112;
      this.minecraft.getTextureManager().bindTexture(itemgroup.getTabsImage());
      if (itemgroup.hasScrollbar()) {
         this.blit(i, j + (int)((float)(k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
      }

      if (itemgroup != null && itemgroup.getTabPage() == tabPage || itemgroup == ItemGroup.SEARCH || itemgroup == ItemGroup.INVENTORY) {
         this.drawTab(itemgroup);
         if (itemgroup == ItemGroup.INVENTORY) {
            InventoryScreen.func_228187_a_(this.guiLeft + 88, this.guiTop + 45, 20, (float)(this.guiLeft + 88 - p_146976_2_), (float)(this.guiTop + 45 - 30 - p_146976_3_), this.minecraft.player);
         }

      }
   }

   protected boolean isMouseOverGroup(ItemGroup p_195375_1_, double p_195375_2_, double p_195375_4_) {
      if (p_195375_1_.getTabPage() != tabPage && p_195375_1_ != ItemGroup.SEARCH && p_195375_1_ != ItemGroup.INVENTORY) {
         return false;
      } else {
         int i = p_195375_1_.getColumn();
         int j = 28 * i;
         int k = 0;
         if (p_195375_1_.isAlignedRight()) {
            j = this.xSize - 28 * (6 - i) + 2;
         } else if (i > 0) {
            j += i;
         }

         int k;
         if (p_195375_1_.isOnTopRow()) {
            k = k - 32;
         } else {
            k = k + this.ySize;
         }

         return p_195375_2_ >= (double)j && p_195375_2_ <= (double)(j + 28) && p_195375_4_ >= (double)k && p_195375_4_ <= (double)(k + 32);
      }
   }

   protected boolean renderCreativeInventoryHoveringText(ItemGroup p_147052_1_, int p_147052_2_, int p_147052_3_) {
      int i = p_147052_1_.getColumn();
      int j = 28 * i;
      int k = 0;
      if (p_147052_1_.isAlignedRight()) {
         j = this.xSize - 28 * (6 - i) + 2;
      } else if (i > 0) {
         j += i;
      }

      int k;
      if (p_147052_1_.isOnTopRow()) {
         k = k - 32;
      } else {
         k = k + this.ySize;
      }

      if (this.isPointInRegion(j + 3, k + 3, 23, 27, (double)p_147052_2_, (double)p_147052_3_)) {
         this.renderTooltip(I18n.format(p_147052_1_.getTranslationKey()), p_147052_2_, p_147052_3_);
         return true;
      } else {
         return false;
      }
   }

   protected void drawTab(ItemGroup p_147051_1_) {
      boolean flag = p_147051_1_.getIndex() == selectedTabIndex;
      boolean flag1 = p_147051_1_.isOnTopRow();
      int i = p_147051_1_.getColumn();
      int j = i * 28;
      int k = 0;
      int l = this.guiLeft + 28 * i;
      int i1 = this.guiTop;
      int j1 = true;
      if (flag) {
         k += 32;
      }

      if (p_147051_1_.isAlignedRight()) {
         l = this.guiLeft + this.xSize - 28 * (6 - i);
      } else if (i > 0) {
         l += i;
      }

      if (flag1) {
         i1 -= 28;
      } else {
         k += 64;
         i1 += this.ySize - 4;
      }

      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      this.blit(l, i1, j, k, 28, 32);
      this.setBlitOffset(100);
      this.itemRenderer.zLevel = 100.0F;
      l += 6;
      i1 = i1 + 8 + (flag1 ? 1 : -1);
      RenderSystem.enableRescaleNormal();
      ItemStack itemstack = p_147051_1_.getIcon();
      this.itemRenderer.renderItemAndEffectIntoGUI(itemstack, l, i1);
      this.itemRenderer.renderItemOverlays(this.font, itemstack, l, i1);
      this.itemRenderer.zLevel = 0.0F;
      this.setBlitOffset(0);
   }

   public int getSelectedTabIndex() {
      return selectedTabIndex;
   }

   public static void handleHotbarSnapshots(Minecraft p_192044_0_, int p_192044_1_, boolean p_192044_2_, boolean p_192044_3_) {
      ClientPlayerEntity clientplayerentity = p_192044_0_.player;
      CreativeSettings creativesettings = p_192044_0_.getCreativeSettings();
      HotbarSnapshot hotbarsnapshot = creativesettings.getHotbarSnapshot(p_192044_1_);
      int j;
      if (p_192044_2_) {
         for(j = 0; j < PlayerInventory.getHotbarSize(); ++j) {
            ItemStack itemstack = ((ItemStack)hotbarsnapshot.get(j)).copy();
            clientplayerentity.inventory.setInventorySlotContents(j, itemstack);
            p_192044_0_.playerController.sendSlotPacket(itemstack, 36 + j);
         }

         clientplayerentity.container.detectAndSendChanges();
      } else if (p_192044_3_) {
         for(j = 0; j < PlayerInventory.getHotbarSize(); ++j) {
            hotbarsnapshot.set(j, clientplayerentity.inventory.getStackInSlot(j).copy());
         }

         String s = p_192044_0_.gameSettings.keyBindsHotbar[p_192044_1_].getLocalizedName();
         String s1 = p_192044_0_.gameSettings.keyBindLoadToolbar.getLocalizedName();
         p_192044_0_.ingameGUI.setOverlayMessage((ITextComponent)(new TranslationTextComponent("inventory.hotbarSaved", new Object[]{s1, s})), false);
         creativesettings.save();
      }

   }

   static {
      selectedTabIndex = ItemGroup.BUILDING_BLOCKS.getIndex();
      tabPage = 0;
   }

   @OnlyIn(Dist.CLIENT)
   static class LockedSlot extends Slot {
      public LockedSlot(IInventory p_i47453_1_, int p_i47453_2_, int p_i47453_3_, int p_i47453_4_) {
         super(p_i47453_1_, p_i47453_2_, p_i47453_3_, p_i47453_4_);
      }

      public boolean canTakeStack(PlayerEntity p_82869_1_) {
         if (super.canTakeStack(p_82869_1_) && this.getHasStack()) {
            return this.getStack().getChildTag("CustomCreativeLock") == null;
         } else {
            return !this.getHasStack();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class CreativeSlot extends Slot {
      private final Slot slot;

      public CreativeSlot(Slot p_i229959_1_, int p_i229959_2_, int p_i229959_3_, int p_i229959_4_) {
         super(p_i229959_1_.inventory, p_i229959_2_, p_i229959_3_, p_i229959_4_);
         this.slot = p_i229959_1_;
      }

      public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
         return this.slot.onTake(p_190901_1_, p_190901_2_);
      }

      public boolean isItemValid(ItemStack p_75214_1_) {
         return this.slot.isItemValid(p_75214_1_);
      }

      public ItemStack getStack() {
         return this.slot.getStack();
      }

      public boolean getHasStack() {
         return this.slot.getHasStack();
      }

      public void putStack(ItemStack p_75215_1_) {
         this.slot.putStack(p_75215_1_);
      }

      public void onSlotChanged() {
         this.slot.onSlotChanged();
      }

      public int getSlotStackLimit() {
         return this.slot.getSlotStackLimit();
      }

      public int getItemStackLimit(ItemStack p_178170_1_) {
         return this.slot.getItemStackLimit(p_178170_1_);
      }

      @Nullable
      public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
         return this.slot.func_225517_c_();
      }

      public ItemStack decrStackSize(int p_75209_1_) {
         return this.slot.decrStackSize(p_75209_1_);
      }

      public boolean isEnabled() {
         return this.slot.isEnabled();
      }

      public boolean canTakeStack(PlayerEntity p_82869_1_) {
         return this.slot.canTakeStack(p_82869_1_);
      }

      public int getSlotIndex() {
         return this.slot.getSlotIndex();
      }

      public boolean isSameInventory(Slot p_isSameInventory_1_) {
         return this.slot.isSameInventory(p_isSameInventory_1_);
      }

      public Slot setBackground(ResourceLocation p_setBackground_1_, ResourceLocation p_setBackground_2_) {
         this.slot.setBackground(p_setBackground_1_, p_setBackground_2_);
         return this;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class CreativeContainer extends Container {
      public final NonNullList<ItemStack> itemList = NonNullList.create();

      public CreativeContainer(PlayerEntity p_i1086_1_) {
         super((ContainerType)null, 0);
         PlayerInventory playerinventory = p_i1086_1_.inventory;

         int k;
         for(k = 0; k < 5; ++k) {
            for(int j = 0; j < 9; ++j) {
               this.addSlot(new CreativeScreen.LockedSlot(CreativeScreen.TMP_INVENTORY, k * 9 + j, 9 + j * 18, 18 + k * 18));
            }
         }

         for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerinventory, k, 9 + k * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      public boolean canInteractWith(PlayerEntity p_75145_1_) {
         return true;
      }

      public void scrollTo(float p_148329_1_) {
         int i = (this.itemList.size() + 9 - 1) / 9 - 5;
         int j = (int)((double)(p_148329_1_ * (float)i) + 0.5D);
         if (j < 0) {
            j = 0;
         }

         for(int k = 0; k < 5; ++k) {
            for(int l = 0; l < 9; ++l) {
               int i1 = l + (k + j) * 9;
               if (i1 >= 0 && i1 < this.itemList.size()) {
                  CreativeScreen.TMP_INVENTORY.setInventorySlotContents(l + k * 9, (ItemStack)this.itemList.get(i1));
               } else {
                  CreativeScreen.TMP_INVENTORY.setInventorySlotContents(l + k * 9, ItemStack.EMPTY);
               }
            }
         }

      }

      public boolean canScroll() {
         return this.itemList.size() > 45;
      }

      public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
         if (p_82846_2_ >= this.inventorySlots.size() - 9 && p_82846_2_ < this.inventorySlots.size()) {
            Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);
            if (slot != null && slot.getHasStack()) {
               slot.putStack(ItemStack.EMPTY);
            }
         }

         return ItemStack.EMPTY;
      }

      public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
         return p_94530_2_.inventory != CreativeScreen.TMP_INVENTORY;
      }

      public boolean canDragIntoSlot(Slot p_94531_1_) {
         return p_94531_1_.inventory != CreativeScreen.TMP_INVENTORY;
      }
   }
}
