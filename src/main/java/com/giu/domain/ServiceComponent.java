package com.giu.domain;

/**
 * Created by mbritez on 14/09/15.
 */
public abstract class ServiceComponent {

    protected String name;
    protected String context;
    protected String icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    abstract public void add(ServiceComponent component);
}
