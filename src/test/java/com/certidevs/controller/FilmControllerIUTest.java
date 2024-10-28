package com.certidevs.controller;

import com.certidevs.model.Film;
import com.certidevs.repository.FilmRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
public class FilmControllerIUTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmRepository filmRepository;

    @Test
    void findAll() throws Exception {

        when(filmRepository.findAll()).thenReturn(List.of(
                Film.builder().id(1L).title("The Godfather").director("Francis Ford Coppola").year(1972L).rented(true).build(),
                Film.builder().id(2L).title("Back to the Future").director("Robert Zemeckis").year(1985L).rented(false).build()
        ));

        mockMvc.perform(get("/films"))
                .andExpect(view().name("film-list"))
                .andExpect(model().attributeExists("films"))
                .andExpect(model().attribute("films", hasSize(2)))
                .andExpect(model().attribute("films", hasItem(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("name", is("MSI")
                        )
                )))
                );

    }

    @Test
    void findById() throws Exception {

        var film = Film.builder().id(1L).title("The Godfather").director("Francis Ford Coppola").year(1972L).rented(true).build();
        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));

        mockMvc.perform(get("/films/{id}", 1L))
                .andExpect(view().name("film-detail"))
                .andExpect(model().attributeExists("film"))
                .andExpect(model().attribute("film",
                        allOf(
                            hasProperty("id", is(1L)),
                            hasProperty("title", is("The Godfather")),
                            hasProperty("director", is("Francis Ford Coppola")),
                            hasProperty("year", is(1972L)),
                            hasProperty("rented", is(true))
                        )
                ));

    }
    @Test
    void getFormToUpdate_Empty() throws Exception {

        when(filmRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/films/edit/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEditFilmForm() {
    }

    @Test
    void save_createNewFilm() throws Exception {

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "The Godfather")
                .param("director", "Francis Ford Coppola")
                .param("year", "1972")
                .param("rented", "true")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("/films"));

        verify(filmRepository).save(Mockito.any(Film.class));

    }

    @Test
    void save_updateFilm() throws Exception {

        var film = Film.builder()
                .id(1L)
                .title("The Godfather")
                .director("Francis Ford Coppola")
                .year(1972L)
                .rented(true)
                .build();

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("title", "The Godfather")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("/films"));

        verify(filmRepository).findById(1L);
        verify(filmRepository).save(film);
    }

    @Test
    void saveAndGoDetail_createNewFilm() throws Exception {

        when(filmRepository.save(Mockito.any(Film.class)))
                .thenAnswer(invocation -> {
                    Film filmToSave = invocation.getArgument(0);
                    filmToSave.setId(1L);
                    return filmToSave;
                });

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "The Godfather")
                        .param("director", "Francis Ford Coppola")
                        .param("year", "1972")
                        .param("rented", "true")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/films/1"));

        verify(filmRepository).save(Mockito.any(Film.class));
    }

    @Test
    void deleteFilm() throws Exception {

        mockMvc.perform(get("/films/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("/films"));

        verify(filmRepository).deleteById(1L);
    }
}