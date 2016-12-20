package com.easydblib;

import com.easydblib.helper.BaseDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * EasyDB配置
 * @author : zhousf
 */

public class EasyDBConfig {

    public static boolean showDBLog;

    public static Builder init() {
        return new Builder();
    }

    public EasyDBConfig(Builder builder) {
        showDBLog = builder.showDBLog;
    }

    public static final class Builder{

        private boolean showDBLog;//是否显示数据库操作日志
        private List<BaseDBHelper> helpers;

        public Builder() {
            helpers = new ArrayList<>();
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
         * 注册DBHelper
         * @param helper 数据库Helper
         */
        public Builder registerHelper(BaseDBHelper helper){
            helpers.add(helper);
            return this;
        }

        public Builder registerHelpers(List<BaseDBHelper> helpers){
            helpers.addAll(helpers);
            return this;
        }

    }



}
