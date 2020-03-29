package net.minecraft.client.renderer.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BlockModelDefinition;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Multipart implements IUnbakedModel {
   private final StateContainer<Block, BlockState> stateContainer;
   private final List<Selector> selectors;

   public Multipart(StateContainer<Block, BlockState> p_i49524_1_, List<Selector> p_i49524_2_) {
      this.stateContainer = p_i49524_1_;
      this.selectors = p_i49524_2_;
   }

   public List<Selector> getSelectors() {
      return this.selectors;
   }

   public Set<VariantList> getVariants() {
      Set<VariantList> lvt_1_1_ = Sets.newHashSet();
      Iterator var2 = this.selectors.iterator();

      while(var2.hasNext()) {
         Selector lvt_3_1_ = (Selector)var2.next();
         lvt_1_1_.add(lvt_3_1_.getVariantList());
      }

      return lvt_1_1_;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Multipart)) {
         return false;
      } else {
         Multipart lvt_2_1_ = (Multipart)p_equals_1_;
         return Objects.equals(this.stateContainer, lvt_2_1_.stateContainer) && Objects.equals(this.selectors, lvt_2_1_.selectors);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.stateContainer, this.selectors});
   }

   public Collection<ResourceLocation> getDependencies() {
      return (Collection)this.getSelectors().stream().flatMap((p_209563_0_) -> {
         return p_209563_0_.getVariantList().getDependencies().stream();
      }).collect(Collectors.toSet());
   }

   public Collection<Material> func_225614_a_(Function<ResourceLocation, IUnbakedModel> p_225614_1_, Set<Pair<String, String>> p_225614_2_) {
      return (Collection)this.getSelectors().stream().flatMap((p_228832_2_) -> {
         return p_228832_2_.getVariantList().func_225614_a_(p_225614_1_, p_225614_2_).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public IBakedModel func_225613_a_(ModelBakery p_225613_1_, Function<Material, TextureAtlasSprite> p_225613_2_, IModelTransform p_225613_3_, ResourceLocation p_225613_4_) {
      MultipartBakedModel.Builder lvt_5_1_ = new MultipartBakedModel.Builder();
      Iterator var6 = this.getSelectors().iterator();

      while(var6.hasNext()) {
         Selector lvt_7_1_ = (Selector)var6.next();
         IBakedModel lvt_8_1_ = lvt_7_1_.getVariantList().func_225613_a_(p_225613_1_, p_225613_2_, p_225613_3_, p_225613_4_);
         if (lvt_8_1_ != null) {
            lvt_5_1_.putModel(lvt_7_1_.getPredicate(this.stateContainer), lvt_8_1_);
         }
      }

      return lvt_5_1_.build();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Multipart> {
      private final BlockModelDefinition.ContainerHolder containerHolder;

      public Deserializer(BlockModelDefinition.ContainerHolder p_i49520_1_) {
         this.containerHolder = p_i49520_1_;
      }

      public Multipart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return new Multipart(this.containerHolder.getStateContainer(), this.getSelectors(p_deserialize_3_, p_deserialize_1_.getAsJsonArray()));
      }

      private List<Selector> getSelectors(JsonDeserializationContext p_188133_1_, JsonArray p_188133_2_) {
         List<Selector> lvt_3_1_ = Lists.newArrayList();
         Iterator var4 = p_188133_2_.iterator();

         while(var4.hasNext()) {
            JsonElement lvt_5_1_ = (JsonElement)var4.next();
            lvt_3_1_.add(p_188133_1_.deserialize(lvt_5_1_, Selector.class));
         }

         return lvt_3_1_;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
