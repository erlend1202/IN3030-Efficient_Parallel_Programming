package com;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;


/*
 * Class to make a parallel version of the convex hull algorithm
 * Inputs needed are:
 * int n: number of points
 * int[] x: list of x coordinates for the points.
 * int[] y: list of y coordinates for the points.
 * 
 */
public class CH_PARA {
    private List<Thread> threads; // list of threads
    Monitor monitor; //monitor class, tracks available work

    int n;
    int[] x;
    int[] y;
    int MAX_X;
    int MIN_X;
    int MAX_Y;

    IntList convexHull;
    IntList all_values;

    IntList convexHull_local;

    public CH_PARA(int n, int[] x, int[] y){
        CH convex = new CH(n, x, y); //reusing code from sequential algorithm, easier this way
        this.convexHull = new IntList();
        this.all_values = convex.all_values;
        this.x = convex.x;
        this.y = convex.y;
        this.MAX_X = convex.MAX_X;
        this.MIN_X = convex.MIN_X;
        this.MAX_Y = convex.MAX_Y;

        this.threads = new ArrayList<>(); // initialize list of threads
        this.monitor = new Monitor();

    }

    //method so i can make a private class for each thread
    public CH_PARA(int[] x, int[] y){
        this.x = x;
        this.y = y;
        this.convexHull_local = new IntList();
    }


    //keep track of recursive depth
    class Depth{
        public int val;

        Depth(){
            this.val = 0;
        }
    }

    /*
     * method to calculate convex hull.
     * int k: number of threads we want to use. 
     * boolean Above: statement to wether or not we work on top or bottom half circle
     */
    public IntList calculatePara(int k, boolean Above)throws InterruptedException{
        this.convexHull = new IntList();
        StoredValues start_store = new StoredValues(0,0, new IntList(), 0, 0);

        if (Above){
            this.convexHull.add(this.MAX_X);
            start_store = recursivePARA(this.MIN_X, this.MAX_X, this.all_values);
        }
        else{
            this.convexHull.add(this.MIN_X);
            start_store = recursivePARA(this.MAX_X, this.MIN_X, this.all_values);
        }
        //sending work to start of with.
        monitor.sendWork(start_store.max_idx, start_store.right, start_store.previous, start_store.max, start_store.max_idx);
        monitor.sendWork(start_store.left, start_store.max_idx, start_store.previous, start_store.max, start_store.max_idx);
        
        this.convexHull.add(start_store.max_idx);
        ReentrantLock lock = new ReentrantLock();

        //Making threads and giving work
        for (int i = 0; i < k; i++){
            int rank = i; //for debugging, to see which thread nr it is.
            Thread thread = new Thread(() -> {
                StoredValues completedWork;
                StoredValues fetchedWork;

                //keep track of depth
                Depth depth = new Depth();
                
                //local method so we can do local calls for each thread
                CH_PARA local = new CH_PARA(this.x, this.y);

                //while there is either work available, or ongoing work, the threads are held in the loop
                while (monitor.AvailableWork > 0 || monitor.OngoingWork > 0){
                    //locking so only one thread can get work at the time
                    lock.lock();
                    if (monitor.AvailableWork > 0){
                        fetchedWork = monitor.askForWork(); //fetching work
                        lock.unlock(); //unlocking, so next thread can fetch work
                        
                        //doing fetched work
                        completedWork = local.recursivePARA(fetchedWork.left, fetchedWork.right, fetchedWork.previous);
                        depth.val += 1; //keeping track of recursive depth
                        
                        //while loop to keep thread working and sending work as long as it hasn't hit a certain 
                        //level of recursive depth.
                        while (completedWork.max > 0 && depth.val < (k-1)/2){
                                local.convexHull_local.add(completedWork.max_idx);
                                lock.lock();
                                monitor.sendWork(completedWork.max_idx, completedWork.right, completedWork.previous, completedWork.max, completedWork.max_idx);
                                lock.unlock();  
                                completedWork = local.recursivePARA(completedWork.left, completedWork.max_idx, completedWork.previous);
                                depth.val += 1;
                        }
                        //if recursive depth of (k-1)/2 was hit, the thread stops sending work and does the rest by itself
                        if (completedWork.max > 0){
                            local.recursive(completedWork.max_idx, completedWork.right, completedWork.previous);
                            local.convexHull_local.add(completedWork.max_idx);
                            local.recursive(completedWork.left, completedWork.max_idx, completedWork.previous);
                        }
                        //here we find all points on a line
                        //this is relatively fast so i chose not to parallelize it
                        if (completedWork.max == 0){
                            local.recursive_line(completedWork.max_idx, completedWork.right, completedWork.previous);
                            local.convexHull_local.add(completedWork.max_idx);
                            local.recursive_line(completedWork.left, completedWork.max_idx, completedWork.previous);
                        }
                        //sending to monitor that work has been completed
                        monitor.doneWithWork();
                    }
                    //if there was no work available, we unlock (just in case some might be stuck before the if statement or something)
                    else{
                        lock.unlock();
                    }
                }
            
            //sending work to main class one at a time
            lock.lock();
            this.convexHull.append(local.convexHull_local);
            lock.unlock();    

            });
            threads.add(thread); // add thread to list of threads
            thread.start(); // start thread
        }
        for (Thread thread : threads) {
            thread.join(); // wait for thread to finish
        }

        this.convexHull = sorter(this.convexHull); //sorting the points we found.
        return this.convexHull;
    }


