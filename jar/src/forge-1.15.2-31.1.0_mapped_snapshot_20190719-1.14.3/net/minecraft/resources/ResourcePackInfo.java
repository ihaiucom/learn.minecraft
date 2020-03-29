package net.minecraft.resources;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackInfo implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final PackMetadataSection field_212500_b;
   private final String name;
   private final Supplier<IResourcePack> resourcePackSupplier;
   private final ITextComponent field_195802_d;
   private final ITextComponent description;
   private final PackCompatibility compatibility;
   private final ResourcePackInfo.Priority priority;
   private final boolean alwaysEnabled;
   private final boolean orderLocked;
   private final boolean hidden;

   @Nullable
   public static <T extends ResourcePackInfo> T createResourcePack(String p_195793_0_, boolean p_195793_1_, Supplier<IResourcePack> p_195793_2_, ResourcePackInfo.IFactory<T> p_195793_3_, ResourcePackInfo.Priority p_195793_4_) {
      try {
         IResourcePack iresourcepack = (IResourcePack)p_195793_2_.get();
         Throwable var6 = null;

         ResourcePackInfo var9;
         try {
            PackMetadataSection packmetadatasection = (PackMetadataSection)iresourcepack.getMetadata(PackMetadataSection.SERIALIZER);
            if (p_195793_1_ && packmetadatasection == null) {
               LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
               packmetadatasection = field_212500_b;
            }

            if (packmetadatasection == null) {
               LOGGER.warn("Couldn't find pack meta for pack {}", p_195793_0_);
               return (ResourcePackInfo)null;
            }

            ResourcePackInfo resourcepackinfo = p_195793_3_.create(p_195793_0_, p_195793_1_, p_195793_2_, iresourcepack, packmetadatasection, p_195793_4_);
            var9 = resourcepackinfo;
         } catch (Throwable var20) {
            var6 = var20;
            throw var20;
         } finally {
            if (iresourcepack != null) {
               if (var6 != null) {
                  try {
                     iresourcepack.close();
                  } catch (Throwable var19) {
                     var6.addSuppressed(var19);
                  }
               } else {
                  iresourcepack.close();
               }
            }

         }

         return var9;
      } catch (IOException var22) {
         LOGGER.warn("Couldn't get pack info for: {}", var22.toString());
         return (ResourcePackInfo)null;
      }
   }

   /** @deprecated */
   @Deprecated
   public ResourcePackInfo(String p_i47907_1_, boolean p_i47907_2_, Supplier<IResourcePack> p_i47907_3_, ITextComponent p_i47907_4_, ITextComponent p_i47907_5_, PackCompatibility p_i47907_6_, ResourcePackInfo.Priority p_i47907_7_, boolean p_i47907_8_) {
      this(p_i47907_1_, p_i47907_2_, p_i47907_3_, p_i47907_4_, p_i47907_5_, p_i47907_6_, p_i47907_7_, p_i47907_8_, false);
   }

   public ResourcePackInfo(String p_i230083_1_, boolean p_i230083_2_, Supplier<IResourcePack> p_i230083_3_, ITextComponent p_i230083_4_, ITextComponent p_i230083_5_, PackCompatibility p_i230083_6_, ResourcePackInfo.Priority p_i230083_7_, boolean p_i230083_8_, boolean p_i230083_9_) {
      this.name = p_i230083_1_;
      this.resourcePackSupplier = p_i230083_3_;
      this.field_195802_d = p_i230083_4_;
      this.description = p_i230083_5_;
      this.compatibility = p_i230083_6_;
      this.alwaysEnabled = p_i230083_2_;
      this.priority = p_i230083_7_;
      this.orderLocked = p_i230083_8_;
      this.hidden = p_i230083_9_;
   }

   /** @deprecated */
   @Deprecated
   public ResourcePackInfo(String p_i47908_1_, boolean p_i47908_2_, Supplier<IResourcePack> p_i47908_3_, IResourcePack p_i47908_4_, PackMetadataSection p_i47908_5_, ResourcePackInfo.Priority p_i47908_6_) {
      this(p_i47908_1_, p_i47908_2_, p_i47908_3_, p_i47908_4_, p_i47908_5_, p_i47908_6_, false);
   }

   public ResourcePackInfo(String p_i230084_1_, boolean p_i230084_2_, Supplier<IResourcePack> p_i230084_3_, IResourcePack p_i230084_4_, PackMetadataSection p_i230084_5_, ResourcePackInfo.Priority p_i230084_6_, boolean p_i230084_7_) {
      this(p_i230084_1_, p_i230084_2_, p_i230084_3_, new StringTextComponent(p_i230084_4_.getName()), p_i230084_5_.getDescription(), PackCompatibility.func_198969_a(p_i230084_5_.getPackFormat()), p_i230084_6_, p_i230084_7_, p_i230084_7_);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent func_195789_b() {
      return this.field_195802_d;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDescription() {
      return this.description;
   }

   public ITextComponent func_195794_a(boolean p_195794_1_) {
      return TextComponentUtils.wrapInSquareBrackets(new StringTextComponent(this.name)).applyTextStyle((p_lambda$func_195794_a$0_2_) -> {
         p_lambda$func_195794_a$0_2_.setColor(p_195794_1_ ? TextFormatting.GREEN : TextFormatting.RED).setInsertion(StringArgumentType.escapeIfRequired(this.name)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new StringTextComponent("")).appendSibling(this.field_195802_d).appendText("\n").appendSibling(this.description)));
      });
   }

   public PackCompatibility getCompatibility() {
      return this.compatibility;
   }

   public IResourcePack getResourcePack() {
      return (IResourcePack)this.resourcePackSupplier.get();
   }

   public String getName() {
      return this.name;
   }

   public boolean isAlwaysEnabled() {
      return this.alwaysEnabled;
   }

   public boolean isOrderLocked() {
      return this.orderLocked;
   }

   public ResourcePackInfo.Priority getPriority() {
      return this.priority;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ResourcePackInfo)) {
         return false;
      } else {
         ResourcePackInfo resourcepackinfo = (ResourcePackInfo)p_equals_1_;
         return this.name.equals(resourcepackinfo.name);
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public void close() {
   }

   static {
      field_212500_b = new PackMetadataSection((new TranslationTextComponent("resourcePack.broken_assets", new Object[0])).applyTextStyles(new TextFormatting[]{TextFormatting.RED, TextFormatting.ITALIC}), SharedConstants.getVersion().getPackVersion());
   }

   public static enum Priority {
      TOP,
      BOTTOM;

      public <T, P extends ResourcePackInfo> int func_198993_a(List<T> p_198993_1_, T p_198993_2_, Function<T, P> p_198993_3_, boolean p_198993_4_) {
         ResourcePackInfo.Priority resourcepackinfo$priority = p_198993_4_ ? this.func_198992_a() : this;
         int i;
         ResourcePackInfo p;
         if (resourcepackinfo$priority == BOTTOM) {
            for(i = 0; i < p_198993_1_.size(); ++i) {
               p = (ResourcePackInfo)p_198993_3_.apply(p_198993_1_.get(i));
               if (!p.isOrderLocked() || p.getPriority() != this) {
                  break;
               }
            }

            p_198993_1_.add(i, p_198993_2_);
            return i;
         } else {
            for(i = p_198993_1_.size() - 1; i >= 0; --i) {
               p = (ResourcePackInfo)p_198993_3_.apply(p_198993_1_.get(i));
               if (!p.isOrderLocked() || p.getPriority() != this) {
                  break;
               }
            }

            p_198993_1_.add(i + 1, p_198993_2_);
            return i + 1;
         }
      }

      public ResourcePackInfo.Priority func_198992_a() {
         return this == TOP ? BOTTOM : TOP;
      }
   }

   @FunctionalInterface
   public interface IFactory<T extends ResourcePackInfo> {
      @Nullable
      T create(String var1, boolean var2, Supplier<IResourcePack> var3, IResourcePack var4, PackMetadataSection var5, ResourcePackInfo.Priority var6);
   }
}
