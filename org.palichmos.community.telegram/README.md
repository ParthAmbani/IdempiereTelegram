# OSGI plugin "org.ipalich.community.telegram" for iDempiere

## Description

This plugin is the basis for creating your Telegram bots.
Telegram bots are essentially your smart and cost effective way to have a mobile interface working with your iDempiere.
Every Android or iOS device can run Telegram. More on this here https://core.telegram.org/bots

The plugin provides:

library for the bot https://github.com/rubenlagus/TelegramBots
library for supporting emoji https://github.com/vdurmont/emoji-java
functionality for launching bots as an iDempiere plugin
Additionally, you can find a [example plugin](https://github.com/NikColonel/org.palichmos.community.telegram.bot.demo) that will help you understand how this works.

## Features
* Support for the basic features of the Telegram API
* Use emoji in the message
* You can create a separate plugin for each bot. Stop \ start plug-in affect the operation of the bot
* Automatically launch all bots at system startup
* In your bot, you can access all iDempiere classes. For example MOrder, DB and others.
