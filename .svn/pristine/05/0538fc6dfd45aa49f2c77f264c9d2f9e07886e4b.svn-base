package com.pcs.ztqtj.control.tool;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author Z 日历
 */
public class ChineseDateUtil {

	private int year;
	private int month;
	private int day;

	private int gregorianYear;
	private int gregorianMonth;
	private int gregorianDate;

	private boolean leap;

	private final String[] Animals = new String[] { "鼠", "牛", "虎", "兔", "龙",
			"蛇", "马", "羊", "猴", "鸡", "狗", "猪" };
	private static final String[] Gan = new String[] { "甲", "乙", "丙", "丁", "戊",
			"己", "庚", "辛", "壬", "癸" };
	private static final String[] Zhi = new String[] { "子", "丑", "寅", "卯", "辰",
			"巳", "午", "未", "申", "酉", "戌", "亥" };
	private static final String[] xingsu = { "角木蛟", "亢金龙", "氐土貉", "房日兔", "心月狐",
			"尾火虎", "箕水豹", "斗木獬", "牛金牛", "氐土貉", "虚日鼠", "危月燕", "室火猪", "壁水獝",
			"奎木狼", "娄金狗", "胃土彘", "昴日鸡", "毕月乌", "觜火猴", "参水猿", "井木犴", "鬼金羊",
			"柳土獐", "星日马", "张月鹿", "翼火蛇", "轸水蚓" };
	private static final String[] dirs = { "东方", "北方", "西方", "南方" };
	private static final String[] _yiStr = {

			"出行,上任,会友,上书,见工",// 建
			"除服,疗病,出行,拆卸,入宅",// 除
			"祈福,祭祀,结亲,开市,交易",// 满
			"祭祀,修墳,涂泥,餘事勿取",// 平
			"交易,立券,会友,签約,納畜",// 定
			"祈福,祭祀,求子,结婚,立约",// 执
			"日值月破 大事不宜",// 破
			"经营,交易,求官,納畜,動土",// 危
			"祈福,入学,开市,求医,成服",// 成
			"祭祀,求财,签约,嫁娶,订盟",// 收
			"疗病,结婚,交易,入仓,求职",// 开
			"祭祀,交易,收财,安葬",// 闭
	};
	private static final String[] _jiStr = { "动土,开仓,嫁娶,纳采",// 建
			"求官,上任,开张,搬家,探病 ",// 除
			"服药,求医,栽种,动土,迁移",// 满
			"移徙.入宅.嫁娶.开市.安葬",// 平
			"种植,置业,卖田,掘井,造船",// 定
			"开市,交易,搬家,远行 ",// 执
			"日值月破 大事不宜",// 破
			"登高,行船.安床.入宅.博彩",// 危
			"词讼,安門,移徙",// 成
			"开市.安床.安葬.入宅.破土",// 收
			"安葬,动土,针灸",// 开
			"宴会,安床,出行,嫁娶,移徙",// 闭
	};
	private final static String chineseNumber[] = { "一", "二", "三", "四", "五",
			"六", "七", "八", "九", "十", "十一", "十二" };
	/**日期格式：yyyyMMdd **/
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	/**日期格式：yyyy年MM月 **/
	public static SimpleDateFormat sdf_ym_chinese = new SimpleDateFormat("yyyy年MM月");
	/**日期格式：MM月 **/
	public static SimpleDateFormat sdf_m_chinese = new SimpleDateFormat("MM月");
	/**日期格式：MM **/
	public static SimpleDateFormat sdf_m = new SimpleDateFormat("MM");

	private static SimpleDateFormat chineseDateFormat = new SimpleDateFormat(
			"yyyy年MM月dd日");
	final static long[] lunarInfo = new long[] { 0x04bd8, 0x04ae0, 0x0a570,
			0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
			0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0,
			0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50,
			0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566,
			0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0,
			0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4,
			0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550,
			0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950,
			0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260,
			0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0,
			0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
			0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40,
			0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3,
			0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960,
			0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0,
			0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9,
			0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0,
			0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65,
			0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0,
			0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2,
			0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0 };

