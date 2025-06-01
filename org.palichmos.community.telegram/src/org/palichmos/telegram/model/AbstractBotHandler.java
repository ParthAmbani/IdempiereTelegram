package org.palichmos.telegram.model;

import org.compiere.util.CLogger;
import org.palichmos.telegram.adinterface.IBotHandler;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.EmojiParser;

public abstract class AbstractBotHandler implements IBotHandler {

    protected static final CLogger log = CLogger.getCLogger(AbstractBotHandler.class);

    @Override
    public boolean isHideInlineButton() {
        return true;
    }

    protected void sendMessage(Session session, String text, ReplyKeyboard keyboard) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(session.getChatID().toString())
                .text(EmojiParser.parseToUnicode(text))
                .replyMarkup(keyboard)
                .parseMode("HTML")
                .disableWebPagePreview(true)
                .disableNotification(true)
                .build();

        try {
            session.getBotInstance().execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warning(e.getMessage() + " TID: " + session.getChatID());
            throw e;
        }
    }

    protected void sendMessageToChatID(Session session, Long chatID, String text, ReplyKeyboard keyboard) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatID.toString())
                .text(EmojiParser.parseToUnicode(text))
                .replyMarkup(keyboard)
                .parseMode("HTML")
                .disableWebPagePreview(true)
                .build();

        try {
            session.getBotInstance().execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warning(e.getMessage() + " TID: " + session.getChatID());
            throw e;
        }
    }

    protected void sendPhoto(Session session, String urlImage) throws TelegramApiException {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(session.getChatID().toString())
                .photo(new InputFile(urlImage))  // Wrap the String URL into InputFile here
                .disableNotification(true)
                .build();

        try {
            session.getBotInstance().execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.warning(e.getMessage() + " URL: " + urlImage + " TID: " + session.getChatID());
            throw e;
        }
    }

    protected void sendAlertCallbackQuery(Session session, String message, boolean isModal) throws TelegramApiException {
        if (!session.getUpdate().hasCallbackQuery()) {
            log.warning("Callback query not found");
            return;
        }

        AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(session.getUpdate().getCallbackQuery().getId())
                .text(message)
                .showAlert(isModal)
                .build();

        session.getBotInstance().execute(answer);
    }

    protected void sendAlertCallbackQueryOrMessage(Session session, String message, boolean isModal) throws TelegramApiException {
        if (!session.getUpdate().hasCallbackQuery()) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(session.getChatID().toString())
                    .text(message)
                    .parseMode("HTML")
                    .disableWebPagePreview(true)
                    .build();

            try {
                session.getBotInstance().execute(sendMessage);
            } catch (TelegramApiException e) {
                log.warning(e.getMessage());
            }
            return;
        }

        AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(session.getUpdate().getCallbackQuery().getId())
                .text(message)
                .showAlert(isModal)
                .build();

        try {
            session.getBotInstance().execute(answer);
        } catch (Exception e) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(session.getChatID().toString())
                    .text(message)
                    .parseMode("HTML")
                    .disableWebPagePreview(true)
                    .build();

            try {
                session.getBotInstance().execute(sendMessage);
            } catch (TelegramApiException ex) {
                log.warning(ex.getMessage());
            }
        }
    }
}
