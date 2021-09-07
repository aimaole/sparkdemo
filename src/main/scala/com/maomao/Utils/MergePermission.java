package com.maomao.Utils;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergePermission {
    private static final String LINE_SEPARATOR = "line.separator";

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("usage: MergePermission <oldFile> <newFile> <permission|user>");
            System.out.println("将<newFile>文件中的内容更新到 <oldFile>文件中，只增加新模块|用户，原有注释的不增加。");
            System.exit(1);
        }
        MergePermission mergePermission = new MergePermission();
        mergePermission.exec(args);
    }

    public void exec(String[] args) throws IOException {
        String oldFilePath = args[0];
        String newFilePath = args[1];

        if (args.length == 2 || "permission".equals(args[2])) {
            //权限文件合并
            Map<String, String> all = new HashMap<>();
            Map<String, List<String>> newRolePermits = new HashMap<>();
            File newFile = new File(newFilePath);
            if (newFile.exists() && newFile.canRead()) {
                List<String> lines = FileUtil.readUtf8Lines(newFile);
                List<String> permitSet = null;
                for (String line : lines) {
                    if (StringUtils.isBlank(line)) continue;
                    if (line.startsWith("[")) {
                        String role = line.substring(1, line.indexOf(']'));
                        permitSet = new ArrayList<>();
                        newRolePermits.put(role, permitSet);
                    } else {
                        String[] parts = line.split("#");
                        String key = parts[0].trim();
                        if (permitSet != null && !key.isEmpty()) permitSet.add(key);
                        if (parts.length == 2) {
                            all.put(parts[0].trim(), parts[1].trim());
                        } else if (parts.length == 3) {
                            all.put(parts[1].trim(), parts[2].trim());
                        }
                    }
                }
            } else {
                System.out.println(newFilePath + "不可读取");
                System.exit(1);
            }

            Map<String, List<String>> oldRolePermits = new HashMap<>();
            File oldFile = new File(oldFilePath);
            if (oldFile.exists() && oldFile.canRead()) {
                List<String> lines = FileUtil.readUtf8Lines(oldFile);
                List<String> permitSet = null;
                for (String line : lines) {
                    if (StringUtils.isBlank(line)) continue;
                    if (line.startsWith("[")) {
                        String role = line.substring(1, line.indexOf(']'));
                        permitSet = new ArrayList<>();
                        oldRolePermits.put(role, permitSet);
                    } else {
                        String key;
                        if (line.startsWith("#")) {
                            String[] parts = line.split("#");
                            key = parts[1].trim();
                        } else {
                            String[] parts = line.split("#");
                            key = parts[0].trim();
                        }
                        if (permitSet != null && !key.isEmpty()) permitSet.add(key);
                    }
                }
            } else {
                System.out.println(oldFilePath + "不可读取");
                System.exit(1);
            }

            for (Map.Entry<String, List<String>> entry : oldRolePermits.entrySet()) {
                String role = entry.getKey();
                List<String> newRoleList = newRolePermits.get(role);
                for (String tmp : entry.getValue()) {
                    newRoleList.remove(tmp);
                    if (CollectionUtils.isEmpty(newRoleList)) newRolePermits.remove(role);
                }
            }

            StringBuilder bufAll = new StringBuilder();  //保存修改过后的所有内容，不断增加
            List<String> lines = FileUtil.readUtf8Lines(oldFile);
            String role = "";
            int count = 0;
            for (String line : lines) {
                if (line.startsWith("[")) {
                    role = line.substring(1, line.indexOf(']'));
                    count = 0;
                }
                bufAll.append(line);
                bufAll.append(System.getProperty(LINE_SEPARATOR));
                count++;
                if (StringUtils.isNotBlank(role) && count > oldRolePermits.get(role).size()) {
                    List<String> list = newRolePermits.get(role);
                    if (CollectionUtils.isNotEmpty(list)) {
                        for (String newPermission : list) {
                            bufAll.append(newPermission);
                            bufAll.append("    #");
                            bufAll.append(all.get(newPermission));
                            bufAll.append(System.getProperty(LINE_SEPARATOR));
                        }
                        newRolePermits.remove(role);
                    }
                }
            }
            FileUtil.writeUtf8String(bufAll.toString(), oldFilePath);
            String os = System.getProperty("os.name");
            if (os.toLowerCase().startsWith("win")) {
                System.out.println("merge permission success");
            } else {
                System.out.println("\33[36;2m merge permission success \33[0;2m");
            }
        } else if ("user".equals(args[2])) {
            //用户合并
            Map<String, String> newUsers = new HashMap<>();
            File newFile = new File(newFilePath);
            if (newFile.exists() && newFile.canRead()) {
                List<String> lines = FileUtil.readUtf8Lines(newFile);
                for (String line : lines) {
                    if (StringUtils.isBlank(line)||line.startsWith("#")) {
                        continue;
                    }
                    String[] split = line.split(",");
                    newUsers.put(split[0], line);
                }
            } else {
                System.out.println(newFilePath + "不可读取");
                System.exit(1);
            }

            StringBuilder bufAll = new StringBuilder();  //保存修改过后的所有内容，不断增加
            File oldFile = new File(oldFilePath);
            if (oldFile.exists() && oldFile.canRead()) {
                List<String> lines = FileUtil.readUtf8Lines(oldFile);
                for (String line : lines) {
                    if (StringUtils.isNotBlank(line)) {
                        bufAll.append(line);
                        bufAll.append(System.getProperty(LINE_SEPARATOR));
                        String[] split = line.split(",");
                        if (newUsers.containsKey(split[0])) {
                            newUsers.remove(split[0]);
                        }
                    }
                }
            } else {
                System.out.println(oldFilePath + "不可读取");
                System.exit(1);
            }
            for (Map.Entry<String, String> entry : newUsers.entrySet()) {
                bufAll.append(entry.getValue());
                bufAll.append(System.getProperty(LINE_SEPARATOR));
            }
            FileUtil.writeUtf8String(bufAll.toString(), oldFilePath);
            String os = System.getProperty("os.name");
            if (os.toLowerCase().startsWith("win")) {
                System.out.println("merge user success");
            } else {
                System.out.println("\33[36;2m merge user success \33[0;2m");
            }
        }
    }
}
