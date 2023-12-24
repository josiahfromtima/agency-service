package com.tima.platform.util;

import lombok.Data;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/21/23
 */
@Data
public class ReportSettings {
        private int page;
        private int size;
        private String sortIn;
        private String sortBy;
        private Instant start;
        private Instant end;

        private ReportSettings() {}

        public static ReportSettings instance() {
            return new ReportSettings();
        }

        public ReportSettings page(int page) {
            this.page = page;
            return this;
        }

        public ReportSettings size(int size) {
            this.size = size;
            return this;
        }

        public ReportSettings sortIn(String sortIn) {
            this.sortIn = sortIn;
            return this;
        }

        public ReportSettings sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }
        public ReportSettings start(String start) {
            String pattern = "T00:00:00.000Z";
            try {
                this.start = Instant.parse(start + pattern);
            }catch (Exception ex){
                this.start = Instant.now();
            }
            return this;
        }
        public ReportSettings end(String end) {
            String pattern = "T23:59:59.000Z";
            try {
                this.end = Instant.parse(end + pattern);
            }catch (Exception ex){
                this.end = Instant.now();
            }
            return this;
        }
}
