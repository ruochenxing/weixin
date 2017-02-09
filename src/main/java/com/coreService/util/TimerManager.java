package com.coreService.util;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import com.coreService.util.Constants;
public class TimerManager {
	/**
	 * 时，分，秒，内容，邮箱，密码(可以是原密码，也可以是40位sha1加密后的密码)
	 */
	public TimerManager() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,Constants.hour);
		calendar.set(Calendar.MINUTE,Constants.minute);
		calendar.set(Calendar.SECOND,Constants.second);
		if(!calendar.getTimeZone().getID().equals("Asia/Shanghai")){
			calendar.add(Calendar.HOUR_OF_DAY,-8);//git服务器获取的是UTC时间，所以要将设置的时间转成UTC时间
		}
		Date date=calendar.getTime();
		Timer timer = new Timer();
		AutoRecycleTask task = new AutoRecycleTask();
		System.out.println("auto AutoRecycleTask will begin at "+date);
		//定时执行
		timer.schedule(task,date,Constants.RECYCLE_PERIOD_DAY);
	}
}