package school.sptech.prova_ac1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {

        if (usuarioRepository.findAll().isEmpty()) {
            return ResponseEntity.status(204).body(usuarioRepository.findAll());
        }

        return ResponseEntity.status(200).body(usuarioRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            return ResponseEntity.status(409).body(usuario);
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.status(409).body(usuario);
        }
        Usuario novoUsario = usuarioRepository.save(usuario);
        return ResponseEntity.status(201).body(novoUsario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(usuario -> ResponseEntity.status(200).body(usuario))
                .orElse(ResponseEntity.notFound()
                        .build()
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (usuarioRepository.findById(id).isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(@RequestParam LocalDate nascimento) {

        var usuariosFiltradosPelaData = usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getDataNascimento().isAfter(nascimento))
                .toList();
        if (usuariosFiltradosPelaData.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(usuariosFiltradosPelaData);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Integer id,
            @RequestBody Usuario usuario
    ) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {

                    if (usuarioRepository.existsByCpf(usuario.getCpf()) &&
                    !usuarioExistente.getCpf().equals(usuario.getCpf())) {
                        return ResponseEntity.status(409).body(usuario);
                    }
                    if (usuarioRepository.existsByEmail(usuario.getEmail()) &&
                    !usuarioExistente.getEmail().equals(usuario.getEmail())) {
                        return ResponseEntity.status(409).body(usuario);
                    }
                    usuarioExistente.setNome(usuario.getNome());
                    usuarioExistente.setCpf(usuario.getCpf());
                    usuarioExistente.setEmail(usuario.getEmail());
                    usuarioExistente.setSenha(usuario.getSenha());
                    usuarioExistente.setDataNascimento(usuario.getDataNascimento());

                    return ResponseEntity.ok(usuarioRepository.save(usuarioExistente));

                })
                .orElse(ResponseEntity.status(409).build());
    }
}
