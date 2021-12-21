package com.scaleup.backend.user;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Optional;

@Repository
public interface UserRepository extends CassandraRepository<User, String> {

    @AllowFiltering
    @NonNull
    Optional<User> findUserById(@NonNull String id);

    @AllowFiltering
    Optional<User> findUserByUsername(String username);

    @AllowFiltering
    @Query("UPDATE users set leagues=?0 where id=?1")
    void updateUserLeagues(LinkedHashMap<String, String> leagues, String id);

    @AllowFiltering
    void deleteUserById(String id);
}
