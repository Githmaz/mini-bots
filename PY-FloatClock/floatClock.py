import tkinter as tk
import time

def update_time():
    current_time = time.strftime('%H:%M:%S')
    label.config(text=current_time)
    label.after(1000, update_time)

root = tk.Tk()
root.overrideredirect(True)  # removes window borders
root.attributes('-topmost', True)  # always on top
root.attributes('-alpha', 0.8)  # transparent
root.config(bg='black')

# place at top left corner of the screen
root.geometry("+10+10")

label = tk.Label(root, font=('Segoe UI', 20, 'bold'), fg='lime', bg='black')
label.pack(padx=10, pady=5)

update_time()

# drag with mouse
def start_move(event):
    root.x = event.x
    root.y = event.y

def stop_move(event):
    root.x = None
    root.y = None

def on_motion(event):
    x = (event.x_root - root.x)
    y = (event.y_root - root.y)
    root.geometry(f"+{x}+{y}")

label.bind('<Button-1>', start_move)
label.bind('<ButtonRelease-1>', stop_move)
label.bind('<B1-Motion>', on_motion)

root.mainloop()
