import java.util.concurrent.*;
import java.util.Arrays; //for printing
import java.util.Collections;

import java.io.IOException;

import java.io.FileWriter; 


import java.util.ArrayList;
import java.util.List;

//To compile:
//javac Oblig3.java; java Oblig3 <n> <k> 
public class Oblig3 {

    static int n; 
    static int k;
    public static void main(String[] args)throws InterruptedException{  
        //Taking n and k as input arguments          
        try {
            n = Integer.parseInt(args[0]);
            if (n < 16) throw new Exception();
        } catch(Exception e) {
            System.out.println("Correct use of program is: " +
            "java Oblig3 <n> <k> where <n> and <k> are positive integers. n must be greater than 16.");
            return;
        }
        k = Integer.parseInt(args[1]);
        if (k <= 0) k = Runtime.getRuntime().availableProcessors(); //Use number of cores available if k <= 0.
        double time_divisor = 1e9; //1e9 is to convert to seconds, 1e6 to convert to ms
        
        int num_runs = 7;

        double[] SieveTime_seq = new double[7]; 
        double[] SieveTime_para = new double[7]; 
        double[] factorTime_seq = new double[7]; 
        double[] factorTime_para = new double[7]; 
        //running 7 times
        for (int run = 0; run < num_runs; run++){
            System.out.println("run number: " + run + " out of " + num_runs);
            double seqStart = System.nanoTime();
            SieveOfEratosthenes sieve_seq = new SieveOfEratosthenes(n);
            int[] primes_Seq = sieve_seq.getPrimes();
            double ElapsedTimeseq = (System.nanoTime() - seqStart)/time_divisor;

            //sieve_seq.printPrimes(primes_Seq);

            double paraStart = System.nanoTime();
            SievePara result = new SievePara(k, n);
            int[] primes_Para = result.getPrimes();
            double ElapsedTimepara = (System.nanoTime() - paraStart)/time_divisor;
            //sieve_seq.printPrimes(primes_Para);

            
            System.out.println("Time for finding primes. Time seq: " + ElapsedTimeseq + "  ,Time para: " + ElapsedTimepara);
            AreEqual(primes_Seq, primes_Para); //checking if the primes we found are equal


            //Now for factorization
            long N = n*((long) n);


            Oblig3Precode precodeSeq = new Oblig3Precode(n);
            Oblig3Precode precodePara = new Oblig3Precode(n);

            double ElapsedTimeseq_factor = 0;
            double ElapsedTimepara_factor = 0;
            
            long N_reduced = 0;
            //Going through 100 greatest values smaller than N
            for (int i = 1; i < 101; i++){
                N_reduced = N-i;

                double seqStart_factor = System.nanoTime();
                Factorize factorFinder = new Factorize(N_reduced, primes_Seq);
                ArrayList<Long> factors = factorFinder.getFactors();
                ElapsedTimeseq_factor += (System.nanoTime() - seqStart_factor)/time_divisor;
                checkSum(factors, N_reduced, "Sequential");


                
                double paraStart_factor = System.nanoTime();
                FactorizePara factorFinderPara = new FactorizePara(N_reduced, primes_Seq, k);
                ArrayList<Long> factorsPara = factorFinderPara.getFactors();
                ElapsedTimepara_factor += (System.nanoTime() - paraStart_factor)/time_divisor;
                checkSum(factorsPara, N_reduced, "Parallel");

                //writing to file
                if (run == num_runs - 1){
                    //For Sequential
                    for (long factor: factors){
                        precodeSeq.addFactor(N_reduced, factor);
                    }
                    precodeSeq.writeFactors("Sequential");


                    //For Parallell
                    for (long factor: factorsPara){
                        precodePara.addFactor(N_reduced, factor);
                    }
                    precodePara.writeFactors("Parallel");
                }
            }

            System.out.println("Time for factorization. Time seq: " + ElapsedTimeseq_factor + "  ,Time para: " + ElapsedTimepara_factor);
            System.out.println("");

            SieveTime_seq[run] = ElapsedTimeseq;
            SieveTime_para[run] = ElapsedTimepara;
            factorTime_seq[run] = ElapsedTimeseq_factor;
            factorTime_para[run] = ElapsedTimepara_factor;

        }
        
        Arrays.sort(SieveTime_seq);
        Arrays.sort(SieveTime_para);
        Arrays.sort(factorTime_seq);
        Arrays.sort(factorTime_para);

        System.out.println("After 7 runs on n = " + n + ", we got these median times: ");
        System.out.println("Sieve sequential: " + SieveTime_seq[3] + ", Sieve parallel: " + SieveTime_para[3]);
        System.out.println("Factor sequential: " + factorTime_seq[3] + ", Factor parallel: " + factorTime_para[3]);
    }

    //method for checking if two lists are equal, we use this to check our primes found in sequential and parallel.
    public static int AreEqual(int[] primes_Seq, int[] primes_Para){
        int length = primes_Seq.length;
        for (int i=0; i<length; i++){
            if (primes_Seq[i] != primes_Para[i]){
                System.out.println("not equal");
                //System.out.println(primes_Para[i] + " " + primes_Seq[i]);
                return 0;
            }
        }
        return 0;
    }

    //method for checking if our factors summed up equals the number we started with.
    public static void checkSum(ArrayList<Long> factors, long numb, String method){
        long temp = 1;
        for (int i = 0; i < factors.size(); i++){
            temp *= factors.get(i);
        }
        if (temp != numb){
            System.out.println(method + ". Not same. Original number was: " + numb + ", we got: " + temp);
        }
    }
  
}