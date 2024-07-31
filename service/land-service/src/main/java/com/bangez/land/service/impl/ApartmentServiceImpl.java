package com.bangez.land.service.impl;

import org.springframework.stereotype.Service;

import com.bangez.land.domain.dto.ApartmentDto;
import com.bangez.land.domain.mapper.ApartmentMapper;
import com.bangez.land.domain.model.ApartmentModel;
import com.bangez.land.repository.ApartmentRepository;
import com.bangez.land.service.ApartmentService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ApartmentServiceImpl implements ApartmentService{
    private final ApartmentMapper apartmentMapper;
    private final ApartmentRepository apartmentRepository;

    public ApartmentServiceImpl(ApartmentMapper apartmentMapper, ApartmentRepository apartmentRepository) {
        this.apartmentMapper = apartmentMapper;
        this.apartmentRepository = apartmentRepository;
    }
    @Override
    public Flux<ApartmentDto> getAllApartments() {
        return apartmentRepository.findAll()
                .map(apartmentMapper::toDTO);
    }
    @Override
    public Mono<ApartmentDto> getApartmentById(String id) {
        return apartmentRepository.findById(id)
                .map(apartmentMapper::toDTO)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Apartment not found")));
    }
    @Override
    public Mono<ApartmentDto> createApartment(ApartmentDto ApartmentDto) {
        ApartmentModel model = apartmentMapper.toModel(ApartmentDto);
        return apartmentRepository.save(model)
                .map(apartmentMapper::toDTO);
    }
    @Override
    public Mono<ApartmentDto> updateApartment(String id, ApartmentDto ApartmentDto) {
        return apartmentRepository.findById(id)
                .flatMap(existingApartment -> {
                    existingApartment.setAtclNo(ApartmentDto.getAtclNo());
                    existingApartment.setAtclNm(ApartmentDto.getAtclNm());
                    existingApartment.setRletTpNm(ApartmentDto.getRletTpNm());
                    existingApartment.setTradTpNm(ApartmentDto.getTradTpNm());
                    existingApartment.setFlrInfo(ApartmentDto.getFlrInfo());
                    existingApartment.setPrc(ApartmentDto.getPrc());
                    existingApartment.setRentPrc(ApartmentDto.getRentPrc());
                    existingApartment.setHanPrc(ApartmentDto.getHanPrc());
                    existingApartment.setSpc1(ApartmentDto.getSpc1());
                    existingApartment.setSpc2(ApartmentDto.getSpc2());
                    existingApartment.setDirection(ApartmentDto.getDirection());
                    existingApartment.setAtclCfmYmd(ApartmentDto.getAtclCfmYmd());
                    existingApartment.setLat(ApartmentDto.getLat());
                    existingApartment.setLng(ApartmentDto.getLng());
                    existingApartment.setAtclFetrDesc(ApartmentDto.getAtclFetrDesc());
                    existingApartment.setTagList(ApartmentDto.getTagList());
                    existingApartment.setBildNm(ApartmentDto.getBildNm());
                    existingApartment.setTown(ApartmentDto.getTown());
                    existingApartment.setRoadAddress(ApartmentDto.getRoadAddress());
                    existingApartment.setAddress(ApartmentDto.getAddress());
                    return apartmentRepository.save(existingApartment);
                })
                .map(apartmentMapper::toDTO)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Apartment not found")));
    }
    @Override
    public Mono<Void> deleteApartment(String id) {
        return apartmentRepository.deleteById(id)
                .onErrorResume(e -> Mono.error(new IllegalArgumentException("Failed to delete apartment", e)));
    }
    @Override
    public Flux<ApartmentDto> searchApartments(String tradTpNm, int minPrice, int maxPrice) {
        return apartmentRepository.findByTradTpNmAndPrcBetween(tradTpNm, minPrice, maxPrice)
                .map(apartmentMapper::toDTO);
    }
    @Override
    public Flux<ApartmentDto> searchApartmentsByPrice(int minPrice, int maxPrice) {
        return apartmentRepository.findByPrcBetween(minPrice, maxPrice)
                .map(apartmentMapper::toDTO);
    }
}
