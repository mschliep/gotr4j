package net.java.hsm;

/**
 * An instance of a {@link net.java.hsm.HSMState} will only live in a single {@link net.java.hsm.HSMContext}.
 */
public interface HSMState<T extends HSMContext> {

    /**
     * Called when the group is entered.
     * @param context
     */
    public void onEntry(T context) throws Exception;

    /**
     * Called when an event is triggered while in this group.
     *
     * @param context context of the group machine at the time of the event
     * @param event event that was triggered
     */
    public void handleEvent(T context, HSMEvent event) throws Exception;

    /**
     * Called when a this group is exited.
     *
     * Should be used to clean up the context
     * @param context
     */
    public void onExit(T context);

}
