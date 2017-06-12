package com.easydblib;

/**
 * EasyDB配置
 * @author : zhousf
 */

public class EasyDBConfig {

    public static boolean showDBLog;
    public static String logTAG = "EasyDB";

    public static Builder init() {
        return new Builder();
    }

    public EasyDBConfig(Builder builder) {
        showDBLog = builder.showDBLog;
        logTAG = builder.logTAG;
    }

    public static final class Builder{

        private boolean showDBLog;//是否显示数据库操作日志
        private String logTAG;//日志显示标识

        public Builder() {

        }

        public EasyDBConfig build(){
            return new EasyDBConfig(this);
        }

        /**
         * 是否显示数据库操作日志
         * @param showDBLog true显示 false不显示
         */
        public Builder showDBLog(boolean showDBLog){
            this.showDBLog = showDBLog;
            return this;
        }

        /**
         * 日志显示标识
         * @param logTAG 日志标识
         */
        public Builder setLogTAG(String logTAG){
            this.logTAG = logTAG;
            return this;
        }

    }



}
