package org.palichmos.telegram.model;

import java.sql.SQLException;
import java.util.Properties;

import org.compiere.util.Env;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class Session {

	private Update update = null;
	private Long chatID = null;
	private TelegramLongPollingBot botInstance = null;
	private User user = null;
	private String trxName = null;
	private Class<?> nextRunHandler = null;

	public Session(AbstractTelegramBot botInstance, Update update, String trxName) throws SQLException {
		this.trxName = trxName;
		this.update = update;

		if (update.hasMessage())
			this.chatID = update.getMessage().getChatId();
		else if (update.hasCallbackQuery())
			this.chatID = update.getCallbackQuery().getFrom().getId();
		else if (update.hasInlineQuery())
			this.chatID = update.getInlineQuery().getFrom().getId();

		this.botInstance = botInstance;
		this.user = new User(botInstance.getPM_Telegram_Bot_ID(), chatID, trxName);
	}

	public Properties getCtx() {
		return Env.getCtx();
	}

	public String getTrxName() {
		return trxName;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public Long getChatID() {
		return chatID;
	}

	public Update getUpdate() {
		return update;
	}

	public void setUpdate(Update update) {
		this.update = update;
	}

	public User getUser() {
		return user;
	}

	public TelegramLongPollingBot getBotInstance() {
		return botInstance;
	}

	public Class<?> getNextRunHandler() {
		return nextRunHandler;
	}

	public void setNextRunHandler(Class<?> nextRunHandler) {
		this.nextRunHandler = nextRunHandler;
	}
}