	// 中国节日日期
	private final static String[] chineseFestivalDate = {
			"一月初一","一月十五","五月初五",
			"七月初七","七月十五","八月十五",
			"九月初九","十二月初八"
	};

	// 中国节日名
	private final static String[] chineseFestivalName = {
			"春节","元宵节","端午节",
			"七夕节","中元节","中秋节",
			"重阳节","腊八节","除夕"
	};

	// ====== 传回农历 y年的总天数
	final private static int yearDays(int y) {
		int i, sum = 348;
		for (i = 0x8000; i > 0x8; i >>= 1) {
			if ((lunarInfo[y - 1900] & i) != 0)
				sum += 1;
		}
		return (sum + leapDays(y));
	}

	// ====== 传回农历 y年闰月的天数
	final private static int leapDays(int y) {
		if (leapMonth(y) != 0) {
			if ((lunarInfo[y - 1900] & 0x10000) != 0)
				return 30;
			else
				return 29;
		} else
			return 0;
	}

	// ====== 传回农历 y年闰哪个月 1-12 , 没闰传回 0
	final private static int leapMonth(int y) {
		return (int) (lunarInfo[y - 1900] & 0xf);
	}

	// ====== 传回农历 y年m月的总天数
	final private static int monthDays(int y, int m) {
		if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
			return 29;
		else
			return 30;
	}

	// ====== 传回农历 y年的生肖
	final public String animalsYear() {
		return Animals[(year - 4) % 12];
	}

	// ====== 传入 月日的offset 传回干支, 0=甲子
	final private static String cyclicalm(int num) {

		return (Gan[num % 10] + Zhi[num % 12]);
	}

	// ====== 传入 offset 传回干支, 0=甲子
	final public String cyclical() {
		int num = year - 1900 + 36;
		return (cyclicalm(num));
	}

	// public Lunar(int y,int m,int d){
	// Calendar c = Calendar.getInstance();
	// this(c);
	// }

	/**
	 * 传出y年m月d日对应的农历. yearCyl3:农历年与1864的相差数 ? monCyl4:从1900年1月31日以来,闰月数
	 * dayCyl5:与1900年1月31日相差的天数,再加40 ?
	 *
	 * @param cal
	 * @return
	 */
	@SuppressWarnings("unused")
	public ChineseDateUtil(Calendar cal) {
		int yearCyl, monCyl, dayCyl;

		this.gregorianDate = cal.get(Calendar.DAY_OF_MONTH);
		this.gregorianMonth = cal.get(Calendar.MONTH) + 1;
		this.gregorianYear = cal.get(Calendar.YEAR);

		int leapMonth = 0;
		Date baseDate = null;
		try {
			baseDate = chineseDateFormat.parse("1900年1月31日");
		} catch (ParseException e) {
			e.printStackTrace(); // To change body of catch statement use
			// Options | File Templates.
		}

		// 求出和1900年1月31日相差的天数
		int offset = (int) ((cal.getTime().getTime() - baseDate.getTime()) / 86400000L);
		dayCyl = offset + 40;
		monCyl = 14;

		// 用offset减去每农历年的天数
		// 计算当天是农历第几天
		// i最终结果是农历的年份
		// offset是当年的第几天
		int iYear, daysOfYear = 0;
		for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
			daysOfYear = yearDays(iYear);
			offset -= daysOfYear;
			monCyl += 12;
		}
		if (offset < 0) {
			offset += daysOfYear;
			iYear--;
			monCyl -= 12;
		}
		// 农历年份
		year = iYear;

		yearCyl = iYear - 1864;
		leapMonth = leapMonth(iYear); // 闰哪个月,1-12
		leap = false;

