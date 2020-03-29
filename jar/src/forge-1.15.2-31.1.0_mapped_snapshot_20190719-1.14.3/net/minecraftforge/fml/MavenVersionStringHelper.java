package net.minecraftforge.fml;

import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;

public class MavenVersionStringHelper {
   public static String artifactVersionToString(ArtifactVersion artifactVersion) {
      return artifactVersion.toString();
   }

   public static String versionRangeToString(VersionRange range) {
      return (String)range.getRestrictions().stream().map(MavenVersionStringHelper::restrictionToString).collect(Collectors.joining(", "));
   }

   public static String restrictionToString(Restriction restriction) {
      if (restriction.getLowerBound() == null && restriction.getUpperBound() == null) {
         return ForgeI18n.parseMessage("fml.messages.version.restriction.any");
      } else if (restriction.getLowerBound() != null && restriction.getUpperBound() != null) {
         if (Objects.equals(artifactVersionToString(restriction.getLowerBound()), artifactVersionToString(restriction.getUpperBound()))) {
            return artifactVersionToString(restriction.getLowerBound());
         } else if (restriction.isLowerBoundInclusive() && restriction.isUpperBoundInclusive()) {
            return ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.inclusive", restriction.getLowerBound(), restriction.getUpperBound());
         } else if (restriction.isLowerBoundInclusive()) {
            return ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.upperexclusive", restriction.getLowerBound(), restriction.getUpperBound());
         } else {
            return restriction.isUpperBoundInclusive() ? ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.lowerexclusive", restriction.getLowerBound(), restriction.getUpperBound()) : ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.exclusive", restriction.getLowerBound(), restriction.getUpperBound());
         }
      } else if (restriction.getLowerBound() != null) {
         return restriction.isLowerBoundInclusive() ? ForgeI18n.parseMessage("fml.messages.version.restriction.lower.inclusive", restriction.getLowerBound()) : ForgeI18n.parseMessage("fml.messages.version.restriction.lower.exclusive", restriction.getLowerBound());
      } else {
         return restriction.isUpperBoundInclusive() ? ForgeI18n.parseMessage("fml.messages.version.restriction.upper.inclusive", restriction.getUpperBound()) : ForgeI18n.parseMessage("fml.messages.version.restriction.upper.exclusive", restriction.getUpperBound());
      }
   }

   public static void parseVersionRange(String formatString, StringBuffer stringBuffer, Object range) {
      stringBuffer.append(versionRangeToString((VersionRange)range));
   }
}
