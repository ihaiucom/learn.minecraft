package net.minecraft.client.renderer.texture;

import java.util.stream.Stream;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaintingSpriteUploader extends SpriteUploader {
   private static final ResourceLocation field_215287_a = new ResourceLocation("back");

   public PaintingSpriteUploader(TextureManager p_i50907_1_) {
      super(p_i50907_1_, new ResourceLocation("textures/atlas/paintings.png"), "painting");
   }

   protected Stream<ResourceLocation> func_225640_a_() {
      return Stream.concat(Registry.MOTIVE.keySet().stream(), Stream.of(field_215287_a));
   }

   public TextureAtlasSprite getSpriteForPainting(PaintingType p_215285_1_) {
      return this.getSprite(Registry.MOTIVE.getKey(p_215285_1_));
   }

   public TextureAtlasSprite func_215286_b() {
      return this.getSprite(field_215287_a);
   }
}
