package com.chinamobile.iot.monitor.model;

/**
 * 代表一个最终的计算指标的配置
 *
 * @author szl
 */
public class Target {
    /**
     * 指标的ID，唯一标识
     */
    private String targetId;
    /**
     * 指标名称
     */
    private String name;
    /**
     * 指标表达式，由中间表达式构成
     */
    private String expression;
    /**
     * 指标计算的周期，单位秒
     */
    private int period;
    /**
     * 指标的描述
     */
    private String descr;

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }


}
