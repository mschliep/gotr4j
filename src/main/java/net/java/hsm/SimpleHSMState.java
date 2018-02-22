package net.java.hsm;

/**
 * The {@link net.java.hsm.SimpleHSMState} will simply do nothing on entry and exit as well as any events.
 *
 * It is a simple base class for all group objects in the hierarchy.
 *
 * This is a poor implementation because it does not handle correctly switching
 * states inside a shared parent group.
 */
public class SimpleHSMState<T extends HSMContext> implements HSMState<T> {

    /**
     * super.onEntry should be called first in all child states.
     *
     * @see net.java.hsm.HSMState#onEntry(HSMContext)
     */
    @Override
    public void onEntry(T context) throws Exception {

    }

    /**
     * super.handleEvent should be called in all child states when they do not handle the specific event.
     *
     * @see net.java.hsm.HSMState#handleEvent(HSMContext, HSMEvent)
     */
    @Override
    public void handleEvent(T context, HSMEvent event) throws Exception {

    }

    /**
     * super.onExit should be called last in all child states.
     *
     * @see net.java.hsm.HSMState#onExit(HSMContext)
     */
    @Override
    public void onExit(T context) {

    }
}
