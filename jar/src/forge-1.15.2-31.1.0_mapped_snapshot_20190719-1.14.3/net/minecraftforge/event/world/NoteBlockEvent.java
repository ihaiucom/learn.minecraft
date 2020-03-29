package net.minecraftforge.event.world;

import com.google.common.base.Preconditions;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;

public class NoteBlockEvent extends BlockEvent {
   private int noteId;

   protected NoteBlockEvent(World world, BlockPos pos, BlockState state, int note) {
      super(world, pos, state);
      this.noteId = note;
   }

   public NoteBlockEvent.Note getNote() {
      return NoteBlockEvent.Note.fromId(this.noteId);
   }

   public NoteBlockEvent.Octave getOctave() {
      return NoteBlockEvent.Octave.fromId(this.noteId);
   }

   public int getVanillaNoteId() {
      return this.noteId;
   }

   public void setNote(NoteBlockEvent.Note note, NoteBlockEvent.Octave octave) {
      Preconditions.checkArgument(octave != NoteBlockEvent.Octave.HIGH || note == NoteBlockEvent.Note.F_SHARP, "Octave.HIGH is only valid for Note.F_SHARP!");
      this.noteId = note.ordinal() + octave.ordinal() * 12;
   }

   public static enum Octave {
      LOW,
      MID,
      HIGH;

      static NoteBlockEvent.Octave fromId(int id) {
         return id < 12 ? LOW : (id == 24 ? HIGH : MID);
      }
   }

   public static enum Note {
      F_SHARP,
      G,
      G_SHARP,
      A,
      A_SHARP,
      B,
      C,
      C_SHARP,
      D,
      D_SHARP,
      E,
      F;

      private static final NoteBlockEvent.Note[] values = values();

      static NoteBlockEvent.Note fromId(int id) {
         return values[id % 12];
      }
   }

   @Cancelable
   public static class Change extends NoteBlockEvent {
      private final NoteBlockEvent.Note oldNote;
      private final NoteBlockEvent.Octave oldOctave;

      public Change(World world, BlockPos pos, BlockState state, int oldNote, int newNote) {
         super(world, pos, state, newNote);
         this.oldNote = NoteBlockEvent.Note.fromId(oldNote);
         this.oldOctave = NoteBlockEvent.Octave.fromId(oldNote);
      }

      public NoteBlockEvent.Note getOldNote() {
         return this.oldNote;
      }

      public NoteBlockEvent.Octave getOldOctave() {
         return this.oldOctave;
      }
   }

   @Cancelable
   public static class Play extends NoteBlockEvent {
      private NoteBlockInstrument instrument;

      public Play(World world, BlockPos pos, BlockState state, int note, NoteBlockInstrument instrument) {
         super(world, pos, state, note);
         this.instrument = instrument;
      }

      public NoteBlockInstrument getInstrument() {
         return this.instrument;
      }

      public void setInstrument(NoteBlockInstrument instrument) {
         this.instrument = instrument;
      }
   }
}
