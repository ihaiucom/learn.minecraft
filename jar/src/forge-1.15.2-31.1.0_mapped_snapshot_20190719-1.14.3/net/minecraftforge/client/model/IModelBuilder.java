package net.minecraftforge.client.model;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

public interface IModelBuilder<T extends IModelBuilder<T>> {
   static IModelBuilder<?> of(IModelConfiguration owner, ItemOverrideList overrides, TextureAtlasSprite particle) {
      return new IModelBuilder.Simple((new SimpleBakedModel.Builder(owner, overrides)).setTexture(particle));
   }

   T addFaceQuad(Direction var1, BakedQuad var2);

   T addGeneralQuad(BakedQuad var1);

   IBakedModel build();

   public static class Simple implements IModelBuilder<IModelBuilder.Simple> {
      final SimpleBakedModel.Builder builder;

      Simple(SimpleBakedModel.Builder builder) {
         this.builder = builder;
      }

      public IModelBuilder.Simple addFaceQuad(Direction facing, BakedQuad quad) {
         this.builder.addFaceQuad(facing, quad);
         return this;
      }

      public IModelBuilder.Simple addGeneralQuad(BakedQuad quad) {
         this.builder.addGeneralQuad(quad);
         return this;
      }

      public IBakedModel build() {
         return this.builder.build();
      }
   }
}
