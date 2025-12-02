    package com.ues.edu.web;

    import org.springframework.security.core.Authentication;
    import org.springframework.stereotype.Component;
    import org.springframework.web.bind.annotation.ControllerAdvice;
    import org.springframework.web.bind.annotation.ModelAttribute;

    import com.ues.edu.model.Usuario;
    import com.ues.edu.serviceimp.UsuarioServiceImpl;

    @ControllerAdvice
    @Component
    public class GlobalAttributes {

        private final UsuarioServiceImpl usuarioService;

        public GlobalAttributes(UsuarioServiceImpl usuarioService) {
            this.usuarioService = usuarioService;
        }

        @ModelAttribute("usuarioSesion")
        public Usuario usuarioSesion(Authentication authentication) {
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            String username = authentication.getName(); // username logueado
            return usuarioService.buscarPorUsername(username);
        }
    }
