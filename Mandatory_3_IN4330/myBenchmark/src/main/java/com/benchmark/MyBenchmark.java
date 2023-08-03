/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.benchmark;

import org.openjdk.jmh.annotations.*;
import java.util.ArrayList;


import com.SieveOfEratosthenes;
import com.SievePara;
import com.Factorize;
import com.FactorizePara;

public class MyBenchmark {


    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 3)
    public int[] testSieve() {
        //Method for benchmark testing sequential Sieve
        int n = 2000000000;
        int k = 4;
        SieveOfEratosthenes sieve_seq = new SieveOfEratosthenes(n);
        int[] primes_Seq = sieve_seq.getPrimes();
        //SievePara result = new SievePara(k, n);
        //int[] primes_Para = result.getPrimes();
        return primes_Seq;
    }

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 3)
    public int[] testSievePar()throws InterruptedException {
        //Method for benchmark testing parallel Sieve
        int n = 2000000000;
        int k = 4;
        SievePara result = new SievePara(k, n);
        int[] primes_Para = result.getPrimes();
        return primes_Para;
    }

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 3)
    public int testFactorize(){
        //Method for benchmark testing sequential factorization
        int n = 2000000000;
        SieveOfEratosthenes sieve_seq = new SieveOfEratosthenes(n);
        int[] primes_Seq = sieve_seq.getPrimes();

        long N = n*((long) n);
        long N_reduced = 0;
        for (int i = 1; i < 100; i++){
            N_reduced = N-i;
            Factorize factorFinder = new Factorize(N_reduced, primes_Seq);
            ArrayList<Long> factors = factorFinder.getFactors();
        }

        return 0;
    }

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 3)
    public int testFactorizePara()throws InterruptedException{
        //Method for benchmark testing parallel factorization
        int n = 2000000000;
        SieveOfEratosthenes sieve_seq = new SieveOfEratosthenes(n);
        int[] primes_Seq = sieve_seq.getPrimes();

        long N = n*((long) n);
        long N_reduced = 0;
        int k = 4;
        for (int i = 1; i < 100; i++){
            N_reduced = N-i;
            FactorizePara factorFinderPara = new FactorizePara(N_reduced, primes_Seq, k);
            ArrayList<Long> factors = factorFinderPara.getFactors();
        }

        return 0;
    }


}
