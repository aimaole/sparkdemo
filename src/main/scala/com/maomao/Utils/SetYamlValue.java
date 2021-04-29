package com.maomao.Utils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2020/4/15 15:02
 * @Version 1.0
 **/
public class SetYamlValue {
    private static final String PARAM_PREFIX = "-D";
    private static final String KEY_INPUT_FILE = "input.file";
    private static final String KEY_KEY = "key";
    private static final String KEY_VALUE = "value";

    private String inputFile = "";
    private String key = "";
    private String value = "";

    public static void main(String[] args) {
        if (args.length < 3) {
            printHelp();
            System.exit(1);
        }
        SetYamlValue setYamlValue = new SetYamlValue();
        setYamlValue.exec(args);
    }

    public static void printHelp() {
        System.out.println("修改yaml的属性值 ");
        System.out.println("usage: SetYamlValue  -Dinput.file=  -Dkey=  -Dvalue= ");
        System.out.println("-Dinput.file=    yaml文件路径  ");
        System.out.println("-Dkey=      yaml的key ");
        System.out.println("-Dvalue=    yaml的key的值  ");
    }

    private void exec(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (String arg : args) {
            if (0 == arg.indexOf(PARAM_PREFIX)) {
                String[] param = arg.split("=");
                if (2 == param.length) {
                    params.put(param[0].substring(2), param[1]);
                }
            }
        }
        if (params.containsKey(KEY_INPUT_FILE)) {
            inputFile = params.get(KEY_INPUT_FILE);
        }
        if (params.containsKey(KEY_KEY)) {
            key = params.get(KEY_KEY);
        }
        if (params.containsKey(KEY_VALUE)) {
            value = params.get(KEY_VALUE);
        }
        JSONObject inputParams = YamlUtils.readYaml(inputFile);
        Map<String, String> yamlNotes = YamlUtils.readYamlNotes(inputFile);
        setValue(inputParams, key, value);
        YamlUtils.saveNotseYaml(inputParams, yamlNotes, inputFile);
    }

    private Map<String, Object> setValue(Map<String, Object> params, String key, String value) {
        String settkey;
        if (key.indexOf('.') > -1) {
            settkey = key.substring(0, key.indexOf('.'));
            Object obj = params.get(settkey);
            if (obj instanceof Map) {
                params = (Map<String, Object>) obj;
                return setValue(params, key.substring(key.indexOf('.') + 1), value);
            } else if (obj == null) {
                Map<String, Object> map = new HashMap<>();
                params.put(settkey, setValue(map, key.substring(key.indexOf('.') + 1), value));
            } else {
                params.put(settkey, value);
            }
        } else {
            settkey = key;
            params.put(settkey, value);
        }
        return params;
    }
}
