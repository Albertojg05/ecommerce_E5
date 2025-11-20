/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.dao;

import com.mycompany.ecommerce_e5.dominio.Pedido;
import com.mycompany.ecommerce_e5.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * @author Alberto Jiménez García 252595 
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */
public class PedidoDAO {

    public List<Pedido> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p "
                    + "LEFT JOIN FETCH p.usuario "
                    + "LEFT JOIN FETCH p.direccionEnvio "
                    + "ORDER BY p.fecha DESC", Pedido.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Pedido obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT p FROM Pedido p "
                    + "LEFT JOIN FETCH p.detalles d "
                    + "LEFT JOIN FETCH d.producto "
                    + "LEFT JOIN FETCH p.pago "
                    + "LEFT JOIN FETCH p.usuario "
                    + "LEFT JOIN FETCH p.direccionEnvio "
                    + "WHERE p.id = :id", Pedido.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Pedido> obtenerPorUsuario(int usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p "
                    + "LEFT JOIN FETCH p.detalles "
                    + "WHERE p.usuario.id = :usuarioId "
                    + "ORDER BY p.fecha DESC", Pedido.class);
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Pedido> obtenerPorEstado(String estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p "
                    + "LEFT JOIN FETCH p.usuario "
                    + "WHERE p.estado = :estado "
                    + "ORDER BY p.fecha DESC", Pedido.class);
            query.setParameter("estado", estado);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Pedido guardar(Pedido pedido) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(pedido);
            em.getTransaction().commit();
            return pedido;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Pedido actualizar(Pedido pedido) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Pedido updated = em.merge(pedido);
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
            Pedido pedido = em.find(Pedido.class, id);
            if (pedido != null) {
                em.remove(pedido);
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
