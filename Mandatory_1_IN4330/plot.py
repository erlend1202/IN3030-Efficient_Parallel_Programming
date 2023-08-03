import numpy as np
import matplotlib.pyplot as plt 
import csv 

k20= np.zeros((2,6))
k100 = np.zeros((2,6))

#going through file for k=20 for naive and smart implementation
with open('k20.txt', 'r') as fd:
    reader = csv.reader(fd)
    idx = 0
    for row in reader:
        for i,word in enumerate(row):
            k20[idx][i] = word 
        idx += 1

#going through file for k=100 for naive and smart implementation
with open('k100.txt', 'r') as fd:
    reader = csv.reader(fd)
    idx = 0
    for row in reader:
        for i,word in enumerate(row):
            k100[idx][i] = word 
        idx += 1


#For parallell solution
k20Para = np.zeros(6)
k100Para = np.zeros(6)

#Going through file for parallel solution for k=20
with open('k20_parallell.txt', 'r') as fd:
    reader = csv.reader(fd)
    for row in reader:
        for i,word in enumerate(row):
            k20Para[i] = word 

#Going through file for parallel solution for k=100
with open('k100_parallell.txt', 'r') as fd:
    reader = csv.reader(fd)
    for row in reader:
        for i,word in enumerate(row):
            k100Para[i] = word 


n_arr = np.logspace(3,8, 6)
plt.plot(n_arr, k20[0], label="k20_naive")
plt.plot(n_arr, k20[1], label="k20_smart")
plt.plot(n_arr, k100[0], label="k100_naive")
plt.plot(n_arr, k100[1], label="k100_smart")
plt.yscale("log")
plt.xlabel("number of elements in array")
plt.ylabel("time used in milliseconds")
plt.title("Timing for sequential algorithm")
plt.legend()
plt.savefig("Sequential timing")
plt.show()


plt.plot(n_arr, k20Para, label="k20_Parallell")
plt.plot(n_arr, k100Para, label="k100_Parallell")
plt.xlabel("number of elements in array")
plt.ylabel("time used in milliseconds")
plt.title("Timing for parallell algorithm")
plt.legend()
plt.savefig("Parallell timing")
plt.show()


plt.plot(n_arr, k20[1]/k20Para, label="k20")
plt.plot(n_arr, k100[1]/k100Para, label="k100")
plt.yscale("log")
plt.xlabel("number of elements in array")
plt.ylabel("Speedup")
plt.title("ratio between parallell and sequential algorithm")
plt.legend()
plt.savefig("Ratio")
plt.show()