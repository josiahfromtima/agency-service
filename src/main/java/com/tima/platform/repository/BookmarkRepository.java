package com.tima.platform.repository;

import com.tima.platform.domain.Bookmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/1/24
 */
public interface BookmarkRepository extends ReactiveCrudRepository<Bookmark, Integer> {
    Mono<Bookmark> findByTitleAndType(String title, String type);

    Flux<Bookmark> findByUserPublicIdAndType(String publicId, String type, Pageable pageable);
}
