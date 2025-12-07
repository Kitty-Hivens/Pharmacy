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
 * This class verifies the behavior of medicine management operations, including
 * creation, retrieval, updates, and deletion. It mocks the repository and mapper
 * layers to test the service logic in isolation.
 *
 * @author Haru
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock
    private MedicineRepository repository;

    @Mock
    private MedicineMapper mapper;

    @InjectMocks
    private MedicineServiceImpl service;

    /**
     * Verifies that the create method correctly maps the input DTO to an entity,
     * saves it using the repository, and maps the saved entity back to a response DTO.
     */
    @Test
    @DisplayName("Create should save medicine and return mapped DTO")
    void create_ShouldSaveAndReturnDto() {
        // Given
        MedicineCreateDto createDto = new MedicineCreateDto(
                "Aspirin", BigDecimal.TEN, "Bayer", "Pain reliever", false
        );
        Medicine entityToSave = new Medicine();
        Medicine savedEntity = new Medicine();
        savedEntity.setId(1L);

        MedicineResponseDto expectedResponse = new MedicineResponseDto(
                1L, "Aspirin", BigDecimal.TEN, "Bayer", "Pain reliever", false
        );

        when(mapper.toEntity(createDto)).thenReturn(entityToSave);
        when(repository.save(entityToSave)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(expectedResponse);

        // When
        MedicineResponseDto actualResponse = service.create(createDto);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.name(), actualResponse.name());
        verify(repository).save(entityToSave);
    }

    /**
     * Verifies that the update method finds an existing entity, updates its fields
     * via the mapper, saves the changes, and returns the updated DTO.
     */
    @Test
    @DisplayName("Update should modify existing medicine and return updated DTO")
    void update_ShouldUpdateEntity_WhenExists() {
        // Given
        Long id = 1L;
        MedicineUpdateDto updateDto = new MedicineUpdateDto(
                "Aspirin Ultra", BigDecimal.valueOf(15), "Bayer", "Stronger", false
        );
        Medicine existingEntity = new Medicine();
        existingEntity.setId(id);
        
        Medicine savedEntity = new Medicine(); // Simulating state after save
        
        MedicineResponseDto expectedResponse = new MedicineResponseDto(
                id, "Aspirin Ultra", BigDecimal.valueOf(15), "Bayer", "Stronger", false
        );

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(existingEntity)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(expectedResponse);

        // When
        MedicineResponseDto actualResponse = service.update(id, updateDto);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.name(), actualResponse.name());
        verify(mapper).updateEntity(updateDto, existingEntity);
        verify(repository).save(existingEntity);
    }

    /**
     * Verifies that the update method throws {@link ResourceNotFoundException}
     * when the medicine with the given ID does not exist.
     */
    @Test
    @DisplayName("Update should throw exception when medicine not found")
    void update_ShouldThrow_WhenNotFound() {
        // Given
        Long id = 999L;
        MedicineUpdateDto updateDto = new MedicineUpdateDto(
                "Name", BigDecimal.ONE, "Manuf", "Desc", false
        );

        when(repository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.update(id, updateDto));
        verify(repository, never()).save(any());
    }

    /**
     * Verifies that the get method returns a DTO when the medicine exists.
     */
    @Test
    @DisplayName("Get should return DTO when found")
    void get_ShouldReturnDto_WhenFound() {
        // Given
        Long id = 1L;
        Medicine entity = new Medicine();
        entity.setId(id);
        MedicineResponseDto expectedResponse = new MedicineResponseDto(
                id, "Test", BigDecimal.TEN, "Test", "Test", false
        );

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(expectedResponse);

        // When
        MedicineResponseDto actualResponse = service.get(id);

        // Then
        assertNotNull(actualResponse);
        assertEquals(id, actualResponse.id());
    }

    /**
     * Verifies that the get method throws {@link ResourceNotFoundException}
     * when the medicine is not found.
     */
    @Test
    @DisplayName("Get should throw exception when not found")
    void get_ShouldThrow_WhenNotFound() {
        // Given
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.get(id));
    }

    /**
     * Verifies that getAll retrieves all entities from the repository and
     * maps them to a list of DTOs.
     */
    @Test
    @DisplayName("GetAll should return list of DTOs")
    void getAll_ShouldReturnList() {
        // Given
        Medicine entity = new Medicine();
        MedicineResponseDto dto = new MedicineResponseDto(
                1L, "Test", BigDecimal.TEN, "Test", "Test", false
        );

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // When
        List<MedicineResponseDto> result = service.getAll();

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    /**
     * Verifies that the delete method calls the repository's deleteById method.
     */
    @Test
    @DisplayName("Delete should invoke repository delete")
    void delete_ShouldCallRepository() {
        // Given
        Long id = 1L;

        // When
        service.delete(id);

        // Then
        verify(repository).deleteById(id);
    }
}