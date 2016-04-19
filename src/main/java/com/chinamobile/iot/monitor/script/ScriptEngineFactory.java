package com.chinamobile.iot.monitor.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


/**
 * SciptEngineFactory的单例工厂
 *
 * @author szl
 */
public class ScriptEngineFactory {

    private static final ScriptEngine engine;

    static {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        engine = scriptEngineManager.getEngineByName("nashorn");
    }

    public static ScriptEngine getScriptEngine() {
        return engine;
    }
}
