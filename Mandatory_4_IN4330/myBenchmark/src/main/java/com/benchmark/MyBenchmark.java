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

//import org.openjdk.jmh.annotations.Benchmark;

import org.openjdk.jmh.annotations.*;
//import java.util.ArrayList;

import com.CH;
import com.CH_PARA;
import com.IntList;
import com.NPunkter17;

public class MyBenchmark {

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 3)
    public IntList testCH() {
        //Method for benchmark testing sequential Sieve
        int n = 10000000;
        int seed = 100;
        int[] x = new int[n];
        int[] y = new int[n];

        NPunkter17 punkter = new NPunkter17(n, seed); //99, 100 and 101 good seeds to test cases
        punkter.fyllArrayer(x, y);
        CH convex = new CH(n, x, y);
        IntList convexHull_seq = convex.calculate();
        //SievePara result = new SievePara(k, n);
        //int[] primes_Para = result.getPrimes();
        return convexHull_seq;
    }

    
    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 3)
    public IntList testCH_para()throws InterruptedException {
        //Method for benchmark testing sequential Sieve
        int n = 10000000;
        int k = 8;
        int seed = 100;
        int[] x = new int[n];
        int[] y = new int[n];

        NPunkter17 punkter = new NPunkter17(n, seed); //99, 100 and 101 good seeds to test cases
        punkter.fyllArrayer(x, y);
        CH_PARA convex_para = new CH_PARA(n, x, y);
        IntList convexHull_para = convex_para.calculatePara(k, true);
        IntList convexHull_para_down = convex_para.calculatePara(k, false);
        convexHull_para.append(convexHull_para_down);
        return convexHull_para;
    }

}

class generate{

    generate(int n, int seed){
        int[] x = new int[n];
        int[] y = new int[n];

        NPunkter17 punkter = new NPunkter17(n, seed); //99, 100 and 101 good seeds to test cases
        punkter.fyllArrayer(x, y);
    }
}