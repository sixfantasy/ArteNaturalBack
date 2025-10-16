package com.artenatural.Back.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
    @Inheritance(strategy = InheritanceType.JOINED)
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
        private Integer id;
        private String username;
        private String password;
        private String displayName;
        private boolean accountNonExpired;
        private boolean accountNonLocked;
        private boolean credentialsNonExpired;
        private boolean enabled;
        private Date birthdate;
        private String mail;
        private String interests;
        private double balance;
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Purchase> orders;
        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private Purchase currentPurchase;

        @ManyToMany(fetch = FetchType.EAGER)
        private List<Role> roles;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
        private ArtistData artistData;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                    .collect(Collectors.toList());
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

