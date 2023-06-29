import tkinter as tk
import threading
import time

from DeviceIdentityDict import DeviceIdentityDict
from NumericDict import NumericDict

FONT = ('Arial', 11)

class SupervisorApp(threading.Thread):
    def __init__(self):
        self.running = True
        threading.Thread.__init__(self)
        self.start()

    def callback(self):
        self.running = False
        self.root.destroy()
    
    def run(self):
        self.root = tk.Tk()
        self.root.protocol("WM_DELETE_WINDOW", self.callback)

        self.root.geometry("1000x800")
        self.root.title("OpenICE Python")

        self.device_btn = tk.PhotoImage(file='')
        self.image_label = tk.Label(self.root, image=self.device_btn)
        self.image_label.pack()

        self.device_label = tk.Label(self.root, text='', font=FONT)
        
        self.device_label.pack()

        self.root.mainloop()

class DeviceInfoPage(threading.Thread):
    def __init__(self):
        self.running = True
        threading.Thread.__init__(self)
        self.start()

    def callback(self):
        self.running = False
        self.root.destroy()

    def run(self):
        self.root = tk.Tk()
        self.root.protocol("WM_DELETE_WINDOW", self.callback)

        self.root.geometry("640x480")
        self.root.title("Device Info")

        manufacturer_title = tk.Label(self.root, text='Manufacturer', font=FONT).place(x=0)
        model_title = tk.Label(self.root, text='Model', font=FONT).place(y=18)
        serial_num_title = tk.Label(self.root, text='Serial Number', font=FONT).place(y=36)
        udi_title = tk.Label(self.root, text='Unique Device Identifier', font=FONT).place(y=54)
        connection_state_title = tk.Label(self.root, text='Connection State', font=FONT).place(y=72)
        version_title = tk.Label(self.root, text='Version', font=FONT).place(y=90)
        os_title = tk.Label(self.root, text='Operating System', font=FONT).place(y=108)
        host_name_title = tk.Label(self.root, text='Host Name', font=FONT).place(y=127)
        infusion_rate_title = tk.Label(self.root, text='Infusion Rate:', font=FONT).place(y=162)

        self.manufacturer_label = tk.Label(self.root, text='', font=FONT)#.place(x=170)
        self.model_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=18)
        self.serial_num_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=36)
        self.udi_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=54)
        self.connection_state_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=72)
        self.version_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=90)
        self.os_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=108)
        self.host_name_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=127)
        self.infusion_rate_label = tk.Label(self.root, text='', font=('Arial', 20))

        self.manufacturer_label.place(x=170)
        self.model_label.place(x=170, y=18)
        self.serial_num_label.place(x=170, y=36)
        self.udi_label.place(x=170, y=54)
        self.connection_state_label.place(x=170, y=72)
        self.version_label.place(x=170, y=90)
        self.os_label.place(x=170, y=108)
        self.host_name_label.place(x=170, y=127)
        self.infusion_rate_label.place(y=180)

        self.root.mainloop()

current_identities = DeviceIdentityDict()
current_numerics = NumericDict()

supervisor = SupervisorApp()
deviceinfopage = DeviceInfoPage()

while supervisor.running:
    time.sleep(2)
    current_identities.update()
    current_numerics.update()
    if len(current_identities.deviceIdentityDict) > 0:
        fetched_identity = current_identities.fetch()[0]
        fetched_numeric = current_numerics.fetch(udi=fetched_identity.unique_device_identifier)[0]
        fetched_identity.icon.render()

        supervisor.device_label['text'] = f'{fetched_identity.manufacturer} {fetched_identity.model}'
        supervisor.device_btn['file'] = fetched_identity.icon.image_path
        print(fetched_identity.icon.image_path)

        deviceinfopage.manufacturer_label['text'] = fetched_identity.manufacturer
        deviceinfopage.model_label['text'] = fetched_identity.model
        deviceinfopage.serial_num_label['text'] = fetched_identity.serial_number
        deviceinfopage.udi_label['text'] = fetched_identity.unique_device_identifier
        deviceinfopage.connection_state_label['text'] = ''
        deviceinfopage.version_label['text'] = fetched_identity.build
        deviceinfopage.os_label['text'] = fetched_identity.operating_system
        deviceinfopage.host_name_label['text'] = ''
        deviceinfopage.infusion_rate_label['text'] = str(float(fetched_numeric.value))