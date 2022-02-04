package com.scaleup.backend.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.*;

import java.util.LinkedHashMap;

@Data
@Table("users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @PrimaryKey("id")
    private String id;

    @Column("username")
    private String username;

    @Frozen
    @Column("leagues")
    private LinkedHashMap<String, String> leagues;
}
