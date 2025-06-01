package org.palichmos.telegram.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class User
{
	private Long chatID = null;
	private String currentState = null;
	private Map<String, Object> oldStateValues = new HashMap<String, Object>();
	private Map<String, Object> newStateValues = new HashMap<String, Object>();
	private String trxName = null;
	private int pm_telegram_bot_id = -1;
	
	@SuppressWarnings("unused")
	private User() { }
	
	public User(int pm_telegram_bot_id, Long chatID, String trxName) throws SQLException
	{
		if (chatID == null)
			return;
		
		this.chatID = chatID;
		this.pm_telegram_bot_id = pm_telegram_bot_id;
		this.trxName = trxName;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			pstmt = DB.prepareStatement("SELECT currentState, COALESCE(stateValue, '') AS stateValue FROM pm_telegram_userstate WHERE pm_telegrambot_id = ? AND TelegramAccountID = ? AND AD_Client_ID = ? AND AD_Org_ID = ?", trxName);
			pstmt.setInt(1, pm_telegram_bot_id);
			pstmt.setInt(2, chatID.intValue());
			pstmt.setInt(3, Env.getAD_Client_ID(Env.getCtx()));
			pstmt.setInt(4, Env.getAD_Org_ID(Env.getCtx()));
			rs = pstmt.executeQuery();
			
			if (rs.next())
			{
				this.currentState = rs.getString("currentState");
				
				for (String line : rs.getString("stateValue").split("&&"))
				{
					if (line.isEmpty())
						continue;
					
					String[] keyName = line.split("##");
					oldStateValues.put(keyName[0], keyName[1]);
				}
			}
			else
			{
				//Create first record user state
				int PM_Telegram_UserState_ID = DB.getNextID(Env.getAD_Client_ID(Env.getCtx()), "pm_telegram_userstate", trxName);
				DB.executeUpdateEx("INSERT INTO pm_telegram_userstate (pm_telegram_userstate_id, currentState, TelegramAccountID, pm_telegrambot_id, AD_Client_ID, AD_Org_ID) VALUES (?, ?, ?, ?, ?, ?)", 
						new Object[] {PM_Telegram_UserState_ID, null, chatID.intValue(), pm_telegram_bot_id, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx())}, trxName);
			}
		}
		catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			DB.close(rs, pstmt);
		}
	}
	
	public Object getValue(String name)
	{
		if (oldStateValues == null || !oldStateValues.containsKey(name))
			return null;
		
		return oldStateValues.get(name);
	}
	
	public int getValueAsInt(String name)
	{
		if (oldStateValues == null || !oldStateValues.containsKey(name))
			return 0;
		
		int retValue = 0;
		
		try
		{
			if (oldStateValues.get(name) instanceof Integer)
				retValue = (int) oldStateValues.get(name);
			else
				retValue = Integer.valueOf(oldStateValues.get(name).toString());
		}
		catch (Exception e)
		{
			retValue = 0;
		}
		
		return retValue;
	}
	
	public void addValue(String name, Object value)
	{
		newStateValues.put(name, value);
	}
	
	public String getCurrentState()
	{
		return (currentState != null)? currentState : "";
	}

	public void setCurrentState(String currentState)
	{
		this.currentState = currentState;
	}
	
	/**
	 * Saved current user state and all values. Values must be re-specified, because they are not re-saved
	 */
	public void saveEx()
	{
		String lines = "";
		
		for (Entry<String, Object> entry : newStateValues.entrySet())
		{
			if (!lines.isEmpty())
				lines += "&&";
				
			lines += entry.getKey() + "##" + entry.getValue();
		}
		
		DB.executeUpdateEx("UPDATE pm_telegram_userstate SET Updated = NOW(), currentState = ?, stateValue = ? WHERE pm_telegrambot_id = ? AND TelegramAccountID = ? AND AD_Client_ID = ? AND AD_Org_ID = ?",
							new Object[] {Integer.valueOf(currentState), lines, pm_telegram_bot_id, chatID.intValue(), Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx())}, trxName);
	}
	
}