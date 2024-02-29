package com.tima.platform.service.dashboard;

import com.tima.platform.model.api.request.analytics.AudienceDistribution;
import com.tima.platform.model.api.request.analytics.AudienceDistributionAnalytic;
import com.tima.platform.model.api.request.analytics.AudienceDistributionGraph;
import com.tima.platform.model.api.request.analytics.AudienceDistributionSummary;
import com.tima.platform.model.api.response.instagram.DemographicStatistic;
import com.tima.platform.model.constant.DemographicType;
import com.tima.platform.repository.ClientSocialMediaRepository;
import com.tima.platform.repository.SocialMediaRepository;
import com.tima.platform.service.social.insight.DefaultSocialBusinessInsight;
import com.tima.platform.service.social.insight.InsightService;
import com.tima.platform.service.social.insight.InstagramBusinessInsight;
import com.tima.platform.util.GenderAtomicLong;
import com.tima.platform.util.LoggerHelper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.tima.platform.util.AppUtil.safeCastList;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/22/24
 */
@Service
@RequiredArgsConstructor
public class CampaignAnalyticsAudienceService {
    private final LoggerHelper log = LoggerHelper.newInstance(CampaignAnalyticsInteractionService.class.getName());
    private final CampaignAnalyticsKpiService kpiService;
    private final ClientSocialMediaRepository mediaRepository;
    private final SocialMediaRepository socialMediaRepository;
    private final InstagramBusinessInsight instagramBusinessInsight;
    private final DefaultSocialBusinessInsight socialBusinessInsight;
    Map<String, InsightService<?, ?, ?>> socialMediaInsights = new HashMap<>();

    private static final String FEMALE = "Female";
    private static final String MALE = "Male";
    @PostConstruct
    public void init() {
        socialMediaInsights.put("Instagram", instagramBusinessInsight);
    }

    public Mono<AudienceDistributionAnalytic> getDistributionSummary(String publicId) {
        log.info("Getting and building the Audience distribution summary for campaign ", publicId);
        return kpiService.getInfluencers(publicId)
                .flatMap(accounts -> buildDistribution(accounts, publicId))
                .flatMap(results -> buildAudienceSummary(results.getT1(), results.getT2(), results.getT3()));
    }

    private Mono<Tuple3<List<?>, List<?>, List<?>>> buildDistribution(List<String> accounts, String publicId) {
        return Mono.zip(kpiService.getSocialMedia(publicId)
                .flatMap(names ->  processCountryDistribution(names, accounts).collectList()),
                kpiService.getSocialMedia(publicId)
                        .flatMap(names ->  processCityDistribution(names, accounts).collectList()),
                kpiService.getSocialMedia(publicId)
                        .flatMap(names ->  processGenderAgeDistribution(names, accounts).collectList()));
    }

    private Flux<?> processCountryDistribution(List<String> names, List<String> accounts) {
        return Flux.fromIterable(names)
                .flatMap(name ->  Flux.fromIterable(accounts)
                        .flatMap(account -> socialMediaRepository.findByName(name)
                                .flatMap(socialMedia -> mediaRepository.findByUserId(account)
                                        .flatMap(clientSocialMedia ->
                                                socialMediaInsights.getOrDefault(name, socialBusinessInsight)
                                                .getUserBasicBusinessInsight(
                                                        kpiService.getSelectedMedia(name, clientSocialMedia),
                                                        kpiService.getSelectedMedia(name, clientSocialMedia)
                                                                .accessToken(), DemographicType.COUNTRY)
                                        )
                                )
                        )
                );
    }
    private Flux<?> processCityDistribution(List<String> names, List<String> accounts) {
        return Flux.fromIterable(names)
                .flatMap(name ->  Flux.fromIterable(accounts)
                        .flatMap(account -> socialMediaRepository.findByName(name)
                                .flatMap(socialMedia -> mediaRepository.findByUserId(account)
                                        .flatMap(clientSocialMedia ->
                                                socialMediaInsights.getOrDefault(name, socialBusinessInsight)
                                                .getUserBasicBusinessInsight(
                                                        kpiService.getSelectedMedia(name, clientSocialMedia),
                                                        kpiService.getSelectedMedia(name, clientSocialMedia)
                                                                .accessToken(), DemographicType.CITY)
                                        )
                                )
                        )
                );
    }

