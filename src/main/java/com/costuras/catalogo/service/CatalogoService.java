package com.costuras.catalogo.service;

import com.costuras.catalogo.dto.*;
import com.costuras.catalogo.model.Categoria;
import com.costuras.catalogo.model.Producto;
import com.costuras.catalogo.repository.CategoriaRepository;
import com.costuras.catalogo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    // ── Productos públicos ─────────────────────────────────────────────────

    public List<ProductoResponse> listarProductos() {
        return productoRepository.findByActivoTrue()
                .stream().map(this::toProductoResponse).toList();
    }

    public List<ProductoResponse> buscarProductos(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre)
                .stream().map(this::toProductoResponse).toList();
    }

    public List<ProductoResponse> listarPorCategoria(Integer idCategoria) {
        return productoRepository.findByCategoriaIdAndActivoTrue(idCategoria)
                .stream().map(this::toProductoResponse).toList();
    }

    public ProductoResponse getProducto(Integer id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
        return toProductoResponse(p);
    }

    // ── Productos ADMIN ────────────────────────────────────────────────────

    public ProductoResponse crearProducto(ProductoRequest request) {
        Categoria categoria = null;
        if (request.getIdCategoria() != null) {
            categoria = categoriaRepository.findById(request.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        }

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .activo(true)
                .categoria(categoria)
                .build();

        return toProductoResponse(productoRepository.save(producto));
    }

    public ProductoResponse editarProducto(Integer id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));

        Categoria categoria = null;
        if (request.getIdCategoria() != null) {
            categoria = categoriaRepository.findById(request.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        }

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);

        return toProductoResponse(productoRepository.save(producto));
    }

    public void desactivarProducto(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    // ── Categorías ADMIN ───────────────────────────────────────────────────

    public List<CategoriaResponse> listarCategorias() {
        return categoriaRepository.findAll()
                .stream().map(this::toCategoriaResponse).toList();
    }

    public CategoriaResponse crearCategoria(CategoriaRequest request) {
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }
        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();
        return toCategoriaResponse(categoriaRepository.save(categoria));
    }

    public CategoriaResponse editarCategoria(Integer id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + id));
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return toCategoriaResponse(categoriaRepository.save(categoria));
    }

    // ── Mappers ────────────────────────────────────────────────────────────

    private ProductoResponse toProductoResponse(Producto p) {
        return ProductoResponse.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .stock(p.getStock())
                .activo(p.getActivo())
                .categoria(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .build();
    }

    private CategoriaResponse toCategoriaResponse(Categoria c) {
        return CategoriaResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .build();
    }
}
