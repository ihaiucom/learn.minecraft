package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateRecipesPacket implements IPacket<IClientPlayNetHandler> {
   private List<IRecipe<?>> recipes;

   public SUpdateRecipesPacket() {
   }

   public SUpdateRecipesPacket(Collection<IRecipe<?>> p_i48176_1_) {
      this.recipes = Lists.newArrayList(p_i48176_1_);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUpdateRecipes(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.recipes = Lists.newArrayList();
      int lvt_2_1_ = p_148837_1_.readVarInt();

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
         this.recipes.add(func_218772_c(p_148837_1_));
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.recipes.size());
      Iterator var2 = this.recipes.iterator();

      while(var2.hasNext()) {
         IRecipe<?> lvt_3_1_ = (IRecipe)var2.next();
         func_218771_a(lvt_3_1_, p_148840_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public List<IRecipe<?>> getRecipes() {
      return this.recipes;
   }

   public static IRecipe<?> func_218772_c(PacketBuffer p_218772_0_) {
      ResourceLocation lvt_1_1_ = p_218772_0_.readResourceLocation();
      ResourceLocation lvt_2_1_ = p_218772_0_.readResourceLocation();
      return ((IRecipeSerializer)Registry.RECIPE_SERIALIZER.getValue(lvt_1_1_).orElseThrow(() -> {
         return new IllegalArgumentException("Unknown recipe serializer " + lvt_1_1_);
      })).read(lvt_2_1_, p_218772_0_);
   }

   public static <T extends IRecipe<?>> void func_218771_a(T p_218771_0_, PacketBuffer p_218771_1_) {
      p_218771_1_.writeResourceLocation(Registry.RECIPE_SERIALIZER.getKey(p_218771_0_.getSerializer()));
      p_218771_1_.writeResourceLocation(p_218771_0_.getId());
      p_218771_0_.getSerializer().write(p_218771_1_, p_218771_0_);
   }
}
