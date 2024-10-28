package com.certidevs.controller;

import com.certidevs.model.Film;
import com.certidevs.repository.FilmRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilmControllerUTest {

    @InjectMocks
    private FilmController filmController;

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private Model model;

    @Test
    void findAll() {

        Film film1 = Film.builder().id(1L).title("The Godfather").director("Francis Ford Coppola").year(1972L).rented(true).build();
        Film film2 = Film.builder().id(2L).title("Back to the Future").director("Robert Zemeckis").year(1985L).rented(false).build();
        List<Film> films = List.of(film1, film2);

        when(filmRepository.findAll()).thenReturn(films);

        String view = filmController.findAll(model);

        assertEquals("film-list", view);
        verify(filmRepository).findAll();
        verify(model).addAttribute("films", films);

    }

    @Test
    void findByIdWhenFilmExists() {

        Film film = Film.builder().id(1L).title("The Godfather").director("Francis Ford Coppola").year(1972L).rented(true).build();
        Optional<Film> filmOptional = Optional.of(film);
        when(filmRepository.findById(1L)).thenReturn(filmOptional);

        String view = filmController.findById(1L, model);

        assertEquals("film-detail", view);
        verify(filmRepository).findById(1L);
        verify(model).addAttribute("film", film);
    }

    @Test
    void findByIdWhenFilmDoesNotExist() {

        when(filmRepository.findById(1L)).thenReturn(Optional.empty());

        String view = filmController.findById(1L, model);

        assertEquals("film-detail", view);
        verify(filmRepository).findById(1L);
        verify(model).addAttribute(anyString(), any());
        }

    @Test
    void getNewFilmForm() {

        String view = filmController.getNewFilmForm(model);

        assertEquals("film-form", view);
        verify(model).addAttribute(eq("film"), any(Film.class));
    }

    @Test
    void getEditFilmForm_IfFilmExists() {

        Film film = Film.builder().id(1L).title("The Godfather").director("Francis Ford Coppola").year(1972L).rented(true).build();
        Optional<Film> filmOptional = Optional.of(film);
        when(filmRepository.findById(1L)).thenReturn(filmOptional);

        String view = filmController.getEditFilmForm(1L, model);

        assertEquals("film-form", view);
        verify(filmRepository).findById(1L);
        verify(model).addAttribute("film", film);
    }

    @Test
    void getEditFilmForm_IfFilmDoesNotExist() {

        when(filmRepository.findById(1L)).thenReturn(Optional.empty());

        String view = filmController.getEditFilmForm(1L, model);

        assertEquals("film-form", view);
        verify(filmRepository).findById(1L);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void saveFilmNew() {

        Film film = new Film();

        String view = filmController.saveFilm(film);

        assertEquals("redirect:/films", view);
        verify(filmRepository).save(film);

    }

    @Test
    void saveFilmExistsUpdate() {

        Film filmToUpdate = new Film();
        filmToUpdate.setId(1L);
        filmToUpdate.setTitle("The Godfather -of Mario Puzo-");

        Film filmFromDB = new Film();
        filmFromDB.setId(1L);
        filmFromDB.setTitle("The Godfather");

        Optional<Film> filmFromDBOptional = Optional.of(filmFromDB);
        when(filmRepository.findById(1L)).thenReturn(filmFromDBOptional);

        String view = filmController.saveFilm(filmToUpdate);

        assertEquals("redirect:/films", view);
        verify(filmRepository).findById(1L);
        verify(filmRepository).save(filmFromDB);
        assertEquals("The Godfather -of Mario Puzo-", filmFromDB.getTitle());
    }

    @Test
    void saveAndGoDetail_NewFilm() {

        Film film = new Film();

        doAnswer(invocation -> {
            Film filmDB = invocation.getArgument(0);
            filmDB.setId(1L);
            filmDB.setTitle("The Godfather -of Mario Puzo-");
            return null;
        }).when(filmRepository).save(film);

        String view = filmController.saveAndGoDetail(film);

        assertEquals("redirect:/films/1", view);
        verify(filmRepository).save(film);
    }

    @Test
    void deleteFilm() {

        String view = filmController.deleteFilm(1L);

        assertEquals("redirect:/films", view);
        verify(filmRepository).deleteById(1L);
    }
}