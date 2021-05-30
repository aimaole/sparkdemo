package com.maomao.Utils;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * 合并两个yml文件或者合并两个Json配置。
 * 注意这里的yml不支持 - 这种数组表达方式，而是把 - 作为普通字符来看待。
 */
public class MergeYaml {


    private Map<String, Object> inputParams = null;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("usage: mergeYaml [-new] [-o] <yamlConfig> <yamlUpdate>");
            System.out.println("将<yamlUpdate>文件中的内容更新到 <yamlConfig>文件中, -o 覆盖yamlConfig，否则只显示");
            System.out.println("-new 更新时遇到相同的key以新值为准，否则以老值为准");
            System.exit(1);
        }

        MergeYaml mergeYaml = new MergeYaml();
        mergeYaml.exec(args);
    }

    public void exec(String[] args) throws IOException {
        int n = 0;
        boolean isOverwrite = false;
        boolean isNewValue = false;
        if (args[n].equals("-new")) {
            isNewValue = true;
            ++n;
        }
        if (args[n].equals("-o")) {
            isOverwrite = true;
            ++n;
        }
        String yamlConfig = args[n++];
        String yamlUpdate = args[n];
        JSONObject jsonCfg = YamlUtils.readYaml(yamlConfig);
        JSONObject jsonUpd = YamlUtils.readYaml(yamlUpdate);
        Map<String, String> yamlNotes = YamlUtils.readYamlNotes(yamlUpdate);
        JSONObject newJson = mergeJson(jsonCfg, jsonUpd, isNewValue);

        if (isOverwrite) {
            if (YamlUtils.saveNotseYaml(newJson, yamlNotes, yamlConfig)) System.out.println("  mergeYaml success");
            else System.out.println("  mergeYaml failed");
        } else System.out.println(YamlUtils.jsonToNoteYamlStr(newJson, yamlNotes));
    }

    public static JSONObject mergeJson(JSONObject originJson, JSONObject updateJson, boolean isNewValue) {
        JSONObject newJson = new JSONObject(originJson);
        updateJson.forEach(
                (key, value) -> newJson.merge(key, value != null ? value : "", (oldVal, newVal) -> {
                    if (oldVal instanceof JSONObject && newVal instanceof JSONObject)
                        return mergeJson((JSONObject) oldVal, (JSONObject) newVal, isNewValue);
                    else if (isNewValue) return newVal;
                    else return oldVal;
                })
        );
        return newJson;
    }
}
