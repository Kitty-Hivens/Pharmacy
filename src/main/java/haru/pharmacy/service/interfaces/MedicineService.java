package haru.pharmacy.service.interfaces;

import haru.pharmacy.dto.medicine.MedicineCreateDto;
import haru.pharmacy.dto.medicine.MedicineResponseDto;
import haru.pharmacy.dto.medicine.MedicineUpdateDto;

import java.util.List;

public interface MedicineService {


    MedicineResponseDto create(MedicineCreateDto dto);

    MedicineResponseDto update(Long id, MedicineUpdateDto dto);

    MedicineResponseDto get(Long id);

    List<MedicineResponseDto> getAll();

    void delete(Long id);
}
