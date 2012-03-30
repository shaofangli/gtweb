package org.commons;

import java.util.HashMap;

public class Statics {
	public static final int TP_SY_WIDTH = 663;
	public static final int TP_SY_HEIGHT = 310;
	public static final String dbNm = "mysql5";// 默认数据库
	public static final int pcount = 50;// 默认分页每页显示
	public static final int csyimg = 5;// 首页图片张数
	public static final String UPLOAD_IMG_PATH = "/uploadfiles/images/";// 上传文件的保存路径，相对于应用的根目录
	public static final String UPLOAD_FILE_TEMP = "/uploadfiles/tmp/";// 上传文件的临时保存路径，相对于应用的根目录

	public static final HashMap<String, Integer> DB_TB_MAP = new HashMap<String, Integer>();

	static {
		// 已经定义的系统默认类型ID
		DB_TB_MAP.put("LX_ALL_ID", 0);// 所有父节点ID
		DB_TB_MAP.put("LX_JS_ID", 1);// 角色ID
		DB_TB_MAP.put("LX_TP_ID", 2);// 图片ID
		DB_TB_MAP.put("LX_WZ_ID", 3);// 文章ID
		DB_TB_MAP.put("LX_GLYJS_ID", 4);// 管理员角色ID
		DB_TB_MAP.put("LX_TP_SY_ID", 5);// 首页图片ID
		DB_TB_MAP.put("LX_NEWS_ID", 6);// 动态新闻文章ID
		DB_TB_MAP.put("LX_ZNXW_ID", 7);// 站内新闻ID
		DB_TB_MAP.put("LX_GJXW_ID", 8);// 国际新闻ID
		DB_TB_MAP.put("LX_YLXW_ID", 9);// 娱乐新闻ID
		DB_TB_MAP.put("LX_GSZZ_ID", 10);// 公司资质ID
		DB_TB_MAP.put("LX_GYWM_ID", 11);// 关于我们ID
		DB_TB_MAP.put("LX_LXWM_ID", 12);// 联系我们ID
		DB_TB_MAP.put("LX_JJFA_ID", 13);// 解决方案ID
	}

	public static boolean isSysLx(String... lxids) {
		for (String lxid : lxids) {
			if (DB_TB_MAP.containsValue(Integer.valueOf(lxid)))
				return true;
		}
		return false;
	}
}