    /*
     * Sorter for our parallel solution
     * It starts with either MAX_X or MIN_X depending which half circle we work with.
     * Then, it finds the point closest and adds this point to a new list, then repeats for this point.
     * 
     * Will go through convex hull points Sum_(i=0)^(i=n) (n-i) number of times, so n(n+1)/2 times, so O(n^2).
     * Should in theory by very fast, since the number of points it goes through (n) is approx 1.4*sqrt(N),
     * where N is all original points.
     */
    public IntList sorter(IntList ch){
        int length = ch.size();
        //makes an arraylist so we can remove elements while sorting
        ArrayList<Integer> data_new = new ArrayList<>(length);
        for (int i = 0; i < length; i++){
            data_new.add(ch.get(i)); 
        }
        

        IntList sorted = new IntList();
        int new_val = data_new.get(0);
        data_new.remove(0);
        sorted.add(new_val);

        double d;
        int idx;
        double min;
        int min_idx = -1;
        while (sorted.size() != length){
            min = Double.MAX_VALUE;
            for (int i = 0; i < data_new.size(); i++){
                idx = data_new.get(i);
                d = calcDistancePoints(idx, new_val);
                if (d < min){
                    min = d;
                    min_idx = i;
                }
            }
            new_val = data_new.get(min_idx);
            data_new.remove(min_idx);
            sorted.add(new_val);

        }
        
        return sorted;
    }

    //method to calculate distance from point to point
    public double calcDistancePoints(int left, int right){
        int y1 = this.y[left];
        int y2 = this.y[right];
        int x1 = this.x[left];
        int x2 = this.x[right];

        double d = (x2 - x1)*(x2-x1) + (y2-y1)*(y2-y1);
        return d;   
    }
    
    //recursive method that returns the values stored in a class
    public StoredValues recursivePARA(int left, int right, IntList previous){
        int length = previous.size();
        IntList current = new IntList();

        int y1 = this.y[left];
        int y2 = this.y[right];
        int x1 = this.x[left];
        int x2 = this.x[right];

        int a = y1 - y2;
        int b = x2 - x1;
        int c = y2*x1 - y1*x2;

        int max = (-1)*Integer.MAX_VALUE;
        int max_idx = -1;
        int d;
        int idx;
        for (int i = 0; i < length; i++){
            idx = previous.get(i);
            d = calcDistance(a, b, c, idx);
            if (d >= 0 && idx != right && idx != left){ 
                current.add(idx);
                if (d > max){
                    max = d;
                    max_idx = idx;
                }
            }
        }
        StoredValues new_store = new StoredValues(left, right, current, max, max_idx);
        return new_store;
    }

    /*
     * Recursive algorithm to find convex hull
     * int left: index for leftmost point (x1, y1)
     * int right: index for rightmost point (x2, y2)
     * IntList previous: List if points which are relevant to check.
     * 
     * Function will fill this.convexHull which is a IntList
     */
    
    public void recursive(int left, int right, IntList previous){
        int length = previous.size();
        IntList current = new IntList();

        int y1 = this.y[left];
        int y2 = this.y[right];
        int x1 = this.x[left];
        int x2 = this.x[right];

        int a = y1 - y2;
        int b = x2 - x1;
        int c = y2*x1 - y1*x2;

        int max = (-1)*Integer.MAX_VALUE;
        int max_idx = -1;
        int d;
        int idx;
        for (int i = 0; i < length; i++){
            idx = previous.get(i);
            d = calcDistance(a, b, c, idx);
            if (d >= 0 && idx != right && idx != left){ 
                current.add(idx);
                if (d > max){
                    max = d;
                    max_idx = idx;
                }
            }
        }

        if (max > 0){
            recursive(max_idx, right, current);
            this.convexHull_local.add(max_idx);
            recursive(left, max_idx, current);
        }
        
        if (max == 0){
            recursive_line(max_idx, right, current);
            this.convexHull_local.add(max_idx);
            recursive_line(left, max_idx, current);
        }

    }

