package com.example.backend.restaurante.config;

import com.example.backend.restaurante.model.user.Roles;
import com.example.backend.restaurante.model.user.Usuario;
import com.example.backend.restaurante.repository.user.RolesRepository;
import com.example.backend.restaurante.repository.user.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final RolesRepository rolesRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RolesRepository rolesRepository,
                           UsuarioRepository usuarioRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.rolesRepository = rolesRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Inicializando configuração do Admin...");

        // Busca o papel ADMIN ou cria caso não exista
        var rolesAdmin = rolesRepository.findByName(Roles.Values.ADMIN.name())
                .orElseGet(() -> {
                    System.out.println("Role ADMIN não encontrada, criando uma nova...");
                    var role = new Roles();
                    role.setName(Roles.Values.ADMIN.name());
                    return rolesRepository.save(role);
                });

        // Busca o usuário admin pelo email
        var userAdmin = usuarioRepository.findByEmail("admin");

        // Se o admin já existir, avisa; se não, cria um novo admin
        userAdmin.ifPresentOrElse(
                user -> System.out.println("Admin já existe"),
                () -> {
                    System.out.println("Criando o usuário admin...");
                    var user = new Usuario();
                    user.setEmail("admin");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setRoles(Set.of(rolesAdmin));
                    usuarioRepository.save(user);
                    System.out.println("Admin criado com sucesso");
                }
        );
    }
}