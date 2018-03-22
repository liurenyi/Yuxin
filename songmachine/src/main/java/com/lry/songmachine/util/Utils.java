package com.lry.songmachine.util;

public class Utils {

    public static final int KEY_PERMISSION_REQUEST_CODE = 1;
    public static final int KEY_DIALOG_PERMISSION_REQUEST_CODE = 2;

    //rx遍历扫描的文件路径
    public static final String KEY_SCANNING_PATH = Method.getInnerSDcardPath() + "/Movies";

    public static final int NUMBER_PER_SCREEN = 9; //每页显示多少个item,记得看Gridview分为几列

    public static final String KEY_CURRENT_SCREEN = "current_screen";

    public static final String KEY_TOGGLE_SCREEN = "toggle_screen";

}
