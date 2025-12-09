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
 *
 * @author Haru
 * @version 2.0
 */
@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock private MedicineRepository repository;
    @Mock private MedicineMapper mapper;

    @InjectMocks
    private MedicineServiceImpl service;

    @Test
    @DisplayName("Create should save entity and return DTO")
    void create_ShouldSave() {
        MedicineCreateDto dto = new MedicineCreateDto(
                "Aspirin", BigDecimal.TEN, "Bayer", "Desc", false
        );
        Medicine entity = new Medicine();
        Medicine saved = new Medicine();
        saved.setId(1L);
        MedicineResponseDto responseDto = new MedicineResponseDto(
                1L, "Aspirin", BigDecimal.TEN, "Bayer", "Desc", false
        );

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(responseDto);

        MedicineResponseDto result = service.create(dto);

        assertEquals(1L, result.id());
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Create should save medicine and return mapped DTO")
    void create_ShouldSaveAndReturnDto() {
        // Given
        MedicineCreateDto dto = new MedicineCreateDto(
                "Aspirin", BigDecimal.TEN, "Bayer", "Pain reliever", false
        );
        Medicine entity = new Medicine();
        Medicine savedEntity = new Medicine();
        savedEntity.setId(1L);

        MedicineResponseDto expectedResponse = new MedicineResponseDto(
                1L, "Aspirin", BigDecimal.TEN, "Bayer", "Pain reliever", false
        );

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(expectedResponse);

        // When
        MedicineResponseDto result = service.create(dto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Aspirin", result.name());
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Update should modify entity when found")
    void update_ShouldUpdate_WhenFound() {
        Long id = 1L;
        MedicineUpdateDto dto = new MedicineUpdateDto(
                "NewName", BigDecimal.ONE, "NewManuf", "Desc", true
        );
        Medicine existing = new Medicine();
        existing.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        when(mapper.toDto(existing)).thenReturn(new MedicineResponseDto(
                id, "NewName", BigDecimal.ONE, "NewManuf", "Desc", true
        ));

        service.update(id, dto);

        verify(mapper).updateEntity(dto, existing);
        verify(repository).save(existing);
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
    @DisplayName("Get should return DTO when found")
    void get_ShouldReturnDto_WhenFound() {
        Long id = 1L;
        Medicine entity = new Medicine();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(new MedicineResponseDto(
                id, "Test", BigDecimal.ONE, "Test", "Test", false));

        MedicineResponseDto result = service.get(id);

        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    @DisplayName("Get should throw exception when not found")
    void get_ShouldThrow_WhenMissing() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(id));
    }

    @Test
    @DisplayName("Get should throw exception when not found")
    void get_ShouldThrow_WhenNotFound() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(id));
    }

    @Test
    @DisplayName("GetAll should return list")
    void getAll_ShouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(new Medicine()));

        List<MedicineResponseDto> result = service.getAll();

        assertFalse(result.isEmpty());
        verify(mapper, atLeastOnce()).toDto(any());
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
