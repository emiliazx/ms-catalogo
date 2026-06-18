package com.costuras.catalogo.controller;

import com.costuras.catalogo.dto.ProductoRequest;
import com.costuras.catalogo.dto.ProductoResponse;
import com.costuras.catalogo.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/catalogo/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión del catálogo de productos")
public class ProductoController {

    private final CatalogoService catalogoService;

    @Operation(summary = "Listar productos",
               description = "Retorna todos los productos activos. Permite filtrar por nombre o categoría.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Integer categoriaId) {
        if (buscar != null) return ResponseEntity.ok(catalogoService.buscarProductos(buscar));
        if (categoriaId != null) return ResponseEntity.ok(catalogoService.listarPorCategoria(categoriaId));
        return ResponseEntity.ok(catalogoService.listarProductos());
    }

    @Operation(summary = "Obtener producto por ID",
               description = "Retorna el detalle de un producto específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> getProducto(@PathVariable Integer id) {
        return ResponseEntity.ok(catalogoService.getProducto(id));
    }

    @Operation(summary = "Crear producto",
               description = "Crea un nuevo producto en el catálogo. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = catalogoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Producto creado correctamente", "producto", producto));
    }

    @Operation(summary = "Editar producto",
               description = "Actualiza los datos de un producto existente. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editar(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = catalogoService.editarProducto(id, request);
        return ResponseEntity.ok(Map.of("mensaje", "Producto actualizado correctamente", "producto", producto));
    }

    @Operation(summary = "Desactivar producto",
               description = "Desactiva (eliminación lógica) un producto del catálogo. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto desactivado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> desactivar(@PathVariable Integer id) {
        catalogoService.desactivarProducto(id);
        return ResponseEntity.ok(Map.of("mensaje", "Producto desactivado correctamente"));
    }
}