		// 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
		int iMonth, daysOfMonth = 0;
		for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
			// 闰月
			if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
				--iMonth;
				leap = true;
				daysOfMonth = leapDays(year);
			} else
				daysOfMonth = monthDays(year, iMonth);

			offset -= daysOfMonth;
			// 解除闰月
			if (leap && iMonth == (leapMonth + 1))
				leap = false;
			if (!leap)
				monCyl++;
		}
		// offset为0时，并且刚才计算的月份是闰月，要校正
		if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
			if (leap) {
				leap = false;
			} else {
				leap = true;
				--iMonth;
				--monCyl;
			}
		}
		// offset小于0时，也要校正
		if (offset < 0) {
			offset += daysOfMonth;
			--iMonth;
			--monCyl;
		}
		month = iMonth;
		day = offset + 1;
	}

	/**
	 * 农历日
	 *
	 * @return
	 */
	public String getChinaDayString() {
		String chineseTen[] = { "初", "十", "廿" };
		int n = day % 10 == 0 ? 9 : day % 10 - 1;
		if (day > 30)
			return "";
		if (day == 10)
			return "初十";
		if (day == 20)
			return "二十";
		if (day == 30)
			return "三十";
		else
			return chineseTen[day / 10] + chineseNumber[n];
	}

	/**
	 * 农历月
	 *
	 * @return
	 */
	public String getChinaMonthString() {
		if(month - 1==0){
			return (leap ? "闰" : "") + "正月";
		}else{
			return (leap ? "闰" : "") + chineseNumber[month - 1] + "月";
		}
	}

	/**
	 * 农历年
	 *
	 * @return
	 */
	public String getChinaYearString() {
		// return year + "年";
		return cyclical() + "(" + animalsYear() + ")" + "年 ";
	}

    public String getChinaYear() {
        // return year + "年";
        return cyclical();
    }

	public String toString() {
		return year + "年" + (leap ? "闰" : "") + chineseNumber[month - 1] + "月"
				+ getChinaDayString();
	}

	/**
	 * 获取农历月
	 * @return
	 */
	public int getMonth() {
		return this.month;
	}

	// 获取当天节日信息
	public String getChineseFestival() {
		String date = chineseNumber[month - 1] + "月" + getChinaDayString();
		for(int i = 0; i < chineseFestivalDate.length; i++) {
			if(date.equals(chineseFestivalDate[i])) {
				return chineseFestivalName[i];
			}
		}
		if(month == 12) {
			// 如果当天是12月最后一天，那么返回除夕
			if(day == monthDays(year, month)) {
				return chineseFestivalName[8];
			}
		}
		return "";
	}

	// 获取当月节气信息
	public String[] getCurrentMonthSolarTerms() {
		int index = (gregorianMonth-1)*2;
		String[] result = {solarTerm[index], solarTerm[index+1]};
		return result;
	}

