package net.minecraftforge.client.event;

import java.util.Set;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public class TextureStitchEvent extends Event {
   private final AtlasTexture map;

   public TextureStitchEvent(AtlasTexture map) {
      this.map = map;
   }

   public AtlasTexture getMap() {
      return this.map;
   }

   public static class Post extends TextureStitchEvent {
      public Post(AtlasTexture map) {
         super(map);
      }
   }

   public static class Pre extends TextureStitchEvent {
      private final Set<ResourceLocation> sprites;

      public Pre(AtlasTexture map, Set<ResourceLocation> sprites) {
         super(map);
         this.sprites = sprites;
      }

      public boolean addSprite(ResourceLocation sprite) {
         return this.sprites.add(sprite);
      }
   }
}
