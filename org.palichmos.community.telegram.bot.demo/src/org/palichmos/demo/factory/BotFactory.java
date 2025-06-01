package org.palichmos.demo.factory;

import java.util.ArrayList;
import java.util.Collection;

import org.palichmos.demo.bot.PalichmosDemoBot;
import org.palichmos.telegram.adinterface.ITelegramBotFactory;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

public class BotFactory implements ITelegramBotFactory
{
	@Override
	public Collection<LongPollingBot> getTelegramBots()
	{
		ArrayList<LongPollingBot> bots = new ArrayList<LongPollingBot>();
		bots.add(PalichmosDemoBot.getInstance());
		return bots;
	}

}