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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {
    private final MedicineRepository repository;
    private final MedicineMapper mapper;

    @Override
    public MedicineResponseDto create(MedicineCreateDto dto) {
        Medicine entity = mapper.toEntity(dto);
        Medicine saved = repository.save(entity);
        return mapper.toDto(saved, 0L);
    }

    @Override
    @Transactional
    public MedicineResponseDto update(Long id, MedicineUpdateDto dto) {
        Medicine entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.medicine.not_found", id));

        mapper.updateEntity(dto, entity);
        repository.save(entity);

        return repository.findDtoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.medicine.not_found", id));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicineResponseDto get(Long id) {
        return repository.findDtoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.medicine.not_found", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineResponseDto> getAll() {
        return repository.findAllSummarized();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("error.medicine.not_found", id);
        }
        repository.deleteById(id);
    }
}
