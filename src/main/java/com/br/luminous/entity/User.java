package com.br.luminous.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="user_id")
    private Long id;
    private String name;
    private String phone;
    private String userName;
    private String email;
    private String password;
    private LocalDate birthdate;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @Column(name = "addresses")
    @JsonManagedReference
    private List<Address> addresses;
    @OneToMany(cascade = CascadeType.REMOVE)
    @Column(name = "tokens")
    @JsonManagedReference
    private List<Token> tokens;

    @ManyToMany()
    @JoinTable(name="users_read_weather_tip", joinColumns = {@JoinColumn(name="user_id")}, inverseJoinColumns = {@JoinColumn(name="id")})
    private List<WeatherTip> weatherTips;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getUserName() {
        return userName;
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
