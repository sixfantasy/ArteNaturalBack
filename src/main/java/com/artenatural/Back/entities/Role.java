package com.artenatural.Back.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
    @Getter
    @Setter
    @Table(name = "role")
    public class  Role implements Serializable {

        private static final long serialVersionUID = 198355926937292762L;

        public static final String ADMIN_ROLE = "ADMIN";
        public static final String ROLE1 = "ROLE1";


        public static final String LST_USER = "lstUser";

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        /**
         * Nombre del rol
         */
        private String roleName;

        @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
        private List<User> users;

        public Role() {
            roleName = "";
            users = new ArrayList<User>();
        }

        public Role(String roleName) {
            if (roleName == null) {
                throw new IllegalArgumentException("El nombre del rol no puede ser nulo");
            }

            this.roleName = roleName;
            users = new ArrayList<User>();
        }

        public boolean equals(Object obj) {
            boolean equal = false;

            if (obj instanceof Role) {
                Role role = (Role) obj;

                if (role.roleName.equals(roleName)) {
                    equal = true;
                }
            }

            return equal;
        }
    }
}
