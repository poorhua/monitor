package com.chinamobile.iot.monitor.model;

/**
 * 指标中间结果计算的对象
 *
 * @author szl
 */
public class MiddleTarget {
    /**
     * 中间指标名称
     */
    private String name;
    /**
     * 计算中间指标的表达式，为JavaScript语句
     */
    private String expression;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MiddleTarget)) return false;

        MiddleTarget that = (MiddleTarget) o;

        if (!name.equals(that.name)) return false;
        return expression.equals(that.expression);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
