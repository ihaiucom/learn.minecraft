package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.IResourcePack;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LanguageManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final Locale CURRENT_LOCALE = new Locale();
   private String currentLanguage;
   private final Map<String, Language> languageMap = Maps.newHashMap();

   public LanguageManager(String p_i48112_1_) {
      this.currentLanguage = p_i48112_1_;
      I18n.setLocale(CURRENT_LOCALE);
   }

   public void parseLanguageMetadata(List<IResourcePack> p_135043_1_) {
      this.languageMap.clear();
      Iterator var2 = p_135043_1_.iterator();

      while(var2.hasNext()) {
         IResourcePack iresourcepack = (IResourcePack)var2.next();

         try {
            LanguageMetadataSection languagemetadatasection = (LanguageMetadataSection)iresourcepack.getMetadata(LanguageMetadataSection.field_195818_a);
            if (languagemetadatasection != null) {
               Iterator var5 = languagemetadatasection.getLanguages().iterator();

               while(var5.hasNext()) {
                  Language language = (Language)var5.next();
                  if (!this.languageMap.containsKey(language.getCode())) {
                     this.languageMap.put(language.getCode(), language);
                  }
               }
            }
         } catch (RuntimeException | IOException var7) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", iresourcepack.getName(), var7);
         }
      }

   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      List<String> list = Lists.newArrayList(new String[]{"en_us"});
      if (!"en_us".equals(this.currentLanguage)) {
         list.add(this.currentLanguage);
      }

      CURRENT_LOCALE.func_195811_a(p_195410_1_, list);
      LanguageMap.replaceWith(CURRENT_LOCALE.properties);
   }

   public boolean isCurrentLanguageBidirectional() {
      return this.getCurrentLanguage() != null && this.getCurrentLanguage().isBidirectional();
   }

   public void setCurrentLanguage(Language p_135045_1_) {
      this.currentLanguage = p_135045_1_.getCode();
   }

   public Language getCurrentLanguage() {
      String s = this.languageMap.containsKey(this.currentLanguage) ? this.currentLanguage : "en_us";
      return (Language)this.languageMap.get(s);
   }

   public SortedSet<Language> getLanguages() {
      return Sets.newTreeSet(this.languageMap.values());
   }

   public Language getLanguage(String p_191960_1_) {
      return (Language)this.languageMap.get(p_191960_1_);
   }

   public IResourceType getResourceType() {
      return VanillaResourceType.LANGUAGES;
   }
}
