package net.cyberer.concatstringbenchmark;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.StringConcatException;
import java.lang.invoke.StringConcatFactory;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class MyBenchmark {
  private static String a = "abcdefg";
  private static String b = "hijklmn";
  private static String c = "opqrstu";
  private MethodHandle concatenator = null;

  public static void main(final String[] args) throws Exception {
    Options opt = new OptionsBuilder().include(MyBenchmark.class.getSimpleName()).forks(1).build();
    new Runner(opt).run();
  }

  @Setup
  public void setUp() throws StringConcatException {
    concatenator = StringConcatFactory.makeConcatWithConstants(MethodHandles.lookup(), "",
        MethodType.methodType(String.class, new Class[] { String.class, String.class, String.class, long.class }), "\1\2\1\2\1\2\1",
        '-', '/', '+').dynamicInvoker();
  }

  @Benchmark
  public void byStringBuffer(final Blackhole bh) {
    bh.consume(new StringBuffer(a).append('-').append(b).append('/').append(c).append('+').append(System.currentTimeMillis()).toString());
  }

  @Benchmark
  public void byStringBuilder(final Blackhole bh) {
    bh.consume(new StringBuilder(a).append('-').append(b).append('/').append(c).append('+').append(System.currentTimeMillis()).toString());
  }

  @Benchmark
  public void byIndy(final Blackhole bh) throws Throwable {
    bh.consume(concatenator.invoke(a, b, c, System.currentTimeMillis()));
  }
}
