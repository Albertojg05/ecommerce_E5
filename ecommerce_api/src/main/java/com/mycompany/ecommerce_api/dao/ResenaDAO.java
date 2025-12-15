/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_api.dao;

import com.mycompany.ecommerce_api.dominio.Resena;
import com.mycompany.ecommerce_api.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Clase DAO para la entidad Resena.
 * Maneja las operaciones de base de datos para las resenas de productos.
 * Permite obtener resenas por producto o por usuario, crear nuevas
 * resenas y eliminarlas.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class ResenaDAO {
    
    public List<Resena> obtenerTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Resena> query = em.createQuery(
                "SELECT r FROM Resena r " +
                "LEFT JOIN FETCH r.usuario " +
                "LEFT JOIN FETCH r.producto " +
                "ORDER BY r.fecha DESC", Resena.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Resena obtenerPorId(int id) {
        if (id <= 0) {
            return null;
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Resena.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Resena> obtenerPorProducto(int productoId) {
        if (productoId <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Resena> query = em.createQuery(
                "SELECT r FROM Resena r " +
                "LEFT JOIN FETCH r.usuario " +
                "WHERE r.producto.id = :productoId " +
                "ORDER BY r.fecha DESC", Resena.class);
            query.setParameter("productoId", productoId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Resena> obtenerPorUsuario(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Resena> query = em.createQuery(
                "SELECT r FROM Resena r " +
                "LEFT JOIN FETCH r.producto " +
                "WHERE r.usuario.id = :usuarioId " +
                "ORDER BY r.fecha DESC", Resena.class);
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Resena guardar(Resena resena) {
        if (resena == null) {
            throw new IllegalArgumentException("La reseña no puede ser null");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(resena);
            em.getTransaction().commit();
            return resena;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar reseña: " + e.getMessage(), e);
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
            Resena resena = em.find(Resena.class, id);
            if (resena != null) {
                em.remove(resena);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar reseña: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}