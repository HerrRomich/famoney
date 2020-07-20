package com.hrrm.famoney.presentation.ui.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContext;

@Component(service = ServletContextHelper.class)
@HttpWhiteboardContext(name = "com.hrrm.famoney.ui", path = "/ui")
public class UiServletContextHelper extends ServletContextHelper {

    @Activate
    public UiServletContextHelper(BundleContext context) {
        super(context.getBundle());
    }

}
