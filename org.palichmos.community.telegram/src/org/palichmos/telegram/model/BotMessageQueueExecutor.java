package org.palichmos.telegram.model;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BotMessageQueueExecutor
{
	private ThreadPoolExecutor executor = null;
	private Long processingChatID = null;
	
	public BotMessageQueueExecutor()
	{
		this.executor = new ThreadPoolExecutor(1, 1, 0l, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	public void execute(UpdateReceivedWorker command)
	{
		this.executor.execute(command);
		this.processingChatID = command.getChatID();
	}
	
	public Long getProcessingChatID()
	{
		return this.processingChatID;
	}
	
	public boolean isActive()
	{
		return this.executor.getActiveCount() > 0;
	}
	
}