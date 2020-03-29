package net.minecraftforge.fml.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.cert.Certificate;

public class CertificateHelper {
   private static final String HEXES = "0123456789abcdef";

   public static ImmutableList<String> getFingerprints(Certificate[] certificates) {
      int len = 0;
      if (certificates != null) {
         len = certificates.length;
      }

      Builder<String> certBuilder = ImmutableList.builder();

      for(int i = 0; i < len; ++i) {
         certBuilder.add(getFingerprint(certificates[i]));
      }

      return certBuilder.build();
   }

   public static String getFingerprint(Certificate certificate) {
      if (certificate == null) {
         return "NO VALID CERTIFICATE FOUND";
      } else {
         try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] der = certificate.getEncoded();
            md.update(der);
            byte[] digest = md.digest();
            return hexify(digest);
         } catch (Exception var4) {
            return "CERTIFICATE FINGERPRINT EXCEPTION";
         }
      }
   }

   public static String getFingerprint(ByteBuffer buffer) {
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-1");
         digest.update(buffer);
         byte[] chksum = digest.digest();
         return hexify(chksum);
      } catch (Exception var3) {
         return "CERTIFICATE FINGERPRINT EXCEPTION";
      }
   }

   private static String hexify(byte[] chksum) {
      StringBuilder hex = new StringBuilder(2 * chksum.length);
      byte[] var2 = chksum;
      int var3 = chksum.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte b = var2[var4];
         hex.append("0123456789abcdef".charAt((b & 240) >> 4)).append("0123456789abcdef".charAt(b & 15));
      }

      return hex.toString();
   }
}
