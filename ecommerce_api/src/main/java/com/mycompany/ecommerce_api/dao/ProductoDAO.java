/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_api.dao;

import com.mycompany.ecommerce_api.dominio.Producto;
import com.mycompany.ecommerce_api.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Clase DAO para la entidad Producto.
 * Maneja todas las operaciones de base de datos para productos:
 * obtener todos, buscar por id, por categoria, por nombre, guardar,
 * actualizar, eliminar y gestionar existencias.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class ProductoDAO {

    public List<Producto> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria ORDER BY p.id DESC", 
                    Producto.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> obtenerPaginados(int pagina, int tamano) {
        if (pagina < 1) {
            throw new IllegalArgumentException("La página debe ser mayor o igual a 1");
        }
        if (tamano < 1) {
            throw new IllegalArgumentException("El tamaño debe ser mayor a 0");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria ORDER BY p.id DESC", 
                    Producto.class);
            query.setFirstResult((pagina - 1) * tamano);
            query.setMaxResults(tamano);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long contarProductos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(p) FROM Producto p", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public Producto obtenerPorId(int id) {
        if (id <= 0) {
            return null;
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Producto producto = em.find(Producto.class, id);
            if (producto != null && producto.getCategoria() != null) {
                // Forzar carga de categoría para evitar LazyInitializationException
                producto.getCategoria().getNombre();
            }
            return producto;
        } finally {
            em.close();
        }
    }

    public List<Producto> obtenerPorCategoria(int categoriaId) {
        if (categoriaId <= 0) {
            throw new IllegalArgumentException("ID de categoría inválido");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria " +
                    "WHERE p.categoria.id = :categoriaId ORDER BY p.nombre", 
                    Producto.class);
            query.setParameter("categoriaId", categoriaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return obtenerTodos();
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria " +
                    "WHERE LOWER(p.nombre) LIKE LOWER(:nombre) ORDER BY p.nombre", 
                    Producto.class);
            query.setParameter("nombre", "%" + nombre.trim() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Producto guardar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
            return producto;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar producto: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Producto actualizar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }
        if (producto.getId() <= 0) {
            throw new IllegalArgumentException("El ID del producto es inválido");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto updated = em.merge(producto);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar producto: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void eliminar(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                em.remove(producto);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar producto: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void actualizarExistencias(int productoId, int cantidad) {
        if (productoId <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, productoId);
            if (producto != null) {
                int nuevasExistencias = producto.getExistencias() + cantidad;
                if (nuevasExistencias < 0) {
                    throw new IllegalStateException("Las existencias no pueden ser negativas");
                }
                producto.setExistencias(nuevasExistencias);
                em.merge(producto);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar existencias: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}