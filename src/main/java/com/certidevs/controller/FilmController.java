package com.certidevs.controller;

import com.certidevs.model.Film;
import com.certidevs.repository.FilmRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@AllArgsConstructor
@Controller
public class FilmController {

    private FilmRepository filmRepository;

    //http://localhost:8080/films
    @GetMapping("/films")
    public String findAll(Model model) {
        model.addAttribute("films", filmRepository.findAll());
        return "film-list";
    }

    //http://localhost:8080/films/1
    @GetMapping("/films/{id}")
    public String findById(@PathVariable Long id, Model model) {
        Optional<Film> filmOptional = filmRepository.findById(id);

        filmOptional.ifPresent(film -> model.addAttribute("film", film));
        return "film-detail";
    }

    //http://localhost:8080/films/create
    @GetMapping("/films/create")
    public String getNewFilmForm(Model model) {
        model.addAttribute("film", new Film());
        return "film-form";
    }

    //http://localhost:8080/films/edit/1
    @GetMapping("/films/edit/{id}")
    public String getEditFilmForm(@PathVariable Long id, Model model) {
        filmRepository.findById(id)
                .ifPresent((film -> model.addAttribute("film", film)));
        return "film-form";
    }

    @PostMapping("films")
    public String saveFilm(@ModelAttribute Film film) {
        boolean exists = false;
        if (film.getId() != null) {
            exists = filmRepository.existsById(film.getId());
        }
        if (!exists) {
            filmRepository.save(film);
        } else {
            filmRepository.findById(film.getId()).
                        ifPresent(filmDB -> {
                        filmDB.setTitle(film.getTitle());
                        filmDB.setDirector(film.getDirector());
                        filmDB.setYear(film.getYear());
                        filmDB.setRented(film.getRented());
                        filmRepository.save(filmDB);
                    });
        }
        return "redirect:/films";
    }

}
