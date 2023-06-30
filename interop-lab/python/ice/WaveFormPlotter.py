import matplotlib.pyplot as plt
import matplotlib.animation as animation
import time

from SampleArrayDict import SampleArrayDict

currentDict = SampleArrayDict()

fig = plt.figure()
ax1 = fig.add_subplot(1, 1, 1)

y_axis = []

def animate(i):
    currentDict.update()
    
    fetchedSampleArray = currentDict.fetch(metric_id='MDC_PULS_OXIM_PLETH')[0]

    current_data = fetchedSampleArray.values.userData

    for datapoint in current_data: y_axis.append(datapoint)
    frequency = len(y_axis)
    x_axis = [i for i in range(0, frequency)]

    ax1.clear()
    ax1.plot(x_axis, y_axis)

while len(currentDict.sampleArrayDict) == 0:
    time.sleep(1.0)
    currentDict.update()

ani = animation.FuncAnimation(fig, animate, interval=1000)
plt.show()
