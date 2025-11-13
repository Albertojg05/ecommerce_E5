/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.dao;

import com.mycompany.ecommerce_e5.dominio.Direccion;
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

public class DireccionDAO {
    
    public List<Direccion> obtenerTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Direccion> query = em.createQuery(
                "SELECT d FROM Direccion d", Direccion.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Direccion obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Direccion.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Direccion> obtenerPorUsuario(int usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Direccion> query = em.createQuery(
                "SELECT d FROM Direccion d WHERE d.usuario.id = :usuarioId", 
                Direccion.class);
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Direccion guardar(Direccion direccion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(direccion);
            em.getTransaction().commit();
            return direccion;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    public Direccion actualizar(Direccion direccion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Direccion updated = em.merge(direccion);
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
            Direccion direccion = em.find(Direccion.class, id);
            if (direccion != null) {
                em.remove(direccion);
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
