package net.minecraftforge.fml;

import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraftforge.fml.loading.EarlyLoadingException;
import net.minecraftforge.forgespi.language.IModInfo;

public class ModLoadingException extends RuntimeException {
   private final IModInfo modInfo;
   private final ModLoadingStage errorStage;
   private final String i18nMessage;
   private final List<Object> context;

   public ModLoadingException(IModInfo modInfo, ModLoadingStage errorStage, String i18nMessage, Throwable originalException, Object... context) {
      super("Mod Loading Exception", originalException);
      this.modInfo = modInfo;
      this.errorStage = errorStage;
      this.i18nMessage = i18nMessage;
      this.context = Arrays.asList(context);
   }

   static Stream<ModLoadingException> fromEarlyException(EarlyLoadingException e) {
      return e.getAllData().stream().map((ed) -> {
         return new ModLoadingException((IModInfo)null, ModLoadingStage.VALIDATE, ed.getI18message(), e.getCause(), ed.getArgs());
      });
   }

   public String getI18NMessage() {
      return this.i18nMessage;
   }

   public Object[] getContext() {
      return this.context.toArray();
   }

   public String formatToString() {
      return ForgeI18n.parseMessage(this.i18nMessage, Streams.concat(new Stream[]{Stream.of(this.modInfo, this.errorStage, this.getCause()), this.context.stream()}).toArray());
   }

   public String getMessage() {
      return this.formatToString();
   }
}
