import tkinter as tk
import time
import winsound

# =========================
# SETTINGS
# =========================
ALARM_DURATION_MS = 3000   # ring for 3 seconds
CHECK_INTERVAL_MS = 200    # check 5 times/sec

# =========================
# STATE
# =========================
last_alarm_key = None      # stores (year, month, day, hour, minute)
alarm_active = False


def beep_loop(end_time):
    """Play Windows beep repeatedly for ~3 seconds."""
    if time.time() < end_time:
        try:
            winsound.MessageBeep()
        except:
            root.bell()
        root.after(400, lambda: beep_loop(end_time))


def stop_alarm():
    global alarm_active
    alarm_active = False
    label.config(fg='lime')


def trigger_alarm():
    global alarm_active
    if alarm_active:
        return

    alarm_active = True
    label.config(fg='red')

    end_time = time.time() + (ALARM_DURATION_MS / 1000)
    beep_loop(end_time)

    root.after(ALARM_DURATION_MS, stop_alarm)


def update_time():
    global last_alarm_key

    now = time.localtime()
    current_time = time.strftime('%H:%M:%S', now)
    label.config(text=current_time)

    # Ring only on exact half-hour marks: minute 0 or 30, second 0
    if now.tm_min in (0, 30) and now.tm_sec == 0:
        alarm_key = (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min)

        # Prevent multiple triggers during the same second/check cycle
        if last_alarm_key != alarm_key:
            last_alarm_key = alarm_key
            trigger_alarm()

    root.after(CHECK_INTERVAL_MS, update_time)


# =========================
# UI
# =========================
root = tk.Tk()
root.overrideredirect(True)          # borderless
root.attributes('-topmost', True)    # always on top
root.attributes('-alpha', 0.85)      # transparency
root.config(bg='black')

# Fixed size so it shows properly
root.geometry("220x60+10+10")

label = tk.Label(
    root,
    font=('Segoe UI', 20, 'bold'),
    fg='lime',
    bg='black'
)
label.pack(expand=True, fill='both')

# Drag window
def start_move(event):
    root.x = event.x
    root.y = event.y

def on_motion(event):
    x = event.x_root - root.x
    y = event.y_root - root.y
    root.geometry(f"+{x}+{y}")

label.bind('<Button-1>', start_move)
label.bind('<B1-Motion>', on_motion)

# Double-click to close
label.bind('<Double-Button-1>', lambda e: root.destroy())

update_time()
root.mainloop()
