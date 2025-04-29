package mx.ipn.escom.ProyectoFinal.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import mx.ipn.escom.ProyectoFinal.services.SismoService;
import java.util.List;
import mx.ipn.escom.ProyectoFinal.models.Sismo;
import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import java.util.Optional;


@Controller
public class SismoController {

    private final SismoService sismoService;
    private final UserRepository userRepository;


    public SismoController(SismoService sismoService, UserRepository userRepository) {
        this.sismoService = sismoService;
        this.userRepository = userRepository;
    }
    

    @GetMapping("/sismos")
    public String mostrarSismos(
        @RequestParam(name = "zona", required = false) String zona,
        @RequestParam(name = "fecha", required = false) String fecha,
        @RequestParam(name = "hora", required = false) String hora,
        @RequestParam(name = "magnitud", required = false) Double magnitud,
        @RequestParam(name = "profundidad", required = false) Double profundidad,
        Model model,
        @AuthenticationPrincipal User user
    ) {
        List<Sismo> sismos = sismoService.obtenerTodosLosSismos();

        if (zona != null && !zona.isEmpty()) {
            sismos = sismos.stream()
                    .filter(s -> s.getZona().toLowerCase().contains(zona.toLowerCase()))
                    .toList();
        }

        if (fecha != null && !fecha.isEmpty()) {
            sismos = sismos.stream()
                    .filter(s -> s.getFecha().equals(fecha))
                    .toList();
        }

        if (hora != null && !hora.isEmpty()) {
            sismos = sismos.stream()
                    .filter(s -> s.getHora().equals(hora))
                    .toList();
        }

        if (magnitud != null) {
            sismos = sismos.stream()
                    .filter(s -> s.getMagnitud() >= magnitud)
                    .toList();
        }

        if (profundidad != null) {
            sismos = sismos.stream()
                    .filter(s -> s.getProfundidad() <= profundidad)
                    .toList();
        }

        model.addAttribute("sismos", sismos);

        // También enviamos el tema para el body
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            String tema = usuarioOptional.get().getPreferenciaUsuario() != null ? usuarioOptional.get().getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
        }

        return "sismos";
    }


}
