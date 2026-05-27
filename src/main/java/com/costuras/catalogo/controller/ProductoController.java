package com.costuras.catalogo.controller;

import com.costuras.catalogo.dto.ProductoRequest;
import com.costuras.catalogo.dto.ProductoResponse;
import com.costuras.catalogo.service.CatalogoService;
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
public class ProductoController {

    private final CatalogoService catalogoService;

   
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Integer categoriaId
    ) {
        if (buscar != null) return ResponseEntity.ok(catalogoService.buscarProductos(buscar));
        if (categoriaId != null) return ResponseEntity.ok(catalogoService.listarPorCategoria(categoriaId));
        return ResponseEntity.ok(catalogoService.listarProductos());
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> getProducto(@PathVariable Integer id) {
        return ResponseEntity.ok(catalogoService.getProducto(id));
    }

  
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = catalogoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Producto creado correctamente", "producto", producto));
    }

    
    public ResponseEntity<Map<String, Object>> editar(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoRequest request
    ) {
        ProductoResponse producto = catalogoService.editarProducto(id, request);
        return ResponseEntity.ok(Map.of("mensaje", "Producto actualizado correctamente", "producto", producto));
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> desactivar(@PathVariable Integer id) {
        catalogoService.desactivarProducto(id);
        return ResponseEntity.ok(Map.of("mensaje", "Producto desactivado correctamente"));
    }
}
