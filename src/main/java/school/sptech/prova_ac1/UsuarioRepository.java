package school.sptech.prova_ac1;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
