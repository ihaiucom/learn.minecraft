package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TexturesParticle {
   @Nullable
   private final List<ResourceLocation> textures;

   private TexturesParticle(@Nullable List<ResourceLocation> p_i51017_1_) {
      this.textures = p_i51017_1_;
   }

   @Nullable
   public List<ResourceLocation> getTextures() {
      return this.textures;
   }

   public static TexturesParticle deserialize(JsonObject p_217595_0_) {
      JsonArray lvt_1_1_ = JSONUtils.getJsonArray(p_217595_0_, "textures", (JsonArray)null);
      List lvt_2_2_;
      if (lvt_1_1_ != null) {
         lvt_2_2_ = (List)Streams.stream(lvt_1_1_).map((p_217597_0_) -> {
            return JSONUtils.getString(p_217597_0_, "texture");
         }).map(ResourceLocation::new).collect(ImmutableList.toImmutableList());
      } else {
         lvt_2_2_ = null;
      }

      return new TexturesParticle(lvt_2_2_);
   }
}
