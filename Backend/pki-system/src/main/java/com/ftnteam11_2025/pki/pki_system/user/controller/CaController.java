package com.ftnteam11_2025.pki.pki_system.user.controller;

import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterResponseDTO;
import com.ftnteam11_2025.pki.pki_system.user.dto.AccountStatusUpdateDTO;
import com.ftnteam11_2025.pki.pki_system.user.service.AuthService;
import com.ftnteam11_2025.pki.pki_system.user.service.CaService;
import com.ftnteam11_2025.pki.pki_system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class CaController {
    private final AuthService authService;
    private final UserService userService;
    private final CaService  caService;

    @GetMapping("/all")
    @Secured("ROLE_CA")
    public ResponseEntity<Page<RegisterResponseDTO>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(caService.getAllPendingUsers(page, size));
    }

    @PostMapping("/account/accept")
    @Secured("ROLE_CA")
    public ResponseEntity<Boolean> acceptUser(@ModelAttribute AccountStatusUpdateDTO dto) {
        return ResponseEntity.ok(caService.acceptUser(dto));
    }

    @PostMapping("/account/reject")
    @Secured("ROLE_CA")
    public ResponseEntity<Boolean> rejectUser(@ModelAttribute AccountStatusUpdateDTO dto) {
        return ResponseEntity.ok(caService.rejectUser(dto));
    }

}
