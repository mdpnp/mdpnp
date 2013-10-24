package org.mdpnp.devices.net;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class TaskQueue {
	
	private final ThreadLocal<List<Task<?>>> tasksToDo = new ThreadLocal<List<Task<?>>>() {
		protected java.util.List<TaskQueue.Task<?>> initialValue() {
			return new ArrayList<Task<?>>();
		}
	};
	
	public long doExpiredTasks(long now) {
		List<Task<?>> tasksToDo = this.tasksToDo.get();
		
		tasksToDo.clear();
		
		synchronized (this) {
			for(Task<?> t : queue) {
				if(t.getScheduledTime()<=now) {
					tasksToDo.add(t);
				}
			}
			queue.removeAll(tasksToDo);
			this.notifyAll();
		}
		if(!tasksToDo.isEmpty()) {
			for(Task<?> t : tasksToDo) {
				t.execute(this);
			}
		} 
		return timeToNextTask(now);
	}
	
	public final synchronized long timeToNextTask(long now) {
		if(queue.isEmpty()) {
			return 0L;
		} else {
			return queue.first().getScheduledTime() - now;
		}
	}
	
	public interface Task<T> extends Comparable<Task<T>> {
		long getScheduledTime();
		void setScheduledTime(long l);
		T execute(TaskQueue queue);
		T waitForResult();
		boolean isRecurrent();
		long getInterval();
		void setInterval(long l);
	}
	
	public abstract static class TaskImpl<T> implements Task<T> {
		private long scheduledTime;
		private T t;
		private Throwable e;
		private long interval;
		
		public abstract T doExecute(TaskQueue queue);
		
		@Override
		public synchronized T waitForResult() {
			while(null == t && null == e) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(null != e) {
				throw new RuntimeException(e);
			} else {
				return this.t;
			}
		}
		
		@Override
		public final T execute(TaskQueue queue) {
			try {
				T t = doExecute(queue);
				synchronized(this) {
					if(isRecurrent()) {
						setScheduledTime(System.currentTimeMillis()+getInterval());
						queue.add(this);
					}
					this.t = t;
					this.notifyAll();
				}
				return t;
			} catch(Throwable e) {
				synchronized(this) {
					this.e = e;
					this.notifyAll();
				}
			}
			return null;
		}
		
		@Override
		public void setScheduledTime(long l) {
			this.scheduledTime = l;
		}
		@Override
		public long getScheduledTime() {
			return scheduledTime;
		}
		@Override
		public int compareTo(Task<T> o) {
			if(scheduledTime < o.getScheduledTime()) {
				return -1;
			} else if(scheduledTime > o.getScheduledTime()) {
				return 1;
			} else {
				if(this.equals(o)) {
					return 0;
				} else {
					// TODO terribly arbitrary
					return -1;
				}
			}
			
		}
		
		public boolean isRecurrent() {
			return interval > 0L;
		}
		@Override
		public long getInterval() {
			return interval;
		}
		@Override
		public void setInterval(long l) {
			this.interval = l;
		}
		
	}
	
	private final SortedSet<Task<?>> queue = new TreeSet<Task<?>>();
	
	public synchronized void add(Task<?> task) {
		queue.add(task);
	}
	public synchronized void clear() {
		queue.clear();
	}
}
