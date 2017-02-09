package com.coreService.util;
import java.util.*;
public class AutoRecycleTask extends TimerTask{
	@Override
	public void run() {
		try{
			TalkManager.recycleTalk();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}