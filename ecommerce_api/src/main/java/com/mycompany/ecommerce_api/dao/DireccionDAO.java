/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_api.dao;

import com.mycompany.ecommerce_api.dominio.Direccion;
import com.mycompany.ecommerce_api.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Clase DAO para la entidad Direccion.
 * Gestiona las direcciones de envio de los usuarios en la base de datos.
 * Permite obtener, guardar, actualizar y eliminar direcciones.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
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
        if (id <= 0) {
            return null;
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Direccion.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Direccion> obtenerPorUsuario(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        
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
        if (direccion == null) {
            throw new IllegalArgumentException("La dirección no puede ser null");
        }
        
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
            throw new RuntimeException("Error al guardar dirección: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    public Direccion actualizar(Direccion direccion) {
        if (direccion == null) {
            throw new IllegalArgumentException("La dirección no puede ser null");
        }
        if (direccion.getId() <= 0) {
            throw new IllegalArgumentException("El ID de la dirección es inválido");
        }
        
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
            throw new RuntimeException("Error al actualizar dirección: " + e.getMessage(), e);
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
            Direccion direccion = em.find(Direccion.class, id);
            if (direccion != null) {
                em.remove(direccion);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar dirección: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}