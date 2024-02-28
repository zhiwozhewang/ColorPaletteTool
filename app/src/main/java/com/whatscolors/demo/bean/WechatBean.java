package com.whatscolors.demo.bean;

import com.whatscolors.demo.utils.http.RespDTO;

/**
 * Author:      wangwei
 * Date:        2019-12-11 15:51
 * Version:     1.0
 */
public class WechatBean extends RespDTO<WechatBean.DataBean> {
    /**
     * code : 200
     * data : {"trade_type":"APP","prepay_id":"wx20191211161005750485","nonce_str":"lM9LiY3ILt6jX1Wl","return_code":"SUCCESS","err_code_des":"ok","sign":"595F8486ABDBD1E9BAE65DA28A6827F2","mch_id":"1567959931","return_msg":"OK","appid":"wx788baac4f53e190e","device_info":"sandbox","result_code":"SUCCESS","err_code":"SUCCESS"}
     */

    public static class DataBean {
        /**
         * trade_type : APP
         * prepay_id : wx20191211161005750485
         * nonce_str : lM9LiY3ILt6jX1Wl
         * return_code : SUCCESS
         * err_code_des : ok
         * sign : 595F8486ABDBD1E9BAE65DA28A6827F2
         * mch_id : 1567959931
         * return_msg : OK
         * appid : wx788baac4f53e190e
         * device_info : sandbox
         * result_code : SUCCESS
         * err_code : SUCCESS
         */

        private String trade_type;
        private String prepay_id;//预付单号：是走统一下单的接口后，微信返回的一个预支付单号

        private String nonce_str;//随机字符串：这个是后台自己随便生成的一个随机字符串，但是不要超过32位，微信官方提供给了我们一个随机生成算法。
        private String return_code;
        private String err_code_des;
        private String sign;//这个最好也是让后台参考微信提供给我们的 签名生成算法
        private String mch_id;//商户号：是需要你向微信注册企业商户后得到的商户号
        private String return_msg;
        private String appid;//应用id： 是你申请应用后就有的
        private String device_info;
        private String result_code;
        private String err_code;
        private String timestamp;


        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }


        public String getTrade_type() {
            return trade_type;
        }

        public void setTrade_type(String trade_type) {
            this.trade_type = trade_type;
        }

        public String getPrepay_id() {
            return prepay_id;
        }

        public void setPrepay_id(String prepay_id) {
            this.prepay_id = prepay_id;
        }

        public String getNonce_str() {
            return nonce_str;
        }

        public void setNonce_str(String nonce_str) {
            this.nonce_str = nonce_str;
        }

        public String getReturn_code() {
            return return_code;
        }

        public void setReturn_code(String return_code) {
            this.return_code = return_code;
        }

        public String getErr_code_des() {
            return err_code_des;
        }

        public void setErr_code_des(String err_code_des) {
            this.err_code_des = err_code_des;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getMch_id() {
            return mch_id;
        }

        public void setMch_id(String mch_id) {
            this.mch_id = mch_id;
        }

        public String getReturn_msg() {
            return return_msg;
        }

        public void setReturn_msg(String return_msg) {
            this.return_msg = return_msg;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getDevice_info() {
            return device_info;
        }

        public void setDevice_info(String device_info) {
            this.device_info = device_info;
        }

        public String getResult_code() {
            return result_code;
        }

        public void setResult_code(String result_code) {
            this.result_code = result_code;
        }

        public String getErr_code() {
            return err_code;
        }

        public void setErr_code(String err_code) {
            this.err_code = err_code;
        }
    }
}
