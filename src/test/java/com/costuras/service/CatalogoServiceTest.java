package com.costuras.service;



import com.costuras.catalogo.dto.CategoriaRequest;
import com.costuras.catalogo.dto.CategoriaResponse;
import com.costuras.catalogo.dto.ProductoRequest;
import com.costuras.catalogo.dto.ProductoResponse;
import com.costuras.catalogo.model.Categoria;
import com.costuras.catalogo.model.Producto;
import com.costuras.catalogo.repository.CategoriaRepository;
import com.costuras.catalogo.repository.ProductoRepository;
import com.costuras.catalogo.service.CatalogoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CatalogoService catalogoService;

    private Categoria categoria;
    private Producto producto;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1)
                .nombre("Telas")
                .descripcion("Telas variadas")
                .build();

        producto = Producto.builder()
                .id(1)
                .nombre("Tela de algodón")
                .descripcion("Tela suave")
                .precio(new BigDecimal("15000"))
                .stock(100)
                .activo(true)
                .categoria(categoria)
                .build();
    }

    // ── Producto ──────────────────────────────────────────────

    @Test
    void listarProductos_retornaLista() {
        when(productoRepository.findByActivoTrue()).thenReturn(List.of(producto));

        List<ProductoResponse> result = catalogoService.listarProductos();

        assertEquals(1, result.size());
        assertEquals("Tela de algodón", result.get(0).getNombre());
        verify(productoRepository).findByActivoTrue();
    }

    @Test
    void buscarProductos_retornaCoincidencias() {
        when(productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue("tela"))
                .thenReturn(List.of(producto));

        List<ProductoResponse> result = catalogoService.buscarProductos("tela");

        assertEquals(1, result.size());
    }

    @Test
    void getProducto_existente_retornaResponse() {
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        ProductoResponse result = catalogoService.getProducto(1);

        assertEquals("Tela de algodón", result.getNombre());
        assertEquals(new BigDecimal("15000"), result.getPrecio());
    }

    @Test
    void getProducto_noExistente_lanzaExcepcion() {
        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> catalogoService.getProducto(99));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void crearProducto_sinCategoria_guardaCorrectamente() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Hilo");
        request.setDescripcion("Hilo negro");
        request.setPrecio(new BigDecimal("500"));
        request.setStock(200);
        request.setIdCategoria(null);

        Producto guardado = Producto.builder()
                .id(2).nombre("Hilo").descripcion("Hilo negro")
                .precio(new BigDecimal("500")).stock(200).activo(true).build();
        when(productoRepository.save(any())).thenReturn(guardado);

        ProductoResponse result = catalogoService.crearProducto(request);

        assertEquals("Hilo", result.getNombre());
        verify(productoRepository).save(any());
    }

    @Test
    void crearProducto_conCategoria_asociaCorrectamente() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Tela seda");
        request.setPrecio(new BigDecimal("30000"));
        request.setStock(50);
        request.setIdCategoria(1);

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any())).thenReturn(producto);

        ProductoResponse result = catalogoService.crearProducto(request);

        assertNotNull(result);
        verify(categoriaRepository).findById(1);
    }

    @Test
    void editarProducto_existente_actualizaCampos() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Tela modificada");
        request.setPrecio(new BigDecimal("20000"));
        request.setStock(80);

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any())).thenReturn(producto);

        ProductoResponse result = catalogoService.editarProducto(1, request);

        assertNotNull(result);
        verify(productoRepository).save(any());
    }

    @Test
    void desactivarProducto_existente_ponActivoFalse() {
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        catalogoService.desactivarProducto(1);

        assertFalse(producto.getActivo());
        verify(productoRepository).save(producto);
    }

    @Test
    void desactivarProducto_noExistente_lanzaExcepcion() {
        when(productoRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> catalogoService.desactivarProducto(5));
    }

    // ── Categoría ─────────────────────────────────────────────

    @Test
    void listarCategorias_retornaLista() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<CategoriaResponse> result = catalogoService.listarCategorias();

        assertEquals(1, result.size());
        assertEquals("Telas", result.get(0).getNombre());
    }

    @Test
    void crearCategoria_nueva_guardaCorrectamente() {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("Botones");
        request.setDescripcion("Botones varios");

        when(categoriaRepository.existsByNombre("Botones")).thenReturn(false);
        when(categoriaRepository.save(any())).thenReturn(
                Categoria.builder().id(2).nombre("Botones").descripcion("Botones varios").build());

        CategoriaResponse result = catalogoService.crearCategoria(request);

        assertEquals("Botones", result.getNombre());
    }

    @Test
    void crearCategoria_duplicada_lanzaExcepcion() {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("Telas");

        when(categoriaRepository.existsByNombre("Telas")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> catalogoService.crearCategoria(request));
    }

    @Test
    void editarCategoria_existente_actualizaNombre() {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("Telas Premium");
        request.setDescripcion("Telas de alta calidad");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any())).thenReturn(categoria);

        CategoriaResponse result = catalogoService.editarCategoria(1, request);

        assertNotNull(result);
        verify(categoriaRepository).save(any());
    }
}

