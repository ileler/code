package org.kerwin.weibo.service;

import java.util.Observable;
import java.util.Observer;

public abstract class FrameEvent<E> extends Observable implements Observer{
	
	public FrameEvent(){
		this.addObserver(this);
	}
	
	public void call(E info, Object... args){
		call(new Request(info,args));
	}

	public abstract void echo(Observable arg0, Request arg1);
	
	private void call(final Request request){
		if(countObservers() == 0){
//			log.debug("未监听任何对象");
			return;
		}
		super.setChanged();
		new Thread(new Runnable(){
			public void run(){
				notifyObservers(request);
			}
		}).start();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable arg0, Object arg1) {
		echo(arg0,(Request)arg1);
	}

	public class Request{
		private E info;
		private Object[] args;
		public Request(E info){
			this.info = info;
		}
		public Request(E info,Object[] args){
			this(info);
			this.args = args;
		}
		public E getInfo() {
			return info;
		}
		public void setInfo(E info) {
			this.info = info;
		}
		public Object[] getArgs() {
			return args;
		}
		public void setArgs(Object[] args) {
			this.args = args;
		}
		
	}
}
