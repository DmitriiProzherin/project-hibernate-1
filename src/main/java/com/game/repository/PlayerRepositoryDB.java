package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.*;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "admin");
        properties.put(Environment.PASS, "admin");
        properties.put(Environment.HBM2DDL_AUTO, "update");


        sessionFactory = new Configuration()
                .addAnnotatedClass(com.game.entity.Player.class)
                .setProperties(properties)
                .buildSessionFactory();

        // Logging
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Player> playersQuery = session.createNativeQuery("select * from rpg.player", Player.class);
            playersQuery.setFirstResult(pageNumber*pageSize);
            playersQuery.setMaxResults(pageSize);
            return playersQuery.list();
        }
        catch (Exception e) {
            // Logging
            return null;
        }
    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            Long playerCount = session.createNamedQuery("get_player_count", Long.class).getSingleResult();
            return Math.toIntExact(playerCount);
        }
        catch (Exception e) {
            // Logging
            return 0;
        }
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(player);
            transaction.commit();
            return player;
        }
        catch (Exception e) {
            // Logging
            return null;
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(player);
            transaction.commit();
            return player;
        }
        catch (Exception e) {
            // Logging
            return null;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Player player = session.get(Player.class, id);
            return Optional.ofNullable(player);
        }
        catch (Exception e) {
           // Logging
           return Optional.empty();
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(player);
            transaction.commit();
        }
        catch (Exception e) {
            // Logging
        }
    }

    @PreDestroy
    public void beforeStop() {
        // Logging
        sessionFactory.close();
    }
}