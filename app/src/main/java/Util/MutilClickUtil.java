package Util;

/**
 * Created by youker on 2017/7/19 0019.
 * 距离上次正常操作间隔时间内 操作无效
 */

public class MutilClickUtil {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 5000;
    private static long lastClickTime;

    public static boolean isNormalClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
            lastClickTime = curClickTime;
        }
        return flag;
    }
}