    /*
     * Recursive algorithm to find all the points on a line.
     * takes same parameters as algorithm above, but checks if there exists a point on the line between left and right.
     */
    public void recursive_line(int left, int right, IntList previous){
        int length = previous.size();
        int y1 = this.y[left];
        int y2 = this.y[right];
        int x1 = this.x[left];
        int x2 = this.x[right];

        int max_x; int min_x;
        int max_y; int min_y;

        if (y1 > y2){
            max_y = y1;
            min_y = y2;
        }
        else{
            max_y = y2;
            min_y = y1;
        }

        if (x1 > x2){
            max_x = x1;
            min_x = x2;
        }
        else{
            max_x = x2;
            min_x = x1;
        }

        int idx;
        for (int i = 0; i < length; i++){
            idx = previous.get(i);
            //checking if it is between the two points
            if (max_x == min_x){
                if (this.y[idx] < max_y && this.y[idx] > min_y){
                    recursive_line(idx, right, previous);
                    this.convexHull_local.add(idx);
                    recursive_line(left,idx, previous);
                    break;
                }
            }
            if (max_y == min_y){
                if (this.x[idx] < max_x && this.x[idx] > min_x){
                    recursive_line(idx, right, previous);
                    this.convexHull_local.add(idx);
                    recursive_line(left,idx, previous);
                    break;
                }
            }
            
            else{
                if (this.x[idx] < max_x && this.x[idx] > min_x && this.y[idx] < max_y && this.y[idx] > min_y){
                    recursive_line(idx, right, previous);
                    this.convexHull_local.add(idx);
                    recursive_line(left,idx, previous);
                    break;
                }
            }
        }
    }

    //method to calculate distance from line to point
    public int calcDistance(int a, int b, int c, int idx){
        int x = this.x[idx];
        int y = this.y[idx];

        int d = a*x + b*y + c;
        return d;
    }

    public static void main(String[] args)throws InterruptedException{
        
    }

}


/*
 * Class used to store the values of: left, right, previous, max and max_idx
 * these are found from the recursive algorithm
 * The class is used so we can return all the values after the calculations are done.
 */
class StoredValues{
    int left;
    int right;
    IntList previous;
    
    int max;
    int max_idx;

    public StoredValues(int left, int right, IntList previous, int max, int max_idx){
        this.left = left;
        this.right = right;
        this.previous = previous;
        
        this.max = max;
        this.max_idx = max_idx;
    }
}

/*
 * A monitor class to keep track of the work available for the classes.
 * It sends out the left and right point we want to draw a line with, as well as 
 * the points relevant to check, and the max_idx and max distance found from previous done work.
 * It also keeps track of available work and ongoing work.
 */
class Monitor{
    int AvailableWork;
    int OngoingWork;
    IntList RIGHT_list;
    IntList LEFT_list;
    List<IntList> PREVIOUS_list; // list of threads
    IntList MAX_list;
    IntList MAX_IDX_list;

    public Monitor(){
        this.RIGHT_list = new IntList();
        this.LEFT_list = new IntList();
        this.PREVIOUS_list = new ArrayList<>();
        this.MAX_list = new IntList();
        this.MAX_IDX_list = new IntList();


        this.OngoingWork = 0;
        this.AvailableWork = 0;

    }

    //Here the monitor fetches work and stores it
    public void sendWork(int left, int right, IntList previous, int max, int max_idx){
        this.AvailableWork += 1;
        this.LEFT_list.add(left);
        this.RIGHT_list.add(right);
        this.PREVIOUS_list.add(previous);

        this.MAX_list.add(max);
        this.MAX_IDX_list.add(max_idx);
    }

    //here the monitor sends work
    public StoredValues askForWork(){
        int left = this.LEFT_list.getAndRemoveLast();
        int right = this.RIGHT_list.getAndRemoveLast();

        IntList previous = this.PREVIOUS_list.get(this.AvailableWork - 1);
        this.PREVIOUS_list.remove(this.AvailableWork - 1);

        int max = this.MAX_list.getAndRemoveLast();
        int max_idx = this.MAX_IDX_list.getAndRemoveLast();

        StoredValues new_store = new StoredValues(left, right, previous, max, max_idx);
        this.OngoingWork += 1;
        this.AvailableWork -= 1;
        return new_store;
    }   

    public void doneWithWork(){
        this.OngoingWork -= 1;
    }

}
