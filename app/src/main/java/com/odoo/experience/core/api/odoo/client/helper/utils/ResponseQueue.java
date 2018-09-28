package com.odoo.experience.core.api.odoo.client.helper.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.odoo.experience.core.api.odoo.client.listeners.IOdooResponse;

/**
 * Responsible to store response callback for each of the requests generated by client
 */
public class ResponseQueue {

    private static ResponseQueue responseQueueSingleton;
    private Map<String, Queue<IOdooResponse>> responseQueue = new HashMap<>();

    public static ResponseQueue getInstanceSingleton() {
        if (responseQueueSingleton == null) {
            responseQueueSingleton = new ResponseQueue();
        }
        return responseQueueSingleton;
    }

    /**
     * Adding response callback to queue with uuid
     *
     * @param uuid        Uniquely generated uuid for response callback
     * @param callbackRef Callback reference
     */
    public void add(int uuid, IOdooResponse callbackRef) {
        if (!contain(uuid)) {
            Queue<IOdooResponse> callback = new LinkedList<>();
            callback.add(callbackRef);
            responseQueue.put("uuid_" + uuid, callback);
        }
    }

    /**
     * Gets the response callback if specified in queue
     *
     * @param uuid Unique id got in response for request
     * @return IOdooResponse callback object if available otherwise null
     */
    public IOdooResponse get(int uuid) {
        if (contain(uuid)) {
            return responseQueue.get("uuid_" + uuid).poll();
        }
        return null;
    }

    /**
     * Checks for response queue availability for unique uuid
     *
     * @param uuid unique uuid for requests
     * @return true, if response callback available in queue otherwise false.
     */
    public boolean contain(int uuid) {
        return responseQueue.containsKey("uuid_" + uuid);
    }

    /**
     * Used to remove response queue after callback done.
     *
     * @param uuid unique request uuid
     */
    public void remove(int uuid) {
        if (contain(uuid)) {
            responseQueue.remove("uuid_" + uuid);
        }
    }

}
