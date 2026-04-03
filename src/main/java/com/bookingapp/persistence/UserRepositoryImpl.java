package com.bookingapp.persistence;

import com.bookingapp.domain.model.User;
import com.bookingapp.persistence.entity.UserEntity;
import com.bookingapp.persistence.mapper.UserPersistenceMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserPersistenceMapper userPersistenceMapper;

    public UserRepositoryImpl(
            UserPersistenceMapper userPersistenceMapper
    ) {
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Transactional
    public User save(User user) {
        UserEntity entity = userPersistenceMapper.toEntity(user);
        if (user.getId() == null) {
            entityManager.persist(entity);
            entityManager.flush();
            return userPersistenceMapper.toDomain(entity);
        } else {
            UserEntity merged = entityManager.merge(entity);
            return userPersistenceMapper.toDomain(merged);
        }
    }

    public Optional<User> findById(Long userId) {
        UserEntity entity = entityManager.find(UserEntity.class, userId);
        return Optional.ofNullable(entity)
                .map(userPersistenceMapper::toDomain);
    }

    public Optional<User> findByEmail(String email) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.email = :email",
                UserEntity.class
        );
        query.setParameter("email", email);
        return query.getResultStream()
                .findFirst()
                .map(userPersistenceMapper::toDomain);
    }

    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email",
                Long.class
        );
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
}
