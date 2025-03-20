package hero.bane.helper;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public class User32Helper {

    public static boolean capsDisabled = false;
    private static final int CAPS = 0x14;
    private static final int KEYWENTUP = 0x0002;

    public interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        short GetKeyState(int key);
        void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);
    }

    public static boolean isCapsLockOn() {
        return (User32.INSTANCE.GetKeyState(CAPS) & 0x0001) != 0;
    }

    public static void disableCapsLock() {
        if (isCapsLockOn()) {
            capsDisabled = true;
            setCapsLockState(false);
        }
    }

    public static void setCapsLockState(boolean enabled) {
        if (!enabled) {
            User32.INSTANCE.keybd_event((byte) CAPS, (byte) 0, 0, 0);
            User32.INSTANCE.keybd_event((byte) CAPS, (byte) 0, KEYWENTUP, 0);
        }
    }
}
