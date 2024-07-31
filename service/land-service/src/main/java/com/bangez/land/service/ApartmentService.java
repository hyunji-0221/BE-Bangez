package com.bangez.land.service;

import com.bangez.land.domain.dto.ApartmentDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
public interface ApartmentService {
    Flux<ApartmentDto> getAllApartments();
    Mono<ApartmentDto> getApartmentById(String id);
    Mono<ApartmentDto> createApartment(ApartmentDto ApartmentDto);
    Mono<ApartmentDto> updateApartment(String id, ApartmentDto ApartmentDto);
    Mono<Void> deleteApartment(String id);
    Flux<ApartmentDto> searchApartments(String tradTpNm, int minPrice, int maxPrice);
    Flux<ApartmentDto> searchApartmentsByPrice(int minPrice, int maxPrice);
    
}
