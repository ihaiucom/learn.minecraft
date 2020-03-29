package net.minecraft.state.properties;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public enum NoteBlockInstrument implements IStringSerializable {
   HARP("harp", SoundEvents.BLOCK_NOTE_BLOCK_HARP),
   BASEDRUM("basedrum", SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM),
   SNARE("snare", SoundEvents.BLOCK_NOTE_BLOCK_SNARE),
   HAT("hat", SoundEvents.BLOCK_NOTE_BLOCK_HAT),
   BASS("bass", SoundEvents.BLOCK_NOTE_BLOCK_BASS),
   FLUTE("flute", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE),
   BELL("bell", SoundEvents.BLOCK_NOTE_BLOCK_BELL),
   GUITAR("guitar", SoundEvents.BLOCK_NOTE_BLOCK_GUITAR),
   CHIME("chime", SoundEvents.BLOCK_NOTE_BLOCK_CHIME),
   XYLOPHONE("xylophone", SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE),
   IRON_XYLOPHONE("iron_xylophone", SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE),
   COW_BELL("cow_bell", SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL),
   DIDGERIDOO("didgeridoo", SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO),
   BIT("bit", SoundEvents.BLOCK_NOTE_BLOCK_BIT),
   BANJO("banjo", SoundEvents.BLOCK_NOTE_BLOCK_BANJO),
   PLING("pling", SoundEvents.BLOCK_NOTE_BLOCK_PLING);

   private final String name;
   private final SoundEvent sound;

   private NoteBlockInstrument(String p_i49336_3_, SoundEvent p_i49336_4_) {
      this.name = p_i49336_3_;
      this.sound = p_i49336_4_;
   }

   public String getName() {
      return this.name;
   }

   public SoundEvent getSound() {
      return this.sound;
   }

   public static NoteBlockInstrument byState(BlockState p_208087_0_) {
      Block lvt_1_1_ = p_208087_0_.getBlock();
      if (lvt_1_1_ == Blocks.CLAY) {
         return FLUTE;
      } else if (lvt_1_1_ == Blocks.GOLD_BLOCK) {
         return BELL;
      } else if (lvt_1_1_.isIn(BlockTags.WOOL)) {
         return GUITAR;
      } else if (lvt_1_1_ == Blocks.PACKED_ICE) {
         return CHIME;
      } else if (lvt_1_1_ == Blocks.BONE_BLOCK) {
         return XYLOPHONE;
      } else if (lvt_1_1_ == Blocks.IRON_BLOCK) {
         return IRON_XYLOPHONE;
      } else if (lvt_1_1_ == Blocks.SOUL_SAND) {
         return COW_BELL;
      } else if (lvt_1_1_ == Blocks.PUMPKIN) {
         return DIDGERIDOO;
      } else if (lvt_1_1_ == Blocks.EMERALD_BLOCK) {
         return BIT;
      } else if (lvt_1_1_ == Blocks.HAY_BLOCK) {
         return BANJO;
      } else if (lvt_1_1_ == Blocks.GLOWSTONE) {
         return PLING;
      } else {
         Material lvt_2_1_ = p_208087_0_.getMaterial();
         if (lvt_2_1_ == Material.ROCK) {
            return BASEDRUM;
         } else if (lvt_2_1_ == Material.SAND) {
            return SNARE;
         } else if (lvt_2_1_ == Material.GLASS) {
            return HAT;
         } else {
            return lvt_2_1_ == Material.WOOD ? BASS : HARP;
         }
      }
   }
}
