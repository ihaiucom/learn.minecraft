package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHeadToggle;
import net.minecraft.client.resources.data.VillagerMetadataSection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerLevelPendantLayer<T extends LivingEntity & IVillagerDataHolder, M extends EntityModel<T> & IHeadToggle> extends LayerRenderer<T, M> implements IResourceManagerReloadListener {
   private static final Int2ObjectMap<ResourceLocation> field_215352_a = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), (p_215348_0_) -> {
      p_215348_0_.put(1, new ResourceLocation("stone"));
      p_215348_0_.put(2, new ResourceLocation("iron"));
      p_215348_0_.put(3, new ResourceLocation("gold"));
      p_215348_0_.put(4, new ResourceLocation("emerald"));
      p_215348_0_.put(5, new ResourceLocation("diamond"));
   });
   private final Object2ObjectMap<IVillagerType, VillagerMetadataSection.HatType> field_215353_b = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<VillagerProfession, VillagerMetadataSection.HatType> field_215354_c = new Object2ObjectOpenHashMap();
   private final IReloadableResourceManager field_215355_d;
   private final String field_215356_e;

   public VillagerLevelPendantLayer(IEntityRenderer<T, M> p_i50955_1_, IReloadableResourceManager p_i50955_2_, String p_i50955_3_) {
      super(p_i50955_1_);
      this.field_215355_d = p_i50955_2_;
      this.field_215356_e = p_i50955_3_;
      p_i50955_2_.addReloadListener(this);
   }

   public void func_225628_a_(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (!p_225628_4_.isInvisible()) {
         VillagerData lvt_11_1_ = ((IVillagerDataHolder)p_225628_4_).getVillagerData();
         IVillagerType lvt_12_1_ = lvt_11_1_.getType();
         VillagerProfession lvt_13_1_ = lvt_11_1_.getProfession();
         VillagerMetadataSection.HatType lvt_14_1_ = this.func_215350_a(this.field_215353_b, "type", Registry.VILLAGER_TYPE, lvt_12_1_);
         VillagerMetadataSection.HatType lvt_15_1_ = this.func_215350_a(this.field_215354_c, "profession", Registry.VILLAGER_PROFESSION, lvt_13_1_);
         M lvt_16_1_ = this.getEntityModel();
         ((IHeadToggle)lvt_16_1_).func_217146_a(lvt_15_1_ == VillagerMetadataSection.HatType.NONE || lvt_15_1_ == VillagerMetadataSection.HatType.PARTIAL && lvt_14_1_ != VillagerMetadataSection.HatType.FULL);
         ResourceLocation lvt_17_1_ = this.func_215351_a("type", Registry.VILLAGER_TYPE.getKey(lvt_12_1_));
         func_229141_a_(lvt_16_1_, lvt_17_1_, p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, 1.0F, 1.0F, 1.0F);
         ((IHeadToggle)lvt_16_1_).func_217146_a(true);
         if (lvt_13_1_ != VillagerProfession.NONE && !p_225628_4_.isChild()) {
            ResourceLocation lvt_18_1_ = this.func_215351_a("profession", Registry.VILLAGER_PROFESSION.getKey(lvt_13_1_));
            func_229141_a_(lvt_16_1_, lvt_18_1_, p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, 1.0F, 1.0F, 1.0F);
            if (lvt_13_1_ != VillagerProfession.NITWIT) {
               ResourceLocation lvt_19_1_ = this.func_215351_a("profession_level", (ResourceLocation)field_215352_a.get(MathHelper.clamp(lvt_11_1_.getLevel(), 1, field_215352_a.size())));
               func_229141_a_(lvt_16_1_, lvt_19_1_, p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, 1.0F, 1.0F, 1.0F);
            }
         }

      }
   }

   private ResourceLocation func_215351_a(String p_215351_1_, ResourceLocation p_215351_2_) {
      return new ResourceLocation(p_215351_2_.getNamespace(), "textures/entity/" + this.field_215356_e + "/" + p_215351_1_ + "/" + p_215351_2_.getPath() + ".png");
   }

   public <K> VillagerMetadataSection.HatType func_215350_a(Object2ObjectMap<K, VillagerMetadataSection.HatType> p_215350_1_, String p_215350_2_, DefaultedRegistry<K> p_215350_3_, K p_215350_4_) {
      return (VillagerMetadataSection.HatType)p_215350_1_.computeIfAbsent(p_215350_4_, (p_215349_4_) -> {
         try {
            IResource lvt_5_1_ = this.field_215355_d.getResource(this.func_215351_a(p_215350_2_, p_215350_3_.getKey(p_215350_4_)));
            Throwable var6 = null;

            VillagerMetadataSection.HatType var8;
            try {
               VillagerMetadataSection lvt_7_1_ = (VillagerMetadataSection)lvt_5_1_.getMetadata(VillagerMetadataSection.field_217827_a);
               if (lvt_7_1_ == null) {
                  return VillagerMetadataSection.HatType.NONE;
               }

               var8 = lvt_7_1_.func_217826_a();
            } catch (Throwable var19) {
               var6 = var19;
               throw var19;
            } finally {
               if (lvt_5_1_ != null) {
                  if (var6 != null) {
                     try {
                        lvt_5_1_.close();
                     } catch (Throwable var18) {
                        var6.addSuppressed(var18);
                     }
                  } else {
                     lvt_5_1_.close();
                  }
               }

            }

            return var8;
         } catch (IOException var21) {
            return VillagerMetadataSection.HatType.NONE;
         }
      });
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.field_215354_c.clear();
      this.field_215353_b.clear();
   }
}
