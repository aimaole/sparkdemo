package com.maomao.Utils;

import org.apache.commons.lang3.StringUtils;

public class InitDataBase {

    enum DataBaseType {
        MYSQL("com.mysql.jdbc.Driver"),
        POSTGRESQL("org.postgresql.Driver"),
        ORACLE("oracle.jdbc.OracleDriver"),
        DM("dm.jdbc.driver.DmDriver"),
        KINGBASE("com.kingbase8.Driver");

        private String driverClass;

        DataBaseType(String driverClass) {
            this.driverClass = driverClass;
        }

        public static DataBaseType from(String driverClass) {
            DataBaseType[] values = values();
            if (StringUtils.isNotBlank(driverClass)) {
                for (DataBaseType value : values) {
                    if (value.driverClass.equalsIgnoreCase(driverClass)) {
                        return value;
                    }
                }
            }
            return null;
        }
    }
}
