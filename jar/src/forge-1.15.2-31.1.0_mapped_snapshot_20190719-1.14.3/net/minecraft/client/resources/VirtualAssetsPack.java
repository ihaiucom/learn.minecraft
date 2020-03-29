package net.minecraft.client.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VirtualAssetsPack extends VanillaPack {
   private final ResourceIndex field_195785_b;

   public VirtualAssetsPack(ResourceIndex p_i48115_1_) {
      super("minecraft", "realms");
      this.field_195785_b = p_i48115_1_;
   }

   @Nullable
   protected InputStream getInputStreamVanilla(ResourcePackType p_195782_1_, ResourceLocation p_195782_2_) {
      if (p_195782_1_ == ResourcePackType.CLIENT_RESOURCES) {
         File lvt_3_1_ = this.field_195785_b.getFile(p_195782_2_);
         if (lvt_3_1_ != null && lvt_3_1_.exists()) {
            try {
               return new FileInputStream(lvt_3_1_);
            } catch (FileNotFoundException var5) {
            }
         }
      }

      return super.getInputStreamVanilla(p_195782_1_, p_195782_2_);
   }

   public boolean resourceExists(ResourcePackType p_195764_1_, ResourceLocation p_195764_2_) {
      if (p_195764_1_ == ResourcePackType.CLIENT_RESOURCES) {
         File lvt_3_1_ = this.field_195785_b.getFile(p_195764_2_);
         if (lvt_3_1_ != null && lvt_3_1_.exists()) {
            return true;
         }
      }

      return super.resourceExists(p_195764_1_, p_195764_2_);
   }

   @Nullable
   protected InputStream getInputStreamVanilla(String p_200010_1_) {
      File lvt_2_1_ = this.field_195785_b.func_225638_a_(p_200010_1_);
      if (lvt_2_1_ != null && lvt_2_1_.exists()) {
         try {
            return new FileInputStream(lvt_2_1_);
         } catch (FileNotFoundException var4) {
         }
      }

      return super.getInputStreamVanilla(p_200010_1_);
   }

   public Collection<ResourceLocation> func_225637_a_(ResourcePackType p_225637_1_, String p_225637_2_, String p_225637_3_, int p_225637_4_, Predicate<String> p_225637_5_) {
      Collection<ResourceLocation> lvt_6_1_ = super.func_225637_a_(p_225637_1_, p_225637_2_, p_225637_3_, p_225637_4_, p_225637_5_);
      lvt_6_1_.addAll(this.field_195785_b.func_225639_a_(p_225637_3_, p_225637_2_, p_225637_4_, p_225637_5_));
      return lvt_6_1_;
   }
}
