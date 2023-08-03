import java.util.concurrent.*;
import java.util.Random; 
import java.util.Arrays; //for printing
import java.util.Collections;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

class MaxValue {
    //making arrays to store our run-times
    static double[] k20_naive = new double[6];
    static double[] k100_naive = new double[6];

    static double[] k20_smart = new double[6];
    static double[] k100_smart = new double[6];
    
    static double[][] k20_smart_temp = new double[6][7];
    static double[][] k100_smart_temp = new double[6][7];
    static double[][] k20_naive_temp = new double[6][7];
    static double[][] k100_naive_temp = new double[6][7];


    static int [] kVals = {20, 100}; //number of k values to go thourhg
    static int iteration; //keeping track of what n we are at
    static Random rd = new Random(543);
    static int z; //keeping track of which run we are at

    //static int n = 100;
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
        //writing values of array to txt file
        for (int k: kVals){
            if (k == 20){
                try {
                    for (z = 0; z<6; z++){
                        Arrays.sort(k20_smart_temp[z]);
                        Arrays.sort(k20_naive_temp[z]);
                        k20_smart[z] = k20_smart_temp[z][3];
                        k20_naive[z] = k20_naive_temp[z][3];
                    }
                    String filename = "C:\\Users\\erlen\\Documents\\UiO\\Master\\Semester 2\\IN3030\\Obliger\\Oblig1\\k20.txt";
                    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
                    outputStream.writeObject(Arrays.toString(k20_naive));
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
                        Arrays.sort(k100_naive_temp[z]);
                        k100_smart[z] = k100_smart_temp[z][3];
                        k100_naive[z] = k100_naive_temp[z][3];
                    }
                    String filename = "C:\\Users\\erlen\\Documents\\UiO\\Master\\Semester 2\\IN3030\\Obliger\\Oblig1\\k100.txt";
                    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
                    outputStream.writeObject(Arrays.toString(k100_naive));
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


        double naiveStart = System.nanoTime();
        Arrays.sort(array_copy);
        double naiveElapsedTime = (System.nanoTime() - naiveStart)/1000000;


        //print the commented out lines if you want to test that the arrays are the same
        //System.out.println("Original array: " + Arrays.toString(array) + "\n");
        //System.out.print("Naive solution is: "); 
        //printKReversed(array_copy, k, n);
        System.out.println("Naive elapsed time " + naiveElapsedTime);


        //overwrite array_copy again//
        System.arraycopy(array, 0, array_copy, 0, n);

        //timing smart implementation//
        double smartStart = System.nanoTime();
        int[] kArray = kLargest(array_copy, n, k);
        double smartElapsedTime = (System.nanoTime() - smartStart)/1000000;
        //System.out.println("Smart solution is: " + Arrays.toString(kArray) + " elapsed time " + smartElapsedTime + "\n");
        System.out.println("Smart elapsed time " + smartElapsedTime + "\n");

        //storing our time values
        if (k == 20){
            k20_naive_temp[iteration][z] = naiveElapsedTime;
            k20_smart_temp[iteration][z] = smartElapsedTime;
        }
        else {
            k100_naive_temp[iteration][z] = naiveElapsedTime;
            k100_smart_temp[iteration][z] = smartElapsedTime;
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

}