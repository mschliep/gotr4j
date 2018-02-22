package net.java.hsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class HSMContext {

    private static final Logger logger = LoggerFactory.getLogger(HSMContext.class);

    private volatile HSMState currentState;
    private volatile HSMState nextState;

    private final LinkedBlockingQueue<HSMEvent> queuedEvents = new LinkedBlockingQueue<HSMEvent>();

    private volatile ConcurrentLinkedQueue<HSMEvent> deferredEventsUse = new ConcurrentLinkedQueue<HSMEvent>();
    private volatile ConcurrentLinkedQueue<HSMEvent> deferredEventsFill = new ConcurrentLinkedQueue<HSMEvent>();
    private volatile Thread thread;
    private volatile boolean running = false;

    private final HSMExceptionHandler exceptionHandler;

    public HSMContext(HSMExceptionHandler exceptionHandler, String threadName, ThreadFactory threadFactory) {
        this.exceptionHandler = exceptionHandler;
        this.thread = threadFactory.newThread(new HSMRunnable());
        this.thread.setName(threadName);
        this.thread.isDaemon();
    }

    public void start() throws Exception{
        synchronized (thread) {
            if(!running) {
                this.running = true;
                this.thread.start();
            }
        }
    }

    public boolean isRunning(){
        synchronized (thread){
            return running;
        }
    }

    public void handelEvent(HSMEvent event) {
        queuedEvents.add(event);
    }

    public void deferEvent(HSMEvent event){
        deferredEventsFill.add(event);
    }

    public void setNextState(HSMState next){
        this.nextState = next;
    }

    public HSMState getCurrentState(){
        return this.currentState;
    }

    public void shutdown(){
        synchronized (thread){
            if(running) {
                this.running = false;
                thread.interrupt();
            }
        }
    }

    private void run() {
        while(running){
            if(this.nextState != null){
                if(this.currentState != null) {
                    this.currentState.onExit(this);
                }
                this.currentState = nextState;
                this.nextState = null;
                deferredEventsFill.addAll(deferredEventsUse);
                deferredEventsUse.clear();
                ConcurrentLinkedQueue<HSMEvent> tmp = deferredEventsUse;
                deferredEventsUse = deferredEventsFill;
                deferredEventsFill = tmp;
                try {
                    this.currentState.onEntry(this);
                } catch (Exception e) {
                    this.exceptionHandler.handleException(new HSMException(this.currentState, null, e));
                }
            }
            else {
                HSMEvent event = deferredEventsUse.poll();
                try {
                    if (event == null) {
                        event = queuedEvents.take();
                    }
                    this.currentState.handleEvent(this, event);
                } catch (InterruptedException e) {
                    if (!running) {
                        return;
                    }
                } catch (Exception e) {
                    this.exceptionHandler.handleException(new HSMException(this.currentState, event, e));
                }
            }
        }
    }

    private class HSMRunnable implements Runnable{

        @Override
        public void run() {
            HSMContext.this.run();
        }
    }

}
