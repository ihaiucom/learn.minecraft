package net.minecraft.util;

public class ActionResult<T> {
   private final ActionResultType type;
   private final T result;

   public ActionResult(ActionResultType p_i46821_1_, T p_i46821_2_) {
      this.type = p_i46821_1_;
      this.result = p_i46821_2_;
   }

   public ActionResultType getType() {
      return this.type;
   }

   public T getResult() {
      return this.result;
   }

   public static <T> ActionResult<T> func_226248_a_(T p_226248_0_) {
      return new ActionResult(ActionResultType.SUCCESS, p_226248_0_);
   }

   public static <T> ActionResult<T> func_226249_b_(T p_226249_0_) {
      return new ActionResult(ActionResultType.CONSUME, p_226249_0_);
   }

   public static <T> ActionResult<T> func_226250_c_(T p_226250_0_) {
      return new ActionResult(ActionResultType.PASS, p_226250_0_);
   }

   public static <T> ActionResult<T> func_226251_d_(T p_226251_0_) {
      return new ActionResult(ActionResultType.FAIL, p_226251_0_);
   }
}
