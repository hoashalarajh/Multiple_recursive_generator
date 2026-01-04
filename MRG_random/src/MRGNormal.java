import java.util.HashMap;
import java.util.Map;

/**
 * A random number generator using the Multiple Recursive Generator (MRG)
 * for uniform distribution, with optional transformation to normal distribution.
 */
public class MRGNormal {

  // Constants
  private static final long MODULUS = 4294967311L; // 2^32 + 15
  private static final long[] COEFFICIENTS = {1664543L, 1013904223L, 1289L, 124897L};

  // Seed Set Configuration
  private static final Map<Integer, long[]> RANDOM_SEED_SET = new HashMap<>();

  static {
    RANDOM_SEED_SET.put(1, new long[]{11981, 4001, 1013, 1997});
    RANDOM_SEED_SET.put(2, new long[]{11981, 4001, 1997, 1013});
    RANDOM_SEED_SET.put(3, new long[]{11981, 1013, 4001, 1997});
    RANDOM_SEED_SET.put(4, new long[]{11981, 1013, 1997, 4001});
    RANDOM_SEED_SET.put(5, new long[]{11981, 1997, 4001, 1013});
    RANDOM_SEED_SET.put(6, new long[]{11981, 1997, 1013, 4001});
    RANDOM_SEED_SET.put(7, new long[]{4001, 11981, 1013, 1997});
    RANDOM_SEED_SET.put(8, new long[]{4001, 11981, 1997, 1013});
    RANDOM_SEED_SET.put(9, new long[]{4001, 1013, 11981, 1997});
    RANDOM_SEED_SET.put(10, new long[]{4001, 1013, 1997, 11981});
    RANDOM_SEED_SET.put(11, new long[]{4001, 1997, 11981, 1013});
    RANDOM_SEED_SET.put(12, new long[]{4001, 1997, 1013, 11981});
    RANDOM_SEED_SET.put(13, new long[]{1013, 11981, 4001, 1997});
    RANDOM_SEED_SET.put(14, new long[]{1013, 11981, 1997, 4001});
    RANDOM_SEED_SET.put(15, new long[]{1013, 4001, 11981, 1997});
    RANDOM_SEED_SET.put(16, new long[]{1013, 4001, 1997, 11981});
    RANDOM_SEED_SET.put(17, new long[]{1013, 1997, 11981, 4001});
    RANDOM_SEED_SET.put(18, new long[]{1013, 1997, 4001, 11981});
    RANDOM_SEED_SET.put(19, new long[]{1997, 11981, 4001, 1013});
    RANDOM_SEED_SET.put(20, new long[]{1997, 11981, 1013, 4001});
    RANDOM_SEED_SET.put(21, new long[]{1997, 4001, 11981, 1013});
    RANDOM_SEED_SET.put(22, new long[]{1997, 4001, 1013, 11981});
    RANDOM_SEED_SET.put(23, new long[]{1997, 1013, 11981, 4001});
    RANDOM_SEED_SET.put(24, new long[]{1997, 1013, 4001, 11981});
  }

  // Instance Variables
  private int seedKey;
  private long[] seedList;
  private double mean;
  private double stdDev;
  private double uniformLow;
  private double uniformHigh;
  private Double cachedNormal = null;

  /**
   * Constructor with all parameters.
   * * @param seedKey      Seed key from 1 to 24. If null (passed as 0 or negative), chosen based on time.
   * @param mean         Mean of the normal distribution.
   * @param stdDev       Standard deviation of the normal distribution.
   * @param uniformLow   Lower bound for the uniform distribution.
   * @param uniformHigh  Upper bound for the uniform distribution.
   */
  public MRGNormal(Integer seedKey, double mean, double stdDev, double uniformLow, double uniformHigh) {
    int numSeeds = RANDOM_SEED_SET.size();

    // Handle auto-seeding if seedKey is null or invalid for manual selection
    if (seedKey == null) {
      this.seedKey = (int) (System.currentTimeMillis() % numSeeds) + 1;
    } else {
      if (seedKey < 1 || seedKey > numSeeds) {
        throw new IllegalArgumentException("Invalid seed_key. Must be an integer between 1 and " + numSeeds + ".");
      }
      this.seedKey = seedKey;
    }

    // Clone the seed array to ensure independence
    this.seedList = RANDOM_SEED_SET.get(this.seedKey).clone();

    this.mean = mean;
    this.stdDev = stdDev;
    this.uniformLow = uniformLow;
    this.uniformHigh = uniformHigh;
  }

  // Overloaded constructor for easier usage
  public MRGNormal() {
    this(null, 0.0, 1.0, 0.0, 1.0);
  }

  public MRGNormal(Integer seedKey) {
    this(seedKey, 0.0, 1.0, 0.0, 1.0);
  }

  /**
   * Internal MRG Step function.
   * Updates the seed list and returns the new seed.
   */
  private long mrgStep() {
    long newSeed = 0;

    // sum(a * x for a, x in zip(self.coefficients, self.seed_list))
    for (int i = 0; i < COEFFICIENTS.length; i++) {
      newSeed += COEFFICIENTS[i] * seedList[i];
    }

    newSeed %= MODULUS;

    // self.seed_list = self.seed_list[1:] + [new_seed]
    // Shift elements left and add newSeed at the end
    System.arraycopy(seedList, 1, seedList, 0, seedList.length - 1);
    seedList[seedList.length - 1] = newSeed;

    return newSeed;
  }

  /**
   * Generate a random number uniformly distributed in [uniform_low, uniform_high].
   *
   * @return A uniform random number.
   */
  public double randomUniform() {
    double raw = (mrgStep() + 1.0) / (MODULUS + 1.0);
    return this.uniformLow + raw * (this.uniformHigh - this.uniformLow);
  }

  /**
   * Generate a random number from a normal distribution with specified mean and std_dev.
   * Uses the Box-Muller transform.
   *
   * @return A normal random number.
   */
  public double randomNormal() {
    if (this.cachedNormal != null) {
      double result = this.cachedNormal;
      this.cachedNormal = null;
      return result;
    }

    double u1 = (mrgStep() + 1.0) / (MODULUS + 1.0);
    double u2 = (mrgStep() + 1.0) / (MODULUS + 1.0);

    // Box-Muller transform
    double z1 = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
    double z2 = Math.sqrt(-2.0 * Math.log(u1)) * Math.sin(2.0 * Math.PI * u2);

    this.cachedNormal = z2 * this.stdDev + this.mean;
    return z1 * this.stdDev + this.mean;
  }
}