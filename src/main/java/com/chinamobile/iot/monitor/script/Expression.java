package com.chinamobile.iot.monitor.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 进行公式计算的对象
 * 每个表达式对应一个对象
 * 在计算时，输入表达式形式为：${cpu_total_user}=${cpu_total_user}+${cpu_user}
 * 转换为为  map.cpu_total_user=map.cpu_total_user+map.cpu_user
 * map为本表达式对象的上下文环境中的map
 *
 * @author szl
 */
public class Expression {

    private static final Logger logger = LoggerFactory.getLogger(Expression.class);
    /**
     * 默认的绑定对象名
     */
    private final String BINDING_NAME = "SzlMap";
    /**
     * 编译后的表达式引擎对象
     */
    private CompiledScript script;
    /**
     * 表达式的参数列表
     */
    private List<String> parameters = new ArrayList<String>(5);

    /**
     * 表达式中计算结果的部分，为等号左边部分所指示的表达式的值
     */
    private String result;
    /**
     * 表达式的名称
     */
    private String name;
    /**
     * 输入的表达式
     */
    private String expr;

    public Expression() {
    }

    public String getBINDING_NAME() {
        return BINDING_NAME;
    }


    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    /**
     * 初始化一个表达式
     *
     * @param name    本表达式的名称，必须具有唯一性
     * @param strExpr 本表示的内容
     */
    public void init(String name, String strExpr) {
        String buildString = buildJScript(strExpr);
        if (buildString == null) {
            logger.warn("init expression {} failed, expr: {}", name, strExpr);
            return;
        }
        Compilable compilable = (Compilable) com.chinamobile.iot.monitor.script.ScriptEngineFactory.getScriptEngine();
        try {
            script = compilable.compile(buildString);
        } catch (ScriptException e) {
            logger.warn("compile expression {} failed, expr: {}", name, strExpr);
            logger.warn(e.getMessage());
        }
        this.name = name;
        this.expr = strExpr;
        logger.info("Init expression {} OK, expr: {}", name, strExpr);
    }

    /**
     * 判断某个表达式是否具备可执行的条件
     *
     * @param dataIn 输入的参数表
     * @return 如果可执行，就返回true，否则返回false
     */
    public boolean canExec(Map<String, Object> dataIn) {
        for (String param : parameters) {
            if (!dataIn.containsKey(param)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 完成表达式计算求值，本表达式计算的结果会保存在dataOut中
     *
     * @param dataIn  输入map
     * @param dataOut 输出的map
     * @return 成功返回0，失败返回-1
     */
    public int exec(Map<String, Object> dataIn, Map<String, Object> dataOut) {
        //logger.info("begin exec the expression,name :{}， expr:{}", name, expr);
        Map<String, Object> map = new HashMap<String, Object>();
        // 必备参数加入绑定的Map
        for (String param : parameters) {
            if (dataIn.containsKey(param)) {
                map.put(param, dataIn.get(param));
            } else {
                logger.warn("some parameter is absent");
                return -1;
            }
        }
        // 结果字段加入绑定的Map
        if (dataOut.containsKey(result)) {
            map.put(result, dataOut.get(result));
        } else {
            map.put(result, new Integer(0));
        }
        Bindings bindings = new SimpleBindings();
        bindings.put(BINDING_NAME, map);
        try {
            script.eval(bindings);
        } catch (ScriptException e) {
            logger.error("error:", e);
            return -1;
        }
        // 如果计算在输入条件中没有，执行失败
        if (map.get(result) == null) {
            logger.error("error occured when exec expr: {}, the result is not in the expression", expr);
            return -1;
        }

        // 把结果值保存在结果hashMap中
        dataOut.put(result, map.get(result));
        //logger.info("exec expr ok! result is: {}", map.get(result));
        return 0;
    }

    /**
     * 完成表达式的字符串转换
     *
     * @param strExpr ${cpu_total_user}=${cpu_total_user}+${cpu_user}
     * @return map.cpu_total_user=map.cpu_total_user+map.cpu_user
     */
    private String buildJScript(String strExpr) {

        // 如果表达式中没找到'='符号，说明该表达式不符合规范
        if (strExpr.indexOf('=') <= 0) {
            logger.warn("expression {} is not valid, it must like ${aa}=${bb}...");
            return null;
        }

        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(strExpr);
        StringBuffer sb = new StringBuffer();
        boolean bFirst = true;
        while (matcher.find()) {
            if (bFirst) {
                result = matcher.group(1);
                bFirst = false;
            } else {
                String key = matcher.group(1);
                if (!key.equals(result)) {
                    if (!parameters.contains(key)) {
                        parameters.add(key);
                    }
                }
            }
            String strReplacement = BINDING_NAME + ".$1";
            matcher.appendReplacement(sb, strReplacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
