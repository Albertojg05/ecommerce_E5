/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.dao;

import com.mycompany.ecommerce_e5.dominio.Producto;
import com.mycompany.ecommerce_e5.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * @author Alberto Jiménez García 252595 
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */
public class ProductoDAO {

    public List<Producto> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "ORDER BY p.id DESC", Producto.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> obtenerPaginados(int pagina, int tamano) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "ORDER BY p.id DESC", Producto.class);
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
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Producto producto = em.find(Producto.class, id);
            if (producto != null && producto.getCategoria() != null) {
                // Forzar carga de categoría
                producto.getCategoria().getNombre();
            }
            return producto;
        } finally {
            em.close();
        }
    }

    public List<Producto> obtenerPorCategoria(int categoriaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "WHERE p.categoria.id = :categoriaId", Producto.class);
            query.setParameter("categoriaId", categoriaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> buscarPorNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "WHERE LOWER(p.nombre) LIKE LOWER(:nombre)", Producto.class);
            query.setParameter("nombre", "%" + nombre + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Producto guardar(Producto producto) {
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
            throw e;
        } finally {
            em.close();
        }
    }

    public Producto actualizar(Producto producto) {
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
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminar(int id) {
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
            throw e;
        } finally {
            em.close();
        }
    }

    public void actualizarExistencias(int productoId, int cantidad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, productoId);
            if (producto != null) {
                producto.setExistencias(producto.getExistencias() + cantidad);
                em.merge(producto);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
