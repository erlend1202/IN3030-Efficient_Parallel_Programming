//javac CH.java; java CH <n> <seed> to compile
import java.util.Arrays;


/*
 * Class for making a convex hull.
 * Inputs needed are:
 * int n: number of points
 * int[] x: list of x coordinates for the points.
 * int[] y: list of y coordinates for the points.
 * 
 */
public class CH {

    int n;
    int[] x;
    int[] y;
    int MAX_X;
    int MIN_X;
    int MAX_Y;

    IntList convexHull = new IntList();
    IntList all_values = new IntList(n);


    //constructor, makes x and y arrays and finds min_y, min_x and max_x
    public CH(int n , int[]x, int[]y){  
        this.n = n;
        this.x = x;
        this.y = y;

        int max = 0; int max_idx = 0;
        int min = Integer.MAX_VALUE; int min_idx = 0;
        int maxy = 0; int maxy_idx = 0;
        //finding max_x, max_y and min_x
        for (int i = 0; i < n; i++){
            if (x[i] > max){
                max = x[i]; max_idx = i;
            } 
            if (x[i] < min){
                min = x[i]; min_idx = i;
            } 
            if (y[i] > maxy){
                maxy = y[i]; maxy_idx = i;
            }
            this.all_values.add(i);

        }
        this.MAX_X = max_idx;
        this.MIN_X = min_idx;
        this.MAX_Y = maxy_idx;


    }

    //method for calculating entire convex hull
    public IntList calculate(){
        this.convexHull.add(this.MAX_X);
        recursive(MIN_X, MAX_X, this.all_values);
        this.convexHull.add(this.MIN_X);
        recursive(MAX_X, MIN_X, this.all_values);

        return this.convexHull;
    }

    /*
     * Recursive algorithm to find convex hull
     * int left: index for leftmost point (x1, y1)
     * int right: index for rightmost point (x2, y2)
     * IntList previous: List of points which are relevant to check.
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

        //calculates the line between our two points
        int a = y1 - y2;
        int b = x2 - x1;
        int c = y2*x1 - y1*x2;

        int max = (-1)*Integer.MAX_VALUE;
        int max_idx = -1;
        int d;
        int idx;
        //goes through each element and finds largest distance away from the line
        //also stores every point which is either on the line or above the line to a IntList
        //these points are then the ones we want to check in the next recursive call.
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

        //if there is a point away from the line, then make new line and repeat
        if (max > 0){
            recursive(max_idx, right, current);
            this.convexHull.add(max_idx);
            recursive(left, max_idx, current);
        }
        
        //the the point "furthest" away is on the line, then find all points on line
        if (max == 0){
            recursive_line(max_idx, right, current);
            this.convexHull.add(max_idx);
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
            //condition to see if the line only moves on the y axis
            if (max_x == min_x){
                if (this.y[idx] < max_y && this.y[idx] > min_y){
                    recursive_line(idx, right, previous);
                    this.convexHull.add(idx);
                    recursive_line(left,idx, previous);
                    break;
                }
            }
            //condition to see if the line only moves on the x axis
            if (max_y == min_y){
                if (this.x[idx] < max_x && this.x[idx] > min_x){
                    recursive_line(idx, right, previous);
                    this.convexHull.add(idx);
                    recursive_line(left,idx, previous);
                    break;
                }
            }
            
            else{
                if (this.x[idx] < max_x && this.x[idx] > min_x && this.y[idx] < max_y && this.y[idx] > min_y){
                    recursive_line(idx, right, previous);
                    this.convexHull.add(idx);
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

        
    public static void main(String[] args){
        int n;      
        int runs = 7;  
        int seed;
        try {
            n = Integer.parseInt(args[0]);
            seed = Integer.parseInt(args[1]);
            if (n < 2) throw new Exception();
        } catch(Exception e) {
            System.out.println("Correct use of program is: " +
            "java Oblig3 <n> <seed> where <n> and <seed> are positive integers. n must be greater than 2.");
            return;
        }
        double time_divisor = 1e9; //1e9 is to convert to seconds, 1e6 to convert to ms
        
        int[] x = new int[n];
        int[] y = new int[n];

        NPunkter17 punkter = new NPunkter17(n, seed); //99, 100 and 101 good seeds to test cases
        punkter.fyllArrayer(x, y);

        double[] times = new double[runs];
        for (int i = 0; i < runs; i++){
            double seqStart = System.nanoTime();
            CH convex = new CH(n, x, y);
            IntList convexHull = convex.calculate();
            double ElapsedTimeseq = (System.nanoTime() - seqStart)/time_divisor;
            System.out.println("Time used sequential: " + ElapsedTimeseq);

            times[i] = ElapsedTimeseq;
            if (i == 0 && n < 10000){        
                Oblig4Precode p = new Oblig4Precode(convex, convexHull);
                p.writeHullPoints(); //write to file
                convexHull.print(); //output in terminal
                p.drawGraph(); //draw graph
            }
        }
        if (runs == 7){
            Arrays.sort(times);
            System.out.println("median time used: " + times[3]);    
        }
    }  

}
