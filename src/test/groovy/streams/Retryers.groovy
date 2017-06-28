package streams

import com.github.rholder.retry.Retryer
import com.github.rholder.retry.RetryerBuilder
import com.github.rholder.retry.StopStrategies
import com.github.rholder.retry.WaitStrategies
import com.google.common.base.Predicate

import javax.annotation.Nullable
import java.util.concurrent.TimeUnit

class Retryers {
  public static Retryer<List> untilNotEmpty = RetryerBuilder.<List> newBuilder()
      .retryIfResult(new Predicate<List>() {
    @Override
    boolean apply(@Nullable List input) {
      input.empty
    }
  })
      .withWaitStrategy(WaitStrategies.fixedWait(3000, TimeUnit.MILLISECONDS))
      .withStopStrategy(StopStrategies.stopAfterAttempt(60))
      .build()
}
