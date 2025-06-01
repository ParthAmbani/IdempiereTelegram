package org.palichmos.telegram.model;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Properties;

import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.palichmos.telegram.adinterface.IBotHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractTelegramBot extends TelegramLongPollingBot
{
	protected final static CLogger log = CLogger.getCLogger(AbstractTelegramBot.class);
	
	private final LinkedList<IBotHandler> handlers = new LinkedList<IBotHandler>();
	private final Properties defaultCtx = new Properties();
	private int pm_telegram_bot_id = 1000000;
	private String token = null;
	private String botUsername = null;
	private BotMessageExecutor threadExecutor = null;
	
	public AbstractTelegramBot(int pm_telegram_bot_id, int maxThread)
	{
		this.threadExecutor = new BotMessageExecutor(maxThread);
		this.pm_telegram_bot_id = pm_telegram_bot_id;
		
		initializeHandlers();
	
		//We sort the handlers according to their priority. The higher the priority number, the sooner the handler will fire
		if (handlers.size() > 1)
		{
			handlers.sort(new Comparator<IBotHandler>()
			{
				@Override
				public int compare(IBotHandler o1, IBotHandler o2) 
				{
					if (o1.getPriority() == o2.getPriority()) 
						return 0;
					else if (o1.getPriority() < o2.getPriority())
						return 1;
					else
						return -1;
				}
			});
		}
		
		//Set default minimal context variable
		Env.setContext(defaultCtx, "#AD_Client_ID", DB.getSQLValueEx(null, "SELECT AD_Client_ID FROM pm_telegrambot WHERE pm_telegrambot_id = ?", pm_telegram_bot_id));
		Env.setContext(defaultCtx, "AD_Client_ID", DB.getSQLValueEx(null, "SELECT AD_Client_ID FROM pm_telegrambot WHERE pm_telegrambot_id = ?", pm_telegram_bot_id));
		Env.setContext(defaultCtx, "#AD_Org_ID", DB.getSQLValueEx(null, "SELECT AD_Org_ID FROM pm_telegrambot WHERE pm_telegrambot_id = ?", pm_telegram_bot_id));
		Env.setContext(defaultCtx, "AD_Org_ID", DB.getSQLValueEx(null, "SELECT AD_Org_ID FROM pm_telegrambot WHERE pm_telegrambot_id = ?", pm_telegram_bot_id));
	}
	
	@Override
	public void onUpdateReceived(Update update)
	{
		threadExecutor.execute(new UpdateReceivedWorker(this, update, defaultCtx, handlers));
	}
	
	protected abstract void initializeHandlers();
	
	public int getPM_Telegram_Bot_ID()
	{
		return pm_telegram_bot_id;
	}
	
	protected void registerHandler(IBotHandler handler)
	{
		if (handler != null)
			handlers.add(handler);
	}
	
	@Override
	public String getBotUsername() 
	{
		if (botUsername == null)
			botUsername = DB.getSQLValueString(null, "SELECT Name FROM pm_telegrambot WHERE pm_telegrambot_id = ?", pm_telegram_bot_id);
		
		return botUsername;
	}
	
	@Override
	public String getBotToken()
	{
		if (token == null)
			token = DB.getSQLValueString(null, "SELECT BotToken FROM pm_telegrambot WHERE pm_telegrambot_id = ?", pm_telegram_bot_id);
		
		return token;
	}

}