    private Flux<?> processGenderAgeDistribution(List<String> names, List<String> accounts) {
        return Flux.fromIterable(names)
                .flatMap(name ->  Flux.fromIterable(accounts)
                        .flatMap(account -> socialMediaRepository.findByName(name)
                                .flatMap(socialMedia -> mediaRepository.findByUserId(account)
                                        .flatMap(clientSocialMedia ->
                                                socialMediaInsights.getOrDefault(name, socialBusinessInsight)
                                                .getUserBasicBusinessInsight(
                                                        kpiService.getSelectedMedia(name, clientSocialMedia),
                                                        kpiService.getSelectedMedia(name, clientSocialMedia)
                                                                .accessToken(), DemographicType.AGE_GENDER)
                                        )
                                )
                        )
                );
    }

    private Mono<AudienceDistributionAnalytic> buildAudienceSummary(List<?> countries,
                                                                    List<?> cities,
                                                                    List<?> ageGenders) {

        return Mono.zip(constructAudienceSummary(countries, cities, ageGenders),
                buildDistributionGraph(countries, ageGenders))
                .map(distributions -> AudienceDistributionAnalytic.builder()
                        .audienceDistributionSummary(distributions.getT1())
                        .audienceDistributionGraph(distributions.getT2())
                        .build());
    }

    private Mono<AudienceDistributionSummary> constructAudienceSummary(List<?> countries,
                                                                 List<?> cities,
                                                                 List<?> ageGenders) {
        return Mono.just(
                AudienceDistributionSummary.builder()
                        .topCountry(topCountry(countries))
                        .topCity(topCity(cities))
                        .topAge(topAge(ageGenders))
                        .topGender(topGender(ageGenders))
                        .build()
        );
    }

    private String topCountry(List<?> countries) {
        log.info("Processing Top Countries ");
        List<DemographicStatistic> demographicStatistics = new ArrayList<>();
        Map<String, Long> countryData = new HashMap<>();

        countries.stream()
                .map(country -> safeCastList((List<?>)  country, DemographicStatistic.class))
                .toList()
                .forEach(statistics -> statistics.forEach(statistic ->
                        countryData.put(statistic.name(),
                                countryData.getOrDefault(statistic.name(), 0L) + statistic.value1())));

        countryData.forEach((key, value) -> demographicStatistics.add(buildDemographicStatistic(key, value)));
        DemographicStatistic demographicStatistic = demographicStatistics.stream()
                .sorted((o1, o2) -> Long.compare(o2.value1(), o1.value1()))
                .toList()
                .get(0);

        return Objects.isNull(demographicStatistic)? "" : demographicStatistic.name();
    }

    private String topCity(List<?> cities) {
        log.info("Processing Top Cities ");
        List<DemographicStatistic> demographicStatistics = new ArrayList<>();
        Map<String, Long> countryData = new HashMap<>();

        cities.stream()
                .map(city -> safeCastList((List<?>)  city, DemographicStatistic.class))
                .toList()
                .forEach(statistics -> statistics.forEach(statistic ->
                        countryData.put(statistic.name(),
                                countryData.getOrDefault(statistic.name(), 0L) + statistic.value1())));

        countryData.forEach((key, value) -> demographicStatistics.add(buildDemographicStatistic(key, value)));
        DemographicStatistic demographicStatistic = demographicStatistics.stream()
                .sorted((o1, o2) -> Long.compare(o2.value1(), o1.value1()))
                .toList()
                .get(0);

        return Objects.isNull(demographicStatistic)? "" : demographicStatistic.name();
    }

    private String topAge(List<?> ageGenders) {
        log.info("Processing Top Ages ", ageGenders);
        List<DemographicStatistic> demographicStatistics = new ArrayList<>();
        Map<String, Long> ageData = new HashMap<>();

        ageGenders.stream()
                .map(age -> safeCastList((List<?>) age, DemographicStatistic.class))
                .toList()
                .forEach(statistics -> statistics.forEach(statistic ->
                        ageData.put(statistic.name(),
                                ageData.getOrDefault(statistic.name(), 0L) + statistic.value1())));

        ageData.forEach((key, value) -> demographicStatistics.add(buildDemographicStatistic(key, value)));
        DemographicStatistic demographicStatistic = demographicStatistics.stream()
                .sorted((o1, o2) -> Long.compare(o2.value1(), o1.value1()))
                .toList()
                .get(0);

        return Objects.isNull(demographicStatistic)? "" : demographicStatistic.name();
    }
    private String topGender(List<?> ageGenders) {
        log.info("Genders ", ageGenders);
        Map<String, Long> genderMap = new HashMap<>();

        genderMap.put(FEMALE, 0L);
        genderMap.put(MALE, 0L);

        ageGenders.stream()
                .map(age -> safeCastList((List<?>) age, DemographicStatistic.class))
                .toList()
                .forEach(statistics -> statistics.forEach(statistic -> {
                    genderMap.put(FEMALE, genderMap.get(FEMALE) + statistic.value1());
                    genderMap.put(MALE, genderMap.get(MALE) + statistic.value2());
                }));

        return genderMap.get(FEMALE) > genderMap.get(MALE) ? FEMALE : MALE;
    }

