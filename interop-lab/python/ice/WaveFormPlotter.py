import matplotlib.pyplot as plt
import time

from SampleArrayDict import SampleArrayDict

currentDict = SampleArrayDict()

while len(currentDict.sampleArrayDict) == 0:
    time.sleep(1.0)
    currentDict.update()

print(len(currentDict.sampleArrayDict))
fetchedSampleArray = currentDict.fetch(metric_id='MDC_PULS_OXIM_PLETH')[0]

current_frequency = fetchedSampleArray.frequency
current_data = fetchedSampleArray.values.userData
current_metric_id = fetchedSampleArray.metric_id

print(current_frequency)
print(len(current_data))

x_axis = [i for i in range(0, current_frequency)]
y_axis = current_data

plt.plot(x_axis, y_axis)
plt.title(f'Waveform Data: {current_metric_id}')
plt.xlabel('Datapoint')
plt.ylabel('Data')
plt.show()