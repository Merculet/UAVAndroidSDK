package io.merculet.uav.sdk.domain.event;

import java.util.Map;

public class CustomEvent extends EventPojo {

    public String action;      //custom;
    public Map<String, String> action_params;      //custom;

    @Override
    protected void init() {

    }

}
