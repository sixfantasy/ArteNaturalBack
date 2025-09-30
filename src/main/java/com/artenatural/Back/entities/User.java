package com.artenatural.Back.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
    @Setter
    @Entity
    @Table(name = "user")
    public class User implements UserDetails {
        
        public User() {}
        
        public User(String username, String password) {
            this.username = username;
            this.password = password;
            this.accountNonExpired = true;
            this.accountNonLocked = true;
            this.credentialsNonExpired = true;
            this.enabled = true;
        }
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // Para generar números autoincrementados
        private int id;
        private String username;
        private String password;
        private boolean accountNonExpired;
        private boolean accountNonLocked;
        private boolean credentialsNonExpired;
        private boolean enabled;
        private int age;
        private String interests;

        @ManyToMany(fetch = FetchType.EAGER, mappedBy = "users", cascade = CascadeType.REMOVE)
        private List<Role> roles;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // Devolvemos un ArrayList vacío porque nuestra app no tiene roles
            return new ArrayList<>();
        }

        @Override
        public boolean isAccountNonExpired() {
            return accountNonExpired;
        }

        @Override
        public boolean isAccountNonLocked() {
            return accountNonLocked;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return credentialsNonExpired;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public String getPassword() {
            return password;
        }
    }
