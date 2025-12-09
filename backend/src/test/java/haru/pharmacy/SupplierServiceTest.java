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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SupplierService}.
 *
 * @author Haru
 * @version 2.0
 */
@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock private SupplierRepository repository;
    @Mock private SupplierMapper mapper;

    @InjectMocks private SupplierService service;

    @Test
    @DisplayName("Create should save supplier")
    void create_ShouldSave() {
        SupplierDto dto = new SupplierDto(null, "Name", "Person", "mail", "123");
        Supplier entity = new Supplier();
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        service.create(dto);

        verify(repository).save(entity);
    }

    @Test
    @DisplayName("GetAll should return list")
    void getAll_ShouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(new Supplier()));
        assertFalse(service.getAll().isEmpty());
    }
}
