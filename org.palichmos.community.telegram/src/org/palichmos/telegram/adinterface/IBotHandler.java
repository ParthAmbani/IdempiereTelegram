package org.palichmos.telegram.adinterface;

import java.sql.SQLException;
import org.palichmos.telegram.model.Session;

public interface IBotHandler
{
	/**
	 * Returns the priority of the handler. The higher the priority number, the sooner the handler will fire. Return 1 by default
	 * @return
	 */
	public int getPriority();
	
	/**
	 * Return true, if need hide inline keyboard (buttons under message) after processing query
	 * @return
	 */
	public boolean isHideInlineButton();
	
	/**
	 * First handler that return true will be responds to the message
	 * @param update - message in bot
	 * @return
	 * @throws SQLException 
	 */
	public boolean onEvent(Session session) throws SQLException;
	
	/**
	 * Action performed by this handler
	 * @param update
	 * @throws Exception 
	 */
	public void doIt(Session session) throws Exception;
	
}