package com.ipvs.cepbenchmarking.engine;

import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;

import com.espertech.esper.client.EPException;
import com.espertech.esper.client.ConfigurationException;

public enum Esper {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(Esper.class.getName());

    private EPServiceProvider serviceProvider;

    // TODO allow multiple instances of esper
    private Esper() {
        serviceProvider = EPServiceProviderManager.getDefaultProvider();
    }

    public void addEventType(String eventTypeName, Map<String, Object> typeMap) {
        try {
            serviceProvider.getEPAdministrator().getConfiguration().addEventType(eventTypeName, typeMap);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            // TODO Log exception
        }
    }

    public void sendEvent(String eventTypeName, Map eventMap) {
        try {
            serviceProvider.getEPRuntime().sendEvent(eventMap, eventTypeName);
        } catch (EPException e) {
            e.printStackTrace();
            // TODO Log exception
        }
    }

    public void addStatement(String statementName, String eplStatement) {
        try {
            EPStatement statement = serviceProvider.getEPAdministrator().createEPL(eplStatement, statementName);

            statement.addListener(new UpdateListener() {
                public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                    EventBean event = newEvents[0];
                    // TODO Make sure the type is correct
                    LOGGER.info(((Map) event.getUnderlying()).toString());
                }
            });
        } catch (EPException e) {
            e.printStackTrace();
            // TODO Log exception
        }
    }
}