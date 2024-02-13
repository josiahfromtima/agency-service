package com.tima.platform.service.bookmark;

import com.tima.platform.model.api.AppResponse;
import com.tima.platform.service.bookmark.impl.CampaignBookmarkService;
import com.tima.platform.service.bookmark.impl.InfluencerBookmarkService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/11/24
 */
@Service
@RequiredArgsConstructor
public class BookmarkManager {

    private final CampaignBookmarkService bookmarkService;
    private final InfluencerBookmarkService influencerBookmarkService;
    Map<String, BookmarkFactory<AppResponse>> bookmarkFactoryMap  = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        bookmarkFactoryMap.put("CAMPAIGN", bookmarkService);
        bookmarkFactoryMap.put("INFLUENCER", influencerBookmarkService);
    }
    public BookmarkFactory<AppResponse> getInstance(String type) { return  bookmarkFactoryMap.get(type); }
}
