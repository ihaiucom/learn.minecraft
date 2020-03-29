package net.minecraftforge.fml.common;

import net.minecraft.crash.ICrashReportDetail;

public interface ICrashCallable extends ICrashReportDetail<String> {
   String getLabel();
}
