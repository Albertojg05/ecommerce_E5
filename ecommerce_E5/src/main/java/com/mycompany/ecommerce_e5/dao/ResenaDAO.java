/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.dao;

import com.mycompany.ecommerce_e5.dominio.Resena;
import com.mycompany.ecommerce_e5.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

public class ResenaDAO {
    
    public List<Resena> obtenerTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Resena> query = em.createQuery(
                "SELECT r FROM Resena r ORDER BY r.fecha DESC", Resena.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Resena obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Resena.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Resena> obtenerPorProducto(int productoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Resena> query = em.createQuery(
                "SELECT r FROM Resena r WHERE r.producto.id = :productoId ORDER BY r.fecha DESC", 
                Resena.class);
            query.setParameter("productoId", productoId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Resena> obtenerPorUsuario(int usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Resena> query = em.createQuery(
                "SELECT r FROM Resena r WHERE r.usuario.id = :usuarioId ORDER BY r.fecha DESC", 
                Resena.class);
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Resena guardar(Resena resena) {
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
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void eliminar(int id) {
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
            throw e;
        } finally {
            em.close();
        }
    }
}