//	public static void main(String[] args) {
//		Calendar c = Calendar.getInstance();
//		c.set(2012, 6, 9);
//		System.out.print(new ChineseDateUtil(c).toString());
//	}

	public static long compareByDay(Calendar begin, Calendar end) {
		long days = 0;
		while (begin.before(end)) {
			days++;
			begin.add(Calendar.DAY_OF_YEAR, 1);
		}
		return days;
	}

	/**
	 * 天干地址
	 *
	 * @return
	 */
	public String getTianGanDay() {

		int y = gregorianYear;
		int m = gregorianMonth;
		int d = gregorianDate;

		System.out.println(y + "年" + m + "月" + d + "日");

		int c = y / 100;// 世纪数
		y = y - c * 100;// 年的后面两位
		int i = m % 2 == 0 ? 6 : 0;

		int g = 4 * c + c / 4 + 5 * y + y / 4 + 3 * (m + 1) / 5 + d - 3 - 1;

		int z = 8 * c + c / 4 + 5 * y + y / 4 + 3 * (m + 1) / 5 + d + 7 + i - 1;

		String gz = Gan[g % 10] + Zhi[z % 12];

		return gz;
	}

	private String[] _riJianStr = new String[] { "建日", "除日", "满日", "平日", "定日",
			"执日", "破日", "危日", "成日", "收日", "开日", "闭日" };

	/**
	 * 日建
	 *
	 * @return
	 */
	public String getDayRijian() {

		int monthIndex = getZhiMouthIndex();

		int dayIndex = getZhiDayIndex();
		String result = null;

		int index = dayIndex - monthIndex;
		index = index < 0 ? index + 12 : index;

		switch (index) {
			case 0:
				result = _riJianStr[0];
				break;
			case 1:
				result = _riJianStr[1];
				break;
			case 2:
				result = _riJianStr[2];
				break;
			case 3:
				result = _riJianStr[3];
				break;
			case 4:
				result = _riJianStr[4];
				break;
			case 5:
				result = _riJianStr[5];
				break;
			case 6:
				result = _riJianStr[6];
				break;
			case 7:
				result = _riJianStr[7];
				break;
			case 8:
				result = _riJianStr[8];
				break;
			case 9:
				result = _riJianStr[9];
				break;
			case 10:
				result = _riJianStr[10];
				break;
			case 11:
				result = _riJianStr[11];
				break;
		}
		return result;
	}

	public int getZhiMouthIndex() {
		int y = gregorianYear;
		int m = gregorianMonth;
		int d = gregorianDate;
		int solarIdx = SolarTerm.getSoralIndex(y, m, d);
		int idx = 0;
		if (solarIdx >= 0 && solarIdx < 2) {
			idx = 11;
		} else if (solarIdx >= 2 && solarIdx < 4) {
			idx = 0;
		} else if (solarIdx >= 4 && solarIdx < 6) {
			idx = 1;
		} else if (solarIdx >= 6 && solarIdx < 8) {
			idx = 2;
		} else if (solarIdx >= 8 && solarIdx < 10) {
			idx = 3;
		} else if (solarIdx >= 10 && solarIdx < 12) {
			idx = 4;
		} else if (solarIdx >= 12 && solarIdx < 14) {
			idx = 5;
		} else if (solarIdx >= 14 && solarIdx < 16) {
			idx = 6;
		} else if (solarIdx >= 16 && solarIdx < 18) {
			idx = 7;
		} else if (solarIdx >= 18 && solarIdx < 20) {
			idx = 8;
		} else if (solarIdx >= 20 && solarIdx < 22) {
			idx = 9;
		} else if (solarIdx >= 22 && solarIdx < 24) {
			idx = 10;
		}
		if (idx > 10) {
			idx = idx - 10;
		} else {
			idx = idx + 2;
		}
		idx %= 12;

		return idx;
	}

	/**
	 * 忌
	 *
	 * @return
	 */
	public String getDayJi() {

		int monthIndex = getZhiMouthIndex();
		int dayIndex = getZhiDayIndex();

		String result = null;

		int index = dayIndex - monthIndex;
		index = index < 0 ? index + 12 : index;
		switch (index) {
			case 0:
				result = _jiStr[0];
				break;
			case 1:
				result = _jiStr[1];
				break;
			case 2:
				result = _jiStr[2];
				break;
			case 3:
				result = _jiStr[3];
				break;
			case 4:
				result = _jiStr[4];
				break;
			case 5:
				result = _jiStr[5];
				break;
			case 6:
				result = _jiStr[6];
				break;
			case 7:
				result = _jiStr[7];
				break;
			case 8:
				result = _jiStr[8];
				break;
			case 9:
				result = _jiStr[9];
				break;
			case 10:
				result = _jiStr[10];
				break;
			case 11:
				result = _jiStr[11];
				break;
		}
		return result;

	}

	/**
	 * 宜
	 *
	 * @return
	 */
	public String getDayYi() {

		int monthIndex = getZhiMouthIndex();
		int dayIndex = getZhiDayIndex();

		String result = null;

		int index = dayIndex - monthIndex;
		index = index < 0 ? index + 12 : index;
		switch (index) {
			case 0:
				result = _yiStr[0];
				break;
			case 1:
				result = _yiStr[1];
				break;
			case 2:
				result = _yiStr[2];
				break;
			case 3:
				result = _yiStr[3];
				break;
			case 4:
				result = _yiStr[4];
				break;
			case 5:
				result = _yiStr[5];
				break;
			case 6:
				result = _yiStr[6];
				break;
			case 7:
				result = _yiStr[7];
				break;
			case 8:
				result = _yiStr[8];
				break;
			case 9:
				result = _yiStr[9];
				break;
			case 10:
				result = _yiStr[10];
				break;
			case 11:
				result = _yiStr[11];
				break;
		}
		return result;
	}

	/**
	 * 干
	 */
	public int getGanDayIndex() {
		int y = gregorianYear;
		int m = gregorianMonth;
		int d = gregorianDate;

        // 1月和2月按上一年的13月和14月来算
        if(gregorianMonth == 1 || gregorianMonth == 2) {
            y = gregorianYear-1;
            m = gregorianMonth+12;
        }

		int c = y / 100;// 世纪数
		y = y - c * 100;// 年的后面两位
		// int i = m % 2 == 0 ? 6 : 0;

		int g = 4 * c + c / 4 + 5 * y + y / 4 + 3 * (m + 1) / 5 + d - 3;
        int remainder = g%10;
        if(remainder == 0) {
            return 9;
        } else {
            return remainder-1;
        }
	}

	/**
	 * 支
	 *
	 * @return
	 */
	public int getZhiDayIndex() {
		int y = gregorianYear;
		int m = gregorianMonth;
		int d = gregorianDate;

        // 1月和2月按上一年的13月和14月来算
        if(gregorianMonth == 1 || gregorianMonth == 2) {
            y = gregorianYear-1;
            m = gregorianMonth+12;
        }

		int c = y / 100;// 世纪数
		y = y - c * 100;// 年的后面两位
		int i = m % 2 == 0 ? 6 : 0;

		int z = 8 * c + c / 4 + 5 * y + y / 4 + 3 * (m + 1) / 5 + d + 7 + i;

        int remainder = z%12;
        if(remainder == 0) {
            return 11;
        } else {
            return remainder-1;
        }
	}

	public String getXingsu() {

		int y = gregorianYear;
		int m = gregorianMonth;
		int d = gregorianDate;

		Calendar startDay = Calendar.getInstance();
		startDay.set(2007, 9 - 1, 13, 0, 0, 0);
		Calendar curDay = Calendar.getInstance();
		curDay.set(y, m - 1, d, 0, 0, 0);

		long offset = compareByDay(startDay, curDay);
		int modStarDay = 0;
		modStarDay = (int) (offset % 28);

		String xingsuString = null;

		xingsuString = dirs[modStarDay / 7];

		xingsuString += (modStarDay >= 0 ? xingsu[modStarDay]
				: xingsu[27 + modStarDay]);

		return xingsuString;
	}

	/**
	 * 彭祖百忌
	 *
	 * @return
	 */
	public String getPengzuBaiJI() {
		String[] g = new String[] { "甲不开仓 ", "乙不栽植 ", "丙不修灶 ", "丁不剃头 ",
				"戊不受田 ", "己不破券 ", "庚不经络 ", "辛不合酱 ", "壬不泱水 ", "癸不词讼 " };
		String[] z = new String[] { "子不问卜", "丑不冠带", "寅不祭祀", "卯不穿井", "辰不哭泣",
				"巳不远行", "午不苫盖", "未不服药", "申不安床", "酉不会客", "戌不吃犬", "亥不嫁娶" };

		int gan = getGanDayIndex();
		int zhi = getZhiDayIndex();

		return g[gan] + z[zhi];

	}

	/**
	 * 胎神
	 *
	 * @return
	 */
	public String getTaiShen() {

		String taisheng = "";

		int tian = getGanDayIndex();
		int di = getZhiDayIndex();

		if (tian == 0 || tian == 5) {
			taisheng = taisheng + "门";
		} else if (tian == 1 || tian == 6) {
			taisheng = taisheng + "碓磨";
		} else if (tian == 2 || tian == 7) {
			taisheng = taisheng + "厨灶";
		} else if (tian == 3 || tian == 8) {
			taisheng = taisheng + "仓库";
		} else if (tian == 4 || tian == 9) {
			taisheng = taisheng + "房床";
		}
		taisheng += ",";
		if (di == 0 || di == 6) {
			taisheng = taisheng + "碓";
		} else if (di == 1 || di == 7) {
			taisheng = taisheng + "厕道";
		} else if (di == 2 || di == 8) {
			taisheng = taisheng + "炉";
		} else if (di == 3 || di == 9) {
			taisheng = taisheng + "大门";
		} else if (di == 4 || di == 10) {
			taisheng = taisheng + "鸡栖";
		} else if (di == 5 || di == 11) {
			taisheng = taisheng + "床";
		}

		String ganzhiri = getTianGanDay();

		if (ganzhiri.equals("辛未") || ganzhiri.equals("壬申")
				|| ganzhiri.equals("癸酉") || ganzhiri.equals("甲戌")
				|| ganzhiri.equals("乙亥") || ganzhiri.equals("丙子")
				|| ganzhiri.equals("丁丑")) {
			taisheng += "外西南";
		} else if (ganzhiri.equals("戊寅") || ganzhiri.equals("己卯")) {
			taisheng += "外正南";
		} else if (ganzhiri.equals("庚辰") || ganzhiri.equals("辛巳")) {
			taisheng += "外正西";
		} else if (ganzhiri.equals("壬午") || ganzhiri.equals("甲申")
				|| ganzhiri.equals("癸未") || ganzhiri.equals("乙酉")
				|| ganzhiri.equals("丙戌") || ganzhiri.equals("丁亥")) {
			taisheng += "外西北";
		} else if (ganzhiri.equals("戊子") || ganzhiri.equals("己丑")
				|| ganzhiri.equals("庚寅") || ganzhiri.equals("辛卯")
				|| ganzhiri.equals("壬辰")) {
			taisheng += "外正北";
		} else if (ganzhiri.equals("癸巳") || ganzhiri.equals("甲午")
				|| ganzhiri.equals("乙未") || ganzhiri.equals("丙申")
				|| ganzhiri.equals("丁酉")) {
			taisheng += "房内北";
		} else if (ganzhiri.equals("戊戌") || ganzhiri.equals("己亥")
				|| ganzhiri.equals("庚子") || ganzhiri.equals("辛丑")
				|| ganzhiri.equals("壬寅") || ganzhiri == "癸卯") {
			taisheng += "房内南";
		} else if (ganzhiri.equals("甲辰") || ganzhiri.equals("乙巳")
				|| ganzhiri.equals("丙午") || ganzhiri.equals("丁未")
				|| ganzhiri.equals("戊申")) {
			taisheng += "房内东";
		} else if (ganzhiri.equals("己酉") || ganzhiri.equals("庚戌")
				|| ganzhiri.equals("辛亥") || ganzhiri.equals("壬子")
				|| ganzhiri.equals("癸丑") || ganzhiri.equals("甲寅")) {
			taisheng += "外东北";
		} else if (ganzhiri.equals("乙卯") || ganzhiri.equals("丙辰")
				|| ganzhiri.equals("丁巳") || ganzhiri.equals("戊午")
				|| ganzhiri.equals("己未")) {
			taisheng += "外正东";
		} else if (ganzhiri.equals("庚申") || ganzhiri.equals("辛酉")
				|| ganzhiri.equals("壬戌") || ganzhiri.equals("癸亥")
				|| ganzhiri.equals("甲子") || ganzhiri.equals("乙丑")) {
			taisheng += "外东南";
		} else if (ganzhiri.equals("丙寅") || ganzhiri.equals("丁卯")
				|| ganzhiri.equals("戊辰") || ganzhiri.equals("己巳")
				|| ganzhiri.equals("庚午")) {
			taisheng += "外正南";
		}
		return taisheng;
	}

	public String getChongString() {

		int zhi = getZhiDayIndex();

		String temp = Animals[zhi] + "日冲";

		if (zhi < 6) {
			zhi += 6;
		} else {
			zhi -= 6;
		}
		return temp + Animals[zhi];
	}

	private final static int[] solarTermInfo = { 0, 21208, 42467, 63836, 85337,
			107014, 128867, 150921, 173149, 195551, 218072, 240693, 263343,
			285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795,
			462224, 483532, 504758 };

	public final static String[] solarTerm = new String[]{
			"小寒", "大寒", "立春", "雨水", "惊蛰", "春分",
			"清明", "谷雨", "立夏", "小满", "芒种", "夏至",
			"小暑", "大暑", "立秋", "处暑", "白露", "秋分",
			"寒露", "霜降", "立冬", "小雪", "大雪", "冬至"
	};

	private static GregorianCalendar utcCal = null;

	/**
	 * 返回公历年节气的日期
	 *
	 * @param solarYear
	 *            指定公历年份(数字)
	 * @param index
	 *            指定节气序号(数字,0从小寒算起)
	 * @return 日期(数字,所在月份的第几天)
	 */
	public static int getSolarTermDay(int solarYear, int index) {
		long l = (long) 31556925974.7 * (solarYear - 1900)
				+ solarTermInfo[index] * 60000L;
		l = l + GMT(1900, 0, 6, 2, 5, 0); // 这里可能导致时区不同出现的计算结果错误??
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(l);
		return getUTCDayInteger(date);
	}

	/**
	 * 返回公历年节气的日期
	 *
	 * @param solarYear
	 *            指定公历年份(数字)
	 * @param index
	 *            指定节气序号(数字,0从小寒算起)
	 * @return 日期(毫秒数,所在月份的第几天)
	 */
	public static Long getSolarTermDayLong(int solarYear, int index) {
		long l = (long) 31556925974.7 * (solarYear - 1900)
				+ solarTermInfo[index] * 60000L;
		l = l + GMT(1900, 0, 6, 2, 5, 0); // 这里可能导致时区不同出现的计算结果错误??
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(l);
		return getUTCDayLong(date);
	}

	/**
	 * 获得该年所有节气的毫秒数
	 * @param year
	 * @return
	 */
	public static Long[] getSolarTermInYear(int year) {
		Long[] result = new Long[24];
		for(int i = 0; i < 24; i++) {
			result[i] = getSolarTermDayLong(year, i);
		}
		return result;
	}

	/**
	 * 返回全球标准时间 (UTC) (或 GMT) 的 1970 年 1 月 1 日到所指定日期之间所间隔的毫秒数。
	 *
	 * @param y
	 *            指定年份
	 * @param m
	 *            指定月份
	 * @param d
	 *            指定日期
	 * @param h
	 *            指定小时
	 * @param min
	 *            指定分钟
	 * @param sec
	 *            指定秒数
	 * @return 全球标准时间 (UTC) (或 GMT) 的 1970 年 1 月 1 日到所指定日期之间所间隔的毫秒数
	 */
	public static synchronized long GMT(int y, int m, int d, int h, int min,
										int sec) {
		makeGMTCalendar();
		synchronized (utcCal) {
			utcCal.clear();
			utcCal.set(y, m, d, h, min, sec);
			return utcCal.getTimeInMillis();
		}
	}

	private static synchronized void makeGMTCalendar() {
		if (utcCal == null) {
			utcCal = new GregorianCalendar(TimeZone.getDefault());
		}
	}

	/**
	 * 取 Date 对象中用全球标准时间 (UTC) 表示的日期
	 *
	 * @param date
	 *            指定日期
	 * @return UTC 全球标准时间 (UTC) 表示的日期
	 */
	public static synchronized int getUTCDayInteger(Calendar date) {
		makeGMTCalendar();
		synchronized (utcCal) {
			utcCal.clear();
			utcCal.setTimeInMillis(date.getTimeInMillis());
			return utcCal.get(Calendar.DAY_OF_MONTH);
		}
	}

	/**
	 * 取 Date 对象中用全球标准时间 (UTC) 表示的日期
	 *
	 * @param date
	 *            指定日期
	 * @return UTC 全球标准时间 (UTC) 表示的日期
	 */
	public static synchronized long getUTCDayLong(Calendar date) {
		makeGMTCalendar();
		synchronized (utcCal) {
			utcCal.clear();
			utcCal.setTimeInMillis(date.getTimeInMillis());
			return utcCal.getTimeInMillis();
		}
	}
}