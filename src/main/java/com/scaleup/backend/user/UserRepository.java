package com.scaleup.backend.user;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CassandraRepository<User, String> {

    @AllowFiltering
    @NonNull
    Optional<User> findById(@NonNull String id);

    @AllowFiltering
    Optional<User> findByUsername(String username);

    @AllowFiltering
    void deleteUserById(String id);
}
