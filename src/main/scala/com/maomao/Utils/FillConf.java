package com.maomao.Utils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2019/12/3 14:35
 * @Version 1.0
 **/
public class FillConf {
    private static final String PARAM_PREFIX = "-D";
    private static final String KEY_CONFIG_INPUT = "config.input";
    private static final String KEY_CONFIG_TEMPALTE = "config.template";
    private static final String KEY_CONFIG_OUTPUT = "config.output";

    private String configInput = "";
    private String configTemplate = "";
    private String configOutput = "";


    public static void main(String[] args) {
        if (args.length <= 2) {
            printHelp();
            System.exit(1);
        }
        FillConf mergeConf = new FillConf();
        mergeConf.exec(args);
    }

    public static void printHelp() {
        System.out.println("usage: FillConf   -Dconfig.template=   -Dconfig.input=  -Dconfig.output=  ");
        System.out.println("-Dconfig.template=  模板文件路径  ");
        System.out.println("-Dconfig.input=   配置文件路径 ");
        System.out.println("-Dconfig.output=   生成文件路径   ");
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

        if (params.containsKey(KEY_CONFIG_INPUT)) {
            configInput = params.get(KEY_CONFIG_INPUT);
        }
        if (params.containsKey(KEY_CONFIG_TEMPALTE)) {
            configTemplate = params.get(KEY_CONFIG_TEMPALTE);
        }
        if (params.containsKey(KEY_CONFIG_OUTPUT)) {
            configOutput = params.get(KEY_CONFIG_OUTPUT);
        }
        JSONObject inputParams = YamlUtils.readYaml(configInput);

        JSONObject template = YamlUtils.readYaml(configTemplate);
        Map<String, String> yamlNotes = YamlUtils.readYamlNotes(configTemplate);

        mergeParams(template, inputParams);
        if (YamlUtils.saveNotseYaml(template, yamlNotes, configOutput)) System.out.println("  FillConf success");
        else System.out.println("  FillConf failed");
    }

    void mergeParams(Map<String, Object> params, Map<String, Object> inputParams) {
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value != null) {
                if (value instanceof Map) {
                    mergeParams((Map) value, inputParams);
                } else {
                    params.put(key, setConfig(value.toString(), inputParams));
                }
            }
        }
    }

    private String setConfig(String str, Map<String, Object> inputParams) {

        while (true) {
            int fromIndex1 = str.indexOf('<', 0);
            int fromIndex2 = str.indexOf('>', fromIndex1 + 1);
            if (-1 == fromIndex1 || -1 == fromIndex2) {
                break;
            }
            String key = str.substring(fromIndex1 + 1, fromIndex2);
            String value = YamlUtils.getFromKey(key, inputParams);

            String l = "";
            l = l + str.subSequence(0, fromIndex1);
            l = l + value;
            l = l + str.subSequence(fromIndex2 + 1, str.length());

            str = l;
        }

        return str;
    }
}
