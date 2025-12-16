/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.dao;

import com.mycompany.ecommerce_e5.dominio.Categoria;
import com.mycompany.ecommerce_e5.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Clase DAO para la entidad Categoria.
 * Realiza operaciones CRUD sobre las categorias de productos
 * en la base de datos.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class CategoriaDAO {
    
    public List<Categoria> obtenerTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Categoria> query = em.createQuery(
                "SELECT c FROM Categoria c ORDER BY c.nombre", Categoria.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Categoria obtenerPorId(int id) {
        if (id <= 0) {
            return null;
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            em.close();
        }
    }
    
    public Categoria guardar(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser null");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(categoria);
            em.getTransaction().commit();
            return categoria;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar categoría: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    public Categoria actualizar(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser null");
        }
        if (categoria.getId() <= 0) {
            throw new IllegalArgumentException("El ID de la categoría es inválido");
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Categoria updated = em.merge(categoria);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar categoría: " + e.getMessage(), e);
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
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria != null) {
                em.remove(categoria);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar categoría: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}