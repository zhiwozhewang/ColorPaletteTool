package com.whatscolors.demo.bean;

import com.whatscolors.demo.utils.http.RespDTO;

/**
 * Author:      wangwei
 * Date:        2019-11-27 20:16
 * Version:     1.0
 */
public class CheckBean extends RespDTO<CheckBean.DataBean> {


    /**
     * code : 200
     * data : {"service":{"status":0,"is_lifetime":false,"valid_until":null},"usageRecord":{"times":"Integer"}}
     */


    public static class DataBean {
        /**
         * service : {"status":0,"is_lifetime":false,"valid_until":null}
         * usageRecord : {"times":"Integer"}
         */

        private String email, first_name, last_name;
        private ServiceBean service;
        private UsageRecordBean usageRecord;

        public ServiceBean getService() {
            return service;
        }

        public void setService(ServiceBean service) {
            this.service = service;
        }

        public UsageRecordBean getUsageRecord() {
            return usageRecord;
        }

        public void setUsageRecord(UsageRecordBean usageRecord) {
            this.usageRecord = usageRecord;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public static class ServiceBean {
            /**
             * status : 0
             * is_lifetime : false
             * valid_until : null
             */

            private int status;
            private boolean is_lifetime;
            private String valid_until;//ISO 8601 Date String

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public boolean isIs_lifetime() {
                return is_lifetime;
            }

            public void setIs_lifetime(boolean is_lifetime) {
                this.is_lifetime = is_lifetime;
            }

            public String getValid_until() {
                return valid_until;
            }

            public void setValid_until(String valid_until) {
                this.valid_until = valid_until;
            }
        }

        public static class UsageRecordBean {
            /**
             * times : Integer
             */

            private int times;

            public int getTimes() {
                return times;
            }

            public void setTimes(int times) {
                this.times = times;
            }
        }
    }
}
