import java.util.concurrent.*;
import java.util.Arrays; //for printing
import java.util.Collections;

import java.io.IOException;

import java.io.FileWriter; 

//javac Oblig2.java; java Oblig2 to compile
public class Oblig2 {
    //static int cores = 6; //setting amount of cores we want to use
    static int cores = Runtime.getRuntime().availableProcessors(); //using max number of cores available
    static int seed = 154; //Setting a seed to reproduce results
    static double[][] A;
    static double[][] B;
    static double[][] C;

    static double [][] TimeArray = new double[6][4]; //array to store the different time usages  

    static int[] n_list = {100, 200, 500, 1000}; //list of number of different matrix sizes we want to test for

    static int iteration = -1; //keep track of which n we are at when storing the times
    public static void main(String[] args){
        System.out.println("Cores used: " + cores);
        for (int n: n_list){
            iteration += 1;
            A = Oblig2Precode.generateMatrixA(seed, n);
            B = Oblig2Precode.generateMatrixB(seed, n);
            C = new double[n][n];
            System.out.println("n is: " + n);
            //checkFunctions(A, n); //test to see if AreEqual and transpose works (they do :) )

            //TimeTestSequential(A, B, C, n); //time test for only sequential methods

            TimeTestParallel(A, B, C, n, TimeArray); //Function which checks times for all parallel and sequential methods
        }
        //Writing our times to file
        try {
            FileWriter myWriter = new FileWriter("Times.txt");
            for (int i = 0; i<6; i++){
                for (int j=0; j<4; j++){
                    myWriter.write(Double.toString(TimeArray[i][j]) + " ");
                }
                myWriter.write("\n");
            }
            myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
    }

    /**
     * Method to check if two arrays (A and B) are equal 
     * @param A: matrix to check 
     * @param A: matrix to check
     * @param n: size of the matrices
     * @return 0, so we can end the function call early if there is a difference.
     */
    public static int AreEqual(double[][] A, double[][] B, int n){
        double lmbda = 1e-6;
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                if (A[i][j] - B[i][j] > lmbda){
                    System.out.println("not equal");
                    return 0;
                }
            }
        }
        return 0;
    }

    /**
     * Method for transposing a input matrix. Will not overwrite given input matrix
     * @param Arr array we want to transpose
     * @param n size of array
     * @return a new array which is the transposed of the input array
     */
    public static double[][] transpose(double[][] Arr, int n){
        double[][] temp = new double[n][n];
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                temp[i][j] = Arr[j][i];
            }
        }
        return temp;
    }

    /**
     * Method for checking if the transpose and AreEqual method work
     * @param A any matrix created randomly
     * @param n size of matrix
     */
    public static void checkFunctions(double[][] A, int n){
        double[][] A_t = transpose(A, n);
        AreEqual(A, transpose(A_t,n), n); //check to see if transposed works
        AreEqual(A, A_t, n); //check to see if AreEqual method works
    }

    /**
     * Method for classic implementation of matrix multiplication
     * Want result C = A*B
     * @param A matrix to multiply
     * @param B matrix to multiply
     * @param C matrix to store results
     * @param n size of the matrices
     */
    public static void MultiplyClassic(double[][] A, double[][] B, double[][] C, int n){
        for(int i=0;i<n;i++)
			for(int j=0;j<n;j++)
				for(int k=0;k<n;k++)
					C[i][j] += A[i][k] * B[k][j];
    }

    /**
     * Method for modified version of matrix multiplication, where we transpose B first
     * @param A Matrix to multiply
     * @param B Matrix to multiply
     * @param C Matrix to store results
     * @param n Size of the matrices
     */
    public static void MultiplyTransposeB(double[][] A, double[][] B, double[][] C, int n){
        double[][] B_t = transpose(B, n);
        for(int i=0;i<n;i++)
			for(int j=0;j<n;j++)
				for(int k=0;k<n;k++)
					C[i][j] += A[i][k] * B_t[j][k];
    }

    /**
     * Method for modified version of matrix multiplication, where we transpose A first
     * @param A Matrix to multiply
     * @param B Matrix to multiply
     * @param C Matrix to store results
     * @param n Size of the matrices
     */
    public static void MultiplyTransposeA(double[][] A, double[][] B, double[][] C, int n){
        double[][] A_t = transpose(A, n);
        for(int i=0;i<n;i++)
			for(int j=0;j<n;j++)
				for(int k=0;k<n;k++)
					C[i][j] += A_t[k][i] * B[k][j];
    }

    /**
     * Method for testing if our sequential algorithms work as intended, 
     * and get a time test of them.
     * @param A Matrix used for multiplication.
     * @param B Matrix used for multiplication.
     * @param C Matrix used for storing values.
     * @param n Size of matrices.
     */
    public static void TimeTestSequential(double[][] A, double[][] B, double[][] C, int n){
        
        double time_divisor = 1e9; //1e9 is to convert to seconds, 1e6 to convert to ms

        double ClassicStart = System.nanoTime();
        MultiplyClassic(A, B, C, n); //classic algorithm
        double ClassicElapsedTime = (System.nanoTime() - ClassicStart)/time_divisor;

        double[][] C_B = new double[n][n];
        double[][] C_A = new double[n][n];

        double TransposeBStart = System.nanoTime();
        MultiplyTransposeB(A, B, C_B, n);
        double TransposeB_ElapsedTime = (System.nanoTime() - TransposeBStart)/time_divisor;

        double TransposeAStart = System.nanoTime();
        MultiplyTransposeA(A, B, C_A, n);
        double TransposeA_ElapsedTime = (System.nanoTime() - TransposeAStart)/time_divisor;

        System.out.println("Classic time: " + ClassicElapsedTime + " seconds");
        System.out.println("Transpose B time: " + TransposeB_ElapsedTime + " seconds");
        System.out.println("Transpose A time: " + TransposeA_ElapsedTime + " seconds");
        System.out.println("Speedup classic to transpose B: " + (ClassicElapsedTime/TransposeB_ElapsedTime));
        System.out.println("Speedup transpose A to transpose B: " + (TransposeA_ElapsedTime/TransposeB_ElapsedTime));
        System.out.println("\n");


        AreEqual(C, C, n);
        AreEqual(C, C_B, n);
        AreEqual(C, C_A, n);
    }

    /**
     * Parallel implementation of our classic matrix multiplication method.
     * @param A Matrix to multiply
     * @param B Matrix to multiply
     * @param C Matrix to store results
     * @param n Size of the matrices
     * @param offset Change in where to start our outer indice (for parallel implementation)
     * @param extra_start Extra index as start value
     * @param extra_end Extra end index + n/cores
     */
    public static void MultiplyClassicPara(double[][] A, double[][] B, double[][] C, int n, int offset, int extra_start, int extra_end){
        for(int i=offset + extra_start;i<offset + extra_end;i++)
            for(int j=0;j<n;j++)
                for(int k=0;k<n;k++)
                    C[i-offset-extra_start][j] += A[i][k] * B[k][j];
    }

    /**
     * Parallel implementation of our matrix multiplication method where we transpose B.
     * @param A Matrix to multiply
     * @param B Matrix to multiply
     * @param C Matrix to store results
     * @param n Size of the matrices
     * @param offset Change in where to start our outer indice (for parallel implementation)
     * @param extra_start Extra index as start value
     * @param extra_end Extra end index + n/cores
     */
    public static void MultiplyTansposeBPara(double[][] A, double[][] B, double[][] C, int n, int offset, int extra_start, int extra_end){
        double[][] B_t = transpose(B, n);
        for(int i=offset + extra_start;i<offset + extra_end;i++)
            for(int j=0;j<n;j++)
                for(int k=0;k<n;k++)
                    C[i-offset-extra_start][j] += A[i][k] * B_t[j][k];
    }
    
    /**
     * Parallel implementation of our matrix multiplication method where we transpose A.
     * @param A Matrix to multiply
     * @param B Matrix to multiply
     * @param C Matrix to store results
     * @param n Size of the matrices
     * @param offset Change in where to start our outer indice (for parallel implementation)
     * @param extra_start Extra index as start value
     * @param extra_end Extra end index + n/cores
     */
    public static void MultiplyTansposeAPara(double[][] A, double[][] B, double[][] C, int n, int offset, int extra_start, int extra_end){
        double[][] A_t = transpose(A, n);
        for(int i=offset + extra_start;i<offset + extra_end;i++)
            for(int j=0;j<n;j++)
                for(int k=0;k<n;k++)
                    C[i-offset-extra_start][j] += A_t[k][i] * B[k][j];
    }
    
    /**
     * Method for testing if our parallel implementations work as intended, 
     * and time testing them.
     * @param A Matrix used for multiplication.
     * @param B Matrix used for multiplication.
     * @param C Matrix used to store results.
     * @param n Size of the matrices.
     * @param TimeArray Array to store our time usages in.
     */
    public static void TimeTestParallel(double[][] A, double[][] B, double[][] C, int n, double [][] TimeArray){
        
        double time_divisor = 1e9; //1e9 is to convert to seconds, 1e6 to convert to ms

        double[][] C_para = new double[n][n];
        double[][] C_B_para = new double[n][n];
        double[][] C_A_para = new double[n][n];
        

        double ClassicStartPara = System.nanoTime();
        InitializeParallell(A, B, C_para, n, "1");
        double ClassicElapsedTimePara = (System.nanoTime() - ClassicStartPara)/time_divisor;


        double TransposeBStartPara = System.nanoTime();
        InitializeParallell(A, B, C_B_para, n, "2");
        double TransposeB_ElapsedTimePara = (System.nanoTime() - TransposeBStartPara)/time_divisor;

        double TransposeAStartPara = System.nanoTime();
        InitializeParallell(A, B, C_A_para, n, "3");
        double TransposeA_ElapsedTimePara = (System.nanoTime() - TransposeAStartPara)/time_divisor;

        System.out.println("Classic time Parallel: " + ClassicElapsedTimePara + " seconds");
        System.out.println("Transpose B time Parallel: " + TransposeB_ElapsedTimePara + " seconds");
        System.out.println("Transpose A time Parallel: " + TransposeA_ElapsedTimePara + " seconds");

        double ClassicStart = System.nanoTime();
        MultiplyClassic(A, B, C, n); //classic algorithm
        double ClassicElapsedTime = (System.nanoTime() - ClassicStart)/time_divisor;

        double[][] C_B = new double[n][n];
        double[][] C_A = new double[n][n];

        double TransposeBStart = System.nanoTime();
        MultiplyTransposeB(A, B, C_B, n);
        double TransposeB_ElapsedTime = (System.nanoTime() - TransposeBStart)/time_divisor;

        double TransposeAStart = System.nanoTime();
        MultiplyTransposeA(A, B, C_A, n);
        double TransposeA_ElapsedTime = (System.nanoTime() - TransposeAStart)/time_divisor;

        System.out.println("");
        System.out.println("Classic time: " + ClassicElapsedTime + " seconds");
        System.out.println("Transpose B time: " + TransposeB_ElapsedTime + " seconds");
        System.out.println("Transpose A time: " + TransposeA_ElapsedTime + " seconds");

        System.out.println("");
        System.out.println("Speedup classic: " + (ClassicElapsedTime/ClassicElapsedTimePara));
        System.out.println("Speedup transpose B: " + (TransposeB_ElapsedTime/TransposeB_ElapsedTimePara));
        System.out.println("Speedup transpose A: " + (TransposeA_ElapsedTime/TransposeA_ElapsedTimePara));
        System.out.println("Speedup between sequential classic and parallel with transpose B: " + (ClassicElapsedTime/TransposeB_ElapsedTimePara));
        System.out.println("\n");

        AreEqual(C, C_para, n);
        AreEqual(C, C_B_para, n);
        AreEqual(C, C_A_para, n);

        TimeArray[0][iteration] = ClassicElapsedTime;
        TimeArray[1][iteration] = TransposeB_ElapsedTime;
        TimeArray[2][iteration] = TransposeA_ElapsedTime;

        TimeArray[3][iteration] = ClassicElapsedTimePara;
        TimeArray[4][iteration] = TransposeB_ElapsedTimePara;
        TimeArray[5][iteration] = TransposeA_ElapsedTimePara;


    }

    /**
     * Class for our parallel program
     */
    static class MaxThread implements Runnable {
        int id;
        int n; 
        double[][] A;
        double[][] B;
        double[][] C;

        public int offset;
        public int extra_start;
        public int extra_end;

        String method;

        /**
         * Method for initializing a new thread to start working.
         * @param t thread id.
         * @param A Matrix we want to multiply.
         * @param B Matrix we want to multiply.
         * @param n Size of the matrices.
         * @param method Which of the 3 matrix multiplication methods we want to use.
         */
        MaxThread(int t, double[][] A, double[][] B, int n, String method) {
            this.id = t;
            this.n = n;
            this.A = A;
            this.B = B;
            this.method = method;

        }

        public void run(){
            offset = n/cores * id;
            int rest = n - n/cores*cores;
            extra_start = 0;
            extra_end = 0;
            for (int i=0; i<rest; i++){ //method for cases where n/cores is not a whole number
                if (id == i){
                    extra_start = i;
                    extra_end = i+1;
                }
            }
            if (id >= rest){
                extra_start = extra_end = rest;
            }
            C = new double[n/cores - extra_start + extra_end][n];

            //We do this since we need this in the loops for later, so we dont have to send
            //n/cores speratly aswell, easier with less input variables.
            extra_end += n/cores; 
            
            //Quick method to check if the work is divided equally, keep unmarked unless you want to manually 
            //look at it and see the results.
            //System.out.println(id + " " + (offset + extra_start) + " " + (offset + extra_end));

            //Choosing which method we use
            if (method == "1")
                MultiplyClassicPara(A, B, C, n, offset, extra_start, extra_end);
            
            else if (method == "2")
                MultiplyTansposeBPara(A, B, C, n, offset, extra_start, extra_end);

            else if (method == "3")
                MultiplyTansposeAPara(A, B, C, n, offset, extra_start, extra_end);
            
            //using classic if nothing else is specified
            else
                MultiplyClassicPara(A, B, C, n, offset, extra_start, extra_end);
        }
        //To return our C matrix.
        public double[][] getC(){
            return C;
        }

    }
    
    /**
     * Method for Initializing our parallel program
     * @param A Matrix we want to multiply.
     * @param B Matrix we want to multiply.
     * @param C Matrix to store results in.
     * @param n Size of the matrices.
     * @param method Which matrix multiplication method we want to use, where
     * 1 is classic, 2 is transpose B and 3 is transpose A.
     */
    public static void InitializeParallell(double[][] A, double[][] B, double[][] C, int n, String method) {

        Thread[] threads = new Thread[cores];
        MaxThread[] workers = new MaxThread[cores];

        for (int t=0; t<cores; t++){
            workers[t] = new MaxThread(t, A,B,n, method);
            threads[t] = new Thread(workers[t]);
            threads[t].start();
        }
        int offset;
        int extra_start;
        int extra_end;
        double[][] tempC;
        //Joining the threads when they all finished their work.
        for (int t=0; t<cores; t++){
            try {
                threads[t].join();
                tempC = workers[t].getC(); //storing the results in temporary matrix
                offset = workers[t].offset;
                extra_start = workers[t].extra_start;
                extra_end = workers[t].extra_end;
                //Storing all the parallel threads results in one single matrix
                for (int i=offset + extra_start; i<offset + extra_end; i++)
                    for(int j=0;j<n;j++)
                        C[i][j] = tempC[i - extra_start - offset][j];

            } catch (Exception e) {
                System.out.println("Did not work");
            }
        }
    }
}


