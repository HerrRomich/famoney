package com.hrrm.faminey.infrastracture.logging.internal;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

@Component
public class LogListenerRegistrator {

    @Reference
    LogReaderService logReaderService;
    private LogListener listener;

    @Activate
    public void registerLogListener() {
        listener = new LogListenerImpl();
        // logReaderService.addLogListener(listener);
    }

    @Deactivate
    public void unregisterLogListener() {
        // logReaderService.removeLogListener(listener);
    }

}
