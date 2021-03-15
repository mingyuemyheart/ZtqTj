package com.pcs.ztqtj.control.livequery;

/**
 * Created by tyaathome on 2017/6/3.
 */

public abstract class ControlDistributionBase {
    public abstract void init();
    public abstract void updateView(ControlDistribution.ColumnCategory column);
    public abstract void clear();
    public abstract void destroy();
}
