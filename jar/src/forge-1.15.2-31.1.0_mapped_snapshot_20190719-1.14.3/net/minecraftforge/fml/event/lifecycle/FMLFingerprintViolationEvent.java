package net.minecraftforge.fml.event.lifecycle;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.util.Set;
import net.minecraftforge.fml.ModContainer;

public class FMLFingerprintViolationEvent extends ModLifecycleEvent {
   private final boolean isDirectory;
   private final Set<String> fingerprints;
   private final File source;
   private final String expectedFingerprint;

   public FMLFingerprintViolationEvent(boolean isDirectory, File source, ImmutableSet<String> fingerprints, String expectedFingerprint) {
      super((ModContainer)null);
      this.isDirectory = isDirectory;
      this.source = source;
      this.fingerprints = fingerprints;
      this.expectedFingerprint = expectedFingerprint;
   }

   public boolean isDirectory() {
      return this.isDirectory;
   }

   public Set<String> getFingerprints() {
      return this.fingerprints;
   }

   public File getSource() {
      return this.source;
   }

   public String getExpectedFingerprint() {
      return this.expectedFingerprint;
   }
}
