package com.scaleup.backend.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @PrimaryKeyColumn(name= "id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    @Column("uuid")
    private UUID id;

    @Column("username")
    private String username;
}
