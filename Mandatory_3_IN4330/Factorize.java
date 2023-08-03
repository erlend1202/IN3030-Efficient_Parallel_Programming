import java.util.ArrayList;
import java.util.List;

public class Factorize {
    public ArrayList<Long> factors = new ArrayList<Long>();
    long n;
    int[] primes;

    public Factorize(long n, int[] primes){
        this.n = n;
        this.primes = primes;
    }

    public ArrayList<Long> getFactors(){
        //int length = primes.length;
        long temp = n;
        for (int prime: primes){
            if (temp <= prime){
                if (temp == prime) factors.add((long)prime);
                return factors;
            }

            while (temp%prime == 0){
                temp = temp/prime;
                factors.add((long)prime);
            }
        }
        if (temp != 1){
            factors.add(temp);
        }
        return factors;
    }

    public void printFactors(List<Long> factors){
        System.out.println("Sequential factorization:");
        for (long factor: factors){
            System.out.println(factor);
        }
    }
}