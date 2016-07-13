package com.giu.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by biandra on 14/09/15.
 */
public class ServiceComposite extends ServiceComponent {

    private List<ServiceComponent> services;

    public ServiceComposite(){
        services = new ArrayList<>();
    }

    public List<ServiceComponent> getServices() {
        return services;
    }

    public void setServices(List<ServiceComponent> services) {
        this.services = services;
    }

    @Override
    public void add(ServiceComponent component) {
        services.add(component);
    }
}
