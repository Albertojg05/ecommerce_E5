/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.dao;

import com.mycompany.ecommerce_e5.dominio.Pedido;
import com.mycompany.ecommerce_e5.dominio.enums.EstadoPedido;
import com.mycompany.ecommerce_e5.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Clase DAO para la entidad Pedido.
 * Maneja las operaciones de base de datos para los pedidos:
 * obtener todos, por usuario, por estado, guardar, actualizar y eliminar.
 * Las consultas hacen JOIN FETCH para cargar las relaciones y evitar
 * problemas de lazy loading.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class PedidoDAO {

    public List<Pedido> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p " +
                    "LEFT JOIN FETCH p.usuario " +
                    "LEFT JOIN FETCH p.direccionEnvio " +
                    "LEFT JOIN FETCH p.pago " +
                    "ORDER BY p.fecha DESC", Pedido.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Pedido obtenerPorId(int id) {
        if (id <= 0) {
            return null;
        }
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT p FROM Pedido p " +
                    "LEFT JOIN FETCH p.detalles d " +
                    "LEFT JOIN FETCH d.producto " +
                    "LEFT JOIN FETCH p.pago " +
                    "LEFT JOIN FETCH p.usuario " +
                    "LEFT JOIN FETCH p.direccionEnvio " +
                    "WHERE p.id = :id", Pedido.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Pedido> obtenerPorUsuario(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p " +
                    "LEFT JOIN FETCH p.detalles d " +
                    "LEFT JOIN FETCH d.producto " +
                    "WHERE p.usuario.id = :usuarioId " +
                    "ORDER BY p.fecha DESC", Pedido.class);
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Pedido> obtenerPorEstado(EstadoPedido estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser null");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p " +
                    "LEFT JOIN FETCH p.usuario " +
                    "LEFT JOIN FETCH p.pago " +
                    "WHERE p.estado = :estado " +
                    "ORDER BY p.fecha DESC", Pedido.class);
            query.setParameter("estado", estado);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene los pedidos asociados a una dirección específica (#37).
     */
    public List<Pedido> obtenerPorDireccion(int direccionId) {
        if (direccionId <= 0) {
            return List.of();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT p FROM Pedido p " +
                    "WHERE p.direccionEnvio.id = :direccionId", Pedido.class);
            query.setParameter("direccionId", direccionId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Pedido guardar(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser null");
        }
        
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
            throw new RuntimeException("Error al guardar pedido: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Pedido actualizar(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser null");
        }
        if (pedido.getId() <= 0) {
            throw new IllegalArgumentException("El ID del pedido es inválido");
        }
        
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
            throw new RuntimeException("Error al actualizar pedido: " + e.getMessage(), e);
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
            Pedido pedido = em.find(Pedido.class, id);
            if (pedido != null) {
                em.remove(pedido);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar pedido: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}