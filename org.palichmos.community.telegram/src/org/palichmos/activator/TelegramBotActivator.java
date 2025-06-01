package org.palichmos.activator;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.compiere.util.CLogger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.palichmos.telegram.adinterface.ITelegramBotFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBotActivator implements BundleActivator, ServiceTrackerCustomizer<Object, Object> {
    private static BundleContext bundleContext;
    private final static CLogger logger = CLogger.getCLogger(TelegramBotActivator.class);
    private static final Map<String, BotSession> bots = new HashMap<>();

    static BundleContext getContext() {
        return bundleContext;
    }

    @Override
    public void start(BundleContext context) {
        bundleContext = context;
        ServiceTracker<Object, Object> registryTracker =
                new ServiceTracker<>(context, ITelegramBotFactory.class.getName(), this);
        registryTracker.open();
    }

    @Override
    public Object addingService(ServiceReference<Object> reference) {
        logger.log(Level.INFO, "Adding Telegram bot service...");
        ITelegramBotFactory factory = (ITelegramBotFactory) bundleContext.getService(reference);

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            for (LongPollingBot bot : factory.getTelegramBots()) {
                try {
                    BotSession sessionBot = telegramBotsApi.registerBot(bot);
                    bots.put(bot.getBotToken(), sessionBot);
                } catch (TelegramApiException e) {
                    logger.log(Level.SEVERE, "Error registering bot: " + bot.getBotUsername(), e);
                }
            }
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, "Error initializing TelegramBotsApi", e);
        }

        return factory;
    }

    @Override
    public void modifiedService(ServiceReference<Object> reference, Object service) {
        // No modification handling needed
    }

    @Override
    public void removedService(ServiceReference<Object> reference, Object service) {
        ITelegramBotFactory factory = (ITelegramBotFactory) bundleContext.getService(reference);

        for (LongPollingBot bot : factory.getTelegramBots()) {
            BotSession session = bots.get(bot.getBotToken());
            if (session != null) {
                session.stop();
                bots.remove(bot.getBotToken());
            }
        }

        bundleContext.ungetService(reference);
    }

    @Override
    public void stop(BundleContext context) {
        logger.log(Level.INFO, "Stopping Telegram bot activator...");
        bundleContext = null;
    }
}
