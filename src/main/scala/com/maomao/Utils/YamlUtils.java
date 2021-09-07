package com.maomao.Utils;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2019/12/4 14:50
 * @Version 1.0
 **/
public class YamlUtils {

    private final static DumperOptions OPTIONS = new DumperOptions();

    static {
        //设置yaml读取方式为块读取
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        OPTIONS.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        OPTIONS.setPrettyFlow(false);
    }

    private YamlUtils() {

    }

    public static JSONObject readYaml(String fileName) {
        Yaml yaml = new Yaml();
        FileInputStream stream;
        try {
            stream = new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e) {
            return null;
        }

        Object obj = yaml.load(stream);
        try {
            stream.close();
        } catch (IOException e) {
            return null;
        }

        if (obj == null) {
            return null;
        }
        return (JSONObject) JSON.toJSON(obj);
    }

    /**
     * 返回yaml文件的注释
     *
     * @param fileName
     * @return
     */
    public static Map<String, String> readYamlNotes(String fileName) {
        Map<String, String> rs = new HashMap<>();
        try (FileInputStream inputStream = new FileInputStream(fileName);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = "";
            String note = "";
            Map<String, String> levelKeyMap = new HashMap<>();
            int level = 0;
            String key = "";
            boolean first = true;
            int firstSpacesCount = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().startsWith("#")) {
                    note = note + line + "\n";
                } else {
                    if (StringUtils.isNotBlank(line)) {
                        if (first) {
                            firstSpacesCount = spacesAtBeginningCount(line);
                            first = false;
                        }
                        line = line.substring(firstSpacesCount);
                        String linekey = line.trim().split(":")[0] + ":";

                        if (line.startsWith(" ")) {
                            int thisCount = spacesAtBeginningCount(line);
                            level = thisCount / 2;
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < level; i++) {
                                sb.append(levelKeyMap.get(i + ""));
                            }
                            key = sb + linekey;
                            levelKeyMap.put(level + "", linekey);
                        } else {
                            key = linekey;
                            level = 0;
                            levelKeyMap.clear();
                            levelKeyMap.put(level + "", key);
                        }
                        if (StringUtils.isNotBlank(note)) {
                            rs.put(key, note);
                        }
                        note = "";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return rs;
        }
        return rs;
    }

    /**
     * 通过注释map与yaml的值map生成file
     *
     * @param map
     * @param note
     * @param file
     * @return
     */
    public static boolean saveNotseYaml(Map map, Map notes, String file) {
        String outString = jsonToNoteYamlStr(map, notes);
        return saveStrToYaml(outString, file);
    }

    public static boolean saveYaml(Map map, String file) {
        String outString = jsonToYamlStr(map);
        return saveStrToYaml(outString, file);
    }

    /**
     * 将 Map转换成Yaml类型字符串
     *
     * @param map
     * @return
     */
    public static String jsonToYamlStr(Map map) {
        String text = yamlDump(map, 0);
        String outString = text.replaceAll("\\\\\"", "\"");
        return outString;
    }

    public static String jsonToNoteYamlStr(Map map, Map<String, String> notes) {
        String text = yamlNoteDump(map, notes, 0, new HashMap<>());
        String outString = text.replaceAll("\\\\\"", "\"");
        return outString;
    }

    /**
     * @param jsonStr 已经格式化过的字符串
     * @param file    文件路径
     * @return
     */
    public static boolean saveStrToYaml(String jsonStr, String file) {
        try {
            FileUtil.writeUtf8String(jsonStr, file);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getFromKey(String key, Map<String, Object> jsonObj) {
        String value = "";
        Map<String, Object> m = jsonObj;
        String[] keys = key.split("\\.");
        for (String k1 : keys) {
            if (m.containsKey(k1)) {
                Object v = m.get(k1);
                if (v instanceof Map) {
                    m = (Map) v;
                } else if (null != v) {
                    value = v.toString();
                }
            }
        }
        return value;
    }

    public static Object getValue(String key, Map<String, Object> yamlMap) {
        String[] keys = key.split("[.]");
        Object o = yamlMap.get(keys[0]);
        if (key.contains(".")) {
            if (o instanceof Map) {
                return getValue(key.substring(key.indexOf('.') + 1), (Map<String, Object>) o);
            } else {
                return null;
            }
        } else {
            return o;
        }
    }

    public static Map<String, Object> setValue(Map<String, Object> map, String key, Object value) {
        String[] keys = key.split("\\.");
        int len = keys.length;
        Map temp = map;
        for (int i = 0; i < len - 1; i++) {
            if (temp.containsKey(keys[i])) {
                temp = (Map) temp.get(keys[i]);
            } else {
                return null;
            }
            if (i == len - 2) {
                temp.put(keys[i + 1], value);
            }
        }
        for (int j = 0; j < len - 1; j++) {
            if (j == len - 1) {
                map.put(keys[j], temp);
            }
        }
        return map;
    }


    public static String yamlDump(Map map, int layer) {
        StringBuilder ret = new StringBuilder();

        for (Object key : map.keySet()) {
            for (int i = 0; i < layer; i++) {
                ret.append(" ");
            }
            ret.append(key.toString());
            ret.append(": ");

            Object value = map.get(key);
            if (value instanceof Map) {
                ret.append("\n");
                ret.append(yamlDump((Map) value, layer + 2));
            } else {
                if (null != value) {
                    ret.append(value);
                }
                ret.append("\n");
            }
        }
        return ret.toString();
    }

    public static String yamlNoteDump(Map map, Map<String, String> notes, int layer, Map<String, String> levelMap) {
        StringBuilder ret = new StringBuilder();

        int level = Integer.parseInt(levelMap.getOrDefault("this", "0"));
        for (Object key : map.keySet()) {
            levelMap.put(level + "", key.toString() + ":");
            StringBuilder noteKey = new StringBuilder();
            for (int i = 0; i <= level; i++) {
                noteKey.append(levelMap.get(i + ""));
            }
            if (notes.containsKey(noteKey.toString())) {
                ret.append(notes.get(noteKey.toString()));
            }
            for (int i = 0; i < layer; i++) {
                ret.append(" ");
            }
            ret.append(key.toString());
            ret.append(": ");

            Object value = map.get(key);
            if (value instanceof Map) {
                levelMap.put("this", level + 1 + "");
                ret.append("\n");
                ret.append(yamlNoteDump((Map) value, notes, layer + 2, levelMap));
            } else {
                if (null != value) {
                    ret.append(value);
                }
                ret.append("\n");
            }
        }
        return ret.toString();
    }

        private static int spacesAtBeginningCount(String str) {
        // 防止字符串尾部有空格，先加上一个字符串A 然后再去除前后空格，因为确定字符串最后是没有空格，所有做到只去除前空格
        String temp = (str + "A").trim();
        // 输出原字符串长度 - 去除空格后的长度的差 得到 字符串前 空格长度
        int count = str.length() - (temp.substring(0, (temp.length() - 1)).length());
        return count;
    }
}
