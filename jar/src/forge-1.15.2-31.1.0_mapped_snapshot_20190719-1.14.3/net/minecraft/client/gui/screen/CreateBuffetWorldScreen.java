package net.minecraft.client.gui.screen;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateBuffetWorldScreen extends Screen {
   private static final List<ResourceLocation> BUFFET_GENERATORS;
   private final CreateWorldScreen parent;
   private final CompoundNBT field_213017_c;
   private CreateBuffetWorldScreen.BiomeList biomeList;
   private int field_205312_t;
   private Button field_205313_u;

   public CreateBuffetWorldScreen(CreateWorldScreen p_i49701_1_, CompoundNBT p_i49701_2_) {
      super(new TranslationTextComponent("createWorld.customize.buffet.title", new Object[0]));
      this.parent = p_i49701_1_;
      this.field_213017_c = p_i49701_2_;
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.addButton(new Button((this.width - 200) / 2, 40, 200, 20, I18n.format("createWorld.customize.buffet.generatortype") + " " + I18n.format(Util.makeTranslationKey("generator", (ResourceLocation)BUFFET_GENERATORS.get(this.field_205312_t))), (p_213015_1_) -> {
         ++this.field_205312_t;
         if (this.field_205312_t >= BUFFET_GENERATORS.size()) {
            this.field_205312_t = 0;
         }

         p_213015_1_.setMessage(I18n.format("createWorld.customize.buffet.generatortype") + " " + I18n.format(Util.makeTranslationKey("generator", (ResourceLocation)BUFFET_GENERATORS.get(this.field_205312_t))));
      }));
      this.biomeList = new CreateBuffetWorldScreen.BiomeList();
      this.children.add(this.biomeList);
      this.field_205313_u = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done"), (p_213014_1_) -> {
         this.parent.chunkProviderSettingsJson = this.serialize();
         this.minecraft.displayGuiScreen(this.parent);
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel"), (p_213012_1_) -> {
         this.minecraft.displayGuiScreen(this.parent);
      }));
      this.deserialize();
      this.func_205306_h();
   }

   private void deserialize() {
      int lvt_2_2_;
      if (this.field_213017_c.contains("chunk_generator", 10) && this.field_213017_c.getCompound("chunk_generator").contains("type", 8)) {
         ResourceLocation lvt_1_1_ = new ResourceLocation(this.field_213017_c.getCompound("chunk_generator").getString("type"));

         for(lvt_2_2_ = 0; lvt_2_2_ < BUFFET_GENERATORS.size(); ++lvt_2_2_) {
            if (((ResourceLocation)BUFFET_GENERATORS.get(lvt_2_2_)).equals(lvt_1_1_)) {
               this.field_205312_t = lvt_2_2_;
               break;
            }
         }
      }

      if (this.field_213017_c.contains("biome_source", 10) && this.field_213017_c.getCompound("biome_source").contains("biomes", 9)) {
         ListNBT lvt_1_2_ = this.field_213017_c.getCompound("biome_source").getList("biomes", 8);

         for(lvt_2_2_ = 0; lvt_2_2_ < lvt_1_2_.size(); ++lvt_2_2_) {
            ResourceLocation lvt_3_1_ = new ResourceLocation(lvt_1_2_.getString(lvt_2_2_));
            this.biomeList.setSelected((CreateBuffetWorldScreen.BiomeList.BiomeEntry)this.biomeList.children().stream().filter((p_213013_1_) -> {
               return Objects.equals(p_213013_1_.field_214394_b, lvt_3_1_);
            }).findFirst().orElse((Object)null));
         }
      }

      this.field_213017_c.remove("chunk_generator");
      this.field_213017_c.remove("biome_source");
   }

   private CompoundNBT serialize() {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      CompoundNBT lvt_2_1_ = new CompoundNBT();
      lvt_2_1_.putString("type", Registry.BIOME_SOURCE_TYPE.getKey(BiomeProviderType.FIXED).toString());
      CompoundNBT lvt_3_1_ = new CompoundNBT();
      ListNBT lvt_4_1_ = new ListNBT();
      lvt_4_1_.add(StringNBT.func_229705_a_(((CreateBuffetWorldScreen.BiomeList.BiomeEntry)this.biomeList.getSelected()).field_214394_b.toString()));
      lvt_3_1_.put("biomes", lvt_4_1_);
      lvt_2_1_.put("options", lvt_3_1_);
      CompoundNBT lvt_5_1_ = new CompoundNBT();
      CompoundNBT lvt_6_1_ = new CompoundNBT();
      lvt_5_1_.putString("type", ((ResourceLocation)BUFFET_GENERATORS.get(this.field_205312_t)).toString());
      lvt_6_1_.putString("default_block", "minecraft:stone");
      lvt_6_1_.putString("default_fluid", "minecraft:water");
      lvt_5_1_.put("options", lvt_6_1_);
      lvt_1_1_.put("biome_source", lvt_2_1_);
      lvt_1_1_.put("chunk_generator", lvt_5_1_);
      return lvt_1_1_;
   }

   public void func_205306_h() {
      this.field_205313_u.active = this.biomeList.getSelected() != null;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderDirtBackground(0);
      this.biomeList.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
      this.drawCenteredString(this.font, I18n.format("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
      this.drawCenteredString(this.font, I18n.format("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   static {
      BUFFET_GENERATORS = (List)Registry.CHUNK_GENERATOR_TYPE.keySet().stream().filter((p_205307_0_) -> {
         return ((ChunkGeneratorType)Registry.CHUNK_GENERATOR_TYPE.getOrDefault(p_205307_0_)).isOptionForBuffetWorld();
      }).collect(Collectors.toList());
   }

   @OnlyIn(Dist.CLIENT)
   class BiomeList extends ExtendedList<CreateBuffetWorldScreen.BiomeList.BiomeEntry> {
      private BiomeList() {
         super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.height, 80, CreateBuffetWorldScreen.this.height - 37, 16);
         Registry.BIOME.keySet().stream().sorted(Comparator.comparing((p_214347_0_) -> {
            return ((Biome)Registry.BIOME.getOrDefault(p_214347_0_)).getDisplayName().getString();
         })).forEach((p_214348_1_) -> {
            this.addEntry(new CreateBuffetWorldScreen.BiomeList.BiomeEntry(p_214348_1_));
         });
      }

      protected boolean isFocused() {
         return CreateBuffetWorldScreen.this.getFocused() == this;
      }

      public void setSelected(@Nullable CreateBuffetWorldScreen.BiomeList.BiomeEntry p_setSelected_1_) {
         super.setSelected(p_setSelected_1_);
         if (p_setSelected_1_ != null) {
            NarratorChatListener.INSTANCE.func_216864_a((new TranslationTextComponent("narrator.select", new Object[]{((Biome)Registry.BIOME.getOrDefault(p_setSelected_1_.field_214394_b)).getDisplayName().getString()})).getString());
         }

      }

      protected void moveSelection(int p_moveSelection_1_) {
         super.moveSelection(p_moveSelection_1_);
         CreateBuffetWorldScreen.this.func_205306_h();
      }

      // $FF: synthetic method
      public void setSelected(@Nullable AbstractList.AbstractListEntry p_setSelected_1_) {
         this.setSelected((CreateBuffetWorldScreen.BiomeList.BiomeEntry)p_setSelected_1_);
      }

      // $FF: synthetic method
      BiomeList(Object p_i48987_2_) {
         this();
      }

      @OnlyIn(Dist.CLIENT)
      class BiomeEntry extends ExtendedList.AbstractListEntry<CreateBuffetWorldScreen.BiomeList.BiomeEntry> {
         private final ResourceLocation field_214394_b;

         public BiomeEntry(ResourceLocation p_i50811_2_) {
            this.field_214394_b = p_i50811_2_;
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            BiomeList.this.drawString(CreateBuffetWorldScreen.this.font, ((Biome)Registry.BIOME.getOrDefault(this.field_214394_b)).getDisplayName().getString(), p_render_3_ + 5, p_render_2_ + 2, 16777215);
         }

         public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (p_mouseClicked_5_ == 0) {
               BiomeList.this.setSelected(this);
               CreateBuffetWorldScreen.this.func_205306_h();
               return true;
            } else {
               return false;
            }
         }
      }
   }
}
