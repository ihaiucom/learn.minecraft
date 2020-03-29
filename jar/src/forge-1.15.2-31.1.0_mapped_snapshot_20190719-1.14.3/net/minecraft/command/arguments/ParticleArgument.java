package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ParticleArgument implements ArgumentType<IParticleData> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
   public static final DynamicCommandExceptionType PARTICLE_NOT_FOUND = new DynamicCommandExceptionType((p_208673_0_) -> {
      return new TranslationTextComponent("particle.notFound", new Object[]{p_208673_0_});
   });

   public static ParticleArgument particle() {
      return new ParticleArgument();
   }

   public static IParticleData getParticle(CommandContext<CommandSource> p_197187_0_, String p_197187_1_) {
      return (IParticleData)p_197187_0_.getArgument(p_197187_1_, IParticleData.class);
   }

   public IParticleData parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return parseParticle(p_parse_1_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static IParticleData parseParticle(StringReader p_197189_0_) throws CommandSyntaxException {
      ResourceLocation lvt_1_1_ = ResourceLocation.read(p_197189_0_);
      ParticleType<?> lvt_2_1_ = (ParticleType)Registry.PARTICLE_TYPE.getValue(lvt_1_1_).orElseThrow(() -> {
         return PARTICLE_NOT_FOUND.create(lvt_1_1_);
      });
      return deserializeParticle(p_197189_0_, lvt_2_1_);
   }

   private static <T extends IParticleData> T deserializeParticle(StringReader p_199816_0_, ParticleType<T> p_199816_1_) throws CommandSyntaxException {
      return p_199816_1_.getDeserializer().deserialize(p_199816_1_, p_199816_0_);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggestIterable(Registry.PARTICLE_TYPE.keySet(), p_listSuggestions_2_);
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
