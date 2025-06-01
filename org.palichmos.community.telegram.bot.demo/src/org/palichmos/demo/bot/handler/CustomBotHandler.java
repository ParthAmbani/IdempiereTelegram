package org.palichmos.demo.bot.handler;

import java.util.ArrayList;

import org.palichmos.demo.bot.IPalichmosdemoBotKeyboard;
import org.palichmos.demo.bot.IPalichmosdemoBotState;
import org.palichmos.telegram.model.AbstractBotHandler;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public abstract class CustomBotHandler extends AbstractBotHandler implements IPalichmosdemoBotKeyboard, IPalichmosdemoBotState
{
	protected ReplyKeyboard getMenuBackButton()
	{
		ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
		keyboard.setResizeKeyboard(true);
		ArrayList<KeyboardRow> rows = new ArrayList<KeyboardRow>();
		keyboard.setKeyboard(rows);

		KeyboardRow rowButton = new KeyboardRow();
		rows.add(rowButton);
		KeyboardButton button = new KeyboardButton(COM_BACK);
		rowButton.add(button);
		
		return keyboard;
	}
	
}