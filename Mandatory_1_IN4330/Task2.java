import java.util.concurrent.*;
import java.util.Random; 
import java.util.Arrays; //for printing
import java.util.Collections;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

class MaxValueParallell {
    static int cores = 6; //setting amount of cores we want to use

    //initializing arrays to store our values
    static double[] k20_smart = new double[6];
    static double[] k100_smart = new double[6];
    
    //used to store all 7 different iterations to find median later
    static double[][] k20_smart_temp = new double[6][7];
    static double[][] k100_smart_temp = new double[6][7];

    //different k values we want to test for
    static int [] kVals = {20, 100};

    static int iteration; //iteration counter for storing values in our arrays, keeps track of which n we are at
    static Random rd = new Random(543);
    static int z; //variable to keep track of which run we are at

    public static void main(String[] args) {        
        for (z = 0; z<7; z++){
            System.out.println("iteration: " + z);
            for (int k: kVals){
                iteration = 0;
                System.out.println("k is: " + k);
                for (int n = 1000; n<1000000000; n=n*10){
                    System.out.println("n is: " + n);
                    compare(n, k);
                    iteration += 1;
                }
            }
        }
        //Writing to file
        for (int k: kVals){
            if (k == 20){
                try {
                    for (z = 0; z<6; z++){
                        Arrays.sort(k20_smart_temp[z]);
                        k20_smart[z] = k20_smart_temp[z][3];
                    }
                    String filename = "C:\\Users\\erlen\\Documents\\UiO\\Master\\Semester 2\\IN3030\\Obliger\\Oblig1\\k20_parallell.txt";
                    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
                    outputStream.writeObject(Arrays.toString(k20_smart));
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    for (z = 0; z<6; z++){
                        Arrays.sort(k100_smart_temp[z]);
                        k100_smart[z] = k100_smart_temp[z][3];
                    }
                    String filename = "C:\\Users\\erlen\\Documents\\UiO\\Master\\Semester 2\\IN3030\\Obliger\\Oblig1\\k100_parallell.txt";
                    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
                    outputStream.writeObject(Arrays.toString(k100_smart));
                }
                catch(IOException e) {
                    e.printStackTrace();
                }   
            }         
        }  
    }

    public static void compare(int n, int k) {

        int[] array = new int[n];
        for (int i = 0; i < n; i++){
            array[i] = rd.nextInt();
        }

        //making a copy of the array
        int[] array_copy = new int[n];
        System.arraycopy(array, 0, array_copy, 0, n);


        //timing smart implementation//
        double smartStart = System.nanoTime();
        int[] kArray = InitializeParallell(array_copy, n, k);
        double smartElapsedTime = (System.nanoTime() - smartStart)/1000000;
        //System.out.println("Smart solution is: " + Arrays.toString(kArray) + " elapsed time " + smartElapsedTime); //used when wanting to see solution
        System.out.println("Elapsed time " + smartElapsedTime);

        
        //Used for testing if they are the same
        /*System.arraycopy(array, 0, array_copy, 0, n);
        Arrays.sort(array_copy);
        System.out.print("Naive solution is: "); 
        printKReversed(array_copy, k, n);
        System.out.print("\n");*/
            
        //storing the times
        if (k == 20){
            k20_smart_temp[iteration][z] = smartElapsedTime;
        }
        else {
            k100_smart_temp[iteration][z] += smartElapsedTime;
        }
    }

    //method for printing our k first elements
    public static void printK(int [] a, int k){
        for (int i = 0; i < k; i++){
            System.out.print(a[i] + " ");
        }
    }

    //method for printing our k last elements of list, starting with the last element
    public static void printKReversed(int [] a, int k, int n){
        for (int i = n-1; i > n-k-1; i--){
            System.out.print(a[i] + " ");
        }
    }

    //Insert sort made to sort descending
    public static void insertSort (int[] a, int v, int h) {
        int i, t;
        for (int k = v; k < h; k++) {
            // invariant: a [v..k] is now sorted ascending (smallest first)
            t = a[k + 1];
            i = k;
            while (i >= v && a[i] < t) {
                a[i + 1] = a[i];
                i--;
            }
            a[i + 1] = t;
        } // than for k
    } // end insertSort

    //Sequential algorithm for finding k largest 
    //n is size of array and k is amount of largest elements we want to find
    public static int[] kLargest(int[] array, int n, int k){
        int[] kArray = new int[k];
        for (int i = 0; i<k; i++){
            kArray[i] = array[i];
        }
        insertSort(kArray, 0, k-1); //sorting first k elements 
        for (int i=k; i<n; i++){
            if (array[i] > kArray[k-1]){
                kArray[k-1] = array[i];
                insertSort(kArray, 0, k-1);
            }
        }

        return kArray;
    }

    //Method for finding kLargest parallelized 
    //n is size of array, k is number elements we want to find
    //offset, extra_start and extra_end are found depending on number of cores and size of array
    public static int[] kLargestPara(int[] array, int n, int k, int offset, int extra_start, int extra_end){
        int[] kArray = new int[k];
        for (int i = 0; i<k; i++){
            kArray[i] = array[i+offset+extra_start];
        }
        insertSort(kArray, 0, k-1); //sorting first k elements 
        for (int i=k+offset+extra_start; i<n+offset+extra_end; i++){
            if (array[i] > kArray[k-1]){
                kArray[k-1] = array[i];
                insertSort(kArray, 0, k-1);
            }
        }

        return kArray;
    }

    //Class for parallelizing our code
    static class MaxThread implements Runnable {
        int id;
        int n; 
        int k;
        int[] array;
        int[] kArray;
        MaxThread(int t, int n, int[] array, int k) {
            this.id = t;
            this.n = n;
            this.array = array;
            this.k = k;
        }

        public void run(){
            int offset = n/cores * id;
            int rest = n - n/cores*cores;
            int extra_start = 0;
            int extra_end = 0;
            for (int i=0; i<rest; i++){ //method for cases where n/cores is not a whole number
                if (id == i){
                    extra_start = i;
                    extra_end = i+1;
                }
            }
            if (id >= rest){
                extra_start = extra_end = rest;
            }
            //System.out.println()
            kArray = kLargestPara(array, n/cores, k, offset, extra_start, extra_end);
        }

        public int[] getkArray(){
            return kArray;
        }
    }
    
    public static int[] InitializeParallell(int[] array, int n, int k) {
        //int max = Integer.MIN_VALUE;

        Thread[] threads = new Thread[cores];
        MaxThread[] workers = new MaxThread[cores];

        for (int t=0; t<cores; t++){
            workers[t] = new MaxThread(t, n, array, k);
            threads[t] = new Thread(workers[t]);
            threads[t].start();
        }

        int[] maxValues = new int[cores*k];
        int[] tempkArr;
        for (int t=0; t<cores; t++){
            try {
                threads[t].join();
                tempkArr = workers[t].getkArray();
                for (int i=0; i<k; i++){
                    maxValues[t*k + i] = tempkArr[i];
                }
                
            } catch (Exception e) {
                System.out.println("Did not work");
            }
        }

        return kLargest(maxValues, cores*k ,k);
    }

}