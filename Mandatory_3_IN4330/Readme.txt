To compile:
javac Oblig3.java; java Oblig3 <n> <k>
where n is size of array, and k is number of cores you want to use.

Can also manually test Sieve time by compiling:
javac SievePara.java; java SievePara <n> <k>

for benchmarking:
go into folder "myBenchmark" and run: java -jar target/benchmarks.jar
if it does not work, first run: mvn clean install, then run the command above.