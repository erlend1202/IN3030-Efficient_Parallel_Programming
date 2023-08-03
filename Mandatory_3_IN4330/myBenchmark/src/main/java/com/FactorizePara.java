package com;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;

public class FactorizePara {
    public ArrayList<Long> factors = new ArrayList<Long>();
    long n;
    int[] primes;
    private List<Thread> threads; // list of threads


    private final int k;

    public FactorizePara(long n, int[] primes, int k){
        this.n = n;
        this.primes = primes;
        this.k = k;
        this.threads = new ArrayList<>(); // initialize list of threads

    }

    class Value {
        public long value;
        Value (long set){
            this.value = set;
        }
    }

    public ArrayList<Long> getFactors()throws InterruptedException{
        int length = primes.length;
        final Value temp = new Value(n);
        //int temp = n;

        ReentrantLock lock = new ReentrantLock();
        for (int z = 0; z < k; z++){
            int stride = z;
            Thread thread = new Thread(() -> {
                int prime = 1;
                for (int i = stride; i < length; i+=k){
                    prime = primes[i];
                    if (temp.value%prime == 0){
                        lock.lock();
                        while (temp.value%prime == 0){
                            temp.value = temp.value/prime;
                            factors.add((long)prime);
                        }   
                        lock.unlock(); 
                    }
                    if (temp.value < prime){
                        //if (temp.value == prime) factors.add(prime);
                        break;
                    }
                }
            });
            threads.add(thread); // add thread to list of threads
            thread.start(); // start thread
        }
        for (Thread thread : threads) {
            thread.join(); // wait for thread to finish
        }
        if (temp.value != 1){
            factors.add(temp.value);
        }
        return factors;
    }

    public void printFactors(List<Long> factors){
        System.out.println("Parallel factorization:");
        for (long factor: factors){
            System.out.println(factor);
        }
    }
}