package net.minecraftforge.fml;

import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraftforge.forgespi.language.IModInfo;

public class ModLoadingWarning {
   private final IModInfo modInfo;
   private final ModLoadingStage warningStage;
   private final String i18nMessage;
   private final List<Object> context;

   public ModLoadingWarning(IModInfo modInfo, ModLoadingStage warningStage, String i18nMessage, Object... context) {
      this.modInfo = modInfo;
      this.warningStage = warningStage;
      this.i18nMessage = i18nMessage;
      this.context = Arrays.asList(context);
   }

   public String formatToString() {
      return ForgeI18n.parseMessage(this.i18nMessage, Streams.concat(new Stream[]{Stream.of(this.modInfo, this.warningStage), this.context.stream()}).toArray());
   }
}
