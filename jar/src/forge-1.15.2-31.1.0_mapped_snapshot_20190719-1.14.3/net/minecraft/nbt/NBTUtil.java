package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NBTUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   @Nullable
   public static GameProfile readGameProfile(CompoundNBT p_152459_0_) {
      String lvt_1_1_ = null;
      String lvt_2_1_ = null;
      if (p_152459_0_.contains("Name", 8)) {
         lvt_1_1_ = p_152459_0_.getString("Name");
      }

      if (p_152459_0_.contains("Id", 8)) {
         lvt_2_1_ = p_152459_0_.getString("Id");
      }

      try {
         UUID lvt_3_2_;
         try {
            lvt_3_2_ = UUID.fromString(lvt_2_1_);
         } catch (Throwable var12) {
            lvt_3_2_ = null;
         }

         GameProfile lvt_4_2_ = new GameProfile(lvt_3_2_, lvt_1_1_);
         if (p_152459_0_.contains("Properties", 10)) {
            CompoundNBT lvt_5_1_ = p_152459_0_.getCompound("Properties");
            Iterator var6 = lvt_5_1_.keySet().iterator();

            while(var6.hasNext()) {
               String lvt_7_1_ = (String)var6.next();
               ListNBT lvt_8_1_ = lvt_5_1_.getList(lvt_7_1_, 10);

               for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_8_1_.size(); ++lvt_9_1_) {
                  CompoundNBT lvt_10_1_ = lvt_8_1_.getCompound(lvt_9_1_);
                  String lvt_11_1_ = lvt_10_1_.getString("Value");
                  if (lvt_10_1_.contains("Signature", 8)) {
                     lvt_4_2_.getProperties().put(lvt_7_1_, new Property(lvt_7_1_, lvt_11_1_, lvt_10_1_.getString("Signature")));
                  } else {
                     lvt_4_2_.getProperties().put(lvt_7_1_, new Property(lvt_7_1_, lvt_11_1_));
                  }
               }
            }
         }

         return lvt_4_2_;
      } catch (Throwable var13) {
         return null;
      }
   }

   public static CompoundNBT writeGameProfile(CompoundNBT p_180708_0_, GameProfile p_180708_1_) {
      if (!StringUtils.isNullOrEmpty(p_180708_1_.getName())) {
         p_180708_0_.putString("Name", p_180708_1_.getName());
      }

      if (p_180708_1_.getId() != null) {
         p_180708_0_.putString("Id", p_180708_1_.getId().toString());
      }

      if (!p_180708_1_.getProperties().isEmpty()) {
         CompoundNBT lvt_2_1_ = new CompoundNBT();
         Iterator var3 = p_180708_1_.getProperties().keySet().iterator();

         while(var3.hasNext()) {
            String lvt_4_1_ = (String)var3.next();
            ListNBT lvt_5_1_ = new ListNBT();

            CompoundNBT lvt_8_1_;
            for(Iterator var6 = p_180708_1_.getProperties().get(lvt_4_1_).iterator(); var6.hasNext(); lvt_5_1_.add(lvt_8_1_)) {
               Property lvt_7_1_ = (Property)var6.next();
               lvt_8_1_ = new CompoundNBT();
               lvt_8_1_.putString("Value", lvt_7_1_.getValue());
               if (lvt_7_1_.hasSignature()) {
                  lvt_8_1_.putString("Signature", lvt_7_1_.getSignature());
               }
            }

            lvt_2_1_.put(lvt_4_1_, lvt_5_1_);
         }

         p_180708_0_.put("Properties", lvt_2_1_);
      }

      return p_180708_0_;
   }

   @VisibleForTesting
   public static boolean areNBTEquals(@Nullable INBT p_181123_0_, @Nullable INBT p_181123_1_, boolean p_181123_2_) {
      if (p_181123_0_ == p_181123_1_) {
         return true;
      } else if (p_181123_0_ == null) {
         return true;
      } else if (p_181123_1_ == null) {
         return false;
      } else if (!p_181123_0_.getClass().equals(p_181123_1_.getClass())) {
         return false;
      } else if (p_181123_0_ instanceof CompoundNBT) {
         CompoundNBT lvt_3_1_ = (CompoundNBT)p_181123_0_;
         CompoundNBT lvt_4_1_ = (CompoundNBT)p_181123_1_;
         Iterator var11 = lvt_3_1_.keySet().iterator();

         String lvt_6_1_;
         INBT lvt_7_1_;
         do {
            if (!var11.hasNext()) {
               return true;
            }

            lvt_6_1_ = (String)var11.next();
            lvt_7_1_ = lvt_3_1_.get(lvt_6_1_);
         } while(areNBTEquals(lvt_7_1_, lvt_4_1_.get(lvt_6_1_), p_181123_2_));

         return false;
      } else if (p_181123_0_ instanceof ListNBT && p_181123_2_) {
         ListNBT lvt_3_2_ = (ListNBT)p_181123_0_;
         ListNBT lvt_4_2_ = (ListNBT)p_181123_1_;
         if (lvt_3_2_.isEmpty()) {
            return lvt_4_2_.isEmpty();
         } else {
            for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_3_2_.size(); ++lvt_5_1_) {
               INBT lvt_6_2_ = lvt_3_2_.get(lvt_5_1_);
               boolean lvt_7_2_ = false;

               for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_4_2_.size(); ++lvt_8_1_) {
                  if (areNBTEquals(lvt_6_2_, lvt_4_2_.get(lvt_8_1_), p_181123_2_)) {
                     lvt_7_2_ = true;
                     break;
                  }
               }

               if (!lvt_7_2_) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return p_181123_0_.equals(p_181123_1_);
      }
   }

   public static CompoundNBT writeUniqueId(UUID p_186862_0_) {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      lvt_1_1_.putLong("M", p_186862_0_.getMostSignificantBits());
      lvt_1_1_.putLong("L", p_186862_0_.getLeastSignificantBits());
      return lvt_1_1_;
   }

   public static UUID readUniqueId(CompoundNBT p_186860_0_) {
      return new UUID(p_186860_0_.getLong("M"), p_186860_0_.getLong("L"));
   }

   public static BlockPos readBlockPos(CompoundNBT p_186861_0_) {
      return new BlockPos(p_186861_0_.getInt("X"), p_186861_0_.getInt("Y"), p_186861_0_.getInt("Z"));
   }

   public static CompoundNBT writeBlockPos(BlockPos p_186859_0_) {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      lvt_1_1_.putInt("X", p_186859_0_.getX());
      lvt_1_1_.putInt("Y", p_186859_0_.getY());
      lvt_1_1_.putInt("Z", p_186859_0_.getZ());
      return lvt_1_1_;
   }

   public static BlockState readBlockState(CompoundNBT p_190008_0_) {
      if (!p_190008_0_.contains("Name", 8)) {
         return Blocks.AIR.getDefaultState();
      } else {
         Block lvt_1_1_ = (Block)Registry.BLOCK.getOrDefault(new ResourceLocation(p_190008_0_.getString("Name")));
         BlockState lvt_2_1_ = lvt_1_1_.getDefaultState();
         if (p_190008_0_.contains("Properties", 10)) {
            CompoundNBT lvt_3_1_ = p_190008_0_.getCompound("Properties");
            StateContainer<Block, BlockState> lvt_4_1_ = lvt_1_1_.getStateContainer();
            Iterator var5 = lvt_3_1_.keySet().iterator();

            while(var5.hasNext()) {
               String lvt_6_1_ = (String)var5.next();
               IProperty<?> lvt_7_1_ = lvt_4_1_.getProperty(lvt_6_1_);
               if (lvt_7_1_ != null) {
                  lvt_2_1_ = (BlockState)setValueHelper(lvt_2_1_, lvt_7_1_, lvt_6_1_, lvt_3_1_, p_190008_0_);
               }
            }
         }

         return lvt_2_1_;
      }
   }

   private static <S extends IStateHolder<S>, T extends Comparable<T>> S setValueHelper(S p_193590_0_, IProperty<T> p_193590_1_, String p_193590_2_, CompoundNBT p_193590_3_, CompoundNBT p_193590_4_) {
      Optional<T> lvt_5_1_ = p_193590_1_.parseValue(p_193590_3_.getString(p_193590_2_));
      if (lvt_5_1_.isPresent()) {
         return (IStateHolder)p_193590_0_.with(p_193590_1_, (Comparable)lvt_5_1_.get());
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", p_193590_2_, p_193590_3_.getString(p_193590_2_), p_193590_4_.toString());
         return p_193590_0_;
      }
   }

   public static CompoundNBT writeBlockState(BlockState p_190009_0_) {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      lvt_1_1_.putString("Name", Registry.BLOCK.getKey(p_190009_0_.getBlock()).toString());
      ImmutableMap<IProperty<?>, Comparable<?>> lvt_2_1_ = p_190009_0_.getValues();
      if (!lvt_2_1_.isEmpty()) {
         CompoundNBT lvt_3_1_ = new CompoundNBT();
         UnmodifiableIterator var4 = lvt_2_1_.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<IProperty<?>, Comparable<?>> lvt_5_1_ = (Entry)var4.next();
            IProperty<?> lvt_6_1_ = (IProperty)lvt_5_1_.getKey();
            lvt_3_1_.putString(lvt_6_1_.getName(), getName(lvt_6_1_, (Comparable)lvt_5_1_.getValue()));
         }

         lvt_1_1_.put("Properties", lvt_3_1_);
      }

      return lvt_1_1_;
   }

   private static <T extends Comparable<T>> String getName(IProperty<T> p_190010_0_, Comparable<?> p_190010_1_) {
      return p_190010_0_.getName(p_190010_1_);
   }

   public static CompoundNBT update(DataFixer p_210822_0_, DefaultTypeReferences p_210822_1_, CompoundNBT p_210822_2_, int p_210822_3_) {
      return update(p_210822_0_, p_210822_1_, p_210822_2_, p_210822_3_, SharedConstants.getVersion().getWorldVersion());
   }

   public static CompoundNBT update(DataFixer p_210821_0_, DefaultTypeReferences p_210821_1_, CompoundNBT p_210821_2_, int p_210821_3_, int p_210821_4_) {
      return (CompoundNBT)p_210821_0_.update(p_210821_1_.func_219816_a(), new Dynamic(NBTDynamicOps.INSTANCE, p_210821_2_), p_210821_3_, p_210821_4_).getValue();
   }
}
