package com.example.filmappjavabackend.models.updates;

import javax.validation.constraints.NotBlank;

// Dla admina, zeby mógł wyłączać lub włączać poszczególne komentarze, filmy, playlisty
// jesli ustawi pole isActive któregoś z dokumentów na false nie będzie on widoczny dla użytkownikó

public class AdminActiveUpdate {

    @NotBlank
    private String isActive;

    public AdminActiveUpdate() {
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
