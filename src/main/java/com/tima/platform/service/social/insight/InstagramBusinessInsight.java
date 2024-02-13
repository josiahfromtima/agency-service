package com.tima.platform.service.social.insight;

import com.tima.platform.domain.CountryISO;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.api.response.instagram.DemographicStatistic;
import com.tima.platform.model.api.response.instagram.business.BasicBusinessInsight;
import com.tima.platform.model.api.response.instagram.insight.Breakdown;
import com.tima.platform.model.api.response.instagram.insight.Demographic;
import com.tima.platform.model.api.response.instagram.insight.FollowerDemographic;
import com.tima.platform.model.api.response.instagram.insight.result.Breakdowns;
import com.tima.platform.model.api.response.instagram.insight.result.Results;
import com.tima.platform.model.constant.DemographicType;
import com.tima.platform.repository.CountryISORepository;
import com.tima.platform.service.social.instagram.InstagramApiService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/27/24
 */
@Service
@RequiredArgsConstructor
public class InstagramBusinessInsight implements InsightService<BasicBusinessInsight, List<DemographicStatistic>> {
    private final LoggerHelper log = LoggerHelper.newInstance(InstagramBusinessInsight.class.getName());
    private final InstagramApiService apiService;
    private final CountryISORepository isoRepository;

    private static final String INVALID_HANDLE = "Invalid instagram user handle";
    @Override
    public Mono<BasicBusinessInsight> getUserBasicBusinessInsight(ClientSelectedSocialMedia media, String token) {
        log.info("Get Instagram Basic Insight for ", media.handle());
        if(Objects.isNull(media.handle()))
            return handleOnErrorResume(new AppException(INVALID_HANDLE), BAD_REQUEST.value());
        return apiService.getBusinessDiscovery(token, media.handle());
    }

    @Override
    public Mono<List<DemographicStatistic>> getUserBasicBusinessInsight(ClientSelectedSocialMedia userId,
                                                      String token,
                                                      DemographicType type) {
        return getStatistic(
                apiService.getMetaBusinessInsight(userId.accessToken(), userId.businessId(), type), type);
    }

    private Mono<List<DemographicStatistic>> getStatistic(Mono<FollowerDemographic> graphApi, DemographicType type) {
        return getIsoCountry()
                .flatMap(countryISOList -> graphApi.map(demographic -> getStatistic(demographic, type, countryISOList ))
                ).switchIfEmpty(Mono.just(List.of()));
    }

    private List<DemographicStatistic> getStatistic(FollowerDemographic graphApi,
                                                    DemographicType type,
                                                    List<CountryISO> countryISOList) {
        List<Demographic> demographics = graphApi.data();
        Breakdown breakdown = demographics.get(0).totalValue();
        List<Breakdowns> breakdowns = breakdown.breakdowns();
        Map<String, Long> stats = new ConcurrentHashMap<>();

        breakdowns.forEach(bk -> getUniqueKeys(bk.results(), stats));
        long total = stats.values().stream().reduce(0L, Long::sum );

        return  (type.equals(DemographicType.AGE_GENDER)) ?
                getBioDemographicStats(stats, total) :
                getLocationDemographicStats(stats, total, countryISOList);
    }

    private void getUniqueKeys(Results results, Map<String, Long> data ){
        String dimension = String.join("", results.dimensionValues());
        data.put(dimension, results.value());
    }

    private List<DemographicStatistic> getBioDemographicStats(Map<String, Long> data, long total) {
        Set<String> dataPoints = new HashSet<>();
        List<DemographicStatistic> demographicStatistics = new ArrayList<>();

        data.keySet().forEach(s -> dataPoints.add(s.substring(0, s.length() -1)));
        List<String> dataPointList = dataPoints.stream().sorted().toList();

        dataPointList.forEach(s -> {
            long value = data.getOrDefault(s+"F", 0L);
            long value2 = data.getOrDefault(s+"M", 0L);
            demographicStatistics.add(DemographicStatistic.builder()
                            .name(s)
                            .value1(value)
                            .value2(value2)
                            .value3(calculatePercentage((value + value2), total))
                    .build());
        });
        return demographicStatistics;
    }

    private List<DemographicStatistic> getLocationDemographicStats(Map<String, Long> data,
                                                                   long total,
                                                                   List<CountryISO> countryISOList) {
        List<DemographicStatistic> demographicStatistics = new ArrayList<>();
        Map<String, String> countryIso = getCountryMapping(countryISOList);

        Set<String> dataPoints = new HashSet<>(data.keySet());
        List<String> dataPointList = dataPoints.stream().sorted().toList();
        log.info("dataPoints ", dataPoints);

        dataPointList.forEach(s -> {
            long value = data.getOrDefault(s, 0L);
            demographicStatistics.add(DemographicStatistic.builder()
                            .name(countryIso.getOrDefault(s, s))
                            .value1(value)
                            .value2(0L)
                            .value3(calculatePercentage(value, total))
                    .build());
        });
        return demographicStatistics.stream()
                .sorted((o1, o2) -> Long.compare(o2.value1(), o1.value1()))
                .toList();
    }

    private long calculatePercentage(long value, long total) {
        if(total == 0) return 0;
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.UP)
                .divide(BigDecimal.valueOf(total), RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100))
                .longValue();
    }

    private Map<String, String> getCountryMapping(List<CountryISO> countryISOList) {
        Map<String, String> countries = new HashMap<>();
        countryISOList.forEach(countryISO -> countries.put(countryISO.getTwoLetterCode(), countryISO.getName()));
        return countries;
    }

    private Mono<List<CountryISO>> getIsoCountry() {
        return isoRepository.findAll().collectList();
    }


}
