package Util;

/**
 * Created by youker on 2017/6/28 0028.
 */
import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * 手机震动工具类
 * @author DM
 *
 */
public class VibratorUtil {
    /**
     * 单次震动
     *
     * @param activity 调用该方法的Activity实例: 如，MainActivity.this
     * @param milliseconds 震动时长, 单位毫秒(ms).
     */
    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
    /**
     * 自定义震动
     *
     * @param activity 调用该方法的Activity实例
     * @param pattern  自定义震动模式:
     *                 数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长...];
     *                 时长的单位是毫秒.
     * @param isRepeat 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */
    public static void Vibrate(final Activity activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

}
