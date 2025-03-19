package hero.bane.helper;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public class User32Helper {

    public interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        short GetKeyState(int key);
        void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);
    }

    private static final int VK_CAPSLOCK = 0x14;
    private static final int KEYEVENTF_KEYUP = 0x0002;

    public static boolean isCapsLockOn() {
        return (User32.INSTANCE.GetKeyState(VK_CAPSLOCK) & 0x0001) != 0;
    }

    public static void disableCapsLock() {
        if (isCapsLockOn()) {
            // Disable Caps Lock by modifying keyboard state, without sending a keypress
            setCapsLockState(false);
        }
    }

    private static void setCapsLockState(boolean enabled) {
        // If Caps Lock should be OFF, we clear the key state without sending an event
        if (!enabled) {
            // Directly modify key state in the OS without generating a keypress event
            User32.INSTANCE.keybd_event((byte) VK_CAPSLOCK, (byte) 0, 0, 0);
            User32.INSTANCE.keybd_event((byte) VK_CAPSLOCK, (byte) 0, KEYEVENTF_KEYUP, 0);
        }
    }
}
