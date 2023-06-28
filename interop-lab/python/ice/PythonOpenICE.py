import tkinter as tk
import threading
import time

from DeviceIdentityDict import DeviceIdentityDict

'''root = tk.Tk()

root.geometry("1000x800")
root.title("OpenICE Python")

lable = tk.Label(root, text="ICE Controllable Pump", font=('Arial', 18))
lable.pack()

textbox = tk.Text(root, height=1, font=('Arial', 16))
textbox.pack()

button = tk.Button(root, text='Click Me', font=('Arial', 16))
button.pack()

root.mainloop()'''

'''def update_info(current_manufacturer):
    current_identities.update()
    if len(current_identities.deviceIdentityDict) > 0:
        current_manufacturer = current_identities.fetch()[0].manufacturer
        manufacturer_value_label['text'] = current_manufacturer
    
    print(current_manufacturer)
    #info_page.after(2000, update_info(current_manufacturer))'''

FONT = ('Arial', 11)

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

        self.manufacturer_label = tk.Label(self.root, text='', font=FONT)#.place(x=170)
        self.model_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=18)
        self.serial_num_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=36)
        self.udi_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=54)
        self.connection_state_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=72)
        self.version_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=90)
        self.os_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=108)
        self.host_name_label = tk.Label(self.root, text='', font=FONT)#.place(x=170, y=127)

        self.manufacturer_label.place(x=170)
        self.model_label.place(x=170, y=18)
        self.serial_num_label.place(x=170, y=36)
        self.udi_label.place(x=170, y=54)
        self.connection_state_label.place(x=170, y=72)
        self.version_label.place(x=170, y=90)
        self.os_label.place(x=170, y=108)
        self.host_name_label.place(x=170, y=127)

        self.root.mainloop()

deviceinfopage = DeviceInfoPage()

current_identities = DeviceIdentityDict()

while deviceinfopage.running:
    time.sleep(2)
    current_identities.update()
    if len(current_identities.deviceIdentityDict) > 0:
        fetched_identity = current_identities.fetch()[0]
        
        deviceinfopage.manufacturer_label['text'] = fetched_identity.manufacturer
        deviceinfopage.model_label['text'] = fetched_identity.model
        deviceinfopage.serial_num_label['text'] = fetched_identity.serial_number
        deviceinfopage.udi_label['text'] = fetched_identity.unique_device_identifier
        deviceinfopage.connection_state_label['text'] = ''
        deviceinfopage.version_label['text'] = fetched_identity.build
        deviceinfopage.os_label['text'] = fetched_identity.operating_system
        deviceinfopage.host_name_label['text'] = ''