package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StatsScreen extends Screen implements IProgressMeter {
   protected final Screen parentScreen;
   private StatsScreen.CustomStatsList generalStats;
   private StatsScreen.StatsList itemStats;
   private StatsScreen.MobStatsList mobStats;
   private final StatisticsManager stats;
   @Nullable
   private ExtendedList<?> displaySlot;
   private boolean doesGuiPauseGame = true;

   public StatsScreen(Screen p_i1071_1_, StatisticsManager p_i1071_2_) {
      super(new TranslationTextComponent("gui.stats", new Object[0]));
      this.parentScreen = p_i1071_1_;
      this.stats = p_i1071_2_;
   }

   protected void init() {
      this.doesGuiPauseGame = true;
      this.minecraft.getConnection().sendPacket(new CClientStatusPacket(CClientStatusPacket.State.REQUEST_STATS));
   }

   public void initLists() {
      this.generalStats = new StatsScreen.CustomStatsList(this.minecraft);
      this.itemStats = new StatsScreen.StatsList(this.minecraft);
      this.mobStats = new StatsScreen.MobStatsList(this.minecraft);
   }

   public void initButtons() {
      this.addButton(new Button(this.width / 2 - 120, this.height - 52, 80, 20, I18n.format("stat.generalButton"), (p_213109_1_) -> {
         this.func_213110_a(this.generalStats);
      }));
      Button lvt_1_1_ = (Button)this.addButton(new Button(this.width / 2 - 40, this.height - 52, 80, 20, I18n.format("stat.itemsButton"), (p_213115_1_) -> {
         this.func_213110_a(this.itemStats);
      }));
      Button lvt_2_1_ = (Button)this.addButton(new Button(this.width / 2 + 40, this.height - 52, 80, 20, I18n.format("stat.mobsButton"), (p_213114_1_) -> {
         this.func_213110_a(this.mobStats);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height - 28, 200, 20, I18n.format("gui.done"), (p_213113_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
      if (this.itemStats.children().isEmpty()) {
         lvt_1_1_.active = false;
      }

      if (this.mobStats.children().isEmpty()) {
         lvt_2_1_.active = false;
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.doesGuiPauseGame) {
         this.renderBackground();
         this.drawCenteredString(this.font, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
         FontRenderer var10001 = this.font;
         String var10002 = LOADING_STRINGS[(int)(Util.milliTime() / 150L % (long)LOADING_STRINGS.length)];
         int var10003 = this.width / 2;
         int var10004 = this.height / 2;
         this.font.getClass();
         this.drawCenteredString(var10001, var10002, var10003, var10004 + 9 * 2, 16777215);
      } else {
         this.func_213116_d().render(p_render_1_, p_render_2_, p_render_3_);
         this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

   }

   public void onStatsUpdated() {
      if (this.doesGuiPauseGame) {
         this.initLists();
         this.initButtons();
         this.func_213110_a(this.generalStats);
         this.doesGuiPauseGame = false;
      }

   }

   public boolean isPauseScreen() {
      return !this.doesGuiPauseGame;
   }

   @Nullable
   public ExtendedList<?> func_213116_d() {
      return this.displaySlot;
   }

   public void func_213110_a(@Nullable ExtendedList<?> p_213110_1_) {
      this.children.remove(this.generalStats);
      this.children.remove(this.itemStats);
      this.children.remove(this.mobStats);
      if (p_213110_1_ != null) {
         this.children.add(0, p_213110_1_);
         this.displaySlot = p_213110_1_;
      }

   }

   private int func_195224_b(int p_195224_1_) {
      return 115 + 40 * p_195224_1_;
   }

   private void drawStatsScreen(int p_146521_1_, int p_146521_2_, Item p_146521_3_) {
      this.drawSprite(p_146521_1_ + 1, p_146521_2_ + 1, 0, 0);
      RenderSystem.enableRescaleNormal();
      this.itemRenderer.renderItemIntoGUI(p_146521_3_.getDefaultInstance(), p_146521_1_ + 2, p_146521_2_ + 2);
      RenderSystem.disableRescaleNormal();
   }

   private void drawSprite(int p_146527_1_, int p_146527_2_, int p_146527_3_, int p_146527_4_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(STATS_ICON_LOCATION);
      blit(p_146527_1_, p_146527_2_, this.getBlitOffset(), (float)p_146527_3_, (float)p_146527_4_, 18, 18, 128, 128);
   }

   @OnlyIn(Dist.CLIENT)
   class MobStatsList extends ExtendedList<StatsScreen.MobStatsList.Entry> {
      public MobStatsList(Minecraft p_i47551_2_) {
         int var10002 = StatsScreen.this.width;
         int var10003 = StatsScreen.this.height;
         int var10005 = StatsScreen.this.height - 64;
         StatsScreen.this.font.getClass();
         super(p_i47551_2_, var10002, var10003, 32, var10005, 9 * 4);
         Iterator var3 = Registry.ENTITY_TYPE.iterator();

         while(true) {
            EntityType lvt_4_1_;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               lvt_4_1_ = (EntityType)var3.next();
            } while(StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(lvt_4_1_)) <= 0 && StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(lvt_4_1_)) <= 0);

            this.addEntry(new StatsScreen.MobStatsList.Entry(lvt_4_1_));
         }
      }

      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ExtendedList.AbstractListEntry<StatsScreen.MobStatsList.Entry> {
         private final EntityType<?> field_214411_b;

         public Entry(EntityType<?> p_i50018_2_) {
            this.field_214411_b = p_i50018_2_;
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            String lvt_10_1_ = I18n.format(Util.makeTranslationKey("entity", EntityType.getKey(this.field_214411_b)));
            int lvt_11_1_ = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(this.field_214411_b));
            int lvt_12_1_ = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(this.field_214411_b));
            MobStatsList.this.drawString(StatsScreen.this.font, lvt_10_1_, p_render_3_ + 2, p_render_2_ + 1, 16777215);
            StatsScreen.MobStatsList var10000 = MobStatsList.this;
            FontRenderer var10001 = StatsScreen.this.font;
            String var10002 = this.func_214409_a(lvt_10_1_, lvt_11_1_);
            int var10003 = p_render_3_ + 2 + 10;
            int var10004 = p_render_2_ + 1;
            StatsScreen.this.font.getClass();
            var10000.drawString(var10001, var10002, var10003, var10004 + 9, lvt_11_1_ == 0 ? 6316128 : 9474192);
            var10000 = MobStatsList.this;
            var10001 = StatsScreen.this.font;
            var10002 = this.func_214408_b(lvt_10_1_, lvt_12_1_);
            var10003 = p_render_3_ + 2 + 10;
            var10004 = p_render_2_ + 1;
            StatsScreen.this.font.getClass();
            var10000.drawString(var10001, var10002, var10003, var10004 + 9 * 2, lvt_12_1_ == 0 ? 6316128 : 9474192);
         }

         private String func_214409_a(String p_214409_1_, int p_214409_2_) {
            String lvt_3_1_ = Stats.ENTITY_KILLED.getTranslationKey();
            return p_214409_2_ == 0 ? I18n.format(lvt_3_1_ + ".none", p_214409_1_) : I18n.format(lvt_3_1_, p_214409_2_, p_214409_1_);
         }

         private String func_214408_b(String p_214408_1_, int p_214408_2_) {
            String lvt_3_1_ = Stats.ENTITY_KILLED_BY.getTranslationKey();
            return p_214408_2_ == 0 ? I18n.format(lvt_3_1_ + ".none", p_214408_1_) : I18n.format(lvt_3_1_, p_214408_1_, p_214408_2_);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class StatsList extends ExtendedList<StatsScreen.StatsList.Entry> {
      protected final List<StatType<Block>> field_195113_v = Lists.newArrayList();
      protected final List<StatType<Item>> field_195114_w;
      private final int[] field_195112_D = new int[]{3, 4, 1, 2, 5, 6};
      protected int field_195115_x = -1;
      protected final List<Item> field_195116_y;
      protected final java.util.Comparator<Item> field_195117_z = new StatsScreen.StatsList.Comparator();
      @Nullable
      protected StatType<?> field_195110_A;
      protected int field_195111_B;

      public StatsList(Minecraft p_i47552_2_) {
         super(p_i47552_2_, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
         this.field_195113_v.add(Stats.BLOCK_MINED);
         this.field_195114_w = Lists.newArrayList(new StatType[]{Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED});
         this.setRenderHeader(true, 20);
         Set<Item> lvt_3_1_ = Sets.newIdentityHashSet();
         Iterator var4 = Registry.ITEM.iterator();

         boolean lvt_6_2_;
         Iterator var7;
         StatType lvt_8_2_;
         while(var4.hasNext()) {
            Item lvt_5_1_ = (Item)var4.next();
            lvt_6_2_ = false;
            var7 = this.field_195114_w.iterator();

            while(var7.hasNext()) {
               lvt_8_2_ = (StatType)var7.next();
               if (lvt_8_2_.contains(lvt_5_1_) && StatsScreen.this.stats.getValue(lvt_8_2_.get(lvt_5_1_)) > 0) {
                  lvt_6_2_ = true;
               }
            }

            if (lvt_6_2_) {
               lvt_3_1_.add(lvt_5_1_);
            }
         }

         var4 = Registry.BLOCK.iterator();

         while(var4.hasNext()) {
            Block lvt_5_2_ = (Block)var4.next();
            lvt_6_2_ = false;
            var7 = this.field_195113_v.iterator();

            while(var7.hasNext()) {
               lvt_8_2_ = (StatType)var7.next();
               if (lvt_8_2_.contains(lvt_5_2_) && StatsScreen.this.stats.getValue(lvt_8_2_.get(lvt_5_2_)) > 0) {
                  lvt_6_2_ = true;
               }
            }

            if (lvt_6_2_) {
               lvt_3_1_.add(lvt_5_2_.asItem());
            }
         }

         lvt_3_1_.remove(Items.AIR);
         this.field_195116_y = Lists.newArrayList(lvt_3_1_);

         for(int lvt_4_1_ = 0; lvt_4_1_ < this.field_195116_y.size(); ++lvt_4_1_) {
            this.addEntry(new StatsScreen.StatsList.Entry());
         }

      }

      protected void renderHeader(int p_renderHeader_1_, int p_renderHeader_2_, Tessellator p_renderHeader_3_) {
         if (!this.minecraft.mouseHelper.isLeftDown()) {
            this.field_195115_x = -1;
         }

         int lvt_4_3_;
         for(lvt_4_3_ = 0; lvt_4_3_ < this.field_195112_D.length; ++lvt_4_3_) {
            StatsScreen.this.drawSprite(p_renderHeader_1_ + StatsScreen.this.func_195224_b(lvt_4_3_) - 18, p_renderHeader_2_ + 1, 0, this.field_195115_x == lvt_4_3_ ? 0 : 18);
         }

         int lvt_5_2_;
         if (this.field_195110_A != null) {
            lvt_4_3_ = StatsScreen.this.func_195224_b(this.func_195105_b(this.field_195110_A)) - 36;
            lvt_5_2_ = this.field_195111_B == 1 ? 2 : 1;
            StatsScreen.this.drawSprite(p_renderHeader_1_ + lvt_4_3_, p_renderHeader_2_ + 1, 18 * lvt_5_2_, 0);
         }

         for(lvt_4_3_ = 0; lvt_4_3_ < this.field_195112_D.length; ++lvt_4_3_) {
            lvt_5_2_ = this.field_195115_x == lvt_4_3_ ? 1 : 0;
            StatsScreen.this.drawSprite(p_renderHeader_1_ + StatsScreen.this.func_195224_b(lvt_4_3_) - 18 + lvt_5_2_, p_renderHeader_2_ + 1 + lvt_5_2_, 18 * this.field_195112_D[lvt_4_3_], 18);
         }

      }

      public int getRowWidth() {
         return 375;
      }

      protected int getScrollbarPosition() {
         return this.width / 2 + 140;
      }

      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      protected void clickedHeader(int p_clickedHeader_1_, int p_clickedHeader_2_) {
         this.field_195115_x = -1;

         for(int lvt_3_1_ = 0; lvt_3_1_ < this.field_195112_D.length; ++lvt_3_1_) {
            int lvt_4_1_ = p_clickedHeader_1_ - StatsScreen.this.func_195224_b(lvt_3_1_);
            if (lvt_4_1_ >= -36 && lvt_4_1_ <= 0) {
               this.field_195115_x = lvt_3_1_;
               break;
            }
         }

         if (this.field_195115_x >= 0) {
            this.func_195107_a(this.func_195108_d(this.field_195115_x));
            this.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

      }

      private StatType<?> func_195108_d(int p_195108_1_) {
         return p_195108_1_ < this.field_195113_v.size() ? (StatType)this.field_195113_v.get(p_195108_1_) : (StatType)this.field_195114_w.get(p_195108_1_ - this.field_195113_v.size());
      }

      private int func_195105_b(StatType<?> p_195105_1_) {
         int lvt_2_1_ = this.field_195113_v.indexOf(p_195105_1_);
         if (lvt_2_1_ >= 0) {
            return lvt_2_1_;
         } else {
            int lvt_3_1_ = this.field_195114_w.indexOf(p_195105_1_);
            return lvt_3_1_ >= 0 ? lvt_3_1_ + this.field_195113_v.size() : -1;
         }
      }

      protected void renderDecorations(int p_renderDecorations_1_, int p_renderDecorations_2_) {
         if (p_renderDecorations_2_ >= this.y0 && p_renderDecorations_2_ <= this.y1) {
            StatsScreen.StatsList.Entry lvt_3_1_ = (StatsScreen.StatsList.Entry)this.getEntryAtPosition((double)p_renderDecorations_1_, (double)p_renderDecorations_2_);
            int lvt_4_1_ = (this.width - this.getRowWidth()) / 2;
            if (lvt_3_1_ != null) {
               if (p_renderDecorations_1_ < lvt_4_1_ + 40 || p_renderDecorations_1_ > lvt_4_1_ + 40 + 20) {
                  return;
               }

               Item lvt_5_1_ = (Item)this.field_195116_y.get(this.children().indexOf(lvt_3_1_));
               this.func_200207_a(this.func_200208_a(lvt_5_1_), p_renderDecorations_1_, p_renderDecorations_2_);
            } else {
               ITextComponent lvt_5_2_ = null;
               int lvt_6_1_ = p_renderDecorations_1_ - lvt_4_1_;

               for(int lvt_7_1_ = 0; lvt_7_1_ < this.field_195112_D.length; ++lvt_7_1_) {
                  int lvt_8_1_ = StatsScreen.this.func_195224_b(lvt_7_1_);
                  if (lvt_6_1_ >= lvt_8_1_ - 18 && lvt_6_1_ <= lvt_8_1_) {
                     lvt_5_2_ = new TranslationTextComponent(this.func_195108_d(lvt_7_1_).getTranslationKey(), new Object[0]);
                     break;
                  }
               }

               this.func_200207_a(lvt_5_2_, p_renderDecorations_1_, p_renderDecorations_2_);
            }

         }
      }

      protected void func_200207_a(@Nullable ITextComponent p_200207_1_, int p_200207_2_, int p_200207_3_) {
         if (p_200207_1_ != null) {
            String lvt_4_1_ = p_200207_1_.getFormattedText();
            int lvt_5_1_ = p_200207_2_ + 12;
            int lvt_6_1_ = p_200207_3_ - 12;
            int lvt_7_1_ = StatsScreen.this.font.getStringWidth(lvt_4_1_);
            this.fillGradient(lvt_5_1_ - 3, lvt_6_1_ - 3, lvt_5_1_ + lvt_7_1_ + 3, lvt_6_1_ + 8 + 3, -1073741824, -1073741824);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0F, 0.0F, 400.0F);
            StatsScreen.this.font.drawStringWithShadow(lvt_4_1_, (float)lvt_5_1_, (float)lvt_6_1_, -1);
            RenderSystem.popMatrix();
         }
      }

      protected ITextComponent func_200208_a(Item p_200208_1_) {
         return p_200208_1_.getName();
      }

      protected void func_195107_a(StatType<?> p_195107_1_) {
         if (p_195107_1_ != this.field_195110_A) {
            this.field_195110_A = p_195107_1_;
            this.field_195111_B = -1;
         } else if (this.field_195111_B == -1) {
            this.field_195111_B = 1;
         } else {
            this.field_195110_A = null;
            this.field_195111_B = 0;
         }

         this.field_195116_y.sort(this.field_195117_z);
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ExtendedList.AbstractListEntry<StatsScreen.StatsList.Entry> {
         private Entry() {
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            Item lvt_10_1_ = (Item)StatsScreen.this.itemStats.field_195116_y.get(p_render_1_);
            StatsScreen.this.drawStatsScreen(p_render_3_ + 40, p_render_2_, lvt_10_1_);

            int lvt_11_1_;
            for(lvt_11_1_ = 0; lvt_11_1_ < StatsScreen.this.itemStats.field_195113_v.size(); ++lvt_11_1_) {
               Stat lvt_12_2_;
               if (lvt_10_1_ instanceof BlockItem) {
                  lvt_12_2_ = ((StatType)StatsScreen.this.itemStats.field_195113_v.get(lvt_11_1_)).get(((BlockItem)lvt_10_1_).getBlock());
               } else {
                  lvt_12_2_ = null;
               }

               this.func_214406_a(lvt_12_2_, p_render_3_ + StatsScreen.this.func_195224_b(lvt_11_1_), p_render_2_, p_render_1_ % 2 == 0);
            }

            for(lvt_11_1_ = 0; lvt_11_1_ < StatsScreen.this.itemStats.field_195114_w.size(); ++lvt_11_1_) {
               this.func_214406_a(((StatType)StatsScreen.this.itemStats.field_195114_w.get(lvt_11_1_)).get(lvt_10_1_), p_render_3_ + StatsScreen.this.func_195224_b(lvt_11_1_ + StatsScreen.this.itemStats.field_195113_v.size()), p_render_2_, p_render_1_ % 2 == 0);
            }

         }

         protected void func_214406_a(@Nullable Stat<?> p_214406_1_, int p_214406_2_, int p_214406_3_, boolean p_214406_4_) {
            String lvt_5_1_ = p_214406_1_ == null ? "-" : p_214406_1_.format(StatsScreen.this.stats.getValue(p_214406_1_));
            StatsList.this.drawString(StatsScreen.this.font, lvt_5_1_, p_214406_2_ - StatsScreen.this.font.getStringWidth(lvt_5_1_), p_214406_3_ + 5, p_214406_4_ ? 16777215 : 9474192);
         }

         // $FF: synthetic method
         Entry(Object p_i50410_2_) {
            this();
         }
      }

      @OnlyIn(Dist.CLIENT)
      class Comparator implements java.util.Comparator<Item> {
         private Comparator() {
         }

         public int compare(Item p_compare_1_, Item p_compare_2_) {
            int lvt_3_2_;
            int lvt_4_3_;
            if (StatsList.this.field_195110_A == null) {
               lvt_3_2_ = 0;
               lvt_4_3_ = 0;
            } else {
               StatType lvt_5_1_;
               if (StatsList.this.field_195113_v.contains(StatsList.this.field_195110_A)) {
                  lvt_5_1_ = StatsList.this.field_195110_A;
                  lvt_3_2_ = p_compare_1_ instanceof BlockItem ? StatsScreen.this.stats.getValue(lvt_5_1_, ((BlockItem)p_compare_1_).getBlock()) : -1;
                  lvt_4_3_ = p_compare_2_ instanceof BlockItem ? StatsScreen.this.stats.getValue(lvt_5_1_, ((BlockItem)p_compare_2_).getBlock()) : -1;
               } else {
                  lvt_5_1_ = StatsList.this.field_195110_A;
                  lvt_3_2_ = StatsScreen.this.stats.getValue(lvt_5_1_, p_compare_1_);
                  lvt_4_3_ = StatsScreen.this.stats.getValue(lvt_5_1_, p_compare_2_);
               }
            }

            return lvt_3_2_ == lvt_4_3_ ? StatsList.this.field_195111_B * Integer.compare(Item.getIdFromItem(p_compare_1_), Item.getIdFromItem(p_compare_2_)) : StatsList.this.field_195111_B * Integer.compare(lvt_3_2_, lvt_4_3_);
         }

         // $FF: synthetic method
         public int compare(Object p_compare_1_, Object p_compare_2_) {
            return this.compare((Item)p_compare_1_, (Item)p_compare_2_);
         }

         // $FF: synthetic method
         Comparator(Object p_i48070_2_) {
            this();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class CustomStatsList extends ExtendedList<StatsScreen.CustomStatsList.Entry> {
      public CustomStatsList(Minecraft p_i47553_2_) {
         super(p_i47553_2_, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
         Iterator var3 = Stats.CUSTOM.iterator();

         while(var3.hasNext()) {
            Stat<ResourceLocation> lvt_4_1_ = (Stat)var3.next();
            this.addEntry(new StatsScreen.CustomStatsList.Entry(lvt_4_1_));
         }

      }

      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ExtendedList.AbstractListEntry<StatsScreen.CustomStatsList.Entry> {
         private final Stat<ResourceLocation> field_214405_b;

         private Entry(Stat<ResourceLocation> p_i50466_2_) {
            this.field_214405_b = p_i50466_2_;
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            ITextComponent lvt_10_1_ = (new TranslationTextComponent("stat." + ((ResourceLocation)this.field_214405_b.getValue()).toString().replace(':', '.'), new Object[0])).applyTextStyle(TextFormatting.GRAY);
            CustomStatsList.this.drawString(StatsScreen.this.font, lvt_10_1_.getString(), p_render_3_ + 2, p_render_2_ + 1, p_render_1_ % 2 == 0 ? 16777215 : 9474192);
            String lvt_11_1_ = this.field_214405_b.format(StatsScreen.this.stats.getValue(this.field_214405_b));
            CustomStatsList.this.drawString(StatsScreen.this.font, lvt_11_1_, p_render_3_ + 2 + 213 - StatsScreen.this.font.getStringWidth(lvt_11_1_), p_render_2_ + 1, p_render_1_ % 2 == 0 ? 16777215 : 9474192);
         }

         // $FF: synthetic method
         Entry(Stat p_i50467_2_, Object p_i50467_3_) {
            this(p_i50467_2_);
         }
      }
   }
}
