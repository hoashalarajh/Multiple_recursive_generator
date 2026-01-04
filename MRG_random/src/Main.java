public class Main {
    public static void main(String[] args) {
        System.out.println("--- Test 1: Default (Time-based seed) ---");
        MRGNormal rngDefault = new MRGNormal();
        System.out.printf("Uniform [0.0, 1.0]: %.5f%n", rngDefault.randomUniform());
        System.out.printf("Normal  (m=0, s=1): %.5f%n", rngDefault.randomNormal());
        System.out.println();

        System.out.println("--- Test 2: Specific Seed (Key = 5) ---");
        // Seed 5, Mean 10, StdDev 2, Uniform Range 100-200
        MRGNormal rngFixed = new MRGNormal(5, 10.0, 2.0, 100.0, 200.0);

        System.out.println("Generating 3 Uniform Numbers [100, 200]:");
        for (int i = 0; i < 3; i++) {
            System.out.printf("  Val %d: %.5f%n", i + 1, rngFixed.randomUniform());
        }

        System.out.println("Generating 4 Normal Numbers (Mean=10, SD=2):");
        for (int i = 0; i < 4; i++) {
            System.out.printf("  Val %d: %.5f%n", i + 1, rngFixed.randomNormal());
        }
        System.out.println("=================================================================");
        System.out.println("This is for random number generation using uniform distribution");
        System.out.println("=================================================================");
        // running a for loop for producing random numbers from a uniform distribution ranging from
        // zero to one (0 - 1)
        for (int i = 0; i < 15; i++){
            System.out.println("This is using uniform distribution default value");
            double rand_num = rngDefault.randomUniform();
            System.out.println("Iteration " + (i + 1) + ": " + rand_num);
        }
        System.out.println("=================================================================");
        System.out.println("This is for random number generation using normal distribution");
        System.out.println("=================================================================");
        // running for loop for generating random numbers from a normal distribution
        for (int i = 0; i < 15; i++){
            System.out.println("This is using normal distribution default value");
            double rand_num = rngDefault.randomNormal();
            System.out.println("Iteration " + (i + 1) + ": " + rand_num);
        }
    }
}