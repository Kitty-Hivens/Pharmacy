package haru.pharmacy;

import haru.pharmacy.dto.SupplierDto;
import haru.pharmacy.mapper.SupplierMapper;
import haru.pharmacy.model.Supplier;
import haru.pharmacy.repository.SupplierRepository;
import haru.pharmacy.service.SupplierService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SupplierService}.
 * <p>
 * Ensures that supplier management operations interact correctly with the repository.
 *
 * @author Haru
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock private SupplierRepository repository;
    @Mock private SupplierMapper mapper;

    @InjectMocks
    private SupplierService service;

    /**
     * Verifies that creating a supplier saves it to the database.
     */
    @Test
    @DisplayName("Create should save supplier")
    void create_ShouldSaveSupplier() {
        SupplierDto dto = new SupplierDto(null, "FarmCo", "John", "email", "123");
        Supplier entity = new Supplier();
        Supplier savedEntity = new Supplier();
        
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        
        service.create(dto);
        
        verify(repository).save(entity);
    }

    /**
     * Verifies that getAll returns a list of mapped DTOs.
     */
    @Test
    @DisplayName("GetAll should return list of DTOs")
    void getAll_ShouldReturnList() {
        Supplier entity = new Supplier();
        SupplierDto dto = new SupplierDto(1L, "FarmCo", "John", "email", "123");
        
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);
        
        List<SupplierDto> result = service.getAll();
        
        assertEquals(1, result.size());
        assertEquals("FarmCo", result.getFirst().name());
    }
}
