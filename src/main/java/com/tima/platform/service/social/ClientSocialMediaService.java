package com.tima.platform.service.social;

import com.tima.platform.converter.ClientSocialMediaConverter;
import com.tima.platform.domain.ClientSocialMedia;
import com.tima.platform.domain.SocialMedia;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.api.response.ClientSocialMediaRecord;
import com.tima.platform.repository.ClientSocialMediaRepository;
import com.tima.platform.util.AppError;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static com.tima.platform.converter.ClientSocialMediaConverter.json;
import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_INFLUENCER;
import static com.tima.platform.util.AppUtil.buildAppResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
@Service
@RequiredArgsConstructor
public class ClientSocialMediaService {
    private final LoggerHelper log = LoggerHelper.newInstance(ClientSocialMediaService.class.getName());
    private final ClientSocialMediaRepository mediaRepository;
    private final SocialMediaService mediaService;
    private final AbstractHandleService handleService;

    private static final String SOCIAL_MSG = "User Selected Social Media request executed successfully";
    private static final String INVALID_NAME = "User has no linked social media account";
    private static final String INVALID_SOCIAL_NAME = "Social media name is not linked to your user account";

    @PreAuthorize(ADMIN_INFLUENCER)
    public Mono<AppResponse> getUserSocialMedia(String publicId) {
        log.info("Getting all User selected social media records...");
        return getSocialMediaUser(publicId)
                .map(ClientSocialMediaConverter::mapToRecord)
                .map(r -> buildAppResponse(r.selectedSocialMedia(), SOCIAL_MSG));
    }
    public Mono<AppResponse> addUserSocialMedia(String publicId, ClientSelectedSocialMedia socialMedia) {
        log.info("Adding User selected social media record");
        return validateName(socialMedia.name())
                .flatMap(sM ->
                        handleService.validateHandle(socialMedia.name(), socialMedia.handle(), sM, socialMedia))
                .flatMap(media -> addOrEdit(publicId, media))
                .flatMap(mediaRepository::save)
                .map(ClientSocialMediaConverter::mapToRecord)
                .map(r -> buildAppResponse(r.selectedSocialMedia(), SOCIAL_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_INFLUENCER)
    public Mono<AppResponse> deleteUserSocialMedia(String publicId, String name) {
        log.info("Deleting User selected social media record ", name);
        return getSocialMediaUser(publicId)
                .flatMap(sM -> remove(sM, name))
                .flatMap(mediaRepository::save)
                .map(ClientSocialMediaConverter::mapToRecord)
                .map(r -> buildAppResponse(r.selectedSocialMedia(), SOCIAL_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_SOCIAL_NAME), BAD_REQUEST.value()));
    }

    private Mono<ClientSocialMedia> getSocialMediaUser(String publicId) {
        return mediaRepository.findByUserId(publicId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_NAME), BAD_REQUEST.value()));
    }

    private Mono<SocialMedia> validateName(String name) {
        return mediaService.getSocialMedia(name);
    }
    private Mono<ClientSocialMediaRecord> buildRecord(String publicId, ClientSelectedSocialMedia media) {
        log.info("build Record: ", media);
        return Mono.just(ClientSocialMediaRecord.builder()
                        .userId(publicId)
                        .selectedSocialMedia(List.of(media))
                .build());
    }
    private Mono<ClientSocialMedia> addOrEdit(String publicId, ClientSelectedSocialMedia media) {
        log.info("Adding or Editing User Social Media selections");
        return mediaRepository.findByUserId(publicId)
                .doOnNext(clientSocialMedia -> log.info("Found existing linked social media"))
                .map(clientSocialMedia -> {
                    ClientSocialMediaRecord socialMedia = ClientSocialMediaConverter.mapToRecord(clientSocialMedia);
                    List<ClientSelectedSocialMedia> mediaList = socialMedia.selectedSocialMedia();
                    boolean isNew = checkExistence(mediaList, media.name());
                    log.info("New Media Added? ", isNew);
                    if(isNew) {
                        mediaList.add(media);
                        clientSocialMedia.setSelectedSocialMedia(json(mediaList));
                    }
                    return clientSocialMedia;
                })
                .switchIfEmpty(
                        buildRecord(publicId, media)
                        .map(ClientSocialMediaConverter::mapToEntity)
                );
    }

    private Mono<ClientSocialMedia> remove(ClientSocialMedia socialMedia, String name) {
        ClientSocialMediaRecord mediaRecord = ClientSocialMediaConverter.mapToRecord(socialMedia);
        ClientSelectedSocialMedia media = checkExistence(name, mediaRecord.selectedSocialMedia());
        if(Objects.nonNull(media)) {
            mediaRecord.selectedSocialMedia().remove(media);
            socialMedia.setSelectedSocialMedia(json(mediaRecord.selectedSocialMedia()));
            return Mono.just(socialMedia);
        }
        return Mono.empty();
    }

    private boolean checkExistence(List<ClientSelectedSocialMedia> mediaList, String name) {
        log.info("Check Social Media Selected ", name);
        return mediaList.stream()
                .noneMatch(media -> media.name().trim().equals(name.trim()));
    }
    private ClientSelectedSocialMedia checkExistence(String  name, List<ClientSelectedSocialMedia> mediaList) {
        log.info("Delete Social Media Selected ", name);
        return mediaList.stream()
                .filter(media -> media.name().trim().equals(name.trim()))
                .findFirst()
                .orElse(null);
    }
}
