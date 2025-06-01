package org.palichmos.telegram.adinterface;

import java.util.Collection;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

public interface ITelegramBotFactory {
    /**
     * Return all Telegram bots implemented by this factory
     * @return Collection of LongPollingBot implementations
     */
    Collection<LongPollingBot> getTelegramBots();
}
