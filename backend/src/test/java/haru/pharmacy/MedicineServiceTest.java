package haru.pharmacy;

import haru.pharmacy.dto.medicine.MedicineCreateDto;
import haru.pharmacy.dto.medicine.MedicineResponseDto;
import haru.pharmacy.dto.medicine.MedicineUpdateDto;
import haru.pharmacy.exception.ResourceNotFoundException;
import haru.pharmacy.mapper.MedicineMapper;
import haru.pharmacy.model.Medicine;
import haru.pharmacy.repository.MedicineRepository;
import haru.pharmacy.service.impl.MedicineServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MedicineServiceImpl}.
 * <p>
 * Verifies the behavior of medicine management operations, including
 * creation, retrieval, updates, and deletion.
 * Matches the optimized architecture (DTO projections).
 *
 * @author Haru
 * @version 3.0
 */
@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock private MedicineRepository repository;
    @Mock private MedicineMapper mapper;

    @InjectMocks
    private MedicineServiceImpl service;

    @Test
    @DisplayName("Create should save entity and return DTO with 0 quantity")
    void create_ShouldSave() {
        // Given
        MedicineCreateDto dto = new MedicineCreateDto(
                "Aspirin", BigDecimal.TEN, "Bayer", "Desc", false
        );
        Medicine entity = new Medicine();
        Medicine saved = new Medicine();
        saved.setId(1L);
        MedicineResponseDto responseDto = new MedicineResponseDto(
                1L, "Aspirin", BigDecimal.TEN, "Bayer", "Desc", false, 0L
        );

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved, 0L)).thenReturn(responseDto);

        // When
        MedicineResponseDto result = service.create(dto);

        // Then
        assertEquals(1L, result.id());
        assertEquals(0L, result.quantity());
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Update should modify entity and fetch updated DTO via projection")
    void update_ShouldUpdate_WhenFound() {
        Long id = 1L;
        MedicineUpdateDto dto = new MedicineUpdateDto(
                "NewName", BigDecimal.ONE, "NewManuf", "Desc", true
        );
        Medicine existing = new Medicine();
        existing.setId(id);

        MedicineResponseDto updatedDto = new MedicineResponseDto(
                id, "NewName", BigDecimal.ONE, "NewManuf", "Desc", true, 50L
        );

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(repository.findDtoById(id)).thenReturn(Optional.of(updatedDto));

        // When
        MedicineResponseDto result = service.update(id, dto);

        // Then
        assertEquals("NewName", result.name());
        assertEquals(50L, result.quantity());

        verify(mapper).updateEntity(dto, existing);
        verify(repository).save(existing);
        verify(repository).findDtoById(id);
    }

    @Test
    @DisplayName("Update should throw exception when medicine not found")
    void update_ShouldThrow_WhenNotFound() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(id, new MedicineUpdateDto(null, null, null, null, null)));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Get should return DTO from optimized query")
    void get_ShouldReturnDto_WhenFound() {
        Long id = 1L;
        MedicineResponseDto dtoFromDb = new MedicineResponseDto(
                id, "Test", BigDecimal.ONE, "Test", "Test", false, 100L);

        when(repository.findDtoById(id)).thenReturn(Optional.of(dtoFromDb));

        MedicineResponseDto result = service.get(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(100L, result.quantity());
        verify(mapper, never()).toDto(any(Medicine.class));
    }

    @Test
    @DisplayName("Get should throw exception when not found")
    void get_ShouldThrow_WhenMissing() {
        Long id = 99L;
        when(repository.findDtoById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(id));
    }

    @Test
    @DisplayName("GetAll should return list using optimized query")
    void getAll_ShouldReturnList() {
        MedicineResponseDto item = new MedicineResponseDto(
                1L, "A", BigDecimal.ONE, "M", "D", false, 10L
        );
        when(repository.findAllSummarized()).thenReturn(List.of(item));

        List<MedicineResponseDto> result = service.getAll();

        assertFalse(result.isEmpty());
        assertEquals(10L, result.getFirst().quantity());
        verify(repository).findAllSummarized();
    }

    @Test
    @DisplayName("Delete should call repository when entity exists")
    void delete_ShouldCallRepo() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Delete should throw exception when entity missing")
    void delete_ShouldThrow_WhenMissing() {
        Long id = 99L;

        when(repository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(id));

        verify(repository, never()).deleteById(any());
    }
}
