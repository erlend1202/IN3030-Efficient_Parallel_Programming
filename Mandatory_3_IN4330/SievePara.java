import java.util.ArrayList;
import java.util.List;
import java.util.Arrays; //for printing

public class SievePara {
    private final int numThreads; // number of threads
    private boolean[] primes; // array to store prime numbers
    private List<Thread> threads; // list of threads

    private int root, oddroot;
    int range;
    int n;


    public SievePara(int numThreads, int n) {
        this.numThreads = numThreads;
        this.threads = new ArrayList<>(); // initialize list of threads


        this.root = (int) Math.sqrt(n);
        this.oddroot = root/2 + root%2;
        this.range = n/2 + n%2;
        this.primes = new boolean[range + 1]; // initialize boolean array to store prime numbers        
        this.n = n;

        // Initialize primes array
        for (int i = 1; i < range; i++){
            primes[i] = true;
            //System.out.println(i*2 + 1 + " " + i); //we need to move from j to j + numThreads*(j*2 + 1) 
                                                    //so start = j + 2*j + 1 = 3*j+1
        }

    
    }

    /*
     * Sieve parallelized:
     * Since we get overhead with bytes, i changed to integers. Algorithm works by only checking odd numbers.
     * Indexing is therefore a bit different than with bytes.
     */
    public int[] getPrimes() throws InterruptedException {
      // Create threads
      for (int i = 0; i < numThreads; i++) {
          int stride = i;
          Thread thread = new Thread(() -> {
              int temp_start;
              int jump;
              // Sieve of Eratosthenes algorithm
              for (int j = 1; j <= oddroot; j++) {
                  if (primes[j]) {
                      //System.out.println(j + " " + stride);
                      jump = 2*j + 1;
                      temp_start = j + jump * (stride + 1);
                      for (int k = temp_start; k <= range; k += jump*(numThreads) ) {
                          primes[k] = false; // mark multiples of j as composite
                      }
                  }
              }
          });

          threads.add(thread); // add thread to list of threads
          thread.start(); // start thread
      }

      // Wait for threads to finish
      for (Thread thread : threads) {
          thread.join(); // wait for thread to finish
      }

      // Create list of prime numbers
      int num_primes = 1;
      for (int i = 1; i <= range; i++) {
          if (primes[i]) {
              num_primes += 1;
          }
      }
      int[] primeNumbers = new int[num_primes];
      int j = 1;
      primeNumbers[0] = 2;

      for (int i = 1; i <= range; i++) {
          if (primes[i]) {
              primeNumbers[j] = i*2 + 1; // add prime numbers to list
              j++;
          }
      }

      return primeNumbers;
  }    

  //Main function made for testing only.
  public static void main(String[] args)throws InterruptedException {

    int n, k;
    double[] timesseq = new double[7];
    double[] timespara = new double[7];

    try {
      n = Integer.parseInt(args[0]);
      k = Integer.parseInt(args[1]);
      if (n <= 0) throw new Exception();
    } catch(Exception e) {
      System.out.println("Correct use of program is: " +
      "java SieveOfEratosthenes <n> where <n> is a positive integer.");
      return;
    }

    double time_divisor = 1e9; //1e9 is to convert to seconds, 1e6 to convert to ms

    for (int run = 0; run < 7; run++){
      System.out.println("Run number: " + run);

      double seqStart = System.nanoTime();
      SieveOfEratosthenes sieve = new SieveOfEratosthenes(n);
      int[] primes = sieve.getPrimes();
      double ElapsedTimeseq = (System.nanoTime() - seqStart)/time_divisor;

      double paraStart = System.nanoTime();
      SievePara sievePara = new SievePara(k, n);
      int[] primesPara = sievePara.getPrimes();
      double ElapsedTimepara = (System.nanoTime() - paraStart)/time_divisor;

      AreEqual(primes, primesPara);
      //printPrimes(primesPara, "Parallel primes:");
      //printPrimes(primes, "Sequential primes:");

      System.out.println("Time for finding primes. Time seq: " + ElapsedTimeseq + "  ,Time para: " + ElapsedTimepara);
      System.out.println("");
      
      timesseq[run] = ElapsedTimeseq;
      timespara[run] = ElapsedTimepara;

    }

    Arrays.sort(timesseq);
    Arrays.sort(timespara);
    System.out.println("After 7 runs on n = " + n + ", we got these median times: ");
    System.out.println("Sieve sequential: " + timesseq[3] + ", Sieve parallel: " + timespara[3]);
    
  }

  public static void printPrimes(int[] primes, String method) {
    System.out.println(method);
    for (int prime : primes)
      System.out.println(prime);
  }

  public static int AreEqual(int[] primes_Seq, int[] primes_Para){
    int length = primes_Seq.length;
    int length2 = primes_Para.length;
    if (length != length2){
      System.out.println("Lengths arent the same.");
      return 0;
    }
    for (int i=0; i<length; i++){
        if (primes_Seq[i] != primes_Para[i]){
            System.out.println("not equal");
            return 0;
        }
    }
    return 0;
  }

}
