package haru.pharmacy.service.impl;

import haru.pharmacy.dto.medicine.MedicineCreateDto;
import haru.pharmacy.dto.medicine.MedicineResponseDto;
import haru.pharmacy.dto.medicine.MedicineUpdateDto;
import haru.pharmacy.exception.ResourceNotFoundException;
import haru.pharmacy.mapper.MedicineMapper;
import haru.pharmacy.model.Medicine;
import haru.pharmacy.repository.MedicineRepository;
import haru.pharmacy.service.interfaces.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {
    private final MedicineRepository repository;
    private final MedicineMapper mapper;

    @Override
    public MedicineResponseDto create(MedicineCreateDto dto) {
        Medicine entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public MedicineResponseDto update(Long id, MedicineUpdateDto dto) {
        Medicine entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.medicine.not_found", id));

        mapper.updateEntity(dto, entity);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public MedicineResponseDto get(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("error.medicine.not_found", id));
    }

    @Override
    public List<MedicineResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("error.medicine.not_found", id);
        }
        repository.deleteById(id);
    }
}