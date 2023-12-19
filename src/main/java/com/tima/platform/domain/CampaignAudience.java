package com.tima.platform.domain;

import com.tima.platform.util.AppUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("public.campaign_audience")
public class CampaignAudience implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;
    private String size;
    private String gender;
    private String ageGroup;
    private String location;
    private String monthlyIncome;
    private Instant createdOn;

    @Override
    public boolean isNew() {
        boolean newRecord = AppUtil.isNewRecord(id);
        if(newRecord) {
            createdOn = Instant.now();
        }
        return newRecord;
    }
}
