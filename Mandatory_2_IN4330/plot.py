import numpy as np
import matplotlib.pyplot as plt 
import csv 


#Function for plotting our desired graphs
def plot_graphs(filename, computer):

    TimeArray = np.zeros((6,4))

    #going through file for k=20 for naive and smart implementation
    with open(filename, 'r') as fd:
        idx = 0
        for rows in fd:
            row = rows.split()
            for i,numb in enumerate(row):
                TimeArray[idx][i] = float(numb) 
            idx += 1



    n_arr = np.array([100,200,500,1000])

    plt.plot(n_arr, TimeArray[0], label="Classic (sequential)")
    plt.plot(n_arr, TimeArray[1], label="Transpose B (sequential)")
    plt.plot(n_arr, TimeArray[2], label="Transpose A (sequential)")
    plt.plot(n_arr, TimeArray[3], label="Classic (parallel)")
    plt.plot(n_arr, TimeArray[4], label="Transpose B (parallel)")
    plt.plot(n_arr, TimeArray[5], label="Transpose A (parallel)")
    plt.xlabel("size of matrix dimentions (n)")
    plt.ylabel("time used in seconds")
    plt.ylim(np.min(TimeArray)/1.2, np.max(TimeArray[4])*1.2)
    plt.title("Time usage for all methods")
    plt.legend()
    plt.savefig(computer + "Time_All")
    plt.show()



    plt.plot(n_arr, TimeArray[0]/TimeArray[3], label="Classic")
    plt.plot(n_arr, TimeArray[1]/TimeArray[4], label="Transpose B")
    plt.plot(n_arr, TimeArray[2]/TimeArray[5], label="Transpose A")
    plt.xlabel("size of matrix dimentions (n)")
    plt.ylabel("Amount of speedup")
    plt.title("Speedup between sequential and parallel")
    plt.legend()
    plt.savefig(computer + "Speedup_seqToPara")
    plt.show()


    plt.plot(n_arr, TimeArray[0]/TimeArray[4])
    plt.xlabel("size of matrix dimentions (n)")
    plt.ylabel("Amount of speedup")
    plt.title("Speedup between classic sequential and parallel with transposed B")
    plt.savefig(computer + "Speedup_ClassicToParaB")
    plt.show()


plot_graphs("Times.txt", "")
#plot_graphs("TimesIFI.txt", "IFI_")