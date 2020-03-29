package net.minecraft.network.play.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SRecipeBookPacket implements IPacket<IClientPlayNetHandler> {
   private SRecipeBookPacket.State state;
   private List<ResourceLocation> recipes;
   private List<ResourceLocation> displayedRecipes;
   private boolean guiOpen;
   private boolean filteringCraftable;
   private boolean field_202494_f;
   private boolean field_202495_g;

   public SRecipeBookPacket() {
   }

   public SRecipeBookPacket(SRecipeBookPacket.State p_i48735_1_, Collection<ResourceLocation> p_i48735_2_, Collection<ResourceLocation> p_i48735_3_, boolean p_i48735_4_, boolean p_i48735_5_, boolean p_i48735_6_, boolean p_i48735_7_) {
      this.state = p_i48735_1_;
      this.recipes = ImmutableList.copyOf(p_i48735_2_);
      this.displayedRecipes = ImmutableList.copyOf(p_i48735_3_);
      this.guiOpen = p_i48735_4_;
      this.filteringCraftable = p_i48735_5_;
      this.field_202494_f = p_i48735_6_;
      this.field_202495_g = p_i48735_7_;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRecipeBook(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.state = (SRecipeBookPacket.State)p_148837_1_.readEnumValue(SRecipeBookPacket.State.class);
      this.guiOpen = p_148837_1_.readBoolean();
      this.filteringCraftable = p_148837_1_.readBoolean();
      this.field_202494_f = p_148837_1_.readBoolean();
      this.field_202495_g = p_148837_1_.readBoolean();
      int lvt_2_1_ = p_148837_1_.readVarInt();
      this.recipes = Lists.newArrayList();

      int lvt_3_2_;
      for(lvt_3_2_ = 0; lvt_3_2_ < lvt_2_1_; ++lvt_3_2_) {
         this.recipes.add(p_148837_1_.readResourceLocation());
      }

      if (this.state == SRecipeBookPacket.State.INIT) {
         lvt_2_1_ = p_148837_1_.readVarInt();
         this.displayedRecipes = Lists.newArrayList();

         for(lvt_3_2_ = 0; lvt_3_2_ < lvt_2_1_; ++lvt_3_2_) {
            this.displayedRecipes.add(p_148837_1_.readResourceLocation());
         }
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.state);
      p_148840_1_.writeBoolean(this.guiOpen);
      p_148840_1_.writeBoolean(this.filteringCraftable);
      p_148840_1_.writeBoolean(this.field_202494_f);
      p_148840_1_.writeBoolean(this.field_202495_g);
      p_148840_1_.writeVarInt(this.recipes.size());
      Iterator var2 = this.recipes.iterator();

      ResourceLocation lvt_3_2_;
      while(var2.hasNext()) {
         lvt_3_2_ = (ResourceLocation)var2.next();
         p_148840_1_.writeResourceLocation(lvt_3_2_);
      }

      if (this.state == SRecipeBookPacket.State.INIT) {
         p_148840_1_.writeVarInt(this.displayedRecipes.size());
         var2 = this.displayedRecipes.iterator();

         while(var2.hasNext()) {
            lvt_3_2_ = (ResourceLocation)var2.next();
            p_148840_1_.writeResourceLocation(lvt_3_2_);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public List<ResourceLocation> getRecipes() {
      return this.recipes;
   }

   @OnlyIn(Dist.CLIENT)
   public List<ResourceLocation> getDisplayedRecipes() {
      return this.displayedRecipes;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isGuiOpen() {
      return this.guiOpen;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFilteringCraftable() {
      return this.filteringCraftable;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFurnaceGuiOpen() {
      return this.field_202494_f;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFurnaceFilteringCraftable() {
      return this.field_202495_g;
   }

   @OnlyIn(Dist.CLIENT)
   public SRecipeBookPacket.State getState() {
      return this.state;
   }

   public static enum State {
      INIT,
      ADD,
      REMOVE;
   }
}
