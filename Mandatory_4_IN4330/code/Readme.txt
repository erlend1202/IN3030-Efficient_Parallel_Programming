To compile:

For only serial part:
javac CH.java; java CH <n> <seed>

For parallel part:
javac CH_PARA.java; java CH_PARA <n> <seed> <cores>


Switch out <n> <seed> <cores> with number you want. 

n is number of points we want to use
seed is the seed we want to use 
cores is number of cores 

if n < 10 000, it will print to terminal, write to file and make a graph of the convex hull and all the points.
if cores is set to 0 or less, it will use the number of cores available on your computer.

The program runs 7 times in total, stores the time used for each run, then prints out the median time used.
In CH_PARA.java it does this for both parallel and sequential solution, and also prints the speedup.