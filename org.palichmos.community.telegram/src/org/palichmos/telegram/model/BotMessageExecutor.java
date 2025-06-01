package org.palichmos.telegram.model;

import java.util.concurrent.LinkedBlockingQueue;
import org.compiere.util.CLogger;

public class BotMessageExecutor 
{
	private final CLogger log = CLogger.getCLogger(BotMessageExecutor.class);
	
	private int maxThread = 1;
	private BotMessageQueueExecutor[] queueExecutors = null;
	private LinkedBlockingQueue<UpdateReceivedWorker> waitQueue = null;
	
	@SuppressWarnings("unused")
	private BotMessageExecutor() { }
	
	public BotMessageExecutor(int maxThread)
	{
		this.maxThread = maxThread;
		this.queueExecutors = new BotMessageQueueExecutor[maxThread];
		this.waitQueue = new LinkedBlockingQueue<UpdateReceivedWorker>();
		
		for (int i = 0; i < maxThread; i++)
		{
			queueExecutors[i] = new BotMessageQueueExecutor();
		}
		
		Thread queueConsumer = new Thread(new QueueConsumer());
		queueConsumer.start();
	}
	
	public void execute(UpdateReceivedWorker command)
	{
		try 
		{
			waitQueue.put(command);
		} 
		catch (InterruptedException e) 
		{
			log.warning(e.getMessage());
		}
		
		synchronized (waitQueue)
		{
			waitQueue.notifyAll();
		}
	}
	
	private class QueueConsumer implements Runnable
	{
		@Override
		public void run()
		{
			while (true)
			{
				try 
				{
					UpdateReceivedWorker command = waitQueue.peek();
					
					if (command == null)
					{
						synchronized (waitQueue)
						{
							waitQueue.wait();
							command = waitQueue.peek();
							
							if (command == null)
								continue;
						}
					}
					
					BotMessageQueueExecutor executor = getAvailableExecutor(command.getChatID());
					executor.execute(waitQueue.poll());
				} 
				catch (InterruptedException e)
				{
					log.warning(e.getMessage());
				}
			}
		}
		
	}

	public BotMessageQueueExecutor getAvailableExecutor(Long chatID) throws InterruptedException
	{
		BotMessageQueueExecutor retValue = null;
		
		while (retValue == null)
		{
			for (int i = 0; i < maxThread; i++)
			{
				if (!queueExecutors[i].isActive() 
						|| queueExecutors[i].getProcessingChatID() == null 
						|| queueExecutors[i].getProcessingChatID().compareTo(chatID) == 0)
				{
					return queueExecutors[i];
				}
			}
			
			if (retValue == null)
				Thread.sleep(50);
		}
		
		return retValue;
	}
	
}