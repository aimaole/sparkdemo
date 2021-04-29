package com.maomao.Utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2020/4/15 15:02
 * @Version 1.0
 **/
public class GetYamlValue {
    private static final String PARAM_PREFIX = "-D";
    private static final String KEY_INPUT_FILE = "input.file";
    private static final String KEY_KEY = "key";
    private static final String DB_IP = "db.ip";
    private static final String DB_PORT = "db.port";
    private static final String DB_NAME = "db.name";

    private String inputFile = "";
    private String key = "";

    public static void main(String[] args) {
        if (args.length < 2) {
            printHelp();
            System.exit(1);
        }
        GetYamlValue getYamlValue = new GetYamlValue();
        getYamlValue.exec(args);
    }

    public static void printHelp() {
        System.out.println("usage: GetYamlValue  -Dinput.file=  -Dkey=  ");
        System.out.println("-Dinput.file=    yaml文件路径  ");
        System.out.println("-Dkey=      获取通用yaml文件的属性值 ");
        System.out.println("定制 -Dkey=   db.ip   获取application.yaml中springboot的db ip ,多个ip通过master获取主ip");
        System.out.println("定制 -Dkey=   db.port   获取application.yaml中springboot的db port ,多个ip通过master获取主ip port");
        System.out.println("定制 -Dkey=   db.name   获取application.yaml中url中的数据库名");
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
        JSONObject inputParams = YamlUtils.readYaml(inputFile);
        String value = "";
        value = getValue(inputParams, key);
        if (StringUtils.isBlank(value)) {
            if (DB_IP.equals(key)) {
                value = getMasterIpAndPort(inputParams).get(DB_IP);
            } else if (DB_PORT.equals(key)) {
                value = getMasterIpAndPort(inputParams).get(DB_PORT);
            } else if (DB_NAME.equals(key)) {
                value = getDataBaseName(inputParams);
            }
        }
        System.out.print(value);
    }

    private Map<String, String> getMasterIpAndPort(Map<String, Object> inputParams) {
        Map<String, String> result = new HashMap<>();
        Map<String, String> ipPort = new HashMap<>();
        String ip = "";
        String url = getValue(inputParams, "spring.datasource.url");
        String ipAndPort = url.substring(url.indexOf("//") + 2, url.lastIndexOf('/'));
        if (ipAndPort.contains(",")) {
            String[] ipAndPorts = ipAndPort.split(",");
            String[] split = ipAndPorts[0].split(":");
            ip = split[0].replace("[", "").replace("]", "");
            ipPort.put(ip, split[1]);
        } else {
            ip = ipAndPort.substring(0, ipAndPort.lastIndexOf(':')).replace("[", "").replace("]", "");
            ipPort.put(ip, ipAndPort.substring(ipAndPort.lastIndexOf(':') + 1));
        }
        result.put(DB_IP, ip);
        result.put(DB_PORT, ipPort.get(ip));
        return result;
    }

    private String getDataBaseName(Map<String, Object> inputParams) {
        String url = getValue(inputParams, "spring.datasource.url");
        return url.substring(url.lastIndexOf('/') + 1, url.indexOf('?'));
    }


    private String getValue(Map<String, Object> params, String key) {
        String getkey;
        if (key.indexOf('.') > -1) {
            getkey = key.substring(0, key.indexOf('.'));
        } else {
            getkey = key;
        }
        Object obj = params.get(getkey);
        if (obj instanceof Map) {
            params = (Map<String, Object>) obj;
            return getValue(params, key.substring(key.indexOf('.') + 1));
        } else if (obj != null) {
            return obj.toString();
        } else {
            return "";
        }
    }
}
