package com.scaleup.backend.user;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {

    @AllowFiltering
    Optional<User> findById(UUID id);

    @AllowFiltering
    Optional<User> findByUsername(String username);
}
