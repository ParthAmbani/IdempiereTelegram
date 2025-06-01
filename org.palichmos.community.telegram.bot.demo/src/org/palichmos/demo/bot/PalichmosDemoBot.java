package org.palichmos.demo.bot;

import org.palichmos.demo.bot.handler.StartHandler;
import org.palichmos.telegram.model.AbstractTelegramBot;

public class PalichmosDemoBot extends AbstractTelegramBot
{
	private static PalichmosDemoBot instance = null;
	private static int pm_telegram_bot_id = 1000000;
	
	private PalichmosDemoBot(int pm_telegram_bot_id, int maxThread)
	{
		super(pm_telegram_bot_id, maxThread);
	}
	
	public static synchronized PalichmosDemoBot getInstance()
	{
		if (instance == null)
			instance = new PalichmosDemoBot(pm_telegram_bot_id, 5);
		
		return instance;
	}

	@Override
	protected void initializeHandlers()
	{
		registerHandler(new StartHandler());
	}
	
}