    private DemographicStatistic buildDemographicStatistic(String name, Long value) {
        return DemographicStatistic.builder()
                .name(name)
                .value1(value)
                .build();
    }


    /**
     * This is the functionality of getting he graphs data reusing the retrieved data
     * @return AudienceDistributionGraph
     */

    private Mono<AudienceDistributionGraph> buildDistributionGraph(List<?> countries,
                                                                   List<?> ageGenders) {

        return Mono.just(
                AudienceDistributionGraph.builder()
                        .ageRange(ageGraph(ageGenders))
                        .genderPie(genderGraph(ageGenders))
                        .country(countryGraph(countries))
                        .build()
        );
    }

    private List<AudienceDistribution> ageGraph(List<?> ageGroups) {
        List<AudienceDistribution> ageDistributions = new ArrayList<>();
        Map<String, GenderAtomicLong> ageGroupData = new HashMap<>();

        ageGroups.stream()
                .map(ageGroup -> safeCastList((List<?>)  ageGroup, DemographicStatistic.class))
                .toList().forEach(statistics -> statistics.forEach(statistic -> ageGroupData.put(statistic.name(),
                        computeGender(ageGroupData.getOrDefault(statistic.name(), defaultGenderMap()), statistic.value1(),
                                statistic.value2()) )));

        ageGroupData.forEach((key, value) -> ageDistributions.add(AudienceDistribution.builder().name(key)
                        .value(value.female().get())
                        .value2(value.male().get())
                        .build())
        );

        return ageDistributions
                .stream()
                .sorted(Comparator.comparing(AudienceDistribution::name))
                .toList();
    }

    private GenderAtomicLong computeGender(GenderAtomicLong atomicLong, long value1, long value2) {
        atomicLong.female().addAndGet(value1);
        atomicLong.male().addAndGet(value2);
        return atomicLong;
    }

    private GenderAtomicLong defaultGenderMap() {
        return GenderAtomicLong.builder()
                .male(new AtomicLong(0L))
                .female(new AtomicLong(0L))
                .build();
    }

    private List<AudienceDistribution> genderGraph(List<?> genders) {
        List<AudienceDistribution> genderDistributions = new ArrayList<>();
        Map<String, Long> genderData = new HashMap<>();

        genders.stream()
                .map(gender -> safeCastList((List<?>)  gender, DemographicStatistic.class))
                .toList().forEach(statistics -> statistics.forEach(statistic -> {
                    genderData.put(FEMALE, genderData.getOrDefault(FEMALE, 0L) + statistic.value1());
                    genderData.put(MALE, genderData.getOrDefault(MALE, 0L) + statistic.value2());
                }));

        long total = calculateTotal(genderData);

        genderData.forEach((key, value) ->
                genderDistributions.add(AudienceDistribution.builder().name(key).value(percentage(value, total)).build()) );

        return genderDistributions
                .stream()
                .sorted(Comparator.comparing(AudienceDistribution::name))
                .toList();
    }

    private List<AudienceDistribution> countryGraph(List<?> countries) {
        List<AudienceDistribution> distributions = new ArrayList<>();
        Map<String, Long> countryData = new HashMap<>();

        countries.stream()
                .map(country -> safeCastList((List<?>)  country, DemographicStatistic.class))
                .toList().forEach(statistics -> statistics.forEach(statistic -> countryData.put(statistic.name(),
                        countryData.getOrDefault(statistic.name(), 0L) + statistic.value1())));

        long total = calculateTotal(countryData);

        countryData.forEach((key, value) ->
                distributions.add(AudienceDistribution.builder().name(key).value(percentage(value, total)).build()) );

        return distributions
                .stream()
                .sorted((o1, o2) -> Long.compare(o2.value(), o1.value()))
                .toList();
    }


    private long percentage(long part, long total) {
        return BigDecimal.valueOf(part)
                .setScale(2, RoundingMode.HALF_EVEN)
                .divide(BigDecimal.valueOf(total), RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.CEILING)
                .longValue();
    }

    private long calculateTotal(Map<String, Long> data) {
        return data.values().stream().reduce(0L, Long::sum);
    }

}
