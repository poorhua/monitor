package com.chinamobile.iot.monitor.schedule;

import com.chinamobile.iot.monitor.model.Target;
import com.chinamobile.iot.monitor.script.Expression;

/**
 * Created by szl on 2016/3/25.
 */
public class TargetWithExpr extends Target {

    private Expression expr;

    public TargetWithExpr() {
    }

    public TargetWithExpr(Expression expr) {
        this.expr = expr;
    }

    public Expression getExpr() {
        return expr;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }
}
