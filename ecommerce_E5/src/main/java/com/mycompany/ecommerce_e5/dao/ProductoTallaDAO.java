/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.dao;

import com.mycompany.ecommerce_e5.dominio.ProductoTalla;
import com.mycompany.ecommerce_e5.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Clase DAO para la entidad ProductoTalla.
 * Maneja todas las operaciones de base de datos para las tallas de productos:
 * obtener por producto, por id, guardar, actualizar, eliminar y gestionar stock.
 *
 * @author Alberto Jimenez Garcia 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Ali Castro Roman 252191
 */
public class ProductoTallaDAO {

    /**
     * Obtiene todas las tallas disponibles para un producto.
     * @param productoId ID del producto
     * @return Lista de tallas del producto
     */
    public List<ProductoTalla> obtenerPorProductoId(int productoId) {
        if (productoId <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ProductoTalla> query = em.createQuery(
                    "SELECT pt FROM ProductoTalla pt WHERE pt.producto.id = :productoId ORDER BY pt.talla",
                    ProductoTalla.class);
            query.setParameter("productoId", productoId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene una talla por su ID.
     * @param id ID de la talla
     * @return ProductoTalla o null si no existe
     */
    public ProductoTalla obtenerPorId(int id) {
        if (id <= 0) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            ProductoTalla talla = em.find(ProductoTalla.class, id);
            if (talla != null && talla.getProducto() != null) {
                // Forzar carga del producto para evitar LazyInitializationException
                talla.getProducto().getNombre();
            }
            return talla;
        } finally {
            em.close();
        }
    }

    /**
     * Busca una talla especifica de un producto.
     * @param productoId ID del producto
     * @param talla Nombre de la talla (ej: "S", "M", "L")
     * @return ProductoTalla o null si no existe
     */
    public ProductoTalla obtenerPorProductoYTalla(int productoId, String talla) {
        if (productoId <= 0 || talla == null || talla.trim().isEmpty()) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ProductoTalla> query = em.createQuery(
                    "SELECT pt FROM ProductoTalla pt WHERE pt.producto.id = :productoId AND pt.talla = :talla",
                    ProductoTalla.class);
            query.setParameter("productoId", productoId);
            query.setParameter("talla", talla.trim());
            List<ProductoTalla> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Guarda una nueva talla de producto.
     * @param productoTalla Talla a guardar
     * @return Talla guardada con ID generado
     */
    public ProductoTalla guardar(ProductoTalla productoTalla) {
        if (productoTalla == null) {
            throw new IllegalArgumentException("La talla no puede ser null");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(productoTalla);
            em.getTransaction().commit();
            return productoTalla;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar talla: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Actualiza una talla de producto existente.
     * @param productoTalla Talla a actualizar
     * @return Talla actualizada
     */
    public ProductoTalla actualizar(ProductoTalla productoTalla) {
        if (productoTalla == null) {
            throw new IllegalArgumentException("La talla no puede ser null");
        }
        if (productoTalla.getId() <= 0) {
            throw new IllegalArgumentException("El ID de la talla es invalido");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ProductoTalla updated = em.merge(productoTalla);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar talla: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una talla de producto.
     * @param id ID de la talla a eliminar
     */
    public void eliminar(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID invalido");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ProductoTalla talla = em.find(ProductoTalla.class, id);
            if (talla != null) {
                em.remove(talla);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar talla: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Actualiza el stock de una talla especifica.
     * @param tallaId ID de la talla
     * @param cantidad Cantidad a sumar (negativo para restar)
     */
    public void actualizarStock(int tallaId, int cantidad) {
        if (tallaId <= 0) {
            throw new IllegalArgumentException("ID de talla invalido");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ProductoTalla talla = em.find(ProductoTalla.class, tallaId);
            if (talla != null) {
                int nuevoStock = talla.getStock() + cantidad;
                if (nuevoStock < 0) {
                    throw new IllegalStateException("El stock no puede ser negativo");
                }
                talla.setStock(nuevoStock);
                em.merge(talla);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Reduce el stock de una talla (usado al realizar compras).
     * @param tallaId ID de la talla
     * @param cantidad Cantidad a reducir
     * @return true si se pudo reducir, false si no hay stock suficiente
     */
    public boolean reducirStock(int tallaId, int cantidad) {
        if (tallaId <= 0 || cantidad <= 0) {
            throw new IllegalArgumentException("Parametros invalidos");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ProductoTalla talla = em.find(ProductoTalla.class, tallaId);
            if (talla == null) {
                em.getTransaction().rollback();
                return false;
            }

            if (talla.getStock() < cantidad) {
                em.getTransaction().rollback();
                return false;
            }

            talla.setStock(talla.getStock() - cantidad);
            em.merge(talla);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al reducir stock: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Elimina todas las tallas de un producto.
     * @param productoId ID del producto
     */
    public void eliminarPorProductoId(int productoId) {
        if (productoId <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM ProductoTalla pt WHERE pt.producto.id = :productoId")
              .setParameter("productoId", productoId)
              .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar tallas del producto: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
