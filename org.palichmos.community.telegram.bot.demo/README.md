# Example OSGI plugin "org.ipalich.community.telegram" for iDempiere

# Usage

  1. Register your Telegram bot with [BotFather](https://t.me/BotFather)
  2. Start iDempiere application
  3. Install plugin [org.ipalich.community.telegram](https://github.com/NikColonel/org.palichmos.community.telegram) with OSGI or Felix console
  4. Open iDempiere and log in
  5. Open window "Telegram bot" and create new record. Set fields name and token (token gave your BotFather). Save record and copy RecordID (pm_telegram_bot_id)
  6. Open source code this example plugin. Open class "PalichmosdemoBot.java" and set your pm_telegram_bot_id for variable "pm_telegram_bot_id"
   
   ```java
      private static int pm_telegram_bot_id = 1000000;
   ```
   
  7. Build this plugin and install with OSGI or Felix console
  8. Open your bot and write "/start"
