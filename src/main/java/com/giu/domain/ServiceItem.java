package com.giu.domain;

/**
 * Created by biandra on 14/09/15.
 */
public class ServiceItem extends ServiceComponent{

    protected String path;
    protected String pathCommand;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPathCommand() {
        return pathCommand;
    }

    public void setPathCommand(String pathCommand) {
        this.pathCommand = pathCommand;
    }

    @Override
    public void add(ServiceComponent component) {
        //This method is not implemented
    }
}
