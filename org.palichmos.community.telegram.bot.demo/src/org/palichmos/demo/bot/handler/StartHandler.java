package org.palichmos.demo.bot.handler;

import java.sql.SQLException;
import org.palichmos.telegram.model.Session;

public class StartHandler extends CustomBotHandler
{
	@Override
	public int getPriority()
	{
		return 1;
	}

	@Override
	public boolean onEvent(Session session) throws SQLException
	{
		if (session.getUpdate().getMessage().getText().equals(COM_START))
			return true;

		if (session.getUpdate().getMessage().getText().equals("Name"))
			return true;

		return false;
	}

	@Override
	public void doIt(Session session) throws Exception
	{
		sendMessage(session, "Hello " + session.getUpdate().getMessage().getChat().getFirstName() +" " + session.getUpdate().getMessage().getChat().getLastName(), null);
		
		session.getUser().setCurrentState(STATE_001);
		session.getUser().addValue("Answer", 42);
		session.getUser().saveEx();
	}

}