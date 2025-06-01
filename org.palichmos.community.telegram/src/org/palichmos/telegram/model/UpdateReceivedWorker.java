package org.palichmos.telegram.model;

import java.util.List;
import java.util.Properties;

import org.adempiere.util.ServerContext;
import org.compiere.util.CLogger;
import org.compiere.util.Trx;
import org.palichmos.telegram.adinterface.IBotHandler;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class UpdateReceivedWorker implements Runnable
{
	private final static CLogger log = CLogger.getCLogger(UpdateReceivedWorker.class);
	
	private AbstractTelegramBot instance = null;
	private Update update = null;
	private List<IBotHandler> handlers = null;
	private Properties defaultCtx = null;
	
	public UpdateReceivedWorker(AbstractTelegramBot instance, Update update, Properties defaultCtx, List<IBotHandler> handlers)
	{
		this.instance = instance;
		this.update = update;
		this.defaultCtx = defaultCtx;
		this.handlers = handlers;
	}
	
	@Override
	public void run()
	{
		Trx trx = null;
		Session session = null;
		
		try
		{
			Properties localCtx = new Properties();
			localCtx.putAll(defaultCtx);
			ServerContext.setCurrentInstance(localCtx);
			
			trx = Trx.get(Trx.createTrxName("BOT" + instance.getPM_Telegram_Bot_ID()), true);
			session = new Session(instance, update, trx.getTrxName());
			
			for (IBotHandler handler : handlers)
			{
				if (handler.onEvent(session))
				{
					handler.doIt(session);
					
					if (handler.isHideInlineButton())
						onHideInlineButton(update);
					
					while (session.getNextRunHandler() != null)
					{
						for (IBotHandler nextHandler : handlers)
						{
							if (nextHandler.getClass().equals(session.getNextRunHandler()))
							{
								session.setNextRunHandler(null);
								nextHandler.doIt(session);
								
								break;
							}
						}
					}
					
					break;
				}
			}
			
			trx.commit(true);
		}
		catch (Exception e)
		{
			if (e instanceof TelegramApiRequestException)
				log.warning(((TelegramApiRequestException) e).getApiResponse());
			else
				log.warning(e.getMessage());
			
			trx.rollback();
		}
		finally
		{
			if (trx != null)
				trx.close();
			
			ServerContext.dispose();
		}
	}
	
	private void onHideInlineButton(Update update) 
	{
		if (!update.hasCallbackQuery())
			return;
		
		EditMessageText editMessage = null;
		EditMessageCaption editMessagePhoto = null;
		
		if (update.getCallbackQuery().getMessage().hasPhoto())
		{
			editMessagePhoto = new EditMessageCaption();
			editMessagePhoto.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
			editMessagePhoto.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
			editMessagePhoto.setCaption(update.getCallbackQuery().getMessage().getCaption());
		}
		else
		{
			editMessage = new EditMessageText();
			editMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
			editMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
			editMessage.enableHtml(true);
			editMessage.setText(update.getCallbackQuery().getMessage().getText());
		}
		
		//Remove the keyboard from the previous message
		try
		{
			instance.execute(editMessage);
		} 
		catch (TelegramApiException e)
		{
			log.warning(e.getMessage());
		}
	}
	
	public Long getChatID()
	{
		if (update == null)
			return null;
		else if (update.hasMessage())
			return update.getMessage().getChatId();
		else if (update.hasCallbackQuery())
			return Long.valueOf(update.getCallbackQuery().getFrom().getId());
		else if (update.hasInlineQuery())
			return Long.valueOf(update.getInlineQuery().getFrom().getId());
		
		return null;
	}

}