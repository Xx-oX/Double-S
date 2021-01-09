import numpy as np
import matplotlib.pyplot as plt
from scipy import signal

with open('./acc_log.txt', 'r') as f:
    lines = f.readlines()
    f.close()

cnt = 0
data = []

for line in lines:
    cnt += 1
    if cnt == 1:
        continue

    str = line.split()
    data.append(str[-1:][0])

    if cnt == 11:
        cnt = 0
        
y = np.array(data)
y = y.astype(np.float32)

b, a = signal.butter(8, 0.08, 'lowpass') 
y = signal.filtfilt(b, a, y)

upperThreshold = 280.0
lowerThreshold = 200.0
cntUpper = 0
cntLower = 0
upperFlag = False
lowerFlag = False

for i in y:
    if i >= upperThreshold:
        if not upperFlag:
            upperFlag = True
    
    if i < upperThreshold:
        if upperFlag:
            upperFlag = False
            cntUpper += 1
    
    if i <= lowerThreshold:
        if not lowerFlag:
            lowerFlag = True
    
    if i > lowerThreshold:
        if lowerFlag:
            lowerFlag = False
            cntLower += 1

print(cntUpper, cntLower)

x = np.arange(len(y))
plt.plot(x, y)
plt.show()
    